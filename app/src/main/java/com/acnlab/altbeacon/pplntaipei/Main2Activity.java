package com.acnlab.altbeacon.pplntaipei;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.acnlab.altbeacon.pplntaipei.Db.DatabaseHelper;
import com.google.android.gms.vision.barcode.Barcode;
import com.notbytes.barcode_reader.BarcodeReaderFragment;

import java.util.List;

public class Main2Activity extends AppCompatActivity  implements View.OnClickListener, BarcodeReaderFragment.BarcodeReaderListener {
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

        if(tempResult.substring(0,3).equals("POS") && tempResult.substring(3,7).matches("[0-9]+") && tempResult.length()>10){
            uuidCode=barcode.rawValue;
            mTvResultHeader.setText("Data diterima sudah dicatat");
        }
        mTvResultUuid.setText(uuidCode);
        mTvResultPos.setText(posCode);

        if(!uuidCode.equals("")){
            //add to database
            if(db.cekDataTerima(uuidCode)){
                promptDialogDemo();
            }else{
                // Toast.makeText(getApplicationContext(), "Data sudah ada didatabase, silahkan scan ulang: ", Toast.LENGTH_LONG).show();

                alertDialogDemo();
                readerFragment.pauseScanning();
            }
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator.hasVibrator()) {
                vibrator.vibrate(500); // for 500 ms
            }

        }
    }
    private void alertDialogDemo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Data ini sudah di scan !");
        builder.setMessage("Silahkan di scan ulang");
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

    @Override
    public void onScannedMultiple(List<Barcode> barcodes) {

    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {

    }

    @Override
    public void onScanError(String errorMessage) {

    }

    private void promptDialogDemo() {
        readerFragment.pauseScanning();
        final EditText edtTextUser = new EditText(this);

        LinearLayout layout=new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10,0,10,0);
        layout.setLayoutParams(params);
        layout.setOrientation(LinearLayout.VERTICAL);
        edtTextUser.setText(uuidCode);
        layout.addView(edtTextUser,params);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi");
        builder.setMessage("Yakin sudah benar?");
        builder.setCancelable(false);
        builder.setView(layout);

        builder.setNeutralButton("Simpan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                long id = db.insertData(edtTextUser.getText().toString(),"".toString(),"TERIMA");
                Toast.makeText(getApplicationContext(), "Data tersimpan id: "+edtTextUser.getText().toString()+"\n", Toast.LENGTH_LONG).show();
                readerFragment.resumeScanning();
                posCode="";
                uuidCode="";
                mTvResultUuid.setText(uuidCode);
                mTvResultPos.setText(posCode);
                mTvResultHeader.setText("");

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
                mTvResultHeader.setText("");

            }
        });
        builder.show();
    }

    @Override
    public void onCameraPermissionDenied() {
        Toast.makeText(this, "Camera permission denied!", Toast.LENGTH_LONG).show();
    }
}
