package image.gallery.organize;

import android.content.Context;

import image.gallery.organize.Helper.Utils;

import org.apache.commons.collections4.map.ListOrderedMap;

import java.util.ArrayList;

import androidx.multidex.MultiDex;

public class MyApplication extends android.app.Application {

    public static MyApplication application;

    public static ArrayList<String> dataArr = new ArrayList<>();

    public static ListOrderedMap<String, ArrayList<String>> folderData = new ListOrderedMap<>();
    public static ListOrderedMap<String, ArrayList<String>> DefaultFolderData = new ListOrderedMap<>();

    public static boolean isAsc = true;
    public static boolean isReloadHidden = false;

    public static ArrayList<String> binImages = new ArrayList<>();

    public static ArrayList<String> allimages = new ArrayList<>();
    public static ArrayList<String> allimagesCopyWithoutDates = new ArrayList<>();

    public static boolean isEnteredPwd = false;

    public static ArrayList<String> HiddenImages = new ArrayList<>();
    public static ArrayList<String> HiddenImagesWithoutFolder = new ArrayList<>();

    static {
        System.loadLibrary("NativeImageProcessor");

    }


    public static synchronized MyApplication getInstance() {
        return application;
    }

    public static synchronized Context getContext() {
        return getInstance().getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        application = this;
        Utils.getInstance().initializeRenderScript(getApplicationContext());

    }

}