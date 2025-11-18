package Utility;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtility {

    private static Workbook workbook;
    private static Sheet sheet;
    private static String filePath;

    
    public static void setExcelFile(String path, String sheetName) throws IOException {
        filePath = path;

        FileInputStream fis = new FileInputStream(path);
        workbook = new XSSFWorkbook(fis);
        sheet = workbook.getSheet(sheetName);
        fis.close();

        if (sheet == null) {
            workbook.close();
            throw new RuntimeException("Sheet not found: " + sheetName + " in file: " + path);
        }

        System.out.println("Loaded Excel sheet: " + sheetName);
    }

    
    public static String getCellData(int rowNum, int colNum) {
        if (sheet == null) {
            throw new RuntimeException("Sheet not initialized. Call setExcelFile() first.");
        }

        try {
            Row row = sheet.getRow(rowNum);
            if (row == null) return "";

            Cell cell = row.getCell(colNum);
            if (cell == null) return "";

            DataFormatter formatter = new DataFormatter();
            return formatter.formatCellValue(cell);

        } catch (Exception e) {
            System.out.println("Error reading cell [" + rowNum + "," + colNum + "]: " + e.getMessage());
            return "";
        }
    }


    public static void setCellData(String value, int rowNum, int colNum) throws IOException {
        if (sheet == null) {
            throw new RuntimeException("Sheet not initialized. Call setExcelFile() first.");
        }

        Row row = sheet.getRow(rowNum);
        if (row == null) row = sheet.createRow(rowNum);

        Cell cell = row.getCell(colNum);
        if (cell == null) cell = row.createCell(colNum);

        cell.setCellValue(value);

        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        }

        System.out.println("Wrote '" + value + "' to [" + rowNum + "," + colNum + "]");
    }


    public static int getRowCount() {
        if (sheet == null) {
            throw new RuntimeException("Sheet not initialized. Call setExcelFile() first.");
        }
        return sheet.getLastRowNum() + 1;
    }

    
    public static void closeExcel() {
        try {
            if (workbook != null) {
                workbook.close();
                workbook = null;
                sheet = null;
                System.out.println("Excel file closed successfully.");
            }
        } catch (IOException e) {
            System.out.println("Error closing Excel file: " + e.getMessage());
        }
    }
}
