package cn.wubo.sql.forge;

import jakarta.persistence.*;

import java.time.LocalDate;

//@Entity - 标识实体类
//@Table - 指定表名及schema
//@Id - 标识主键
//@GeneratedValue - 主键生成策略
//@Column - 字段与列映射
//@Transient - 忽略字段不映射
//@Enumerated - 枚举类型映射
//@Lob - 大对象映射
@Table(name = "test_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name", length = 50, nullable = false,comment = "用户名")
    private String name;

    @Column(name = "email_address", unique = true,comment = "邮箱地址")
    private String email;

    @Column(name = "birth_date",comment = "生日")
    private LocalDate birthDate;

    @Column(name = "income",precision = 10, scale = 2,comment = "收入")
    private Integer income;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender",comment = "性别")
    private Gender gender;

    @Lob
    @Column(name = "description",columnDefinition = "TEXT",comment = "描述")
//    @Column(name = "description",columnDefinition = "CLOB",comment = "描述")
    private String description;

    @Lob
    @Column(name = "desc",columnDefinition = "CLOB",comment = "desc")
    private String desc;

    @Lob
    private byte[] data;
}
