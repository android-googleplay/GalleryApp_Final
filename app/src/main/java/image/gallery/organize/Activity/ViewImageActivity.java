package image.gallery.organize.Activity;

import static image.gallery.organize.MyApplication.DefaultFolderData;
import static image.gallery.organize.MyApplication.HiddenImages;
import static image.gallery.organize.MyApplication.HiddenImagesWithoutFolder;
import static image.gallery.organize.MyApplication.allimages;
import static image.gallery.organize.MyApplication.allimagesCopyWithoutDates;
import static image.gallery.organize.MyApplication.folderData;
import static image.gallery.organize.MyApplication.isEnteredPwd;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import image.gallery.organize.Adhelper.InterAds;
import image.gallery.organize.Adhelper.ListBannerAds;
import image.gallery.organize.Fragment.AllPhotosFragment;
import image.gallery.organize.Fragment.FolderFragment;
import image.gallery.organize.Fragment.HideImagesFragment;
import image.gallery.organize.Helper.Constant;
import image.gallery.organize.Helper.CustomVideoView;
import image.gallery.organize.Helper.Database;
import image.gallery.organize.Helper.Utils;
import image.gallery.organize.MyApplication;
import image.gallery.organize.R;
import com.github.florent37.inlineactivityresult.InlineActivityResult;
import com.github.florent37.inlineactivityresult.Result;
import com.github.florent37.inlineactivityresult.callbacks.FailCallback;
import com.github.florent37.inlineactivityresult.callbacks.SuccessCallback;
import com.github.florent37.inlineactivityresult.request.Request;
import com.github.florent37.inlineactivityresult.request.RequestFabric;
import com.google.gson.Gson;
import com.jsibbold.zoomage.ZoomageView;
import com.preference.PowerPreference;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ViewImageActivity extends AppCompatActivity {

    MediaMetadataRetriever mediaMetadataRetriever;
    private int angle = 0;
    private Bitmap bm;
    public static Activity activity;
    private String path;

    Dialog mLoadingDialog;
    private long date;
    private boolean isFav = false;
    private boolean isFromRotate;
    private boolean isImage = true;
    private ZoomageView imgPreview;
    private ArrayList<String> data = new ArrayList<>();
    private boolean ispagescrolled = false;
    private boolean isstart = true;
    int pos = 0;
    private ViewPager pager;
    ImageView imgFav;
    LinearLayout llEdit;
    TextView txttitle;

    File from, to;
    // type

    // 1 - all photos
    // 2 - Hidden images
    // 3 - folder images

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;
        setContentView(R.layout.activity_view_image);
        mediaMetadataRetriever = new MediaMetadataRetriever();

        pos = getIntent().getIntExtra("position", 0);
        if (getIntent().getIntExtra("type", 0) == 1) {
            data.addAll(allimagesCopyWithoutDates);
        } else if (getIntent().getIntExtra("type", 0) == 2) {
            data.addAll(HiddenImagesWithoutFolder);
        } else if (getIntent().getIntExtra("type", 0) == 3) {
            data.addAll(MyApplication.dataArr);
            ArrayList<String> dataarr = new ArrayList<>();
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).contains("storage"))
                    dataarr.add(data.get(i));
            }
            data.clear();
            data.addAll(dataarr);
        }

        setview();
    }

    public void refresh() {
        data.clear();
        pos = getIntent().getIntExtra("position", 0);
        if (getIntent().getIntExtra("type", 0) == 1) {
            data.addAll(allimagesCopyWithoutDates);
        } else if (getIntent().getIntExtra("type", 0) == 2) {
            data.addAll(HiddenImagesWithoutFolder);
        } else if (getIntent().getIntExtra("type", 0) == 3) {
            data.addAll(MyApplication.dataArr);
            ArrayList<String> dataarr = new ArrayList<>();
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).contains("storage"))
                    dataarr.add(data.get(i));
            }
            data.clear();
            data.addAll(dataarr);
        }
    }


    private void setview() {

        findViewById(R.id.imgBack).setOnClickListener(view -> onBackPressed());

        pager = findViewById(R.id.pager);

        ImageView imgDetails = findViewById(R.id.imgDetails);

        LinearLayout llShare = findViewById(R.id.llShare);
        LinearLayout llHide = findViewById(R.id.llHide);
        imgFav = findViewById(R.id.imgFav);
        llEdit = findViewById(R.id.llEdit);
        LinearLayout llDelete = findViewById(R.id.llDelete);
        LinearLayout llMore = findViewById(R.id.llMore);


        txttitle = findViewById(R.id.txttitle);

        path = getIntent().getStringExtra("path");

        if (path.endsWith(".nomedia")) {
            llDelete.setVisibility(View.GONE);
            llMore.setVisibility(View.GONE);
            llEdit.setVisibility(View.GONE);
        }

        imgFav.setVisibility(View.GONE);

        pager.setOffscreenPageLimit(0);
        pager.setSaveFromParentEnabled(false);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (!isstart) {
                    ispagescrolled = true;
                } else {
                    isstart = false;

                }
            }

            @Override
            public void onPageSelected(int position) {

                pos = position;
                try {
                    if (position >= data.size()) {
                        position = position - 1;
                    }

                    ispagescrolled = false;
                    path = data.get(position);

                    if (path.endsWith(".nomedia")) {
                        llDelete.setVisibility(View.GONE);
                        llMore.setVisibility(View.GONE);
                        llEdit.setVisibility(View.GONE);
                    }


                    isFav = Database.getInstance(ViewImageActivity.this).isAddedToFav(path);

                    if (isFav) {
                        imgFav.setImageResource(R.drawable.ic_favourite_red);
                    } else {
                        imgFav.setImageResource(R.drawable.ic_fav_gray);
                    }

                    if (path.endsWith(".nomedia")) {
                        imgFav.setVisibility(View.GONE);

                        date = new File(path).getAbsoluteFile().lastModified();
                        isImage = Utils.getInstance().isImageTypeForHidden(path);
                    } else {
                        if (path.contains("storage")) {
                            date = Utils.getInstance().getimageInfo(path, ViewImageActivity.this);
                            imgFav.setVisibility(View.VISIBLE);
                        }

                        isImage = Utils.getInstance().isImageType(path, ViewImageActivity.this);
                    }

                    if (isImage) {
                        new Thread(() -> bm = getImageExIF(path)).start();

                        llEdit.setVisibility(View.VISIBLE);
                    } else {
                        llEdit.setVisibility(View.GONE);
                    }

                    /*if (date > 0) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(date);

                        if (calendar.get(Calendar.YEAR) == 1970) {
                            calendar.setTimeInMillis(date * 1000);
                        }

                        SimpleDateFormat timeStampFormat = new SimpleDateFormat("MMM dd,yyyy");

                        String datestr = timeStampFormat.format(calendar.getTime());
                        if (datestr.equals(getTodayDate(true))) {
                            txttitle.setText("Today");
                        } else if (datestr.equals(getTodayDate(false))) {
                            txttitle.setText("Yesterday");
                        } else {
                            try {
                                String dates = datestr;
                                SimpleDateFormat spf = new SimpleDateFormat("MMM dd,yyyy");
                                Date newDate = spf.parse(dates);
                                spf = new SimpleDateFormat("dd MMMM yyyy");
                                dates = spf.format(newDate);
                                txttitle.setText(dates);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    } else {
                        txttitle.setText("Photos");
                    }
*/

                    txttitle.setText(new File(path).getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

                ispagescrolled = false;
            }
        });

        PagerAdapter adapter = new PagerAdapter() {

            @Override
            public int getCount() {
                return data.size();
            }

            @NonNull
            @NotNull
            @Override
            public Object instantiateItem(@NonNull @NotNull ViewGroup container, int position) {

                View inflate = LayoutInflater.from(ViewImageActivity.this).inflate(R.layout.item_view_photo, container, false);
                try {

                    CustomVideoView videoview;

                    String path = "";
                    File filesrc;
                    boolean isImage;

                    if (position >= data.size()) {
                        position = position - 1;
                    }

                    path = data.get(position);


                    filesrc = new File(path);

                    if (path.endsWith(".nomedia")) {
                        imgFav.setVisibility(View.GONE);
                        isImage = Utils.getInstance().isImageTypeForHidden(path);
                    } else {
                        isImage = Utils.getInstance().isImageType(path, ViewImageActivity.this);

                        if (path.contains("storage"))
                            imgFav.setVisibility(View.VISIBLE);
                    }

                    if (position == 0) {

                        isFav = Database.getInstance(ViewImageActivity.this).isAddedToFav(path);

                        if (isFav) {
                            imgFav.setImageResource(R.drawable.ic_favourite_red);
                        } else {
                            imgFav.setImageResource(R.drawable.ic_favourite_gray);
                        }
                    }

                    videoview = inflate.findViewById(R.id.videoview);
                    imgPreview = inflate.findViewById(R.id.imgPreview);
                    RelativeLayout relVideoView = inflate.findViewById(R.id.relVideoView);

                    if (isImage) {

                        relVideoView.setVisibility(View.GONE);
                        imgPreview.setVisibility(View.VISIBLE);

                        if (!isFromRotate) {
                            Glide.with(ViewImageActivity.this).load(path).into(imgPreview);

                        } else {
                            isFromRotate = false;
                            Glide.with(ViewImageActivity.this).load(bm).into(imgPreview);
                        }


                    } else {

                        relVideoView.setVisibility(View.VISIBLE);
                        imgPreview.setVisibility(View.GONE);

                        ImageView imgPlay = inflate.findViewById(R.id.imgPlay);

                        Handler handler = new Handler();

                        MediaController mediaController = new MediaController(ViewImageActivity.this);
                        mediaController.setAnchorView(videoview);

                        videoview.setMediaController(null);

                        try {
                            mediaMetadataRetriever.setDataSource(path);
                            if (mediaMetadataRetriever.getFrameAtTime() != null) {
                                videoview.setVideoURI(Uri.fromFile(filesrc));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (mediaMetadataRetriever != null)
                                mediaMetadataRetriever.release();
                            mediaMetadataRetriever = new MediaMetadataRetriever();
                        }


                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {

                                if (ispagescrolled) {
                                    if (videoview != null && videoview.isPlaying())
                                        videoview.pause();

                                    if (mediaController != null && mediaController.isShowing())
                                        mediaController.hide();

                                } else {
                                    handler.removeCallbacks(this);
                                }

                                if (videoview != null && videoview.isPlaying())
                                    handler.postDelayed(this, 100);
                            }
                        };


                        videoview.setPlayPauseListener(new CustomVideoView.PlayPauseListener() {
                            @Override

                            public void onPlay() {
                                imgPlay.setVisibility(View.GONE);
                                handler.postDelayed(runnable, 100);
                            }

                            @Override
                            public void onPause() {
                                imgPlay.setVisibility(View.VISIBLE);
                            }
                        });

                        videoview.setOnCompletionListener(mp -> imgPlay.setVisibility(View.VISIBLE));

                        videoview.setOnPreparedListener(mp -> {
                            videoview.start();

                            if (mediaController.isShowing())
                                mediaController.hide();

                            new Handler().postDelayed(() -> videoview.pause(), 5);

                        });

                        imgPlay.setOnClickListener(v -> {
                            try {
                                videoview.setMediaController(mediaController);
                                videoview.start();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                container.addView(inflate);
                return inflate;
            }

            @Override
            public void destroyItem(@NotNull ViewGroup collection, int position, @NotNull Object view) {
                collection.removeView((View) view);
            }

            @Override
            public boolean isViewFromObject(@NotNull View view, @NotNull Object object) {
                return (view == object);
            }

            @Override
            public int getItemPosition(@NotNull Object object) {
                return PagerAdapter.POSITION_NONE;
            }

        };

        pager.setAdapter(adapter);
        if (data.size() > 1) {
            pager.setCurrentItem(data.indexOf(path));
        }

        if (path.endsWith(".nomedia")) {
            imgFav.setVisibility(View.GONE);

            date = new File(path).getAbsoluteFile().lastModified();
            isImage = Utils.getInstance().isImageTypeForHidden(path);
        } else {
            if (path.contains("storage")) {
                date = Utils.getInstance().getimageInfo(path, ViewImageActivity.this);
                imgFav.setVisibility(View.VISIBLE);
            }

            isImage = Utils.getInstance().isImageType(path, ViewImageActivity.this);
        }

        if (isImage) {
            new Thread(() -> bm = getImageExIF(path)).start();
            llEdit.setVisibility(View.VISIBLE);
        } else {
            llEdit.setVisibility(View.GONE);
        }

        txttitle.setText(new File(path).getName());

        imgDetails.setOnClickListener(view -> {
            new InterAds().showInterAds(ViewImageActivity.this, new InterAds.OnAdClosedListener() {
                @Override
                public void onAdClosed() {
                    startActivity(new Intent(ViewImageActivity.this, ShowDetailsActivity.class).putExtra("path", path));
                }
            });
        });

        imgFav.setOnClickListener(view -> {
            Database.getInstance(this).addToFavourite(path, !isFav);

            isFav = Database.getInstance(ViewImageActivity.this).isAddedToFav(path);

            if (isFav) {
                imgFav.setImageResource(R.drawable.ic_favourite_red);
            } else {
                imgFav.setImageResource(R.drawable.ic_favourite_gray);
            }
        });

        if (getIntent().hasExtra("isFromHidden")) {

            ImageView imgUnlock = findViewById(R.id.imglock);
            TextView txtlock = findViewById(R.id.txtlock);

            txtlock.setText("Unhide");
            imgUnlock.setImageResource(R.drawable.ic_unlock);
        }

        llShare.setOnClickListener(view -> {

            try {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                if (isImage)
                    shareIntent.setType("image/*");
                else
                    shareIntent.setType("video/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(ViewImageActivity.this, getPackageName() + ".provider", new File(path)));

                startActivity(Intent.createChooser(shareIntent, "Share Images Using"));

            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        llHide.setOnClickListener(v -> {

            if (SplashActivity.isDataLoaded) {
                hideunhide(adapter);
            } else {
                Utils.getInstance().showImageLoadingDialog(ViewImageActivity.this, () -> hideunhide(adapter));
            }
        });

        llEdit.setOnClickListener(v -> {
            new InterAds().showInterAds(ViewImageActivity.this, new InterAds.OnAdClosedListener() {
                @Override
                public void onAdClosed() {
                    startActivity(new Intent(ViewImageActivity.this, EditImageActivity.class).putExtra("path", path));
                }
            });
        });

        llDelete.setOnClickListener(view -> {
            if (SplashActivity.isDataLoaded) {
                showLoader(ViewImageActivity.this);
                delete(adapter);
            } else {
                Utils.getInstance().showImageLoadingDialog(ViewImageActivity.this, () -> delete(adapter));
            }
        });

        llMore.setOnClickListener(view -> {
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            View popupView = layoutInflater.inflate(R.layout.menu_more_view_image, null);

            PopupWindow infoPopup = new PopupWindow(popupView, (int) getResources().getDimension(R.dimen._130sdp), ViewGroup.LayoutParams.WRAP_CONTENT);
            infoPopup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            infoPopup.setOutsideTouchable(true);

            TextView txtAddtoAlbum = popupView.findViewById(R.id.txtAddtoAlbum);
            RelativeLayout relRotate = popupView.findViewById(R.id.relRotate);
            TextView txtRename = popupView.findViewById(R.id.txtRename);
            TextView txtSetas = popupView.findViewById(R.id.txtSetas);

            if (isImage) {
                relRotate.setVisibility(View.VISIBLE);
                txtSetas.setVisibility(View.VISIBLE);
            } else {
                relRotate.setVisibility(View.GONE);
                txtSetas.setVisibility(View.GONE);
            }

            txtAddtoAlbum.setOnClickListener(view1 -> {
                infoPopup.dismiss();

                ArrayList<String> selectedItems = new ArrayList<>();
                selectedItems.add(path);

                new InterAds().showInterAds(ViewImageActivity.this, new InterAds.OnAdClosedListener() {
                    @Override
                    public void onAdClosed() {
                        startActivity(new Intent(ViewImageActivity.this, AddtoAlbumActivity.class).putExtra("selected", selectedItems).putExtra("isFromView", true));

                    }
                });
            });

            txtRename.setOnClickListener(view12 -> {
                infoPopup.dismiss();

                File filesrc = new File(path);
                String oldName = filesrc.getName();
                Utils.getInstance().renameFilePopup(this, filesrc.getName().substring(0, filesrc.getName().lastIndexOf(".")), name -> {
                    imageRename(filesrc.getAbsolutePath(), name);
                });
            });

            txtSetas.setOnClickListener(v -> {
                infoPopup.dismiss();
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());

                Intent setAs = new Intent(Intent.ACTION_ATTACH_DATA);
                setAs.addCategory(Intent.CATEGORY_DEFAULT);
                Uri sourceUri = Uri.fromFile(new File(path));
                setAs.setDataAndType(sourceUri, "image/*");
                setAs.putExtra("mimeType", "image/*");
                setAs.putExtra("save_path", sourceUri);
                startActivity(Intent.createChooser(setAs, "Select service:"));
            });

            relRotate.setOnClickListener(view13 -> {
                infoPopup.dismiss();

                LayoutInflater layoutInflater1 = LayoutInflater.from(this);
                View popupView1 = layoutInflater1.inflate(R.layout.menu_rotate, null);

                PopupWindow infoPopup1 = new PopupWindow(popupView1, (int) getResources().getDimension(R.dimen._130sdp), ViewGroup.LayoutParams.WRAP_CONTENT);
                infoPopup1.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                infoPopup1.setOutsideTouchable(true);

                TextView txtRotateLeft = popupView1.findViewById(R.id.txtRotateLeft);
                TextView txtRotateRight = popupView1.findViewById(R.id.txtRotateRight);
                TextView txtFlip = popupView1.findViewById(R.id.txFlip);

                txtFlip.setOnClickListener(view14 -> {

                    isFromRotate = true;
                    infoPopup1.dismiss();

                    flipBitmap(bm);
                    adapter.notifyDataSetChanged();


                    Utils.getInstance().saveBitmapToStorage(bm, new File(path));

                    isFromRotate = false;

                });

                txtRotateLeft.setOnClickListener(view16 -> {
                    isFromRotate = true;

                    infoPopup1.dismiss();

                    rotateBitmap(bm, angle - 90);
                    adapter.notifyDataSetChanged();

                    Utils.getInstance().saveBitmapToStorage(bm, new File(path));

                    isFromRotate = false;
                });

                txtRotateRight.setOnClickListener(view15 -> {
                    isFromRotate = true;

                    infoPopup1.dismiss();
                    rotateBitmap(bm, angle + 90);
                    adapter.notifyDataSetChanged();

                    Utils.getInstance().saveBitmapToStorage(bm, new File(path));

                    isFromRotate = false;

                });

                Point size = new Point();
                getWindowManager().getDefaultDisplay().getSize(size);
                infoPopup1.showAtLocation(view, Gravity.BOTTOM, size.x - view.getWidth(), view.getBottom());

            });

            Point size = new Point();
            getWindowManager().getDefaultDisplay().getSize(size);
            infoPopup.showAtLocation(view, Gravity.BOTTOM, size.x - view.getWidth(), view.getBottom());
        });
    }

    private void imageRename(String originalImagePath, String newImageNameWithoutExtension) {
        from = new File(originalImagePath);
        String parentPath = from.getParent();
        String name = null;
        String extension = null;
        if (originalImagePath.indexOf(".") > 0) {
            name = from.getName().substring(0, from.getName().lastIndexOf("."));
            extension = from.getName().substring(from.getName().lastIndexOf("."));
        }

        String finalExtension = extension;

        String renamePath = parentPath + "/" + newImageNameWithoutExtension + finalExtension;
        to = new File(renamePath);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ArrayList<Uri> list = new ArrayList<>();

            Uri uri = getUriFromPath(from.getAbsolutePath(), getApplicationContext(), isImage);
            if (uri == null)
                return;

            list.add(uri);
            PendingIntent intent = MediaStore.createWriteRequest(getContentResolver(), list);
            Request request = RequestFabric.create(intent.getIntentSender(), null, 0, 0, 0, null);
            new InlineActivityResult(this)
                    .startForResult(request)
                    .onSuccess(new SuccessCallback() {
                        @Override
                        public void onSuccess(Result result) {
                            if (result.getResultCode() == RESULT_OK) {
                                Uri uri;

                                uri = getUriFromPath(from.getAbsolutePath(), getApplicationContext(), isImage);
                                if (uri == null)
                                    return;
                                ContentValues values = new ContentValues();

                                if (isImage) {

                                    values.put(MediaStore.Images.Media.IS_PENDING, 1);
                                    getContentResolver().update(uri, values, null, null);

                                    values.put(MediaStore.Images.Media.IS_PENDING, 0);
                                    values.put(MediaStore.Images.Media.DISPLAY_NAME, to.getName());

                                    getContentResolver().update(uri, values, null, null);
                                    getContentResolver().notifyChange(uri, null);

                                } else {

                                    values.put(MediaStore.Video.Media.IS_PENDING, 1);
                                    getContentResolver().update(uri, values, null, null);

                                    values.put(MediaStore.Video.Media.IS_PENDING, 0);
                                    values.put(MediaStore.Video.Media.DISPLAY_NAME, to.getName());

                                    getContentResolver().update(uri, values, null, null);
                                    getContentResolver().notifyChange(uri, null);

                                }

                                renameComplete();

                                MediaScannerConnection.scanFile(ViewImageActivity.this, new String[]{from.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                                    public void onScanCompleted(String path, Uri uri) {
                                    }
                                });
                            }
                        }
                    }).onFail(new FailCallback() {
                        @Override
                        public void onFailed(Result result) {

                        }
                    });


        } else {
            if (from.exists()) {
                boolean isCheck = from.renameTo(to);
                if (isCheck) {
                    renameComplete();
                }
            }
        }
    }


    public Uri getUriFromPath(String pathMain, Context context, boolean isImage) {
        Uri mainUri = null;

        Cursor cursor = null;
        try {
            if (isImage) {
                cursor = context.getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Images.Media._ID},
                        MediaStore.Images.Media.DATA + "=? ",
                        new String[]{pathMain}, null);


                if (cursor != null && cursor.moveToFirst()) {
                    @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                    mainUri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
                }

            } else {
                cursor = context.getContentResolver().query(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Video.Media._ID},
                        MediaStore.Video.Media.DATA + "=? ",
                        new String[]{pathMain}, null);

                if (cursor != null && cursor.moveToFirst()) {
                    @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                    mainUri = Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "" + id);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return mainUri;

    }

    public void renameComplete() {
        Utils.getInstance().scanMedia(ViewImageActivity.this, to.getAbsolutePath());

        String foldername = to.getParentFile().getName();

        if (folderData.containsKey(foldername)) {
            ArrayList<String> data = new ArrayList<>();
            data.addAll(folderData.get(foldername));

            if (data.contains(path)) {
                data.set(data.indexOf(path), to.getAbsolutePath());
            }

            folderData.put(foldername, data);
        } else if (DefaultFolderData.containsKey(foldername)) {
            ArrayList<String> data = new ArrayList<>();
            data.addAll(DefaultFolderData.get(foldername));
            if (data.contains(path)) {
                data.set(data.indexOf(path), to.getAbsolutePath());
            }
            DefaultFolderData.put(foldername, data);
        }

        if (allimages.contains(path))
            allimages.set(allimages.indexOf(path), to.getAbsolutePath());

        if (allimagesCopyWithoutDates.contains(path))
            allimagesCopyWithoutDates.set(allimagesCopyWithoutDates.indexOf(path), to.getAbsolutePath());

        data.set(data.indexOf(path), to.getAbsolutePath());

        AllPhotosFragment.mAdapter.adddata(allimages);
        AllPhotosFragment.mAdapter.notifyDataSetChanged();

        FolderFragment.adapterDefault.addData(DefaultFolderData);
        FolderFragment.adapter.addData(folderData);

        path = to.getAbsolutePath();
        txttitle.setText(new File(path).getName());
    }


    private void hideunhide(PagerAdapter adapter) {
        if (getIntent().hasExtra("isFromHidden")) {

            Utils.getInstance().unhideImage(path, ViewImageActivity.this);
            Utils.getInstance().showSuccess(ViewImageActivity.this, getResources().getString(R.string.unhided_from_privacy));

            finish();

            if (isEnteredPwd) {
                if (HiddenImages.size() > 0) {
                    HideImagesFragment.stickyList.setVisibility(View.VISIBLE);
                    HideImagesFragment.llNoDataFound.setVisibility(View.GONE);
                } else {
                    HideImagesFragment.stickyList.setVisibility(View.GONE);
                    HideImagesFragment.llNoDataFound.setVisibility(View.VISIBLE);
                }
            }
        } else {
            ArrayList<String> dataq = new ArrayList<>();
            dataq.add(path);

            Utils.getInstance().hidePhotos(dataq, this, false, () -> runOnUiThread(() -> {

                data.remove(path);
                adapter.notifyDataSetChanged();

                if (data.size() <= 0) {
                    onBackPressed();
                } else {
                    path = data.get(pager.getCurrentItem());

                    if (path.endsWith(".nomedia")) {
                        imgFav.setVisibility(View.GONE);

                        date = new File(path).getAbsoluteFile().lastModified();
                        isImage = Utils.getInstance().isImageTypeForHidden(path);
                    } else {
                        if (path.contains("storage")) {
                            date = Utils.getInstance().getimageInfo(path, ViewImageActivity.this);
                            imgFav.setVisibility(View.VISIBLE);
                        }

                        isImage = Utils.getInstance().isImageType(path, ViewImageActivity.this);
                    }

                    if (isImage) {
                        new Thread(() -> bm = getImageExIF(path)).start();
                        llEdit.setVisibility(View.VISIBLE);
                    } else {
                        llEdit.setVisibility(View.GONE);
                    }

                    txttitle.setText(new File(path).getName());

                }
            }));
        }
    }

    public void dismissLoader() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing())
            mLoadingDialog.dismiss();
    }

    private void showLoader(Activity mActivity) {

        try {

            mLoadingDialog = new Dialog(mActivity);
            mLoadingDialog.setContentView(R.layout.dialog_loader);
            mLoadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mLoadingDialog.setCancelable(false);
            mLoadingDialog.setCanceledOnTouchOutside(false);
            mLoadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            mLoadingDialog.show();
            TextView textView = mLoadingDialog.findViewById(R.id.txt_Msg);
            textView.setText(getResources().getString(R.string.delete_in_progress));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void delete(PagerAdapter adapter) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

                    ArrayList<String> selectedItems = new ArrayList<>();
                    selectedItems.add(path);

                    ArrayList<Uri> uris = new ArrayList<>();
                    for (int i = 0; i < selectedItems.size(); i++) {
                        String path = selectedItems.get(i);

                        Utils.getInstance().recycleImage(selectedItems.get(i), ViewImageActivity.this, true);
                        if (new File(selectedItems.get(i)).exists()) {
                            Uri uri = Utils.getInstance().getAppendedUri(selectedItems.get(i), ViewImageActivity.this);
                            if (uri != null)
                                uris.add(uri);
                        } else {
                            Utils.getInstance().removeImage(path, ViewImageActivity.this);
                        }
                    }

                    if (uris.size() > 0) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dismissLoader();

                                PendingIntent intent = MediaStore.createDeleteRequest(getContentResolver(), uris);
                                Request request = RequestFabric.create(intent.getIntentSender(), null, 0, 0, 0, null);

                                new InlineActivityResult(ViewImageActivity.this)
                                        .startForResult(request)
                                        .onSuccess(new SuccessCallback() {
                                            @Override
                                            public void onSuccess(Result result) {
                                                if (result.getResultCode() == RESULT_OK) {

                                                    Utils.getInstance().removeImage(path, ViewImageActivity.this);

                                                    MediaScannerConnection.scanFile(ViewImageActivity.this, new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
                                                        public void onScanCompleted(String path, Uri uri) {

                                                        }
                                                    });

                                                    PowerPreference.getDefaultFile().putString(Constant.recycleList, new Gson().toJson(new ArrayList<String>()));
                                                    dismissLoader();
                                                    refreshdd(adapter);

                                                } else {
                                                    Utils.getInstance().clearRecycledImage(ViewImageActivity.this);
                                                    dismissLoader();
                                                }

                                            }
                                        }).onFail(new FailCallback() {
                                            @Override
                                            public void onFailed(Result result) {
                                                Utils.getInstance().clearRecycledImage(ViewImageActivity.this);
                                                dismissLoader();
                                            }
                                        });
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                PowerPreference.getDefaultFile().putString(Constant.recycleList, new Gson().toJson(new ArrayList<String>()));
                                dismissLoader();
                                refreshdd(adapter);
                            }
                        });

                    }
                } else {

                    Utils.getInstance().recycleImage(path, ViewImageActivity.this, false);
                    Utils.getInstance().removeImage(path, ViewImageActivity.this);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dismissLoader();
                            refreshdd(adapter);
                        }
                    });
                }
            }
        }).start();
    }


    public void refreshdd(PagerAdapter adapter) {
        AllPhotosFragment.mAdapter.adddata(allimages);
        AllPhotosFragment.mAdapter.notifyDataSetChanged();

        FolderFragment.adapterDefault.notifyDataSetChanged();
        FolderFragment.adapter.notifyDataSetChanged();

        if (data.size() > 0)
            data.remove(path);

        adapter.notifyDataSetChanged();

        dismissLoader();
        if (data.size() <= 0) {
            onBackPressed();
        } else {
            path = data.get(pager.getCurrentItem());


            if (path.endsWith(".nomedia")) {
                imgFav.setVisibility(View.GONE);

                date = new File(path).getAbsoluteFile().lastModified();
                isImage = Utils.getInstance().isImageTypeForHidden(path);
            } else {
                if (path.contains("storage")) {
                    date = Utils.getInstance().getimageInfo(path, ViewImageActivity.this);
                    imgFav.setVisibility(View.VISIBLE);
                }

                isImage = Utils.getInstance().isImageType(path, ViewImageActivity.this);
            }

            if (isImage) {
                new Thread(() -> bm = getImageExIF(path)).start();
                llEdit.setVisibility(View.VISIBLE);
            } else {
                llEdit.setVisibility(View.GONE);
            }

            txttitle.setText(new File(path).getName());
        }
    }

    public static Bitmap getImageExIF(String str) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        int i = 1;
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(str, options);
        Bitmap decodeFile = BitmapFactory.decodeFile(str, new BitmapFactory.Options());

        try {
            String attribute = new ExifInterface(str).getAttribute("Orientation");
            if (attribute != null) {
                i = Integer.parseInt(attribute);
            }

            int i2 = 0;
            if (i == 6) {
                i2 = 90;
            }
            if (i == 3) {
                i2 = 180;
            }
            if (i == 8) {
                i2 = 270;
            }

            Matrix matrix = new Matrix();
            matrix.setRotate((float) i2, ((float) decodeFile.getWidth()) / 2.0f, ((float) decodeFile.getHeight()) / 2.0f);
            return Bitmap.createBitmap(decodeFile, 0, 0, options.outWidth, options.outHeight, matrix, true);

        } catch (Exception e) {
            return null;
        }
    }

    public void rotateBitmap(Bitmap original, float degrees) {

        Bitmap res = original.copy(original.getConfig(), true);

        Matrix matrix = new Matrix();
        matrix.preRotate(degrees);

        Bitmap rotatedBitmap = Bitmap.createBitmap(res, 0, 0, res.getWidth(), res.getHeight(), matrix, true);
        res.recycle();

        res = rotatedBitmap;
        bm = res;
    }

    public void flipBitmap(Bitmap bms) {
        Matrix matrix = new Matrix();
        matrix.setScale(-1, 1);
        matrix.postTranslate(bms.getWidth(), 0);

        matrix.postTranslate(0, bms.getHeight());
        bm = Bitmap.createBitmap(bms, 0, 0, bms.getWidth(), bms.getHeight(), matrix, true);
    }

    private String getTodayDate(boolean isToday) {

        Calendar calendar = Calendar.getInstance();
        if (!isToday)
            calendar.add(Calendar.DATE, -1);

        SimpleDateFormat timeStampFormat = new SimpleDateFormat("MMM dd,yyyy");

        return timeStampFormat.format(calendar.getTime());
    }


    @Override
    protected void onResume() {
        super.onResume();


        new ListBannerAds().showBannerAds(this, null, null);

    }


    @Override
    public void onBackPressed() {
        finish();

        if (AllPhotosFragment.mAdapter != null && allimages != null)
            AllPhotosFragment.mAdapter.adddata(allimages);

        if (AllPhotosFragment.mAdapter != null)
            AllPhotosFragment.mAdapter.notifyDataSetChanged();

        if (FolderFragment.adapterDefault != null)
            FolderFragment.adapterDefault.notifyDataSetChanged();

        if (FolderFragment.adapter != null)
            FolderFragment.adapter.notifyDataSetChanged();

    }
}
