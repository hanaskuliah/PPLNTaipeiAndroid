package com.acnlab.altbeacon.pplntaipei;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.acnlab.altbeacon.pplntaipei.Db.DatabaseHelper;

public class ActionTerimaActivity extends AppCompatActivity {

    private Button btnScan;
    private Button btnShowData;
    private TextView txtTotal;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_menu);

        btnScan=(Button)findViewById(R.id.btn_scan);
        btnShowData=(Button)findViewById(R.id.btn_show_data);
        txtTotal=(TextView)findViewById(R.id.txt_jumlah);
        db=new DatabaseHelper(this);
        int totalDataKirim=db.getDataCount(DatabaseHelper.DATA_TERIMA);
        txtTotal.setText("Terima: "+totalDataKirim);

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActionTerimaActivity.this, Main2Activity.class);
                startActivity(intent);
            }
        });

        btnShowData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActionTerimaActivity.this, ListTerimaActivity.class);
                startActivity(intent);
            }
        });

    }

    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(ActionTerimaActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    protected void onResume() {
        super.onResume();
        if(db!=null){
            int totalDataKirim=db.getDataCount(DatabaseHelper.DATA_KIRIM);
            txtTotal.setText("Kirim: "+totalDataKirim);

        }else {
            db=new DatabaseHelper(this);
            int totalDataKirim=db.getDataCount(DatabaseHelper.DATA_KIRIM);
            txtTotal.setText("Kirim: "+totalDataKirim);
        }
    }
}
