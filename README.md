# 任务看板 Task Board

基于 **Spring Boot 3 + Spring Data JPA + MySQL** 实现的简单任务看板项目。

## 功能
- 任务增删改查
- 三列看板（待办 / 进行中 / 已完成）
- 任务优先级（高 / 中 / 低）彩色标色
- 截止时间设置，超期自动标红提醒
- 任务编辑、分页查询
- 统一返回格式、全局异常处理、参数校验

## 技术栈
- Java 17
- Spring Boot 3.3.5
- Spring Data JPA
- MySQL 8
- H2（开发调试可切换）

## 运行方式
1. 创建数据库：`CREATE DATABASE taskboard`
2. 修改 `src/main/resources/application.properties` 中的数据库账号密码
3. 运行主类 `TaskBoardApplication`
4. 浏览器访问 `http://localhost:8080`
