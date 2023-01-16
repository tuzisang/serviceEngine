package com.serviceengine.tool.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.MiniTableRenderData;
import com.deepoove.poi.data.RowRenderData;
import com.serviceengine.tool.moduel.Param;
import com.serviceengine.tool.utils.ExcelUtils;
import com.serviceengine.tool.utils.WordUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author jianwei
 * @create 2022-11-14 22:53
 */
@Controller
public class ParseXMLController {

    private final String excelFileName = "D:/workcode/服务引擎/服务引擎接口清单_测试.xlsx";

    private final String wordTemplateFileName = "D:/workcode/接口文档-模板.docx";

    private final String wordFileName = "D:/workcode/接口文档.docx";

    @GetMapping("/parseXML")
    public String parseXML(String xml, Model model) {
        // 获取根部
        Element root = getRootElement(xml);

        // 解析输入参数
        List<Param> inPutParamList = new ArrayList<>();
        Iterator iterator = root.element("url").element("inputParam").elementIterator();
        while (iterator.hasNext()) {
            Element element = (Element) iterator.next();
            Param param = new Param(
                    element.attributeValue("memo"),
                    element.attributeValue("name"),
                    element.attributeValue("type"),
                    element.attributeValue("require"),
                    element.attributeValue("defaultValue")
            );
            inPutParamList.add(param);
        }


        // 解析输出参数
        List<Param> outPutParamList = new ArrayList<>();
        iterator = root.element("url").element("outputParam").elementIterator();
        while (iterator.hasNext()) {
            Element element = (Element) iterator.next();
            Param param = new Param(
                    element.attributeValue("memo"),
                    element.attributeValue("name").toLowerCase(Locale.ROOT),
                    element.attributeValue("type")
            );
            if ("状态码".equals(param.getMemo()) || "内容信息".equals(param.getMemo()) || "返回对象".equals(param.getMemo())) {
                continue;
            }
            outPutParamList.add(param);
        }

        // 生成输出参数的json
        String outPutJSON = generateOutputJSON(outPutParamList);

        // 把入参和出参追加到 清单表
        appendToExcel(excelFileName, inPutParamList, outPutParamList);

        // 把入参、出参、json 写到 接口文档模板
        Map<String, Object> data = new HashMap<>(20);
        data.put("title", "接口文档");
        data.put("inParamTable", buildInputParameterTable(inPutParamList));
        data.put("outParamTable", buildOutputParameterTable(outPutParamList));
        data.put("json", outPutJSON);

        // 生成文档
        generateWordDocument(wordTemplateFileName, wordFileName, data);

        model.addAttribute("excelFileName", excelFileName);
        model.addAttribute("wordFileName", wordFileName);

        return "parseXML";
    }

    public static void appendToExcel(String fileName, List<Param> inPutParamList, List<Param> outPutParamList) {
        XSSFWorkbook workbook = ExcelUtils.openWorkBook(fileName);
        XSSFSheet sheet = workbook.getSheet("出参入参清单");
        // 追加新的行
        ExcelUtils.appendRow(sheet, inPutParamList, true);
        ExcelUtils.appendRow(sheet, outPutParamList, false);
        ExcelUtils.saveWorkBook(fileName, workbook);
    }

    

    public static void generateWordDocument(String templateFileName, String createFileName, Map<String, Object> data) {
        XWPFTemplate template = WordUtils.openTemplateAndWire(templateFileName, data);
        WordUtils.saveAs(template, createFileName);
    }

    private static MiniTableRenderData buildInputParameterTable(List<Param> inPutParamList) {
        RowRenderData header = RowRenderData.build("序号", "参数名称", "中文说明", "类型", "长度", "是否必填", "参数说明", "参数示例");
        List<RowRenderData> data = IntStream.range(0, inPutParamList.size())
                .mapToObj(i -> {
                    Param param = inPutParamList.get(i);
                    return RowRenderData.build(
                            i + 1 + "",
                            param.getName(),
                            param.getMemo(),
                            param.getType(),
                            "",
                            param.getIsPrimaryKey() ? "是" : "否",
                            "",
                            param.getExample()
                    );
                })
                .collect(Collectors.toList());

        return new MiniTableRenderData(header, data);
    }

    private static MiniTableRenderData buildOutputParameterTable(List<Param> outPutParamList) {
        RowRenderData header = RowRenderData.build("序号", "参数名称", "中文说明", "类型", "参数说明");
        List<RowRenderData> data = IntStream.range(0, outPutParamList.size())
                .mapToObj(i -> {
                    Param param = outPutParamList.get(i);
                    return RowRenderData.build(
                            i + 1 + "",
                            param.getName(),
                            param.getMemo(),
                            param.getType()
                    );
                })
                .collect(Collectors.toList());

        return new MiniTableRenderData(header, data);
    }

    public static String generateOutputJSON(List<Param> outPutParamList) {
        Map<String, Object> outPutMap = new HashMap<>();
        outPutMap.put("msg", "请求处理正常");
        outPutMap.put("code", 200);
        HashMap<String, Object> viewObj = new LinkedHashMap<>();
        outPutParamList.forEach(param -> {
            String type = param.getType();
            if ("String".equals(type)) {
                String val = "xxxx";
                viewObj.put(param.getName(), val);
            } else if ("Long".equals(type)) {
                Long val = 1000000L;
                viewObj.put(param.getName(), val);
            } else if ("Double".equals(type)) {
                Double val = 1234.56;
                viewObj.put(param.getName(), val);
            }
        });
        outPutMap.put("viewObject", viewObj);
        return JSON.toJSONString(outPutMap, JSONWriter.Feature.WriteMapNullValue, JSONWriter.Feature.PrettyFormat);
    }

    private static Element getRootElement(String xml){
        // 解析xml
        xml = xml.trim();
        Document doc = null;
        Element root = null;
        try {
            doc = DocumentHelper.parseText(xml);
            // 获取根部root
            root = doc.getRootElement();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return root;
    }

}
