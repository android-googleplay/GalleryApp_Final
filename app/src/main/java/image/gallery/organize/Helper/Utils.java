package image.gallery.organize.Helper;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.renderscript.Allocation;
import androidx.renderscript.Element;
import androidx.renderscript.RenderScript;
import androidx.renderscript.ScriptIntrinsicConvolve3x3;

import com.gallery.photo.album.photomanager.ScriptC_brightness;
import com.gallery.photo.album.photomanager.ScriptC_burn;
import com.gallery.photo.album.photomanager.ScriptC_saturation;
import image.gallery.organize.Activity.AddtoAlbumActivity;
import image.gallery.organize.Activity.MainActivity;
import image.gallery.organize.Activity.SetpasswordActivity;
import image.gallery.organize.Activity.SplashActivity;
import image.gallery.organize.Fragment.FolderFragment;
import image.gallery.organize.Fragment.HideImagesFragment;
import image.gallery.organize.MyApplication;
import image.gallery.organize.R;
import image.gallery.organize.library.TastyToast.TastyToast;
import com.github.florent37.inlineactivityresult.InlineActivityResult;
import com.github.florent37.inlineactivityresult.Result;
import com.github.florent37.inlineactivityresult.callbacks.FailCallback;
import com.github.florent37.inlineactivityresult.callbacks.SuccessCallback;
import com.github.florent37.inlineactivityresult.request.Request;
import com.github.florent37.inlineactivityresult.request.RequestFabric;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.preference.PowerPreference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TreeMap;

public class Utils {

    Dialog mLoadingDialog;

    int tot = 0;
    private static RenderScript rs;
    private static Utils utils;
    public final String ImageSaveDirectory = "Gallery";
    public final String HideSaveDirectory = ".Gallery";

    public static Utils getInstance() {
        if (utils == null)
            utils = new Utils();

        return utils;
    }

