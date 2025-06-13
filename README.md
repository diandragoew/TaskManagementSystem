# Task Management System

![Java Version](https://img.shields.io/badge/java-1.8-orange)
![Maven Build](https://img.shields.io/badge/build-Maven-brightgreen)

## Description

This project implements a simple console-based Task Management System in Java. It allows users to manage their tasks by creating, viewing, searching, completing, and deleting them. Tasks are categorized, prioritized, and their status can be tracked. The system supports multiple users with different roles (Employee and Administrator) and persists task data to a file, ensuring data is saved between application runs.

## Table of Contents

-   [Features](#features)
-   [Technologies Used](#technologies-used)
-   [Setup and Installation](#setup-and-installation)
-   [Usage](#usage)
-   [User Credentials](#user-credentials)
-   [Running Tests](#running-tests)
-   [File Structure](#file-structure)
-   [Contributing](#contributing)
-   [License](#license)
-   [Contact](#contact)

## Features

-   **User Authentication:** Login functionality for existing users (`Employee` and `Administrator` roles).
-   **Task Creation:** Create new tasks with title, description, due date, priority (LOW, MEDIUM, HIGH), status (PENDING, COMPLETED), category (WORK, PERSONAL), and creation date.
-   **Task Display:**
    -   Display all tasks currently in the system.
    -   Display a specific set of tasks (e.g., search results).
-   **Task Search:** Search tasks by title and/or description (case-insensitive).
-   **Task Completion:** Mark existing tasks as `COMPLETED` using their ID.
-   **Task Deletion:** Remove tasks from the system using their ID.
-   **Data Persistence:** All task operations (create, complete, delete) are saved to a file (`src/main/resources/tasks`) using a `FileHandler`.
-   **Unique Task IDs:** Automatically generates a unique ID for each new task.

## Technologies Used

-   **Java 8**: The core programming language.
-   **Apache Maven 3.x**: For project build automation and dependency management.
-   **JUnit 5**: The testing framework used for unit tests.
-   **Mockito 3.x**: A mocking framework used in tests, specifically `mockito-inline` for mocking static methods (e.g., `FileHandler`).

## Setup and Installation

Follow these steps to get a local copy of the project up and running on your machine.

### Prerequisites

-   Java Development Kit (JDK) 8
-   Apache Maven (latest stable version recommended)
-   Git

### Steps

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/diandragoew/TaskManagementSystem.git
    cd TaskManagementSystem
    ```

2.  **Build the project using Maven:**
    ```bash
    mvn clean install
    ```
    This command will download all necessary project dependencies (including JUnit and Mockito) and compile the source code, creating a runnable JAR file in the `target/` directory.

## Usage

After successfully building the project, you can run the application from your terminal.

1.  **Navigate to the project root (if not already there):**
    ```bash
    cd ...:\...\...\...\TaskManagementSystem
    ```
2.  **Run the application:**
    ```bash
    java -jar target/TaskManagementSystem-1.0-SNAPSHOT.jar
    ```
3.  **Application Flow:**
    The application will present a menu. You must **Login (Option 7)** first to access the task management features.

    ```
    --- Task Management System Menu ---
    7. Login
    6. Exit program
    Enter your choice:
    ```

    Once logged in, the menu will expand:

    ```
    Logged in as: [username] ([UserType])
    1. Display all tasks
    2. Create task
    3. Search tasks
    4. Complete task
    5. Delete task
    8. Logout
    6. Exit program
    Enter your choice:
    ```

    Follow the prompts to perform task operations. Input for dates should be in `YYYY-MM-DD` format, and enums (Priority, Status, Category) should be entered as their names (e.g., `LOW`, `WORK`).

## User Credentials

The application is pre-populated with a few sample user accounts:

| Username | Password | Role          |
| :------- | :------- | :------------ |
| `ivan`   | `123`    | `Employee`    |
| `pesho`  | `1234`   | `Administrator` |
| `georgi` | `12345`  | `Employee`    |
| `martin` | `123456` | `Administrator` |

## Running Tests

To execute the unit tests for the project, navigate to the project's root directory in your terminal and run:

```bash
mvn test