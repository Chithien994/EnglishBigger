package com.tcn.handle;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.tcn.englishbigger.R;

/**
 * Created by MyPC on 27/12/2017.
 */

public class MyAction {
    //Activity
    public static final int LIST_ACTIVITY = 1;
    public static final int LEARN_ACTIVITY = 2;
    public static final int TOPIC_ACTIVITY = 3;

    //Fragment
    public static final int LEARN_FRAGMENT = 1;
    public static final int ADD_NEW_WORD_FRAGMENT = 2;
    public static final int TOPIC_FRAGMENT = 3;
    public static final int ADD_TOPIC_FRAGMENT = 4;
    public static final int EDIT_TOPIC_FRAGMENT = 5;
    public static final int NOTE_FRAGMENT = 6;
    public static final int WDPL_FRAGMENT = 7;
    public static final int TOPIC_FRIEND_FRAGMENT = 8;
    public static final int TAB_BF_FRAGMENT = 9;
    public static final int TAB_BO_FRAGMENT = 10;

    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;
    private static String TAG = "MyAction";



    public static void setActivityBulb(Context context, int activity){
        preferences = context.getSharedPreferences(context.getString(R.string.saveInfoApp), context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putInt("ACTIVITY_BULB", activity);
        editor.commit();
        editor.clear();
        Log.d(TAG, "Save ACTIVITY_BULB: "+preferences.getInt("ACTIVITY_BULB",0));
    }

    public static int getActivityBulb(Context context) {
        preferences = context.getSharedPreferences(context.getString(R.string.saveInfoApp), context.MODE_PRIVATE);
        return preferences.getInt("ACTIVITY_BULB", 0);
    }

    public static void setFragmentBulb(Context context, int fragment){
        preferences = context.getSharedPreferences(context.getString(R.string.saveInfoApp), context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putInt("FRAGMENT_BULB", fragment);
        editor.commit();
        editor.clear();
        Log.d(TAG, "Save FRAGMENT_BULB: "+preferences.getInt("FRAGMENT_BULB",0));
    }

    public static int getFragmentBulb(Context context) {
        preferences = context.getSharedPreferences(context.getString(R.string.saveInfoApp), context.MODE_PRIVATE);
        return preferences.getInt("FRAGMENT_BULB", 0);
    }

    public static void setFragmentNew(Context context, int fragment){
        preferences = context.getSharedPreferences(context.getString(R.string.saveInfoApp), context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putInt("FRAGMENT_NEW", fragment);
        editor.commit();
        editor.clear();
        Log.d(TAG, "Save FRAGMENT_NEW: "+preferences.getInt("FRAGMENT_NEW",0));
    }

    public static int getFragmentNew(Context context) {
        preferences = context.getSharedPreferences(context.getString(R.string.saveInfoApp), context.MODE_PRIVATE);
        return preferences.getInt("FRAGMENT_NEW", 0);
    }

    public static void setAction(Context context, boolean action){
        preferences = context.getSharedPreferences(context.getString(R.string.saveInfoApp), context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putBoolean("action",action);
        editor.commit();
        editor.clear();
        Log.d(TAG, "Save and check action: "+preferences.getBoolean("action",false));
    }

    public static boolean getAction(Context context){
        preferences = context.getSharedPreferences(context.getString(R.string.saveInfoApp), context.MODE_PRIVATE);
        return preferences.getBoolean("action",false);
    }

    public static void setLoginSession(Context context, String actionDate){
        preferences = context.getSharedPreferences(context.getString(R.string.saveInfoApp), context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString("action_date",actionDate);
        editor.commit();
        editor.clear();
        Log.d(TAG, "New login session: "+preferences.getString("action_date","0/0/0 0:0:0"));
    }

    public static String getLoginSession(Context context){
        preferences = context.getSharedPreferences(context.getString(R.string.saveInfoApp), context.MODE_PRIVATE);
        Log.d(TAG, "Get login session: "+preferences.getString("action_date","0/0/0 0:0:0"));
        return preferences.getString("action_date","0/0/0 0:0:0");
    }

    //Check if the login session has expired?
    //The login session expires:
    //Renewed login session add return true
    public static boolean checkRefreshDateLogin(Context context){
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String[] sDateNow = sdfDate.format(Calendar.getInstance().getTime()).split(" ")[0].split("/");
        String[] sTimeNow = sdfDate.format(Calendar.getInstance().getTime()).split(" ")[1].split(":");
        String[] sDateLogin  = getLoginSession(context).split(" ")[0].split("/");
        String[] sTimeLogin  = getLoginSession(context).split(" ")[1].split(":");
        boolean response = false;
        try {
            response = convertInt(sDateNow[2]) != convertInt(sDateLogin[2])?true:
                    convertInt(sDateNow[1]) != convertInt(sDateLogin[1])?true:
                            (convertInt(sDateNow[0]) - convertInt(sDateLogin[0]) > 1)?true:
                                    (convertInt(sDateNow[0]) - convertInt(sDateLogin[0]) > 0)?
                                            (convertInt(sTimeNow[0]) - convertInt(sTimeLogin[0]) >= 0)?true:false:false;
        }catch (Exception e){
            e.printStackTrace();
            setLoginSession(context,sdfDate.format(Calendar.getInstance().getTime()));
            return true;
        }

        if (response) setLoginSession(context,sdfDate.format(Calendar.getInstance().getTime()));
        sdfDate.clone();
        Log.d(TAG, "Auto login: "+response);
        return response;
    }

    private static int convertInt(String input){
        try {
            return Integer.parseInt(input);
        }catch (Exception e){
            return 0;
        }
    }

    public static void setIdTopic(Context context, int id ){
        try {
            preferences = context.getSharedPreferences(context.getString(R.string.saveInfoApp), context.MODE_PRIVATE);
            editor = preferences.edit();
            editor.putInt("ID_TOPIC",id);
            editor.commit();
            editor.clear();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static int getIdTopic(Context context){
        preferences = context.getSharedPreferences(context.getString(R.string.saveInfoApp), context.MODE_PRIVATE);
        return preferences.getInt("ID_TOPIC",0);
    }

    public static void setPosition(Context context, int id ){
        preferences = context.getSharedPreferences(context.getString(R.string.saveInfoApp), context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putInt("POSITION",id);
        editor.commit();
        editor.clear();
        Log.d(TAG, "Save POSITION: "+preferences.getInt("POSITION",0));
    }

    public static int getPosition(Context context){
        preferences = context.getSharedPreferences(context.getString(R.string.saveInfoApp), context.MODE_PRIVATE);
        return preferences.getInt("POSITION",0);
    }

    public static void setIDFriend(Context context, String id){
        preferences = context.getSharedPreferences(context.getString(R.string.saveInfoApp), context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString("ID_FRIEND", id);
        editor.commit();
        editor.clear();
        Log.d(TAG, "Save ID_FRIEND: "+preferences.getString("ID_FRIEND","0"));
    }

    public static String getIDFriend(Context context) {
        preferences = context.getSharedPreferences(context.getString(R.string.saveInfoApp), context.MODE_PRIVATE);
        return preferences.getString("ID_FRIEND", "0");
    }

    public static void setNameFriend(Context context, String name){
        preferences = context.getSharedPreferences(context.getString(R.string.saveInfoApp), context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString("NAME_FRIEND", name);
        editor.commit();
        editor.clear();
        Log.d(TAG, "Save NAME_FRIEND: "+preferences.getString("NAME_FRIEND",""));
    }

    public static String getNameFriend(Context context) {
        preferences = context.getSharedPreferences(context.getString(R.string.saveInfoApp), context.MODE_PRIVATE);
        return preferences.getString("NAME_FRIEND", "");
    }

    public static void setRefreshTopic(Context context, boolean cfRefresh){
        preferences = context.getSharedPreferences(context.getString(R.string.saveInfoApp), context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putBoolean("REFRESH_TOPIC", cfRefresh);
        editor.commit();
        editor.clear();
        Log.d(TAG, "Save REFRESH_TOPIC: "+preferences.getBoolean("REFRESH_TOPIC",true));
    }

    public static boolean getRefreshTopic(Context context) {
        preferences = context.getSharedPreferences(context.getString(R.string.saveInfoApp), context.MODE_PRIVATE);
        return preferences.getBoolean("REFRESH_TOPIC", true);
    }
}
