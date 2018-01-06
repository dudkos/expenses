package com.finance.common.context;

import com.finance.common.exception.ServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.util.StringUtils;

import java.util.Map;

public class OAuth2UserContextImpl implements UserContext {

    private static final String PRINCIPAL = "principal";

    private static final String ID = "id";

    public Integer getUserId() throws ServiceException {
        String userId = retrieveUserIdFromUserAuthenticationDetails(getUserAuthenticationDetails());

        if (userId == null) {
           throw new ServiceException(HttpStatus.UNAUTHORIZED.value(), "Authentication error. User ID is null");
        }

        return Integer.valueOf(userId);
    }

    public String getUserName() {
        String userName = getOAuth2AuthenticationFromContext().getName();

        if(StringUtils.isEmpty(userName)) {
            throw new ServiceException(HttpStatus.UNAUTHORIZED.value(), "Authentication error. User NAME is null or empty");
        }

        return getOAuth2AuthenticationFromContext().getName();
    }

    private String retrieveUserIdFromUserAuthenticationDetails(Map details) throws ServiceException {
        if(details != null && !details.isEmpty()) {
            Map principal = (Map) details.get(PRINCIPAL);
            return principal == null || principal.isEmpty() ? null : principal.get(ID).toString();
        }

        throw new ServiceException(HttpStatus.UNAUTHORIZED.value(), "Authentication error. User details is null or empty.");
    }

    private Map getUserAuthenticationDetails() throws ServiceException {
        try {
            return (Map) getOAuth2AuthenticationFromContext().getUserAuthentication().getDetails();
        } catch (Exception e) {
            throw new ServiceException(HttpStatus.UNAUTHORIZED.value(), "Authentication error." + e.getMessage());
        }
    }

    private OAuth2Authentication getOAuth2AuthenticationFromContext() {
        return (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
    }
}
