package org.oneuponcancer.redemption.config;

import org.oneuponcancer.redemption.Redemption;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
    @Bean(name = "applicationVersion")
    public String getApplicationVersion() {
        return Redemption.class.getPackage().getImplementationVersion();
    }
}
