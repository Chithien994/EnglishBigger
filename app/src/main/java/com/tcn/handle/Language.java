package com.tcn.handle;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Locale;

import com.tcn.englishbigger.ListActivity;
import com.tcn.englishbigger.R;

/**
 * Created by MyPC on 07/10/2017.
 */

public class Language {
    Activity activity;
    SharedPreferences pf;
    SharedPreferences.Editor editor;

    public Language(Activity activity) {
        this.activity = activity;
        this.pf = activity.getSharedPreferences(activity.getString(R.string.inforLocale), activity.MODE_PRIVATE);
        this.editor = pf.edit();
    }

    public Language(){}

    public void setLanguageDivice(String lag){
        this.editor.putString("locale", lag);
        editor.commit();
    }

    public String getLanguageDivice(){
        return pf.getString("locale","en");
    }

    public void setLanguage(String lag){
        editor.putString("language", lag);
        editor.commit();
    }

    public String getLanguage(Context context){
        pf = context.getSharedPreferences(context.getString(R.string.inforLocale), context.MODE_PRIVATE);
        return pf.getString("language","en");
    }

    public void setCFLanguageDivice(boolean bool){
        editor.putBoolean("divice",bool);
        editor.commit();
    }

    public boolean getCFLanguageDivice(Context context){
        pf = context.getSharedPreferences(context.getString(R.string.inforLocale), context.MODE_PRIVATE);
        return pf.getBoolean("divice",true);
    }

    public void settingLanguage(String lag){
        Log.d("Language",lag);
        Configuration configuration = new Configuration();
        configuration.locale = new Locale(lag);
        activity.getBaseContext().getResources().updateConfiguration(
                configuration,
                activity.getResources().getDisplayMetrics()
        );
        Intent intent = activity.getIntent();
        activity.finish();
        activity.startActivity(intent);
    }

    public void settingLanguage(Activity activity){
        if (!getCFLanguageDivice(activity)){
            Log.d("Language",getLanguage(activity));
            Configuration configuration = new Configuration();
            configuration.locale = new Locale(getLanguage(activity));
            activity.getBaseContext().getResources().updateConfiguration(
                    configuration,
                    activity.getResources().getDisplayMetrics()
            );
        }
    }
}
