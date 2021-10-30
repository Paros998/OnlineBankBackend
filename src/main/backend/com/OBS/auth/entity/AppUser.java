package com.OBS.auth.entity;

import com.OBS.auth.AppUserRole;
import com.OBS.entity.Employee;
import com.OBS.requestBodies.UserCredentials;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Entity
@Table(name = "users")
public class AppUser implements UserDetails {
    @Id
    @GeneratedValue(
            strategy = GenerationType.AUTO
    )
    @Column(
            nullable = false,
            updatable = false
    )
    private Long userId;
    private String username;
    private String password;
    private String email;
    @Enumerated(EnumType.STRING)
    private AppUserRole appUserRole;
    private Boolean locked;
    private Boolean enabled;

    @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL, mappedBy = "user")
    private Employee employee;

    public AppUser(UserCredentials userCredentials,
                   Boolean locked,
                   Boolean enabled) {
        this.username = userCredentials.getUsername();
        this.password = userCredentials.getPassword();
        this.email = userCredentials.getEmail();
        this.appUserRole = userCredentials.getAppUserRole();
        this.locked = locked;
        this.enabled = enabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(appUserRole.name());
        return Collections.singletonList(authority);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
