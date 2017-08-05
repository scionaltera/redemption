package org.oneuponcancer.redemption.resource;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class IndexResource {
    @RequestMapping("/")
    public String index(Principal principal) {
        if (principal != null) {
            return "redirect:/dashboard";
        }

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

        return "index";
    }

    @RequestMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
}
