package com.acnlab.altbeacon.pplntaipei;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.security.PrivateKey;
import java.util.PriorityQueue;

import static com.acnlab.altbeacon.pplntaipei.OpeningActivity.KPPS_NO;
import static com.acnlab.altbeacon.pplntaipei.OpeningActivity.PREFERENCE;
import static com.acnlab.altbeacon.pplntaipei.OpeningActivity.PREF_NAME;
import static com.acnlab.altbeacon.pplntaipei.OpeningActivity.PREF_SKIP_LOGIN;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnEditProfile;
    private Button btnKirimSS;
    private Button btnTerimaSS;
    private Button btnLihatSS;
    private TextView txtNamaUser;
    private TextView txtKppsNo;

    private static long back_pressed;

    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        txtNamaUser=(TextView)findViewById(R.id.user_home);
        txtKppsNo=(TextView)findViewById(R.id.kpps_no_home);
        btnEditProfile =(Button)findViewById(R.id.update_profile);
        btnKirimSS=(Button)findViewById(R.id.kirim_ss);
        btnTerimaSS=(Button)findViewById(R.id.terima_ss);
        btnLihatSS=(Button)findViewById(R.id.lihat_ss);
        btnEditProfile.setOnClickListener(this);
        btnKirimSS.setOnClickListener(this);
        btnTerimaSS.setOnClickListener(this);
        btnLihatSS.setOnClickListener(this);

        mSharedPreferences = getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        if (mSharedPreferences.contains(PREF_NAME)&& mSharedPreferences.contains(KPPS_NO)) {

            txtNamaUser.setText("Halo, "+mSharedPreferences.getString(PREF_NAME,""));
            int kppsnomor;
            kppsnomor=mSharedPreferences.getInt(KPPS_NO,0);
            txtKppsNo.setText("KPPS POS No. : "+kppsnomor);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.update_profile:
                Intent intent = new Intent(HomeActivity.this, RegisterKpps.class);
                startActivity(intent);
                break;
            case R.id.kirim_ss:
                Intent intentKirim = new Intent(HomeActivity.this, ActionKirimActivity.class);
                startActivity(intentKirim);
                break;
            case R.id.terima_ss:
                Intent intentTerima = new Intent(HomeActivity.this, ActionTerimaActivity.class);
                startActivity(intentTerima);
                break;
            case R.id.lihat_ss:
                Intent intentLihat = new Intent(HomeActivity.this, ListAllActivity.class);
                startActivity(intentLihat);
                break;
        }
    }



    @Override
    public void onBackPressed()
    {
        if (back_pressed + 2000 > System.currentTimeMillis()){
            super.onBackPressed();
            finish();
            moveTaskToBack(true);
        }
        else {
            Toast.makeText(getBaseContext(), "Tekan sekali lagi untuk keluar", Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
    }
}
