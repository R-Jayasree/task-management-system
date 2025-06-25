package tasks;
import java.text.SimpleDateFormat;
import java.util.*;

public class RecurringTask extends PersonalTask {
    private Date endDate;     // End date of the task
    private String frequency;  // Frequency of the recurrence (e.g., "daily", "weekly", "monthly")

    public RecurringTask(int taskId, String title, String description, String status, String priority, String tag, Date startDate, Date endDate, String frequency) {
        super(taskId, title, description, status, priority, tag, startDate);
        this.endDate = endDate;
        this.frequency = frequency;
    }
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }
    @Override
    public String getTaskInfo(){
        return taskId + "," + title + "," + description + "," + status + "," + tag + "," + priority + 
                ","  + new SimpleDateFormat("yyyy-MM-dd").format(startDate) + "," +
                new SimpleDateFormat("yyyy-MM-dd").format(endDate) + "," +  "Yes" + "," + frequency;
    }  

}