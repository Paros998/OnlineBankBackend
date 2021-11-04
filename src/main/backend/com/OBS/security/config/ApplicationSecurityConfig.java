package com.OBS.security.config;

import com.OBS.service.AppUserService;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import lombok.AllArgsConstructor;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static com.OBS.auth.AppUserRole.ADMIN;
import static com.OBS.auth.AppUserRole.EMPLOYEE;
import static com.OBS.auth.AppUserRole.CLIENT;

@Configuration
@EnableEncryptableProperties
@AllArgsConstructor
@EnableWebSecurity

@EnableWebMvc
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

    private final AppUserService appUserService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http    .httpBasic()
                .and()
                .cors()
                .and()
                .csrf().disable()

                .authorizeRequests()

                .antMatchers("/index", "/css/*", "/js/*","/swagger-ui.html").permitAll()

                .antMatchers(HttpMethod.PATCH,"/users/**").anonymous()
                .antMatchers(HttpMethod.POST,"/visits/**").anonymous()
                .antMatchers(HttpMethod.GET,
                        "/dictionary/clients/",
                        "/dictionary/credit-cards",
                        "/dictionary/orders",
                        "/dictionary/visits").hasAnyRole(ADMIN.name(),EMPLOYEE.name())
                .antMatchers(HttpMethod.GET,"/dictionary/**").hasRole(ADMIN.name())
                .antMatchers("/employees/**").hasAnyRole(ADMIN.name(), EMPLOYEE.name())
                .antMatchers("/clients/**").hasAnyRole(ADMIN.name(), EMPLOYEE.name())
                .antMatchers("/users/**").hasRole(ADMIN.name())
                .antMatchers("/announcements/**").hasRole(ADMIN.name())
                .antMatchers("/credit-cards/**").hasAnyRole(ADMIN.name(), EMPLOYEE.name())
                .antMatchers("/clients/**").hasAnyRole(ADMIN.name(), EMPLOYEE.name())
                .and()
                .formLogin()
                .permitAll()
                .and()
                .logout()
                .permitAll();
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
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("entire-public")
                .pathsToMatch("/**")
                .build();
    }
    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Online Bank API")
                        .description("Online bank sample application")
                        .version("v0.2")
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("Online Bank Wiki Documentation")
                        .url("https://github.com/Paros998/OnlineBankBackend"));
    }


}
