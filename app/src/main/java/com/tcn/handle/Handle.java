package com.tcn.handle;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import com.tcn.adapters.BackupTopicAdapter;
import com.tcn.adapters.DrawerAdapter;
import com.tcn.adapters.NoteAdapter;
import com.tcn.adapters.TopicAdapter;
import com.tcn.englishbigger.LearnActivity;
import com.tcn.englishbigger.R;
import com.tcn.englishbigger.SplashActivity;
import com.tcn.englishbigger.TopicActivity;
import com.tcn.fragment.AddFragment;
import com.tcn.fragment.NoteFragment;
import com.tcn.fragment.TabBackupFriendsFragment;
import com.tcn.fragment.TabBackupOthersFragment;
import com.tcn.fragment.WhatDoPeopleLearnFragment;
import com.tcn.models.NoteModels;
import com.tcn.models.TopicModels;

import static com.facebook.FacebookSdk.getCacheDir;

/**
 * Created by MyPC on 08/08/2017.
 */

public class Handle {
    public Handle(){}

    //Translate text and set for TextView
    //textView: TextView to setText
    //sourceText Text to translate
    //languageBefore: Current language
    //languageAfter: Language needs to be translated
    //progressBar: Show the rotating bar when translating;
    public void handleTranslateText(Activity activity, final TextView textView, final String sourceText, String languageBefore, String languageAfter) throws UnsupportedEncodingException {
       try {
           Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
           cache.clear();
           Network network = new BasicNetwork(new HurlStack());

           RequestQueue requestQueue= new RequestQueue(cache, network);

           try {

           }catch (Exception e){
               e.fillInStackTrace();
           }

           requestQueue.start();

           final String query = URLEncoder.encode(sourceText, "UTF-8");

           String url = "https://statickidz.com/scripts/traductor/?q="+query+"&source="+languageBefore+"&target="+languageAfter;
           JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET,
                   url, null,
                   new Response.Listener<JSONObject>() {

                       @Override
                       public void onResponse(JSONObject response) {
                           Log.d("TAG", response.toString());
                           try {
                               textView.setText(response.getString("translation"));

                           } catch (JSONException e) {
                               textView.setText(sourceText);
                           }
                       }
                   }, new Response.ErrorListener() {

               @Override
               public void onErrorResponse(VolleyError error) {
                   VolleyLog.d("TAG", "Error: " + error.getMessage());
                   textView.setText(sourceText);
               }
           });
           requestQueue.add(jsonArrayRequest);


       }catch (Exception e){
           e.printStackTrace();
       }
    }

    //Translate text and set for Buttom
    //textView: button to setText
    //sourceText Text to translate
    //languageBefore: Current language
    //languageAfter: Language needs to be translated
    //progressBar: Show the rotating bar when translating;
    public void handleTranslateText(Activity activity, final Button button, final String sourceText, String languageBefore, String languageAfter, final ProgressBar progressBar) throws UnsupportedEncodingException {
        if (button.getText().toString().equals("")) {
            progressBar.setVisibility(View.VISIBLE);
        }
        final String query = URLEncoder.encode(sourceText, "UTF-8");
        String url = "https://statickidz.com/scripts/traductor/?q="+query+"&source="+languageBefore+"&target="+languageAfter;
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("TAG", response.toString());
                        progressBar.setVisibility(View.GONE);
                        try {
                            button.setText(response.getString("translation"));

                        } catch (JSONException e) {
                            button.setText(query);
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("TAG", "Error: " + error.getMessage());
                progressBar.setVisibility(View.GONE);
                button.setText(query);
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        requestQueue.add(jsonArrayRequest);
    }

    public void sendBroadCastToListActivity(Activity activity, boolean cf){
        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(com.tcn.englishbigger.ListActivity.cfBroadcastAction);
        broadCastIntent.putExtra("CF",cf);
        activity.sendBroadcast(broadCastIntent);
    }

    public void sendBroadCastToAddFragment(Activity activity){
        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(AddFragment.cfAdded);
        activity.sendBroadcast(broadCastIntent);
    }

    public void sendBroadCastToNoteFragment(Activity activity, ArrayList<NoteModels> noteModes) {
        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(NoteFragment.MY_BRC_NOTE);
        broadCastIntent.putExtra("NOTE", noteModes);
        activity.sendBroadcast(broadCastIntent);
    }

    public void sendBroadCastToNoteAdapter(Activity activity, int type) {
        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(NoteAdapter.MY_BRC_NOTE_ADAPTER);
        broadCastIntent.putExtra("TYPE", type);
        activity.sendBroadcast(broadCastIntent);
    }

    public void sendBroadCastToNoteAdapter(Activity activity, int type, boolean cf) {
        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(NoteAdapter.MY_BRC_NOTE_ADAPTER);
        broadCastIntent.putExtra("TYPE", type);
        broadCastIntent.putExtra("CF", cf);
        activity.sendBroadcast(broadCastIntent);
    }

    public void sendBroadCastToBackupTopicAdapter(Activity activity, boolean err) {
        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(BackupTopicAdapter.MY_BRC_TOPIC_ADAPTER);
        broadCastIntent.putExtra("ERR", err);
        activity.sendBroadcast(broadCastIntent);
    }

    public void sendBroadCastToDrawerAdapter(Activity activity, boolean err) {
        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(DrawerAdapter.MY_BRC_DRAWER_ADAPTER);
        broadCastIntent.putExtra("ERR", err);
        activity.sendBroadcast(broadCastIntent);
    }

    public void sendBroadCastToTopicAdapter(Activity activity) {
        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(TopicAdapter.MY_BRC_TOPIC_ADAPTER);
        activity.sendBroadcast(broadCastIntent);
    }

    public void sendBroadCastTopicActivity(Activity activity, ArrayList<TopicModels> topicModes){
        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(TopicActivity.broadcastAction);
        broadCastIntent.putExtra("TOPIC", topicModes);
        activity.sendBroadcast(broadCastIntent);
    }


    public void sendBroadCastToTabBackupFriends(Activity activity, ArrayList<TopicModels> topicModes){
        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(TabBackupFriendsFragment.BROADCAST_ACTION);
        broadCastIntent.putExtra("TOPIC", topicModes);
        activity.sendBroadcast(broadCastIntent);
    }

    public void sendBroadCastToWDKFragment(Activity activity, ArrayList<TopicModels> topicModes){
        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(WhatDoPeopleLearnFragment.BROADCAST_ACTION_WDPL);
        broadCastIntent.putExtra("TOPIC", topicModes);
        activity.sendBroadcast(broadCastIntent);
    }

    public void sendBroadCastToTabBackupOthers(Activity activity, ArrayList<TopicModels> topicModes){
        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(TabBackupOthersFragment.BROADCAST_ACTION_OTHERS);
        broadCastIntent.putExtra("TOPIC", topicModes);
        activity.sendBroadcast(broadCastIntent);
    }

    public void sendBroadCastNameTopicToLearnActivity(Activity activity, ArrayList<TopicModels> topicModes){
        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(LearnActivity.MY_BRC_LEARN_ACTIVITY);
        broadCastIntent.putExtra("TOPIC", topicModes);
        activity.sendBroadcast(broadCastIntent);
    }

    public void sendBroadCastToSplashActivity(Activity activity, String version, String url){
        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(SplashActivity.BROADCAST_ACTION_SPLASH);
        broadCastIntent.putExtra("VERSION", version);
        broadCastIntent.putExtra("URL", url);
        activity.sendBroadcast(broadCastIntent);
    }

    public Bitmap cropBitmap(Bitmap inBitmap){

        int inWidth = inBitmap.getWidth();
        int inHeight = inBitmap.getHeight();
        int newWidth = (inHeight > inWidth) ? inWidth : inHeight;
        int newHeight = (inHeight > inWidth) ? inHeight - (inHeight - inWidth) : inHeight;
        int cropWidth = (inWidth - inHeight) / 2;
        cropWidth = (cropWidth < 0) ? 0 : cropWidth;
        int cropHeight = (inHeight - inWidth) / 2;
        cropHeight = (cropHeight < 0) ? 0 : cropHeight;

        Log.i("CROP"," cropWidth: "+cropWidth+" cropHeight: "+cropHeight);
        Log.i("NEW"," newWidth: "+newWidth+" newHeight: "+newHeight);

        Bitmap outBitmap = Bitmap.createBitmap(inBitmap, cropWidth , cropHeight, newWidth, newHeight);
        return outBitmap;
    }

    public Bitmap convertTextToBitmap(final String text) throws IOException {
        String inText = "";
        int sp = 0, spMax = 0;
        int plus =0;
        int size = 0;
        for (int i = 0; i < text.length(); i++){

            if (String.valueOf(text.charAt(i)).equals(" ")){
                sp++;
            }
            if (i > 2 && sp > 1){
                spMax++;
                inText = "";
                sp = 0;
            }else {
                inText ="y";
            }
            plus++;
        }
        if (inText.equals("")==false){
            spMax++;
        }
        size = plus;
        plus *= 4;
        plus += 64;


        TextPaint textPaint = new TextPaint() {
            {
                setColor(Color.WHITE);
                setTextAlign(Align.CENTER);
                setAntiAlias(true);
            }
        };

        Log.i("convertTextToBitmap", "spMax: " +spMax + "\nsize: " + size + "\nplus: " +plus);
        if (spMax > 1 && size > 7){
            textPaint.setTextSize(30f);
        }else if (spMax == 1 && size > 6){
            textPaint.setTextSize(24f);
        }else if (spMax == 1 && size == 6){
            textPaint.setTextSize(30f);
        }else if (spMax == 0 && size > 6){
            textPaint.setTextSize(26f);
        }else if (spMax == 0 && size < 7){
            textPaint.setTextSize(30f);
        }else {
            textPaint.setTextSize(24f);
        }

        final Rect bounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), bounds);

        int width = bounds.width() + plus/2;
        int height = bounds.height()+ plus;
        //Handle size bitmap
        if (width < height){
            width = height;
        }else {
            width = height + height/2;
        }

        final Bitmap bmp = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bmp.eraseColor(Color.BLACK);// just adding black background
        final Canvas canvas = new Canvas(bmp);
        int xPos = (canvas.getWidth() / 2);
        int yPos = (int) ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2)) ;


        if (spMax > 5){
            yPos -= 24*3;
        }else if (spMax > 4){
            yPos -= 22*3;
        }else if (spMax > 3){
            yPos -= 26*2;
        }else if (spMax > 2){
            yPos -= 32;
        }else if (spMax > 1){
            yPos -=20;
        }

        sp = 0;
        inText = "";
        for (int i = 0; i < text.length(); i++){
            if (String.valueOf(text.charAt(i)).equals(" ")){
                sp++;
            }
            if (i > 2 && sp > 1){

                canvas.drawText(inText, xPos, yPos, textPaint);
                yPos +=32;
                sp = 0;
                if (String.valueOf(text.charAt(i)).equals(" ") == false){
                    inText = String.valueOf(text.charAt(i));
                }else {
                    inText = "";
                }
            }else {
                inText += text.charAt(i);
            }
        }
        if (inText.equals("")==false){
            canvas.drawText(inText, xPos, yPos, textPaint);
        }
        return bmp;
    }

    public static void unregisterReceiver(Context context, BroadcastReceiver receiver){
        try {
            context.unregisterReceiver(receiver);
            Log.d("unregisterReceiver","Unregister Receiver");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //check keyboard hide or show
    //If it's showing then hide it
    public static void hideKeyboard(final Activity activity, final View contentView) {

        Rect r = new Rect();
        contentView.getWindowVisibleDisplayFrame(r);
        int screenHeight = contentView.getRootView().getHeight();

        // r.bottom is the position above soft keypad or device button.
        // if keypad is shown, the r.bottom is smaller than that before.
        int keypadHeight = screenHeight - r.bottom;

        Log.d("keypadHeight", keypadHeight +"");

        if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
            // keyboard is opened
            if (contentView != null) {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        }
        else {
            // keyboard is closed
        }
    }


    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
}
