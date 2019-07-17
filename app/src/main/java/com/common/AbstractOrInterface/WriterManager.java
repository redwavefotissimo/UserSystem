package com.common.AbstractOrInterface;

import java.io.File;

abstract public class WriterManager {

    protected File saveFileLocation;
    protected String fileName;
    protected String fileType;
    protected int row;

    public WriterManager(File saveFileLocation, String fileName){
        this.saveFileLocation = saveFileLocation;
        this.fileName = fileName;
        init();
    }

    abstract public void init();

    abstract public void insertRow(WriterManagerInfo[] writerManagerInfos);

    abstract public void insertRow(WriterManagerInfo writerManagerInfo, int col);

    abstract public void insertRow(WriterManagerInfo writerManagerInfo);

    abstract public void write() throws Exception;
}
