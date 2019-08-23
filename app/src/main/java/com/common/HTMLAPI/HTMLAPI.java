package com.common.HTMLAPI;

import android.util.Base64;
import android.util.Log;

import com.common.AbstractOrInterface.ClassInfoAnnotation;
import com.common.AbstractOrInterface.WriterManager;
import com.common.AbstractOrInterface.WriterManagerInfo;
import com.common.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import static com.common.AbstractOrInterface.WriterManagerInfo.ContentStyle.Bold;
import static com.common.AbstractOrInterface.WriterManagerInfo.ContentStyle.BoldItalic;
import static com.common.AbstractOrInterface.WriterManagerInfo.ContentStyle.BoldStrikeThrough;
import static com.common.AbstractOrInterface.WriterManagerInfo.ContentStyle.BoldUnderLine;
import static com.common.AbstractOrInterface.WriterManagerInfo.ContentStyle.Italic;
import static com.common.AbstractOrInterface.WriterManagerInfo.ContentStyle.StrikeThrough;
import static com.common.AbstractOrInterface.WriterManagerInfo.ContentStyle.UnderLine;

@ClassInfoAnnotation(name="HTML Format")
public class HTMLAPI extends WriterManager {

    final String TAG = "ExcelAPI";

    StringBuilder htmlDocument;

    public HTMLAPI(File saveFileLocation, String fileName) {
        super(saveFileLocation, fileName);
        this.fileType = ".html";
    }

    @Override
    public void init() {
        htmlDocument = new StringBuilder("<!DOCTYPE html><html><head><title>" + this.fileName + "</title></head><body><table style='border: 1px solid black;'>");
        this.row = 0;
    }

    @Override
    public void insertRow(WriterManagerInfo[] writerManagerInfos) {
        htmlDocument.append("<tr>");
        for(int col = 0; col < writerManagerInfos.length; col++){
            insertRow(writerManagerInfos[col], col);
        }
        htmlDocument.append("</tr>");
        this.row++;
    }

    @Override
    public void insertRow(WriterManagerInfo writerManagerInfo, int col) {
        if(writerManagerInfo.format == WriterManagerInfo.DataFormat.Image){
            insertCellWithImage(writerManagerInfo, col);
        }else {
            htmlDocument.append("<td style='" + this.setCSSStyle(writerManagerInfo) + "' >");
            htmlDocument.append(writerManagerInfo.value);
            htmlDocument.append("</td>");
        }
    }

    @Override
    public void insertRow(WriterManagerInfo writerManagerInfo) {
        htmlDocument.append("<tr>");
        insertRow(writerManagerInfo, 0);
        htmlDocument.append("</tr>");
        this.row++;
    }

    @Override
    public void insertCellWithImage(WriterManagerInfo writerManagerInfo, int col) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(writerManagerInfo.value);

            htmlDocument.append("<td style='" + this.setCSSStyle(writerManagerInfo) + "' >");
            htmlDocument.append("<img src=\"data:image/png;base64, " + new String(Base64.encode(Utils.toByteArray(inputStream), Base64.DEFAULT)) + "\"  alt=\"Barcode Image\" />");
            htmlDocument.append("</td>");
        }
        catch (Exception ex){
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
        htmlDocument.append("</table></body></html>");
        OutputStreamWriter outputStreamWriter = null;
        FileOutputStream fos = null;
        try{
            fos = new FileOutputStream(new File(this.saveFileLocation, this.fileName + this.fileType));
            outputStreamWriter = new OutputStreamWriter(fos);
            outputStreamWriter.write(htmlDocument.toString());
        }
        finally{
            if(outputStreamWriter != null){
                outputStreamWriter.close();
            }
            if(fos != null){
                fos.close();
            }
        }
    }

    private String setCSSStyle(WriterManagerInfo writerManagerInfo){
        return this.setStyle(writerManagerInfo.contentStyle) + this.setAlignment(writerManagerInfo.contentAlignment);
    }

    private String setAlignment(WriterManagerInfo.ContentAlignment contentAlignment){
        if(contentAlignment == WriterManagerInfo.ContentAlignment.Center){
            return "text-align: center;";
        }else if (contentAlignment == WriterManagerInfo.ContentAlignment.Right){
            return "text-align: right;";
        }else{
            return "text-align: left;";
        }
    }

    private String setStyle(WriterManagerInfo.ContentStyle contentStyle){
        StringBuilder style = new StringBuilder("");
        if(contentStyle == Bold || contentStyle == BoldItalic ||
                contentStyle == BoldStrikeThrough || contentStyle == BoldUnderLine) {
            style.append("font-weight: bold;");
        }
        if(contentStyle == Italic || contentStyle == BoldItalic) {
            style.append("font-style: italic;");
        }
        if(contentStyle == UnderLine || contentStyle == BoldUnderLine) {
            style.append("text-decoration: underline;");
        }
        if(contentStyle == StrikeThrough || contentStyle == BoldStrikeThrough) {
            style.append("text-decoration: line-through;");
        }
        return style.toString();
    }
}
