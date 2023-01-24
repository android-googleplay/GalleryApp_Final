package image.gallery.organize.Adhelper;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import image.gallery.organize.Helper.Constant;
import image.gallery.organize.R;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.preference.PowerPreference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class ListNative2Ads {

    private static ArrayList<NativeAd> gNativeAd = new ArrayList<>();
    private static String ad_type;

    public void loadNativeAds(Activity activity, Dialog dialog) {
        if (PowerPreference.getDefaultFile().getBoolean(Constant.AdsOnOff, false) && PowerPreference.getDefaultFile().getBoolean(Constant.GoogleListNativeOnOff, false)) {

            if (gNativeAd.size() >= 5) {
                Collections.shuffle(gNativeAd);
                return;
            }

            final String nativeAdstr = PowerPreference.getDefaultFile().getString(Constant.NATIVEID, "123");

            AdLoader.Builder builder = new AdLoader.Builder(activity, nativeAdstr);
            builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                @Override
                public void onNativeAdLoaded(@NonNull NativeAd natives) {
                    gNativeAd.add(0, natives);
                }
            });

            VideoOptions videoOptions = new VideoOptions.Builder()
                    .setStartMuted(true)
                    .build();

            NativeAdOptions adOptions = new NativeAdOptions.Builder()
                    .setVideoOptions(videoOptions)
                    .build();

            builder.withNativeAdOptions(adOptions);

            AdLoader adLoader = builder.withAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(LoadAdError errorCode) {
                    Constant.showLog(errorCode.toString());
                }
            }).build();

            adLoader.loadAd(new AdRequest.Builder().build());
        }
    }


    public void showListNativeAds(Activity activity, FrameLayout nativeAd, ImageView adSpace) {
        if (PowerPreference.getDefaultFile().getBoolean(Constant.AdsOnOff, true)) {
            if (adSpace != null)
                showLargeNativeAds(activity, nativeAd, adSpace);
            else
                showLargeNativeAds(activity, nativeAd);
        } else {
            if (nativeAd != null)
                nativeAd.setVisibility(View.GONE);
            if (adSpace != null)
                adSpace.setVisibility(View.GONE);
        }
    }

    public void populateUnifiedNativeAdView(NativeAd nativeAd, NativeAdView adView) {

        try {
            if (adView.findViewById(R.id.rlMain) != null) {
                RelativeLayout relativeLayout = adView.findViewById(R.id.rlMain);
                ViewTreeObserver vto = relativeLayout.getViewTreeObserver();
                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        relativeLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        int width = relativeLayout.getMeasuredWidth();
                        relativeLayout.setLayoutParams(new FrameLayout.LayoutParams(width, width));
                    }
                });
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        if (adView.findViewById(R.id.ad_media) != null) {
            MediaView mediaView = adView.findViewById(R.id.ad_media);
            mediaView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
            adView.setMediaView(mediaView);

        }
        if (adView.findViewById(R.id.ad_headline) != null)
            adView.setHeadlineView(adView.findViewById(R.id.ad_headline));

        if (adView.findViewById(R.id.ad_body) != null)
            adView.setBodyView(adView.findViewById(R.id.ad_body));

        if (adView.findViewById(R.id.ad_call_to_action) != null)
            adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));

        if (adView.findViewById(R.id.ad_app_icon) != null)
            adView.setIconView(adView.findViewById(R.id.ad_app_icon));

        if (adView.findViewById(R.id.ad_stars) != null)
            adView.setStarRatingView(adView.findViewById(R.id.ad_stars));


        if (nativeAd.getStarRating() == null) {
            if (adView.getStarRatingView() != null)
                Objects.requireNonNull(adView.getStarRatingView()).setVisibility(View.GONE);
        } else {
            if (adView.getStarRatingView() != null) {
                Objects.requireNonNull(adView.getStarRatingView()).setVisibility(View.VISIBLE);
                ((RatingBar) adView.getStarRatingView()).setRating(Float.parseFloat(String.valueOf(nativeAd.getStarRating())));
            }
        }

        if (nativeAd.getHeadline() == null) {
            if (adView.getHeadlineView() != null)
                Objects.requireNonNull(adView.getHeadlineView()).setVisibility(View.GONE);
        } else {
            if (adView.getHeadlineView() != null) {
                Objects.requireNonNull(adView.getHeadlineView()).setVisibility(View.VISIBLE);
                ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
            }
        }

        if (nativeAd.getBody() == null) {
            if (adView.getBodyView() != null)
                Objects.requireNonNull(adView.getBodyView()).setVisibility(View.GONE);
        } else {
            if (adView.getBodyView() != null) {
                Objects.requireNonNull(adView.getBodyView()).setVisibility(View.VISIBLE);
                ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
            }
        }

        if (nativeAd.getCallToAction() == null) {
            if (adView.getCallToActionView() != null)
                Objects.requireNonNull(adView.getCallToActionView()).setVisibility(View.INVISIBLE);
        } else {
            if (adView.getCallToActionView() != null) {
                Objects.requireNonNull(adView.getCallToActionView()).setVisibility(View.VISIBLE);
                ((TextView) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
            }
        }

        if (nativeAd.getIcon() == null) {
            if (adView.getIconView() != null)
                Objects.requireNonNull(adView.getIconView()).setVisibility(View.GONE);
        } else {
            if (adView.getIconView() != null) {
                ((ImageView) Objects.requireNonNull(adView.getIconView())).setImageDrawable(
                        nativeAd.getIcon().getDrawable());
                adView.getIconView().setVisibility(View.VISIBLE);
            }
        }

        adView.setNativeAd(nativeAd);
    }

    public void showLargeNativeAds(Activity activity, FrameLayout adLayout, ImageView adSpace) {

        RelativeLayout adView = null;

        if (PowerPreference.getDefaultFile().getBoolean(Constant.AdsOnOff, true)) {

            if (PowerPreference.getDefaultFile().getBoolean(Constant.GoogleListNativeOnOff, true) && gNativeAd.size() > 0) {

                adView = (RelativeLayout) activity.getLayoutInflater().inflate(R.layout.layout_native_image, null);

                NativeAd lovalNative = gNativeAd.get(0);

                populateUnifiedNativeAdView(lovalNative, adView.findViewById(R.id.uadview));

                adLayout.removeAllViews();
                adLayout.addView(adView);

                adSpace.setVisibility(View.GONE);
                adLayout.setVisibility(View.VISIBLE);

                loadNativeAds(activity, null);

            } else {

                loadNativeAds(activity, null);

                adLayout.setVisibility(View.GONE);
                adSpace.setVisibility(View.GONE);
            }
        } else {
            adLayout.setVisibility(View.GONE);
            adSpace.setVisibility(View.GONE);
        }
    }


    public void showLargeNativeAds(Activity activity, FrameLayout adLayout) {

        LinearLayout adView = null;

        if (PowerPreference.getDefaultFile().getBoolean(Constant.AdsOnOff, true)) {

            if (PowerPreference.getDefaultFile().getBoolean(Constant.GoogleListNativeOnOff, true) && gNativeAd.size() > 0) {

                adView = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.layout_native_folder, null);

                NativeAd lovalNative = gNativeAd.get(0);

                populateUnifiedNativeAdView(lovalNative, adView.findViewById(R.id.uadview));

                adLayout.removeAllViews();
                adLayout.addView(adView);

                adLayout.setVisibility(View.VISIBLE);

                loadNativeAds(activity, null);

            } else {

                loadNativeAds(activity, null);

                adLayout.setVisibility(View.GONE);
            }
        } else {
            adLayout.setVisibility(View.GONE);
        }
    }


}
