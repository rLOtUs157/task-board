package com.example.taskboard.controller;

import com.example.taskboard.common.JwtUtil;
import com.example.taskboard.common.Result;
import com.example.taskboard.entity.User;
import com.example.taskboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 认证接口：注册 + 登录（登录成功签发JWT并存入Redis）
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private StringRedisTemplate redisTemplate;

    // 注册
    @PostMapping("/register")
    public Result<?> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        if (username == null || password == null || username.isBlank() || password.isBlank()) {
            return Result.error("用户名和密码不能为空");
        }
        if (userRepository.findByUsername(username).isPresent()) {
            return Result.error("用户名已存在");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(password); // 简化演示，生产环境应加密存储
        user.setNickname(body.getOrDefault("nickname", username));
        user.setCreateTime(LocalDateTime.now());
        userRepository.save(user);
        return Result.success("注册成功");
    }

    // 登录：校验密码，签发JWT并存入Redis
    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null || !user.getPassword().equals(password)) {
            return Result.error("用户名或密码错误");
        }
        String token = jwtUtil.generateToken(user.getId(), username);
        // 登录态存入Redis，24小时过期（体现Redis用法）
        redisTemplate.opsForValue().set("token:" + token, username, 24, TimeUnit.HOURS);

        Map<String, String> data = new HashMap<>();
        data.put("token", token);
        data.put("username", username);
        data.put("nickname", user.getNickname() == null ? username : user.getNickname());
        return Result.success(data);
    }

    // 退出登录：删除Redis中的token
    @PostMapping("/logout")
    public Result<?> logout(@RequestHeader(value = "Authorization", required = false) String auth) {
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            redisTemplate.delete("token:" + token);
        }
        return Result.success("已退出登录");
    }
}
