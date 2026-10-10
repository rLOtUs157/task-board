package com.example.taskboard.repository;

import com.example.taskboard.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    // 按用户分页查询任务（数据权限隔离）
    Page<Task> findByUserId(Long userId, Pageable pageable);
}
