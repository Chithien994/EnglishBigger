package com.tcn.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
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

import java.util.ArrayList;

import com.tcn.englishbigger.ListActivity;
import com.tcn.englishbigger.R;
import com.tcn.englishbigger.TopicActivity;
import com.tcn.handle.IntentActivity;
import com.tcn.handle.MyAction;
import com.tcn.models.LocaleModels;
import com.tcn.models.UsersModels;


/**
 * Created by MyPC on 08/08/2017.
 */

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {
    Activity context;
    ArrayList<UsersModels> objects;

    public FriendsAdapter(Activity context, ArrayList<UsersModels> objects) {
        this.context = context;
        this.objects = objects;
    }


    @Override
    public FriendsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friends, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final FriendsAdapter.ViewHolder holder, int position) {
        final UsersModels usersModels = this.objects.get(position);
        holder.pbLoading.setVisibility(View.VISIBLE);
        loadImage(usersModels, holder, position);
        holder.txtName.setText(usersModels.getName());
        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyAction.setFragmentNew(context, MyAction.TOPIC_FRIEND_FRAGMENT);
                MyAction.setNameFriend(context,usersModels.getName());
                MyAction.setIDFriend(context,usersModels.getIdUser());
                MyAction.setActivityBulb(context,MyAction.LIST_ACTIVITY);
                IntentActivity.handleOpenTopicActivy(context);
            }
        });

    }

    private void loadImage(UsersModels usersModels, final ViewHolder holder, int position) {
        ListActivity listActivity = (ListActivity) this.context;

        if (listActivity.position < position){
            // Deletes the image in the buffer and loads the new image

            listActivity.position = position; //

            Glide.with(context)
                    .load(usersModels.getLinkAvatar())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .dontAnimate()
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
                    .into(holder.imgAvatar);
        }else {
            //Do not delete the images in the buffer
            Glide.with(context)
                    .load(usersModels.getLinkAvatar())
                    .dontAnimate()
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
                    .into(holder.imgAvatar);
        }
    }


    @Override
    public int getItemCount() {
        return objects.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView txtName;
        LinearLayout layoutBGR;
        ProgressBar pbLoading;
        LinearLayout layoutItem;
        public ViewHolder(View view) {
            super(view);
            imgAvatar = view.findViewById(R.id.imgAvatar);
            txtName = view.findViewById(R.id.txtName);
            layoutBGR = view.findViewById(R.id.layoutBGR);
            pbLoading = view.findViewById(R.id.pbLoading);
            layoutItem = view.findViewById(R.id.layoutItem);
        }
    }
}
