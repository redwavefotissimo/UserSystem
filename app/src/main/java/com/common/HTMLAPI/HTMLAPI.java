package com.common.HTMLAPI;

import com.common.WriterManager;
import com.common.WriterManagerInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import static com.common.WriterManagerInfo.ContentStyle.Bold;
import static com.common.WriterManagerInfo.ContentStyle.BoldItalic;
import static com.common.WriterManagerInfo.ContentStyle.BoldStrikeThrough;
import static com.common.WriterManagerInfo.ContentStyle.BoldUnderLine;
import static com.common.WriterManagerInfo.ContentStyle.Italic;
import static com.common.WriterManagerInfo.ContentStyle.StrikeThrough;
import static com.common.WriterManagerInfo.ContentStyle.UnderLine;


public class HTMLAPI extends WriterManager {

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
        htmlDocument.append("<td style='"+ this.setCSSStyle(writerManagerInfo) +"' >");
        htmlDocument.append(writerManagerInfo.value);
        htmlDocument.append("</td>");
    }

    @Override
    public void insertRow(WriterManagerInfo writerManagerInfo) {
        htmlDocument.append("<tr>");
        insertRow(writerManagerInfo, 0);
        htmlDocument.append("</tr>");
        this.row++;
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
