package com.serviceengine.tool.utils;

import com.deepoove.poi.XWPFTemplate;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * @author jianwei
 * @create 2022-12-29-14:59
 */
public class WordUtils {
    public static void saveAs(XWPFTemplate template, String createFileName){
        try (FileOutputStream fos = new FileOutputStream(createFileName)) {
            template.write(fos);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (template != null) {
                try {
                    template.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static XWPFTemplate openTemplateAndWire(String templateFileName, Map<String, Object> data){
        return XWPFTemplate.compile(templateFileName).render(data);
    }

}
