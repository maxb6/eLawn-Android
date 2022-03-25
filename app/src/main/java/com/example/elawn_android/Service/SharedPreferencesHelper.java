package com.example.elawn_android.Service;
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

    public void setNewUser(Boolean newUser) {
        SharedPreferences.Editor edt = sharedPreferences.edit();
        edt.putBoolean("new_user", newUser);
        edt.commit();
    }

    public Boolean isNewUser() {
        return sharedPreferences.getBoolean("new_user", true);
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

    public void setBatteryLevel(Float batteryLevel) {
        SharedPreferences.Editor edt = sharedPreferences.edit();
        edt.putFloat("battery_level", batteryLevel);
        edt.commit();
    }

    public Float getBatteryLevel() {
        return sharedPreferences.getFloat("battery_level", 0);

    }



    public void setNextPathNumber(String pathNumber) {
        SharedPreferences.Editor edt = sharedPreferences.edit();
        edt.putString("next_path_number", pathNumber);
        edt.commit();
    }

    public String getNextPathNumber() {
        return sharedPreferences.getString("next_path_number", null);

    }

    public void setNotification(Boolean control){
        SharedPreferences.Editor edt = sharedPreferences.edit();
        edt.putBoolean("notif_control", control);
        edt.commit();
    }

    public Boolean getNotification(){
        return sharedPreferences.getBoolean("notif_control",true);
    }

    public void setPower(Boolean control){
        SharedPreferences.Editor edt = sharedPreferences.edit();
        edt.putBoolean("power_control", control);
        edt.commit();
    }

    public Boolean getPower(){
        return sharedPreferences.getBoolean("power_control",true);
    }



}