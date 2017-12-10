package org.oneuponcancer.redemption.model.transport;

import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

public class StaffEditRequest {
    @Pattern(regexp = "[a-z]{3,255}", message = "Usernames must only be lower case letters and at least 3 letters long.")
    private String username;

    @Pattern(regexp = "^$|.{6,}", message = "Passwords must be at least 6 characters long, or empty if you do not wish to change it.")
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
