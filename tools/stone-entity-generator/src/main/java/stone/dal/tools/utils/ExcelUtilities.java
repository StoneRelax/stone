package stone.dal.tools.utils;


import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.model.InternalSheet;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import stone.dal.tools.meta.ExcelColumnMeta;
import stone.dal.tools.meta.SingleExcelExportMeta;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Component:  ExcelUtilities
 * Description:  ExcelUtilities
 * User: feng.xie
 * Date: 29/06/11
 */
public class ExcelUtilities {

    public static void export(SingleExcelExportMeta exportMeta, OutputStream fis) {
        try {
            String title = exportMeta.getTitle();
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = null;
            if (StringUtils.isEmpty(title)) {
                sheet = workbook.createSheet();
            } else {
                sheet = workbook.createSheet(title);
            }
            int currentRow = 0;
            HSSFDataFormat format = workbook.createDataFormat();
            String dateFormat = exportMeta.getDateFormat();
            if (dateFormat == null) {
                dateFormat = "yyyy/MM/dd";
            }
            HSSFCellStyle dateCellStyle = workbook.createCellStyle();
            dateCellStyle.setDataFormat(format.getFormat(dateFormat));

            HSSFCellStyle dateTimeCellStyle = workbook.createCellStyle();
            dateTimeCellStyle.setDataFormat(format.getFormat(dateFormat + " HH:mm:ss"));

            HSSFCellStyle headCellStyle = workbook.createCellStyle();
            headCellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
//            headCellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
//            headCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            headCellStyle.setWrapText(true);

            HSSFCellStyle sequenceCellStyle = workbook.createCellStyle();
            sequenceCellStyle.setFillForegroundColor(IndexedColors.GOLD.getIndex());
//            sequenceCellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
//            sequenceCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

            if (!StringUtilities.isEmpty(exportMeta.getTitle())) {
                HSSFRow row = sheet.createRow(currentRow);
                HSSFCell cell = row.createCell(0);
                cell.setCellValue(exportMeta.getTitle());
                currentRow++;
            }
            if (exportMeta.getHeader() != null && !exportMeta.getHeader().isEmpty()) {
                for (int i = 0; i < exportMeta.getHeader().size(); i++) {
                    String header = exportMeta.getHeader().get(i);
                    HSSFRow row = sheet.createRow(currentRow);
                    HSSFCell cell = row.createCell(i);
                    cell.setCellValue(header);
                    currentRow++;
                }
            }
            if (exportMeta.getCollection() != null && exportMeta.getColumns() != null) {
                HSSFRow row = sheet.createRow(currentRow);
                int colIndex = 0;
                if (exportMeta.isSequence()) {
                    HSSFCell cell = row.createCell(colIndex);
                    cell.setCellStyle(sequenceCellStyle);
                    cell.setCellValue("#");
                    colIndex++;
                }
                for (int i = 0; i < exportMeta.getColumns().size(); i++) {
                    ExcelColumnMeta column = exportMeta.getColumns().get(i);
                    HSSFCell cell = row.createCell(colIndex + i);
                    cell.setCellValue(column.getTitle());
                    cell.setCellStyle(headCellStyle);
                    sheet.setColumnWidth(colIndex + i, (short) (column.getWidth() * 35.7));
                }
                currentRow++;
                int rowIndex = 0;
                for (Object item : exportMeta.getCollection()) {
                    row = sheet.createRow(currentRow);
                    HSSFCell cell;
                    colIndex = 0;
                    if (exportMeta.isSequence()) {
                        cell = row.createCell(colIndex);
                        cell.setCellValue(new HSSFRichTextString(String.valueOf(rowIndex + 1)));
                        cell.setCellStyle(headCellStyle);
                        colIndex++;
                    }
                    for (int i = 0; i < exportMeta.getColumns().size(); i++) {
                        cell = row.createCell(colIndex + i);
                        Object value = ClassUtilities.getPropertyValue(item, exportMeta.getColumns().get(i).getField());
                        if (value == null) {
                            cell.setCellValue("");
                        } else {
                            setValue(cell, value);
                            if (Timestamp.class.isAssignableFrom(value.getClass())) {
                                cell.setCellStyle(dateTimeCellStyle);
                            } else if (Date.class.isAssignableFrom(value.getClass())) {
                                cell.setCellStyle(dateCellStyle);
                            }
                        }
                    }
                    currentRow++;
                    rowIndex++;
                }
            }
            workbook.write(fis);
        } catch (IOException e) {
//            tracer.logError(e);
        }
    }

