package com.example.elawn_android.MainFragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.elawn_android.LoginActivity;
import com.example.elawn_android.R;
import com.example.elawn_android.Service.SharedPreferencesHelper;
import com.example.elawn_android.Service.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private EditText editName;
    private EditText editEmail;
    private Button editButton;
    private Button saveButton;
    private ImageView backButton;

    private FirebaseUser user;
    private String userID;

    private View view;
    private Button signOutButton;
    private DatabaseReference userReference;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup root = (ViewGroup)inflater.inflate(R.layout.fragment_profile, container, false);
        view =  inflater.inflate(R.layout.fragment_profile, container, false);

        //initializing editTexts and button
        editName = root.findViewById(R.id.editProfileName);
        editEmail = root.findViewById(R.id.editProfileEmail);
        editButton = root.findViewById(R.id.editButton);
        saveButton = root.findViewById(R.id.saveButton);
        signOutButton = root.findViewById(R.id.signOutButton2);
        backButton = root.findViewById(R.id.backButton2);


        //By default have the editTexts not be editable by user
        editName.setEnabled(false);
        editEmail.setEnabled(false);


        //firebase authentication
        user = FirebaseAuth.getInstance().getCurrentUser();
        userReference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        SharedPreferencesHelper spHelper = new SharedPreferencesHelper(getActivity());

        saveButton.setVisibility(View.GONE);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsFragment settingsFragment = new SettingsFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, settingsFragment, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });

        userReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if(userProfile != null){
                    String name = userProfile.name;
                    String email = userProfile.email;

                    editName.setText(name);
                    editEmail.setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(),"User info error!",Toast.LENGTH_LONG).show();

            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                enterEditMode();
            }
        });

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLoginActivity();
                FirebaseAuth.getInstance().signOut();
                spHelper.setUserLogIn(false);
            }
        });



        // Inflate the layout for this fragment
        return root;
    }

    protected void enterEditMode(){
        //when editMode is active, allow user to input in editTexts
        //By default have the editTexts not be editable by user
        editName.setEnabled(true);
        editEmail.setEnabled(true);
        editName.setTextColor(Color.GRAY);
        editEmail.setTextColor(Color.GRAY);
        editButton.setVisibility(View.GONE);
        saveButton.setVisibility(View.VISIBLE);


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userReference = FirebaseDatabase.getInstance().getReference("Users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                user = FirebaseAuth.getInstance().getCurrentUser();

                user.updateEmail(String.valueOf(editEmail.getText()))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    userReference.child("name").setValue(editName.getText().toString());
                                    userReference.child("email").setValue(editEmail.getText().toString());
                                    final String TAG = "EmailUpdate";
                                    Log.d(TAG, "User email address updated.");
                                    Toast.makeText(getActivity().getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    FirebaseAuthException e = (FirebaseAuthException)task.getException();
                                    Toast.makeText(getActivity().getApplicationContext(), "Re-login to edit your profile!", Toast.LENGTH_LONG).show();
                                    goToLoginActivity();
                                }
                            }
                        });
                exitEditMode();

            }
        });
    }

    protected void exitEditMode(){
        //when we exit editMode, we return to displayMode
        //make editTexts uneditable
        //hide the save button
        editName.setEnabled(false);
        editEmail.setEnabled(false);
        editName.setTextColor(Color.BLACK);
        editEmail.setTextColor(Color.BLACK);
        editButton.setText("Edit Profile");
        saveButton.setVisibility(View.GONE);
        editButton.setVisibility(View.VISIBLE);
    }

    protected void goToLoginActivity(){
        Intent intent = new Intent (getActivity(), LoginActivity.class);
        startActivity(intent);
    }


}