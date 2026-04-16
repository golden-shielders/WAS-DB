package com.golden_shielders.server.service;

import com.golden_shielders.server.config.JwtUtil;
import com.golden_shielders.server.entity.WebSiteUser;
import com.golden_shielders.server.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    @Transactional
    public String checkLogin(String userName, String password) {
        try {
            WebSiteUser user = userRepository.findUserByUserName(userName);

            if (user.getPw().equals(password)) {
                return jwtUtil.generateToken(user.getUserName(), user.getRole());
            }
            return null;
        } catch (Exception e) {
            System.out.println("Exception : " + e);
            return null;
        }
    }
}
