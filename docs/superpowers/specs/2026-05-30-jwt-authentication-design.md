# 第三课：登录认证（JWT）设计文档

## 概述

为 JobDispatchPlatform 添加基于 JWT 的登录认证功能。使用 Spring Security + JJWT 实现，支持用户注册、登录、token 验证。本课只做认证（验证 token 有效性），不做接口权限控制。

## 技术选型

- **Spring Security** — 安全框架，管理过滤链和认证上下文
- **JJWT** — JWT token 的生成与解析
- **BCrypt** — 密码加密（Spring Security 内置）

## 数据模型

### User 表

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | int | 主键自增 | 用户ID |
| phone | varchar(20) | 唯一，非空 | 手机号，用于登录 |
| username | varchar(50) | 非空 | 用户名/昵称，可重复 |
| password | varchar(100) | 非空 | BCrypt 加密后的密码 |
| role | varchar(20) | 非空，默认 USER | 角色：ADMIN / USER |
| create_time | datetime | 非空 | 创建时间 |

## 包结构

```
org.main.jobdispatchplatform/
├── controller/
│   └── AuthController          — 登录、注册接口
├── entity/
│   └── User                    — 用户实体
├── mapper/
│   └── UserMapper              — 用户数据访问
├── service/
│   └── UserService             — 用户业务逻辑（注册、查询）
├── security/
│   ├── SecurityConfig          — Spring Security 配置（过滤链、白名单）
│   ├── JwtTokenProvider        — JWT 生成与解析
│   └── JwtAuthenticationFilter — 请求拦截，解析 token 设置认证信息
└── common/
    └── Result                  — 复用现有响应包装
```

## 接口设计

| 接口 | 方法 | 说明 | 认证 |
|------|------|------|------|
| `/auth/register` | POST | 开放注册，默认 USER 角色 | 无需 |
| `/auth/login` | POST | 手机号+密码登录，返回 token | 无需 |
| `/user` | POST | 管理员创建用户，可指定角色 | 需要（本课暂不校验角色） |

### POST /auth/register

请求体：
```json
{
  "phone": "13800138000",
  "username": "张三",
  "password": "123456"
}
```

成功响应：
```json
{
  "code": 200,
  "message": "注册成功",
  "data": null
}
```

错误响应（手机号已存在）：
```json
{
  "code": 400,
  "message": "手机号已注册",
  "data": null
}
```

### POST /auth/login

请求体：
```json
{
  "phone": "13800138000",
  "password": "123456"
}
```

成功响应：
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  }
}
```

错误响应：
```json
{
  "code": 401,
  "message": "手机号或密码错误",
  "data": null
}
```

### POST /user

请求体：
```json
{
  "phone": "13900139000",
  "username": "李四",
  "password": "123456",
  "role": "ADMIN"
}
```

成功响应：
```json
{
  "code": 200,
  "message": "创建成功",
  "data": null
}
```

## 核心流程

### 注册流程

1. 接收参数：phone、username、password
2. 校验手机号是否已存在 → 已存在返回错误
3. BCrypt 加密密码
4. 插入用户记录（role 默认为 USER）
5. 返回成功（不自动登录，需要用户手动登录）

### 登录流程

1. 接收参数：phone、password
2. 通过 phone 查询用户 → 不存在返回错误
3. BCrypt 验证密码 → 不匹配返回错误
4. 生成 JWT token（payload: userId, phone, role）
5. 返回 token

### 管理员创建用户

1. 接收参数：phone、username、password、role
2. 校验手机号是否已存在
3. BCrypt 加密密码
4. 插入用户记录（使用指定的 role）
5. 返回成功

### JWT Filter 拦截流程

1. 从请求 Header 中取 `Authorization: Bearer <token>`
2. 如果没有 token 且路径在白名单中 → 放行
3. 如果没有 token 且路径不在白名单 → 返回 401
4. 有 token → 解析验证（签名、过期时间）
5. 验证通过 → 创建 Authentication 对象放入 SecurityContext → 放行
6. 验证失败（过期/篡改）→ 返回 401

## JWT Token 配置

- 签名算法：HS256
- 有效期：24小时（可配置在 application.properties）
- Secret Key：配置在 application.properties
- Payload 包含：userId、phone、role

## 白名单路径

- `POST /auth/login`
- `POST /auth/register`

## Spring Security 配置要点

- 禁用 CSRF（REST API 不需要）
- 禁用 Session（使用 JWT 无状态认证）
- 配置白名单路径允许匿名访问
- 其余路径需要认证
- 注册 JwtAuthenticationFilter 在 UsernamePasswordAuthenticationFilter 之前

## 依赖新增

```xml
<!-- Spring Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- JJWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
```

## application.properties 新增配置

```properties
# JWT
jwt.secret=your-256-bit-secret-key-here-change-in-production
jwt.expiration=86400000
```

## 错误处理

复用现有的 GlobalExceptionHandler，新增：
- 认证失败返回 401（在 Filter 中直接写响应，不经过 Controller）
- 手机号已存在返回 400

## 不在本课范围内

- 接口权限控制（RBAC）— 下一课
- Refresh Token — 后续迭代
- 严格手机号格式校验 — 本课仅做 @NotBlank 非空校验，严格格式校验后续迭代
- 用户信息修改/删除接口 — 后续迭代
