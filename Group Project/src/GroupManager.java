// Class to manage group ownership and members

import java.util.ArrayList;
import java.util.List;

class GroupManager {
    private String groupName;
    private String owner; // Username of the owner
    private List<String> users; // List of usernames with access

    public GroupManager(String groupName, String owner) {
        this.groupName = groupName;
        this.owner = owner;
        this.users = new ArrayList<>();
    }

    public String getGroupName() {
        return groupName;
    }

    public String getOwner() {
        return owner;
    }

    public List<String> getUsers() {
        return users;
    }

    public void addUser(String username) {
        if (!users.contains(username)) {
            users.add(username);
        }
    }

    public void removeUser(String username) {
        users.remove(username);
    }
    
    
}

