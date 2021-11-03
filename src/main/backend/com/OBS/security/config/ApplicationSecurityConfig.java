package com.OBS.security.config;

import com.OBS.service.AppUserService;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static com.OBS.auth.AppUserRole.ADMIN;
import static com.OBS.auth.AppUserRole.EMPLOYEE;

@Configuration
@EnableEncryptableProperties
@AllArgsConstructor
@EnableWebSecurity
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

    private final AppUserService appUserService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().httpBasic()
                .and()
                .csrf()
                .and()
                .authorizeRequests()
                .antMatchers("/index", "/css/*", "/js/*").permitAll()
                .antMatchers("/employees/**").hasAnyRole(ADMIN.name(), EMPLOYEE.name())
                .antMatchers("/clients/**").hasAnyRole(ADMIN.name(), EMPLOYEE.name())
                .antMatchers("/users/**").hasRole(ADMIN.name())
                .antMatchers("/announcements/**").hasRole(ADMIN.name())
                .antMatchers("/credit-cards/**").hasAnyRole(ADMIN.name(), EMPLOYEE.name())
                .antMatchers("/clients/**").hasAnyRole(ADMIN.name(), EMPLOYEE.name())
                .anyRequest()
                .authenticated()
                .and()
                .formLogin();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(bCryptPasswordEncoder);
        provider.setUserDetailsService(appUserService);
        return provider;
    }

}