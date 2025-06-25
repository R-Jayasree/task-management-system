package tasks;
import java.text.SimpleDateFormat;
import java.util.*;
public class WorkTask extends Task {
    private Date startDate, dueDate;
    private int assignedUser; 

    // Constructor
    public WorkTask(int taskId, String title, String description, String status, String tag, 
                    String priority, Date startDate, Date dueDate, int assignedUser) {
        super(taskId, title, description, status, tag, priority);
        this.startDate = startDate;
        this.dueDate = dueDate; 
        this.assignedUser = assignedUser;
    }

    public int getAssignedUser() {
        return assignedUser;
    }
    public void setAssignedUser(int assignedUser) {
        this.assignedUser = assignedUser;
    }
    public Date getDueDate() {
        return dueDate;
    }
    public Date getStartDate(){
        return startDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }
    @Override
    public String getTaskInfo(){
        return taskId + "," + title + "," + description + "," + status + "," + tag + "," + priority + 
                ","  + new SimpleDateFormat("yyyy-MM-dd").format(startDate) + "," +
                new SimpleDateFormat("yyyy-MM-dd").format(dueDate);
    }  
}