    public void dismissLoader() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing())
            mLoadingDialog.dismiss();
    }

    @SuppressLint("SetTextI18n")
    public void showLoader(Activity mActivity) {

        try {
            mLoadingDialog = new Dialog(mActivity);
            mLoadingDialog.setContentView(R.layout.dialog_loader);
            mLoadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mLoadingDialog.setCancelable(false);
            mLoadingDialog.setCanceledOnTouchOutside(false);
            mLoadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


            mLoadingDialog.show();
            TextView textView = mLoadingDialog.findViewById(R.id.txt_Msg);
            if (mActivity instanceof AddtoAlbumActivity)
                textView.setText(mActivity.getResources().getString(R.string.task_progress));
            else
                textView.setText(mActivity.getResources().getString(R.string.hide_progress));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void showSuccess(Context context, String msg) {
        TastyToast.makeText(context, msg, TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
    }

    public void showError(Context context, String msg) {
        TastyToast.makeText(context, msg, TastyToast.LENGTH_SHORT, TastyToast.ERROR);
    }

    public void showInfo(Context context, String msg) {
        TastyToast.makeText(context, msg, TastyToast.LENGTH_SHORT, TastyToast.INFO);
    }

    public void showWarning(Context context, String msg) {
        TastyToast.makeText(context, msg, TastyToast.LENGTH_SHORT, TastyToast.WARNING);
    }

    public void showConfusing(Context context, String msg) {
        TastyToast.makeText(context, msg, TastyToast.LENGTH_SHORT, TastyToast.CONFUSING);
    }

    public String getImageSaveDirectory() {
        String path = Constant.getOutputFolder() + "/" + ImageSaveDirectory;

        if (!new File(path).exists()) {
            new File(path).mkdirs();
        }

        return path;
    }

    /*public String getHideSaveDirectory() {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + HideSaveDirectory;

        if (!new File(path).exists()) {
            new File(path).mkdirs();
        }

        return path;
    }
*/

    public String getHideSaveDirectory(Context context) {
        /*String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + HideSaveDirectory;

        if (!new File(path).exists()) {
            new File(path).mkdirs();
        }

        return path;*/

        String path = context.getCacheDir() + "/" + ImageSaveDirectory + "/" + HideSaveDirectory;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }


        return file.getAbsolutePath();
    }

    public String getBinFolderPath(Context context) {
        String path = context.getCacheDir() + "/" + ImageSaveDirectory + "/bin";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }

        return file.getAbsolutePath();
    }

    public void saveBitmapToStorage(Bitmap bitmap, File file) {

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    public int getAllPhotosCount(Context context) {

        final String[] columns = {MediaStore.Images.ImageColumns._ID};
        Cursor cursor = null;
        int tot = 0;
        try {
            cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, null);

            if (cursor != null)
                tot = cursor.getCount();
            else
                tot = 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return tot;
    }

    public int getAllVideoCount(Context context) {

        final String[] columns = {MediaStore.Video.VideoColumns._ID};
        Cursor cursor = null;
        int tot = 0;

        try {
            cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, columns, null, null, null);

            if (cursor != null)
                tot = cursor.getCount();
            else
                tot = 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return tot;
    }

    public void copyImageFile(Context context, File sourceFile, File destFile) {
        try {

            InputStream in = new FileInputStream(sourceFile);
            OutputStream out = new FileOutputStream(destFile);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();

            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (destFile.getAbsolutePath().contains("storage")) {

            long length = destFile.length();
            ContentValues contentValues = new ContentValues(9);
            contentValues.put("title", destFile.getName());
            contentValues.put("_display_name", destFile.getName());

            if (getInstance().isImageType(sourceFile.getAbsolutePath(), context))
                contentValues.put(MediaStore.Files.FileColumns.MEDIA_TYPE, MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
            else {
                contentValues.put(MediaStore.Files.FileColumns.MEDIA_TYPE, MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
            }
            contentValues.put("_data", destFile.getAbsolutePath());
            contentValues.put("_size", Long.valueOf(length));

            try {
                context.getContentResolver().insert(MediaStore.Files.getContentUri("external"), contentValues);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


        scanMedia(context, destFile.getAbsolutePath());
    }


    private void copytoCache(Context context, File sourceFile, File destFile) {
        try {

            InputStream in = new FileInputStream(sourceFile);
            OutputStream out = new FileOutputStream(destFile);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();

            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Cursor getAllImagesVideoFromStorageForDate(Context context) {

        final String[] columns = {MediaStore.Files.FileColumns.DATE_MODIFIED, MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.DURATION, MediaStore.Files.FileColumns.MEDIA_TYPE};
        final String orderBy = MediaStore.Files.FileColumns.DATE_MODIFIED + " desc ";

        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

        return context.getContentResolver().query(MediaStore.Files.getContentUri("external"), columns, selection, null, orderBy);
    }

    private int getMediaIdFromPath(String path, Context context) {
        final String[] columns = {MediaStore.Files.FileColumns._ID};

        Cursor cursor = null;

        int id = 0;

        try {
            cursor = context.getContentResolver().query(MediaStore.Files.getContentUri("external"),
                    columns, MediaStore.Files.FileColumns.DATA + "=? ",
                    new String[]{path}, null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    id = cursor.getInt(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return id;
    }


    public void hidePhotos(ArrayList<String> list, AppCompatActivity context, boolean isShowFragment, hidedone callback) {

        if (!Utils.getInstance().isPasswordSetupDone(context)) {
            if (isShowFragment) {

                HideImagesFragment.list.clear();
                HideImagesFragment.list.addAll(list);

                if (HideImagesFragment.stickyList != null && HideImagesFragment.llNoDataFound != null && MyApplication.isEnteredPwd) {
                    if (MyApplication.HiddenImages.size() > 0) {
                        HideImagesFragment.stickyList.setVisibility(View.VISIBLE);
                        HideImagesFragment.llNoDataFound.setVisibility(View.GONE);
                    } else {
                        HideImagesFragment.stickyList.setVisibility(View.GONE);
                        HideImagesFragment.llNoDataFound.setVisibility(View.VISIBLE);
                    }
                }
                MainActivity.activity.onBackPressed();
                MainActivity.viewPager.setCurrentItem(2, true);

            } else {
                context.startActivity(new Intent(context, SetpasswordActivity.class).putExtra("list", list));
            }

        } else {

            if (SplashActivity.isDataLoaded) {
                showDialog(context, list, callback);
                if (!isHiddenApiDone(context)) {
                    callHideApi(context);
                }
            } else {
                Utils.getInstance().showImageLoadingDialog(context, () -> {
                    showDialog(context, list, callback);

                    if (!isHiddenApiDone(context)) {
                        callHideApi(context);
                    }
                });
            }
        }
    }

    private void callHideApi(Context context) {
        setHiddenApiDone(true, context);
    }


    @SuppressLint("CheckResult")
    private void showDialog(AppCompatActivity context, ArrayList<String> list, hidedone listener) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            showLoader(context);
            new Thread(new Runnable() {
                @Override
                public void run() {

                    ArrayList<Uri> uris = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        String path = list.get(i);

                        Utils.getInstance().hideImage(list.get(i), context);
                        if (new File(list.get(i)).exists()) {
                            Uri uri = Utils.getInstance().getAppendedUri(list.get(i), context);
                            if (uri != null)
                                uris.add(uri);
                        } else {
                            Utils.getInstance().removeImage(path, context);
                        }
                    }


                    if (uris.size() > 0) {

                        dismissLoader();
                        PendingIntent intent = MediaStore.createDeleteRequest(context.getContentResolver(), uris);
                        Request request = RequestFabric.create(intent.getIntentSender(), null, 0, 0, 0, null);

                        new InlineActivityResult(context)
                                .startForResult(request)
                                .onSuccess(new SuccessCallback() {
                                    @Override
                                    public void onSuccess(Result result) {
                                        if (result.getResultCode() == context.RESULT_OK) {

                                            for (String path : list) {
                                                Utils.getInstance().removeImage(path, context);
                                            }


                                            PowerPreference.getDefaultFile().putString(Constant.recycleList, new Gson().toJson(new ArrayList<String>()));
                                            Utils.getInstance().showSuccess(context, context.getResources().getString(R.string.hided_to_privacy));

                                            if (listener != null)
                                                listener.hideComplete();

                                            Utils.getInstance().getHiddenImages(context, null, null);

                                        } else {
                                            Utils.getInstance().clearRecycledImage(context);
                                        }

                                    }
                                }).onFail(new FailCallback() {
                                    @Override
                                    public void onFailed(Result result) {
                                        Utils.getInstance().clearRecycledImage(context);
                                        context.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                dismissLoader();
                                            }
                                        });
                                    }
                                });


                    } else {

                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dismissLoader();
                                Utils.getInstance().showSuccess(context,  context.getResources().getString(R.string.hided_to_privacy));

                                if (listener != null)
                                    listener.hideComplete();

                                Utils.getInstance().getHiddenImages(context, null, null);
                            }
                        });
                    }
                }
            }).start();
        } else {
            final Dialog dialogProgress = new Dialog(context, R.style.dialog);
            dialogProgress.setCancelable(false);

            dialogProgress.setContentView(R.layout.dialog_progress);
            dialogProgress.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            ProgressBar progress = dialogProgress.findViewById(R.id.progress);
            TextView txtPerc = dialogProgress.findViewById(R.id.txtPerc);
            TextView txtTotal = dialogProgress.findViewById(R.id.txtTotal);
            TextView txtCancelCopy = dialogProgress.findViewById(R.id.txtCancel);

            txtTotal.setText("0/" + list.size());


            HideTask hideTask = new HideTask(context, list, dialogProgress, progress, txtTotal, txtPerc, listener);
            hideTask.execute();

            txtCancelCopy.setOnClickListener(v15 -> {
                dialogProgress.dismiss();
                hideTask.cancel(true);
            });

            txtPerc.setText("0%");
            progress.setMax(list.size());

        }
    }


    public void removeImage(String path, Context context) {

        if (path.endsWith(".nomedia")) {

            ArrayList<String> datatemp = new ArrayList<>();
            datatemp.add(path);

            if (new File(path).exists()) {
                Utils.getInstance().deleteFile(context, path);
            }

            //  Database.getInstance(context).deleteFromHidden(datatemp);

            int index = MyApplication.HiddenImages.indexOf(path);
            int prev, post;

            if (MyApplication.HiddenImages.size() == 2) {
                MyApplication.HiddenImages.clear();
                MyApplication.HiddenImagesWithoutFolder.clear();
            } else {

                prev = index - 1;
                post = index + 1;

                if (prev > 0) {
                    if (post > MyApplication.HiddenImages.size() - 1) {
                        if (!MyApplication.HiddenImages.get(prev).contains(".Gallery")) {
                            MyApplication.HiddenImages.remove(prev);
                        }
                    } else {
                        if (!MyApplication.HiddenImages.get(prev).contains(".Gallery") && !MyApplication.HiddenImages.get(post).contains(".Gallery")) {
                            MyApplication.HiddenImages.remove(prev);
                        }
                    }

                } else if (prev == 0) {
                    if (!MyApplication.HiddenImages.get(post).contains(".Gallery")) {
                        MyApplication.HiddenImages.remove(0);
                    }
                }

                MyApplication.HiddenImages.remove(path);
                MyApplication.HiddenImagesWithoutFolder.remove(path);
            }

            if (HideImagesFragment.mAdapter != null)
                HideImagesFragment.mAdapter.notifyDataSetChanged();

            if (MyApplication.HiddenImages.size() > 0) {
                HideImagesFragment.stickyList.setVisibility(View.VISIBLE);
                HideImagesFragment.llNoDataFound.setVisibility(View.GONE);
            } else {
                HideImagesFragment.stickyList.setVisibility(View.GONE);
                HideImagesFragment.llNoDataFound.setVisibility(View.VISIBLE);
            }

        } else {
            int index = MyApplication.allimages.indexOf(path);

            int prev, post;

            if (index >= 0) {

                if (MyApplication.allimages.size() == 2) {

                    MyApplication.allimages.clear();
                    MyApplication.allimagesCopyWithoutDates.clear();
                } else {

                    prev = index - 1;
                    post = index + 1;

                    if (prev > 0) {
                        if (post > MyApplication.allimages.size() - 1) {
                            if (!MyApplication.allimages.get(prev).contains("storage")) {
                                MyApplication.allimages.remove(prev);
                            }
                        } else {
                            if (!MyApplication.allimages.get(prev).contains("storage") && !MyApplication.allimages.get(post).contains("storage")) {
                                MyApplication.allimages.remove(prev);
                            }
                        }

                    } else if (prev == 0) {
                        if (!MyApplication.allimages.get(post).contains("storage")) {
                            MyApplication.allimages.remove(0);
                        }
                    }

                    MyApplication.allimages.remove(path);
                    MyApplication.allimagesCopyWithoutDates.remove(path);
                }

                String foldername = new File(path).getParentFile().getName();

                if (MyApplication.folderData.containsKey(foldername)) {
                    ArrayList<String> data = new ArrayList<>();
                    data.addAll(MyApplication.folderData.get(foldername));

                    if (data.contains(path)) {
                        data.remove(path);
                    }

                    if (data.size() <= 0) {
                        MyApplication.folderData.remove(foldername);
                    } else {
                        MyApplication.folderData.put(foldername, data);
                    }

                    if (FolderFragment.adapter != null)
                        FolderFragment.adapter.addData(MyApplication.folderData);

                } else if (MyApplication.DefaultFolderData.containsKey(foldername)) {
                    ArrayList<String> data = new ArrayList<>();
                    data.addAll(MyApplication.DefaultFolderData.get(foldername));

                    if (data.contains(path)) {
                        data.remove(path);
                    }

                    if (data.size() <= 0) {
                        MyApplication.DefaultFolderData.remove(foldername);
                    } else {
                        MyApplication.DefaultFolderData.put(foldername, data);
                    }

                    if (FolderFragment.adapterDefault != null)
                        FolderFragment.adapterDefault.addData(MyApplication.DefaultFolderData);
                }
            }
        }
    }

    public void addFiles(String dest, Context context) {
        SimpleDateFormat timeStampFormat = new SimpleDateFormat("MMM dd,yyyy");
        String datestr = timeStampFormat.format(Calendar.getInstance().getTime());

        if (MyApplication.allimages.contains(datestr)) {
            MyApplication.allimages.add(MyApplication.allimages.indexOf(datestr) + 1, dest);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                MyApplication.allimagesCopyWithoutDates.clear();
                MyApplication.allimagesCopyWithoutDates.addAll(MyApplication.allimages);
                MyApplication.allimagesCopyWithoutDates.removeIf(s -> !s.contains("storage"));

            } else {
                MyApplication.allimagesCopyWithoutDates.add(dest);
            }

        } else {
            MyApplication.allimages.add(0, datestr);
            MyApplication.allimages.add(1, dest);

            MyApplication.allimagesCopyWithoutDates.add(0, dest);

        }

        String foldername = new File(dest).getParentFile().getName();

        if (MyApplication.folderData.containsKey(foldername)) {
            ArrayList<String> data = new ArrayList<>();
            data.addAll(MyApplication.folderData.get(foldername));
            data.add(dest);

            MyApplication.folderData.put(foldername, data);
        } else if (MyApplication.DefaultFolderData.containsKey(foldername)) {

            ArrayList<String> data = new ArrayList<>();
            data.addAll(MyApplication.DefaultFolderData.get(foldername));
            data.add(dest);

            MyApplication.DefaultFolderData.put(foldername, data);
        } else {

            ArrayList<String> data = new ArrayList<>();
            data.add(dest);
            MyApplication.folderData.put(foldername, data);
        }

        FolderFragment.adapter.addData(MyApplication.folderData);
        FolderFragment.adapterDefault.addData(MyApplication.DefaultFolderData);

        scanMedia(context, dest);
    }

    public void scanMedia(Context context) {

        MediaScannerConnection.scanFile(context,
                new String[]{getImageSaveDirectory()},
                null,
                (path, uri) -> {
                });
    }

    public void scanMedia(Context context, String destpath) {

        MediaScannerConnection.scanFile(context,
                new String[]{destpath},
                null,
                (path, uri) -> {
                });

        scanMedia(context);
    }

    public void createAlbumPopup(Context context, onDialogDone dialogDone) {

        final Dialog dialog = new Dialog(context, R.style.dialog);

        dialog.setContentView(R.layout.dialog_create_folder);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        EditText editname = dialog.findViewById(R.id.editname);
        TextView txtCancel = dialog.findViewById(R.id.txtCancel);
        TextView txtOk = dialog.findViewById(R.id.txtOk);

        txtOk.setOnClickListener(v12 -> {

            if (editname.getText() == null) {

                Utils.getInstance().showWarning(context, context.getResources().getString(R.string.enter_album_name));
                return;
            }

            if (editname.getText().toString().length() <= 0) {

                Utils.getInstance().showWarning(context, context.getResources().getString(R.string.enter_album_name));
                return;
            }

            File file = new File(Utils.getInstance().getImageSaveDirectory(), editname.getText().toString());

            if (file.exists()) {

                Utils.getInstance().showWarning(context, context.getResources().getString(R.string.album_exists));
                return;
            }

            editname.clearFocus();
            dialogDone.onDialogDone(editname.getText().toString());

            dialog.dismiss();
        });

        txtCancel.setOnClickListener(v -> {
            editname.clearFocus();
            dialog.dismiss();
        });
        dialog.show();
    }

    public void getHiddenImages(Activity activity, hidedone callback, Dialog dialog) {
        MyApplication.HiddenImages.clear();
        MyApplication.HiddenImagesWithoutFolder.clear();

        new Thread(() -> {

            File file = new File(Utils.getInstance().getHideSaveDirectory(activity));
            File[] files = file.listFiles();
            if (files != null) {

                HashMap<String, ArrayList<String>> hidden = new HashMap<>();

                for (File value : files) {
                    String folder = Database.getInstance(activity).getHiddenFileFoldername(value.getName());

                    if (folder == null) {
                        folder = String.valueOf(Constant.getOutputFolder());
                        folder = folder.substring(folder.lastIndexOf("/") + 1);
                    } else {
                        folder = new File(Database.getInstance(activity).getHiddenFileFoldername(value.getName())).getParentFile().getName();
                    }

                    ArrayList<String> data = new ArrayList<>();

                    if (hidden.containsKey(folder)) {
                        data.addAll(hidden.get(folder));
                    }

                    data.add(value.getAbsolutePath());
                    hidden.put(folder, data);
                }


                if (hidden.size() > 0) {
                    TreeMap<String, ArrayList<String>> sorted = new TreeMap<>(hidden);

                    Object[] keys = hidden.keySet().toArray();

                    for (Object key : keys) {

                        MyApplication.HiddenImages.add((String) key);
                        MyApplication.HiddenImages.addAll(sorted.get(key));
                        MyApplication.HiddenImagesWithoutFolder.addAll(sorted.get(key));
                    }
                }

                try {
                    synchronized (this) {
                        wait(1000);

                        activity.runOnUiThread(() -> {

                            if (dialog != null)
                                dialog.dismiss();

                            if (MyApplication.isEnteredPwd) {
                                if (HideImagesFragment.stickyList != null && HideImagesFragment.llNoDataFound != null) {

                                    if (HideImagesFragment.mAdapter != null)
                                        HideImagesFragment.mAdapter.notifyDataSetChanged();

                                    if (MyApplication.HiddenImages.size() > 0) {
                                        HideImagesFragment.stickyList.setVisibility(View.VISIBLE);
                                        HideImagesFragment.llNoDataFound.setVisibility(View.GONE);
                                    } else {
                                        HideImagesFragment.stickyList.setVisibility(View.GONE);
                                        HideImagesFragment.llNoDataFound.setVisibility(View.VISIBLE);
                                    }

                                }
                            }

                            if (callback != null)
                                callback.hideComplete();
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        }).start();
    }

    public void getBinImages(Activity activity) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MyApplication.binImages.clear();
                File file = new File(Utils.getInstance().getBinFolderPath(activity));
                File[] files = file.listFiles();
                if (files != null) {
                    for (File value : files) {
                        MyApplication.binImages.add(value.getAbsolutePath());
                    }
                }
            }
        }).start();
    }

    public void renameAlbumPopup(Activity context, String name, onDialogDone dialogDone) {

        if (SplashActivity.isDataLoaded) {
            final Dialog dialog = new Dialog(context, R.style.dialog);

            dialog.setContentView(R.layout.dialog_create_folder);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            EditText editname = dialog.findViewById(R.id.editname);
            TextView txtCancel = dialog.findViewById(R.id.txtCancel);
            TextView txtOk = dialog.findViewById(R.id.txtOk);

            editname.setText(name);

            txtOk.setOnClickListener(v12 -> {

                if (editname.getText() == null) {

                    Utils.getInstance().showWarning(context, context.getResources().getString(R.string.enter_album_name));
                    return;
                }

                if (editname.getText().toString().length() <= 0) {
                    Utils.getInstance().showWarning(context, context.getResources().getString(R.string.enter_album_name));
                    return;
                }

                File file = new File(Utils.getInstance().getImageSaveDirectory(), editname.getText().toString());

                if (file.exists()) {
                    Utils.getInstance().showWarning(context, context.getResources().getString(R.string.album_exists));
                    return;
                }

                editname.clearFocus();

                if (!editname.getText().toString().equals(name)) {
                    dialogDone.onDialogDone(editname.getText().toString());
                }
                dialog.dismiss();
            });

            txtCancel.setOnClickListener(v -> {
                editname.clearFocus();
                dialog.dismiss();
            });

            dialog.show();

        } else {
            Utils.getInstance().showImageLoadingDialog(context, () -> renameAlbumPopup(context, name, dialogDone));
        }
    }

    public void renameFilePopup(Activity context, String name, onDialogDone dialogDone) {

        if (SplashActivity.isDataLoaded) {
            final Dialog dialog = new Dialog(context, R.style.dialog);

            dialog.setContentView(R.layout.dialog_rename_file);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            EditText editname = dialog.findViewById(R.id.editname);
            TextView txtCancel = dialog.findViewById(R.id.txtCancel);
            TextView txtOk = dialog.findViewById(R.id.txtOk);

            editname.setText(name);

            txtOk.setOnClickListener(v12 -> {

                if (editname.getText() == null) {

                    Utils.getInstance().showWarning(context, context.getResources().getString(R.string.enter_file_name));
                    return;
                }

                if (editname.getText().toString().length() <= 0) {
                    Utils.getInstance().showWarning(context, context.getResources().getString(R.string.enter_file_name));
                    return;
                }

                File file = new File(Utils.getInstance().getImageSaveDirectory(), editname.getText().toString());

                if (file.exists()) {
                    Utils.getInstance().showWarning(context, context.getResources().getString(R.string.file_exixts));
                    return;
                }

                editname.clearFocus();

                if (!editname.getText().toString().equals(name)) {
                    dialogDone.onDialogDone(editname.getText().toString());
                }
                dialog.dismiss();
            });

            txtCancel.setOnClickListener(v -> {
                editname.clearFocus();
                dialog.dismiss();
            });

            dialog.show();

        } else {
            Utils.getInstance().showImageLoadingDialog(context, () -> renameAlbumPopup(context, name, dialogDone));
        }
    }

    public File copyImage(File file, Context context, String name) {
        String oldname = file.getName();
        String newName = "";

        String mimetype = MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath());
        if (!mimetype.startsWith("."))
            mimetype = "." + mimetype;
        newName = name + mimetype;

        File destin = new File(file.getParent(), newName);
        if (destin.exists()) {
            Toast.makeText(context, "filename already exists", Toast.LENGTH_SHORT).show();
            return null;
        } else {
            Utils.getInstance().copytoCache(context, file, destin);
            deleteFile(context, file.getAbsolutePath());
            scanMedia(context, file.getAbsolutePath());
            return destin;
        }
    }

    public void recycleImage(String path, Context context, boolean isCheck) {
        String oldname = new File(path).getName();
        String newName = "";

        File destin = new File(Utils.getInstance().getBinFolderPath(context), oldname.replaceAll(" ", ""));
        if (destin.exists()) {
            if (isImageType(path, context)) {
                newName = System.currentTimeMillis() + ".png";
                destin = new File(Utils.getInstance().getBinFolderPath(context), newName);
            } else {
                newName = System.currentTimeMillis() + ".mp4";
                destin = new File(Utils.getInstance().getBinFolderPath(context), newName);
            }
        } else {
            newName = oldname;
        }

        if (isCheck) {

            java.lang.reflect.Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            ArrayList<String> list = new Gson().fromJson(PowerPreference.getDefaultFile().getString(Constant.recycleList, new Gson().toJson(new ArrayList<String>())), type);

            list.add(destin.getAbsolutePath());
            PowerPreference.getDefaultFile().putString(Constant.recycleList, new Gson().toJson(list));
        }

        Utils.getInstance().copytoCache(context, new File(path), destin);
        deleteFile(context, path);

        Database.getInstance(context).addToBin(destin.getName(), path);
        MyApplication.binImages.add(destin.getAbsolutePath());
        scanMedia(context, path);
    }


    public void clearRecycledImage(Context context) {
        java.lang.reflect.Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        ArrayList<String> list = new Gson().fromJson(PowerPreference.getDefaultFile().getString(Constant.recycleList, new Gson().toJson(new ArrayList<String>())), type);

        for (int i = 0; i < list.size(); i++) {
            Utils.getInstance().deleteFile(context, list.get(i));
            MyApplication.binImages.remove(list.get(i));
        }

        Database.getInstance(context).deleteFromBin(list);

        list.clear();
        PowerPreference.getDefaultFile().putString(Constant.recycleList, new Gson().toJson(list));
    }

    public void deleteFile(Context context, String path) {
        new File(path).delete();

        int id = getMediaIdFromPath(path, context);
        Uri uri = Uri.withAppendedPath(MediaStore.Files.getContentUri("external"), String.valueOf(id));
        context.getContentResolver().delete(
                uri,
                null, null);
    }


    public Uri getAppendedUri(String path, Context context) {
        int id = getMediaIdFromPath(path, context);

        if (id == 0)
            return null;

        boolean isImage = true;
        if (path.endsWith(".nomedia")) {
            isImage = Utils.getInstance().isImageTypeForHidden(path);
        } else {
            isImage = Utils.getInstance().isImageType(path, context);
        }
        if (isImage)
            return Uri.withAppendedPath(MediaStore.Images.Media.getContentUri("external"), String.valueOf(id));
        else
            return Uri.withAppendedPath(MediaStore.Video.Media.getContentUri("external"), String.valueOf(id));
    }

    @SuppressLint("Range")
    public long getimageInfo(String path, Context context) {
        Cursor cursor = null;

        long datedadded;

        try {
            String[] proj = {MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DATE_ADDED};
            String sel = MediaStore.Files.FileColumns.DATA + "=?";

            cursor = context.getContentResolver().query(MediaStore.Files.getContentUri("external"), proj, sel, new String[]{path}, null);
            cursor.moveToFirst();

            datedadded = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED));
            return datedadded;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

    }

    public boolean isImageType(String path, Context context) {
        if (path.endsWith(".nomedia")) {
            return new File(path).getName().startsWith("file");
        } else {
            String mimeType = URLConnection.guessContentTypeFromName(path);
            return mimeType != null && mimeType.startsWith("image");
        }
      /*  if (path.contains("storage")) {
            Cursor cursor = null;

            int type;
            try {
                String[] proj = {MediaStore.Files.FileColumns.MEDIA_TYPE};
                String sel = MediaStore.Files.FileColumns.DATA + "=?";

                cursor = context.getContentResolver().query(MediaStore.Files.getContentUri("external"), proj, sel, new String[]{path}, null);
                cursor.moveToFirst();

                type = cursor.getInt(cursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE));

            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

            return type == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
        } else {
            return path.endsWith(".png") || path.endsWith(".jpg") || path.endsWith(".jpeg");
        }*/
    }

    public boolean isImageTypeForHidden(String path) {
        if (path.endsWith(".nomedia")) {
            return new File(path).getName().startsWith("file");
        } else {
            return path.endsWith(".png") || path.endsWith(".jpg") || path.endsWith(".jpeg") || isImageFile(path);
        }
    }

    public static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }

    public boolean isPasswordSetupDone(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("MY_PREFS", MODE_PRIVATE);
        return prefs.getBoolean("passcodeSetup", false);
    }

    public void setPasswordSetupDone(boolean isdone, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences("MY_PREFS", MODE_PRIVATE).edit();
        editor.putBoolean("passcodeSetup", isdone);
        editor.apply();
    }

    public void setpassword(Context context, String passcode) {
        SharedPreferences.Editor editor = context.getSharedPreferences("MY_PREFS", MODE_PRIVATE).edit();
        editor.putString("passcode", passcode);
        editor.apply();
    }

    public String getPassword(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("MY_PREFS", MODE_PRIVATE);
        return prefs.getString("passcode", "");

    }

    public void colorStatusBar(Activity activity) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }
    }

    public static void initializeRenderScript(final Context context) {
        rs = RenderScript.create(context);
    }

    public Bitmap brightness(final Bitmap bmp, float exposure) {

        Bitmap res = null;

        try {
            res = bmp.copy(bmp.getConfig(), true);

            if (exposure <= 0) exposure *= -exposure;
            exposure *= 2.55f;

            Allocation input = Allocation.createFromBitmap(rs, res);
            Allocation output = Allocation.createTyped(rs, input.getType());

            ScriptC_brightness script = new ScriptC_brightness(rs);

            script.invoke_setBright(exposure / 3);
            script.forEach_brightness(input, output);

            output.copyTo(res);
            RenderScriptTools.cleanRenderScript(script, input, output);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }

    public Bitmap contrastBurn(final Bitmap bmp, float level) {
        Bitmap res = null;
        try {
            level /= 100f;

            res = bmp.copy(bmp.getConfig(), true);

            Allocation input = Allocation.createFromBitmap(rs, res);
            Allocation output = Allocation.createTyped(rs, input.getType());

            ScriptC_burn script = new ScriptC_burn(rs);

            if (level < 0) {
                level /= 2f;
            } else {
                level *= 2f;
            }

            script.invoke_setBurnIntensity(level);

            script.forEach_burn(input, output);

            output.copyTo(res);
            RenderScriptTools.cleanRenderScript(script, input, output);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }

    public Bitmap saturation(final Bitmap bmp, float saturation) {
        Bitmap res = null;

        try {
            saturation /= 100f;

            res = bmp.copy(bmp.getConfig(), true);

            Allocation input = Allocation.createFromBitmap(rs, res);
            Allocation output = Allocation.createTyped(rs, input.getType());

            ScriptC_saturation script = new ScriptC_saturation(rs);

            script.set_saturationValue(saturation);

            script.forEach_saturation(input, output);

            output.copyTo(res);
            RenderScriptTools.cleanRenderScript(script, input, output);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return res;
    }

    public static Bitmap doSharpen(Bitmap original, float amount) {
        Bitmap bitmap = null;
        try {

            bitmap = Bitmap.createBitmap(
                    original.getWidth(), original.getHeight(),
                    Bitmap.Config.ARGB_8888);

            amount /= 150f;

            float[] kernel = {
                    0f, -amount, 0f,
                    -amount, 1f + 4f * amount, -amount,
                    0f, -amount, 0f
            };

            Allocation allocIn = Allocation.createFromBitmap(rs, original);
            Allocation allocOut = Allocation.createFromBitmap(rs, bitmap);

            ScriptIntrinsicConvolve3x3 convolution = ScriptIntrinsicConvolve3x3.create(rs, Element.U8_4(rs));
            convolution.setInput(allocIn);
            convolution.setCoefficients(kernel);
            convolution.forEach(allocOut);

            allocOut.copyTo(bitmap);
            rs.destroy();


        } catch (Exception e) {
            e.printStackTrace();
        }


        return bitmap;

    }

    public Bitmap sharpen(final Bitmap bmp, float amount) {

        amount /= 200f;

        float[] kernel = {
                0f, -amount, 0f,
                -amount, 1f + 4f * amount, -amount,
                0f, -amount, 0f
        };
        return RenderScriptTools.applyConvolution(bmp, rs, 3, 3, kernel);
    }

    public void unhideImage(String s, Context context) {

        ArrayList<String> data = new ArrayList<>();
        data.add(s);

        String dest = Database.getInstance(context).getHiddenFileFoldername(new File(s).getName());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            String filename = "";
            if (new File(s).getName().startsWith("file")) {
                filename = System.currentTimeMillis() + ".jpg";
            } else {
                filename = System.currentTimeMillis() + ".mp4";
            }
            dest = new File(Constant.getOutputFolder(), filename).getAbsolutePath();
        } else {
            if (dest == null) {
                String filename = "";
                if (new File(s).getName().startsWith("file")) {
                    filename = System.currentTimeMillis() + ".jpg";
                } else {
                    filename = System.currentTimeMillis() + ".mp4";
                }
                dest = new File(Constant.getOutputFolder(), filename).getAbsolutePath();
            }
        }


        File srcfile = new File(s);
        File destfile = new File(dest);

        Utils.getInstance().copyImageFile(context, srcfile, destfile);
        Utils.getInstance().deleteFile(context, srcfile.getAbsolutePath());

        int index = MyApplication.HiddenImages.indexOf(s);

        if (MyApplication.HiddenImages.size() > index + 1) {
            if (index - 1 >= 0 && !MyApplication.HiddenImages.get(index - 1).contains(".Gallery") && !MyApplication.HiddenImages.get(index + 1).contains(".Gallery")) {
                MyApplication.HiddenImages.remove(index - 1);
            }
        } else {
            if (index - 1 >= 0 && !MyApplication.HiddenImages.get(index - 1).contains(".Gallery")) {
                MyApplication.HiddenImages.remove(index - 1);
            }
        }

        MyApplication.HiddenImages.remove(s);
        MyApplication.HiddenImagesWithoutFolder.remove(s);

        Utils.getInstance().addFiles(destfile.getAbsolutePath(), context);
    }

    public void setSecurityQues(Context context, String que) {
        SharedPreferences.Editor editor = context.getSharedPreferences("MY_PREFS", MODE_PRIVATE).edit();
        editor.putString("securityQue", que);
        editor.apply();

    }

    public String getSecurityQues(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("MY_PREFS", MODE_PRIVATE);
        return prefs.getString("securityQue", "");

    }

    public void setSecurityQuesInt(Context context, int que) {
        SharedPreferences.Editor editor = context.getSharedPreferences("MY_PREFS", MODE_PRIVATE).edit();
        editor.putInt("securityQueInt", que);
        editor.apply();

    }

    public int getSecurityQuesInt(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("MY_PREFS", MODE_PRIVATE);
        return prefs.getInt("securityQueInt", 0);

    }

    public void setSecurityAns(Context context, String Ans) {
        SharedPreferences.Editor editor = context.getSharedPreferences("MY_PREFS", MODE_PRIVATE).edit();
        editor.putString("securityAns", Ans);
        editor.apply();

    }

    public String getSecurityAns(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("MY_PREFS", MODE_PRIVATE);
        return prefs.getString("securityAns", "");

    }

    public boolean isConnected(Context mActivity) {
        ConnectivityManager cm = (ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public String getdeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public String getvideoDurationForhidden(Context context, String s) {

        Cursor cursor = null;

        long dur = 0;

        try {
            String[] proj = {MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DURATION};
            String sel = MediaStore.Files.FileColumns.DATA + "=?";

            cursor = context.getContentResolver().query(MediaStore.Files.getContentUri("external"), proj, sel, new String[]{s}, null);
            if (cursor.moveToFirst()) {
                dur = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.DURATION));
            }


        } catch (Exception e) {
            e.printStackTrace();
            return "00:00";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return convertMillieToHMmSs(getDuration(context, s));
    }

    public long getDuration(Context context, String filepath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        String time = "";
        try {
            retriever.setDataSource(context, Uri.parse(filepath));
            time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

        return Long.parseLong(time);
    }

/*    public String getvideoDuration(Context context, String s) {

        MediaPlayer player = MediaPlayer.create(context, Uri.fromFile(new File(s)));
        if (player != null)
            return convertMillieToHMmSs(player.getDuration());
        else
            return "00:00";
    }*/

    public static String convertMillieToHMmSs(long millie) {

        long seconds = (millie / 1000);
        long second = seconds % 60;
        long minute = (seconds / 60) % 60;
        long hour = (seconds / (60 * 60)) % 24;

        String result = "";
        if (hour > 0) {
            return String.format("%02d:%02d:%02d", hour, minute, second);
        } else {
            return String.format("%02d:%02d", minute, second);
        }

    }

    public interface hidedone {
        void hideComplete();
    }


    public void hideImage(String path, Context context) {

        File src = new File(path);
        String filename;

        if (isImageType(path, context)) {
            filename = "file" + Calendar.getInstance().getTime().getTime() + ".nomedia";
        } else
            filename = Calendar.getInstance().getTime().getTime() + ".nomedia";

        File dest = new File(getHideSaveDirectory(context), filename);

        java.lang.reflect.Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        ArrayList<String> list = new Gson().fromJson(PowerPreference.getDefaultFile().getString(Constant.recycleList, new Gson().toJson(new ArrayList<String>())), type);

        list.add(dest.getAbsolutePath());
        PowerPreference.getDefaultFile().putString(Constant.recycleList, new Gson().toJson(list));

        Utils.getInstance().copytoCache(context, new File(path), dest);
        deleteFile(context, path);

        Database.getInstance(context).addToHideen(filename, src.getAbsolutePath());
        Utils.getInstance().scanMedia(context);

    }

    private class HideTask extends AsyncTask<Void, Void, Void> {

        private ArrayList<String> data;
        private Dialog dialog;
        private ProgressBar progressBar;
        private TextView total;
        private Activity ctx;
        private TextView perc;
        private hidedone callback;

        public HideTask(Activity context, ArrayList<String> list, Dialog dialogProgress, ProgressBar progress, TextView txtTotal, TextView txtPerc, hidedone listener) {
            data = list;
            this.dialog = dialogProgress;
            this.progressBar = progress;
            this.total = txtTotal;
            this.perc = txtPerc;
            this.ctx = context;
            callback = listener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ctx.runOnUiThread(() -> dialog.show());
        }

        @Override
        protected Void doInBackground(Void... voids) {

            for (int i = 0; i < data.size(); i++) {

                File src = new File(data.get(i));
                String filename;

                if (isImageType(data.get(i), ctx)) {
                    filename = "file" + Calendar.getInstance().getTime().getTime() + ".nomedia";
                } else
                    filename = Calendar.getInstance().getTime().getTime() + ".nomedia";

                File dest = new File(getHideSaveDirectory(ctx), filename);

                copyImageFile(ctx, src, dest);
                removeImage(data.get(i), ctx);

                Database.getInstance(ctx).addToHideen(filename, src.getAbsolutePath());
                Utils.getInstance().deleteFile(ctx, src.getAbsolutePath());

                int temp = ((i + 1) * 100);
                temp = temp / data.size();

                int finalI = i;
                int finalTemp = temp;

                ctx.runOnUiThread(() -> {

                    progressBar.setProgress(finalTemp);
                    perc.setText(finalTemp + "%");
                    total.setText(finalI + 1 + "/" + data.size());

                    if (finalI == data.size() - 1) {

                        Utils.getInstance().scanMedia(ctx);

                        Utils.getInstance().showSuccess(ctx,  ctx.getResources().getString(R.string.hided_to_privacy));

                        if (callback != null)
                            callback.hideComplete();
                    }

                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            Utils.getInstance().getHiddenImages(ctx, null, dialog);
        }
    }

    public boolean isHiddenApiDone(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("MY_PREFS", MODE_PRIVATE);
        return prefs.getBoolean("hiddenApi", false);
    }

    public void setHiddenApiDone(boolean isdone, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences("MY_PREFS", MODE_PRIVATE).edit();
        editor.putBoolean("hiddenApi", isdone);
        editor.apply();
    }

    public void showImageLoadingDialog(Activity activity, imageLoadingDone listener) {
        Dialog dialog = new Dialog(activity, R.style.dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        dialog.setContentView(R.layout.dialog_image_loading_wait);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        TextView txtPerc = dialog.findViewById(R.id.txtPerc);
        TextView txtCurrent = dialog.findViewById(R.id.txtCurrent);
        TextView txtTotal = dialog.findViewById(R.id.txtTotal);
        ProgressBar progress = dialog.findViewById(R.id.progress);

        tot = 0;
        Cursor cursor = null;
        try {
            cursor = Utils.getInstance().getAllImagesVideoFromStorageForDate(activity);
            tot = cursor.getCount();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }

        txtTotal.setText(tot + "");

        if (tot > 0) {
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {

                    if (!SplashActivity.isDataLoaded)
                        handler.postDelayed(this, 100);
                    else {
                        handler.removeCallbacks(this);
                        listener.onImageLoaded();
                        dialog.dismiss();
                    }

                    int pro = SplashActivity.count * 100;


                    pro = pro / tot;

                    txtPerc.setText(pro + "%");

                    progress.setProgress(pro);
                    txtCurrent.setText(SplashActivity.count + "");
                }
            };

            handler.postDelayed(runnable, 100);
        } else {
            txtPerc.setText(100 + "%");

            progress.setProgress(100);
            txtCurrent.setText(SplashActivity.count + "");
        }

        dialog.show();
    }

    public interface imageLoadingDone {
        void onImageLoaded();
    }

}
