package tasks;
import java.util.*;
public abstract class PersonalTask extends Task {
    protected List<SubTask> subTasks; // Subtasks
    protected Date startDate;
    
    public PersonalTask(int taskId,String title, String description, String status, String priority, String tag, Date startDate) {
        super(taskId, title, description, status, priority, tag);
        this.startDate = startDate;
        this.subTasks = new ArrayList<>();
    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }
    public void setSubTasks(List<SubTask> subTasks) {
        this.subTasks = subTasks;
    }
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    @Override
    public abstract String getTaskInfo();
}
