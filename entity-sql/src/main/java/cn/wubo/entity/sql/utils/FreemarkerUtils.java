package cn.wubo.entity.sql.utils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

public class FreemarkerUtils {

    private FreemarkerUtils() {
    }

        /**
     * 使用FreeMarker模板引擎生成字符串内容
     * @param templateName 模板文件名称
     * @param params 模板参数Map，键为模板变量名，值为对应的变量值
     * @return 生成的字符串内容
     * @throws IOException 当模板文件读取失败时抛出
     * @throws TemplateException 当模板处理过程中出现错误时抛出
     */
    public static String write(String templateName, Map<String, Object> params) throws IOException, TemplateException {
        try (StringWriter sw = new StringWriter()) {
            // 初始化FreeMarker配置，设置模板加载路径
            Configuration cfg = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_23);
            cfg.setClassForTemplateLoading(FreemarkerUtils.class, "/template");

            // 加载指定名称和编码的模板
            Template template = cfg.getTemplate(templateName, "UTF-8");

            // 使用模板和参数生成输出，写入到StringWriter中
            template.process(params, sw);

            return sw.toString();
        }
    }

}
