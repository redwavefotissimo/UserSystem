package com.common.ExcelAPI;

import com.common.AbstractOrInterface.WriterManager;
import com.common.AbstractOrInterface.WriterManagerInfo;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FontUnderline;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileOutputStream;

import static com.common.AbstractOrInterface.WriterManagerInfo.ContentStyle.Bold;
import static com.common.AbstractOrInterface.WriterManagerInfo.ContentStyle.BoldItalic;
import static com.common.AbstractOrInterface.WriterManagerInfo.ContentStyle.BoldStrikeThrough;
import static com.common.AbstractOrInterface.WriterManagerInfo.ContentStyle.BoldUnderLine;
import static com.common.AbstractOrInterface.WriterManagerInfo.ContentStyle.Italic;
import static com.common.AbstractOrInterface.WriterManagerInfo.ContentStyle.StrikeThrough;
import static com.common.AbstractOrInterface.WriterManagerInfo.ContentStyle.UnderLine;

public class ExcelAPI extends WriterManager {

    XSSFWorkbook workBook;
    XSSFSheet sheet;
    Row rowHeader;

    public ExcelAPI(File saveFileLocation, String fileName) {
        super(saveFileLocation, fileName);
        this.fileType = ".xlsx";
    }

    @Override
    public void init() {
        workBook = new XSSFWorkbook();
        sheet = workBook.createSheet("simple sheet");
        this.row = 0;
    }

    @Override
    public void insertRow(WriterManagerInfo writerManagerInfo){
        rowHeader = sheet.createRow(this.row);
        insertRow(writerManagerInfo, 0);
        this.row++;
    }

    @Override
    public void insertRow(WriterManagerInfo writerManagerInfo, int col){
        Cell cell = rowHeader.createCell(col);
        cell.setCellValue(writerManagerInfo.value);
        XSSFCellStyle style = workBook.createCellStyle();
        style.setAlignment(setAlignment(writerManagerInfo.contentAlignment));
        XSSFFont font = workBook.createFont();
        setContentStylet(font, writerManagerInfo.contentStyle);
        font.setFontHeight(writerManagerInfo.contentSize);
        style.setFont(font);
        cell.setCellStyle(style);
    }

    @Override
    public void insertRow(WriterManagerInfo[] writerManagerInfos){
        rowHeader = sheet.createRow(this.row);
        for(int col = 0; col < writerManagerInfos.length; col++){
            insertRow(writerManagerInfos[col], col);
        }
        this.row++;
    }

    @Override
    public void write() throws Exception {
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(new File(this.saveFileLocation, this.fileName + this.fileType));
            workBook.write(fos);
        }
        finally {
            if(fos != null) {
                fos.close();
            }
        }
    }

    private HorizontalAlignment setAlignment(WriterManagerInfo.ContentAlignment contentAlignment){
        if(contentAlignment == WriterManagerInfo.ContentAlignment.Center){
            return HorizontalAlignment.CENTER;
        }else if (contentAlignment == WriterManagerInfo.ContentAlignment.Right){
            return HorizontalAlignment.RIGHT;
        }else{
            return HorizontalAlignment.LEFT;
        }
    }

    private void setContentStylet(XSSFFont font, WriterManagerInfo.ContentStyle contentStyle){
        if(contentStyle == Bold || contentStyle == BoldItalic ||
                contentStyle == BoldStrikeThrough || contentStyle == BoldUnderLine) {
            font.setBold(true);
        }
        if(contentStyle == Italic || contentStyle == BoldItalic) {
            font.setItalic(true);
        }
        if(contentStyle == UnderLine || contentStyle == BoldUnderLine) {
            font.setUnderline(FontUnderline.SINGLE);
        }
        if(contentStyle == StrikeThrough || contentStyle == BoldStrikeThrough) {
            font.setStrikeout(true);
        }
    }
}
