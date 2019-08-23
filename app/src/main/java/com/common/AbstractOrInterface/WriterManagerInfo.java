package com.common.AbstractOrInterface;

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

    public static enum DataFormat{
        Text,
        Image
    }

    public WriterManagerInfo(){
        this.contentAlignment = ContentAlignment.Left;
        this.contentStyle = ContentStyle.Normal;
        this.contentSize = 12.0D;
        this.format = DataFormat.Text;
        this.value = "";
    }

    public ContentAlignment contentAlignment;
    public ContentStyle contentStyle;
    public String value;
    public DataFormat format;
    public double contentSize;

}
