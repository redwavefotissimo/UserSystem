package com.common.DocXAPI;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.common.AbstractOrInterface.ClassInfoAnnotation;
import com.common.AbstractOrInterface.WriterManager;
import com.common.AbstractOrInterface.WriterManagerInfo;

import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import static com.common.AbstractOrInterface.WriterManagerInfo.ContentStyle.*;

@ClassInfoAnnotation(name="Document Format")
public class DocXAPI extends WriterManager {

    final String TAG = "DocXAPI";

    CustomXWPFDocument document;
    XWPFTable table;
    XWPFTableRow tableRow;

    public DocXAPI(File saveFileLocation, String fileName) {
        super(saveFileLocation, fileName);
        this.fileType = ".docx";
    }

    @Override
    public void init() {
        document = new CustomXWPFDocument();
        table = document.createTable();
        this.row = 0;
    }

    @Override
    public void insertRow(WriterManagerInfo[] writerManagerInfos) {
        if(this.row > 0){
            tableRow = table.createRow();
        }else{
            tableRow = table.getRow(0);
        }

        for(int col = 0; col < writerManagerInfos.length; col++){
            insertRow(writerManagerInfos[col], col);
        }
        this.row++;
    }

    @Override
    public void insertRow(WriterManagerInfo writerManagerInfo, int col) {
        if(writerManagerInfo.format == WriterManagerInfo.DataFormat.Image){
            insertCellWithImage(writerManagerInfo, col);
        }else {
            XWPFTableCell XWPFTableCell = null;
            if(this.row == 0){
                if(col == 0) {
                    XWPFTableCell = tableRow.getCell(0);
                } else{
                    XWPFTableCell = tableRow.addNewTableCell();
                }
            }else{
                XWPFTableCell = tableRow.getCell(col);
            }

            XWPFParagraph paragraph = XWPFTableCell.addParagraph();
            XWPFRun run = paragraph.createRun();
            run.setFontSize((int)writerManagerInfo.contentSize);
            setContentStylet(run, writerManagerInfo.contentStyle);
            paragraph.setAlignment(setAlignment(writerManagerInfo.contentAlignment));
            run.setText(writerManagerInfo.value);
            //XWPFTableCell.setParagraph(paragraph);
        }
    }

    @Override
    public void insertRow(WriterManagerInfo writerManagerInfo) {
        if(this.row > 0){
            tableRow = table.createRow();
        }else{
            tableRow = table.getRow(0);
        }

        insertRow(writerManagerInfo, 0);
        this.row++;
    }

    @Override
    public void insertCellWithImage(WriterManagerInfo writerManagerInfo, int col) {
        XWPFTableCell XWPFTableCell = null;
        if(this.row == 0){
            if(col == 0) {
                XWPFTableCell = tableRow.getCell(0);
            } else{
                XWPFTableCell = tableRow.addNewTableCell();
            }
        }else{
            XWPFTableCell = tableRow.getCell(col);
        }

        XWPFParagraph paragraph = XWPFTableCell.addParagraph();
        XWPFRun run = paragraph.createRun();
        InputStream inputStream = null;
        try {
            Bitmap yourSelectedImage = BitmapFactory.decodeFile(writerManagerInfo.value);
            inputStream = new FileInputStream(writerManagerInfo.value);

            String blipId = document.addPictureData(inputStream, Document.PICTURE_TYPE_JPEG);

            CustomXWPFRun.createPicture(blipId,document.getNextPicNameNumber(Document.PICTURE_TYPE_JPEG),
                    yourSelectedImage.getWidth(), yourSelectedImage.getHeight(), run);

            //XWPFTableCell.setParagraph(paragraph);
        } catch (Exception ex){
            Log.e(TAG, ex.toString());
        }
        finally{
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            }catch(Exception ex){
                Log.e(TAG, ex.toString());
            }
        }
    }

    @Override
    public void write() throws Exception {
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(new File(this.saveFileLocation, this.fileName + this.fileType));
            document.write(fos);
        }
        finally {
            if(fos != null) {
                fos.close();
            }
        }
    }

    private void setContentStylet(XWPFRun run, WriterManagerInfo.ContentStyle contentStyle){
        if(contentStyle == Bold || contentStyle == BoldItalic ||
                contentStyle == BoldStrikeThrough || contentStyle == BoldUnderLine) {
            run.setBold(true);
        }
        if(contentStyle == Italic || contentStyle == BoldItalic) {
            run.setItalic(true);
        }
        if(contentStyle == UnderLine || contentStyle == BoldUnderLine) {
            run.setUnderline(UnderlinePatterns.SINGLE);
        }
        if(contentStyle == StrikeThrough || contentStyle == BoldStrikeThrough) {
            run.setStrike(true);
        }
    }

    private ParagraphAlignment setAlignment(WriterManagerInfo.ContentAlignment contentAlignment){
        if(contentAlignment == WriterManagerInfo.ContentAlignment.Center){
            return ParagraphAlignment.CENTER;
        }else if (contentAlignment == WriterManagerInfo.ContentAlignment.Right){
            return ParagraphAlignment.RIGHT;
        }else{
            return ParagraphAlignment.LEFT;
        }
    }

    private int getPictureFormat(String fileName){
        String fileNameLowerCase = fileName.toLowerCase();
        if(fileNameLowerCase.endsWith(".png")){
            return XWPFDocument.PICTURE_TYPE_PNG;
        }else{
            return XWPFDocument.PICTURE_TYPE_JPEG;
        }
    }
}
