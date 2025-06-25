package main;
import exceptions.*;
import java.io.*;
import java.util.Scanner;
import users.*;
public class TaskManagementSystem {

    private static final String USERS_FILE = ".\\main\\users.csv";
    private static final String RESET_COLOR = "\u001B[0m";
    private static final String TEXT_COLOR = "\u001B[32m"; // Green 
    private static final String OPTION_COLOR = "\u001B[34m"; // Blue 
    private static final String HEADER_COLOR = "\u001B[35m"; 


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Console console = System.console();
        System.out.println("\nWelcome to the Task Management System!");
        System.out.println("*******************************************\n");
        System.out.print("Are you an existing user? (y/n): ");
        String choice = scanner.nextLine();

        if (choice.equalsIgnoreCase("y")) {
            System.out.print("Enter your email: ");
            String email = scanner.nextLine();
            System.out.print("Enter your password: ");
            char[] passwordArray = console.readPassword();  
            String password = new String(passwordArray);
            if (loginUser(email,password)) {
                System.out.println("Login successful!");
                createUserObject(email);
            } else {
                System.out.println("Invalid credentials. Please try again.");
            }
        } else {            
            if (signUpUser(scanner)){
                System.out.println("Sign-up successful! Please log in to continue.");
                System.out.print("\nEnter your email: ");
                String email = scanner.nextLine();
                System.out.print("Enter your password: ");
                String password = new String(console.readPassword());
                if (loginUser(email,password)) {
                    System.out.println("Login successful!\n");
                    createUserObject(email);
                } else {
                    System.out.println("Invalid credentials. Please try again.");
                }
            }
            scanner.close();
       }
    }

    // Method to log in the user by verifying credentials
    private static boolean loginUser(String email, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values[1].equals(email) && values[3].equals(password)) {
                    return true;  
                }
            }
        } catch (IOException e) {
            System.out.println("Error accessing users file.");
        }
        return false;  
    }

    // Method to handle new user sign-up
    private static boolean signUpUser(Scanner scanner) {
        Console console = System.console();
        System.out.print("Enter your email: ");
        String email = scanner.nextLine();
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = new String(console.readPassword());
        System.out.print("Confirm your password: ");
        String confirmPassword = new String(console.readPassword());

        if (!password.equals(confirmPassword)) {
            System.out.println("Passwords do not match. Please try again.");
            return false;  
        }

        System.out.print("Enter user type (Admin/Regular): ");
        String userType = scanner.nextLine();
        int userId = getNextUserId();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(USERS_FILE, true))) {
            bw.write(userId + "," + email + "," + username + "," + password + "," + userType);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error saving user data.");
        }
        return true;
    }

    // Method to get the next user ID to assign
    private static int getNextUserId() {
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE))) {
            br.readLine();
            while (br.readLine() != null) count++;
        } catch (IOException e) {
            System.out.println("Error reading users file.");
        } 
        return count + 1; 
    }

    // Method to check the user type and call the appropriate menu
    public static void createUserObject(String email) {
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE))) {
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                //System.out.println(values[0] +values[1]+values[2]+ values[3]);
                String userType = values[4];
                if (values[1].equals(email)) {
                    int userId = Integer.parseInt(values[0]);
                    if (userType.equalsIgnoreCase("Regular")) {
                        RegularUser regularUser = new RegularUser(userId, email, values[2], values[3], userType);
                        regularUserMenu(regularUser);
                    } else if (userType.equalsIgnoreCase("Admin")) {
                        AdminUser adminUser = new AdminUser(userId,email,values[2], values[3], userType);
                        adminUserMenu(adminUser); 
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("users.csv file not found. ");
        } catch (NumberFormatException e){
            System.out.println("Error reading users.csv file" + e.getMessage() + "\n" );
        }
    }

    // Regular User Menu
    public static void regularUserMenu(RegularUser user) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println(HEADER_COLOR + "Welcome, " + user.getUsername() + "! Select your mode:" + RESET_COLOR);
        System.out.println(OPTION_COLOR + "1. Personal Use" + RESET_COLOR);
        System.out.println(OPTION_COLOR + "2. Work Use" + RESET_COLOR);

        int modeChoice;
        System.out.print(TEXT_COLOR + "Enter your choice (1 for Personal Use, 2 for Work Use): " + RESET_COLOR);
        modeChoice = scanner.nextInt();
        scanner.nextLine(); 

        if (modeChoice == 1) {
            System.out.println("\n" + HEADER_COLOR + "PERSONAL USE MODE" + RESET_COLOR);
            personalUseMenu(user);
        } else {
            System.out.println("\n" + HEADER_COLOR + "WORK USE MODE" + RESET_COLOR);
        }
    }

    private static void personalUseMenu(RegularUser user) {
        Scanner scanner = new Scanner(System.in);

        System.out.println(HEADER_COLOR + "\nPersonal Task Management Options:" + RESET_COLOR);
        System.out.println(OPTION_COLOR + "1. View Tasks" + RESET_COLOR);
        System.out.println(OPTION_COLOR + "2. Add Task" + RESET_COLOR);
        System.out.println(OPTION_COLOR + "3. Update Task" + RESET_COLOR);
        System.out.println(OPTION_COLOR + "4. Delete Task" + RESET_COLOR);
        System.out.println(OPTION_COLOR + "5. Sort Tasks" + RESET_COLOR);
        System.out.println(OPTION_COLOR + "6. Filter Tasks" + RESET_COLOR);
        System.out.println(OPTION_COLOR + "7. Export Personal Tasks" + RESET_COLOR);
        System.out.println(OPTION_COLOR + "8. Export Work Tasks" + RESET_COLOR);
        System.out.println(OPTION_COLOR + "9. Exit" + RESET_COLOR);

        int choice;
        do {
            System.out.print(TEXT_COLOR + "\nChoose an option (1-8): " + RESET_COLOR);
            choice = new Scanner(System.in).nextInt();
            switch (choice) {
                case 1: 
                    System.out.println("View Tasks selected."); 
                    user.printAllTasks(user.getPersonalTasks());
                    break;

                case 2: 
                    System.out.println("Add Task selected."); 
                    try{
                        user.addTask();
                    } catch (EndDateBeforeStartDateException e){
                        System.out.println(e.getMessage());
                    }
                    break;

                case 3: 
                    System.out.println("Update Task selected."); 
                    try{
                        user.updateTask();
                    } catch (TaskNotFoundException e){
                        System.out.println(e.getMessage());
                    }
                    break;

                case 4: 
                    System.out.println("Delete Task selected.");
                    System.out.println("Do you want to delete by ID or by Name? (Enter 'ID' or 'Name')");
                    String deleteOption = scanner.nextLine();
                    
                    if (deleteOption.equalsIgnoreCase("ID")) {
                        System.out.print("Enter Task ID to delete: ");
                        int taskId = scanner.nextInt();
                        try{
                            user.deleteTask(taskId);
                        } catch (TaskNotFoundException e){
                            System.out.println(e.getMessage());
                        }   
                    } else if (deleteOption.equalsIgnoreCase("Name")) {
                        System.out.print("Enter Task Name to delete: ");
                        String taskName = scanner.nextLine();
                        try{
                            user.deleteTask(taskName);
                        } catch (TaskNotFoundException e){
                            System.out.println(e.getMessage());
                        } 
                    } else {
                        System.out.println("Invalid option selected.");
                    }
                    break;

                case 5: 
                    System.out.println("Sort Tasks selected."); 
                    user.sortTasks();
                    break;

                case 6: 
                    System.out.println("Filter Tasks selected."); 
                    try{
                        user.filterTasksMenu();
                    } catch (InvalidFilterCriteriaException e){
                        System.out.println(e.getMessage());
                    }
                    break;

                case 7: 
                    System.out.println("Export Personal Tasks selected."); 
                    String filePath = user.getUsername() + "_personalTasks.csv";
                    String header = "TaskId, Task Title, Description,Status,Tag,Priority,Start Date,Due Date,isRecurring,Frequency\n";
                    user.exportToCSV(user.getPersonalTasks(), filePath, header);
                    break;

                case 8 :
                    System.out.println("Export work Tasks selected."); 
                    filePath = user.getUsername() + "_workTasks.csv";
                    header = "TaskID,Title,Description,Status,Tag,Priority,StartDate,DueDate\n";
                    user.exportToCSV(user.getWorkTasks(), filePath, header);
                    break;

                case 9: 
                    System.out.println(TEXT_COLOR + "\nHave a nice day !! :)\nByeee ..." + RESET_COLOR); 
                    user.updateTaskFile();
                    break;

                default: 
                    System.out.println("Invalid option. Please choose a number between 1 and 8.");
            }
        } while (choice != 8);
    }

    private static void adminUserMenu(AdminUser user) {
        Scanner scanner = new Scanner(System.in);
        
        user.createGroup();

        while (true) {
            System.out.println("=====================================");
            System.out.println("Admin Menu Options:");
            System.out.println("1. View all Work Tasks");
            System.out.println("2. Add a member to group");
            System.out.println("3. Remove a member from group");
            System.out.println("4. Add a work task");
            System.out.println("5. Remove a work task");

            System.out.println("6. Exit\n");
            System.out.print("Enter your option (1-10): ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); 

            switch (choice) {
                case 1 :
                    user.printAllTasks(user.getGroup().getWorkTasks());
                    break;
                case 2:
                    
                    System.out.println("Adding a member to the group...");
                    try{
                        user.addMemberToGroup();
                    } catch (UserNotFoundException e){
                        System.out.println(e.getMessage());
                    }
                    break;

                case 3:
                    System.out.println("Removing a member from the group...");
                    try{
                        user.removeMemberFromGroup();
                    } catch (UserNotFoundException e){
                        System.out.println(e.getMessage());
                    }
                    break;

                case 4:
                    System.out.println("Adding a work task...");
                    user.addWorkTaskToGroup();
                    break;

                case 5:
                    System.out.println("Removing a work task...");
                    try {
                        user.deleteWorkTaskFromGroup();
                    } catch (TaskNotFoundException e) {
                        System.out.println(e.getMessage());
                    }
                    break;

                case 6:
                    System.out.println("Exiting Admin Menu.");
                    return;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

}
