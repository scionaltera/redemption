package org.oneuponcancer.redemption.config;

import org.oneuponcancer.redemption.Redemption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
    @Value("${service.audit.request.max:1000}")
    private int auditServiceRequestMax;

    @Bean(name = "applicationVersion")
    public String getApplicationVersion() {
        return Redemption.class.getPackage().getImplementationVersion();
    }

    @Bean(name = "auditServiceRequestMax")
    public Integer getAuditServiceRequestMax() {
        return auditServiceRequestMax;
    }
}
