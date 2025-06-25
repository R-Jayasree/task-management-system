package tasks;
import java.text.SimpleDateFormat;
import java.util.*;
public class DeadlineTask extends PersonalTask {
    private Date dueDate; 
    public DeadlineTask(int taskId,String title, String description, String status, String priority, String tag, Date startDate, Date dueDate) {
        super(taskId, title, description, status, priority, tag, startDate);
        this.dueDate = dueDate;
    }
    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }
    @Override
    public String getTaskInfo(){
        return taskId + "," + title + "," + description + "," + status + "," + tag + "," + priority + 
                ","  + new SimpleDateFormat("yyyy-MM-dd").format(startDate) + "," +
                new SimpleDateFormat("yyyy-MM-dd").format(dueDate) + "," + "No" + "," +  "-";
    }   

}