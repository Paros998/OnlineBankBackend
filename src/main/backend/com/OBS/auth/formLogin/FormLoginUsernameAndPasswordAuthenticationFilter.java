package com.OBS.auth.formLogin;

import com.OBS.service.AppUserService;
import com.OBS.service.ClientService;
import com.OBS.service.EmployeeService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Date;

@AllArgsConstructor
@Getter
public class FormLoginUsernameAndPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final AppUserService appUserService;
    private final RelationshipSearcher relationshipSearcher;
    private final boolean postOnly = true;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (this.postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        } else {
            String username = this.obtainUsername(request);
            username = username != null ? username : "";
            username = username.trim();
            String password = this.obtainPassword(request);
            password = password != null ? password : "";
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
            this.setDetails(request, authRequest);
            return this.getAuthenticationManager().authenticate(authRequest);
        }
    }
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        String key = "someStringHashToHaveReallyGoodSecurityOverHereSoNoOneWithAmateurSkillsWouldn'tHackThis";
        String username = this.obtainUsername(request);

        Long appUserId = appUserService.getUser(username).getUserId();
        Long userID = relationshipSearcher.searchIdByAppUserId(appUserId);

        if(userID == null)
            throw new IllegalStateException("User not assigned to any clients or employees!");

        String token = Jwts.builder()
                .setSubject(authResult.getName())
                .claim("authorities", authResult.getAuthorities())
                .claim("userId",userID)
                .setIssuedAt(new Date())
                .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(1)))
                .signWith(Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8)))
                .compact();

        response.addHeader("Authorization","Bearer " + token);

    }
}
