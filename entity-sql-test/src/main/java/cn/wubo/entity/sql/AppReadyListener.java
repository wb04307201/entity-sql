package cn.wubo.entity.sql;

import cn.wubo.dynamic.loader.utility.compiler.DynamicCompiler;
import cn.wubo.entity.sql.web.EntityWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AppReadyListener implements ApplicationRunner {

    private final EntityWebService entityWebService;

    @Autowired
    public AppReadyListener(EntityWebService entityWebService) {
        this.entityWebService = entityWebService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // user 为页面访问标识，多个界面注意标识不要重复
        entityWebService.build("user", User.class);


        Class<?> clazz;
        try {
            clazz = DynamicCompiler.compileAndLoad("""
                    
                    package cn.wubo.one.table.ai;
                    
                    import cn.wubo.entity.sql.core.annotations.*;
                    import cn.wubo.entity.sql.core.enums.ColumnType;
                    import cn.wubo.entity.sql.core.enums.EditType;
                    import lombok.Data;
                    
                    import java.time.LocalDate;
                    
                    @Data
                    @Table(value = "student_info_9f3a1b2c", init = true)
                    public class Student {
                    
                        @Column(value = "id", key = @Key(isKey = true))
                        private String id;
                    
                        @Column(value = "student_name", label = "学生姓名", type = ColumnType.VARCHAR, length = 50, edit = @Edit(required = true), search = @Search(searchable = true))
                        private String studentName;
                    
                        @Column(value = "gender", label = "性别", type = ColumnType.VARCHAR, length = 2,
                                items = {@Item(value = "male", label = "男"), @Item(value = "female", label = "女")},
                                edit = @Edit(type = EditType.CHECKBOX, required = true), search = @Search(searchable = true))
                        private String gender;
                    
                        @Column(value = "birth_date", label = "出生日期", type = ColumnType.DATE, edit = @Edit(type = EditType.DATE, required = true), search = @Search(searchable = true))
                        private LocalDate birthDate;
                    
                        @Column(value = "enrollment_date", label = "入学日期", type = ColumnType.DATE, edit = @Edit(type = EditType.DATE, required = true))
                        private LocalDate enrollmentDate;
                    
                        @Column(value = "grade", label = "年级", type = ColumnType.VARCHAR, length = 10,
                                items = {@Item(value = "1", label = "一年级"), @Item(value = "2", label = "二年级"), @Item(value = "3", label = "三年级"),
                                        @Item(value = "4", label = "四年级"), @Item(value = "5", label = "五年级"), @Item(value = "6", label = "六年级")},
                                edit = @Edit(type = EditType.SELECT, required = true), search = @Search(searchable = true))
                        private String grade;
                    
                        @Column(value = "class_name", label = "班级", type = ColumnType.VARCHAR, length = 4,
                                items = {@Item(value = "1", label = "一班"), @Item(value = "2", label = "二班"), @Item(value = "3", label = "三班")},
                                edit = @Edit(type = EditType.SELECT, required = true), search = @Search(searchable = true))
                        private String className;
                    
                        @Column(value = "student_id", label = "学号", type = ColumnType.VARCHAR, length = 20, edit = @Edit(required = true), search = @Search(searchable = true))
                        private String studentId;
                    }
                    
                    """);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        entityWebService.build("student", clazz);
    }
}
