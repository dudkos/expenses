package com.finance.security.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

import static com.finance.security.api.config.Constants.AUTHENTICATION_MANAGER_NAME;

@Configuration
@EnableAuthorizationServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class OAuth2ServerConfig extends AuthorizationServerConfigurerAdapter {

    private final TokenStore tokenStore;

    private final UserDetailsService userDetailsService;

    private AuthenticationManager authenticationManager;

    @Value("${expenses.service.password}")
    private String expensesServicePassword;

    @Value("${gui.password}")
    private String guiPassword;

    @Autowired
    public OAuth2ServerConfig(TokenStore tokenStore,
                              UserDetailsService userDetailsService,
                              @Qualifier(AUTHENTICATION_MANAGER_NAME) AuthenticationManager authenticationManager) {

        this.tokenStore = tokenStore;
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("gui")
                .secret(guiPassword)
                .authorizedGrantTypes("refresh_token", "password")
                .scopes("gui")
             .and()
                .withClient("expenses-service")
                .secret(expensesServicePassword)
                .authorizedGrantTypes("client_credentials", "refresh_token")
                .scopes("server");
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .tokenStore(tokenStore)
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()");
    }
}
