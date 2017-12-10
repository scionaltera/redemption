package org.oneuponcancer.redemption.service;

import org.oneuponcancer.redemption.model.Permission;
import org.oneuponcancer.redemption.model.Staff;
import org.oneuponcancer.redemption.repository.StaffRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class StaffDetailsService implements UserDetailsService {
    private StaffRepository staffRepository;

    @Inject
    public StaffDetailsService(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Staff staff = staffRepository.findByUsername(username);

        if (staff == null) {
            throw new UsernameNotFoundException("Username not found.");
        }

        if (!staff.hasPermission(Permission.LOGIN)) {
            throw new UsernameNotFoundException("User is not allowed to log in.");
        }

        List<GrantedAuthority> roles = staff.getPermissions().stream().map(p -> new SimpleGrantedAuthority(p.name())).collect(Collectors.toList());

        return new User(username, staff.getPassword(), roles);
    }
}
