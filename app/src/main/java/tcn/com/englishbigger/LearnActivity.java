package tcn.com.englishbigger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import tcn.com.fragment.AddFragment;
import tcn.com.fragment.LearnNowFragment;
import tcn.com.fragment.TopicFragment;
import tcn.com.handle.Constants;
import tcn.com.handle.Handle;
import tcn.com.handle.Language;
import tcn.com.handle.ServerAPI;
import tcn.com.handle.Users;
import tcn.com.models.TopicModels;

public class LearnActivity extends AppCompatActivity {

    public static String MY_DATABASE = "vocabulary.sqlite";
    public static String MY_BRC_LEARN_ACTIVITY = "MY_BRC_LEARN_ACTIVITY";
    public SQLiteDatabase database;
    Toolbar toolbar;
    public ArrayList<TopicModels> topicModes;
    public ServerAPI serverAPI;
    public Handle handle;
    public int id;
    public boolean cfFinish;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        addControls();
        addEvents();
    }

    private void addEvents() {

    }

    private void addControls() {
        topicModes = new ArrayList<>();
        serverAPI = new ServerAPI();
        handle = new Handle();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();

        //Get the topic name from the server
        handleGetNameTopic();

    }

    //Get the topic name from the server
    private void handleGetNameTopic() {
        myIntentFilter();

        Users users = new Users(this);
        JSONObject object = new JSONObject();
        try {
            object.put("idUser", users.getIdUser());
            serverAPI.getTopic(this, object, Constants.GET_NAME_TOPIC);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void handleOpenFragment(){
        Intent intent = getIntent();
        int in = intent.getIntExtra("PUT",0);
        if (in == 1){
            cfFinish = intent.getBooleanExtra("FINISH",false);
            callFragment(new  LearnNowFragment());

        }else if (in == 2){
            callFragment(new AddFragment());

        }else if (in == 3){
            callFragment(new TopicFragment());

        }else if (in == 4){
            id = intent.getIntExtra("ID",0);
            callFragment(new  AddFragment());
        }
    }

    public void callFragment(Fragment fragment) {
        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment, fragment);
        transaction.commit();
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        Language language = new Language(LearnActivity.this);
        language.settingLanguage(LearnActivity.this);
    }

    public void myIntentFilter(){
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(MY_BRC_LEARN_ACTIVITY);
        registerReceiver(mReceiver,mIntentFilter);
        Log.i("DK","mReceiver");
    }
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MY_BRC_LEARN_ACTIVITY)) {
                //
                if (intent.getSerializableExtra("TOPIC") != null){
                    topicModes = (ArrayList<TopicModels>) intent.getSerializableExtra("TOPIC");
                    if (topicModes.size() > 0){

                        handleOpenFragment();
                    }else {
                        // If not topic
                        new AlertDialog.Builder(LearnActivity.this)
                                .setMessage(getString(R.string.youHaveNoTopic))
                                .setCancelable(false)
                                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //close learn now
                                        finish();
                                    }
                                })
                                .setPositiveButton(getString(R.string.add), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //add topic && close learn now
                                        Intent intent = new Intent(LearnActivity.this, TopicActivity.class);
                                        intent.putExtra("type", 1);
                                        intent.putExtra("id", 0);
                                        startActivity(intent);
                                        finish();

                                    }
                                }).show();
                    }

                    unregisterReceiver(mReceiver);

                }
            }
        }
    };

    public void callFragment(Fragment fragment, String type, int id) {
        Bundle bundle = new Bundle();
        bundle.putString("SELECT", type);
        bundle.putInt("ID", id);
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.layoutFragmentTopic, fragment);
        fragmentTransaction.commit();
    }

}
