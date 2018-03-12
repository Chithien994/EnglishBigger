package com.tcn.handle;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import com.tcn.englishbigger.R;

/**
 * Created by MyPC on 29/12/2017.
 */

public class AdFast {
    public static void loadAdView(Context context, LinearLayout totalLayout, String ad_id) {
        // Sample AdMob app ID:
        MobileAds.initialize(context, context.getString(R.string.ad_mob_app_id));

        ViewGroup.LayoutParams framelayout_params =
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
        FrameLayout mFrameLayout = new FrameLayout(context);
        mFrameLayout.setLayoutParams(framelayout_params);

        AdView adView = new AdView(context);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(ad_id);

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        RelativeLayout relativeLayout = new RelativeLayout(context);
        mFrameLayout.addView(relativeLayout);

        RelativeLayout.LayoutParams adViewParams = new RelativeLayout.LayoutParams(
                AdView.LayoutParams.WRAP_CONTENT,
                AdView.LayoutParams.WRAP_CONTENT);
        // align bottom
        adViewParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        // align center
        adViewParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        relativeLayout.addView(adView, adViewParams);

        adView.loadAd(adRequest);
        adView.setBackgroundColor(Color.BLACK);
        adView.setBackgroundColor(0);
        totalLayout.addView(mFrameLayout);
    }

    public static void loadAdView(Context context, RelativeLayout totalLayout, String ad_id) {
        // Sample AdMob app ID:
        MobileAds.initialize(context, context.getString(R.string.ad_mob_app_id));

        ViewGroup.LayoutParams framelayout_params =
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
        FrameLayout mFrameLayout = new FrameLayout(context);
        mFrameLayout.setLayoutParams(framelayout_params);

        AdView adView = new AdView(context);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(ad_id);

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        RelativeLayout relativeLayout = new RelativeLayout(context);
        mFrameLayout.addView(relativeLayout);

        RelativeLayout.LayoutParams adViewParams = new RelativeLayout.LayoutParams(
                AdView.LayoutParams.WRAP_CONTENT,
                AdView.LayoutParams.WRAP_CONTENT);
        // align bottom
        adViewParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        // align center
        adViewParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        relativeLayout.addView(adView, adViewParams);

        adView.loadAd(adRequest);
        adView.setBackgroundColor(Color.BLACK);
        adView.setBackgroundColor(0);
        totalLayout.addView(mFrameLayout);
    }
}
