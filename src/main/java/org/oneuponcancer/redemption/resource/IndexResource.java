package org.oneuponcancer.redemption.resource;


import org.oneuponcancer.redemption.exception.InsufficientPermissionException;
import org.oneuponcancer.redemption.loader.StaffLoader;
import org.oneuponcancer.redemption.model.Permission;
import org.oneuponcancer.redemption.model.Staff;
import org.oneuponcancer.redemption.repository.StaffRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;

@Controller
public class IndexResource {
    private String applicationVersion;
    private StaffLoader staffLoader;
    private StaffRepository staffRepository;

    @Inject
    public IndexResource(String applicationVersion, StaffLoader staffLoader, StaffRepository staffRepository) {
        this.applicationVersion = applicationVersion;
        this.staffLoader = staffLoader;
        this.staffRepository = staffRepository;
    }

    @RequestMapping("/")
    public String index(Principal principal, Model model) {
        if (principal != null) {
            return "redirect:/dashboard";
        }

        model.addAttribute("version", applicationVersion);

        return "index";
    }

    @RequestMapping("/login")
    public String login(@RequestParam(required = false) String logout,
                        @RequestParam(required = false) String error,
                        Model model) {

        if (logout != null) {
            model.addAttribute("message", "You have been logged out.");
        }

        if (error != null) {
            model.addAttribute("message", "Invalid credentials.");
        }

        model.addAttribute("version", applicationVersion);

        return "index";
    }

    @RequestMapping("/dashboard")
    public String dashboard(Principal principal, Model model) {
        model.addAttribute("version", applicationVersion);
        model.addAttribute("secure", staffLoader.isSecure());

        Staff staff = staffRepository.findByUsername(principal.getName());

        if (staff != null) {
            model.addAttribute("staff", staff);
        }

        return "dashboard";
    }

    @RequestMapping("/staff/{id}")
    public String editStaff(Principal principal, Model model, @PathVariable String id) {
        if (((UsernamePasswordAuthenticationToken)principal).getAuthorities().stream().noneMatch(a -> a.getAuthority().equals(Permission.EDIT_STAFF.name()))) {
            throw new InsufficientPermissionException("Not allowed to edit staff accounts.");
        }

        Staff staff = staffRepository.findOne(id);

        if (staff == null) {
            throw new IllegalArgumentException("No staff member with provided ID");
        }

        model.addAttribute("permissions", Permission.values());
        model.addAttribute("staff", staff);

        return "staffedit";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public void handleIllegalArgumentException(IllegalArgumentException ex, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }
}
