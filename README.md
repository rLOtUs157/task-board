# 任务看板 Task Board

基于 **Spring Boot 3 + Spring Data JPA + MySQL + Redis + JWT** 实现的任务看板项目，带用户登录鉴权与缓存。

## 功能
- 用户注册 / 登录 / 退出（JWT 令牌鉴权，登录拦截器保护所有业务接口）
- 登录态与令牌存入 Redis，支持过期与退出失效
- 任务增删改查，三列看板（待办 / 进行中 / 已完成）
- 任务优先级（高 / 中 / 低）彩色标色
- 截止时间设置，超期自动标红提醒
- 任务编辑、分页查询
- 分页查询结果 Redis 缓存，降低数据库压力
- 统一返回格式、全局异常处理、参数校验

## 技术栈
- Java 17
- Spring Boot 3.3.5
- Spring Data JPA
- MySQL 8
- Redis（缓存 + 登录态）
- JJWT（JWT 令牌）
- Lombok

## 运行方式
1. 启动 MySQL，创建数据库：`CREATE DATABASE taskboard`
2. 启动 Redis（默认 6379 端口）
3. 修改 `src/main/resources/application.properties` 中的数据库账号密码
4. 运行主类 `TaskBoardApplication`
5. 浏览器访问 `http://localhost:8080`，注册账号后登录使用
