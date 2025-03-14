package com.leon.datalink.web.controller;

import com.leon.datalink.core.storage.impl.ObjectStorage;
import com.leon.datalink.web.common.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.lang.reflect.Field;
import java.util.*;

@Controller
@Api(tags = "获取数据库数据相关接口")
@RequestMapping("/AllData")
public class getAllData {

    private static final String[] POSSIBLE_PACKAGES = {
            "com.leon.datalink.core.backup.",
            "com.leon.datalink.node.plugin.",
            "com.leon.datalink.resource.entity.",
            "com.leon.datalink.rule.entity.",
            "com.leon.datalink.node.script.",
            "java.lang.",
            "com.leon.datalink.web.model.",
            "com.leon.datalink.core.variable.",
    };

    public static Class<?> findClass(String className) throws ClassNotFoundException {
        for (String pkg : POSSIBLE_PACKAGES) {
            try {
                String fullClassName = pkg + Character.toUpperCase(className.charAt(0)) + className.substring(1);
                return Class.forName(fullClassName); // 尝试加载类
            } catch (ClassNotFoundException e) {
                // 继续尝试下一个包路径
            }
        }
        throw new ClassNotFoundException("找不到类：" + className);
    }

    @ApiOperation("获取所有数据库表名和字段信息")
    @RequestMapping(value = "/getData", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Map<String, Object>> getData() {
        try {
            Map<String, List<String>> databaseSchema = new HashMap<>();
            Map<String, Object> databaseData = new HashMap<>();
            Map<String, Object> res = new HashMap<>();

            // 获取所有表（类名）
            List<String> tables = ObjectStorage.getAllTables();

            for (String tableName : tables) {
                try {
                    // 通过类名找到java类
//                    Class<?> clazz = Class.forName("com.leon.datalink.core.model." + tableName);
                    Class<?> clazz = findClass(tableName);
                    List<String> fields = new ArrayList<>();

                    // 读取表所有的字段
                    for (Field field : clazz.getDeclaredFields()) {
                        fields.add(field.getName());
                    }
                    databaseSchema.put(tableName, fields);

                    // 读取表所有的数据
                    ObjectStorage<?> storage = new ObjectStorage<>(clazz);
                    List<?> values = storage.getValues(); // 获取LMDB里所有的数据

                    // 组合 JSON 结构
                    Map<String, Object> tableData = new HashMap<>();
                    tableData.put("fileds", fields);
                    tableData.put("data",values);
                    databaseData.put(tableName, tableData);


                } catch (ClassNotFoundException e) {
                    databaseSchema.put(tableName, Collections.singletonList("Class not found"));
                    databaseData.put(tableName, Collections.singletonMap("error", "Class not found"));
                }
            }
            res.put("databaseSchema",databaseSchema);
            res.put("databaseData",databaseData);

            return CommonResult.success(res);
        } catch (Exception e) {
            return CommonResult.failed("获取数据库表信息失败：" + e.getMessage());
        }
    }
}
