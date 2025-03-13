package com.leon.datalink.web.model;

import java.util.List;

public class CreateIdParamVO {
    private List<String> ids;
    private String entityType;
    private String type;

    public List<String> getIds() {
        return ids;
    }

    public CreateIdParamVO setIds(List<String> ids) {
        this.ids = ids;
        return this;
    }

    public String getEntityType() {
        return entityType;
    }

    public CreateIdParamVO setEntityType(String entityType) {
        this.entityType = entityType;
        return this;
    }

    public String getType() {
        return type;
    }

    public CreateIdParamVO setType(String type) {
        this.type = type;
        return this;
    }
}
