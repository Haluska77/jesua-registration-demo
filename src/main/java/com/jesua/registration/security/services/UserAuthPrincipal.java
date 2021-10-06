package com.jesua.registration.security.services;

import com.jesua.registration.entity.Project;
import com.jesua.registration.entity.User;
import com.jesua.registration.oauth.AuthProvider;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UserAuthPrincipal implements UserDetails {

    private final User user;

    public UserAuthPrincipal(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(user.getRole());
        Set<SimpleGrantedAuthority> permission = new HashSet<>();
        permission.add(simpleGrantedAuthority);

        return permission;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
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
        return user.getActive();
    }

    public String getAvatar() {
        return user.getAvatar();
    }

    public String getName() {
        return user.getUserName();
    }

    public String getRole() {
        return user.getRole();
    }

    public UUID getId() {
        return user.getId();
    }

    public Instant getCreated() {
        return user.getCreated();
    }

    public Set<Project> getProjects() {
        return user.getProjects();
    }

    public AuthProvider getAuthProvider() {
        return user.getAuthProvider();
    }
}
