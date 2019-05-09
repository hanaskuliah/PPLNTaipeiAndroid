package com.acnlab.altbeacon.pplntaipei;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.acnlab.altbeacon.pplntaipei.Db.Adapter;
import com.acnlab.altbeacon.pplntaipei.Db.DatabaseHelper;

public class ListKirimActivity extends AppCompatActivity {

    public static Adapter adapter;
    RecyclerView recyclerView;
    DatabaseHelper databaseHelper;
    private TextView totalJumlah;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_kirim);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        totalJumlah=(TextView)findViewById(R.id.total_list);

        databaseHelper = new DatabaseHelper(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this,AddData.class);
//                startActivity(intent);
//                finish();
            }
        });
        adapter = new Adapter(this,databaseHelper.getAllDatas(DatabaseHelper.DATA_KIRIM),databaseHelper,Adapter.DATA_KIRIM);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        int totalDataKirim=databaseHelper.getDataCount(DatabaseHelper.DATA_KIRIM);
        totalJumlah.setText("Data Terkirim: "+totalDataKirim);
    }

    public static void notifyAdapter(){
        adapter.notifyDataSetChanged();
    }


}
