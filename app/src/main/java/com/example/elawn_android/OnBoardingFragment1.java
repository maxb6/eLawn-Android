package com.example.elawn_android;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class OnBoardingFragment1 extends Fragment {


    private TextView skipButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup)inflater.inflate(R.layout.fragment_on_boarding1, container, false);

        skipButton = root.findViewById(R.id.skip1);

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLoginActivity();
            }
        });

        return root;
    }

    protected void goToLoginActivity(){
        Intent intent = new Intent (getActivity(),LoginActivity.class);
        startActivity(intent);
    }
}