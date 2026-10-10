package com.example.taskboard.controller;

import com.example.taskboard.common.Result;
import com.example.taskboard.entity.Task;
import com.example.taskboard.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/tasks")
@Validated
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    // 从拦截器拿当前登录用户ID
    private Long currentUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }

    // 清空该用户的任务缓存（增删改后调用，保证缓存一致）
    private void clearCache(Long userId) {
        Set<String> keys = redisTemplate.keys("tasks:" + userId + ":*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    // 1. 分页查询当前用户的任务（带Redis缓存，key含userId隔离）
    @GetMapping
    public Object getAllTasks(HttpServletRequest request,
                              @RequestParam(defaultValue = "1") Integer page,
                              @RequestParam(defaultValue = "10") Integer size) throws Exception {
        Long userId = currentUserId(request);
        String cacheKey = "tasks:" + userId + ":" + page + ":" + size;
        String cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached; // 命中缓存，直接返回
        }
        Page<Task> result = taskRepository.findByUserId(userId, PageRequest.of(page - 1, size));
        String json = objectMapper.writeValueAsString(Result.success(result));
        redisTemplate.opsForValue().set(cacheKey, json, 5, TimeUnit.MINUTES); // 缓存5分钟
        return json;
    }

    // 2. 新增任务（归属当前登录用户）
    @PostMapping
    public Result<Task> createTask(HttpServletRequest request, @Valid @RequestBody Task task) {
        task.setUserId(currentUserId(request));
        Task saved = taskRepository.save(task);
        clearCache(task.getUserId());
        return Result.success(saved);
    }

    // 3. 按id查询单个任务（校验归属）
    @GetMapping("/{id}")
    public Result<Task> getTaskById(HttpServletRequest request, @PathVariable Long id) {
        Task t = taskRepository.findById(id).orElse(null);
        if (t == null) {
            return Result.error("任务不存在");
        }
        if (!t.getUserId().equals(currentUserId(request))) {
            return Result.error("无权访问该任务");
        }
        return Result.success(t);
    }

    // 4. 修改任务（校验归属）
    @PutMapping("/{id}")
    public Result<Task> updateTask(HttpServletRequest request, @PathVariable Long id, @RequestBody Task newTask) {
        Long userId = currentUserId(request);
        return taskRepository.findById(id).map(task -> {
            if (!task.getUserId().equals(userId)) {
                return Result.<Task>error("无权修改该任务");
            }
            task.setTitle(newTask.getTitle());
            task.setContent(newTask.getContent());
            task.setStatus(newTask.getStatus());
            task.setPriority(newTask.getPriority());
            task.setDeadline(newTask.getDeadline());
            Task saved = taskRepository.save(task);
            clearCache(userId);
            return Result.success(saved);
        }).orElse(Result.error("任务不存在"));
    }

    // 5. 删除任务（校验归属）
    @DeleteMapping("/{id}")
    public Result<?> deleteTask(HttpServletRequest request, @PathVariable Long id) {
        Long userId = currentUserId(request);
        Task t = taskRepository.findById(id).orElse(null);
        if (t == null) {
            return Result.error("任务不存在");
        }
        if (!t.getUserId().equals(userId)) {
            return Result.error("无权删除该任务");
        }
        taskRepository.deleteById(id);
        clearCache(userId);
        return Result.success();
    }
}
