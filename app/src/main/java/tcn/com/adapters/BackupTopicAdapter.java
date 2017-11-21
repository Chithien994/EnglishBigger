package tcn.com.adapters;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import tcn.com.englishbigger.R;
import tcn.com.englishbigger.TopicActivity;
import tcn.com.fragment.NoteFragment;
import tcn.com.fragment.WhatDoPeopleLearnFragment;
import tcn.com.handle.Constants;
import tcn.com.handle.Handle;
import tcn.com.models.TopicModels;

/**
 * Created by MyPC on 20/08/2017.
 */

public class BackupTopicAdapter extends RecyclerView.Adapter<BackupTopicAdapter.ViewHolder> {
    TopicActivity topicActivity;
    Activity context;
    int resource;
    ArrayList<TopicModels> objects;
    public static String MY_BRC_TOPIC_ADAPTER = "MY_BRC_TOPIC_ADAPTER";

    public BackupTopicAdapter(Activity context, int resource, ArrayList<TopicModels> objects){
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }
    @Override
    public BackupTopicAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(resource, parent,false);
        ViewHolder viewHolder = new  ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final BackupTopicAdapter.ViewHolder holder, final int position) {
        topicActivity = (TopicActivity) context;
        final TopicModels topicModels = this.objects.get(position);

        holder.txtNameUser.setText(topicModels.getNameUser());
        holder.txtNameTopic.setText(topicModels.getName());

        holder.pbLoading.setVisibility(View.VISIBLE);

        String total = topicModels.getTotal()+" " + context.getString(R.string.vocabularyWords);
        holder.txtAllVocabulary.setText(total);

        Glide.with(context)
                .load(topicModels.getLinkImage())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        holder.pbLoading.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        holder.pbLoading.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.imgItemTopic);


        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleManageVocabulary(position);
            }
        });

        holder.layoutAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleBackupTopic(topicModels);
            }
        });
    }

    private void handleBackupTopic(TopicModels topicModels) {
        JSONObject object = new JSONObject();
        try {
            object.put("idUser", topicActivity.usersFB.getIdUser());
            object.put("idUserFriend", topicModels.getIdUser());
            object.put("idTopic", topicModels.getId());
            topicActivity.serverAPI.handleBackup(topicActivity, object, Constants.BACKUP_TOPIC);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void handleManageVocabulary(int position) {
        topicActivity.saveFragmen(new WhatDoPeopleLearnFragment());
        topicActivity.callFragment(new NoteFragment(), "", position);

    }



    public void myIntentFilter(){
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(MY_BRC_TOPIC_ADAPTER);
        context.registerReceiver(mReceiver,mIntentFilter);
        Log.i("DK","mReceiver");
    }
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MY_BRC_TOPIC_ADAPTER)) {
                Log.d("unregisterReceiver","Unregister Receiver");

                context.unregisterReceiver(mReceiver);
            }
        }
    };


    @Override
    public int getItemCount() {
        return this.objects.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgItemTopic;
        TextView txtNameTopic, txtAllVocabulary, txtNameUser;
        ConstraintLayout layoutAdd;
        LinearLayout layoutItem;
        ProgressBar pbLoading;
        public ViewHolder(View view) {
            super(view);
            imgItemTopic = view.findViewById(R.id.imgItemTopic);
            txtNameTopic = view.findViewById(R.id.txtNameTopic);
            layoutAdd = view.findViewById(R.id.layoutAdd);
            layoutItem = view.findViewById(R.id.layoutItem);
            pbLoading = view.findViewById(R.id.pbLoading);
            txtAllVocabulary = view.findViewById(R.id.txtAllVocabulary);
            txtNameUser = view.findViewById(R.id.txtNameUser);
        }
    }



}
