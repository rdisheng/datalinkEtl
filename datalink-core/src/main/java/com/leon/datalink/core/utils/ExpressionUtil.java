package com.leon.datalink.core.utils;

import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import org.beetl.core.statement.PlaceholderST;

import java.util.Map;

public class ExpressionUtil {

    private static final TemplateEngine templateEngine;

    static {
        // 模板引擎输出json
        PlaceholderST.output = (ctx, value) -> {
            if (value instanceof String) {
                ctx.byteWriter.writeString(value.toString());
            } else {
                ctx.byteWriter.writeString(JacksonUtils.toJson(value));
            }
        };
        templateEngine = TemplateUtil.createEngine();
    }

    public static String analysis(String template, Map<?, ?> data) {
        if(StringUtils.isEmpty(template) || data == null){
            return template;
        }
        String result = templateEngine.getTemplate(template).render(data);
        if (StringUtils.isEmpty(result)) {
            return template;
        } else {
            return result;
        }
    }


}
