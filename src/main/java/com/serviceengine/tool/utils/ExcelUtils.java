package com.serviceengine.tool.utils;

import com.serviceengine.tool.moduel.Param;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * @author jianwei
 * @create 2022-12-29-14:48
 */
public class ExcelUtils {
    public static XSSFWorkbook openWorkBook(String fileName){
        XSSFWorkbook workbook = null;
        try (FileInputStream fis = new FileInputStream(fileName)) {
            workbook = new XSSFWorkbook(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return workbook;
    }

    public static void saveWorkBook(String fileName, XSSFWorkbook workbook){
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            workbook.write(fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void appendRow(XSSFSheet sheet, List<Param> paramList, boolean isInParameter) {
        int lastRowNum = sheet.getLastRowNum();
        for (int i = 0; i < paramList.size(); i++) {
            XSSFRow row = sheet.createRow(lastRowNum + i + 1);
            XSSFCell cell = row.createCell(2);
            cell.setCellValue(isInParameter ? "入参" : "出参");
            cell = row.createCell(3);
            cell.setCellValue(paramList.get(i).getName());
            cell = row.createCell(4);
            cell.setCellValue(paramList.get(i).getMemo());
            cell = row.createCell(5);
            cell.setCellValue(paramList.get(i).getType());
        }
    }
}
