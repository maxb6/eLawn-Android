package com.example.elawn_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

import com.example.elawn_android.OnboardingFragments.OnBoardingFragment1;
import com.example.elawn_android.OnboardingFragments.OnBoardingFragment2;
import com.example.elawn_android.OnboardingFragments.OnBoardingFragment3;
import com.example.elawn_android.Service.SharedPreferencesHelper;


public class LauncherActivity extends AppCompatActivity {

    private WifiManager wifiManager;
    private WifiInfo connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        //Read wifi information
        wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE) ;
        connection = wifiManager.getConnectionInfo();
        String display = "\n"+"SSID :"+connection.getSSID()+"\n" +"RSSI: "+connection.getRssi()+"\n"+"Mac address: "+connection.getMacAddress();

        Log.i("wifi info",display);

        //check if user is logged in and direct them to the appropriate activity
        //check if its a new user


        SharedPreferencesHelper spHelper = new SharedPreferencesHelper(this);
        if (spHelper.isUserLoggedIn())
        {
            goToMainActivity();
        }
        else{
            if(spHelper.isNewUser()){
                goToOnBoardingActivity();
            }
            else{
                goToLoginActivity();
            }
        }

    }

    private void goToOnBoardingActivity() {
        Intent intent = new Intent (this,OnBoardingActivity.class);
        startActivity(intent);
    }


    protected void goToMainActivity(){
        Intent intent = new Intent (this,MainActivity2.class);
        startActivity(intent);
    }

    protected void goToLoginActivity(){
        Intent intent = new Intent (this,LoginActivity.class);
        startActivity(intent);
    }
}