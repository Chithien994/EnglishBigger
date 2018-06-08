package com.tcn.handle;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Network;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import com.tcn.englishbigger.R;
import com.tcn.models.NoteModels;
import com.tcn.models.TopicModels;

import static com.facebook.FacebookSdk.getCacheDir;

/**
 * Created by MyPC on 17/08/2017.
 */

public class ServerAPI {

    Handle handle = new  Handle();
    ProgressDialog dialog;
    boolean cfErr = true;
    boolean cfSendBRC = true;

    public ServerAPI(){}

    public void post(final Activity activity, final JSONObject object, final int type, String url){

        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);

        Network network = new BasicNetwork(new HurlStack());

        final RequestQueue requestQueue= new RequestQueue(cache, network);

        requestQueue.start();

        Log.d("Request",object.toString());
        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            if(type != Constants.LOGIN || type != Constants.CHECK_UPDATE) dialog.cancel();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        try {

                            switch (type){

                                case Constants.FEEDBACK:
                                    specifyNotify(activity,response.getString("message"));
                                    handle.sendBroadCastToDrawerAdapter(activity, cfErr);
                                    break;

                                case Constants.BACKUP_TOPIC:
                                    specifyNotify(activity,response.getString("message"));
                                    if(!cfErr)
                                        MyAction.setLoadedTopic(activity, false);
                                    Log.d("Request","cfErr: "+cfErr +" cfSendBRC: "+cfSendBRC);
                                    if(cfSendBRC) handle.sendBroadCastToBackupTopicAdapter(activity, cfErr);
                                    break;
                                case Constants.BACKUP_VOCABULARY:
                                case Constants.INSERT_VOCABULARY:
                                    specifyNotify(activity,response.getString("message"));
                                    if (!cfErr)
                                        MyAction.setLoadedTopic(activity, false);
                                    break;

                                case Constants.EDIT_VOCABULARY:
                                    specifyNotify(activity,response.getString("message"));
                                    handle.sendBroadCastToNoteAdapter(activity, type, cfErr);
                                    break;

                                case Constants.GET_VOCABULARY:
                                case Constants.LEARN:
                                    setVocabulary(activity, response, type);
                                    break;

                                case Constants.DELETE_NOTE:
                                case Constants.DELETE_TOPIC:
                                    handleResponseDelete(activity, response, type);
                                    break;

                                case Constants.GET_TOPIC_A_FRIEND:
                                case Constants.GET_TOPIC_FRIENDS:
                                case Constants.GET_ALL_TOPIC:
                                case Constants.GET_NAME_TOPIC:
                                case Constants.GET_TOPIC:
                                    setTopic(activity, response, type);
                                    break;

                                case Constants.LOGIN:
                                    getInfoUsers(activity);
                                    break;

                                case Constants.GET_INFO_USER:
                                    saveInfo(activity,response);
                                    break;

                                case Constants.CHECK_UPDATE:
                                    handle.sendBroadCastToSplashActivity(activity,
                                            response.getString("version"),
                                            response.getString("files_url"));
                                    break;

                                default:{
                                    break;
                                }
                            }

                            Log.d("Response", response.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                try {
                    dialog.cancel();
                }catch (Exception e){
                    e.printStackTrace();
                }

                try {
                    switch (type){

                        case Constants.INSERT_VOCABULARY:
                            handle.sendBroadCastToAddFragment(activity);
                            break;

                        case Constants.GET_NAME_TOPIC:
                        case Constants.GET_TOPIC:
                            activity.finish();
                            break;

                        case Constants.LOGIN:
                        case Constants.GET_INFO_USER:
                            handle.sendBroadCastToListActivity(activity, false);
                            return;
                        case Constants.CHECK_UPDATE:
                            handle.sendBroadCastToSplashActivity(activity, null, null);
                            return;
                        default:{
                            break;
                        }
                    }

                    if (error.toString()!= null){
                        if (error.toString().contains("com.android.volley.TimeoutError")
                                || error.toString().contains("No address associated with hostname")
                                || error.toString().contains("Unexpected response code 200")
                                || error.toString().contains("HTTP response for request")){
                            Toast.makeText(activity,activity.getString(R.string.errorInternetConnectionProblems),Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                    Toast.makeText(activity,activity.getString(R.string.failure),Toast.LENGTH_LONG).show();

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
        requestQueue.add(objectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    27000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)));
    }


    public void get(final Activity activity, final int type, String url){
        final UsersFB usersFB = new UsersFB(activity);
        final Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);

        Network network = new BasicNetwork(new HurlStack());

        final RequestQueue requestQueue= new RequestQueue(cache, network);

        requestQueue.start();

        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("TYPE",type +"");
                            switch (type){
                                case Constants.SHARE:
                                    usersFB.shareLink(response);
                                    break;

                                case Constants.INVITE:
                                    usersFB.shareLink(response);
                                    break;

                                default:{
                                    break;
                                }
                            }
                            Log.d("Response",response.toString());
                            try {
                                dialog.cancel();
                            }catch (Exception e){

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                try {
                    dialog.cancel();
                }catch (Exception e){

                }
                if (error.toString()!= null){
                    if (error.toString().contains("com.android.volley.TimeoutError")
                            || error.toString().contains("No address associated with hostname")
                            || error.toString().contains("Unexpected response code 200")
                            || error.toString().contains("HTTP response for request")){
                        Toast.makeText(activity,activity.getString(R.string.errorInternetConnectionProblems),Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                Toast.makeText(activity,activity.getString(R.string.failure),Toast.LENGTH_LONG).show();

            }
        });
        requestQueue.add(objectRequest.setRetryPolicy(new DefaultRetryPolicy(
                27000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)));
    }

    private void handleResponseDelete(Activity activity, JSONObject response, int type) {
        try {
            if (response.getString("message").equals("Deleted successfully")){

                cfErr = false;
                Toast.makeText(activity, activity.getString(R.string.deletedSuccessfully), Toast.LENGTH_LONG).show();
                switch (type){
                    case Constants.DELETE_TOPIC:
                        handle.sendBroadCastToTopicAdapter(activity);
                        break;
                    case Constants.DELETE_NOTE:
                        handle.sendBroadCastToNoteAdapter(activity, type, cfErr);
                        break;
                    default:{
                        break;
                    }
                }

            }else {
                Toast.makeText(activity, activity.getString(R.string.failure), Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void specifyNotify(Activity activity, String message) {
        handle.sendBroadCastToAddFragment(activity);

        switch (message.toLowerCase()){

            case "this word or phrase already exists":
                Toast.makeText(activity,activity.getString(R.string.msgVocabularyAlreadyExists),Toast.LENGTH_LONG).show();
                break;

            case "successfully added":
                Toast.makeText(activity,activity.getString(R.string.successfullyAdded),Toast.LENGTH_LONG).show();
                cfErr = false;
                break;

            case "changed successfully":
                Toast.makeText(activity,activity.getString(R.string.changedSuccessfully),Toast.LENGTH_LONG).show();
                cfErr = false;
                break;

            case "error input parameters":
                Toast.makeText(activity,activity.getString(R.string.errorInputParameters),Toast.LENGTH_LONG).show();
                break;

            case "nothing to add":
                Toast.makeText(activity,activity.getString(R.string.nothingToAdd),Toast.LENGTH_LONG).show();
                cfErr = false;
                break;

            case "commented successfully":
                Toast.makeText(activity,activity.getString(R.string.commentedSuccessfully),Toast.LENGTH_LONG).show();
                cfErr = false;
                break;

            case "commented failed":
                Toast.makeText(activity,activity.getString(R.string.commentedFailed),Toast.LENGTH_LONG).show();
                cfErr = true;
                break;

            default:{
                Toast.makeText(activity,activity.getString(R.string.failure),Toast.LENGTH_LONG).show();
                break;
            }
        }
    }

    private void saveInfo(Activity activity, JSONObject object) {
        UsersFB usersFB = new UsersFB(activity);
        try {
            if (object.getString("message") != null){
                Toast.makeText(activity, activity.getString(R.string.failure), Toast.LENGTH_LONG).show();
                usersFB.confirmLogin(false);// Confirm successful login (boolean = true)
                return;
            }
        }catch (Exception e){

        }
        Users users = new Users(activity);
        try {
            users.setIdUser(object.getString("idUser"));
            users.setFirstName(object.getString("first_name"));
            users.setName(object.getString("name"));
            users.setUrlAvatar(object.getString("urlAvatar"));
            users.setEmail(object.getString("email"));
            users.setBirthday(object.getString("birthday"));
            users.setLocation(object.getString("location"));
            users.setGender(object.getString("gender"));
            users.setAccessToken(object.getString("remember_token"));
            users.setTokenFirebase(object.getString("token_firebase"));

        }catch (JSONException e){
            e.printStackTrace();
        }

        usersFB.confirmLogin(true);// Confirm successful login (boolean = true)
    }

    public void postVocabulary(Activity activity, JSONObject object){
        post(activity, object, Constants.INSERT_VOCABULARY, Constants.VOCABULARY_URL);
    }

    public void postEditVocabulary(Activity activity, JSONObject object){
        cfErr = true;
        showDialog(activity);

        post(activity, object, Constants.EDIT_VOCABULARY, Constants.VOCABULARY_URL);
    }

    //Require the server to test the registered account? If not then register.
    public void postUsers(Activity activity, JSONObject object, int type){
        if(!(new UsersFB(activity).confirmLogin()))
            showDialog(activity);
        post(activity, object, type, Constants.USER_URL);
    }

    public void getInfoUsers(Activity activity){

        UsersFB usersFB = new UsersFB(activity);
        JSONObject object = new JSONObject();
        try {
            object.put("idUser", usersFB.getIdUser());
            object.put("password","");
            object.put("accessToken",usersFB.getAccessToken());
            object.put("type","Social");

            //Login and get info user
            post(activity, object, Constants.GET_INFO_USER, Constants.USER_INFO_URL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getVocabulary(Activity activity, JSONObject object, int type){
        showDialog(activity);

        post(activity, object, type, Constants.VOCABULARY_URL);
    }

    private void setVocabulary(Activity activity, JSONObject response, int type) {

        try {
            ArrayList<NoteModels> noteModels = new ArrayList<>();
            JSONArray json = response.getJSONArray("vocabulary");
            if (response.getString("vocabulary").equals("[]")){
                //empty
                if (type == Constants.LEARN){

                    Toast.makeText(activity, activity.getString(R.string.chooseATopicToLearn), Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(activity, activity.getString(R.string.empty), Toast.LENGTH_LONG).show();
                }

            }else {
                for (int i = 0; i < json.length(); i++){
                    JSONObject object = json.getJSONObject(i);
                    byte b = (byte) object.getInt("learned");
                    noteModels.add(new NoteModels(object.getInt("id"),
                            object.getString ("noteSource"),
                            object.getString ("langguageSource"),
                            object.getString ("noteMeaning"),
                            object.getString ("langguageMeaning"),
                            b));

                }
            }
            handle.sendBroadCastToNoteFragment(activity, noteModels);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void handleDeleteTopic(Activity activity, JSONObject object, int type){
        showDialog(activity);

        String url = null;

        switch (type){
            case Constants.DELETE_NOTE:
                url = Constants.VOCABULARY_URL;
                break;

            default:{
                //Constants.DELETE_TOPIC
                url = Constants.DELETE_URL;
                break;
            }
        }
        post(activity, object, type, url);
    }

    public void getVocabulary(Activity activity, int id, int type) {

        JSONObject object = new JSONObject();
        try {
            object.put("idTopic", id);
            object.put("type","_SHOW");
            getVocabulary(activity, object, type);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setLearned(Activity activity, int id, byte b) {

        JSONObject object = new JSONObject();
        try {
            object.put("id", id);
            object.put("learned",b);
            post(activity, object, Constants.LEARNED, Constants.LEARNED_URL);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getLink(Activity activity, int type){
        showDialog(activity);
        get(activity, type, Constants.GET_LINK_APP_URL);
    }

    public void getTopic(Activity activity, JSONObject object, int type){
        switch (type){
            case Constants.GET_ALL_TOPIC:
                showDialog(activity);
                post(activity, object, type, Constants.ALL_TOPIC_URL);
                break;
            case Constants.GET_TOPIC_A_FRIEND:
            case Constants.GET_TOPIC_FRIENDS:
                post(activity, object, type, Constants.TOPIC_OF_FRIENDS_URL);
                break;
            default:{
                //GET_TOPIC or GET_NAME_TOPIC
                showDialog(activity);
                post(activity, object, type, Constants.TOPIC_URL);
                break;
            }

        }
    }

    private void setTopic(Activity activity, JSONObject response, int type) {
        ArrayList<TopicModels> topicModes = new ArrayList<>();
        try {
            JSONArray json = response.getJSONArray("topic");
            for (int i = 0; i < json.length(); i++){
                JSONObject object = json.getJSONObject(i);
                String name = "";
                String idUser = "";
                try {
                    idUser = object.getString("idUser");
                    name = object.getString("nameUser");
                }catch (Exception e){
                    Log.e("NAME_USER", "No name");
                }
                topicModes.add(new TopicModels(
                        object.getInt("id"),
                        object.getString("name"),
                        object.getString("url"),
                        object.getInt("percent"),
                        object.getInt("total"),
                        idUser,
                        name));

            }

            switch (type){
                case Constants.GET_TOPIC:
                    handle.sendBroadCastTopicActivity(activity, topicModes);
                    break;

                case Constants.GET_NAME_TOPIC:
                    handle.sendBroadCastNameTopicToLearnActivity(activity, topicModes);
                    break;

                case Constants.GET_TOPIC_FRIENDS:
                    handle.sendBroadCastToTabBackupFriends(activity, topicModes);
                    break;
                case Constants.GET_TOPIC_A_FRIEND:
                    handle.sendBroadCastToWDKFragment(activity, topicModes);
                    break;

                case Constants.GET_ALL_TOPIC:
                    handle.sendBroadCastToTabBackupOthers(activity, topicModes);
                    break;

                default:{
                    break;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void handleBackup(Activity activity, JSONObject object, int type, boolean cfSendBRC){
        cfErr = true;
        this.cfSendBRC = cfSendBRC;
        showDialog(activity);

        switch (type){
            case Constants.BACKUP_TOPIC:
                post(activity, object, type, Constants.BACKUP_TOPIC_URL);
                break;

            case Constants.BACKUP_VOCABULARY:
                post(activity, object, type, Constants.BACKUP_VOCABULARY_URL);
                break;

            default:{
                break;
            }

        }

    }

    public void checkUpdateVersion(Activity activity, JSONObject object){
        post(activity, object, Constants.CHECK_UPDATE, Constants.CHECK_UPDATE_URL);
    }

    public void postFeedBack(Activity activity, JSONObject object){
        showDialog(activity);
        cfErr = true;
        post(activity, object, Constants.FEEDBACK, Constants.FEEDBACK_URL);
    }

    private void showDialog(Activity activity){
        dialog = new ProgressDialog(activity);
        dialog.setMessage(activity.getString(R.string.pleaseWait));
        dialog.setCancelable(true);
        try {
            dialog.dismiss();
        }catch (Exception e){
            e.fillInStackTrace();
        }
        dialog.show();
    }
}
