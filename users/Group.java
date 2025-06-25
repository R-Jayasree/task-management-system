package users;
import java.util.ArrayList;
import java.util.List;
import tasks.*;

public class Group {
    private String groupId;
    private String groupName;
    private int adminUserId;
    private List<WorkTask> workTasks; 
    private List<Integer> members;

    public Group(String groupId, String groupName, int adminUserId) {
        this.groupId = groupId;
        this.adminUserId = adminUserId;
        this.workTasks = new ArrayList<>();
        this.members = new ArrayList<>();
    }
    public String getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public int getAdminUserId() {
        return adminUserId;
    }

    public List<WorkTask> getWorkTasks() {
        return workTasks;
    }

    public List<Integer> getMembers() {
        return members;
    }

    // Setters
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setAdminUserId(int adminUserId) {
        this.adminUserId = adminUserId;
    }

    public void setWorkTasks(List<WorkTask> workTasks) {
        this.workTasks = workTasks;
    }

    public void setMembers(List<Integer> members) {
        this.members = members;
    }

}

