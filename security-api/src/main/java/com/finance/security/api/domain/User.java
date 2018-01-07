package com.finance.security.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "EXPENSES_USER")
public class User implements Serializable, UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @JsonIgnore
    @Version
    @Column(name = "LAST_MODIFICATION")
    private Timestamp lastModification;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    //@NotEmpty
    @Column(name = "LOGIN")
    private String login;

//    @NotEmpty()
//    @Email
    @Column(name = "EMAIL")
    private String email;

    @Column(name = "ROLE")
    private String role;

    //@NotEmpty
    @Column(name = "PASSWORD")
    private String password;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getLastModification() {
        return lastModification;
    }

    public void setLastModification(Timestamp lastModification) {
        this.lastModification = lastModification;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<GrantedAuthority>(){{
            add(new SimpleGrantedAuthority(role.toUpperCase()));
        }};
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void update(User user) {
        if (!StringUtils.isEmpty(user.getFirstName())) {
            this.firstName = user.getFirstName();
        }
        if (!StringUtils.isEmpty(user.getLastName())) {
            this.lastName = user.getLastName();
        }
        if (!StringUtils.isEmpty(user.getLogin())) {
            this.login = user.getLogin();
        }
        if (!StringUtils.isEmpty(user.getEmail())) {
            this.email = user.getEmail();
        }
        if (!StringUtils.isEmpty(user.getRole())) {
            this.role = user.getRole();
        }
        if (!StringUtils.isEmpty(user.getPassword())) {
            this.password = user.getPassword();
        }
    }
}
