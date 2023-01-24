package image.gallery.organize.Helper;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import image.gallery.organize.Activity.LeavingAppActivity;
import image.gallery.organize.Activity.MainActivity;
import image.gallery.organize.Adhelper.InterAds;
import image.gallery.organize.MyApplication;
import image.gallery.organize.R;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;
import com.preference.PowerPreference;

import java.io.File;

public class Constant {

    public static String isReviewShow = "isReviewShow";

    public static String recycleList = "recycleList";
    public static String DEF_VALUE = "#1aff0006";

    public static String adsLog = "adsLog";
    public static String errorLog = "errorLog";
    public static String isFetched = "isFetched";

    public static final String POLICY = "PolicyLink";

    public static final String AdsOnOff = "AdsOnOff";

    public static final String AppOpen = "AppOpen";

    public static final String GoogleAppOpenOnOff = "GoogleAppOpenOnOff";
    public static final String GoogleBannerOnOff = "GoogleBannerOnOff";

    public static final String GoogleMiniNativeOnOff = "GoogleMiniNativeOnOff";
    public static final String GoogleLargeNativeOnOff = "GoogleLargeNativeOnOff";
    public static final String GoogleListNativeOnOff = "GoogleListNativeOnOff";

    public static final String SERVER_INTERVAL_COUNT = "GoogleIntervalCount";
    public static final String APP_INTERVAL_COUNT = "APP_INTERVAL_COUNT";

    public static final String BACK_ADS = "GoogleBackInterOnOff";
    public static final String SERVER_BACK_COUNT = "GoogleBackInterIntervalCount";
    public static final String APP_BACK_COUNT = "APP_BACK_COUNT";
    public static final String GoogleSplashOpenAdsOnOff = "GoogleSplashOpenAdsOnOff";
    public static final String GoogleExitSplashInterOnOff = "GoogleExitSplashInterOnOff";

    public static final String GoogleNativeTextOnOff = "GoogleNativeTextOnOff";
    public static final String GoogleNativeText = "GoogleNativeText";

    public static final String GoogleWhichOneNative = "GoogleWhichOneNative";
    public static final String ListNativeWhichOne = "ListNativeWhichOne";
    public static final String ListNativeAfterCount = "ListNativeAfterCount";

    public static final String ShowDialogBeforeAds = "ShowDialogBeforeAds";
    public static final String DialogTimeInSec = "DialogTimeInSec";
    public static final String LoaderNativeOnOff = "LoaderNativeOnOff";

    public static final String OneSignalAppId = "OneSignalAppId";

    public static String BANNERID = "GoogleBannerAds";
    public static String OPENAD = "GoogleAppOpenAds";
    public static String INTERID = "GoogleInterAds";
    public static String NATIVEID = "GoogleNativeAds";

    public static String mAds = "mAds";


    public static void showLog(String message) {
        Log.e("errorLog", message);
    }

    public static void Log(String message) {
        Log.e("errorLog", message);
    }

    public static void showErrorLog(String message) {
        Log.e("errorLog", message);
    }


    public static boolean checkInternet(Activity activity) {
        ConnectivityManager cm =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    public static void gotoTerms(Context context) {
        try {
            String packageName = "com.android.chrome";
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            builder.setToolbarColor(ContextCompat.getColor(context, R.color.colorMain));
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.intent.setPackage(packageName);
            customTabsIntent.launchUrl(context, Uri.parse(PowerPreference.getDefaultFile().getString(Constant.POLICY, "https://1064.win.qureka.com/")));
        } catch (Exception e) {
            Log.e("TAG", e.toString());
        }
    }

    public static String getOutputFolder() {
        File dCimDirPath = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM).getAbsolutePath());

        if (!dCimDirPath.exists())
            dCimDirPath.mkdir();

        return dCimDirPath + File.separator + MyApplication.getContext().getString(R.string.folder_output);
    }

    public static void showReview(Activity context) {
        ReviewManager manager = ReviewManagerFactory.create(context);
    }


    public static void showRateDialog(Activity activity, boolean isStart, boolean isAds) {
        try {
            Dialog dialog = new Dialog(activity);
            dialog.setContentView(R.layout.dialog_exit);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            final AppCompatButton btnCancel = dialog.findViewById(R.id.btnCancel);
            final AppCompatButton btnRate = dialog.findViewById(R.id.btnRate);
            final AppCompatButton btnExit = dialog.findViewById(R.id.btnExit);

            if (!isStart)
                btnExit.setVisibility(View.GONE);

            btnRate.setOnClickListener(view -> {
                dialog.dismiss();
                Constant.rateUs(activity);
            });

            btnExit.setOnClickListener(view -> {
                dialog.dismiss();
                activity.finishAffinity();
            });

            btnCancel.setOnClickListener(view -> {
                dialog.dismiss();
            });

            dialog.show();


        } catch (Exception e) {
            Log.e("Catch", e.getMessage());
        }

    }

    public interface onItemClick {
        public void onSuccess();

        public void onFailed();
    }

    public static void showBottomSheetDialog(Context context, onItemClick click) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(R.layout.dialog_confirm_delete);
        bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView textView = bottomSheetDialog.findViewById(R.id.txtCancel);
        TextView textView2 = bottomSheetDialog.findViewById(R.id.txtDelete);
        textView.setOnClickListener(v -> {
            click.onFailed();
            bottomSheetDialog.dismiss();
        });
        textView2.setOnClickListener(v -> {
            click.onSuccess();
            bottomSheetDialog.dismiss();
        });
        bottomSheetDialog.show();
    }


    public static void rateUs(Activity activity) {
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + activity.getPackageName())));
        } catch (ActivityNotFoundException e) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + activity.getPackageName())));
        }
    }


}
