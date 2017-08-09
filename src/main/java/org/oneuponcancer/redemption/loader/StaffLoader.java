package org.oneuponcancer.redemption.loader;

import org.oneuponcancer.redemption.model.Staff;
import org.oneuponcancer.redemption.repository.StaffRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Component
public class StaffLoader {
    static final String DEFAULT_USER = "admin";
    static final String DEFAULT_PASS = "admin";

    private static final Logger LOGGER = LoggerFactory.getLogger(StaffLoader.class);

    private StaffRepository staffRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private boolean isSecure = true;

    @Inject
    public StaffLoader(StaffRepository staffRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.staffRepository = staffRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @PostConstruct
    public void load() {
        if (staffRepository.count() == 0) {
            Staff admin = new Staff();

            admin.setUsername(DEFAULT_USER);
            admin.setPassword(bCryptPasswordEncoder.encode(DEFAULT_PASS));

            staffRepository.save(admin);

            isSecure = false;

            LOGGER.warn("No staff accounts found in database. Populated the default account.");
            LOGGER.warn("Please be sure to create new accounts before using Redemption in a production setting!");
        } else {
            Staff admin = staffRepository.findByUsername(DEFAULT_USER);

            if (bCryptPasswordEncoder.matches(DEFAULT_PASS, admin.getPassword())) {
                isSecure = false;

                LOGGER.warn("The default Staff account exists in the database!");
                LOGGER.warn("Please be sure to delete it or change the password before using Redemption in a production setting!");
            }
        }
    }

    public boolean isSecure() {
        return isSecure;
    }
}
