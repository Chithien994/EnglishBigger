package com.tcn.handle;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.tcn.englishbigger.R;

/**
 * Created by MyPC on 23/08/2017.
 */

public class Users {
    Activity activity;
    SharedPreferences pf;
    SharedPreferences.Editor editor;
    Context context;
    public Users(Activity activity){
        this.activity = activity;
        pf = activity.getSharedPreferences(activity.getString(R.string.saveInforUser), activity.MODE_PRIVATE);
        editor = pf.edit();
    }

    public Users(Context context){
        this.context = context;
        pf = context.getSharedPreferences(context.getString(R.string.saveInforUser), context.MODE_PRIVATE);
        editor = pf.edit();
    }

    public String getTokenFirebase(){
        return pf.getString("token_firebase","");
    }

    public void setTokenFirebase(String tokenFirebase){
        Log.d("tokenFirebase","Saved: "+tokenFirebase);
        editor.putString("token_firebase",tokenFirebase);
        editor.commit();
    }

    public String getIdUser(){
        return pf.getString("id","");
    }

    public void setIdUser(String idUser){
        editor.putString("id",idUser);
        editor.commit();
    }

    public String getFirstName(){
        return pf.getString("first_name","English Bigger");
    }

    public void setFirstName(String firstName){
        editor.putString("first_name",firstName);
        editor.commit();
    }

    public String getName(){
        return pf.getString("name","English Bigger");
    }

    public void setName(String name){
        editor.putString("name", name);
        editor.commit();
    }

    public String getEmail(){
        return pf.getString("email","");
    }

    public void setEmail(String email){
        editor.putString("email", email);
        editor.commit();
    }

    public String getGender(){
        return pf.getString("gender","");
    }

    public void setGender(String gender){
        editor.putString("gender", gender);
        editor.commit();
    }

    public String getBirthday(){
        return pf.getString("birthday","");
    }

    public void setBirthday(String birthday){
        editor.putString("birthday", birthday);
        editor.commit();
    }

    public String getLocation(){
        return pf.getString("location","");
    }

    public void setLocation(String location){
        editor.putString("location", location);
        editor.commit();
    }

    public String getUrlAvatar(){
        return pf.getString("urlAvatar","");
    }

    public void setUrlAvatar(String urlAvatar){
        editor.putString("urlAvatar", urlAvatar);
        editor.commit();
    }

    public String getAccessToken(){
        return pf.getString("accessToken","");
    }

    public void setAccessToken(String accessToken){
        editor.putString("accessToken", accessToken);
        editor.commit();
    }

    public void setUser(String accessToken, String id, String first_name, String name, String email, String gender, String birthday, String location, String urlAvatar) {
        try {
            editor.putString("accessToken",accessToken);
            editor.putString("id", id);
            editor.putString("first_name", first_name);
            editor.putString("name", name);
            editor.putString("email", email);
            editor.putString("gender",gender);
            editor.putString("birthday", birthday);
            editor.putString("location",location);
            editor.putString("urlAvatar",urlAvatar);
            editor.commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "ID: " + getIdUser() +
                "\nName: " + getName() +
                "\nGender: " + getGender() +
                "\nBirthday: " + getBirthday() +
                "\nLocation: " + getLocation() +
                "\nEmail: " + getEmail() +
                "\nUrlAvatar: " + getUrlAvatar();
    }
}
