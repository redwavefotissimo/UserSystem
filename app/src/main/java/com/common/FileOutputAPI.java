package com.common;

import com.common.AbstractOrInterface.WriterManager;
import com.common.DocXAPI.DocXAPI;
import com.common.ExcelAPI.ExcelAPI;
import com.common.HTMLAPI.HTMLAPI;
import com.common.PDFAPI.PDFAPI;

import java.io.File;
import java.util.ArrayList;

public class FileOutputAPI {
    public static ArrayList<WriterManager> getList(File saveFileLocaiton, String fileName ){
        ArrayList<WriterManager> fileOutputList = new ArrayList<WriterManager>();

        fileOutputList.add(new DocXAPI(saveFileLocaiton, fileName));
        fileOutputList.add(new ExcelAPI(saveFileLocaiton, fileName));
        fileOutputList.add(new HTMLAPI(saveFileLocaiton, fileName));
        fileOutputList.add(new PDFAPI(saveFileLocaiton, fileName));

        return fileOutputList;
    }

}
