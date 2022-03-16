package com.example.elawn_android.OnboardingFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.elawn_android.LoginActivity;
import com.example.elawn_android.MainActivity2;
import com.example.elawn_android.MainFragments.SettingsFragment;
import com.example.elawn_android.R;
import com.example.elawn_android.Service.SharedPreferencesHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class OnBoardingFragment3 extends Fragment {

    private FloatingActionButton arrowButton;
    private TextView skipButton;
    private SharedPreferencesHelper spHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup)inflater.inflate(R.layout.fragment_on_boarding3, container, false);

        arrowButton = root.findViewById(R.id.arrowButton);
        skipButton = root.findViewById(R.id.skip3);

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spHelper.isUserLoggedIn()){
                    goToMainActivity();
                }
                else{
                    goToLoginActivity();
                }
            }
        });

        arrowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(spHelper.isUserLoggedIn()){
                   goToMainActivity();
               }
               else{
                   goToLoginActivity();
               }
            }
        });

        return root;
    }

    private void goToMainActivity() {
        Intent intent = new Intent (getActivity(), MainActivity2.class);
        //intent.putExtra("frgToLoad", SETTINGS_FRAGMENT);
        startActivity(intent);
    }

    protected void goToLoginActivity(){
        Intent intent = new Intent (getActivity(),LoginActivity.class);
        startActivity(intent);
    }
}
