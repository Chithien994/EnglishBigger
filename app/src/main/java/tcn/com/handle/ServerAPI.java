package tcn.com.handle;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import tcn.com.englishbigger.R;
import tcn.com.models.NoteModels;
import tcn.com.models.TopicModels;

import static com.facebook.FacebookSdk.getCacheDir;

/**
 * Created by MyPC on 17/08/2017.
 */

public class ServerAPI {

    Handle handle = new  Handle();
    ProgressDialog dialog;
    public ServerAPI(){}

    public void post(final Activity activity, JSONObject object, final String type, String url){

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
                        Log.d("Response", response.toString());
                        try {

                            switch (type){

                                case "insert_vocabulary":
                                    specifyNotify(activity,response.getString("message"));
                                    break;

                                case "edit_vocabulary":
                                    handle.sendBroadCastToNoteAdapter(activity, type);
                                    specifyNotify(activity,response.getString("message"));
                                    dialog.cancel();
                                    break;

                                case "getvocabulary":
                                    dialog.cancel();
                                    setVocabulary(activity, response, type);
                                    break;

                                case "learn":
                                    dialog.cancel();
                                    setVocabulary(activity, response, type);
                                    break;

                                case "deletenote":
                                    dialog.cancel();
                                    handleResponseDelete(activity, response, type);
                                    break;

                                case "deletetopic":
                                    dialog.cancel();
                                    handleResponseDelete(activity, response, type);
                                    break;

                                case "getnametopic":
                                    dialog.cancel();
                                    setTopic(activity, response, type);
                                    break;

                                case "gettopic":
                                    dialog.cancel();
                                    setTopic(activity, response, type);
                                    break;

                                case "log":
                                    getInfoUsers(activity);
                                    break;

                                case "getinfo":
                                    dialog.cancel();
                                    saveInfo(activity,response);
                                    break;

                                case "TOPIC_FRIENDS":
                                    setTopic(activity, response, type);
                                    break;


                                case "ALL_TOPIC":
                                    dialog.cancel();
                                    setTopic(activity, response, type);
                                    break;

                                default:{
                                    break;
                                }
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

                    if (type.equals("insert_vocabulary")){
                        handle.sendBroadCastToAddFragment(activity);

                    }else if(type.equals("log") || type.equals("getinfo")){
                        dialog.cancel();
                        handle.sendBroadCastToListActivity(activity, false);
                        return;

                    }else if (type.equals("gettopic") || type.equals("getnametopic")){
                        activity.finish();

                    }else if (type.equals("TOPIC_FRIENDS")){
                        handle.sendBroadCastToWhatDoPeopleLearnAdapter(activity, true);
                        return;
                    }

                    if (error.toString()!= null){

                        if (error.toString().equalsIgnoreCase("com.android.volley.TimeoutError")
                                || error.toString().equalsIgnoreCase("com.android.volley.NoConnectionError: java.net.UnknownHostException: Unable to resolve host \"bdata.dlinkddns.com\": No address associated with hostname")
                                || error.toString().equalsIgnoreCase("java.net.UnknownHostException: Unable to resolve host \"mybigger.ga\": No address associated with hostname")){

                            Toast.makeText(activity,activity.getString(R.string.errorInternetConnectionProblems),Toast.LENGTH_LONG).show();
                            dialog.cancel();
                            return;

                        }
                    }

                    dialog.cancel();
                    Toast.makeText(activity,error.getMessage(),Toast.LENGTH_LONG).show();

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

    public void get(final Activity activity, final String type, String url){
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
                        Log.d("Response",response.toString());
                        dialog.cancel();
                        try {
                            Log.d("TYPE",type);
                            String urlApp;
                            switch (type){
                                case "share":
                                    urlApp = response.getString("url_app");
                                    String name = response.getString("name");
                                    usersFB.shareLink(urlApp, name);
                                    break;

                                case "invite":
                                    urlApp = response.getString("url_app");
                                    String imageUrl = response.getString("image_url");
                                    usersFB.inviteFriends(urlApp, imageUrl);
                                    break;

                                default:{
                                    break;
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                dialog.cancel();
                Toast.makeText(activity,activity.getString(R.string.errorInternetConnectionProblems),Toast.LENGTH_LONG).show();

            }
        });
        requestQueue.add(objectRequest.setRetryPolicy(new DefaultRetryPolicy(
                27000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)));
    }

    private void handleResponseDelete(Activity activity, JSONObject response, String type) {
        try {
            if (response.getString("message").equals("Deleted successfully")){

                Toast.makeText(activity, activity.getString(R.string.deletedSuccessfully), Toast.LENGTH_LONG).show();

                if (type.equals("deletetopic")){

                    handle.sendBroadCastToTopicAdapter(activity);

                }else if (type.equals("deletenote")) {

                    handle.sendBroadCastToNoteAdapter(activity, type);
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
        if (message.equals("This word or phrase already exists")){
            Toast.makeText(activity,activity.getString(R.string.msgVocabularyAlreadyExists),Toast.LENGTH_LONG).show();
        }else if (message.equals("Successfully added")){
            Toast.makeText(activity,activity.getString(R.string.successfullyAdded),Toast.LENGTH_LONG).show();
        }else if (message.equals("Changed successfully")){
            Toast.makeText(activity,activity.getString(R.string.changedSuccessfully),Toast.LENGTH_LONG).show();
        }else if (message.equals("Error input parameters")){
            Toast.makeText(activity,activity.getString(R.string.errorInputParameters),Toast.LENGTH_LONG).show();
        }
    }

    private void saveInfo(Activity activity, JSONObject object) {
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

        }catch (JSONException e){
            e.printStackTrace();
        }
        UsersFB usersFB = new UsersFB(activity);
        usersFB.confirmLogin(true);// Confirm successful login (boolean = true)
    }

    public void postVocabulary(Activity activity, JSONObject object){
        post(activity, object, "insert_vocabulary", Constants.VOCABULARY_URL);
    }

    public void postEditVocabulary(Activity activity, JSONObject object){
        showDialog(activity);

        post(activity, object, "edit_vocabulary", Constants.VOCABULARY_URL);
    }

    //Require the server to test the registered account? If not then register.
    public void postUsers(Activity activity, JSONObject object, String type){
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
            post(activity, object, "getinfo", Constants.USER_INFO_URL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getTopic(Activity activity, JSONObject object, String type){
        showDialog(activity);

        post(activity, object, type, Constants.TOPIC_URL);
    }

    private void setTopic(Activity activity, JSONObject response, String type) {
        ArrayList<TopicModels> topicModes = new ArrayList<>();
        try {
            JSONArray json = response.getJSONArray("topic");
            for (int i = 0; i < json.length(); i++){
                JSONObject object = json.getJSONObject(i);
                String name = "";
                try {
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
                        name));

            }

            if (type.equals("gettopic")){
                handle.sendBroadCastTopicActivity(activity, topicModes);

            }else if (type.equals("getnametopic")){
                handle.sendBroadCastNameTopicToLearnActivity(activity, topicModes);

            }else if (type.equals("ALL_TOPIC") || type.equals("TOPIC_FRIENDS")){
                handle.sendBroadCastToWhatDoPeopleLearnAdapter(activity, topicModes);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getVocabulary(Activity activity, JSONObject object, String type){
        showDialog(activity);

        post(activity, object, type, Constants.VOCABULARY_URL);
    }

    private void setVocabulary(Activity activity, JSONObject response, String type) {

        try {
            ArrayList<NoteModels> noteModels = new ArrayList<>();
            JSONArray json = response.getJSONArray("vocabulary");
            if (response.getString("vocabulary").equals("[]")){
                //empty
                Toast.makeText(activity, activity.getString(R.string.empty), Toast.LENGTH_LONG).show();

                if (type.equals("learn")){
                    activity.finish();
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
                handle.sendBroadCastToNoteFragment(activity, noteModels);

            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void handleDeleteTopic(Activity activity, JSONObject object, String type){
        showDialog(activity);

        String url = null;

        if (type.equals("deletenote")){
            url = Constants.VOCABULARY_URL;

        }else if (type.equals("deletetopic")){

            url = Constants.DELETE_URL;
        }
        post(activity, object, type, url);
    }

    public void getVocabulary(Activity activity, int id, String type) {

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
            post(activity, object, "learned", Constants.LEARNED_URL);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getLink(Activity activity, String type){
        showDialog(activity);
        get(activity, type, Constants.GET_LINK_APP_URL);
    }

    public void getAllTopic(Activity activity, JSONObject object){
        showDialog(activity);
        post(activity, object, "ALL_TOPIC", Constants.ALL_TOPIC_URL);
    }

    public void getTopicFriends(Activity activity, JSONObject object){
        post(activity, object, "TOPIC_FRIENDS", Constants.TOPIC_OF_FRIENDS_URL);
    }

    private void showDialog(Activity activity){
        dialog = new ProgressDialog(activity);
        dialog.setMessage(activity.getString(R.string.pleaseWait));
        dialog.setCancelable(false);
        dialog.show();
    }
}
