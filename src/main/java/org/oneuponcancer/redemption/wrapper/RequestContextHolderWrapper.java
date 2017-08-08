package org.oneuponcancer.redemption.wrapper;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@Component
public class RequestContextHolderWrapper {
    public RequestAttributes getRequestAttributes() {
        return RequestContextHolder.getRequestAttributes();
    }
}
