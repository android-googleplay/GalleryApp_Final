package image.gallery.organize.Adhelper;

import android.app.Activity;
import android.widget.FrameLayout;
import android.widget.TextView;

import image.gallery.organize.Helper.Constant;
import com.preference.PowerPreference;


public class ListBannerAds {

    public void showBannerAds(Activity activity, FrameLayout nativeAd, TextView adSpace) {
        if (PowerPreference.getDefaultFile().getInt(Constant.GoogleWhichOneNative, 0) == 0) {
            new BannerAds().showBannerAds(activity, nativeAd, adSpace);
        } else {
            new MiniNativeAds().showNativeAds(activity, null, nativeAd, adSpace);
        }
    }
}
