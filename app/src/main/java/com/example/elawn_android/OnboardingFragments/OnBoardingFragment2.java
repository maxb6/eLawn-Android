package com.example.elawn_android.OnboardingFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.elawn_android.LoginActivity;
import com.example.elawn_android.MainActivity2;
import com.example.elawn_android.R;
import com.example.elawn_android.Service.SharedPreferencesHelper;

public class OnBoardingFragment2 extends Fragment {

    private TextView skipButton;
    private ImageView fieldImage;
    private ImageView marker1;
    private ImageView marker2;
    private ImageView marker3;
    private ImageView marker4;
    private SharedPreferencesHelper spHelper;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup)inflater.inflate(R.layout.fragment_on_boarding2, container, false);

        skipButton = root.findViewById(R.id.skip2);
        fieldImage = root.findViewById(R.id.field);
        marker1 = root.findViewById(R.id.marker1);
        marker2 = root.findViewById(R.id.marker2);
        marker3 = root.findViewById(R.id.marker3);
        marker4 = root.findViewById(R.id.marker4);
        Animation myFadeInAnimation = AnimationUtils.loadAnimation(getActivity(),R.anim.fadein);
        marker1.startAnimation(myFadeInAnimation);
        marker2.startAnimation(myFadeInAnimation);
        marker3.startAnimation(myFadeInAnimation);
        marker4.startAnimation(myFadeInAnimation);

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

        return root;
    }

    private void goToMainActivity() {
        Intent intent = new Intent (getActivity(), MainActivity2.class);
        //intent.putExtra("frgToLoad", SETTINGS_FRAGMENT);
        startActivity(intent);
    }

    protected void goToLoginActivity(){
        Intent intent = new Intent (getActivity(), LoginActivity.class);
        startActivity(intent);
    }
}