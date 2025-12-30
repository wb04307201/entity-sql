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
- [Entity](file://D:\developer\IdeaProjects\entity-sql\sql-forge-crud\src\main\java\cn\wubo\sql\forge\Entity.java) 提供了对实体对象进行数据库操作的构建器，包括删除、插入、查询、更新、保存等操作，简化`SQL`构建过程。
- [EntityService](file://D:\developer\IdeaProjects\entity-sql\sql-forge-crud\src\main\java\cn\wubo\sql\forge\EntityService.java) 负责执行**构建器**的数据库操作。

#### 特点
- 使用链式调用，API 设计简洁
- 支持类型安全的泛型操作
- 通过构建器模式灵活配置查询条件
- 统一的数据库操作入口
- 

#### 使用示例

假设有一个用户实体类 [User](file://D:\developer\IdeaProjects\entity-sql\sql-forge-test\src\test\java\cn\wubo\sql\forge\User.java)：

```java
@Autowired
private EntityService entityService;


// 查询操作
EntitySelect<User> select = Entity.select(User.class)
                .distinct(true)
                .columns(User::getId, User::getUsername, User::getEmail)
                .orders(User::getUsername)
                .in(User::getUsername, "alice", "bob")
                .page(0, 1);
List<User> users = entityService.run(select);
Object key = entityService.run(insert);

// 插入操作  
EntityInsert<User> insert = Entity.insert(User.class).set(User::getId, id)
        .set(User::getUsername, "wb04307201")
        .set(User::getEmail, "wb04307201@gitee.com");
int count = entityService.run(update);

// 更新操作
EntityUpdate<User> update = Entity.update(User.class)
        .set(User::getEmail, "wb04307201@github.com")
        .eq(User::getId, id);
int count = entityService.run(update);

// 删除操作
EntityDelete<User> delete = Entity.delete(User.class)
        .eq(User::getId, id);
count = entityService.run(delete);

// 对象保存操作（插入或更新）
User user = new User();
user.setUsername("wb04307201");
user.setEmail("wb04307201@gitee.com");
user = entityService.run(Entity.save(user));
user.setEmail("wb04307201@github.com");
user = entityService.run(Entity.save(user));

// 对象删除操作
int count = entityService.run(Entity.delete(user));
```

#### 查询构造说明

##### 1. 列选择
- column(SFunction<T, ?> column) - 选择单个列
- columns(SFunction<T, ?>... columns) - 选择多个列

##### 2. 查询条件
- eq(SFunction<T, ?> column, Object value) - 等于
- neq(SFunction<T, ?> column, Object value) - 不等于
- gt(SFunction<T, ?> column, Object value) - 大于
- lt(SFunction<T, ?> column, Object value) - 小于
- gteq(SFunction<T, ?> column, Object value) - 大于等于
- lteq(SFunction<T, ?> column, Object value) - 小于等于
- like(SFunction<T, ?> column, Object value) - 模糊匹配
- notLike(SFunction<T, ?> column, Object value) - 不模糊匹配
- leftLike(SFunction<T, ?> column, Object value) - 左模糊匹配
- rightLike(SFunction<T, ?> column, Object value) - 右模糊匹配
- between(SFunction<T, ?> column, Object value1, Object value2) - 在范围内
- notBetween(SFunction<T, ?> column, Object value1, Object value2) - 不在范围内
- in(SFunction<T, ?> column, Object... value) - 在集合中
- notIn(SFunction<T, ?> column, Object... value) - 不在集合中
- isNull(SFunction<T, ?> column) - 为 NULL
- isNotNull(SFunction<T, ?> column) - 不为 NULL

##### 3. 排序
- orderAsc(SFunction<T, ?> column) - 升序排序
- orderDesc(SFunction<T, ?> column) - 降序排序
- orders(SFunction<T, ?>... columns) - 多列排序（默认升序）

##### 4. 分页
- page(Integer pageIndex, Integer pageSize) - 设置分页参数

##### 5. 去重
- distinct(Boolean distinct) - 设置是否去重

#### 对象保存操作构造说明

根据`@Id`注解判断主键字段，如果没有主键字段，抛出 `IllegalArgumentException`
- **插入条件**: 当主键值为 `null` 时执行插入操作
    - `String` 类型主键：自动生成 `UUID` 作为主键值
    - 其他类型主键：使用数据库自动生成的主键值
- **更新条件**: 当主键值不为 `null` 时执行更新操作
    - 使用主键值作为更新条件


### Json API 模块
让前端无需编写后端代码即可操作数据库，通过`JSON`格式描述自己需要的数据结构和操作，后端自动生成对应的`SQL`执行并返回结果。

- **请求路径**: `sql/forge/api/json/{method}/{tableName}`
- **请求方法**: `POST`
- **内容类型**: `application/json`
- **路径参数**:
  - `{method}`: 操作方法类型(delete、insert、select、update)
  - `{tableName}`: 数据库表名称                             |

#### delete 方法

#### 请求格式
```json
{
  "@where": [
    {
      "column": "字段名",
      "condition": "条件类型",
      "value": "值"
    }
  ],
  "@with_select": {
    "columns": ["字段名"],
    "wheres": [...],
    "page": {
      "pageIndex": 0,
      "pageSize": 10
    }
  }
}
```

#### 参数说明
- `@where`: 删除条件数组，每个条件包含：
  - column: 要匹配的字段名
  - condition: 条件类型（EQ、NOT_EQ、GT、LT、GTEQ、LTEQ、LIKE、NOT_LIKE、LEFT_LIKE、RIGHT_LIKE、BETWEEN、NOT_BETWEEN、IN、NOT_IN、IS_NULL、IS_NOT_NULL）
  - value: 匹配的值
- `@with_select`: 可选的查询条件，用于关联查询

#### insert 方法

#### 请求格式
```json
{
  "@set": {
    "字段名1": "值1",
    "字段名2": "值2"
  },
  "@with_select": {
    "columns": ["字段名"],
    "wheres": [...],
    "page": {
      "pageIndex": 0,
      "pageSize": 10
    }
  }
}
```


#### 参数说明
- `@set`: 要插入的字段和值的键值对，至少需要一个字段
- `@with_select`: 可选的查询条件，用于关联查询

#### select 方法

#### 请求格式
```json
{
  "@column": ["字段名1", "字段名2"],
  "@where": [
    {
      "column": "字段名",
      "condition": "条件类型",
      "value": "值"
    }
  ],
  "@page": {
    "pageIndex": 0,
    "pageSize": 10
  },
  "@join": [
    {
      "type": "JOIN类型",
      "joinTable": "关联表名",
      "on": "关联条件"
    }
  ],
  "@order": ["字段名 ASC", "字段名 DESC"],
  "@group": ["字段名"],
  "@distince": false
}
```

##### 参数说明
- `@column`: 要查询的字段数组，为空则查询所有字段
- `@where`: 查询条件数组
- `@page`v分页参数
  - pageIndex: 页码（从0开始）
  - pageSize: 每页大小
- `@join`: 关联查询条件数组
- `@order`: 排序字段数组
- `@group`: 分组字段数组
- `@distince`: 是否去重

#### update 方法

##### 请求格式
```json
{
  "@set": {
    "字段名1": "新值1",
    "字段名2": "新值2"
  },
  "@where": [
    {
      "column": "字段名",
      "condition": "条件类型",
      "value": "值"
    }
  ],
  "@with_select": {
    "columns": ["字段名"],
    "wheres": [...],
    "page": {
      "pageIndex": 0,
      "pageSize": 10
    }
  }
}
```

##### 参数说明
- `@set`: 要更新的字段和新值的键值对，至少需要一个字段
- `@where`: 更新条件数组，指定要更新哪些记录
- `@with_select`: 可选的查询条件，用于更新后查询

#### 示例
```http request
POST http://localhost:8080/sql/forge/api/json/select/orders
Content-Type: application/json

{
    "@column": [
        "orders.id AS order_id",
        "users.username",
        "products.name AS product_name",
        "products.price",
        "orders.quantity",
        "(products.price * orders.quantity) AS total"
    ],
    "@where": [
        {
            "column": "users.username",
            "condition": "EQ",
            "value": "alice"
        }
    ],
    "@page": {
        "pageIndex": 0,
        "pageSize": 10
    },
    "@join": [
        {
            "type": "INNER_JOIN",
            "joinTable":"users",
            "on": "orders.user_id = users.id"
        },
        {
            "type": "INNER_JOIN",
            "joinTable":"products",
            "on": "orders.product_id = products.id"
        }
    ],
    "@order": [
        "orders.order_date"
    ],
    "@group": null,
    "@distince": false
}
```

```http request
POST http://localhost:8080/sql/forge/api/json/insert/users
Content-Type: application/json

{
    "@set": [
        {
            "column": "id",
            "value": "26a05ba3-913d-4085-a505-36d40021c8d1"
        },
        {
            "column": "username",
            "value": "wb04307201"
        },
        {
            "column": "email",
            "value": "wb04307201@gitee.com"
        }
    ],
    "@with_select": {
        "@column": null,
        "@where": [
            {
                "column": "id",
                "condition": "EQ",
                "value": "26a05ba3-913d-4085-a505-36d40021c8d1"
            }
        ],
        "@page": null,
        "@join": null,
        "@order": null,
        "@group": null,
        "@distince": false
    }
}
```

```http request
POST http://localhost:8080/sql/forge/api/json/update/users
Content-Type: application/json

{
    "@set": [
        {
            "column": "email",
            "value": "wb04307201@github.com"
        }
    ],
    "@where": [
        {
            "column": "id",
            "condition": "EQ",
            "value": "26a05ba3-913d-4085-a505-36d40021c8d1"
        }
    ],
    "@with_select": {
        "@column": null,
        "@where": [
            {
                "column": "id",
                "condition": "EQ",
                "value": "26a05ba3-913d-4085-a505-36d40021c8d1"
            }
        ],
        "@page": null,
        "@join": null,
        "@order": null,
        "@group": null,
        "@distince": false
    }
}
```

```http request
POST http://localhost:8080/sql/forge/api/json/delete/users
Content-Type: application/json

{
    "@where": [
        {
            "column": "id",
            "condition": "EQ",
            "value": "26a05ba3-913d-4085-a505-36d40021c8d1"
        }
    ],
    "@with_select": {
        "@column": null,
        "@where": [
            {
                "column": "id",
                "condition": "EQ",
                "value": "26a05ba3-913d-4085-a505-36d40021c8d1"
            }
        ],
        "@page": null,
        "@join": null,
        "@order": null,
        "@group": null,
        "@distince": false
    }
}
```

#### 配置
可通过`sql.forge.api.json.enabled=false`关闭Json API 模块

### Template API 模块
面对更复杂SQL语句需求，提供SQL模板引擎功能，支持条件判断、循环等模板语法，根据参数动态生成`SQL`执行并返回结果。
- **API 模板管理**：提供 API 模板的存储、查询、删除等管理功能
- **模板化 API 执行**：支持通过模板 ID 和参数来执行预定义的 API 模板

#### 模板管理接口

- `POST /sql/forge/api/template` - 保存新的 API 模板
   - id: 模板 ID
   - context: 模板内容
- `GET /sql/forge/api/template/{id}` - 根据 ID 获取模板
- `GET /sql/forge/api/template` - 获取模板列表
- `DELETE /sql/forge/api/template/{id}` - 删除指定 ID 的模板

#### 模板执行接口

- `POST /sql/forge/api/template/execute/{id}` - 执行指定 ID 的模板
  - 模板参数 Map


### 示例
```http request
POST http://localhost:8080/sql/forge/api/template
content-type: application/json

{
  "id": "api-template-test",
  "context": "SELECT * FROM users WHERE 1=1<if test=\"name != null && name != ''\"> AND username = #{name}</if><if test=\"ids != null && !ids.isEmpty()\"><foreach collection=\"ids\" item=\"id\" open=\" AND id IN (\" separator=\",\" close=\")\">#{id}</foreach></if><if test=\"(name == null || name == '') && (ids == null || ids.isEmpty()) \"> AND 0=1</if> ORDER BY username DESC"
}
```

```http request
POST http://localhost:8080/sql/forge/api/template/execute/api-template-test
content-type: application/json

{
"name":"alice",
"ids":null
}
```

### 联邦查询 API 模块
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
