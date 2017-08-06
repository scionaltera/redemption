package org.oneuponcancer.redemption.resource;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.inject.Inject;
import java.security.Principal;

@Controller
public class IndexResource {
    private String applicationVersion;

    @Inject
    public IndexResource(String applicationVersion) {
        this.applicationVersion = applicationVersion;
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
    public String dashboard(Model model) {
        model.addAttribute("version", applicationVersion);

        return "dashboard";
    }
}
