package com.finance.expensesservice;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finance.expensesservice.util.security.OAuth2Util;
import oracle.jdbc.pool.OracleDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import static com.finance.expensesservice.util.ExpensesServiceConstants.DateFormat.OUTPUT_DATE_FORMAT;

@Configuration
public class ExpensesServerConfiguration {

    @Value("${expenses.db.url}")
    private  String url;
    @Value("${expenses.db.user}")
    private String user;
    @Value("${expenses.db.password}")
    private String password;

    @Bean
    public DataSource dataSource() throws SQLException {
        OracleDataSource dataSource = new OracleDataSource();
        dataSource.setUser(user);
        dataSource.setPassword(password);
        dataSource.setURL(url);
        return dataSource;
    }

    @Bean
    public OAuth2Util oAuth2Util() {
        return new OAuth2Util();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat(OUTPUT_DATE_FORMAT));
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
    }
}
