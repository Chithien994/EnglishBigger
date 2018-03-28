package com.tcn.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import com.tcn.englishbigger.LearnActivity;
import com.tcn.englishbigger.R;
import com.tcn.englishbigger.TopicActivity;
import com.tcn.handle.AdFast;
import com.tcn.handle.Constants;
import com.tcn.handle.IntentActivity;
import com.tcn.handle.MyAction;
import com.tcn.handle.Users;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;

public class AddAndEditTopicFragment extends Fragment implements View.OnClickListener {
    TopicActivity topicActivity; //
    //Declaring views
    private ImageView btnCamera;
    private ImageView btnChoosePhoto;
    private ImageView imgItemTopic;
    private ImageView btnBack;
    private ImageView btnSave;
    private ImageView imgDelete;
    private TextView txtNameControl;
    private TextView txtTotalWords;
    private EditText txtNameTopic;
    private ConstraintLayout layoutAddNewWords;
    private Toolbar toolbarViewTopic;
    private ProgressBar pbLoading;
    private ProgressDialog dialog;

    private int PICK_IMAGE_REQUEST = 1; //Image request code
    private int RESQUEST_TAKE_PHOTO = 2;
    private Bitmap bitmap; //Bitmap to get image from gallery
    private Uri filePath; //Uri to store the image uri
    private Users users; //user information
    private static final String TAG = "AndroidUploadService";
    private UploadNotificationConfig notify;
    private String type;
    private String notificationType;
    private String url;
    private String id;
    private int position;
    private String path = "";
    private String mySelect;
    private String name;


