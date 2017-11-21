package tcn.com.handle;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.AppInviteDialog;
import com.facebook.share.widget.ShareDialog;

import tcn.com.englishbigger.R;

/**
 * Created by MyPC on 23/08/2017.
 */

public class UsersFB {
    Activity activity;
    SharedPreferences pf;
    SharedPreferences.Editor editor;
    public UsersFB(Activity activity){
        this.activity = activity;
        pf = activity.getSharedPreferences(activity.getString(R.string.saveInforLoginFB), activity.MODE_PRIVATE);
        editor = pf.edit();
    }

    public String getIdUser(){
        return pf.getString("id_fb","");
    }

    public void setIdUser(String idUser){
        editor.putString("id_fb",idUser);
        editor.commit();
    }

    public String getFirstName(){
        return pf.getString("first_name_fb","");
    }

    public void setFirstName(String firstName){
        editor.putString("first_name_fb",firstName);
        editor.commit();
    }

    public String getName(){
        return pf.getString("name_fb","");
    }

    public void setName(String name){
        editor.putString("name_fb", name);
        editor.commit();
    }

    public String getEmail(){
        return pf.getString("email_fb","");
    }

    public void setEmail(String email){
        editor.putString("email_fb", email);
        editor.commit();
    }

    public String getGender(){
        return pf.getString("gender_fb","");
    }

    public void setGender(String gender){
        editor.putString("gender_fb", gender);
        editor.commit();
    }

    public String getBirthday(){
        return pf.getString("birthday_fb","");
    }

    public void setBirthday(String birthday){
        editor.putString("birthday_fb", birthday);
        editor.commit();
    }

    public String getLocation(){
        return pf.getString("location_fb","");
    }

    public void setLocation(String location){
        editor.putString("location_fb", location);
        editor.commit();
    }

    public String getUrlAvatar(){
        return pf.getString("urlAvatar","");
    }

    public void setUrlAvatar(String urlAvatar){
        editor.putString("urlAvatar_fb", urlAvatar);
        editor.commit();
    }

    public String getAccessToken(){
        return pf.getString("accessToken_fb","");
    }

    public void setAccessToken(String accessToken){
        editor.putString("accessToken_fb", accessToken);
        editor.commit();
    }

    public void confirmLogin(boolean cf){
        editor.putBoolean("LOGIN",cf);
        editor.commit();
        Log.i("Confirm","Login = "+cf);
        sendBroadCastToListActivity(activity,cf);
    }

    public boolean confirmLogin(){
        return pf.getBoolean("LOGIN",false);
    }

    public   void setUserFB(String accessToken, String id, String first_name, String name, String email, String gender, String birthday, String location, String urlHome, String urlAvatar ){
        try {
            editor.putString("accessToken_fb",accessToken);
            editor.putString("id_fb", id);
            editor.putString("first_name_fb", first_name);
            editor.putString("name_fb", name);
            editor.putString("email_fb", email);
            editor.putString("gender_fb",gender);
            editor.putString("birthday_fb", birthday);
            editor.putString("location_fb",location);
            editor.putString("urlHome_fb",urlHome);
            editor.putString("urlAvatar_fb",urlAvatar);
            editor.commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void shareLink(String appLinkUrl, String name){
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(appLinkUrl))
                .setShareHashtag(new ShareHashtag.Builder()
                        .setHashtag(name)
                        .build())
                .build();
        ShareDialog.show(activity, content);
    }

    public void inviteFriends(String appLinkUrl, String previewImageUrl){
        FacebookSdk.sdkInitialize(activity);
        if (AppInviteDialog.canShow()) {
            AppInviteContent content = new AppInviteContent.Builder()
                    .setApplinkUrl(appLinkUrl)
                    .setPreviewImageUrl(previewImageUrl)
                    .build();
            AppInviteDialog.show(activity, content);
        }
    }

    public void sendBroadCastToListActivity(Activity activity, boolean cf){
        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(tcn.com.englishbigger.ListActivity.cfBroadcastAction);
        broadCastIntent.putExtra("CF",cf);
        activity.sendBroadcast(broadCastIntent);
    }

    @Override
    public String toString() {
        return "ID: " + getIdUser() +
                "\nName: " + getName() +
                "\nGender: " + getGender() +
                "\nBirthday: " + getBirthday() +
                "\nLocation: " + getLocation() +
                "\nEmail: " + getEmail() +
                "\nUrlAvatar: " + getUrlAvatar();
    }
}