    private static void setValue(HSSFCell cell, Object obj) {
        if (obj == null) {
            cell.setCellValue(new HSSFRichTextString());
        } else if (obj instanceof String) {
            cell.setCellValue(new HSSFRichTextString(obj.toString()));
        } else if (obj instanceof Boolean) {
            cell.setCellValue((Boolean) obj);
        } else if ((obj instanceof Double)) {
            cell.setCellValue((Double) obj);
        } else if (obj instanceof BigDecimal) {
            Double value = ((BigDecimal) obj).doubleValue();
            cell.setCellValue(value);
        } else if (Timestamp.class.isAssignableFrom(obj.getClass())) {
            cell.setCellValue(new HSSFRichTextString(DateUtilities.formatDate((Date) obj, "yyyy-MM-dd HH:mm:ss")));
        } else if (Date.class.isAssignableFrom(obj.getClass())) {
            cell.setCellValue((Date) obj);
        } else if (obj instanceof Long) {
            cell.setCellValue((Long) obj);
        } else if (obj instanceof Integer) {
            cell.setCellValue((Integer) obj);
        } else {
            throw new UnsupportedOperationException("Unexpect Data type:" + obj.getClass());
        }
    }

//    public static void merge(InputStream[] inputs, OutputStream out) throws IOException, NoSuchFieldException, IllegalAccessException {
//        try {
//            if (inputs == null) {
//                throw new IllegalArgumentException("At least one input stream should exist.");
//            }
//            HSSFWorkbook workbook = getWorkbook(inputs[0]);
//            List<Sheet> sheets = new ArrayList<Sheet>();
//            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
//                sheets.add(workbook.getSheetAt(i));
//            }
//            if (sheets == null || sheets.size() == 0) {
//                throw new IllegalArgumentException("At least one sheet should exist.");
//            }
//            HSSFSheet rootSheet = (HSSFSheet) sheets.get(sheets.size() - 1);
//            int rootRows = rootSheet.getLastRowNum() + 1;
//            Map<Integer, Integer> map = new HashMap(10000);
//            for (int i = 1; i < inputs.length; i++) {
//                List<Record> records = getRecords(inputs[i]);
//                int rowsOfCurXls = 0;
//                for (Iterator itr = records.iterator(); itr.hasNext(); ) {
//                    Record record = (Record) itr.next();
//                    if (record.getSid() == RowRecord.sid) {
//                        RowRecord rowRecord = (RowRecord) record;
//                        rowRecord.setRowNumber(rootRows + rowRecord.getRowNumber());
//                        getInternalSheet(rootSheet).addRow(rowRecord);
//                        rowsOfCurXls++;
//                    } else if (record.getSid() == SSTRecord.sid) {
//                        SSTRecord sstRecord = (SSTRecord) record;
//                        for (int j = 0; j < sstRecord.getNumUniqueStrings(); j++) {
//                            int index = workbook.addSSTString(sstRecord.getString(j).toString());
//                            map.put(Integer.valueOf(j), Integer.valueOf(index));
//                        }
//                    } else if (record.getSid() == LabelSSTRecord.sid) {
//                        LabelSSTRecord label = (LabelSSTRecord) record;
//                        label.setSSTIndex(map.get(Integer.valueOf(label.getSSTIndex())));
//                    }
//                    if (record instanceof CellValueRecordInterface) {
//                        CellValueRecordInterface cell = (CellValueRecordInterface) record;
//                        int cellRow = cell.getRow() + rootRows;
//                        cell.setRow(cellRow);
//                        getInternalSheet(rootSheet).addValueRecord(cellRow, cell);
//                    }
//                }
//                rootRows += rowsOfCurXls;
//            }
//            workbook.write(out);
//        } finally {
//            if (inputs != null) {
//                for (InputStream input : inputs) {
//                    if (input != null) {
//                        input.close();
//                    }
//                }
//            }
//        }
//    }

    public static HSSFWorkbook getWorkbook(InputStream is) throws IOException {
        POIFSFileSystem fs = new POIFSFileSystem(is);
        return new HSSFWorkbook(fs);
    }

    public static boolean cellBool(HSSFCell cell) {
        return cell != null && (cell.getStringCellValue().equalsIgnoreCase("y") || ObjectUtilities.booleanValueForString(cell.getStringCellValue()));
    }

    public static String cellStr(HSSFCell cell) {
        if (cell != null) {
            try {
                return cell.getStringCellValue();
            } catch (Exception ex) {
                double _v = cell.getNumericCellValue();
                String sV = String.valueOf(_v);
                if (sV.contains("E")) {
                    return String.valueOf((long) _v);
                }
                return sV;
            }
        }
        return null;
    }

    public static String cellStrWithoutScale(HSSFCell cell) {
        if (cell != null) {
            try {
                return cell.getStringCellValue();
            } catch (Exception ex) {
                double _v = cell.getNumericCellValue();
                String sV = String.valueOf(_v);
                if (sV.contains("E")) {
                    sV = String.valueOf((long) _v);
                }
                if (sV.contains(".")) {
                    sV = sV.substring(0, sV.indexOf("."));
                }
                return sV;
            }
        }
        return null;
    }

    public static String cellStr(HSSFCell cell, String type) {
        if (cell != null) {
            try {
                return cell.getStringCellValue();
            } catch (Exception ex) {
                double _v = cell.getNumericCellValue();
                if (type.equals("int")) {
                    return Integer.toString((int) _v);
                } else if (type.equals("long")) {
                    return Long.toString((long) _v);
                }
                return String.valueOf(_v);
            }
        }
        return null;
    }

    private static List<Record> getRecords(InputStream input) throws IOException {
        POIFSFileSystem poifs = new POIFSFileSystem(input);
        InputStream stream = poifs.getRoot().createDocumentInputStream("Workbook");
        return org.apache.poi.hssf.record.RecordFactory.createRecords(stream);
    }

    private static InternalSheet getInternalSheet(HSSFSheet sheet) throws NoSuchFieldException, IllegalAccessException {
        Field field = HSSFSheet.class.getDeclaredField("_sheet");
        field.setAccessible(true);
        return (InternalSheet) field.get(sheet);
    }

}
