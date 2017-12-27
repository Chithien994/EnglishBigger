package tcn.com.handle;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import tcn.com.englishbigger.R;

/**
 * Created by MyPC on 27/12/2017.
 */

public class MyAction {
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;
    private static String TAG = "MyAction";

    public static void setAction(Context context, boolean action){
        preferences = context.getSharedPreferences(context.getString(R.string.inforLocale), context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putBoolean("action",action);
        editor.commit();
        editor.clear();
        Log.d(TAG, "Save and check action: "+preferences.getBoolean("action",false));
    }

    public static boolean getAction(Context context){
        preferences = context.getSharedPreferences(context.getString(R.string.inforLocale), context.MODE_PRIVATE);
        return preferences.getBoolean("action",false);
    }

    public static void setLoginSession(Context context, String actionDate){
        preferences = context.getSharedPreferences(context.getString(R.string.inforLocale), context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString("action_date",actionDate);
        editor.commit();
        editor.clear();
        Log.d(TAG, "New login session: "+preferences.getString("action_date","0-0-0 0:0:0"));
    }

    public static String getLoginSession(Context context){
        preferences = context.getSharedPreferences(context.getString(R.string.inforLocale), context.MODE_PRIVATE);
        Log.d(TAG, "Get login session: "+preferences.getString("action_date","0-0-0 0:0:0"));
        return preferences.getString("action_date","0-0-0 0:0:0");
    }

    //Check if the login session has expired?
    //The login session expires:
    //Renewed login session add return true
    public static boolean checkRefreshDateLogin(Context context){
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String[] sDateNow = sdfDate.format(Calendar.getInstance().getTime()).split(" ")[0].split("-");
        String[] sTimeNow = sdfDate.format(Calendar.getInstance().getTime()).split(" ")[1].split(":");
        String[] sDateLogin  = getLoginSession(context).split(" ")[0].split("-");
        String[] sTimeLogin  = getLoginSession(context).split(" ")[1].split(":");
        boolean response = convertInt(sDateNow[2]) != convertInt(sDateLogin[2])?true:
                        convertInt(sDateNow[1]) != convertInt(sDateLogin[1])?true:
                                (convertInt(sDateNow[0]) - convertInt(sDateLogin[0]) > 1)?true:
                                        (convertInt(sDateNow[0]) - convertInt(sDateLogin[0]) > 0)?
                                                (convertInt(sTimeNow[0]) - convertInt(sTimeLogin[0]) >= 0)?true:false:false;
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
}
