package com.tcn.englishbigger;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONException;
import org.json.JSONObject;

import com.tcn.adapters.DrawerAdapter;
import com.tcn.adapters.LocaleAdapter;
import com.tcn.handle.AdFast;
import com.tcn.handle.Constants;
import com.tcn.handle.Handle;
import com.tcn.handle.IntentActivity;
import com.tcn.handle.Language;
import com.tcn.handle.MyAction;
import com.tcn.handle.ServerAPI;
import com.tcn.handle.Users;
import com.tcn.handle.UsersFB;
import com.tcn.models.DrawerModels;
import com.tcn.models.LocaleModels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;


public class ListActivity extends AppCompatActivity {


    public static String cfBroadcastAction = "CF_BROADCAST_ACTION";

    private CallbackManager callbackManager;
    public SharedPreferences pf, spSV;
    private long mLastClickTime = 0;
    private boolean cfDeleteImgCache = true;
    public int position = -1;
    private boolean cfAutoLogin = true;

    public Handle handle = new Handle();
    public Language language;
    public UsersFB usersFB;
    public Users users;
    public ServerAPI serverAPI = new ServerAPI();

    private ListView lvLeftDrawer;
    private ArrayList<DrawerModels> dsDrawerModelses;
    private DrawerAdapter drawerAdapter;