    public AddAndEditTopicFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_and_edit_topic, container, false);
        //The two main components handle the control and event handlers
        AdFast.loadAdView(topicActivity, (RelativeLayout) view.findViewById(R.id.totalLayout), getString(R.string.ad_id_small_3));
        addControls(view);
        addEvents();
        return view;
    }

    private void addEvents() {
        btnCamera.setOnClickListener(this);
        btnChoosePhoto.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        layoutAddNewWords.setOnClickListener(this);
        imgDelete.setOnClickListener(this);
    }


    private void addControls(View view) {
        toolbarViewTopic = view.findViewById(R.id.toolbarViewTopic);
        topicActivity.setSupportActionBar(toolbarViewTopic);
        btnBack = view.findViewById(R.id.btnBack);
        btnSave = view.findViewById(R.id.btnSave);
        btnCamera = view.findViewById(R.id.btnCamera);
        imgDelete = view.findViewById(R.id.imgDelete);
        btnChoosePhoto = view.findViewById(R.id.btnChoosePhoto);
        txtTotalWords = view.findViewById(R.id.txtTotalWords);
        txtNameControl = view.findViewById(R.id.txtNameControl);
        txtNameTopic = view.findViewById(R.id.txtNameTopic);
        layoutAddNewWords = view.findViewById(R.id.layoutAddNewWords);
        pbLoading = view.findViewById(R.id.pbLoading);
        imgItemTopic = view.findViewById(R.id.imgItemTopic);
        users = new Users(topicActivity);
        dialog = new ProgressDialog(topicActivity);
        checkTheChoice();
    }

    //Check user choice (Add or edit)
    private void checkTheChoice() {
        pbLoading.setVisibility(View.GONE);
        Bundle bundle = this.getArguments();
        mySelect = "add";
        if (bundle != null) {
            mySelect = bundle.getString("SELECT");
            Log.i("SELECT","Fragment: "+mySelect);
        }
        if (mySelect.equals("add")==true){ //If the user selects the add button
            txtNameControl.setText(getActivity().getString(R.string.add));
            type = getActivity().getString(R.string.addingTopic);
            notificationType = getActivity().getString(R.string.successfullyAdded);
            id = users.getIdUser();
            url = Constants.INSERT_URL;

        }else if (mySelect.equals("edit")==true){ //If the user selects the edit button
            pbLoading.setVisibility(View.VISIBLE);
            final String link;
            txtNameControl.setText(getActivity().getString(R.string.edit));
            type = getActivity().getString(R.string.updatingTopic);
            notificationType = getActivity().getString(R.string.changedSuccessfully);
            url = Constants.UPDATE_URL;
            position = bundle.getInt("ID");
            txtTotalWords.setText("(" + topicActivity.topicYourModes.get(position).getTotal() + " " + getActivity().getString(R.string.vocabularyWords)+ ")");
            id = topicActivity.topicYourModes.get(position).getId() +"";
            name = topicActivity.topicYourModes.get(position).getName();
            txtNameTopic.setText(name);
            link = topicActivity.topicYourModes.get(position).getLinkImage();
            Log.i("Link",link);
            final Bitmap[] bmap = {null};
            //get image and path
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        Looper.prepare();

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    try {
                        bmap[0] = Glide
                                .with(topicActivity)
                                .load(link+"")
                                .asBitmap()
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .into(-1, -1) // Width and height
                                .get();
                    } catch (final ExecutionException e) {
                        Log.e(TAG, e.fillInStackTrace()+"");
                    } catch (final InterruptedException e) {
                        Log.e(TAG, e.fillInStackTrace()+"");
                    }
                    return null;
                }
                @Override
                protected void onPostExecute(Void dummy) {

                    pbLoading.setVisibility(View.GONE);
                    if (null != bmap[0]) {
                        // The full bitmap should be available here
                        imgItemTopic.setImageBitmap(bmap[0]);
                        path = storeImage(bmap[0]);
                        Log.d(TAG, "Image loaded");
                    };
                }
            }.execute();
        }
    }

    //handling the image chooser activity result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //try catch: Ignore errors when refusing to open the camera, or not selecting images from the gallery
        try {
            Log.i("requestCode",requestCode+"");
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
                filePath = data.getData();

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                    imgItemTopic.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if (requestCode == RESQUEST_TAKE_PHOTO){

                bitmap = (Bitmap) data.getExtras().get("data");
                imgItemTopic.setImageBitmap(bitmap);
                Log.i("RESQUEST_TAKE_PHOTO",requestCode+"");
            }

            //getting the actual path of the image
            path = storeImage(topicActivity.handle.cropBitmap(bitmap));

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Save the image and return the image path
    private String storeImage(Bitmap image) {

        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.d(TAG,
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return null;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.fillInStackTrace());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.fillInStackTrace());
        }
        return pictureFile.getPath();
    }

    /** Create a File for saving an image or video */
    private  File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getApplicationContext().getPackageName()
                + "/Files");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        //String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
       // String mImageName="MI_"+ timeStamp +".jpg";
        String mImageName="up.jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

