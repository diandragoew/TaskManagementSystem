import model.task.Category;
import model.task.Priority;
import model.task.Status;
import model.task.Task;
import model.user.Administrator;
import model.user.Employee;
import model.user.User;
import service.TaskManagerImpl;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public class Main {
    private static Set<User> users = new TreeSet<>();
    private static User loggedInUser = null;

    public static void main(String[] args) {
        try { //global try-catch
            TaskManagerImpl taskManager = new TaskManagerImpl();

            User user1 = new Employee("ivan", "123", taskManager);
            User user2 = new Administrator("pesho", "1234", taskManager);
            User user3 = new Employee("georgi", "12345", taskManager);
            User user4 = new Administrator("martin", "123456", taskManager);
            users.add(user1);
            users.add(user2);
            users.add(user3);
            users.add(user4);

            Scanner scanner = new Scanner(System.in);
            int action;
            Task task;

            while (true) {
                System.out.println("\n--- Task Management System Menu ---");
                if (loggedInUser == null) {
                    System.out.println("7. Login");
                    System.out.println("6. Exit program");
                } else {
                    System.out.println("Logged in as: " + loggedInUser.getUsername() + " (" + loggedInUser.getClass().getSimpleName() + ")");
                    System.out.println("1. Display all tasks");
                    System.out.println("2. Create task");
                    System.out.println("3. Search tasks");
                    System.out.println("4. Complete task");
                    System.out.println("5. Delete task");
                    System.out.println("8. Logout");
                    System.out.println("6. Exit program");
                }
                System.out.print("Enter your choice: ");

                try {
                    action = scanner.nextInt();
                    scanner.nextLine(); // Consume newline

                    if (loggedInUser == null) {
                        if (action == 7) {
                            handleLogin(scanner);
                            continue;
                        } else if (action == 6) {
                            System.out.println("You chose 6 and exited from the program. Goodbye!");
                            scanner.close();
                            return;
                        } else {
                            System.out.println("Please login first (Option 7) to access task features, or exit (Option 6).");
                            continue;
                        }
                    }

                    switch (action) {
                        case 1:
                            loggedInUser.displayAllTasks();
                            break;
                        case 2:
                            System.out.println("Creating a new task...");
                            System.out.print("Enter Task Title: ");
                            String title = scanner.nextLine();
                            System.out.print("Enter Task Description: ");
                            String description = scanner.nextLine();

                            // --- Input for Due Date ---
                            LocalDate dueDate = null;
                            while (dueDate == null) {
                                System.out.print("Enter Due Date (YYYY-MM-DD): ");
                                String dueDateStr = scanner.nextLine();
                                try {
                                    dueDate = LocalDate.parse(dueDateStr);
                                } catch (DateTimeParseException e) {
                                    System.out.println("Invalid date format. Please use YYYY-MM-DD.");
                                }
                            }

                            // --- Input for Priority ---
                            Priority priority = null;
                            while (priority == null) {
                                System.out.print("Enter Priority (LOW, MEDIUM, HIGH): ");
                                String priorityStr = scanner.nextLine().toUpperCase(); // Convert to uppercase for enum matching
                                try {
                                    priority = Priority.valueOf(priorityStr);
                                } catch (IllegalArgumentException e) {
                                    System.out.println("Invalid Priority. Please choose from LOW, MEDIUM, HIGH.");
                                }
                            }

                            // --- Input for Status ---
                            // It's common for new tasks to start as PENDING, but if user can choose:
                            Status status = null;
                            while (status == null) {
                                System.out.print("Enter Status (PENDING, COMPLETED): ");
                                String statusStr = scanner.nextLine().toUpperCase();
                                try {
                                    status = Status.valueOf(statusStr);
                                } catch (IllegalArgumentException e) {
                                    System.out.println("Invalid Status. Please choose from PENDING,COMPLETED.");
                                }
                            }

                            // --- Input for Category ---
                            Category category = null;
                            while (category == null) {
                                System.out.print("Enter Category (WORK, PERSONAL): ");
                                String categoryStr = scanner.nextLine().toUpperCase();
                                try {
                                    category = Category.valueOf(categoryStr);
                                } catch (IllegalArgumentException e) {
                                    System.out.println("Invalid Category. Please choose from WORK, PERSONAL");
                                }
                            }

                            task = loggedInUser.createTask(
                                    title,
                                    description,
                                    dueDate,
                                    priority,
                                    status,
                                    category,
                                    LocalDate.now() // current date is set on creationDate
                            );
                            System.out.println("Task created: " + task.getTitle());
                            break;
                        case 3:
                            System.out.print("Enter search title (leave empty for none): ");
                            String searchTitle = scanner.nextLine();
                            System.out.print("Enter search description (leave empty for none): ");
                            String searchDescription = scanner.nextLine();
                            Set<Task> searchedTasks = loggedInUser.searchTask(searchTitle, searchDescription);
                            if (searchedTasks.isEmpty()) {
                                System.out.println("No tasks found matching your criteria.");
                            } else {
                                System.out.println("Found tasks:");
                                loggedInUser.displayGivenTasks(searchedTasks);
                            }
                            break;
                        case 4:
                            System.out.print("Enter the ID of the task to complete: ");
                            Long completeTaskId = scanner.nextLong();
                            scanner.nextLine();
                            loggedInUser.completeTask(completeTaskId);
                            break;
                        case 5:
                            System.out.print("Enter the ID of the task to delete: ");
                            Long deleteTaskId = scanner.nextLong();
                            scanner.nextLine();
                            loggedInUser.deleteTask(deleteTaskId);
                            break;
                        case 8: // Logout option
                            loggedInUser = null;
                            System.out.println("Successfully logged out.");
                            break;
                        case 6: // Exit program
                            System.out.println("You chose 6 and exited from the program. Goodbye!");
                            scanner.close();
                            return;
                        default:
                            System.out.println("Invalid choice. Please enter a valid number from the menu.");
                            break;
                    }
                } catch (InputMismatchException e) { // Use specific exception import
                    System.out.println("Invalid input. Please enter a number.");
                    scanner.nextLine(); // Consume the invalid input
                } catch (Exception e) {
                    System.err.println("An unexpected error occurred: " + e.getMessage());
                    e.printStackTrace();
                    scanner.close();
                    return;
                }
            }
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void handleLogin(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        Optional<User> foundUser = users.stream()
                .filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password))
                .findFirst();

        if (foundUser.isPresent()) {
            loggedInUser = foundUser.get();
            System.out.println("Login successful! Welcome, " + loggedInUser.getUsername() + ".");
        } else {
            System.out.println("Invalid username or password. Please try again.");
            loggedInUser = null;
        }
    }
}