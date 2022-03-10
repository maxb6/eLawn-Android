package com.example.elawn_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
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

    private ViewPager viewPager;
    private ScreenSlidePagerAdapter pagerAdapter;
    private WifiManager wifiManager;
    private WifiInfo connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        viewPager = findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        //Read wifi information
        wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE) ;
        connection = wifiManager.getConnectionInfo();
        String display = "\n"+"SSID :"+connection.getSSID()+"\n" +"RSSI: "+connection.getRssi()+"\n"+"Mac address: "+connection.getMacAddress();

        Log.i("wifi info",display);





        SharedPreferencesHelper spHelper = new SharedPreferencesHelper(this);
        if (spHelper.isUserLoggedIn())
        {
            goToMainActivity();
        }
        else{
            goToLoginActivity();
        }


    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        private static final int NUM_PAGES = 3;

        public ScreenSlidePagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0:
                    OnBoardingFragment1 tab1 = new OnBoardingFragment1();
                    return tab1;
                case 1:
                    OnBoardingFragment2 tab2 = new OnBoardingFragment2();
                    return tab2;
                case 2:
                    OnBoardingFragment3 tab3 = new OnBoardingFragment3();
                    return tab3;
            }
            return null;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
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