    private LoginButton loginButton;
    private Button btnLeaarnNow, btnWhatDoPeopleLearn, btnAdd, btnLearnByTopic;
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private NavigationView navigationViewRight, navigationViewLeft;
    private DrawerLayout drawer;
    private ImageView imgAvatarMin, imgAvatarMax;
    private TextView txtNameMin, txtNameMax, txtInviteFriends, txtShare;
    private ConstraintLayout layoutLogInFB;
    private ProgressBar pbLoadingMax;
    private ProgressBar pbLoadingMin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        language = new Language();
        checkSelectLanguage();
        setContentView(R.layout.activity_list);
        AdFast.loadAdView(this, (RelativeLayout) findViewById(R.id.totalLayout), getString(R.string.ad_id_small_1));
        addControns();
        addEvents();
    }

    private void addEvents() {

        btnLearnByTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleLearnByTopic();
            }
        });
        btnLeaarnNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleLearnNow();
            }
        });
        btnWhatDoPeopleLearn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleWhatDoPeopleLearn();
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleAdd();
            }
        });
        navigationViewLeft.setNavigationItemSelectedListener(mNavLeft);
        layoutLogInFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!usersFB.confirmLogin() || cfAutoLogin){
                    cfAutoLogin = false;
                    try {
                        LoginManager.getInstance().logOut(); //logout facebook
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    myIntentFilter();
                    loginButton.performClick();
                    MyAction.setLoadedTopic(ListActivity.this, false);
                    MyAction.setRefreshTopic(ListActivity.this, true);
                }
            }
        });
        imgAvatarMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Drawer","On");
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.openDrawer(GravityCompat.START);
                drawerAdapter.notifyDataSetChanged();
            }
        });

        txtInviteFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serverAPI.getLink(ListActivity.this, Constants.INVITE);
            }
        });

        txtShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serverAPI.getLink(ListActivity.this, Constants.SHARE);
            }
        });
    }

    public void handleLogOut() {
        if (usersFB.confirmLogin()) {
            new AlertDialog.Builder(ListActivity.this)
                    .setMessage(getString(R.string.youAreAlreadyLoggedOnByTheAccount) + " " + spSV.getString("name", "English Bigger"))
                    .setNegativeButton(getString(R.string.no), null)
                    .setPositiveButton(getString(R.string.logout), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            outUsers();

                        }
                    }).show();
        }else {
            Toast.makeText(ListActivity.this, getString(R.string.msgYouAreNotLoggedIn), Toast.LENGTH_SHORT).show();
        }
    }

    private void outUsers(){
        myIntentFilter(); //Listen for the change of function "confirmLogin(Activity, boolean)" and if function "confirmLogin(Activity) = true" logout facebook
        usersFB.confirmLogin(false);
    }


    private void handleWhatDoPeopleLearn() {
        if (usersFB.confirmLogin()){
            MyAction.setActivityBulb(this, MyAction.LIST_ACTIVITY);
            MyAction.setFragmentNew(this, MyAction.WDPL_FRAGMENT);
            IntentActivity.handleOpenTopicActivy(ListActivity.this);
        }else {
            loginRequired();
        }

    }

    private void handleAdd() {
        if (usersFB.confirmLogin()){
            MyAction.setActivityBulb(this, MyAction.LIST_ACTIVITY);
            MyAction.setFragmentNew(this, MyAction.ADD_NEW_WORD_FRAGMENT);
            IntentActivity.handleOpenLearnActivity(ListActivity.this);
        }else {
            loginRequired();
        }
    }

    private void handleLearnNow() {
        if (usersFB.confirmLogin()){
            MyAction.setActivityBulb(this, MyAction.LIST_ACTIVITY);
            MyAction.setFragmentNew(this, MyAction.LEARN_FRAGMENT);
            IntentActivity.handleOpenLearnActivity(ListActivity.this);
        }else {
            loginRequired();
        }
    }

    private void handleLearnByTopic() {
        if (usersFB.confirmLogin()){
            MyAction.setActivityBulb(this, MyAction.LIST_ACTIVITY);
            MyAction.setFragmentNew(this, MyAction.TOPIC_FRAGMENT);
            IntentActivity.handleOpenTopicActivy(ListActivity.this);
        }else {
            loginRequired();
        }
    }

    private void loginRequired(){
        Toast.makeText(ListActivity.this, getString(R.string.msgPleaseLoginToUseThisFeature), Toast.LENGTH_LONG).show();
        Log.d("Drawer","On");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.openDrawer(GravityCompat.START);
    }

    private void addControns() {
        language = new Language(ListActivity.this);
        usersFB = new UsersFB(ListActivity.this);
        users = new Users(ListActivity.this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        appBarLayout = findViewById(R.id.appBarLayout);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close){

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState(); //will synchronize the changed icon's state, remove the syncState(), you will realize that the icon of arrow won't rotate any more.
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        navigationViewLeft = (NavigationView) findViewById(R.id.nav_view_left);
        btnLeaarnNow = (Button) findViewById(R.id.btnLearnNow);
        btnWhatDoPeopleLearn = (Button) findViewById(R.id.btnWhatDoPeopleLearn);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnLearnByTopic = (Button) findViewById(R.id.btnLearnByTopic);
        imgAvatarMax = (ImageView) navigationViewLeft.findViewById(R.id.imgAvatarMax);
        imgAvatarMin = (ImageView) findViewById(R.id.imgAvatarMin);
        txtNameMax = (TextView) navigationViewLeft.findViewById(R.id.txtNameMax);
        txtNameMin = (TextView) findViewById(R.id.txtNameMin);
        txtInviteFriends = (TextView) findViewById(R.id.txtInviteFriends);
        txtShare = (TextView) findViewById(R.id.txtShare);
        layoutLogInFB = (ConstraintLayout) navigationViewLeft.findViewById(R.id.layoutLoginFB);
        pbLoadingMin = (ProgressBar) findViewById(R.id.pbLoadingMin);
        pbLoadingMax = (ProgressBar) findViewById(R.id.pbLoadingMax);
        pf = getSharedPreferences(getString(R.string.saveInforLoginFB),MODE_PRIVATE);
        spSV = getSharedPreferences(getString(R.string.saveInforUser),MODE_PRIVATE);
        FacebookSdk.sdkInitialize(FacebookSdk.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        handleSetOptionsDrawerLeft();
        handleLogInFB();
        handleShowInfor();
        handleShowButtomLogin();
    }


    private void handleSetOptionsDrawerLeft() {
        lvLeftDrawer = (ListView) navigationViewLeft.findViewById(R.id.lvLeftDrawer);
        dsDrawerModelses = new ArrayList<>();
        dsDrawerModelses.add(new DrawerModels(1, R.drawable.ic_info ,getString(R.string.myInfor)));
        dsDrawerModelses.add(new DrawerModels(2, R.drawable.ic_friends ,getString(R.string.friend)));
        dsDrawerModelses.add(new DrawerModels(3, R.drawable.ic_accout_settings ,getString(R.string.settings)));
        dsDrawerModelses.add(new DrawerModels(4, R.drawable.ic_policy ,getString(R.string.policy)));
        dsDrawerModelses.add(new DrawerModels(5, R.drawable.ic_support ,getString(R.string.support)));
        dsDrawerModelses.add(new DrawerModels(6, R.drawable.ic_feedback ,getString(R.string.feedback)));
        dsDrawerModelses.add(new DrawerModels(7, R.drawable.ic_logout ,getString(R.string.logout)));
        drawerAdapter = new DrawerAdapter(
                ListActivity.this,
                R.layout.item_left_drawer,
                dsDrawerModelses);
        lvLeftDrawer.setAdapter(drawerAdapter);
    }

    public void handleShowInfor() {
        if (usersFB.confirmLogin()){
            pbLoadingMin.setVisibility(View.VISIBLE);
            pbLoadingMax.setVisibility(View.VISIBLE);

        }else {
            pbLoadingMin.setVisibility(View.GONE);
            pbLoadingMax.setVisibility(View.GONE);

        }
        try{
            String url = users.getUrlAvatar();
            String name = users.getFirstName();

            loadAvatar(url);

            if (usersFB.confirmLogin()){
                txtNameMax.setText(name);
                txtNameMin.setText(name);
            }else {
                txtNameMin.setText(getString(R.string.app_name));
                txtNameMax.setText(getString(R.string.app_name));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void loadAvatar(String url) {
        if (cfDeleteImgCache){
            // Deletes the image in the buffer and loads the new image

            cfDeleteImgCache = false;
            Glide.with(this)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .dontAnimate()
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            pbLoadingMax.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            pbLoadingMax.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(imgAvatarMax);
            Glide.with(this)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .dontAnimate()
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            pbLoadingMin.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            pbLoadingMin.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(imgAvatarMin);

        }else {
            //Do not delete the images in the buffer
            Glide.with(this)
                    .load(url)
                    .dontAnimate()
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            pbLoadingMax.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            pbLoadingMax.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(imgAvatarMax);
            Glide.with(this)
                    .load(url)
                    .dontAnimate()
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            pbLoadingMin.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            pbLoadingMin.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(imgAvatarMin);
        }
    }

    private void handleLogInFB() {
        loginButton.setReadPermissions(Arrays.asList(
                "public_profile",
                "email",
                "user_birthday",
                "user_friends",
                "user_location"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                final String accessToken = loginResult.getAccessToken().getToken();
                Log.i("accessToken", accessToken + "\nloginResult.getAccessToken(): "+loginResult.getAccessToken().getUserId());
                GraphRequest graphRequest = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.d("JSON","INFO Login facebook: " + response.getJSONObject().toString());
                                getInFo(object,accessToken);
                            }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields","id,name,email,gender,birthday,first_name,location");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
                //get friends

            }

            @Override
            public void onCancel() {
                usersFB.confirmLogin(false);
            }

            @Override
            public void onError(FacebookException error) {
                usersFB.confirmLogin(false);
            }
        });
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("DATA", +requestCode+"\n"+resultCode+"\n"+data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void getInFo(JSONObject object, String accessToken){

        String id="", name = "", first_name = "", email = "", gender = "", birthday = "",
                link = "", location = "", linkAvatar = "";
        String url2 = "/picture?type=large&width=512&height=512";
        String url1 = "https://graph.facebook.com/";
        try {
            id = object.getString("id");
            first_name = object.getString("first_name");
            name = object.getString("name");
        }catch (JSONException e){
            e.printStackTrace();
        }
        try {
            email = object.getString("email");
        }catch (JSONException e){
            e.printStackTrace();
        }
        try {
            birthday = object.getString("birthday");
        }catch (JSONException e){
            e.printStackTrace();
        }
        try {
            location = object.getJSONObject("location").getString("name");
        }catch (JSONException e){
            e.printStackTrace();
        }
        try {
            gender = object.getString("gender");
            if(gender.equals("male")){
                gender = "Nam";
            }else if(gender.equals("female")){
                gender = "Nữ";
            }else {
                gender = "Không xác định";
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        link = "https://www.facebook.com/"+id;
        linkAvatar = url1+id+url2;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("idUser",id);
            jsonObject.put("email",email);
            jsonObject.put("first_name",first_name);
            jsonObject.put("name",name);
            jsonObject.put("gender",gender);
            jsonObject.put("birthday",birthday);
            jsonObject.put("location",location);
            jsonObject.put("type_register","Social");
            jsonObject.put("urlAvatar",linkAvatar);
            jsonObject.put("accessToken",accessToken);
            jsonObject.put("token_firebase",users.getTokenFirebase());
            jsonObject.put("type","_INSERT");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("length",accessToken.length()+"");
        serverAPI.postUsers(ListActivity.this,jsonObject,Constants.LOGIN);

        usersFB.setIdUser(id);
        usersFB.setAccessToken(accessToken);
        usersFB.setFirstName(first_name);
        usersFB.setName(name);
        usersFB.setBirthday(birthday);
        usersFB.setEmail(email);
        usersFB.setGender(gender);
        usersFB.setLocation(location);
        usersFB.setUrlAvatar(linkAvatar);

        Log.i("INFOR","Facebook:\n ID: "+id
                +"\nNAME: "+name
                +"\nEMAIL: "+email
                +"\nGENDER: "+gender
                +"\nBIRTHDAY: "+birthday
                +"\nLOCATION: "+location
                +"\nLINK_HOME: "+link
                +"\nLINK_AVATAR: "+linkAvatar);
    }


    private void checkSelectLanguage() {

        Log.d("LAG", language.getLanguage(ListActivity.this)+" : "+language.getCFLanguageDivice(ListActivity.this));
        if (language.getLanguage(ListActivity.this).equals("") && language.getCFLanguageDivice(ListActivity.this)){
            handleSelectLocale();
        }else {
            language.settingLanguage(ListActivity.this);
            Log.d("Divice",language.getCFLanguageDivice(ListActivity.this)+"");
        }
    }

    private void handleSelectLocale() {
        ArrayList<LocaleModels> dsLocaleModelses = new ArrayList<>();
        LocaleAdapter localeAdapter;

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_dialog, null);
        dialogBuilder.setView(dialogView);

        RecyclerView rvLocale = (RecyclerView) dialogView.findViewById(R.id.rvLocale);
        rvLocale.setLayoutManager(new LinearLayoutManager(ListActivity.this, LinearLayoutManager.VERTICAL, false));
        LinearLayout layoutDevice = (LinearLayout) dialogView.findViewById(R.id.layoutDevice);
        dsLocaleModelses.add(new LocaleModels("en"));
        dsLocaleModelses.add(new LocaleModels("vi"));
        dsLocaleModelses.add(new LocaleModels("zh"));
        dsLocaleModelses.add(new LocaleModels("ja"));
        localeAdapter = new LocaleAdapter(
                ListActivity.this,
                dsLocaleModelses
        );
        rvLocale.setAdapter(localeAdapter);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
        layoutDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                language.setCFLanguageDivice(true);
                language.setLanguage(Locale.getDefault().getLanguage());
                alertDialog.cancel();
            }
        });
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }


    }

    NavigationView.OnNavigationItemSelectedListener mNavLeft = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);

            return true;
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                if ((keyCode == KeyEvent.KEYCODE_BACK)) {
                    if(SystemClock.elapsedRealtime()-mLastClickTime<1000){
                        drawer.closeDrawer(GravityCompat.START);
                    }else {
                        drawerAdapter.notifyDataSetChanged();
                    }
                    mLastClickTime=SystemClock.elapsedRealtime();
                }
            }
        }catch (Exception e){
            return false;
        }
        return false;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        language.settingLanguage(ListActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyAction.setAction(this,false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyAction.setPosition(this,0);
        MyAction.setAction(this,true);
        //Function checkRefreshDateLogin:
        //Check if the login session has expired?
        //The login session expires:
        //Renewed login session add return true
        if (MyAction.checkRefreshDateLogin(this))
            //Check if you have ever logged in before? Will login automatically!
            if (!users.getIdUser().isEmpty()) layoutLogInFB.performClick();

    }

    public void handleShowButtomLogin(){
        if (usersFB.confirmLogin()){
            layoutLogInFB.setVisibility(View.GONE);
        }else {
            layoutLogInFB.setVisibility(View.VISIBLE);
        }
    }

    public void myIntentFilter(){
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(cfBroadcastAction);
        //mIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver,mIntentFilter);
        Log.i("DK","mReceiver");
    }
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(cfBroadcastAction)) {
                boolean cf = intent.getBooleanExtra("CF",false);
                Log.i("BR","BroadcastReceiver: Notify: CF: "+cf);
                if (cf){
                    try {
                        layoutLogInFB.setVisibility(View.GONE);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    LoginManager.getInstance().logOut(); //logout facebook
                    layoutLogInFB.setVisibility(View.VISIBLE);
                    users.setUser(null, null, getString(R.string.app_name), null, null, null, null, null, null);
                    usersFB.setUserFB(null, null, getString(R.string.app_name), null, null, null, null, null, null, null);
                    Toast.makeText(ListActivity.this, getString(R.string.accountSuccessfullyLoggedOut), Toast.LENGTH_LONG).show();
                }
                handleShowInfor();
                drawerAdapter.notifyDataSetChanged();
                layoutLogInFB.setEnabled(true);
                //unregisterReceiver(mReceiver);

            }
        }
    };

}
