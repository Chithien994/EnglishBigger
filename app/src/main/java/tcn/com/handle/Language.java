package tcn.com.handle;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.util.Log;

import java.util.Locale;

import tcn.com.englishbigger.ListActivity;
import tcn.com.englishbigger.R;

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
        return pf.getString("locale","");
    }

    public void setLanguage(String lag){
        editor.putString("language", lag);
        editor.commit();
    }

    public String getLanguage(Activity activity){
        pf = activity.getSharedPreferences(activity.getString(R.string.inforLocale), activity.MODE_PRIVATE);
        return pf.getString("language","");
    }

    public void setCFLanguageDivice(boolean bool){
        editor.putBoolean("divice",bool);
        editor.commit();
    }

    public boolean getCFLanguageDivice(Activity activity){
        pf = activity.getSharedPreferences(activity.getString(R.string.inforLocale), activity.MODE_PRIVATE);
        return pf.getBoolean("divice",true);
    }

    public void settingLanguage(String lag){
        Locale locale = new Locale(lag);
        Log.d("Language",lag);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        activity.getBaseContext().getResources().updateConfiguration(
                configuration,
                activity.getResources().getDisplayMetrics()
        );
        Intent intent = new Intent(activity, activity.getClass());
        activity.startActivity(intent);
        activity.finish();
    }

    public void settingLanguage(Activity activity){
        if (pf.getBoolean("divice",false)==false){
            Locale locale = new Locale(pf.getString("language","en"));
            Log.d("Language",pf.getString("language","en"));
            Configuration configuration = new Configuration();
            configuration.locale = locale;
            activity.getBaseContext().getResources().updateConfiguration(
                    configuration,
                    activity.getResources().getDisplayMetrics()
            );
        }

    }
}
