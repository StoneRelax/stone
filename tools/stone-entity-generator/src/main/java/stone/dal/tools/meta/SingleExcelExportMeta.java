package stone.dal.tools.meta;

import java.util.List;

/**
 * Component:
 * Description:
 * User: feng.xie
 * Date: 29/06/11
 */
public class SingleExcelExportMeta {
    private List<String> header;
    private List<ExcelColumnMeta> columns;
    private List<Object> collection;
    private String title;
    private String fileName;
    private String dateFormat;
    private boolean sequence;

    public boolean isSequence() {
        return sequence;
    }

    public void setSequence(boolean sequence) {
        this.sequence = sequence;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public void setColumns(List<ExcelColumnMeta> columns) {
        this.columns = columns;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<String> getHeader() {
        return header;
    }

    public void setHeader(List<String> header) {
        this.header = header;
    }

    public List getCollection() {
        return collection;
    }

    public void setCollection(List collection) {
        this.collection = collection;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ExcelColumnMeta> getColumns() {
        return columns;
    }
}
