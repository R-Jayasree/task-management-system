package users;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.stream.Collectors;
import tasks.*;
import exceptions.*;

public class RegularUser extends User {
    private List<PersonalTask> personalTasks;
    private Group group;
    
    private static final String RESET_COLOR = "\u001B[0m";
    private static final String HEADER_COLOR = "\u001B[35m"; // Purple
    private static final String TASK_COLOR = "\u001B[34m"; // Blue
    private static final String SUBTASK_COLOR = "\u001B[36m"; // Cyan

    public RegularUser(int userId, String email, String username, String password, String userType) {
        super(userId, email, username, password, userType);
        this.personalTasks = new ArrayList<>();
        loadTasksFromFile();
        
    }
    public List<PersonalTask> getPersonalTasks(){
        return personalTasks;
    }
    // Method to load tasks from the CSV file
    private void loadTasksFromFile() {
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(".\\main\\personal_tasks.csv"))) {
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                int userIdFromFile = Integer.parseInt(values[0]);
                if (userIdFromFile == this.userId) {
                    int taskId = Integer.parseInt(values[1]);
                    String title = values[2], description = values[3], status = values[4], tag = values[5], priority = values[6];
                    Date startDate = new SimpleDateFormat("yyyy-MM-dd").parse(values[7]);
                    Date dueDate = new SimpleDateFormat("yyyy-MM-dd").parse(values[8]);
                    boolean isRecurring = Boolean.parseBoolean(values[9]);
                    // Create a PersonalTask or DeadlineTask based on the details
                    PersonalTask task;
                    if (isRecurring) {
                        String frequency = values[10];
                        task = new RecurringTask(taskId, title, description, status, priority, tag, startDate, dueDate, frequency);
                    } else {
                        task = new DeadlineTask(taskId, title, description, status, priority, tag, startDate, dueDate);
                    } 
                    List<SubTask> subtasks = loadSubtasksFromFile(taskId);   // get subtasks
                    task.setSubTasks(subtasks);
                    personalTasks.add(task);
                }                
            }
        } catch (IOException e){
            System.out.println("Unable to read file");
        } catch(ParseException e) {
            System.out.println("Unable to parse values from file !");
        }
    }
    
    public void printAllTasks(List<PersonalTask> tasks) {
        if (tasks.isEmpty()) {
            System.out.println("Hurray ! No tasks yet..");
            return;
        }
        System.out.println(HEADER_COLOR + String.format("%-10s %-20s %-20s %-11s %-10s %-10s %-15s %-15s %-10s %-10s", 
                "Task ID", "Title", "Description", "Status", "Priority", "Tag", "Start Date", "Due Date", "Recurring", "Frequency") + RESET_COLOR);
        System.out.println(HEADER_COLOR + "--------------------------------------------------------------------------------------------------------------------------------------" + RESET_COLOR);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (PersonalTask task : tasks) {
            String startDate = dateFormat.format(task.getStartDate());
            String dueDate = "";
            String isRecurring = "No";
            String frequency = "-";
        
            if (task instanceof DeadlineTask) {
                DeadlineTask deadlineTask = (DeadlineTask) task;
                dueDate = dateFormat.format(deadlineTask.getDueDate());
            }
            else if (task instanceof RecurringTask) {
                RecurringTask recurringTask = (RecurringTask) task;
                dueDate = dateFormat.format(recurringTask.getEndDate());
                isRecurring = "Yes";
                frequency = recurringTask.getFrequency();
            }
            System.out.println(TASK_COLOR + String.format("%-10s %-20s %-20s %-11s %-10s %-10s %-15s %-15s %-10s %-10s",
                    task.getTaskId(), task.getTitle(), task.getDescription(),
                    task.getStatus(),  task.getPriority(), task.getTag(),
                    startDate, dueDate, isRecurring, frequency) + RESET_COLOR);
            // Print subtasks associated with this task
            for (SubTask subtask : task.getSubTasks()) {
                System.out.println(SUBTASK_COLOR + String.format("%-10s %-20s %-20s %-11s %-10s %-10s %-15s %-15s",
                    "Subtask: ", subtask.getTitle(), "", subtask.getStatus(),
                    subtask.getPriority(), "", 
                    dateFormat.format(subtask.getStartDate()), dateFormat.format(subtask.getEndDate())) + RESET_COLOR);
            }
            System.out.println(HEADER_COLOR + "--------------------------------------------------------------------------------------------------------------------------------------" + RESET_COLOR + "\n");
        }
    }

    public void addTask() throws EndDateBeforeStartDateException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter task type (1 for Deadline Task, 2 for Recurring Task): ");
        int taskType = scanner.nextInt();
        scanner.nextLine(); 
        String title, description, status = "Pending", tag, priority;
        Date startDate = null, endDate = null;

        System.out.print("Enter Task Title : ");
        title = scanner.nextLine();
        System.out.print("Enter Description : ");
        description = scanner.nextLine();
        System.out.print("Enter Tag: ");
        tag = scanner.nextLine();
        System.out.print("Enter Priority (High/Medium/Low): ");
        priority = scanner.nextLine();
        System.out.print("Enter Start Date (yyyy-MM-dd): ");
        String start = scanner.nextLine();
        startDate = parseDate(start);
        int taskId = personalTasks.size() +1;
        PersonalTask task;

        if (taskType == 1) { // Deadline Task
            System.out.print("Enter Due Date (yyyy-MM-dd): ");
            String end = scanner.nextLine();
            endDate = parseDate(end);
            task = new DeadlineTask(taskId, title, description, status, priority, tag, startDate, endDate);

        } else if (taskType == 2){ // Recurring Task
            System.out.print("Enter End Date (yyyy-MM-dd): ");
            String end = scanner.nextLine();
            endDate = parseDate(end);
            System.out.print("Enter Frequency (daily/weekly/monthly): ");
            String frequency = scanner.nextLine();
            task = new RecurringTask(taskId, title, description, status, priority, tag, startDate, endDate, frequency);
        }
        else{
            System.out.println("Invalid option !");
            return;
        }
        if (endDate.before(startDate)) {
                throw new EndDateBeforeStartDateException("End date cannot be before the start date.");
        }
        
        System.out.println("Does this task have subtasks? (y/n): ");
        String hasSubtasks = scanner.nextLine();
        if (hasSubtasks.equalsIgnoreCase("y")) {
            System.out.println("How many subtasks do you want to add? ");
            int no = scanner.nextInt();
            scanner.nextLine(); 

            for (int i = 0; i < no; i++) {
                System.out.print("\nEnter Subtask Title: " +  (i+1));
                String subtaskTitle = scanner.nextLine();
                System.out.print("Enter Subtask Priority (High/Medium/Low): ");
                String subtaskPriority = scanner.nextLine();
                System.out.print("Enter Start Date (yyyy-MM-dd): ");
                start = scanner.nextLine();
                System.out.print("Enter End Date (yyyy-MM-dd): ");
                String end = scanner.nextLine();
                startDate = parseDate(start);
                endDate = parseDate(end);
                SubTask subTask = new SubTask(subtaskTitle, status, subtaskPriority, startDate, endDate);
                task.getSubTasks().add(subTask);
            }
        }
        personalTasks.add(task);
        System.out.println("Task added successfully!");
    }

    public void updateTask() throws TaskNotFoundException{
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Task ID to update: ");
        int taskId = scanner.nextInt();
        PersonalTask taskToUpdate = null;
        for (PersonalTask task : personalTasks) {
            if (task.getTaskId() == taskId) {
                taskToUpdate = task;
                break;
            }
        }
        if (taskToUpdate == null) {
            throw new TaskNotFoundException("No such task with id " + taskId + " found. ");
        }
        // Display update options
        System.out.println("What would you like to update?");
        System.out.println("1. Status");
        System.out.println("2. Priority");
        System.out.println("3. Due Date");
        System.out.println("4. Start Date");
        System.out.println("5. Subtask status update");
        System.out.print("Choose an option (1-4): ");
        int choice = scanner.nextInt();
        scanner.nextLine(); 

        switch (choice) {
            case 1:
                System.out.print("Enter new status (Pending/In Progress/Completed): ");
                String newStatus = scanner.nextLine();
                taskToUpdate.setStatus(newStatus);
                break;
            case 2:
                System.out.print("Enter new priority: ");
                String newPriority = scanner.nextLine();
                taskToUpdate.setPriority(newPriority);
                break;
            case 3:
                System.out.print("Enter new due date (yyyy-MM-dd): ");
                String dueDateStr = scanner.nextLine();
                Date newDueDate = parseDate(dueDateStr);
                if (taskToUpdate instanceof DeadlineTask) {
                    ((DeadlineTask) taskToUpdate).setDueDate(newDueDate);
                } else if (taskToUpdate instanceof RecurringTask) {
                    ((RecurringTask) taskToUpdate).setEndDate(newDueDate);
                }
                break;

            case 4:
                System.out.print("Enter new start date (yyyy-MM-dd): ");
                String start = scanner.nextLine();
                Date newStartDate = parseDate(start);
                taskToUpdate.setStartDate(newStartDate);
                break;

            case 5 :
                System.out.print("Enter the name of the subtask you want to update: ");
                String subtaskName = scanner.nextLine();
                int f = 0;
                for (SubTask subtask : taskToUpdate.getSubTasks()) {
                    if (subtask.getTitle().equalsIgnoreCase(subtaskName)) {
                        f = 1;
                        System.out.println("Enter new status (Pending/In Progress/Completed): ");
                        String newStat = scanner.nextLine();
                        subtask.setStatus(newStat);
                        break;
                    }
                }
                if (f == 0) {
                    System.out.println("Subtask with the name '" + subtaskName + "' not found.");
                }
                break;

            default:
                System.out.println("Invalid option.");
                return;
        }
        System.out.println("Task updated successfully!");
    }
    // method overloading is used here
    public void deleteTask(int taskId) throws TaskNotFoundException {
        PersonalTask taskToDelete = null;
        for (PersonalTask task : personalTasks) {
            if (task.getTaskId() == taskId) {
                taskToDelete = task;
                break;
            }
        }
        if (taskToDelete == null) {
            throw new TaskNotFoundException("No such task with id " + taskId + " found. ");
        }
        personalTasks.remove(taskToDelete);
        System.out.println("Task deleted successfully!");    
    }
    
    public void deleteTask(String taskName) throws TaskNotFoundException {
        PersonalTask taskToDelete = null;
        for (PersonalTask task : personalTasks) {
            if (task.getTitle().equalsIgnoreCase(taskName)) {
                taskToDelete = task;
                break;
            }
        }
        if (taskToDelete == null) {
            throw new TaskNotFoundException("No such task with title " + taskName + " found. ");
        }
        personalTasks.remove(taskToDelete);
        System.out.println("Task deleted successfully!");
    }
    
    public void updateTaskFile() {
        List<String> allLines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(".\\main\\personal_tasks.csv"))) {
            br.readLine();
            String line;
            while ((line = br.readLine())!= null){
                String values[] = line.split(",");
                if (Integer.parseInt(values[0]) != userId) allLines.add(line);
            }
        } catch (IOException e) {
                System.out.println("Unable to read file");
        }
        for (PersonalTask task : personalTasks){
            String endDate, frequency = "-";
            boolean isRecurring = false;
            String startDate = new SimpleDateFormat("yyyy-MM-dd").format(task.getStartDate());
            if (task instanceof DeadlineTask){
                endDate = new SimpleDateFormat("yyyy-MM-dd").format(((DeadlineTask) task).getDueDate());
            }
            else{
                endDate = new SimpleDateFormat("yyyy-MM-dd").format(((RecurringTask) task).getEndDate());
                frequency = ((RecurringTask) task).getFrequency();
                isRecurring = true;
            }
            String line = userId + "," + task.getTaskId() + "," + task.getTitle() + "," + task.getDescription() + "," + task.getStatus() + "," +
                    task.getTag() + "," + task.getPriority() + "," + startDate + "," + endDate + "," + isRecurring + "," + frequency;
            allLines.add(line);

        } 
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(".\\main\\personal_tasks.csv"))) {
            bw.write("UserId,TaskId, Task Title, Description,Status,Tag,Priority,Start Date,Due Date,isRecurring,Frequency\n");
            for (String l : allLines) {
                bw.write(l);
                bw.newLine();
            }
            updateSubtaskFile();

        } catch (IOException e){
            System.out.println("Unable to write file");
        }
    }
    
    // Method to update personal_subtasks.csv 
    private void updateSubtaskFile() {
        List<String> allSubtasks = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(".\\main\\personal_subtasks.csv"))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (Integer.parseInt(values[0]) != userId) {
                    allSubtasks.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Unable to read file: " + e.getMessage());
            return;
        }
        for (PersonalTask task : personalTasks) {
            for (SubTask subtask : task.getSubTasks()) {
                String startDate = new SimpleDateFormat("yyyy-MM-dd").format(subtask.getStartDate());
                String endDate = new SimpleDateFormat("yyyy-MM-dd").format(subtask.getEndDate());

                String subtaskLine = userId + "," + task.getTaskId() + "," + subtask.getTitle() + "," + subtask.getStatus() + "," +
                                     subtask.getPriority() + "," + startDate + "," + endDate;
                allSubtasks.add(subtaskLine);
            }
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(".\\main\\personal_subtasks.csv"))) {
            bw.write("UserId,TaskId,Subtask Title,Status,Priority,StartDate,EndDate\n");
            for (String remainingSubtask : allSubtasks) {
                bw.write(remainingSubtask);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Unable to write to file: " + e.getMessage());
        }
    }
    private List<PersonalTask> sortTasksByPriority() {
        List<PersonalTask> sortedTasks = new ArrayList<>(personalTasks);
        Collections.sort(sortedTasks, new Comparator<PersonalTask>() {
                @Override
                public int compare(PersonalTask t1, PersonalTask t2) {
                    return getPriorityOrder(t1.getPriority()) - getPriorityOrder(t2.getPriority());
                }
        });
        return sortedTasks;

    }

    private int getPriorityOrder(String priority) {
        switch (priority.toLowerCase()) {
            case "high":
                return 1;
            case "medium":
                return 2;
            case "low":
                return 3;
            default:
                return 0;
        }
    }
    private List<PersonalTask> sortTasksByStartDate() {
        List<PersonalTask> sortedTasks = new ArrayList<>(personalTasks);
        Collections.sort(sortedTasks, new Comparator<PersonalTask>() {
                @Override
                public int compare(PersonalTask t1, PersonalTask t2) {
                    Date startDate1 = t1.getStartDate();
                    Date startDate2 = t2.getStartDate();
                    return startDate1.compareTo(startDate2);
                }
        });
        return sortedTasks;
    }

    private List<PersonalTask> sortTasksByDueDate() {
        List<PersonalTask> sortedTasks = new ArrayList<>(personalTasks);
        Collections.sort(sortedTasks, new Comparator<PersonalTask>() {
                @Override
                public int compare(PersonalTask t1, PersonalTask t2) {
                    Date dueDate1, dueDate2;
                    if (t1 instanceof DeadlineTask) dueDate1 = ((DeadlineTask) t1).getDueDate();
                    else dueDate1 = ((RecurringTask) t1).getEndDate();
                    if (t2 instanceof DeadlineTask) dueDate2 = ((DeadlineTask) t2).getDueDate();
                    else  dueDate2 = ((RecurringTask) t2).getEndDate();
                    return dueDate1.compareTo(dueDate2);
                }
        });
        return sortedTasks;
    }

    public void sortTasks() {
        System.out.println("Sort tasks by:");
        System.out.println("1. Priority");
        System.out.println("2. Start Date");
        System.out.println("3. Due Date");
        
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        List <PersonalTask> sortedTasks;
        if (personalTasks.isEmpty()){
            System.out.println("No tasks present to sort.");
            return;
        }
        switch (choice) {
            case 1:
                sortedTasks = sortTasksByPriority();
                break;
            case 2:
                sortedTasks = sortTasksByStartDate();
                break;
            case 3:
                sortedTasks = sortTasksByDueDate();
                break;
            default:
                System.out.println("Invalid choice.");
                return;
        }
       
        System.out.println("Tasks sorted successfully!");
        printAllTasks(sortedTasks);
        
    }

    private List<PersonalTask> filterTasks(String priority, Date dueDate, String tag, String status, boolean isRecurring) {
        return personalTasks.stream()
            .filter(task -> (priority == null || task.getPriority().equalsIgnoreCase(priority)) &&
                            (dueDate == null || 
                                (task instanceof DeadlineTask && ((DeadlineTask) task).getDueDate().before(dueDate)) ||
                                (task instanceof RecurringTask && ((RecurringTask) task).getEndDate().before(dueDate)))&&
                            (tag == null || task.getTag().equalsIgnoreCase(tag)) &&
                            (status == null || task.getStatus().equalsIgnoreCase(status)) &&
                            (!isRecurring || task instanceof RecurringTask))
            .collect(Collectors.toList());
    }



    public void filterTasksMenu() throws InvalidFilterCriteriaException{
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nYou can filter your tasks based on one or more criterias : ");
        System.out.println("Filter tasks : By status/ By Tag/ By Priority/ Within the due date/ Recurring tasks");
        System.out.print("Enter priority to filter (or leave empty to skip): ");
        String priority = scanner.nextLine().trim();
        if (priority.isEmpty()) {
            priority = null; 
        }
        else if (!(priority.equalsIgnoreCase("High") || priority.equalsIgnoreCase("Medium") || priority.equalsIgnoreCase("Low"))){
            throw new InvalidFilterCriteriaException("Invalid filter criteria.");
        }
        
        System.out.print("Enter tag to filter (or leave empty to skip): ");
        String tag = scanner.nextLine().trim();
        if (tag.isEmpty()) {
            tag = null; 
        }

        System.out.print("Enter status to filter (or leave empty to skip): ");
        String status = scanner.nextLine().trim();
        if (status.isEmpty()) {
            status = null; 
        }
         else if (!(status.equalsIgnoreCase("Pending") || status.equalsIgnoreCase("In Progress") || status.equalsIgnoreCase("Completed"))){
            throw new InvalidFilterCriteriaException("Invalid filter criteria.");
        }
        System.out.print("Enter due date to filter (yyyy-MM-dd) (or leave empty to skip): ");
        String dueDateStr = scanner.nextLine().trim();
        Date dueDate = null;
        if (!dueDateStr.isEmpty()) {
            dueDate = parseDate(dueDateStr);
        }
        System.out.print("Filter recurring tasks only? (yes/no): ");
        String recurringInput = scanner.nextLine().trim();
        boolean isRecurring = recurringInput.equalsIgnoreCase("yes");
        List<PersonalTask> filteredTasks = filterTasks(priority, dueDate, tag, status, isRecurring);

        // Print the filtered tasks
        if (filteredTasks.isEmpty()) {
            System.out.println("No tasks found matching the filter criteria.");
        } else {
            System.out.println("Filtered Tasks:");
            printAllTasks(filteredTasks);
        }
    }

    public List<WorkTask> getWorkTasks(){
        List<WorkTask> workTasks = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try(BufferedReader br = new BufferedReader(new FileReader(".\\main\\work_tasks.csv"))){
            String line;
            br.readLine();
            while ((line= br.readLine()) != null){
                String[] fields = line.split(",");
                if (Integer.parseInt(fields[2]) == userId){
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

        } catch (IOException e){
            System.out.println("Error reading csv file.");
        }
        return workTasks;
    }
    /*
    public void exportTasksToCSV(String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("Task ID,Title,Description,Status,Tag,Priority,Start Date,Due Date,Is Recurring,Frequency\n");
            
            // Write task data
            for (PersonalTask task : personalTasks) {
                String frequency; Date end; boolean isRecurring;
                String startDate = new SimpleDateFormat("yyyy-MM-dd").format(task.getStartDate());
                if (task instanceof DeadlineTask){
                    end = ((DeadlineTask) task).getDueDate();
                    isRecurring = false;
                    frequency = "-";
                }
                else{
                    end = ((RecurringTask) task).getEndDate();
                    isRecurring = true;
                    frequency = ((RecurringTask) task).getFrequency();
                }
                String endDate = new SimpleDateFormat("yyyy-MM-dd").format(end);
                writer.write(task.getTaskId() + "," + task.getTitle() + "," + task.getDescription() + "," + task.getStatus() + "," +
                             task.getTag() + "," + task.getPriority() + "," + startDate + "," + endDate + "," + isRecurring + "," + frequency + "\n");
                for (SubTask subtask : task.getSubTasks()){
                    startDate = new SimpleDateFormat("yyyy-MM-dd").format(subtask.getStartDate());
                    endDate = new SimpleDateFormat("yyyy-MM-dd").format(subtask.getEndDate());
                    writer.write("Subtask" + "," + subtask.getTitle() + "," + "," + subtask.getStatus() + "," + 
                                  "," + subtask.getPriority() + "," + startDate + "," + endDate + "\n" );
                }
            }
            System.out.println("Tasks exported successfully to " + filePath);
        } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
        }
    }
    */

    public <T extends Task> void exportToCSV(List<T> tasks, String filePath, String header) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write(header);
            bw.newLine();

            for (T task : tasks) {
                bw.write(task.getTaskInfo());
                bw.newLine();
            }
            System.out.println("Exported data to " + filePath);
        } catch (IOException e) {
            System.out.println("Error writing to file:" + e.getMessage());
        }
    }

    private Date parseDate(String dateStr) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (ParseException e) {
            System.out.println("Invalid date entered! Unable to parse.");
            return null; 
        }
    }

    private List<SubTask> loadSubtasksFromFile(int taskId) {
        String line;
        List<SubTask> subTasks = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(".\\main\\personal_subtasks.csv"))) {
            br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                int userIdFromFile = Integer.parseInt(values[0]);
                if (userIdFromFile == this.userId) {
                    int parentTaskId = Integer.parseInt(values[1]);
                    if (taskId == parentTaskId){
                        String title = values[2], status = values[3], priority = values[4];
                        Date startDate = new SimpleDateFormat("yyyy-MM-dd").parse(values[5]);
                        Date endDate = new SimpleDateFormat("yyyy-MM-dd").parse(values[6]);
                        
                        SubTask subtask = new SubTask(title, status, priority, startDate, endDate);
                        subTasks.add(subtask);
                    }
                }
            }
        } 
        catch (IOException e){
            System.out.println("Unable to read file");
        } catch(ParseException e) {
            System.out.println("Unable to parse values from file !");
        }
        return subTasks;
    }
}