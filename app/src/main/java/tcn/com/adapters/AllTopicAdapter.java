package tcn.com.adapters;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Locale;

import tcn.com.englishbigger.LearnActivity;
import tcn.com.englishbigger.R;
import tcn.com.englishbigger.TopicActivity;
import tcn.com.fragment.AddAndEditTopicFragment;
import tcn.com.fragment.NoteFragment;
import tcn.com.fragment.TopicFragment;
import tcn.com.fragment.WhatDoPeopleLearnFragment;
import tcn.com.handle.Handle;
import tcn.com.models.TopicModels;

/**
 * Created by MyPC on 20/08/2017.
 */

public class AllTopicAdapter extends RecyclerView.Adapter<AllTopicAdapter.ViewHolder> {
    TopicActivity topicActivity;
    Activity context;
    int resource;
    ArrayList<TopicModels> objects;
    boolean cfEdit = false;
    int donate = 0;
    boolean type; //Grid type = true
    Handle handle = new Handle();
    private int ps;
    private int length = 0;
    public static String MY_BRC_TOPIC_ADAPTER = "MY_BRC_TOPIC_ADAPTER";

    public AllTopicAdapter(Activity context, int resource, ArrayList<TopicModels> objects){
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }
    @Override
    public AllTopicAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(resource, parent,false);
        ViewHolder viewHolder = new  ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final AllTopicAdapter.ViewHolder holder, final int position) {
        topicActivity = (TopicActivity) context;
        final TopicModels topicModels = this.objects.get(position);

        holder.txtNameUser.setText(topicModels.getNameUser());
        holder.txtNameTopic.setText(topicModels.getName());

        holder.pbLoading.setVisibility(View.VISIBLE);

        loadImages(topicModels, holder, position);
        String total = topicModels.getTotal()+" " + context.getString(R.string.vocabularyWords);
        holder.txtAllVocabulary.setText(total);

    }

    //this.topicActivity.position: is the location of the image is updated
    private void loadImages(TopicModels topicModels, final ViewHolder holder, final int position) {
        Log.d("Position is updated", this.topicActivity.position + " ? Current position: " + position);
        if (this.topicActivity.position == position && this.topicActivity.position >= 0 ){
            // Deletes the image in the buffer and loads the new image

            this.topicActivity.position = -1; //Delete position
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
        }else {
            //Do not delete the images in the buffer
            Glide.with(context)
                    .load(topicModels.getLinkImage())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
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
        }

        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleManageVocabulary(position);
            }
        });
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

                handleShowListNote();
                context.unregisterReceiver(mReceiver);
            }
        }
    };

    private void handleShowListNote() {
        try {
            this.objects.remove(ps);
            notifyDataSetChanged();

        }catch (Exception e){
            e.printStackTrace();
        }
    }


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
