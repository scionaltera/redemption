package org.oneuponcancer.redemption.model.transport;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public class StaffCreateRequest {
    @Pattern(regexp = "[a-z]{3,255}", message = "Usernames must only be lower case letters and at least 3 letters long.")
    private String username;

    @Size(min = 6, message = "Passwords must be at least 6 characters long.")
    private String password;

    private List<String> permissions = new ArrayList<>();

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
}
