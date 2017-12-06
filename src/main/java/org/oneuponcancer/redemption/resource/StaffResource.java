package org.oneuponcancer.redemption.resource;

import org.oneuponcancer.redemption.exception.InsufficientPermissionException;
import org.oneuponcancer.redemption.model.Permission;
import org.oneuponcancer.redemption.model.Staff;
import org.oneuponcancer.redemption.repository.StaffRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/api/v1/staff")
public class StaffResource {
    private StaffRepository staffRepository;

    @Inject
    public StaffResource(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    @RequestMapping("")
    @ResponseBody
    public List<Staff> fetchStaff(Principal principal) {
        if (((UsernamePasswordAuthenticationToken)principal).getAuthorities().stream().noneMatch(a -> a.getAuthority().equals(Permission.LIST_STAFF.name()))) {
            throw new InsufficientPermissionException("Not allowed to list staff accounts.");
        }

        List<Staff> results = staffRepository.findAll();

        // mask the password hashes
        results.forEach(staff -> staff.setPassword("********"));

        return results;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public String updateStaff(@PathParam("id") String id, Principal principal, Model model) {
        if (((UsernamePasswordAuthenticationToken)principal).getAuthorities().stream().noneMatch(a -> a.getAuthority().equals(Permission.EDIT_STAFF.name()))) {
            throw new InsufficientPermissionException("Not allowed to edit staff accounts.");
        }

        // TODO make changes to Staff object

        // TODO add required stuff to model

        return "redirect:/dashboard";
    }

    @ExceptionHandler(InsufficientPermissionException.class)
    public void handleInsufficientPermissionException(InsufficientPermissionException ex, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.FORBIDDEN.value(), ex.getMessage());
    }
}
