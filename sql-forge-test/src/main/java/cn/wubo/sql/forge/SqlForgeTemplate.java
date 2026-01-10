package cn.wubo.sql.forge;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Table(name = "sql_forge_template")
public class SqlForgeTemplate {

    @Id
    private String id;

    @Column(name = "template_type")
    private String templateType;

    @Column(name = "context")
    private String context;
}
