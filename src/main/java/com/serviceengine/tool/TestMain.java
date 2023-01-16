package com.serviceengine.tool;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.MiniTableRenderData;
import com.deepoove.poi.data.RowRenderData;

import java.io.*;
import java.util.*;

/**
 * @author jianwei
 * @create 2022-11-14 22:27
 */
public class TestMain {
    public static void main(String[] args) {
        String templateFileName = "D:\\workcode\\接口文档-模板.docx";
        XWPFTemplate template = XWPFTemplate.compile(templateFileName)
                .render(new HashMap<String, Object>() {{
                    /* 文字 */
                    put("title", "接口文档");
                    RowRenderData header = RowRenderData.build("姓名", "学历");
                    RowRenderData row0 = RowRenderData.build("张三", "研究生");
                    RowRenderData row1 = RowRenderData.build("李四", "博士");
                    RowRenderData row2 = RowRenderData.build("王五", "博士后");
                    put("table", new MiniTableRenderData(header, Arrays.asList(row0, row1, row2)));
                }});

        try (FileOutputStream fos = new FileOutputStream("D:\\workcode\\接口文档.docx")) {
            template.write(fos);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (template != null) {
                template.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
