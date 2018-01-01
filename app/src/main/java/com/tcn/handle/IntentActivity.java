package com.tcn.handle;

import android.app.Activity;
import android.content.Intent;

import com.tcn.englishbigger.LearnActivity;
import com.tcn.englishbigger.ListActivity;
import com.tcn.englishbigger.TopicActivity;

/**
 * Created by MyPC on 12/10/2017.
 */

public class IntentActivity {
    public static final int INTENT_TOPIC = 0;
    public static final int INTENT_LEARN = 1;
    public static final int INTENT_WHAT_DO_PEOPLE_LEARN = 3;
    public static final int INTENT_ADD = 4;


    public static void handleOpenLearnActivity(Activity activity) {
        Intent intent = new Intent(activity, LearnActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        activity.startActivity(intent);
    }

    public static void openAndUpdateLearnActivity(Activity activity) {
        Intent intent = new Intent(activity, LearnActivity.class);
        activity.startActivity(intent);
    }

    public static void handleOpenTopicActivy(Activity activity) {
        Intent intent = new Intent(activity, TopicActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        activity.startActivity(intent);
    }

    public static void openAndUpdateTopicActivy(Activity activity) {
        Intent intent = new Intent(activity, TopicActivity.class);
        activity.startActivity(intent);
    }

    public static void handleOpenListActivity(Activity activity) {
        Intent intent = new Intent(activity, ListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        activity.startActivity(intent);
    }

    public static void apenAndUpdateListActivity(Activity activity) {
        Intent intent = new Intent(activity, ListActivity.class);
        activity.startActivity(intent);
    }
}
