package com.project.young.discoveryserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Value("${eureka.username}")
    private String username;

    @Value("${eureka.password}")
    private String password;

//    @Bean
//    public SecurityWebFilterChain webFilterChainConfig(ServerHttpSecurity serverHttpSecurity) {
//        serverHttpSecurity.csrf()
//                .disable()
//                .authorizeExchange()
//                .anyExchange()
//                .authenticated()
//                .and()
//                .httpBasic();
//
//        return serverHttpSecurity.build();
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf()
                .disable()
                .authorizeRequests().anyRequest()
                .authenticated()
                .and()
                .httpBasic();
        return httpSecurity.build();
    }

    @Bean
    public UserDetailsService inMemoryUser() {
        User.UserBuilder user = User.withDefaultPasswordEncoder();
        UserDetails userDetails = user
                .username(username)
                .password(password)
                .authorities("USER")
                .build();

        return new InMemoryUserDetailsManager(userDetails);
    }
}
