package com.acnlab.altbeacon.pplntaipei;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class OpeningActivity extends AppCompatActivity {

    private EditText mUsername, mUserpasswd;
    private Button mLogin;
    private TextView mRegister;
    private String Name, Password;
    private SharedPreferences mSharedPreferences;
    public static final String PREFERENCE = "preference";
    public static final String PREF_NAME = "name";
    public static final String KPPS_NO = "kpps_no";
    public static final String PREF_SKIP_LOGIN = "skip_profile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);
        mSharedPreferences = getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        if (mSharedPreferences.contains(PREF_SKIP_LOGIN)) {

            new Handler().postDelayed(new Runnable() {


                @Override
                public void run() {
                    // This method will be executed once the timer is over
                    Intent intent = new Intent(OpeningActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 1500);

        } else {

            if (mSharedPreferences.contains(PREF_NAME) && mSharedPreferences.contains(KPPS_NO)) {
                SharedPreferences.Editor mEditor = mSharedPreferences.edit();
                mEditor.putString(PREF_SKIP_LOGIN, "skip");
                mEditor.apply();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // This method will be executed once the timer is over
                        Intent intent = new Intent(OpeningActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, 1500);
            } else {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // This method will be executed once the timer is over
                        Intent intent = new Intent(OpeningActivity.this, RegisterKpps.class);
                        startActivity(intent);
                        finish();
                    }
                }, 1500);
            }


        }
    }
}