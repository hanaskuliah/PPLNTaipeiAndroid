package com.acnlab.altbeacon.pplntaipei;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.acnlab.altbeacon.pplntaipei.Db.Adapter;
import com.acnlab.altbeacon.pplntaipei.Db.DatabaseHelper;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.acnlab.altbeacon.pplntaipei.OpeningActivity.KPPS_NO;
import static com.acnlab.altbeacon.pplntaipei.OpeningActivity.PREFERENCE;
import static com.acnlab.altbeacon.pplntaipei.OpeningActivity.PREF_NAME;

public class ListAllActivity extends AppCompatActivity {

    public static Adapter adapter;
    RecyclerView recyclerView;
    DatabaseHelper databaseHelper;
    private TextView totalJumlah;
    final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private SharedPreferences mSharedPreferences;
    private String fileNameSaved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_kirim);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        totalJumlah=(TextView)findViewById(R.id.total_list);

        databaseHelper = new DatabaseHelper(this);
        mSharedPreferences = getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Generate dan Share CSV", Toast.LENGTH_SHORT).show();
                checkPermissions();
                exportDB();
                //ShareFile();
//                for(int a=0;a<5000;a++){
//                    databaseHelper.insertData("hanas","hendara","aaaa");
//                }
//                Intent intent = new Intent(MainActivity.this,AddData.class);
//                startActivity(intent);
//                finish();
            }
        });
        adapter = new Adapter(this,databaseHelper.getAllDatas(DatabaseHelper.DATA_SEMUA),databaseHelper,Adapter.DATA_SEMUA);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        int totalDataKirim=databaseHelper.getDataCount(DatabaseHelper.DATA_SEMUA);
        totalJumlah.setText("Data Total: "+totalDataKirim);
    }

    public static void notifyAdapter(){
        adapter.notifyDataSetChanged();
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
            ShareFile();
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


    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Toast.makeText(getApplicationContext(), "Permission Granted, Silahkan tekan lagi untuk simpan", Toast.LENGTH_LONG).show();
                } else {
                    // Permission Denied
                    Toast.makeText(getApplicationContext(), "WRITE_EXTERNAL_STORAGE Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    private void ShareFile() {
        File exportDir = new File(Environment.getExternalStorageDirectory(),"/pplntaipei/");
        String fileName = fileNameSaved+".csv";
        File sharingGifFile = new File(exportDir, fileName);
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.setType("text/comma_separated_values/csv");
        Uri uri = Uri.fromFile(sharingGifFile);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        //startActivity(Intent.createChooser(shareIntent, "Share CSV"));
        startActivity(Intent.createChooser(shareIntent, "Share CSV"));

    }

}
