package tcn.com.englishbigger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import tcn.com.fragment.AddAndEditTopicFragment;
import tcn.com.fragment.TopicFragment;
import tcn.com.fragment.WhatDoPeopleLearnFragment;
import tcn.com.handle.Handle;
import tcn.com.handle.HandleIntent;
import tcn.com.handle.ServerAPI;
import tcn.com.handle.Users;
import tcn.com.handle.UsersFB;
import tcn.com.models.TopicModels;
import tcn.com.models.UsersModels;

public class TopicActivity extends AppCompatActivity {
    private Toolbar toolbarTopic;
    public ArrayList<TopicModels> topicModes;
    private LinearLayout layoutFragmentTopic;
    public Fragment fragmentBack;
    public boolean cfFinish;
    public static final String broadcastAction = "BroadcastAction";
    private static final int STORAGE_PERMISSION_CODE = 123; //storage permission code
    private static final int CAMERA_PERMISSION_CODE = 321; //camera permission code
    public ServerAPI serverAPI;
    public UsersFB usersFB;
    public Handle handle;
    public int type;
    private int id;
    public UsersModels usersModels;
    public int size; //Saves the number of words of a selected topic
    public int sizeNow; //Saves the number of words of a selected topic
    public int position = -1; //The updated image position will be assigned to it

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);
        //Requesting storage permission
        requestStoragePermission();
        addControls();
        addEvents();
    }

    private void addEvents() {

    }

    public void saveFragmen(Fragment fragment){
        cfFinish = false;
        fragmentBack = fragment;
    }
    //Requesting permission
    private void requestStoragePermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    //Requesting permission
    private void requestCameraPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {
            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, getString(R.string.permissionGrantedNowYouCanReadTheStorage), Toast.LENGTH_LONG).show();
                //Requesting camera permission
                requestCameraPermission();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, getString(R.string.oopsYouJustDeniedThePermission), Toast.LENGTH_LONG).show();
                //Requesting storage permission
                requestStoragePermission();
            }

        }else if (requestCode == CAMERA_PERMISSION_CODE){

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, getString(R.string.permissionGrantedNowYouCanOpenTheCamera), Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this,getString(R.string.oopsYouJustDeniedThePermission), Toast.LENGTH_LONG).show();
                //Requesting camera permission
                requestCameraPermission();
            }
        }
    }
    private void addControls() {
        size = -1;
        toolbarTopic = (Toolbar) findViewById(R.id.toolbarTopic);
        setSupportActionBar(toolbarTopic);
        topicModes = new ArrayList<>();
        serverAPI = new ServerAPI();
        usersFB = new UsersFB(TopicActivity.this);
        handle = new Handle();

        Intent intent = getIntent();
        //type = 0 Open a topic section
        //type = 1 Open a add section
        //type = 2 Open a edit section
        //type = 3 Open a WhatDoPeopleLearn
        //type = 4 Open a topic of friend
        type = intent.getIntExtra("type",0);

        //id topic need edit
        id = intent.getIntExtra("id",0);

        try {
            usersModels = (UsersModels) intent.getSerializableExtra("FRIENDS");
        }catch (Exception e){
            Log.e("FRIENDS","Not");
        }


        position = intent.getIntExtra("position",-1); //Get position to update photos

        if (type == 3){
            //What do people learn?
            callFragment(new WhatDoPeopleLearnFragment());
        }else {
            loadingTopic();
        }

    }

    public void loadingTopic() {
        myIntentFilter();
        Users users = new Users(TopicActivity.this);
        JSONObject object = new JSONObject();
        String idUser = users.getIdUser();
        if (type == 4){
            idUser = usersModels.getIdUser();
        }
        try {
            object.put("idUser", idUser);
            serverAPI.getTopic(this, object, "gettopic");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void callFragment(Fragment fragment) {
        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.layoutFragmentTopic, fragment);
        transaction.commit();
    }
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

    public void myIntentFilter(){
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(broadcastAction);
        registerReceiver(mReceiver,mIntentFilter);
        Log.i("DK","mReceiver");
    }
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(broadcastAction)) {
                try {
                    topicModes = (ArrayList<TopicModels>) intent.getSerializableExtra("TOPIC");

                    //type = 0 Open a topic section
                    //type = 1 Open a add section
                    //type = 2 Open a edit section
                    if (type == 0){
                        callFragment(new TopicFragment());

                    }else if (type == 1){
                        callFragment(new AddAndEditTopicFragment(), "add", 0);

                    }else if (type == 2){
                        callFragment(new AddAndEditTopicFragment(), "edit", id);

                    }else if (type == 3){
                        callFragment(new WhatDoPeopleLearnFragment());

                    }else if (type == 4){
                        callFragment(new WhatDoPeopleLearnFragment());
                    }

                    unregisterReceiver(mReceiver);
                    Log.d("unregisterReceiver","Unregister Receiver");

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {

            if(cfFinish){
                onBackPressed();
                finish();
            }else {
                if (size != sizeNow && size != -1){
                    HandleIntent hi = new HandleIntent();
                    hi.intentActivity(this, HandleIntent.INTENT_TOPIC);
                    finish();
                }else {
                    callFragment(fragmentBack);
                }
                size = -1;
                sizeNow = 0;
            }

        }
        return true;
    }
}
