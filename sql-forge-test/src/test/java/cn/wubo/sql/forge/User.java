package cn.wubo.sql.forge;

import jakarta.persistence.*;
import lombok.Data;

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