//    //method to get the file path from uri
//    public String getPath(Uri uri) {
//        try {
//            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
//            cursor.moveToFirst();
//            String document_id = cursor.getString(0);
//            document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
//            cursor.close();
//
//            cursor = getActivity().getContentResolver().query(
//                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                    null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
//            cursor.moveToFirst();
//            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//            cursor.close();
//
//            return path;
//        }catch (Exception e){
//            e.printStackTrace();
//            return null;
//        }
//    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnSave:
                handleSaveTopic(); //Used to upload topic to server
                break;

            case R.id.btnBack:
                handleBack(); //Used to back(no upload)
                break;

            case R.id.btnCamera:
                handleOpenCamera(); //Used to opening camera
                break;

            case R.id.btnChoosePhoto:
                handleChoosePhoto(); // Used to select images from the library
                break;

            case R.id.layoutAddNewWords:
                handleAddNewWords(); // Used to add new words
                break;

            case R.id.imgDelete:
                handleDefaultPhoto();
                break;

            default:{
                break;
            }
        }
    }

    private void handleDefaultPhoto() {
        new AlertDialog.Builder(topicActivity)
        .setMessage(getActivity().getString(R.string.defaultPhoto))
        .setCancelable(false)
        .setNegativeButton(getActivity().getString(R.string.no), null)
        .setPositiveButton(getActivity().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    //sDefault = "true";
                    //bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                    bitmap = topicActivity.handle.convertTextToBitmap(txtNameTopic.getText().toString());
                    imgItemTopic.setImageBitmap(bitmap);
                    path = storeImage(bitmap);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).show();
    }

    private void handleSaveTopic() {
        //to close the keyboard
        txtNameTopic.setEnabled(false);
        txtNameTopic.setEnabled(true);

        String name = txtNameTopic.getText().toString().trim();
        if (mySelect.equals("add")){

            if (checkForExistence(txtNameTopic.getText().toString().trim())){

                handleUploadTopic();
            }else {
                Toast.makeText(topicActivity, getActivity().getString(R.string.topicAlreadyExists), Toast.LENGTH_LONG).show();
            }
        }else {

            for (int i = 0; i < topicActivity.topicYourModes.size(); i++){

                if (name.equalsIgnoreCase(topicActivity.topicYourModes.get(i).getName()) && i != position){
                    Toast.makeText(topicActivity, getActivity().getString(R.string.topicAlreadyExists), Toast.LENGTH_LONG).show();
                    return;
                }

            }
            handleUploadTopic();
        }
    }

    // Used to select images from the library
    //method to show file chooser
    private void handleChoosePhoto() {
        topicActivity.requestStoragePermission();
        try {

            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

        }catch (Exception e){

        }

    }

    //Used to opening camera
    private void handleOpenCamera() {
        topicActivity.requestCameraPermission();
        try {
            Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(i, RESQUEST_TAKE_PHOTO);
        }catch (Exception ex){
            ex.printStackTrace();
            Toast.makeText(topicActivity, getActivity().getString(R.string.thisDeviceHasNoCamera), Toast.LENGTH_LONG).show();
        }

    }

    // Used to add new words
    private void handleAddNewWords() {
        MyAction.setFragmentNew(topicActivity, MyAction.ADD_NEW_WORD_FRAGMENT);
        MyAction.setActivityBulb(topicActivity, MyAction.TOPIC_ACTIVITY);
        MyAction.setPosition(topicActivity, position);
        IntentActivity.handleOpenLearnActivity(topicActivity);
    }

    //Used to back(no upload)
    private void handleBack() {
        MyAction.setFragmentNew(topicActivity, MyAction.TOPIC_FRAGMENT);
        topicActivity.callFragment(new TopicFragment());
    }

    //Used to upload topic to server
    /*
    * This is the method responsible for image upload
    * We need the full image path and the name for the image in this method
    * */
    private void handleUploadTopic() {

        //Check the name field entered
        if (txtNameTopic.getText().toString().equals("")){

            //Not yet entered
            Toast.makeText(topicActivity, getActivity().getString(R.string.youHaveNotEnteredTheTopicNameYet), Toast.LENGTH_SHORT).show();

        }else {
            //Was entered

            //Initialize notification dialog
            dialog = new ProgressDialog(topicActivity);
            dialog.setMessage(getActivity().getString(R.string.pleaseWait));
            dialog.setCancelable(true);
            dialog.show();

            notify = new UploadNotificationConfig()
                    .setTitle(type)
                    .setIcon(R.drawable.up)
                    .setCompletedMessage(notificationType)
                    .setInProgressMessage(getActivity().getString(R.string.uploadInProgress))
                    .setAutoClearOnSuccess(true)
                    .setErrorMessage(getActivity().getString(R.string.failure));
            //getting name for the image
            final String name = txtNameTopic.getText().toString().trim();

            //
            if (path.equals("")){
                //sDefault = "true";
                //bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                try {
                    path = storeImage(topicActivity.handle.convertTextToBitmap(name));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //Uploading code
            try {
                String uploadId = UUID.randomUUID().toString();
                //Creating a multi part request
                new MultipartUploadRequest(topicActivity, uploadId, url)
                        .addFileToUpload(path, "image") //Adding file
                        .addParameter("id", id) //Adding id user to the request
                        .addParameter("name", name) //Adding text parameter to the request
                        .setUtf8Charset()
                        .setNotificationConfig(notify)
                        .setMaxRetries(2).setDelegate(new UploadStatusDelegate() {
                    @Override
                    public void onProgress(UploadInfo uploadInfo) {
                        Log.i("Progress", uploadInfo.toString());
                    }

                    @Override
                    public void onError(UploadInfo uploadInfo, Exception exception) {
                        Log.i("Error UploadInfo", uploadInfo.toString());
                        Log.i("Error Exception", exception.toString());
                        dialog.cancel();
                        Toast.makeText(topicActivity, getActivity().getString(R.string.errorInternetConnectionProblems), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCompleted(UploadInfo uploadInfo, ServerResponse serverResponse) {

                        Log.i("Completed","serverResponse " +serverResponse.getBodyAsString());
                        specifyNotify(handleMessage(serverResponse.getBodyAsString().toLowerCase()));

                        //Close the dialog and restart TopicActivity, when added successfully
                        //Assign updated position
                        dialog.cancel();
                        MyAction.setRefreshTopic(topicActivity, true);
                        MyAction.setFragmentNew(topicActivity, MyAction.TOPIC_FRAGMENT);
                        topicActivity.startActivity(new Intent(topicActivity, TopicActivity.class).putExtra("position", position));
                        topicActivity.finish();
                    }

                    @Override
                    public void onCancelled(UploadInfo uploadInfo) {
                        Log.i("Cancelled UploadInfo", uploadInfo.toString());
                        dialog.cancel();
                    }
                }).startUpload(); //Starting the upload
            } catch (Exception exc) {

                if (exc.getMessage().equalsIgnoreCase("Please specify a file path! Passed path value is: null")
                        || exc.getMessage().equalsIgnoreCase("Please specify a file path!")){

//                    new AlertDialog.Builder(topicActivity)
//                            .setMessage(getActivity().getString(R.string.itLooksLikeYouHaveNotSelectedAPhotoYet))
//                            .setCancelable(false)
//                            .setNegativeButton(getActivity().getString(R.string.no), null)
//                            .setPositiveButton(getActivity().getString(R.string.defaultPhoto), new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    try {
//                                        //sDefault = "true";
//                                        //bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
//                                        path = storeImage(topicActivity.handle.convertTextToBitmap(name));
//                                        handleUploadTopic();
//
//                                    }catch (Exception e){
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }).show();
                    try {
                        //sDefault = "true";
                        //bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                        path = storeImage(topicActivity.handle.convertTextToBitmap(name));
                        handleUploadTopic();

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                exc.printStackTrace();
                dialog.cancel();
            }

        }

    }

    private String handleMessage(String message){
        return message.contains("topic already exists")?"Topic already exists":
        message.contains("successfully added")?"Successfully added":
        message.contains("changed successfully")?"Changed successfully":
        message.contains("error input parameters")?"Error input parameters":
        message.contains("failure")?"Failure":message;
    }
    private void specifyNotify(String message) {

        switch (message.toLowerCase()){
            case "topic already exists":
                Toast.makeText(getActivity(),getActivity().getString(R.string.topicAlreadyExists),Toast.LENGTH_LONG).show();
                break;

            case "successfully added":
                Toast.makeText(getActivity(),getActivity().getString(R.string.successfullyAdded),Toast.LENGTH_LONG).show();
                break;

            case "changed successfully":
                Toast.makeText(getActivity(),getActivity().getString(R.string.changedSuccessfully),Toast.LENGTH_LONG).show();
                break;

            case "error input parameters":
                Toast.makeText(getActivity(),getActivity().getString(R.string.errorInputParameters),Toast.LENGTH_LONG).show();
                break;

            default:{
                Toast.makeText(getActivity(),getActivity().getString(R.string.failure),Toast.LENGTH_LONG).show();
                break;
            }
        }
    }

    private boolean checkForExistence(String name) {
        boolean b = true;
        for (int i = 0; i < topicActivity.topicYourModes.size(); i++){
            if (name.equalsIgnoreCase(topicActivity.topicYourModes.get(i).getName())){
                b = false;
                break;
            }
        }
        return b;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TopicActivity) {
            topicActivity = (TopicActivity) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

}
