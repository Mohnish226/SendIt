package com.mohdeva.sendit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Environment;

import com.google.zxing.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class receiveActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    EditText editTextAddress,editTextFileName;
    Button buttonConnect;
    TextView textPort;
    static final int SocketServerPORT = 8080;
    private ZXingScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);


        editTextAddress = (EditText) findViewById(R.id.address);
        editTextFileName = (EditText) findViewById(R.id.filename);

        textPort = (TextView) findViewById(R.id.port);
        textPort.setText("port: " + SocketServerPORT);
        buttonConnect = (Button) findViewById(R.id.connect);

        buttonConnect.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                ClientRxThread clientRxThread =
                        new ClientRxThread(
                                editTextAddress.getText().toString(),editTextFileName.getText().toString(),
                                SocketServerPORT);
                clientRxThread.start();
            }});

    }
    private class ClientRxThread extends Thread {
        String dstAddress;
        int dstPort;
        String fileName;

        ClientRxThread(String address,String FileName, int port) {
            dstAddress = address;
            dstPort = port;
            fileName = FileName;
            //Toast.makeText(receiveActivity.this, dstAddress +"\n"+fileName, Toast.LENGTH_LONG).show();
        }

        @Override
        public void run() {
            Socket socket = null;

            try {
                socket = new Socket(dstAddress, dstPort);
                //Toast.makeText(receiveActivity.this, dstAddress+"\n"+fileName, Toast.LENGTH_LONG).show();
                File file = new File(
                        Environment.getExternalStorageDirectory(),
                        "SendIt/"+fileName);

                byte[] bytes = new byte[1024];
                InputStream is = socket.getInputStream();
                FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                int bytesRead = is.read(bytes, 0, bytes.length);
                bos.write(bytes, 0, bytesRead);
                bos.close();
                socket.close();

                receiveActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(receiveActivity.this,
                                "Finished",
                                Toast.LENGTH_LONG).show();
                    }});

            } catch (IOException e) {

                e.printStackTrace();

                final String eMsg = "Something wrong: " + e.getMessage();
                receiveActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(receiveActivity.this,
                                eMsg,
                                Toast.LENGTH_LONG).show();
                    }});

            } finally {
                if(socket != null){
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    public void QrScanner(View view){
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();         // Start camera
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.e("handler", rawResult.getText()); // Prints scan results
        Log.e("handler", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode)
        String[] fromScanner=rawResult.getText().toString().split("::");
        //Toast.makeText(receiveActivity.this, fromScanner[0]+"\n"+fromScanner[1], Toast.LENGTH_LONG).show();
        editTextAddress.setText(fromScanner[0]);
        editTextFileName.setText(fromScanner[1]);
        buttonConnect.performClick();
    }
    @Override
    public void onBackPressed() {
        this.finish();
        return;
    }
}