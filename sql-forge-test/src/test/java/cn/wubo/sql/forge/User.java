package cn.wubo.sql.forge;

import jakarta.persistence.*;

@Table(name = "users")
public class User {

    @Id
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;
}
