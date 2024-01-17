package com.project.chat.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final JwtUtils jwtUtils;

    public UserDetailsServiceImpl(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        String u = jwtUtils.getUserNameFromJwtToken(username);
        List<String> roles = jwtUtils.getRolesFromJwtToken(username);
        System.out.println("In loadUserByUsername: " + roles + " " + u);
        return UserDetailsImpl.buildFromJwtClaims(u, roles);
    }
}