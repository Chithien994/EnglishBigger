package com.tcn.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;

import com.tcn.englishbigger.R;
import com.tcn.handle.Handle;
import com.tcn.models.DrawerModels;
import com.tcn.models.LocaleModels;
import com.tcn.models.UsersModels;


/**
 * Created by MyPC on 02/08/2017.
 */

public class DrawerAdapter extends ArrayAdapter<DrawerModels> {
    com.tcn.englishbigger.ListActivity listActivity;
    Activity context;
    int resource;
    List<DrawerModels> objects;
    ImageView imgShow;
    ImageView imgShowed;
    Handle handle = new Handle();
    AlertDialog alertDialog;
    public static String MY_BRC_DRAWER_ADAPTER = "MY_BRC_DRAWER_ADAPTER";
    public DrawerAdapter(Activity context, int resource, List<DrawerModels> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = this.context.getLayoutInflater();
        listActivity = (com.tcn.englishbigger.ListActivity) context;
        final View row = inflater.inflate(this.resource, null);

        final LinearLayout layoutMainItem = row.findViewById(R.id.layoutMainItem);
        TextView txtNameMainItem = row.findViewById(R.id.txtNameMainItem);
        ImageView imgMainItem = row.findViewById(R.id.imgMainItem);

        final DrawerModels drawerModels = this.objects.get(position);

        imgMainItem.setImageResource(drawerModels.getAvatar());
        txtNameMainItem.setText(drawerModels.getName());

        layoutMainItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerModels.getName().equals(context.getString(R.string.logout))){
                    listActivity.handleLogOut();
                }else if (drawerModels.getName().equals(context.getString(R.string.myInfor))){
                    handleShowInfo(row);
                }else if (drawerModels.getName().equals(context.getString(R.string.friend))){
                    handleGetListFriendsUseApp(row);
                }else if (drawerModels.getName().equals(context.getString(R.string.settings))){
                    handleSettingLanguage(row);
                }else if (drawerModels.getName().equals(context.getString(R.string.policy))){
                    handleShowPolicy(row);
                }else if (drawerModels.getName().equals(context.getString(R.string.support))){
                    support(row);
                }else if (drawerModels.getName().equals(context.getString(R.string.feedback))){
                    handleFeedBack(row);
                }
            }
        });
        if (drawerModels.getName().equals(context.getString(R.string.logout)) || drawerModels.getName().equals(context.getString(R.string.feedback))){
            imgShow = row.findViewById(R.id.imgShow);
            imgShow.setVisibility(View.GONE);
            LinearLayout layoutMore = row.findViewById(R.id.layoutMore);
            layoutMore.setVisibility(View.VISIBLE);
        }else if (drawerModels.getName().equals(context.getString(R.string.policy))){
            LinearLayout layoutMore = row.findViewById(R.id.layoutMore);
            layoutMore.setVisibility(View.VISIBLE);
        }else if (drawerModels.getName().equals(context.getString(R.string.settings))){
            LinearLayout layoutMore = row.findViewById(R.id.layoutMore);
            layoutMore.setVisibility(View.VISIBLE);
        }
        return row;
    }

    private void handleFeedBack(final View row){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = context.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.layout_dialog_feedback, null);
        dialogBuilder.setView(dialogView);

        ImageView imgCloseDialog = dialogView.findViewById(R.id.imgCloseDialog);
        final EditText txtContentDialog = dialogView.findViewById(R.id.txtContentDialog);
        Button btnPostDialog = dialogView.findViewById(R.id.btnPostDialog);

        alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(true);
        alertDialog.show();

        btnPostDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Handle.hideKeyboard(listActivity, row);
                if (!txtContentDialog.getText().toString().equals("")){
                    JSONObject object = new JSONObject();
                    try {
                        myIntentFilter();
                        object.put("idUser", listActivity.users.getIdUser());
                        object.put("content",txtContentDialog.getText().toString());
                        listActivity.serverAPI.postFeedBack(listActivity, object);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(listActivity,listActivity.getString(R.string.msgYouHaveNotEnteredAnythingYet), Toast.LENGTH_LONG).show();
                }

            }
        });

        imgCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Handle.hideKeyboard(listActivity, row);
                alertDialog.cancel();
            }
        });
    }

    private void handleGetListFriendsUseApp(final View row) {
        LinearLayout layoutLanguageChild = row.findViewById(R.id.layoutLanguageChild);
        if (layoutLanguageChild.getVisibility()==View.GONE){
            final ProgressDialog dialog = new ProgressDialog(context);
            dialog.setMessage(context.getString(R.string.pleaseWait));
            dialog.setCancelable(false);
            dialog.show();
            if (listActivity.usersFB.confirmLogin()){
                SharedPreferences spSV = context.getSharedPreferences(context.getString(R.string.saveInforUser), context.MODE_PRIVATE);
                new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/"+spSV.getString("id","")+"/friends",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                            /* handle the result */
                                dialog.dismiss();
                                try {
                                    handleShowFriends(row, response.getJSONObject());
                                }catch (EmptyStackException e){
                                    e.printStackTrace();
                                }
                            }
                        }
                ).executeAsync();
            }else {
                dialog.dismiss();
                Toast.makeText(context, context.getString(R.string.msgYouAreNotLoggedIn), Toast.LENGTH_SHORT).show();
            }
        }else {
            imgShow = row.findViewById(R.id.imgShow);
            imgShowed = row.findViewById(R.id.imgShowed);
            imgShow.setVisibility(View.VISIBLE);
            imgShowed.setVisibility(View.GONE);
            hideOptionView(layoutLanguageChild);
        }


    }

    private void handleShowFriends(View row, JSONObject object) {
        imgShow = row.findViewById(R.id.imgShow);
        imgShowed = row.findViewById(R.id.imgShowed);
        LinearLayout layoutLanguageChild = row.findViewById(R.id.layoutLanguageChild);
        TextView txtAllFriends = row.findViewById(R.id.txtAllFriends);
        final ConstraintLayout layoutDevice = row.findViewById(R.id.layoutDevice);
        layoutDevice.setVisibility(View.GONE);
        RecyclerView rvItem = row.findViewById(R.id.rvItem);

        if (object!=null) {

            ArrayList<UsersModels> dsUsersModels = new ArrayList<>();
            FriendsAdapter friendsAdapter;

            String id="", name = "", linkAvatar = "";
            String url2 = "/picture?type=large&width=512&height=512";
            String url1 = "https://graph.facebook.com/";
            int num = 0;

            JSONArray json = null;
            if (object.isNull("data")==false){
                try {
                    json = object.getJSONArray("data");
                    num = json.length();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                txtAllFriends.setText(context.getString(R.string.allFriends) + " " + num);

                for (int i = 0; i < json.length(); i++){
                    try {
                        id = json.getJSONObject(i).getString("id");
                        name = json.getJSONObject(i).getString("name");
                        linkAvatar = url1 + id + url2;
                        Log.i("INFOR",(i+1)+ ") Friends Facebook:\n ID: "+id
                                +"\nNAME: "+name
                                +"\nLINK_AVATAR: "+linkAvatar);
                        dsUsersModels.add(new UsersModels(id, linkAvatar, name));


                    }catch (JSONException e){
                        e.printStackTrace();
                    }

                }
                rvItem.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                friendsAdapter = new FriendsAdapter(
                        context,
                        dsUsersModels
                );
                rvItem.setAdapter(friendsAdapter);

            }else {
                new AlertDialog.Builder(context)
                        .setMessage(context.getString(R.string.noFriendsUseThisApp))
                        .setNegativeButton(context.getString(R.string.close),null)
                        .setPositiveButton(context.getString(R.string.inviteFriends), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(context,context.getString(R.string.functionIsBeingUpdated),Toast.LENGTH_SHORT).show();
                            }
                        }).show();
            }

        }else {
            Toast.makeText(context,context.getString(R.string.errorInternetConnectionProblems),Toast.LENGTH_SHORT).show();
        }
        if (layoutLanguageChild.getVisibility() == View.GONE){
            showOptionView(layoutLanguageChild);
            imgShow.setVisibility(View.GONE);
            imgShowed.setVisibility(View.VISIBLE);
        }
    }

    private void support(View row) {
        imgShow = row.findViewById(R.id.imgShow);
        imgShowed = row.findViewById(R.id.imgShowed);
        if (imgShowed.getVisibility()==View.VISIBLE){
            imgShow.setVisibility(View.VISIBLE);
            imgShowed.setVisibility(View.GONE);
        }else {
            imgShow.setVisibility(View.GONE);
            imgShowed.setVisibility(View.VISIBLE);
        }
    }

    private void handleShowPolicy(View row) {
        imgShow = row.findViewById(R.id.imgShow);
        imgShowed = row.findViewById(R.id.imgShowed);
        if (imgShowed.getVisibility()==View.VISIBLE){
            imgShow.setVisibility(View.VISIBLE);
            imgShowed.setVisibility(View.GONE);
        }else {
            imgShow.setVisibility(View.GONE);
            imgShowed.setVisibility(View.VISIBLE);
        }
    }

    private void handleShowInfo(View row) {
        if (listActivity.usersFB.confirmLogin()){
            imgShow = row.findViewById(R.id.imgShow);
            imgShowed = row.findViewById(R.id.imgShowed);
            LinearLayout layoutChildItem = row.findViewById(R.id.layoutChildItem);
            TextView txtNameChildItem,txtBirthdayChildItem,txtGenderChildItem,txtLocationChildItem,txtEmailChildItem;
            txtNameChildItem = row.findViewById(R.id.txtNameChildItem);
            txtBirthdayChildItem = row.findViewById(R.id.txtBirthdayChildItem);
            txtGenderChildItem = row.findViewById(R.id.txtGenderChildItem);
            txtLocationChildItem = row.findViewById(R.id.txtLocationChildItem);
            txtEmailChildItem = row.findViewById(R.id.txtEmailChildItem);

            txtNameChildItem.setText(listActivity.users.getName());
            txtBirthdayChildItem.setText(listActivity.users.getBirthday());
            txtGenderChildItem.setText(listActivity.users.getGender());
            txtLocationChildItem.setText(listActivity.users.getLocation());
            txtEmailChildItem.setText(listActivity.users.getEmail());

            if (layoutChildItem.getVisibility()==View.VISIBLE){
                hideOptionView(layoutChildItem);
                imgShow.setVisibility(View.VISIBLE);
                imgShowed.setVisibility(View.GONE);
            }else {
                showOptionView(layoutChildItem);
                imgShow.setVisibility(View.GONE);
                imgShowed.setVisibility(View.VISIBLE);
            }
        }else {
            Toast.makeText(context, context.getString(R.string.msgYouAreNotLoggedIn), Toast.LENGTH_SHORT).show();
        }
    }

    private void handleSettingLanguage(View row) {
        imgShow = row.findViewById(R.id.imgShow);
        imgShowed = row.findViewById(R.id.imgShowed);
        LinearLayout layoutLanguageChild = row.findViewById(R.id.layoutLanguageChild);
        TextView txtAllFriends = row.findViewById(R.id.txtAllFriends);
        final ConstraintLayout layoutDevice = row.findViewById(R.id.layoutDevice);
        RecyclerView rvItem = row.findViewById(R.id.rvItem);
        final ImageView imgSelected = row.findViewById(R.id.imgSelected);

        txtAllFriends.setVisibility(View.GONE);

        ArrayList<LocaleModels> dsLocaleModelses = new ArrayList<>();
        LocaleAdapter localeAdapter;

        dsLocaleModelses.add(new LocaleModels("en"));
        dsLocaleModelses.add(new LocaleModels("vi"));
        dsLocaleModelses.add(new LocaleModels("zh"));
        dsLocaleModelses.add(new LocaleModels("ja"));

        rvItem.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        localeAdapter = new LocaleAdapter(
                listActivity,
                dsLocaleModelses
        );
        rvItem.setAdapter(localeAdapter);

        if (listActivity.language.getCFLanguageDivice(listActivity)){
            imgSelected.setVisibility(View.VISIBLE);
        }else {
            imgSelected.setVisibility(View.GONE);
        }
        Log.d("Device",listActivity.language.getCFLanguageDivice(listActivity)+"");

        Log.d("Language number", dsLocaleModelses.size()+"");

        layoutDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgSelected.setVisibility(View.VISIBLE);
                listActivity.language.setCFLanguageDivice(true);
                listActivity.language.setLanguage(listActivity.language.getLanguageDivice());
                listActivity.language.settingLanguage(listActivity.language.getLanguageDivice());
                Log.d("LanguageDivice", listActivity.language.getLanguageDivice());

            }
        });
        if (layoutLanguageChild.getVisibility() == View.VISIBLE){
            hideOptionView(layoutLanguageChild);
            imgShow.setVisibility(View.VISIBLE);
            imgShowed.setVisibility(View.GONE);
        }else {
            showOptionView(layoutLanguageChild);
            imgShow.setVisibility(View.GONE);
            imgShowed.setVisibility(View.VISIBLE);
        }
    }

    private void hideOptionView(final LinearLayout layout){
        layout.animate().setInterpolator(new AccelerateDecelerateInterpolator()).scaleY(0)
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        layout.setVisibility(View.GONE);
                    }
                });
    }

    private void showOptionView(final LinearLayout layout){
        layout.animate().setInterpolator(new AccelerateDecelerateInterpolator()).scaleY(1)
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        layout.setVisibility(View.VISIBLE);
                    }
                });

        //layoutOptionView.animate().setInterpolator(new AccelerateDecelerateInterpolator()).scaleX(1).scaleY(1);
    }

    public void myIntentFilter(){
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(MY_BRC_DRAWER_ADAPTER);
        context.registerReceiver(mReceiver,mIntentFilter);
        Log.i("DK","mReceiver");
    }
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MY_BRC_DRAWER_ADAPTER)) {
                context.unregisterReceiver(mReceiver);
                Log.d("unregisterReceiver","Unregister Receiver");
                if(!intent.getBooleanExtra("ERR",true))
                    try {
                        alertDialog.cancel();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
            }
        }
    };
}
