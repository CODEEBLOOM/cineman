package com.codebloom.cineman.model;

import com.codebloom.cineman.common.enums.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserPrincipal implements UserDetails {

    private final UserEntity user;

    public UserPrincipal(UserEntity user) {
        this.user = user;
    }
    
    public Long getUserId() {
        return this.user.getUserId();
    }

    public UserEntity getUser() {
        return this.user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        this.user.getUserRoles().forEach(authority -> {
            authorities.add(new SimpleGrantedAuthority(String.format("ROLE_%s",authority.getRole().getRoleId())));
        });
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.user.getPassword();
    }

    @Override
    public String getUsername() {
        return this.user.getEmail();
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
        return UserStatus.ACTIVE.equals(this.user.getStatus());
    }
}
