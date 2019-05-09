package com.acnlab.altbeacon.pplntaipei;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.acnlab.altbeacon.pplntaipei.Db.DatabaseHelper;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.acnlab.altbeacon.pplntaipei.OpeningActivity.KPPS_NO;
import static com.acnlab.altbeacon.pplntaipei.OpeningActivity.PREFERENCE;
import static com.acnlab.altbeacon.pplntaipei.OpeningActivity.PREF_NAME;
import static com.acnlab.altbeacon.pplntaipei.OpeningActivity.PREF_SKIP_LOGIN;

public class RegisterKpps extends AppCompatActivity {

    private EditText mName,mPasswd;
    private Button mRegisterBtn,mResetBackupBtn;
    private String Name, KppsNo;
    public static final String PREFERENCE= "preference";
    public static final String PREF_NAME = "name";
    public static final String KPPS_NO = "kpps_no";

    final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private SharedPreferences mSharedPreferences;
    private String fileNameSaved;

    DatabaseHelper databaseHelper;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_kpps);
        mName = (EditText)findViewById(R.id.txt_name);
        mPasswd = (EditText)findViewById(R.id.txt_kpps_no);
        mRegisterBtn = (Button)findViewById(R.id.registerBtn);
        mResetBackupBtn=(Button)findViewById(R.id.btn_reset);
        mResetBackupBtn.setVisibility(View.INVISIBLE);

        mSharedPreferences = getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);

        if(!validUserData() && mSharedPreferences.contains(PREF_NAME) && mSharedPreferences.contains(KPPS_NO)){

            mName.setText(mSharedPreferences.getString(PREF_NAME,""));
            int kppsnomor;
            kppsnomor=mSharedPreferences.getInt(KPPS_NO,0);
            mPasswd.setText(""+kppsnomor);
            mRegisterBtn.setText("UPDATE PROFILE");

            databaseHelper=new DatabaseHelper(this);
            if(databaseHelper.haveDB(this)){
                mResetBackupBtn.setVisibility(View.VISIBLE);
            }
            mResetBackupBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkPermissions();
                    exportDB();
                }
            });


        }

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Selamat menggunakan",Toast.LENGTH_SHORT).show();
                if(validUserData()){
                    SharedPreferences mSharedPreference = getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
                    SharedPreferences.Editor mEditor = mSharedPreference.edit();
                    mEditor.putString(PREF_NAME,Name);
                    mEditor.putInt(KPPS_NO, Integer.parseInt(KppsNo));
                    mEditor.apply();
                    Intent intent = new Intent(RegisterKpps.this,HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }

    private boolean validUserData() {
        Name = mName.getText().toString().trim();
        KppsNo = mPasswd.getText().toString().trim();
        return !(Name.isEmpty() || KppsNo.isEmpty());
    }

    private void exportDB() {

        File exportDir = new File(Environment.getExternalStorageDirectory(), "");
        File dir = new File(exportDir.getAbsolutePath() + "/pplntaipei");
        if (!dir.exists())
        {
            dir.mkdirs();
        }

        //penamaan
        String fileNameTimeStamp = new SimpleDateFormat("MM-dd-HH-mm-ss").format(new Date());
        if (mSharedPreferences.contains(PREF_NAME) && mSharedPreferences.contains(KPPS_NO)) {
            fileNameTimeStamp= mSharedPreferences.getString(PREF_NAME,"")+fileNameTimeStamp;
            fileNameTimeStamp=mSharedPreferences.getInt(KPPS_NO,0)+fileNameTimeStamp;
        }

        File file = new File(dir, fileNameTimeStamp+".csv");
        fileNameSaved=fileNameTimeStamp;
        try
        {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));

            Cursor curCSV = databaseHelper.raw();
            csvWrite.writeNext(curCSV.getColumnNames());
            int xx=0;
            while(curCSV.moveToNext())
            {
                //Which column you want to exprort
                xx++;
                String arrStr[] ={String.valueOf(xx),curCSV.getString(1), curCSV.getString(2), curCSV.getString(3), curCSV.getString(4)};
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
            Toast.makeText(getApplicationContext(), fileNameTimeStamp+".csv Berhasil tersimpan di folder PPLN dalam memory", Toast.LENGTH_SHORT).show();
            confirmDialogDemo();

        }
        catch(Exception sqlEx)
        {
            Log.e("ListAllActivity", sqlEx.getMessage(), sqlEx);
        }
    }

    private void checkPermissions() {
        int hasWriteContactsPermission = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hasWriteContactsPermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
            }
            return;
        }
        //Toast.makeText(getBaseContext(), "Permission is already granted", Toast.LENGTH_LONG).show();
    }

    private void confirmDialogDemo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("PERHATIAN");
        builder.setMessage("Yakin untuk menghapus semua data?");
        builder.setCancelable(false);
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                databaseHelper.deleteAll();
                Toast.makeText(getApplicationContext(), "Semua data dihapus", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Batal dihapus", Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }


    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Toast.makeText(getApplicationContext(), "Permission Granted, Silahkan tekan lagi tombol reset", Toast.LENGTH_LONG).show();
                } else {
                    // Permission Denied
                    Toast.makeText(getApplicationContext(), "WRITE_EXTERNAL_STORAGE Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}