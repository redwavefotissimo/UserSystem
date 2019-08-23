package com.common.PDFAPI;

import com.common.AbstractOrInterface.ClassInfoAnnotation;
import com.common.AbstractOrInterface.WriterManager;
import com.common.AbstractOrInterface.WriterManagerInfo;
import com.common.HTMLAPI.HTMLAPI;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

@ClassInfoAnnotation(name="PDF Format")
public class PDFAPI extends WriterManager {

    HTMLAPI HtmlApi;
    Document document;

    public PDFAPI(File saveFileLocation, String fileName) {
        super(saveFileLocation, fileName);
        this.fileType = ".pdf";
    }

    @Override
    public void init() {
        HtmlApi = new HTMLAPI(super.saveFileLocation, super.fileName);
        document = new Document();
        this.row = 0;
    }

    @Override
    public void insertRow(WriterManagerInfo[] writerManagerInfos) {
        for(WriterManagerInfo info : writerManagerInfos){
            setWriterManagerInfoImgToDirectPath(info);
        }
        HtmlApi.insertRow(writerManagerInfos);
    }

    @Override
    public void insertRow(WriterManagerInfo writerManagerInfo, int col) {
        setWriterManagerInfoImgToDirectPath(writerManagerInfo);
        HtmlApi.insertRow(writerManagerInfo, col);
    }

    @Override
    public void insertRow(WriterManagerInfo writerManagerInfo) {
        setWriterManagerInfoImgToDirectPath(writerManagerInfo);
        HtmlApi.insertRow(writerManagerInfo);
    }

    @Override
    public void insertCellWithImage(WriterManagerInfo writerManagerInfo, int col) {
        HtmlApi.insertCellWithImage(writerManagerInfo, col);
    }

    @Override
    public void write() throws Exception {
        HtmlApi.write();

        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(new File(super.saveFileLocation, super.fileName + super.fileType)));
        document.open();
        XMLWorkerHelper.getInstance().parseXHtml(writer, document, new FileInputStream(HtmlApi.getFileLoc()));
        document.close();

        HtmlApi.deleteFile();
    }

    private void setWriterManagerInfoImgToDirectPath(WriterManagerInfo info){
        if(info.format == WriterManagerInfo.DataFormat.Image){
            info.format = WriterManagerInfo.DataFormat.Text;
            info.value = "<img src=\"" + info.value + "\" />";
        }
    }
}
