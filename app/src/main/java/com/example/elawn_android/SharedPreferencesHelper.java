package com.example.elawn_android;
import android.content.Context;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {

    private SharedPreferences sharedPreferences;

    public SharedPreferencesHelper(Context context) {
        sharedPreferences = context.getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);   //same code used to open file
    }

    public void setUserLogIn(Boolean login) {
        SharedPreferences.Editor edt = sharedPreferences.edit();
        edt.putBoolean("activity_executed", login);
        edt.commit();
    }

    public Boolean isUserLoggedIn() {
        return sharedPreferences.getBoolean("activity_executed", false);
    }

    public void setCurrentPath(int currentPath) {
        SharedPreferences.Editor edt = sharedPreferences.edit();
        edt.putInt("current_path", currentPath);
        edt.commit();
    }

    public int getCurrentPath() {
        return sharedPreferences.getInt("current_path", 0);

    }

    public void setPathNumber(String pathNumber) {
        SharedPreferences.Editor edt = sharedPreferences.edit();
        edt.putString("path_number", pathNumber);
        edt.commit();
    }

    public String getPathNumber() {
        return sharedPreferences.getString("path_number", null);

    }

}