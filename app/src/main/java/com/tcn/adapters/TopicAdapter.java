package com.tcn.adapters;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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

import com.tcn.englishbigger.LearnActivity;
import com.tcn.englishbigger.R;
import com.tcn.englishbigger.TopicActivity;
import com.tcn.fragment.AddAndEditTopicFragment;
import com.tcn.fragment.NoteFragment;
import com.tcn.fragment.TopicFragment;
import com.tcn.handle.Constants;
import com.tcn.handle.Handle;
import com.tcn.handle.IntentActivity;
import com.tcn.handle.MyAction;
import com.tcn.models.TopicModels;

/**
 * Created by MyPC on 20/08/2017.
 */

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.ViewHolder> {
    TopicActivity topicActivity;
    TopicFragment topicFragment;
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

    public TopicAdapter(Activity context, TopicFragment topicFragment, int resource, ArrayList<TopicModels> objects, boolean type){
        this.context = context;
        this.resource = resource;
        this.objects = objects;
        this.type = type;
        this.topicFragment = topicFragment;
    }
    @Override
    public TopicAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(resource, parent,false);

//        RecyclerView.LayoutParams lp;
//        if (type){// for grid view
//            lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        }else{// for list view
//            lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        }
//        view.setLayoutParams(lp);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TopicAdapter.ViewHolder holder, final int position) {
        topicActivity = (TopicActivity) context;
        final TopicModels topicModels = this.objects.get(position);

        holder.txtNameTopic.setText(topicModels.getName());

        holder.pbLoading.setVisibility(View.VISIBLE);

        loadImages(topicModels, holder, position);

        if (cfEdit){
            holder.layoutEdit.setVisibility(View.VISIBLE);

        }else {
            holder.layoutEdit.setVisibility(View.GONE);

        }
        topicFragment.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkEdit();
            }
        });
        holder.layoutEditIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (topicFragment.layoutOptionView.getVisibility()==View.GONE){
                    MyAction.setFragmentNew(context, MyAction.EDIT_TOPIC_FRAGMENT);
                    topicActivity.saveFragmen(new TopicFragment());
                    topicActivity.callFragment(new AddAndEditTopicFragment(), "edit", position);
                }
            }
        });
        holder.layoutDeleteIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (topicFragment.layoutOptionView.getVisibility()==View.GONE){
                    handleDeleteTopic(topicModels, position);
                }
            }
        });

        if (length != objects.size() - 1 || length == 0){

            if (type){
                // Get item width
                // Set item height equal to item width
                ViewTreeObserver vto = holder.layoutItem.getViewTreeObserver();
                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            holder.layoutItem.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            holder.layoutItem.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                        int width = holder.layoutItem.getMeasuredWidth();
                        Log.d("TopicAdapter","Width item: "+width);
                        //int height = holder.layoutItem.getMeasuredHeight();

                        //Set item height
                        ViewGroup.LayoutParams params = holder.layoutItem.getLayoutParams();
                        params.height = width;
                        holder.layoutItem.setLayoutParams(params);

                    }
                });



            }else {
                String learned = "You've learned " + topicModels.getPercent() +"%\n (out of "+ topicModels.getTotal()+" words)";
                SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.inforLocale), ((TopicActivity) context).MODE_PRIVATE);
                String languageAfter = "en";
                if (sp.getString("language","en").equals("zz_ZZ")){
                    languageAfter = Locale.getDefault()+"";

                }else {
                    languageAfter = sp.getString("language","en");
                }
                try {
                    //Automatically translated into the user's language
                    handle.handleTranslateText(
                            context,
                            holder.txtLearned,
                            learned,
                            "en",
                            languageAfter);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                holder.layoutLearn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!cfEdit){
                            handleLearnNow(topicModels.getId());

                        }
                    }
                });

            }
            if (length < position){
                length = position;
            }
        }
        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (topicFragment.layoutOptionView.getVisibility()==View.VISIBLE){
                    topicFragment.hideOptionView();
                }else {
                    if (!cfEdit) {

                        if (type) {
                            handleLearnNow(topicModels.getId());

                        } else {
                            handleManageVocabulary(position);

                        }

                    }
                }
            }
        });

    }

    //this.topicActivity.position: is the location of the image is updated
    private void loadImages(TopicModels topicModels, final ViewHolder holder, int position) {
        Log.d("Position is updated", this.topicActivity.position + " ? Current position: " + position);
        if (this.topicActivity.position == position && this.topicActivity.position >= 0 ){
            // Deletes the image in the buffer and loads the new image

            this.topicActivity.position = -1; //Delete position
            Glide.with(topicFragment)
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
            Glide.with(topicFragment)
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
    }

    private void handleManageVocabulary(int position) {
        MyAction.setFragmentNew(context, MyAction.NOTE_FRAGMENT);
        MyAction.setFragmentBulb(context, MyAction.TOPIC_FRAGMENT);
        topicActivity.saveFragmen(new TopicFragment());
        topicActivity.callFragment(new NoteFragment(), "", position);

    }

    private void handleLearnNow(int id) {
        if (topicFragment.layoutOptionView.getVisibility()==View.GONE){
            MyAction.setIdTopic(topicActivity, id);
            MyAction.setActivityBulb(context, MyAction.TOPIC_ACTIVITY);
            MyAction.setFragmentBulb(context, MyAction.TOPIC_FRAGMENT);
            MyAction.setFragmentNew(context, MyAction.LEARN_FRAGMENT);
            IntentActivity.handleOpenLearnActivity(context);
        }else{
            topicFragment.hideOptionView();
        }
    }

    private void handleDeleteTopic(final TopicModels topicModels, final int position) {
        final JSONObject object = new JSONObject();
        new AlertDialog.Builder(topicActivity)
                .setMessage(context.getString(R.string.doYouWantToDeleteIt))
                .setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            myIntentFilter();
                            ps = position;
                            object.put("id", topicModels.getId());
                            object.put("url", topicModels.getLinkImage());
                            //
                            topicActivity.serverAPI.handleDeleteTopic(topicActivity, object, Constants.DELETE_TOPIC);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton(context.getString(R.string.no), null)
                .show();
    }

    //check btnEdit clicked?
    private void checkEdit() {

        if (topicFragment.layoutOptionView.getVisibility() == View.VISIBLE){

            topicFragment.hideOptionView();
        }
        if (cfEdit){
            topicFragment.btnEdit.setImageResource(R.drawable.ic_edit_w);
            cfEdit = false;
        }else {
            topicFragment.btnEdit.setImageResource(R.drawable.ic_save_w);
            cfEdit = true;
        }
        topicFragment.cfEdit = cfEdit;
        notifyDataSetChanged();
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
                Handle.unregisterReceiver(context, mReceiver);
            }
        }
    };

    private void handleShowListNote() {
        try {
            this.objects.remove(ps);
            notifyDataSetChanged();
            MyAction.setRefreshTopic(topicActivity, true);
            MyAction.setFragmentNew(topicActivity, MyAction.TOPIC_FRAGMENT);
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
        TextView txtNameTopic, txtLearned;
        ConstraintLayout layoutEdit, layoutLearn;
        LinearLayout layoutEditIn, layoutDeleteIn, layoutItem;
        ProgressBar pbLoading;
        public ViewHolder(View view) {
            super(view);
            imgItemTopic = view.findViewById(R.id.imgItemTopic);
            txtNameTopic = view.findViewById(R.id.txtNameTopic);
            layoutEdit = view.findViewById(R.id.layoutEdit);
            layoutEditIn = view.findViewById(R.id.layoutEditIn);
            layoutDeleteIn = view.findViewById(R.id.layoutDeleteIn);
            layoutItem = view.findViewById(R.id.layoutItem);
            pbLoading = view.findViewById(R.id.pbLoading);
            if (type==false){
                txtLearned = view.findViewById(R.id.txtLearned);
                layoutLearn = view.findViewById(R.id.layoutLearn);
            }
        }
    }



}
