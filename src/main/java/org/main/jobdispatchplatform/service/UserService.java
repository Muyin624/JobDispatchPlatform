package org.main.jobdispatchplatform.service;

import org.main.jobdispatchplatform.entity.User;
import org.main.jobdispatchplatform.mapper.UserMapper;
import org.main.jobdispatchplatform.security.JwtTokenProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserMapper userMapper, JwtTokenProvider jwtTokenProvider) {
        this.userMapper = userMapper;
        this.jwtTokenProvider = jwtTokenProvider;
    }

//    注册
    public void register(User user) {
        if (userMapper.findByPhone(user.getPhone()) != null){
            throw new RuntimeException("手机号已注册");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }
        userMapper.insert(user);
    }

//    登录
    public String login(String username, String password) {
        User user = userMapper.findByPhone(username);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("手机号或者密码错误");
        }
        return jwtTokenProvider.generateToken(user.getId(),user.getPhone(),user.getRole());
    }
}
