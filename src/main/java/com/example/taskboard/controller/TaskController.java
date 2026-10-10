package com.example.taskboard.controller;

import com.example.taskboard.common.Result;
import com.example.taskboard.entity.Task;
import com.example.taskboard.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    // 1. 分页查询所有任务（带Redis缓存）
    @GetMapping
    public Object getAllTasks(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) throws Exception {
        String cacheKey = "tasks:" + page + ":" + size;
        String cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached; // 命中缓存，直接返回
        }
        Page<Task> result = taskRepository.findAll(PageRequest.of(page - 1, size));
        String json = objectMapper.writeValueAsString(Result.success(result));
        redisTemplate.opsForValue().set(cacheKey, json, 5, TimeUnit.MINUTES); // 缓存5分钟
        return json;
    }

    // 2. 新增任务（带参数校验）
    @PostMapping
    public Result<Task> createTask(@Valid @RequestBody Task task) {
        return Result.success(taskRepository.save(task));
    }

    // 3. 按id查询单个任务
    @GetMapping("/{id}")
    public Result<Task> getTaskById(@PathVariable Long id) {
        Task t = taskRepository.findById(id).orElse(null);
        if (t == null) {
            return Result.error("任务不存在");
        }
        return Result.success(t);
    }

    // 4. 修改任务
    @PutMapping("/{id}")
    public Result<Task> updateTask(@PathVariable Long id, @RequestBody Task newTask) {
        return taskRepository.findById(id).map(task -> {
            task.setTitle(newTask.getTitle());
            task.setContent(newTask.getContent());
            task.setStatus(newTask.getStatus());
            task.setPriority(newTask.getPriority());
            task.setDeadline(newTask.getDeadline());
            return Result.success(taskRepository.save(task));
        }).orElse(Result.error("任务不存在"));
    }

    // 5. 删除任务
    @DeleteMapping("/{id}")
    public Result<?> deleteTask(@PathVariable Long id) {
        taskRepository.deleteById(id);
        return Result.success();
    }
}
