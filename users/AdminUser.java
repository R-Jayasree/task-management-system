package users;
import java.io.*;
import java.text.*;
import java.util.*;
import tasks.*;
import exceptions.*;
public class AdminUser extends User {
    private Group group;
    private static final String HEADER_COLOR = "\u001B[34m"; // Blue
    private static final String ROW_COLOR = "\u001B[32m"; // Green
    private static final String RESET_COLOR = "\u001B[0m"; // Reset color
    
    public AdminUser(int userId, String email, String username, String password, String usertype) {
        super(userId,email,username,password , usertype);
    }
    public Group getGroup(){
        return group;
    }
    public void createGroup() {
        try (BufferedReader reader = new BufferedReader(new FileReader(".\\main\\groups.csv"))) {
            boolean groupExists = false;
            String line;
            List<String> groups = new ArrayList<>();
            List<Integer> members = new ArrayList<>();
            // Check if the admin already has a group
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[1].equals(userId)) {
                    groupExists = true;
                    System.out.println("You have a group with ID: " + data[0] + " and name: " + data[2]);
                    for (int i = 3; i < data.length; i++){
                        members.add(Integer.parseInt(data[i]));
                    }
                    return;
                }
                groups.add(data[0]);
            }
            // If no group exists, create a new one
            if (!groupExists) {
                String groupId;
                Scanner scanner = new Scanner(System.in);
                System.out.println("Enter a unique Group ID for your group: ");
                groupId = scanner.nextLine();
                if (groups.contains(groupId)){
                    System.out.println("Group ID already exists. Please try a different ID.");
                    loadWorkTasksFromFile();
                    return;
                }
                System.out.println("Enter Group Name: ");
                String groupName = scanner.nextLine();
                group = new Group(groupId, groupName, userId);
                group.setMembers(members);
                // Write the new group data to groups.csv
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(".\\main\\groups.csv", true))) {
                    writer.write(groupId + "," + userId + "," + groupName + ",");
                    writer.newLine();
                    System.out.println("Group created successfully with ID: " + groupId + " and Name: " + groupName);
                }
            }
        } catch (IOException e) {
            System.out.println("Error accessing groups file. ");
        }
    }
    public void loadWorkTasksFromFile() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<WorkTask> workTasks = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(".\\main\\work_tasks.csv"))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                String groupId = fields[1].trim();
                if (groupId.equals(group.getGroupId())){
                    int taskId = Integer.parseInt(fields[0].trim());
                    int assignedUserId = Integer.parseInt(fields[2].trim());
                    String title = fields[3].trim();
                    String description = fields[4].trim();
                    String status = fields[5].trim();
                    String tag = fields[6].trim();
                    String priority = fields[7].trim();
                    Date startDate = null;
                    Date dueDate = null;
                    
                    try {
                        startDate = dateFormat.parse(fields[8].trim());
                        dueDate = dateFormat.parse(fields[9].trim());
                    } catch (ParseException e) {
                        System.out.println("Error parsing date in file: ");
                    }
                    
                    WorkTask workTask = new WorkTask(taskId, title, description, status, tag, priority, startDate, dueDate, assignedUserId);
                    workTasks.add(workTask);
                }
            }
            group.setWorkTasks(workTasks);
        } catch (IOException e) {
            System.out.println("Error reading from file. ");
        }
    }
    public void printAllTasks(List<WorkTask> tasks) {
        if (tasks.isEmpty()) {
            System.out.println("No work tasks present.");
            return;
        }
        System.out.println(HEADER_COLOR + String.format("%-8s %-15s %-20s %-30s %-10s %-10s %-10s %-12s %-12s",
                "TaskID", "AssignedUserID", "Title", "Description", "Status", "Tag", "Priority", "StartDate", "DueDate") + RESET_COLOR);
        System.out.println("--------------------------------------------------------------------------------------------------------------" +
                "------------------------------------------------");
        for (WorkTask task : group.getWorkTasks()){
            System.out.println(ROW_COLOR + String.format("%-8s %-15s %-20s %-30s %-10s %-10s %-10s %-12s %-12s",
                task.getTaskId(), task.getAssignedUser(), task.getTitle(), task.getDescription(),
                task.getStatus(), task.getTag(), task.getPriority(), task.getStartDate(), task.getDueDate()) + RESET_COLOR);
            System.out.println("--------------------------------------------------------------------------------------------------------------" +
                "------------------------------------------------");
        }
    }

    // Method to add a member to the group
    public void addMemberToGroup() throws UserNotFoundException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the User ID to add as a member: ");
        int id = scanner.nextInt();
        boolean isUserInFile = false;
        try (BufferedReader br = new BufferedReader(new FileReader(".\\main\\users.csv"))) {
            br.readLine(); // Skip the header line
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                int idInFile = Integer.parseInt(values[0]);
                if (idInFile == userId) {
                    isUserInFile = true;
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading users.csv file: " + e.getMessage());
        }
        
        if (isUserInFile) {
            List<Integer> members = group.getMembers();
            members.add(id);
            group.setMembers(members);
            System.out.println("User ID " + userId + " added successfully to the group.");
        } else {
            throw new UserNotFoundException("User with ID " + userId + " not found in users.csv file.");
        }
    }
    
    public void removeMemberFromGroup() throws UserNotFoundException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the user ID to remove: ");
        int userIdToRemove = scanner.nextInt();
        
        // Check if the userId exists in the members list
        if (group.getMembers().contains(userIdToRemove)) {
            group.getMembers().remove(Integer.valueOf(userIdToRemove));
            System.out.println("User with ID " + userIdToRemove + " has been removed from the group.");
        } else {
            throw new UserNotFoundException("User with ID " + userIdToRemove + " is not a member of this group.");
        }
    }

    public void addWorkTaskToGroup() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Task ID: ");
        int taskId = scanner.nextInt();

        System.out.print("Enter Assigned User ID: ");
        int assignedUserId = scanner.nextInt();
        scanner.nextLine(); 

        System.out.print("Enter Title: ");
        String title = scanner.nextLine();

        System.out.print("Enter Description: ");
        String description = scanner.nextLine();

        System.out.print("Enter Status: ");
        String status = scanner.nextLine();

        System.out.print("Enter Tag: ");
        String tag = scanner.nextLine();

        System.out.print("Enter Priority: ");
        String priority = scanner.nextLine();

        System.out.print("Enter Start Date (yyyy-MM-dd): ");
        Date startDate = parseDate(scanner.nextLine());

        System.out.print("Enter Due Date (yyyy-MM-dd): ");
        Date dueDate = parseDate(scanner.nextLine());

        WorkTask newTask = new WorkTask(taskId, title, description, status, tag, priority, startDate, dueDate, assignedUserId);
        group.getWorkTasks().add(newTask);
        System.out.println("Work task added successfully to the group.");
    }

    public void deleteWorkTaskFromGroup() throws TaskNotFoundException {
        Scanner scanner = new Scanner(System.in);

        // Get the taskId of the task to be deleted
        System.out.print("Enter the Task ID of the work task to delete: ");
        int taskIdToDelete = scanner.nextInt();
        WorkTask taskToDelete = null;
        for (WorkTask task : group.getWorkTasks()) {
            if (task.getTaskId() == taskIdToDelete) {
                taskToDelete = task;
                break;
            }
        }
        if (taskToDelete != null) {
            group.getWorkTasks().remove(taskToDelete);
            System.out.println("Work task with Task ID " + taskIdToDelete + " has been successfully deleted.");
        } else {
            throw new TaskNotFoundException("Work Task with Task ID " + taskIdToDelete + " not found.");
        }
    }


    // Option to update a work task in the group
    public void updateWorkTaskInGroup(int taskId, WorkTask updatedTask) {
        //group.updateWorkTask(taskId, updatedTask);
    }
    // Sorting tasks by priority or due date
    public void sortGroupTasks(Comparator<Task> comparator) {
        //group.sortTasks(comparator);
    }

    private Date parseDate(String dateStr) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (ParseException e) {
            System.out.println("Invalid date entered! Unable to parse.");
            return null; 
        }
    }
}

