package org.oneuponcancer.redemption.resource;


import org.oneuponcancer.redemption.loader.StaffLoader;
import org.oneuponcancer.redemption.model.Staff;
import org.oneuponcancer.redemption.repository.StaffRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.inject.Inject;
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
}
