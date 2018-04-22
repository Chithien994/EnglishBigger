package com.tcn.adapters;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import com.tcn.englishbigger.R;
import com.tcn.englishbigger.TopicActivity;
import com.tcn.fragment.NoteFragment;
import com.tcn.fragment.WhatDoPeopleLearnFragment;
import com.tcn.handle.Constants;
import com.tcn.handle.Handle;
import com.tcn.handle.MyAction;
import com.tcn.models.TopicModels;

/**
 * Created by MyPC on 20/08/2017.
 */

public class BackupTopicAdapter extends RecyclerView.Adapter<BackupTopicAdapter.ViewHolder> {
    private TopicActivity topicActivity;
    private Activity context;
    private int resource;
    private ArrayList<TopicModels> objects;
    private String nameTopic = null;
    private AlertDialog alertDialog;
    private boolean spIsChecked = false;
    public static String MY_BRC_TOPIC_ADAPTER = "MY_BACKUP_TOPIC_ADAPTER";
    public static String TAG = "BACKUP_TOPIC_ADAPTER";

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

    private void handleBackupTopic(final TopicModels topicModels) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = context.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.layout_dialog_set_name, null);
        dialogBuilder.setView(dialogView);

        ImageView imgCloseDialog = dialogView.findViewById(R.id.imgCloseDialog);
        final LinearLayout layoutNewNameDialog = dialogView.findViewById(R.id.layoutNewName);
        final LinearLayout layoutGetNameTopicDialog = dialogView.findViewById(R.id.layoutGetNameTopic);
        final Spinner spNameTopicDialog = dialogView.findViewById(R.id.spNameTopicDialog);
        final RadioButton radShowTXTNewName = dialogView.findViewById(R.id.radShowTXTNewName);
        final RadioButton radShowSPNewName = dialogView.findViewById(R.id.radShowSPNewName);
        Button btnBackupDialog = dialogView.findViewById(R.id.btnBackupDialog);
        final EditText txtNewNameTopicDialog = dialogView.findViewById(R.id.txtNewNameTopicDialog);
        final ArrayList<String> dsNameTopic = new ArrayList<>();
        alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(true);
        alertDialog.show();
        txtNewNameTopicDialog.setText(topicModels.getId() + " " + topicModels.getName());
        nameTopic = txtNewNameTopicDialog.getText().toString();
        layoutGetNameTopicDialog.setVisibility(View.GONE);
        layoutNewNameDialog.setVisibility(View.VISIBLE);

        ArrayAdapter<String> arrayAdapter;
        dsNameTopic.add(0, context.getString(R.string.select_));
        for (int i = 0; i < topicActivity.topicYourModes.size(); i++){
            dsNameTopic.add(i+1, topicActivity.topicYourModes.get(i).getName());
        }
        arrayAdapter =  new ArrayAdapter<String>(topicActivity, R.layout.support_simple_spinner_dropdown_item, dsNameTopic);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spNameTopicDialog.setAdapter(arrayAdapter);

        radShowSPNewName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutGetNameTopicDialog.setVisibility(View.VISIBLE);
                layoutNewNameDialog.setVisibility(View.GONE);
                spNameTopicDialog.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        nameTopic = dsNameTopic.get(i);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }
        });

        radShowTXTNewName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutGetNameTopicDialog.setVisibility(View.GONE);
                layoutNewNameDialog.setVisibility(View.VISIBLE);
            }
        });

        btnBackupDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Handle.hideKeyboard(topicActivity, topicActivity.view);

                if (radShowTXTNewName.isChecked()){
                    nameTopic = txtNewNameTopicDialog.getText().toString();
                    for (int i = 0; i < topicActivity.topicYourModes.size(); i++){
                        if (nameTopic.equals(topicActivity.topicYourModes.get(i).getName())){
                            Toast.makeText(topicActivity, topicActivity.getString(R.string.topicAlreadyExists), Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                }

                else spIsChecked = true;
                if(!nameTopic.equalsIgnoreCase(context.getString(R.string.select_))){
                    JSONObject object = new JSONObject();
                    try {
                        myIntentFilter();
                        object.put("idUser", topicActivity.usersFB.getIdUser());
                        object.put("nameTopic", nameTopic);
                        object.put("idTopic", topicModels.getId());
                        topicActivity.serverAPI.handleBackup(topicActivity, object, Constants.BACKUP_TOPIC, true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(topicActivity, context.getString(R.string.msgPleaseSelectAValidTopicName), Toast.LENGTH_LONG).show();
                }


            }
        });

        imgCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Handle.hideKeyboard(topicActivity, topicActivity.view);
                alertDialog.cancel();
            }
        });

    }


    private void handleManageVocabulary(int position) {
        topicActivity.saveFragmen(new WhatDoPeopleLearnFragment());
        MyAction.setFragmentBulb(topicActivity, MyAction.WDPL_FRAGMENT);
        MyAction.setFragmentNew(topicActivity, MyAction.NOTE_FRAGMENT);
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
                Handle.unregisterReceiver(context, mReceiver);
                Log.d("unregisterReceiver","Unregister Receiver");
                if(!intent.getBooleanExtra("ERR",false))
                    try {
                        if (!spIsChecked)
                            MyAction.setRefreshTopic(context,true);
                        alertDialog.cancel();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
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
