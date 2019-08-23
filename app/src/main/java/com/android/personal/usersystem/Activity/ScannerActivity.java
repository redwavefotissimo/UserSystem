package com.android.personal.usersystem.Activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.personal.usersystem.R;
import com.android.personal.usersystem.UserSystemMySqlAPI.UserAttachmentInfo;
import com.android.personal.usersystem.UserSystemMySqlAPI.UserSystemMySQLAPIResponse;
import com.android.personal.usersystem.UserSystemMySqlAPI.UserSystemMySqlAPI;
import com.android.personal.usersystem.data.SharedStaticClass;
import com.common.AbstractOrInterface.ClassInfoAnnotation;
import com.common.AbstractOrInterface.WriterManager;
import com.common.AbstractOrInterface.WriterManagerInfo;
import com.common.BoxNetAPI.BoxItemInfo;
import com.common.BoxNetAPI.BoxItemSimpleInfo;
import com.common.BoxNetAPI.BoxNetAPI;
import com.common.BoxNetAPI.BoxUploadedFileInfo;
import com.common.FileOutputAPI;
import com.common.Utils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.io.File;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerActivity extends BaseActivity implements ZXingScannerView.ResultHandler {

    RelativeLayout scannerView;
    Button uploadBTN, reSCanBTN;
    TextView scanTextValue;
    RadioGroup exportFormatRDOGRP;
    ProgressBar progressBar;

    ZXingScannerView mScannerView;
    Bitmap selfGeneratedBarcode;
    UserAttachmentInfo userAttachmentInfo;

    Handler handler;
    HandlerThread handlerThread;
    UserSystemMySqlAPI UserSystemMySqlAPI;
    BoxNetAPI boxNetAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        TAG = "ScannerActivity";

        try {

            progressBar = (ProgressBar) this.findViewById(R.id.progressBar);

            handlerThread = new HandlerThread("backgroundThread");
            handlerThread.start();

            handler = new Handler(handlerThread.getLooper());
            UserSystemMySqlAPI = new UserSystemMySqlAPI(this);

            uploadBTN = (Button) this.findViewById(R.id.uploadBTN);
            uploadBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //scanTextValue.setText("test");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                enableProgress(true);

                                Thread.sleep(1000 * 2); // fake delay

                                //selfGeneratedBarcode = Utils.encodeAsBitmap("test", BarcodeFormat.QR_CODE, 600, 300);

                                RadioButton selectedOutput = (RadioButton) ScannerActivity.this.findViewById(exportFormatRDOGRP.getCheckedRadioButtonId());
                                WriterManager outputAPI = (WriterManager) selectedOutput.getTag();
                                ClassInfoAnnotation classInfoAnnotation = outputAPI.getClass().getAnnotation(ClassInfoAnnotation.class);
                                File barcodeFile = new File(ScannerActivity.super.fileSaveLocation + "/barcode.png");
                                try {
                                    userAttachmentInfo = new UserAttachmentInfo();
                                    generateOutputFile(barcodeFile, outputAPI);
                                    userAttachmentInfo.attachmentId = uploadFileToBox(outputAPI.getFileLoc());
                                    userAttachmentInfo.userRecId = SharedStaticClass.userInfo.recId;
                                    postToDB();
                                    outputAPI.deleteFile();
                                } catch (Exception ex) {
                                    toastMessage(ex.toString());
                                } finally {
                                    barcodeFile.delete();
                                }
                            } catch (Exception ex) {
                                if(boxNetAPI != null){
                                    try {
                                        BoxItemInfo BoxItemInfo = boxNetAPI.getItemInfo(userAttachmentInfo.attachmentId);
                                        BoxItemSimpleInfo BoxItemSimpleInfo = new BoxItemSimpleInfo();
                                        BoxItemSimpleInfo.id = BoxItemInfo.id;
                                        BoxItemSimpleInfo.etag = BoxItemInfo.etag;
                                        boxNetAPI.deleteItem(BoxItemSimpleInfo);
                                    }
                                    catch(Exception ex1){
                                        toastMessage(ex1.toString());
                                    }
                                }
                                toastMessage(ex.toString());
                            } finally {
                                enableProgress(false);
                                ScannerActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mScannerView.resumeCameraPreview(ScannerActivity.this);
                                        scanTextValue.setText("");
                                    }
                                });

                            }
                        }
                    });
                }
            });

            reSCanBTN = (Button) this.findViewById(R.id.reSCanBTN);
            reSCanBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mScannerView.resumeCameraPreview(ScannerActivity.this);
                    scanTextValue.setText("");
                }
            });

            exportFormatRDOGRP = (RadioGroup) this.findViewById(R.id.exportFormatRDOGRP);

            scanTextValue = (TextView) this.findViewById(R.id.scanTextValue);

            mScannerView = new ZXingScannerView(this);
            scannerView = (RelativeLayout) findViewById(R.id.scannerView);
            scannerView.addView(mScannerView);
            mScannerView.setResultHandler(this);
            mScannerView.setClickable(true);
            mScannerView.startCamera();
            mScannerView.setSoundEffectsEnabled(true);
            mScannerView.setAutoFocus(true);

            createOutputFormatSelection();
        }
        catch(Exception e){
            toastMessage(e.toString());
        }
    }

    @Override
    public void handleResult(Result rawResult) {
        scanTextValue.setText(rawResult.getText());
        try {
            selfGeneratedBarcode = Utils.encodeAsBitmap(rawResult.getText(), rawResult.getBarcodeFormat(), 600, 300);
        }
        catch(Exception ex){
            toastMessage(ex.toString());
        }
    }

    private void createOutputFormatSelection(){

        exportFormatRDOGRP.removeAllViews();

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        File saveFileLocation = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String fileName = "ScannerResult";
        for(WriterManager fileOutputAPI : FileOutputAPI.getList(saveFileLocation, fileName)){
            RadioButton rb = new RadioButton(this);
            rb.setTag(fileOutputAPI);
            ClassInfoAnnotation classInfoAnnotation = fileOutputAPI.getClass().getAnnotation(ClassInfoAnnotation.class);
            rb.setText(classInfoAnnotation.name());
            exportFormatRDOGRP.addView(rb, p);

            if(this.findViewById(exportFormatRDOGRP.getCheckedRadioButtonId()) == null){
                exportFormatRDOGRP.check(rb.getId());
            }
        }

    }

    private void enableControls(final boolean enable){
        ScannerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                uploadBTN.setEnabled(enable);
                reSCanBTN.setEnabled(enable);
            }
        });
    }

    private void enableProgress(final boolean enable){
        ScannerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(enable){
                    progressBar.setVisibility(View.VISIBLE);
                }else{
                    progressBar.setVisibility(View.GONE);
                }
                progressBar.setEnabled(enable);
                enableControls(!enable);
            }
        });
    }

    private void generateOutputFile(File fileLoc, WriterManager outputAPI) throws Exception{
        Utils.saveByteToFile(Utils.convertBitmapToByte(selfGeneratedBarcode), fileLoc);

        WriterManagerInfo[] rowInfo = new WriterManagerInfo[2];
        rowInfo[0] = new WriterManagerInfo();
        rowInfo[0].value = "Barcode Image";
        rowInfo[1] = new WriterManagerInfo();
        rowInfo[1].value = fileLoc.getAbsolutePath();
        rowInfo[1].format = WriterManagerInfo.DataFormat.Image;
        outputAPI.insertRow(rowInfo);

        rowInfo = new WriterManagerInfo[2];
        rowInfo[0] = new WriterManagerInfo();
        rowInfo[0].value = "Scan Text";
        rowInfo[1] = new WriterManagerInfo();
        rowInfo[1].value = scanTextValue.getText().toString();
        outputAPI.insertRow(rowInfo);

        outputAPI.write();
    }

    private String uploadFileToBox(File fileLoc) throws Exception{
        if(boxNetAPI == null){
            boxNetAPI = new BoxNetAPI(this);
        }

        BoxUploadedFileInfo BoxUploadedFileInfo = boxNetAPI.updloadFile(fileLoc, SharedStaticClass.userInfo.attachementFolderId);

        if(BoxUploadedFileInfo.entries != null && BoxUploadedFileInfo.entries.length > 0){

            boxNetAPI.setFileItemAsSharable(BoxUploadedFileInfo.entries[0].id);

            return BoxUploadedFileInfo.entries[0].id;
        }
        return "";
    }

    private void postToDB() throws Exception{
        UserSystemMySQLAPIResponse UserSystemMySQLAPIResponse = UserSystemMySqlAPI.insertUserAttachment(this.userAttachmentInfo);

        if(UserSystemMySQLAPIResponse.statusCode != 200){
            throw new Exception(UserSystemMySQLAPIResponse.message);
        }
    }

}
