package com.leon.datalink.web.model;

import java.util.List;
import java.util.Map;

/**
 * PointsExportVO
 * @author Leon
 * @date 2023年7月4日17:33:12
 * @version 1.0.0
 */
public class PointsExportVO {

    private String fileName;

    private List<Object> data;

    private Map<String,String> fieldMap;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<Object> getData() {
        return data;
    }

    public void setData(List<Object> data) {
        this.data = data;
    }

    public Map<String, String> getFieldMap() {
        return fieldMap;
    }

    public void setFieldMap(Map<String, String> fieldMap) {
        this.fieldMap = fieldMap;
    }
}
