package cn.wubo.entity.sql;

import cn.wubo.entity.sql.core.annotations.*;
import cn.wubo.entity.sql.core.enums.ColumnType;
import cn.wubo.entity.sql.core.enums.EditType;
import lombok.Data;

import java.time.LocalDate;

@Data
@Table(value = "test_user")
public class User {
    @Key
    @Column(value = "id")
    private String id;

    @Column(value = "user_name", label = "用户名", type = ColumnType.VARCHAR, length = 20, edit = @Edit(required = true),search = @Search(searchable = true))
    private String userName;

    @Column(value = "department", label = "部门",
            items = {@Item(value = "1", label = "部门1"), @Item(value = "2", label = "部门2"), @Item(value = "3", label = "部门3")},
            view = @View(width = 300),
            edit = @Edit(type = EditType.SELECT),
            search = @Search(searchable = true))
    private String department;

    @Column(value = "birth", label = "生日", type = ColumnType.DATE,edit = @Edit(type = EditType.DATE))
    private LocalDate birth;

    @Column(value = "age", label = "年龄", type = ColumnType.NUMBER, precision = 10, scale = 0,edit = @Edit(type = EditType.NUMBER))
    private Integer age;

    @Column(value = "amount", label = "薪酬", type = ColumnType.NUMBER, precision = 10, scale = 2,edit = @Edit(type = EditType.NUMBER))
    private Float amount;

    @Column(value = "status", label = "在职", type = ColumnType.VARCHAR, length = 1,
            items = {@Item(value = "Y", label = "在职"), @Item(value = "N", label = "离职")},
            view = @View(),
            edit = @Edit(type = EditType.CHECKBOX)
    )
    private String status;
}
