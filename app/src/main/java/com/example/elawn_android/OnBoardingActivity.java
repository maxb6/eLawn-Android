package com.example.elawn_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.elawn_android.OnboardingFragments.OnBoardingFragment1;
import com.example.elawn_android.OnboardingFragments.OnBoardingFragment2;
import com.example.elawn_android.OnboardingFragments.OnBoardingFragment3;
import com.example.elawn_android.Service.SharedPreferencesHelper;

public class OnBoardingActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private OnBoardingActivity.ScreenSlidePagerAdapter pagerAdapter;
    private SharedPreferencesHelper spHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        spHelper = new SharedPreferencesHelper(this);

        viewPager = findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());

        //when this activity is launched, the new user is set to false
        spHelper.setNewUser(false);
        viewPager.setAdapter(pagerAdapter);


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

}