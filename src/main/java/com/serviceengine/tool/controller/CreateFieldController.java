package com.serviceengine.tool.controller;

import com.serviceengine.tool.moduel.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author jianwei
 * @create 2022-11-15 20:29
 */
@Controller
public class CreateFieldController {

    @GetMapping("/createFiled")
    public String createFiled(String memo, String name, String type, String isPrimaryKey, String tableName, Model model) {
        // 将所有信息做一个空格过滤
        String[] memoArr = Arrays.stream(memo.split("\n")).map(String::trim).toArray(String[]::new);
        String[] nameArr = Arrays.stream(name.split("\n")).map(String::trim).toArray(String[]::new);
        String[] typeArr = Arrays.stream(type.split("\n")).map(String::trim).toArray(String[]::new);
        String[] isPrimaryKeyArr = Arrays.stream(isPrimaryKey.split("\n")).map(String::trim).toArray(String[]::new);
        if ((memoArr.length + nameArr.length + typeArr.length + isPrimaryKeyArr.length) % 4 != 0) {
            System.out.println("长度不相等，请检查字段");
        }
        // 以这个数组的长度为准
        int nameArrLen = nameArr.length;

        // 把大写下划线的命名 更改成 驼峰命名
        String[] camelNameArr = Arrays.stream(nameArr)
                .map(CreateFieldController::toCamelCase)
                .toArray(String[]::new);
        // 生成sql查询语句
        String sqlQuery = IntStream.range(0, nameArrLen)
                .mapToObj(i -> nameArr[i] + " as " + camelNameArr[i])
                .collect(Collectors.joining(",\n    ", "select\n    ", "\nfrom " + tableName));

        // 生成sql建表语句
        String[] tableNameArr = tableName.split("\\.");
        String createDataBase = "Create Database If Not Exists "+ tableNameArr[0] +
                " Character Set UTF8;\nuse "+ tableNameArr[0] +
                ";\n create table " + tableNameArr[1] + "(\n    ";
        String sqlCreateTable = IntStream.range(0, nameArrLen)
                .mapToObj(i -> nameArr[i] + " " + typeArr[i] + (isPrimaryKeyArr[i].equals("Y") || isPrimaryKeyArr[i].equals("是") ? " primary key" : ""))
                .collect(
                        Collectors.joining(",\n    ",
                                createDataBase,
                                "\n);")
                );

        // --------------------------------------------- 分割线  ---------------------------------------------------------
        // 将要生成的实体属性信息的集合
        List<Param> paramList = IntStream.range(0, nameArrLen)
                .mapToObj(i -> new Param(
                        memoArr[i],
                        camelNameArr[i],
                        typeArr[i],
                        isPrimaryKeyArr[i].equals("") ? "N" : isPrimaryKeyArr[i]
                ))
                .collect(Collectors.toList());


        // 拼接成实体类的属性
        StringJoiner attributeStrJoiner = new StringJoiner("\n\n", "", "");
        paramList.forEach(param -> {
            String attributeStr = "@JSONField(serialize = false)\n" +
                    "@ApiModelProperty(value = \"" + param.getMemo() + "\"" +
                    ", name = \"" + param.getName() + "\", example = \"\", required = " + param.getIsPrimaryKey() + ")\n" +
                    "private " + param.getType() + " " + param.getName() + ";";
            attributeStrJoiner.add(attributeStr);
        });

        model.addAttribute("attributeStrJoiner", attributeStrJoiner);
        model.addAttribute("sqlQuery", sqlQuery);
        model.addAttribute("sqlCreateTable", sqlCreateTable);
        return "field";
    }

    /**
     * 把字符串改成驼峰命名法
     * @param input
     * @return
     */
    public static String toCamelCase(String input) {
        StringBuilder result = new StringBuilder();
        if (input != null && input.length() > 0) {
            // 按照下划线分割单词
            String[] words = input.split("_");
            // 如果本身就是驼峰命名法的，就不需要转换
            if (words.length == 1) {
                // 如果本身是全大写，那么就改成全小写
                if (isUpperCase(input)) {
                    return input.toLowerCase();
                }
                return input;
            }

            // 遍历单词，改为驼峰命名法
            for (int i = 0; i < words.length; i++) {
                if (i == 0) {
                    result.append(words[i].toLowerCase());
                } else {
                    result.append(Character.toUpperCase(words[i].charAt(0))); // 将单词的首字母转换为大写
                    result.append(words[i].substring(1).toLowerCase()); // 将单词的其他字母小写后添加到结果字符串中
                }
            }
        }
        return result.toString();
    }

    public static boolean isUpperCase(String input) {
        for (int i = 0; i < input.length(); i++) {
            if (Character.isLowerCase(input.charAt(i))) {
                return false;
            }
        }
        return true;
    }


}
