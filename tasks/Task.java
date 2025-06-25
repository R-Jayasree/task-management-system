package tasks;
public abstract class Task {
    protected int taskId;
    protected String title;
    protected String description;
    protected String status;
    protected String priority;
    protected String tag; 
    // Constructor
    public Task(int taskId, String title, String description, String status, String priority, String tag) {
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.tag = tag;
    }
    public int getTaskId() {
        return taskId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getPriority() {
        return priority;
    }

    public String getTag() {
        return tag;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
    public abstract String getTaskInfo();
}
