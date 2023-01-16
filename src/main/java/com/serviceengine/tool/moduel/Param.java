package com.serviceengine.tool.moduel;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.ui.context.ThemeSource;

import java.util.Locale;

/**
 * @author jianwei
 * @create 2022-11-15 17:33
 */

public class Param {
    private static final String NUMERIC = "numeric";
    private static final String VARCHAR = "varchar";
    /**
     * 中文说明
     */
    private String memo;

    /**
     * 参数名称
     */
    private String name;

    /**
     * 数据类型
     */
    private String type;


    /**
     * 是否主键
     */
    private boolean isPrimaryKey;

    /**
     * 参数示例
     */
    private String example;

    public Param(String memo, String name, String type) {
        this.memo = memo;
        this.name = name;
        this.type = type;
    }

    public Param() {
    }

    public Param(String memo, String name, String type, String isPrimaryKey) {
        this.memo = memo;
        this.name = name;
        setType(type);
        setIsPrimaryKey(isPrimaryKey);
    }
    public Param(String memo, String name, String type, String isPrimaryKey, String example) {
        this.memo = memo;
        this.name = name;
        setType(type);
        setIsPrimaryKey(isPrimaryKey);
        this.example = example;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getName() {
        return name;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        if (type.contains(NUMERIC.toLowerCase(Locale.ROOT))) {
            String[] split = type.split(",");
            if (split.length > 1) {
                if ("2".equals(split[1])) {
                    this.type = "Double";
                } else {
                    this.type = "Long";
                }
            }
        } else if (type.contains(VARCHAR.toLowerCase(Locale.ROOT))) {
            this.type = "String";
        } else {
            this.type = type;
        }
    }


    public boolean getIsPrimaryKey() {
        return isPrimaryKey;
    }

    public void setIsPrimaryKey(String isPrimaryKey) {
        this.isPrimaryKey = "Y".equals(isPrimaryKey) || "是".equals(isPrimaryKey) || "true".equals(isPrimaryKey);

    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        isPrimaryKey = primaryKey;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    @Override
    public String toString() {
        return "Param{" +
                "memo='" + memo + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", isPrimaryKey=" + isPrimaryKey +
                '}';
    }


}
