package service;

import model.task.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import util.FileHandler;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*; // Static imports for when, verify, times

public class TaskManagerImplTest {

    // The TaskManagerImpl instance we are testing
    private TaskManagerImpl taskManager;

    // To mock static methods of FileHandler
    private MockedStatic<FileHandler> mockedFileHandler;

    // For capturing System.out output
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        // Redirect System.out to capture console output
        System.setOut(new PrintStream(outContent));

        // Start mocking static methods of FileHandler
        // This MUST be done within a try-with-resources or explicitly closed later.
        // It's usually done once per test method if static calls are complex,
        // or managed carefully in BeforeEach/AfterEach.
        mockedFileHandler = Mockito.mockStatic(FileHandler.class);

        // Configure mock behavior for the initial loadAllTasks() call in TaskManagerImpl constructor
        // By default, assume no tasks are loaded initially unless a test specifies otherwise.
        mockedFileHandler.when(FileHandler::readTasksFromFile)
                .thenReturn(new TreeSet<>()); // TaskManagerImpl uses TreeSet

        // Initialize TaskManagerImpl. Its constructor will call FileHandler.readTasksFromFile().
        taskManager = new TaskManagerImpl();

        // After setup, clear any output from TaskManagerImpl's constructor loading tasks
        outContent.reset();
    }

    @AfterEach
    void tearDown() {
        // Restore System.out
        System.setOut(originalOut);

        // Close the static mock. This is CRUCIAL to prevent interference between tests.
        // If not closed, subsequent tests will still have FileHandler mocked.
        mockedFileHandler.close();
    }

    // Helper method to create a basic task
    private Task createSimpleTask(long id, String title, String description) {
        return TaskCreator.createTask(id, title, description, LocalDate.now().plusDays(1), Priority.MEDIUM, Status.PENDING, Category.PERSONAL, LocalDate.now());
    }

    // --- Test loadAllTasks() ---
    @Test
    void loadAllTasks_loadsEmptyFileCorrectly() {
        // Arrange: setUp already configured FileHandler.readTasksFromFile to return an empty set.
        // Act: TaskManagerImpl constructor (called in setUp) already called loadAllTasks().
        // No explicit call needed here for initial load.

        // Assert: TaskManager's internal tasks set should be empty.
        assertTrue(taskManager.getTasks().isEmpty(), "Tasks set should be empty after loading from an empty mock file.");
        // Verify FileHandler.readTasksFromFile was called once during construction
        mockedFileHandler.verify(FileHandler::readTasksFromFile, times(1));
    }

    @Test
    void loadAllTasks_loadsExistingTasksCorrectly() {
        // Arrange: Define some tasks that FileHandler.readTasksFromFile will return
        Task task1 = createSimpleTask(1, "Task A", "Desc A");
        Task task2 = createSimpleTask(2, "Task B", "Desc B");
        Set<Task> initialTasks = new TreeSet<>(Arrays.asList(task1, task2));

        // Configure mock behavior for readTasksFromFile
        mockedFileHandler.when(FileHandler::readTasksFromFile)
                .thenReturn(initialTasks);

        // Act: Manually call loadAllTasks to re-load with these new initial tasks
        // This simulates a scenario where file content changed and we reload.
        taskManager.loadAllTasks();

        // Assert: TaskManager's internal tasks set should now contain the mocked tasks
        assertEquals(2, taskManager.getTasks().size(), "Should load 2 tasks from mock file.");
        assertTrue(taskManager.getTasks().contains(task1));
        assertTrue(taskManager.getTasks().contains(task2));
        mockedFileHandler.verify(FileHandler::readTasksFromFile, times(2)); // Once in constructor, once here
    }

    // --- Test generateNextTaskId() (Indirectly via createTask) ---
    @Test
    void createTask_generatesIdOneForEmptyTasks() {
        // Arrange: TaskManager initialized with empty tasks (default setUp behavior)
        outContent.reset(); // Clear constructor output

        // Act
        Task createdTask = taskManager.createTask(
                "First Task", "Description", LocalDate.now(), Priority.LOW, Status.PENDING, Category.WORK, LocalDate.now()
        );

        // Assert ID is 1
        assertNotNull(createdTask);
        assertEquals(1, createdTask.getId(), "First task ID should be 1.");

        // Verify that writeTasksToFile was called with a set containing this new task
        Set<Task> expectedTasksAfterCreate = new TreeSet<>(Collections.singletonList(createdTask));
        mockedFileHandler.verify(() -> FileHandler.writeTasksToFile(expectedTasksAfterCreate), times(1));
    }

    @Test
    void createTask_generatesIncrementingIds() {
        // Arrange: Set up initial tasks with max ID 5
        Set<Task> initialTasks = new TreeSet<>(Arrays.asList(createSimpleTask(5, "Existing Task", "Desc")));
        mockedFileHandler.when(FileHandler::readTasksFromFile).thenReturn(initialTasks);
        taskManager = new TaskManagerImpl(); // Re-initialize to load these tasks
        outContent.reset(); // Clear constructor output

        // Act: Create a new task
        Task newTask = taskManager.createTask(
                "New Task", "New Desc", LocalDate.now(), Priority.MEDIUM, Status.PENDING, Category.PERSONAL, LocalDate.now()
        );

        // Assert ID is 6
        assertNotNull(newTask);
        assertEquals(6, newTask.getId(), "New task ID should be 6 (5 + 1).");

        // Verify writeTasksToFile was called with both tasks
        Set<Task> expectedTasksAfterCreate = new TreeSet<>(initialTasks);
        expectedTasksAfterCreate.add(newTask);
        mockedFileHandler.verify(() -> FileHandler.writeTasksToFile(expectedTasksAfterCreate), times(1));
    }

    @Test
    void createTask_throwsExceptionOnMaxLongOverflow() {
        // Arrange: Set up initial tasks with ID as Long.MAX_VALUE
        // Use Long.MAX_VALUE directly, as Task.id is already Long.
        Set<Task> initialTasks = new TreeSet<>(Arrays.asList(createSimpleTask(Long.MAX_VALUE, "Max ID Task", "Desc")));
        mockedFileHandler.when(FileHandler::readTasksFromFile).thenReturn(initialTasks);
        taskManager = new TaskManagerImpl(); // Re-initialize to load these tasks
        outContent.reset(); // Clear constructor output

        // Act & Assert: expect an IllegalStateException
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            taskManager.createTask("Overflow Task", "Desc", LocalDate.now(), Priority.LOW, Status.PENDING, Category.WORK, LocalDate.now());
        }, "Should throw IllegalStateException when max ID is Long.MAX_VALUE.");

        assertTrue(exception.getMessage().contains("Maximum ID limit reached"), "Exception message should indicate ID limit reached.");
        // Verify FileHandler.writeTasksToFile was NOT called as task was not added
        mockedFileHandler.verify(() -> FileHandler.writeTasksToFile(anySet()), never());
    }


    // --- Test displayAllTasks() and displayGivenTasks() ---
    @Test
    void displayAllTasks_delegatesToDisplayGivenTasks() {
        // Arrange: Populate TaskManager with some tasks
        Task task1 = createSimpleTask(1, "Task A", "Desc A");
        Task task2 = createSimpleTask(2, "Task B", "Desc B");
        Set<Task> currentTasks = new TreeSet<>(Arrays.asList(task1, task2));
        mockedFileHandler.when(FileHandler::readTasksFromFile).thenReturn(currentTasks);
        taskManager = new TaskManagerImpl(); // Re-initialize to load these tasks
        outContent.reset(); // Clear constructor output

        // Act
        taskManager.displayAllTasks();

        // Assert: Check System.out for printed content
        String output = outContent.toString();
        assertTrue(output.contains("--- Displaying Tasks ---"));
        assertTrue(output.contains("--- Task ID: 1 ---"));
        assertTrue(output.contains("Title: Task A"));
        assertTrue(output.contains("--- Task ID: 2 ---"));
        assertTrue(output.contains("Title: Task B"));
    }

    @Test
    void displayGivenTasks_displaysEmptySetCorrectly() {
        // Arrange: An empty set
        Set<Task> emptySet = new TreeSet<>();
        outContent.reset(); // Clear setup output

        // Act
        taskManager.displayGivenTasks(emptySet);

        // Assert: Console output contains "No tasks to display."
        assertTrue(outContent.toString().contains("No tasks to display."));
        assertFalse(outContent.toString().contains("--- Task ID:")); // Ensure no task details are printed
    }


    // --- Test searchTask() ---
    @Test
    void searchTask_returnsEmptySetIfNoMatch() {
        // Arrange: Populate TaskManager with a task
        Task task1 = createSimpleTask(1, "Unique Title", "Unique Description");
        mockedFileHandler.when(FileHandler::readTasksFromFile).thenReturn(new TreeSet<>(Collections.singletonList(task1)));
        taskManager = new TaskManagerImpl(); // Re-initialize to load this task

        // Act: Search for a non-existent task
        Set<Task> results = taskManager.searchTask("nonexistent", "nothing");

        // Assert: Result set should be empty
        assertTrue(results.isEmpty());
    }

    @Test
    void searchTask_findsByTitleCaseInsensitive() {
        // Arrange
        Task task1 = createSimpleTask(1, "Meeting Prep", "Prepare agenda");
        mockedFileHandler.when(FileHandler::readTasksFromFile).thenReturn(new TreeSet<>(Collections.singletonList(task1)));
        taskManager = new TaskManagerImpl();

        // Act
        Set<Task> results = taskManager.searchTask("meeting", null); // null description handled

        // Assert
        assertEquals(1, results.size());
        assertTrue(results.contains(task1));
    }

    @Test
    void searchTask_findsByDescriptionCaseInsensitive() {
        // Arrange
        Task task1 = createSimpleTask(1, "Meeting Prep", "Prepare agenda");
        mockedFileHandler.when(FileHandler::readTasksFromFile).thenReturn(new TreeSet<>(Collections.singletonList(task1)));
        taskManager = new TaskManagerImpl();

        // Act
        Set<Task> results = taskManager.searchTask(null, "agenda"); // null title handled

        // Assert
        assertEquals(1, results.size());
        assertTrue(results.contains(task1));
    }

    @Test
    void searchTask_findsByBothTitleAndDescription() {
        // Arrange
        Task task1 = createSimpleTask(1, "Meeting Prep", "Prepare agenda for Project X");
        Task task2 = createSimpleTask(2, "Review Doc", "Finalize report on Project Y");
        mockedFileHandler.when(FileHandler::readTasksFromFile).thenReturn(new TreeSet<>(Arrays.asList(task1, task2)));
        taskManager = new TaskManagerImpl();

        // Act
        Set<Task> results = taskManager.searchTask("Meeting", "Project X");

        // Assert
        assertEquals(1, results.size());
        assertTrue(results.contains(task1));
        assertFalse(results.contains(task2));
    }

    @Test
    void searchTask_handlesNullSearchStrings() {
        // Arrange: Add a task
        Task task1 = createSimpleTask(1, "Title", "Description");
        mockedFileHandler.when(FileHandler::readTasksFromFile).thenReturn(new TreeSet<>(Collections.singletonList(task1)));
        taskManager = new TaskManagerImpl();

        // Act: Search with both nulls (should return all tasks)
        Set<Task> results = taskManager.searchTask(null, null);

        // Assert
        assertEquals(1, results.size());
        assertTrue(results.contains(task1));
    }

    // --- Test completeTask() ---
    @Test
    void completeTask_marksTaskAsCompletedAndSavesToFile() {
        // Arrange: Populate TaskManager with a task initially PENDING
        Task taskToComplete = createSimpleTask(1, "To Complete", "Change my status");
        taskToComplete.setStatus(Status.PENDING); // Ensure it's PENDING initially
        Set<Task> initialTasks = new TreeSet<>(Collections.singletonList(taskToComplete));
        mockedFileHandler.when(FileHandler::readTasksFromFile).thenReturn(initialTasks);
        taskManager = new TaskManagerImpl(); // Re-initialize to load this task
        outContent.reset(); // Clear constructor output

        // Assert initial status in manager's internal set
        assertEquals(Status.PENDING, taskManager.getTaskById(taskToComplete.getId()).getStatus());

        // Act: Complete the task
        taskManager.completeTask(taskToComplete.getId());

        // Assert:
        // 1. TaskManager's internal state reflects the change
        Task updatedTaskInManager = taskManager.getTaskById(taskToComplete.getId());
        assertNotNull(updatedTaskInManager);
        assertEquals(Status.COMPLETED, updatedTaskInManager.getStatus(), "Task status in manager should be COMPLETED.");

        // 2. Verify FileHandler.writeTasksToFile was called with the updated set
        Set<Task> expectedTasksAfterUpdate = new TreeSet<>();
        Task completedTaskVersion = createSimpleTask(1, "To Complete", "Change my status"); // Create a new Task with the expected completed status
        completedTaskVersion.setStatus(Status.COMPLETED); // Set the status for comparison
        expectedTasksAfterUpdate.add(completedTaskVersion);

        mockedFileHandler.verify(() -> FileHandler.writeTasksToFile(expectedTasksAfterUpdate), times(1));

        // 3. Console output
        assertTrue(outContent.toString().contains("Task ID " + taskToComplete.getId() + " marked as COMPLETED and file saved."));
    }

    @Test
    void completeTask_doesNothingIfTaskNotFound() {
        // Arrange: Populate TaskManager with one task (not the one we'll try to complete)
        Task existingTask = createSimpleTask(10, "Existing Task", "Desc");
        Set<Task> initialTasks = new TreeSet<>(Collections.singletonList(existingTask));
        mockedFileHandler.when(FileHandler::readTasksFromFile).thenReturn(initialTasks);
        taskManager = new TaskManagerImpl(); // Re-initialize
        outContent.reset();

        // Act: Try to complete a non-existent task
        taskManager.completeTask(999L); // Non-existent ID

        // Assert:
        // 1. TaskManager's internal set size and content should remain the same
        assertEquals(1, taskManager.getTasks().size(), "Task count should not change.");
        assertTrue(taskManager.getTasks().contains(existingTask), "Existing task should still be in memory.");
        // 2. Verify FileHandler.writeTasksToFile was NOT called
        mockedFileHandler.verify(() -> FileHandler.writeTasksToFile(anySet()), never());
        // 3. Console output
        assertTrue(outContent.toString().contains("Task ID 999 not found."));
    }

    @Test
    void completeTask_handlesNullId() {
        outContent.reset();
        taskManager.completeTask(null);
        assertTrue(outContent.toString().contains("Task ID to complete cannot be null."));
        mockedFileHandler.verify(() -> FileHandler.writeTasksToFile(anySet()), never());
    }

    // --- Test deleteTask() ---
    @Test
    void deleteTask_removesTaskAndSavesToFile() {
        // Arrange: Populate TaskManager with two tasks
        Task taskToDelete = createSimpleTask(1, "Delete Me", "Desc");
        Task anotherTask = createSimpleTask(2, "Keep Me", "Desc");
        Set<Task> initialTasks = new TreeSet<>(Arrays.asList(taskToDelete, anotherTask));
        mockedFileHandler.when(FileHandler::readTasksFromFile).thenReturn(initialTasks);
        taskManager = new TaskManagerImpl(); // Re-initialize
        outContent.reset();

        // Act: Delete taskToDelete
        taskManager.deleteTask(taskToDelete.getId());

        // Assert:
        // 1. TaskManager's internal state
        assertEquals(1, taskManager.getTasks().size(), "Only one task should remain in memory.");
        assertFalse(taskManager.getTasks().contains(taskToDelete), "Deleted task should not be in memory.");
        assertTrue(taskManager.getTasks().contains(anotherTask), "Other task should still be in memory.");

        // 2. Verify FileHandler.writeTasksToFile was called with the updated set
        Set<Task> expectedTasksAfterDelete = new TreeSet<>(Collections.singletonList(anotherTask));
        mockedFileHandler.verify(() -> FileHandler.writeTasksToFile(expectedTasksAfterDelete), times(1));

        // 3. Console output
        assertTrue(outContent.toString().contains("Task ID " + taskToDelete.getId() + " deleted and file saved."));
    }

    @Test
    void deleteTask_doesNothingIfTaskNotFound() {
        // Arrange: Populate TaskManager with one task
        Task existingTask = createSimpleTask(10, "Existing Task", "Desc");
        Set<Task> initialTasks = new TreeSet<>(Collections.singletonList(existingTask));
        mockedFileHandler.when(FileHandler::readTasksFromFile).thenReturn(initialTasks);
        taskManager = new TaskManagerImpl(); // Re-initialize
        outContent.reset();

        // Act: Try to delete a non-existent task
        taskManager.deleteTask(999L); // Non-existent ID

        // Assert:
        // 1. TaskManager's internal set size and content should remain the same
        assertEquals(1, taskManager.getTasks().size(), "Task count should not change.");
        assertTrue(taskManager.getTasks().contains(existingTask), "Existing task should still be in memory.");
        // 2. Verify FileHandler.writeTasksToFile was NOT called
        mockedFileHandler.verify(() -> FileHandler.writeTasksToFile(anySet()), never());
        // 3. Console output
        assertTrue(outContent.toString().contains("Task ID 999 not found."));
    }

    @Test
    void deleteTask_handlesNullId() {
        outContent.reset();
        taskManager.deleteTask(null);
        assertTrue(outContent.toString().contains("Task ID to delete cannot be null."));
        mockedFileHandler.verify(() -> FileHandler.writeTasksToFile(anySet()), never());
    }

    // --- Test getTaskById() ---
    @Test
    void getTaskById_returnsCorrectTask() {
        // Arrange: Populate TaskManager with tasks
        Task task1 = createSimpleTask(1, "Task One", "Desc 1");
        Task task2 = createSimpleTask(2, "Task Two", "Desc 2");
        Set<Task> currentTasks = new TreeSet<>(Arrays.asList(task1, task2));
        mockedFileHandler.when(FileHandler::readTasksFromFile).thenReturn(currentTasks);
        taskManager = new TaskManagerImpl(); // Re-initialize

        // Act & Assert
        assertEquals(task1, taskManager.getTaskById(task1.getId()));
        assertEquals(task2, taskManager.getTaskById(task2.getId()));
    }

    @Test
    void getTaskById_returnsNullIfNotFound() {
        // Arrange: Empty tasks or tasks not containing the ID
        mockedFileHandler.when(FileHandler::readTasksFromFile).thenReturn(new TreeSet<>()); // Empty
        taskManager = new TaskManagerImpl();

        // Act & Assert
        assertNull(taskManager.getTaskById(999L));
    }

    @Test
    void getTaskById_returnsNullForNullId() {
        assertNull(taskManager.getTaskById(null));
    }
}