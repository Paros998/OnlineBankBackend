package com.OBS.security;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableEncryptableProperties
public class ApplicationSecurityConfig {

    public ApplicationSecurityConfig() {
    }
}
