package com.acnlab.altbeacon.pplntaipei;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.acnlab.altbeacon.pplntaipei.Db.DatabaseHelper;
import com.google.android.gms.vision.barcode.Barcode;
import com.notbytes.barcode_reader.BarcodeReaderFragment;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, BarcodeReaderFragment.BarcodeReaderListener {
    private static final int BARCODE_READER_ACTIVITY_REQUEST = 1208;
    private TextView mTvResultUuid, mTvResultPos;
    private TextView mTvResultHeader;

    private String uuidCode;
    private String posCode;

    private BarcodeReaderFragment readerFragment;

    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_fragment).setOnClickListener(this);
        mTvResultUuid = findViewById(R.id.tv_result_uuid);
        mTvResultPos = findViewById(R.id.tv_result_pos);
        mTvResultHeader = findViewById(R.id.tv_result_head);
        uuidCode="";
        posCode="";
        db = new DatabaseHelper(this);
        addBarcodeReaderFragment();

    }

    private void addBarcodeReaderFragment() {
        readerFragment = BarcodeReaderFragment.newInstance(true, false, View.VISIBLE);
        readerFragment.setListener(this);
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fm_container, readerFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_fragment:
                addBarcodeReaderFragment();
                break;
        }
    }


    @Override
    public void onScanned(Barcode barcode) {
        Toast.makeText(this, barcode.rawValue, Toast.LENGTH_SHORT).show();
        String tempResult;
        tempResult=barcode.rawValue;
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        if(tempResult.substring(0,3).equals("POS") && tempResult.substring(3,7).matches("[0-9]+") && tempResult.length()>10){
            uuidCode=barcode.rawValue;
            if(!db.cekDataKirim(uuidCode,posCode)){
                uuidCode=uuidCode+" sudah ada";
                alertDialogDemo();
                readerFragment.pauseScanning();

            }else{
                mTvResultHeader.setText("Kedua, Scan Tracking POS "+tempResult);
                mTvResultHeader.setTextColor(getResources().getColor(R.color.colorAccent));
                if (vibrator.hasVibrator()) {
                    vibrator.vibrate(500); // for 500 ms
//                readerFragment.playBeep();
                }
            }


        }else{
            if(!uuidCode.equals("")&& tempResult.length()>13){
                if(!db.cekDataKirim(uuidCode,posCode)){
                    uuidCode=uuidCode+" ok";
                    posCode=posCode+" sudah ada ";
                    alertDialogDemo();
                    readerFragment.pauseScanning();

                }else{
                    mTvResultUuid.setText(uuidCode);
                    mTvResultPos.setText(posCode);

                    posCode=barcode.rawValue;
                    vibrator.vibrate(500); // for 500 ms
                    //             readerFragment.playBeep();
                }

            }
        }

        if(!posCode.equals("") && !uuidCode.equals("")){
            //add to database
            if(db.cekDataKirim(uuidCode,posCode)){
                promptDialogDemo();
            }else{
               // Toast.makeText(getApplicationContext(), "Data sudah ada didatabase, silahkan scan ulang: ", Toast.LENGTH_LONG).show();
                uuidCode=uuidCode+" ok";
                posCode=posCode+" sudah ada ";
                alertDialogDemo();
                readerFragment.pauseScanning();
            }


            if (vibrator.hasVibrator()) {
                vibrator.vibrate(500); // for 500 ms
            }

        }
    }

    private void alertDialogDemo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Data sudah di scan!");
        builder.setMessage(" \n"+uuidCode+" \n "+posCode+" \n ");
        builder.setCancelable(true);
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                readerFragment.resumeScanning();
                posCode="";
                uuidCode="";
                mTvResultUuid.setText(uuidCode);
                mTvResultPos.setText(posCode);
                mTvResultHeader.setText(getString(R.string.title_scan));
            }
        });
        builder.show();
    }

    /**
     * Prompt dialog demo
     * it is used when you want to capture user input
     */
    private void promptDialogDemo() {
        readerFragment.pauseScanning();
        final EditText edtTextUser = new EditText(this);
        final EditText edtTextPos = new EditText(this);

        LinearLayout layout=new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10,0,10,0);
        layout.setLayoutParams(params);
        layout.setOrientation(LinearLayout.VERTICAL);

        edtTextPos.setText(posCode);
        edtTextUser.setText(uuidCode);

        layout.addView(edtTextUser,params);
        layout.addView(edtTextPos,params);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi");
        builder.setMessage("Yakin sudah benar?");
        builder.setCancelable(false);
        builder.setView(layout);

        builder.setNeutralButton("Simpan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                long id = db.insertData(edtTextUser.getText().toString(),edtTextPos.getText().toString(),"KIRIM");
                Toast.makeText(getApplicationContext(), "Data tersimpan id: "+id+"\n "+edtTextUser.getText().toString()+"\n "+edtTextPos.getText().toString(), Toast.LENGTH_LONG).show();
                readerFragment.resumeScanning();
                posCode="";
                uuidCode="";
                mTvResultUuid.setText(uuidCode);
                mTvResultPos.setText(posCode);
                mTvResultHeader.setText(getString(R.string.title_scan));
            }
        });
        builder.setNegativeButton("Ulangi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                readerFragment.resumeScanning();
                posCode="";
                uuidCode="";
                mTvResultUuid.setText(uuidCode);
                mTvResultPos.setText(posCode);
                mTvResultHeader.setText(getString(R.string.title_scan));

            }
        });
        builder.show();
    }

    @Override
    public void onScannedMultiple(List<Barcode> barcodes) {

    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {

    }

    @Override
    public void onScanError(String errorMessage) {

    }

    @Override
    public void onCameraPermissionDenied() {
        Toast.makeText(this, "Camera permission denied!", Toast.LENGTH_LONG).show();
    }
}
