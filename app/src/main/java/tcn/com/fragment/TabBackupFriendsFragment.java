package tcn.com.fragment;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import tcn.com.adapters.BackupTopicAdapter;
import tcn.com.englishbigger.R;
import tcn.com.englishbigger.TopicActivity;
import tcn.com.handle.Constants;
import tcn.com.models.TopicModels;

public class TabBackupFriendsFragment extends Fragment {

    TopicActivity topicActivity;
    public static String BROADCAST_ACTION = "BROADCAST_ACTION";
    RecyclerView rvItem;
    private BackupTopicAdapter backupTopicAdapter;
    private ProgressDialog dialog;

    public TabBackupFriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_backup_friends, container, false);
        addControls(view);
        addEvents();

        return view;
    }

    private void addEvents() {
        handleGetListFriendsUseApp();
    }

    public void handleGetListFriendsUseApp() {
        myIntentFilter();//Registered broadcast action
        dialog = new ProgressDialog(topicActivity);
        dialog.setMessage(topicActivity.getString(R.string.pleaseWait));
        dialog.setCancelable(false);
        dialog.show();
        if (topicActivity.usersFB.confirmLogin()){
            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/"+topicActivity.usersFB.getIdUser()+"/friends",
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            /* handle the result */
                            try {
                                String id="";

                                JSONArray json = null;
                                if (!response.getJSONObject().isNull("data")){
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
                                    new AlertDialog.Builder(topicActivity)
                                            .setMessage(topicActivity.getString(R.string.noFriendsUseThisApp))
                                            .setNegativeButton(topicActivity.getString(R.string.close),null)
                                            .setPositiveButton(topicActivity.getString(R.string.inviteFriends), new DialogInterface.OnClickListener() {
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
            Toast.makeText(topicActivity, topicActivity.getString(R.string.msgYouAreNotLoggedIn), Toast.LENGTH_SHORT).show();
        }


    }

    private void showTopicFriend() {
        rvItem.setLayoutManager(new LinearLayoutManager(topicActivity, LinearLayoutManager.VERTICAL, false));
        backupTopicAdapter = new BackupTopicAdapter(
                topicActivity,
                R.layout.item_topic_of_everyone,
                topicActivity.topicModes);
        rvItem.setAdapter(backupTopicAdapter);

    }

    private void addControls(View view) {
        rvItem = view.findViewById(R.id.rvItem);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TopicActivity) {
            this.topicActivity = (TopicActivity) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public void myIntentFilter(){
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BROADCAST_ACTION);
        topicActivity.registerReceiver(mReceiver,mIntentFilter);
        Log.i("DK","mReceiver");
    }
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BROADCAST_ACTION)) {
                try {
                    dialog.cancel();
                }catch (Exception e){

                }
                topicActivity.topicModes = new ArrayList<>();
                topicActivity.topicModes = (ArrayList<TopicModels>) intent.getSerializableExtra("TOPIC");
                showTopicFriend();
                topicActivity.unregisterReceiver(mReceiver);
            }
        }
    };

}
