package org.oneuponcancer.redemption.resource;


import org.oneuponcancer.redemption.exception.InsufficientPermissionException;
import org.oneuponcancer.redemption.loader.StaffLoader;
import org.oneuponcancer.redemption.model.Asset;
import org.oneuponcancer.redemption.model.Permission;
import org.oneuponcancer.redemption.model.Staff;
import org.oneuponcancer.redemption.repository.AssetRepository;
import org.oneuponcancer.redemption.repository.StaffRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class IndexResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexResource.class);

    private String applicationVersion;
    private StaffLoader staffLoader;
    private StaffRepository staffRepository;
    private AssetRepository assetRepository;

    @Inject
    public IndexResource(String applicationVersion,
                         StaffLoader staffLoader,
                         StaffRepository staffRepository,
                         AssetRepository assetRepository) {
        this.applicationVersion = applicationVersion;
        this.staffLoader = staffLoader;
        this.staffRepository = staffRepository;
        this.assetRepository = assetRepository;
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

        model.addAttribute("staff", staff);

        if (staff != null) {
            List<String> activePermissions = new ArrayList<>();

            Arrays.stream(Permission.values()).forEach(p -> {
                if (((UsernamePasswordAuthenticationToken)principal).getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(p.name()))) {

                    model.addAttribute(p.getUnique(), true);
                    activePermissions.add(p.getUnique());
                }
            });

            model.addAttribute("permissions", activePermissions);
        }

        return "dashboard";
    }

    @RequestMapping("/staff")
    public String createStaff(Principal principal, Model model) {
        if (((UsernamePasswordAuthenticationToken)principal).getAuthorities().stream().noneMatch(a -> a.getAuthority().equals(Permission.CREATE_STAFF.name()))) {
            throw new InsufficientPermissionException("Not allowed to create staff accounts.");
        }

        model.addAttribute("version", applicationVersion);
        model.addAttribute("permissions", Permission.values());

        return "staffcreate";
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

        model.addAttribute("version", applicationVersion);
        model.addAttribute("permissions", Permission.values());
        model.addAttribute("staff", staff);

        return "staffedit";
    }

    @RequestMapping("/asset")
    public String createAsset(Principal principal, Model model) {
        if (((UsernamePasswordAuthenticationToken)principal).getAuthorities().stream().noneMatch(a -> a.getAuthority().equals(Permission.CREATE_ASSET.name()))) {
            throw new InsufficientPermissionException("Not allowed to create assets.");
        }

        model.addAttribute("version", applicationVersion);
        model.addAttribute("permissions", Permission.values());

        return "assetcreate";
    }

    @RequestMapping("/asset/{id}")
    public String editAsset(Principal principal, Model model, @PathVariable String id) {
        if (((UsernamePasswordAuthenticationToken)principal).getAuthorities().stream().noneMatch(a -> a.getAuthority().equals(Permission.EDIT_ASSET.name()))) {
            throw new InsufficientPermissionException("Not allowed to list assets.");
        }

        Asset asset = assetRepository.findOne(id);

        if (asset == null) {
            throw new IllegalArgumentException("No asset with provided ID");
        }

        model.addAttribute("version", applicationVersion);
        model.addAttribute("permissions", Permission.values());
        model.addAttribute("asset", asset);

        return "assetedit";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public void handleIllegalArgumentException(IllegalArgumentException ex, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }
}
