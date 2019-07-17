package com.common;

public class WriterManagerInfo {

    public static enum ContentAlignment{
        Left,
        Right,
        Center
    }

    public static enum ContentStyle{
        Normal,
        Bold,
        Italic,
        BoldItalic,
        StrikeThrough,
        BoldStrikeThrough,
        UnderLine,
        BoldUnderLine
    }

    public WriterManagerInfo(){
        this.contentAlignment = ContentAlignment.Left;
        this.contentStyle = ContentStyle.Normal;
        this.contentSize = 12.0D;
        this.format = "";
        this.value = "";
    }

    public ContentAlignment contentAlignment;
    public ContentStyle contentStyle;
    public String value;
    public String format;
    public double contentSize;

}
