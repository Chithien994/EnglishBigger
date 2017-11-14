package tcn.com.englishbigger;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.facebook.login.LoginManager;

import java.util.Locale;

import tcn.com.handle.Handle;
import tcn.com.handle.Language;
import tcn.com.handle.Users;
import tcn.com.handle.UsersFB;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DISPLAY_LENGTH = 500;
    private Language language;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        language = new Language(SplashActivity.this);
        language.setLanguageDivice(Locale.getDefault().getLanguage());

        UsersFB usersFB = new UsersFB(SplashActivity.this);
        Handle handle = new Handle();


        handle.deleteCache(SplashActivity.this);
        usersFB.confirmLogin(false);
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashActivity.this,ListActivity.class);
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
