package image.gallery.organize.Adhelper;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
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
import java.util.Objects;


public class MiniNativeAds {

    private static ArrayList<NativeAd> gNativeAd = new ArrayList<>();
    private static String ad_type;

    public void loadNativeAds(Activity activity, Dialog dialog) {
        if (PowerPreference.getDefaultFile().getBoolean(Constant.AdsOnOff, false) && PowerPreference.getDefaultFile().getBoolean(Constant.GoogleMiniNativeOnOff, false)) {
            final String nativeAdstr = PowerPreference.getDefaultFile().getString(Constant.NATIVEID, "123");

            AdLoader.Builder builder = new AdLoader.Builder(activity, nativeAdstr);
            builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                @Override
                public void onNativeAdLoaded(@NonNull NativeAd natives) {
                    gNativeAd.clear();
                    gNativeAd.add(natives);
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


    public FrameLayout getFrameLayout(Activity activity, Dialog dialog) {
        if (dialog != null) {
            return dialog.findViewById(R.id.adFrameMini);
        } else {
            return activity.findViewById(R.id.adFrameMini);
        }
    }


    public TextView getTextLayout(Activity activity, Dialog dialog) {
        if (dialog != null) {
            return dialog.findViewById(R.id.adSpaceMini);
        } else {
            return activity.findViewById(R.id.adSpaceMini);
        }
    }


    public void showNativeAds(Activity activity, Dialog dialog, FrameLayout adLayout, TextView adSpace) {

        if (adLayout == null || adSpace == null) {
            adLayout = getFrameLayout(activity, dialog);
            adSpace = getTextLayout(activity, dialog);
        }

        if (adLayout == null || adSpace == null)
            return;

        LinearLayout adView = null;

        if (PowerPreference.getDefaultFile().getBoolean(Constant.AdsOnOff, true)) {

            if (PowerPreference.getDefaultFile().getBoolean(Constant.GoogleMiniNativeOnOff, true) &&
                    gNativeAd.size() > 0) {

                adView = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.layout_native_tiny, null);

                NativeAd lovalNative = gNativeAd.get(0);

                assert adView != null;
                populateUnifiedNativeAdView(lovalNative, adView.findViewById(R.id.uadview));

                adLayout.removeAllViews();
                adLayout.addView(adView);

                adSpace.setVisibility(View.GONE);
                adLayout.setVisibility(View.VISIBLE);

                loadNativeAds(activity, dialog);

            } else {

                loadNativeAds(activity, dialog);

                adLayout.setVisibility(View.GONE);
                adSpace.setVisibility(View.GONE);

            }
        } else {
            adLayout.setVisibility(View.GONE);
            adSpace.setVisibility(View.GONE);
        }
    }


    public void populateUnifiedNativeAdView(NativeAd nativeAd, NativeAdView adView) {

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
                if (PowerPreference.getDefaultFile().getBoolean(Constant.GoogleNativeTextOnOff, true)) {
                    ((TextView) adView.getCallToActionView()).setText(PowerPreference.getDefaultFile().getString(Constant.GoogleNativeText, "OPEN"));
                } else {
                    ((TextView) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
                }
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
}
