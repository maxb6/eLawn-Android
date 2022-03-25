package com.example.elawn_android.MainFragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.example.elawn_android.Database.DatabaseHelper;
import com.example.elawn_android.LauncherActivity;
import com.example.elawn_android.MapsActivity;
import com.example.elawn_android.OnBoardingActivity;
import com.example.elawn_android.R;
import com.example.elawn_android.Service.Path;
import com.example.elawn_android.Service.PathAdapter;
import com.example.elawn_android.Service.SharedPreferencesHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class SettingsFragment extends Fragment {

    private static final String TAG = "Settings fragment: ";
    private DatabaseReference mowControlReference;
    private Switch powerSwitch;
    private Switch notificationSwitch;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;

    private List<Path> pathList = new ArrayList();

    private SharedPreferencesHelper spHelper;
    private DatabaseHelper dbHelper;
    private static DatabaseReference pathReference;
    private static DatabaseReference controlReference;

    private Button mapsButton;
    private ImageView questionMark;
    private Button profileButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup root = (ViewGroup)inflater.inflate(R.layout.fragment_settings, container, false);

        spHelper = new SharedPreferencesHelper(getActivity());
        dbHelper = new DatabaseHelper(getActivity());

        mapsButton = root.findViewById(R.id.mapsButton2);
        profileButton = root.findViewById(R.id.profileButton);
        questionMark = root.findViewById(R.id.questionMark);

        pathReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Mower Paths");

        controlReference = FirebaseDatabase.getInstance().getReference("Control");

        //control switch initialization
        powerSwitch = root.findViewById(R.id.controlSwitch);
        notificationSwitch = root.findViewById(R.id.notificationSwitch);
        mRecyclerView = root.findViewById(R.id.pathRecyclerView);


        setPowerSwitchValue();
        setNotificationSwitchValue();
        loadPaths();

        questionMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Swipe to delete your path in the Path Manager", Toast.LENGTH_LONG).show();
            }
        });

        mapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMapsActivity();
            }
        });

        Button instructionButton = root.findViewById(R.id.button);
        instructionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToOnBoardingActivity2();
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileFragment profileFragment = new ProfileFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, profileFragment, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });



        // Inflate the layout for this fragment
        return root;
    }

    protected void setPowerSwitchValue(){
        if(spHelper.getPower()==false){
           powerSwitch.setChecked(false);
        }else{
            powerSwitch.setChecked(true);
        }

        powerSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(powerSwitch.isChecked()){
                    spHelper.setPower(true);
                }
                else{
                    controlReference.child("Status").setValue("Off");
                    controlReference.child("Blade").setValue(0);
                    controlReference.child("MowControl").setValue(0);
                    spHelper.setPower(false);
                }
            }
        });
    }

    protected void setNotificationSwitchValue(){
        if(spHelper.getNotification()==false){
            notificationSwitch.setChecked(false);
        }else{
            notificationSwitch.setChecked(true);
        }


        notificationSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(notificationSwitch.isChecked()){
                    spHelper.setNotification(true);
                }
                else{
                    spHelper.setNotification(false);
                }
            }
        });
    }

    private void loadPaths() {

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        pathList = dbHelper.getAllPaths();

        mAdapter = new PathAdapter(pathList);
        Log.i(TAG,"Path list: "+pathList.toString());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter.notifyDataSetChanged();
        new ItemTouchHelper (simpleCallback).attachToRecyclerView(mRecyclerView);

    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
            int position = viewHolder.getAdapterPosition();
            String pathID = String.valueOf(pathList.get(position).getId());
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked
                            //remove path from sql and firebase
                            dbHelper.deletePathByID(pathID);
                            pathReference.child(pathID).removeValue();
                            mAdapter.notifyItemRemoved(position);
                            loadPaths();

                            Toast.makeText(getActivity(),"Path deleted successfully",Toast.LENGTH_SHORT).show();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            loadPaths();
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Are you sure you want to delete your path?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }
    };

    private void goToLauncherActivity() {
        Intent intent = new Intent (getActivity(), LauncherActivity.class);
        startActivity(intent);
    }

    private void goToOnBoardingActivity2() {
        Intent intent = new Intent (getActivity(), OnBoardingActivity.class);
        startActivity(intent);
    }

    private void goToMapsActivity() {
        Intent intent = new Intent (getActivity(), MapsActivity.class);
        startActivity(intent);
    }


}