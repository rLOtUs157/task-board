package com.example.taskboard.controller;

import com.example.taskboard.common.Result;
import com.example.taskboard.entity.Task;
import com.example.taskboard.repository.TaskRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@Validated
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    // 1. 分页查询所有任务
    @GetMapping
    public Result<Page<Task>> getAllTasks(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Task> result = taskRepository.findAll(PageRequest.of(page - 1, size));
        return Result.success(result);
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
