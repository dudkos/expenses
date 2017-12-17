package com.finance.expensesservice.util.security;

import com.finance.expensesservice.exception.ExpensesServiceException;
import com.finance.expensesservice.util.ExpensesServiceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.util.Map;

////TODO common module
public class OAuth2Util implements ExpensesServiceConstants {

    private final static Logger logger  = LoggerFactory.getLogger(OAuth2Util.class);

    public Integer getUserIdFromAuth() throws ExpensesServiceException {
        String userId = retrieveUserIdFromUserAuthenticationDetails(getUserAuthenticationDetails());

        if (userId == null) {
           logger.error("user id is null");
           throw new ExpensesServiceException(HttpStatus.UNAUTHORIZED.value(), "authentication error");
        }

        return Integer.valueOf(userId);
    }

    public String getUserNameFromAuth() {
        return getOAuth2AuthenticationFromContext().getName();
    }

    private String retrieveUserIdFromUserAuthenticationDetails(Map details) throws ExpensesServiceException {
        if(details != null && !details.isEmpty()) {
            Map principal = (Map) details.get(MapFields.PRINCIPAL);
            return principal == null || principal.isEmpty() ? null : principal.get(MapFields.ID).toString();
        }

        logger.error("details is null or empty");

        throw new ExpensesServiceException(HttpStatus.UNAUTHORIZED.value(), "authentication error");
    }

    private Map getUserAuthenticationDetails() throws ExpensesServiceException {
        try {
            return (Map) getOAuth2AuthenticationFromContext().getUserAuthentication().getDetails();
        } catch (Exception e) {
            logger.error("error getting user auth details " + e);
            throw new ExpensesServiceException(HttpStatus.UNAUTHORIZED.value(), "authentication error");
        }
    }

    private OAuth2Authentication getOAuth2AuthenticationFromContext() {
        return (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
    }
}
