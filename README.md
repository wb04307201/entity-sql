# SQL Forge - SQL工坊

> SQL Forge 是一个用于动态生成和处理SQL语句的Java框架，旨在简化数据库操作并提高开发效率。

[![](https://jitpack.io/v/com.gitee.wb04307201/sql-forge.svg)](https://jitpack.io/#com.gitee.wb04307201/sql-forge)
[![star](https://gitee.com/wb04307201/sql-forge/badge/star.svg?theme=dark)](https://gitee.com/wb04307201/sql-forge)
[![fork](https://gitee.com/wb04307201/sql-forge/badge/fork.svg?theme=dark)](https://gitee.com/wb04307201/sql-forge)
[![star](https://img.shields.io/github/stars/wb04307201/sql-forge)](https://github.com/wb04307201/sql-forge)
[![fork](https://img.shields.io/github/forks/wb04307201/sql-forge)](https://github.com/wb04307201/sql-forge)  
![MIT](https://img.shields.io/badge/License-Apache2.0-blue.svg) ![JDK](https://img.shields.io/badge/JDK-17+-green.svg) ![SpringBoot](https://img.shields.io/badge/Srping%20Boot-3+-green.svg)

## 功能特性

- `sql-forge-crud`: 实现常见的增删改查操作
- `sql-forge-template`: 提供SQL模板引擎功能
- `sql-forge-calcite`: 集成Apache Calcite进行高级SQL处理
- `sql-forge-console`: 控制台工具模块


## 使用
### 引入依赖
增加 JitPack 仓库
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

```xml
<dependency>
    <groupId>com.gitee.wb04307201.sql-forge</groupId>
    <artifactId>sql-forge-spring-boot-starter</artifactId>
    <version>1.4.4</version>
</dependency>
```

## 使用

### Entity 模块
提供基于实体类的ORM操作：
```java
// 实体类定义
@Data
@Table(name = "users")
public class User {
  @Id
  private String id;

  @Column(name = "username")
  private String username;

  @Column(name = "email")
  private String email;
}

// 使用示例
User user = new User();
user.setUsername("john");
user.setEmail("john@example.com");
user = entityService.run(Entity.save(user));
```

### Json CRUD 模块
提供基于Json的crud操作：

### Template 模块
提供SQL模板引擎功能，支持动态SQL生成：
```java
String template = "SELECT * FROM users WHERE 1=1" +
    "<if test=\"name != null && name != ''\"> AND username = #{name}</if>" +
    "<if test=\"ids != null && !ids.isEmpty()\">" +
    "<foreach collection=\"ids\" item=\"id\" open=\" AND id IN (\" separator=\",\" close=\")\">#{id}</foreach>" +
    "</if>";

Map<String, Object> params = new HashMap<>();
params.put("name", "john");
params.put("ids", Arrays.asList(1, 2, 3));

SqlTemplateEngine engine = new SqlTemplateEngine();
SqlScript script = engine.process(template, params);
```

### 联邦查询
集成Apache Calcite实现联邦查询：
```java
// 支持跨多个数据源的联合查询
String sql = """
    SELECT student.name, sum(score.grade) as grade 
    FROM MYSQL.student as student 
    JOIN POSTGRES.score as score ON student.id=score.student_id 
    WHERE student.id>0 
    GROUP BY student.name
    """;
```

### 控制台
提供Web界面用于调试和管理SQL操作：
- 数据库元数据浏览
- SQL模板管理
- 在线SQL执行
- 查询结果展示
