package tcn.com.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

import tcn.com.englishbigger.R;
import tcn.com.englishbigger.TopicActivity;
import tcn.com.handle.Constants;
import tcn.com.models.DrawerModels;
import tcn.com.models.TopicModels;

/**
 * Created by MyPC on 12/11/2017.
 */

public class WhatDoPeopleLearnAdapter extends ArrayAdapter<DrawerModels> {
    public static String broadcastAction = "broadcastActionAdapter";
    private tcn.com.englishbigger.TopicActivity topicActivity;
    private Activity context;
    private int resource;
    private List<DrawerModels> objects;
    private ImageView imgShow;
    private ImageView imgShowed;
    private TextView txtAllFriends;
    private ConstraintLayout layoutDevice;
    private RecyclerView rvItem;

    private View v;
    private int type = 1;
    private ProgressDialog dialog;

    public WhatDoPeopleLearnAdapter(@NonNull Activity context, @LayoutRes int resource, @NonNull List<DrawerModels> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = this.context.getLayoutInflater();
        final View row = inflater.inflate(this.resource, null);

        topicActivity = (TopicActivity) context;

        final LinearLayout layoutMainItem = row.findViewById(R.id.layoutMainItem);
        TextView txtNameMainItem = row.findViewById(R.id.txtNameMainItem);
        ImageView imgMainItem = row.findViewById(R.id.imgMainItem);

        txtAllFriends = row.findViewById(R.id.txtAllFriends);
        txtAllFriends.setVisibility(View.GONE);
        layoutDevice = row.findViewById(R.id.layoutDevice);
        layoutDevice.setVisibility(View.GONE);

        final DrawerModels drawerModels = this.objects.get(position);

        imgMainItem.setImageResource(drawerModels.getAvatar());
        txtNameMainItem.setText(drawerModels.getName());
        layoutMainItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (v != null && v != row){
                    LinearLayout layoutLanguageChild = v.findViewById(R.id.layoutLanguageChild);
                    imgShow = v.findViewById(R.id.imgShow);
                    imgShowed = v.findViewById(R.id.imgShowed);
                    layoutLanguageChild.setVisibility(View.GONE);
                    imgShow.setVisibility(View.VISIBLE);
                    imgShowed.setVisibility(View.GONE);
                }
                v = row;
                closeOrOpen(drawerModels);
            }
        });
        return row;
    }

    private void closeOrOpen(DrawerModels drawerModels){
        LinearLayout layoutLanguageChild = v.findViewById(R.id.layoutLanguageChild);
        imgShow = v.findViewById(R.id.imgShow);
        imgShowed = v.findViewById(R.id.imgShowed);

        if (layoutLanguageChild.getVisibility() == View.VISIBLE){
            imgShow.setVisibility(View.VISIBLE);
            imgShowed.setVisibility(View.GONE);
            layoutLanguageChild.setVisibility(View.GONE);

        }else {
            imgShow.setVisibility(View.GONE);
            imgShowed.setVisibility(View.VISIBLE);
            layoutLanguageChild.setVisibility(View.VISIBLE);

            myIntentFilter(); //Register to listen to the results returned
            if (drawerModels.getName().equals(context.getString(R.string.friend))){
                handleGetListFriendsUseApp();

            }else if (drawerModels.getName().equals(context.getString(R.string.other))){
                type = 1;
                JSONObject object = new JSONObject();
                try {
                    object.put("idUser", topicActivity.usersFB.getIdUser());
                    topicActivity.serverAPI.getTopic(topicActivity, object, Constants.GET_ALL_TOPIC);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }


    private void handleGetListFriendsUseApp() {

        dialog = new ProgressDialog(context);
        dialog.setMessage(context.getString(R.string.pleaseWait));
        dialog.setCancelable(false);
        if (type != 0){
            dialog.show();
        }
        type = 0;
        if (topicActivity.usersFB.confirmLogin()){
            SharedPreferences spSV = context.getSharedPreferences(context.getString(R.string.saveInforUser), context.MODE_PRIVATE);
            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/"+spSV.getString("id","")+"/friends",
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            /* handle the result */
                            try {
                                String id="";

                                JSONArray json = null;
                                if (response.getJSONObject().isNull("data")==false){
                                    try {
                                        json = response.getJSONObject().getJSONArray("data");
                                    } catch (JSONException e) {
                                        dialog.dismiss();
                                        e.printStackTrace();
                                    }
                                    for (int i = 0; i < json.length(); i++){
                                        try {
                                            id += json.getJSONObject(i).getString("id") + ";";
                                            Log.i("INFOR",(i+1)+ ") Friends Facebook:\n ID: "+id);


                                        }catch (JSONException e){
                                            dialog.dismiss();
                                            e.printStackTrace();
                                        }

                                    }

                                    JSONObject object = new JSONObject();
                                    try {

                                        object.put("allIDUsers", id);
                                        topicActivity.serverAPI.getTopic(topicActivity, object, Constants.GET_TOPIC_FRIENDS);

                                    } catch (JSONException e) {
                                        dialog.dismiss();
                                        e.printStackTrace();
                                    }

                                }else {
                                    new AlertDialog.Builder(context)
                                            .setMessage(context.getString(R.string.noFriendsUseThisApp))
                                            .setNegativeButton(context.getString(R.string.close),null)
                                            .setPositiveButton(context.getString(R.string.inviteFriends), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    topicActivity.serverAPI.getLink(topicActivity, Constants.INVITE);
                                                }
                                            }).show();
                                }

                            }catch (EmptyStackException e){
                                dialog.dismiss();
                                e.printStackTrace();
                            }
                        }
                    }
            ).executeAsync();
        }else {
            dialog.dismiss();
            Toast.makeText(context, context.getString(R.string.msgYouAreNotLoggedIn), Toast.LENGTH_SHORT).show();
        }


    }

    private void handleShowTopic(ArrayList<TopicModels> topicModels) {
        Log.d("SIZE_TOPIC", topicModels.size() +"");
        BackupTopicAdapter backupTopicAdapter;
        rvItem = v.findViewById(R.id.rvItem);
        rvItem.setBackgroundResource(R.drawable.bgr_list_ac);
        rvItem.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        backupTopicAdapter = new BackupTopicAdapter(
                context,
                R.layout.item_topic_of_everyone,
                topicModels
        );
        rvItem.setAdapter(backupTopicAdapter);
    }

    public void myIntentFilter(){
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(broadcastAction);
        topicActivity.registerReceiver(mReceiver,mIntentFilter);
        Log.i("DK","mReceiver");
    }
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(broadcastAction)) {
                if (type == 0){
                    try {
                        dialog.cancel();
                    }catch (Exception e){

                    }
                }
                topicActivity.topicModes = new ArrayList<>();
                topicActivity.topicModes = (ArrayList<TopicModels>) intent.getSerializableExtra("TOPIC");
                handleShowTopic(topicActivity.topicModes);

                topicActivity.unregisterReceiver(mReceiver);
            }
        }
    };
}
