package com.example.taskboard.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "任务标题不能为空")
    @Column(nullable = false)
    private String title;   // 任务标题

    private String content; // 任务详情

    private String status;  // 状态：待办/进行中/已完成

    private String priority; // 优先级：高/中/低

    private LocalDateTime deadline; // 截止时间
}
