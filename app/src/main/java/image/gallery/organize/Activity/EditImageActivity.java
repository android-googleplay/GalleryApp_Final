package image.gallery.organize.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import image.gallery.organize.Adhelper.BackInterAds;
import image.gallery.organize.Adhelper.InterAds;
import image.gallery.organize.Adhelper.ListBannerAds;
import image.gallery.organize.crop.CropImageView;
import image.gallery.organize.library.ViewAnimator.ViewAnimator;
import image.gallery.organize.Helper.Utils;
import image.gallery.organize.R;
import image.gallery.organize.extras.ActionListeners;
import image.gallery.organize.extras.ViewToImage;
import image.gallery.organize.library.ViewAnimator.AnimationListener;
import image.gallery.organize.sticker.BitmapStickerIcon;
import image.gallery.organize.sticker.DeleteIconEvent;
import image.gallery.organize.sticker.FlipHorizontallyEvent;
import image.gallery.organize.sticker.Sticker;
import image.gallery.organize.sticker.StickerView;
import image.gallery.organize.sticker.ZoomIconEvent;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailCallback;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class EditImageActivity extends AppCompatActivity {

    public static StickerView stickerView;
    private int type = 0;
    private Bitmap bm;
    private Bitmap bmCopy;
    private Bitmap bmthumb;

    private TextView txt1;
    private TextView txt2;
    private TextView txt3;
    private TextView txt4;
    private TextView txt5;
    private TextView txt6;
    private TextView txt7;
    private TextView txt8;
    private TextView txt9;
    private ImageView imgNoCrop;
    private ImageView imgPreview;

    private RelativeLayout relPreview;
    boolean isClose = false;

    Dialog mSavingDialog;
    Dialog mLoadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);
        Utils.getInstance().colorStatusBar(this);
        showLoader(EditImageActivity.this);
        hidePanel();
    }

    public void hidePanel() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        LinearLayout llAdjustLayout = findViewById(R.id.llAdjustLayout);
        LinearLayout llCropLayout = findViewById(R.id.llCropLayout);
        LinearLayout llFilterLayout = findViewById(R.id.llFilterLayout);

        ViewAnimator
                .animate(llAdjustLayout)
                .translationY(height)
                .andAnimate(llCropLayout)
                .translationY(height)
                .andAnimate(llFilterLayout)
                .translationY(height)
                .duration(1)
                .onStop(new AnimationListener.Stop() {
                    @Override
                    public void onStop() {
                        llCropLayout.setVisibility(View.VISIBLE);
                        llAdjustLayout.setVisibility(View.VISIBLE);
                        llFilterLayout.setVisibility(View.VISIBLE);
                        setview();
                    }
                }).start();
    }

    public void dismissSavingLoader() {
        if (mSavingDialog != null && mSavingDialog.isShowing())
            mSavingDialog.dismiss();
    }


    private void showScreenDataLoader(Activity mActivity) {

        try {
            mSavingDialog = new Dialog(mActivity);
            mSavingDialog.setContentView(R.layout.dialog_save);
            mSavingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mSavingDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            mSavingDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void setview() {

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;

        LinearLayout llAdjustLayout = findViewById(R.id.llAdjustLayout);
        LinearLayout llCropLayout = findViewById(R.id.llCropLayout);
        LinearLayout llFilterLayout = findViewById(R.id.llFilterLayout);

        relPreview = findViewById(R.id.relPreview);

        findViewById(R.id.imgBack).setOnClickListener(v -> onBackPressed());

        stickerView = findViewById(R.id.stickerView);
        setUpStickerView();

        imgPreview = findViewById(R.id.imgPreview);
        ImageView imgDone = findViewById(R.id.imgDone);
        ImageView imgDoneAdjust = findViewById(R.id.imgDoneAdjust);
        ImageView imgClose = findViewById(R.id.imgClose);
        CropImageView imgCrop = findViewById(R.id.imgCrop);

        imgDone.setOnClickListener(view -> {

            imgDone.setVisibility(View.GONE);
            showScreenDataLoader(EditImageActivity.this);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    imgCrop.setVisibility(View.GONE);
                    stickerView.setLocked(true);

                    imgPreview.setVisibility(View.VISIBLE);

                    new ViewToImage(EditImageActivity.this, relPreview, new ActionListeners() {
                        @Override
                        public void convertedWithSuccess(Bitmap bitmap, String filePath, File file) {


                            Utils.getInstance().scanMedia(EditImageActivity.this, file.getAbsolutePath());

                            long length = file.length();
                            ContentValues contentValues = new ContentValues(9);
                            contentValues.put("title", file.getName());
                            contentValues.put("_display_name", file.getName());
                            contentValues.put(MediaStore.Files.FileColumns.MEDIA_TYPE, MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);

                            contentValues.put("_data", file.getAbsolutePath());
                            contentValues.put("_size", length);

                            getContentResolver().insert(MediaStore.Files.getContentUri("external"), contentValues);

                            if (SplashActivity.isDataLoaded) {
                                addfile(file);
                            } else {
                                dismissSavingLoader();

                                // addfile(file);
                                Utils.getInstance().showImageLoadingDialog(EditImageActivity.this, () -> addfile(file));
                            }
                        }

                        @Override
                        public void convertedWithError(String error) {
                            if (mLoadingDialog != null && mLoadingDialog.isShowing())
                                mLoadingDialog.dismiss();

                            Utils.getInstance().showError(EditImageActivity.this, "" + error);
                        }
                    });
                }
            }, 1000);




         /*   bm = createBitmapFromView(relPreview);

            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + ".png");
            Utils.getInstance().saveBitmapToStorage(bm, file);
            Utils.getInstance().scanMedia(this, file.getAbsolutePath());

            long length = file.length();
            ContentValues contentValues = new ContentValues(9);
            contentValues.put("title", file.getName());
            contentValues.put("_display_name", file.getName());
            contentValues.put(MediaStore.Files.FileColumns.MEDIA_TYPE, MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);

            contentValues.put("_data", file.getAbsolutePath());
            contentValues.put("_size", length);

            getContentResolver().insert(MediaStore.Files.getContentUri("external"), contentValues);

            if (SplashActivity.isDataLoaded) {
                addfile(file);
            } else {
                Utils.getInstance().showImageLoadingDialog(EditImageActivity.this, () -> addfile(file));
            }*/
        });

        ImageView imgDoneFilter = findViewById(R.id.imgDoneFilter);
        ImageView imgCloseFilter = findViewById(R.id.imgCloseFilter);

        ImageView imgDoneCrop = findViewById(R.id.imgDoneCrop);
        ImageView imgCloseCrop = findViewById(R.id.imgCloseCrop);

        imgNoCrop = findViewById(R.id.imgNoCrop);
        txt1 = findViewById(R.id.txt1);
        txt2 = findViewById(R.id.txt2);
        txt3 = findViewById(R.id.txt3);
        txt4 = findViewById(R.id.txt4);
        txt5 = findViewById(R.id.txt5);
        txt6 = findViewById(R.id.txt6);
        txt7 = findViewById(R.id.txt7);
        txt8 = findViewById(R.id.txt8);
        txt9 = findViewById(R.id.txt9);

        LinearLayout llCrop = findViewById(R.id.llCrop);
        LinearLayout llAdjust = findViewById(R.id.llAdjust);
        LinearLayout llFilter = findViewById(R.id.llFilter);
        LinearLayout llText = findViewById(R.id.llText);

        LinearLayout llBrightness = findViewById(R.id.llBrightness);
        LinearLayout llContrast = findViewById(R.id.llContrast);
        LinearLayout llSaturation = findViewById(R.id.llSaturation);
        LinearLayout llSharpen = findViewById(R.id.llSharpen);

        AppCompatSeekBar seekbar = findViewById(R.id.seekbar);

        String path = "file://" + getIntent().getStringExtra("path");

        Glide.with(this).asBitmap().load(getIntent().getStringExtra("path")).addListener(new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                Utils.getInstance().showError(EditImageActivity.this, getResources().getString(R.string.something_went_wrong));
                onBackPressed();
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                bm = resource;
                bmCopy = bm.copy(bm.getConfig(), true);
                bmthumb = ThumbnailUtils.extractThumbnail(bm, Math.min(bm.getWidth(), bm.getHeight()), Math.min(bm.getWidth(), bm.getHeight()));
                setupFilter();
                return false;
            }
        }).into(imgPreview);

        llText.setOnClickListener(v -> {
            if (!isClose) {

                new InterAds().showInterAds(EditImageActivity.this, new InterAds.OnAdClosedListener() {
                    @Override
                    public void onAdClosed() {
                        startActivity(new Intent(EditImageActivity.this, EditTextActivity.class));
                    }
                });
            }
        });

        llAdjust.setOnClickListener(view -> {

            if (!isClose) {
                isClose = true;
                type = 0;
                llBrightness.setBackgroundResource(R.drawable.bg_selection);
                llContrast.setBackground(null);
                llSaturation.setBackground(null);
                llSharpen.setBackground(null);

                llAdjustLayout.animate().translationY(0).setDuration(500);
                imgDone.setVisibility(View.GONE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    seekbar.setMin(-100);
                }
                seekbar.setProgress(0);
                seekbar.setMax(100);
            }
        });

        llBrightness.setOnClickListener(view -> {
            type = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                seekbar.setMin(-100);
            }
            seekbar.setProgress(0);
            seekbar.setMax(100);

            llBrightness.setBackgroundResource(R.drawable.bg_selection);
            llContrast.setBackground(null);
            llSaturation.setBackground(null);
            llSharpen.setBackground(null);
        });

        llContrast.setOnClickListener(view -> {

            type = 1;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                seekbar.setMin(-50);
            }
            seekbar.setProgress(0);
            seekbar.setMax(50);

            llContrast.setBackgroundResource(R.drawable.bg_selection);
            llBrightness.setBackground(null);
            llSaturation.setBackground(null);
            llSharpen.setBackground(null);
        });

        llSaturation.setOnClickListener(view -> {

            type = 2;
            seekbar.setProgress(100);
            seekbar.setMax(200);

            llSaturation.setBackgroundResource(R.drawable.bg_selection);
            llBrightness.setBackground(null);
            llContrast.setBackground(null);
            llSharpen.setBackground(null);
        });

        llSharpen.setOnClickListener(view -> {

            type = 3;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                seekbar.setMin(-100);
            }
            seekbar.setProgress(0);
            seekbar.setMax(100);

            llSharpen.setBackgroundResource(R.drawable.bg_selection);
            llBrightness.setBackground(null);
            llContrast.setBackground(null);
            llSaturation.setBackground(null);
        });

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                try {
                    if (b) {
                        if (type == 0) {
                            imgPreview.setImageBitmap(Utils.getInstance().brightness(bm, seekBar.getProgress()));
                        } else if (type == 1) {
                            imgPreview.setImageBitmap(Utils.getInstance().contrastBurn(bm, seekBar.getProgress()));
                        } else if (type == 2) {
                            imgPreview.setImageBitmap(Utils.getInstance().saturation(bm, seekBar.getProgress()));
                        } else if (type == 3) {
                            imgPreview.setImageBitmap(Utils.getInstance().doSharpen(bm, seekBar.getProgress()));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        imgClose.setOnClickListener(view -> {
            llAdjustLayout.animate().translationY(height).setDuration(700);
            imgDone.setVisibility(View.VISIBLE);

            imgPreview.setImageBitmap(bmCopy);
            imgCrop.setVisibility(View.GONE);
            imgPreview.setVisibility(View.VISIBLE);

            bm = bmCopy.copy(bmCopy.getConfig(), true);
            isClose = false;

        });

        imgDoneAdjust.setOnClickListener(v -> {
            llAdjustLayout.animate().translationY(height).setDuration(700);
            imgDone.setVisibility(View.VISIBLE);

            bmCopy = bm.copy(bm.getConfig(), true);
            isClose = false;

        });

        llCrop.setOnClickListener(view -> {

            if (!isClose) {
                isClose = true;
                llCropLayout.animate().translationY(0).setDuration(500);
                imgDone.setVisibility(View.GONE);
                imgCrop.setImageBitmap(bm);
                commonClr();

                imgNoCrop.setBackground(getResources().getDrawable(R.drawable.bg_crop_selected));
                imgCrop.setCropMode(CropImageView.CropMode.FIT_IMAGE);

                imgCrop.setVisibility(View.VISIBLE);
                imgPreview.setVisibility(View.GONE);
            }
        });

        imgCloseCrop.setOnClickListener(v -> {

            llCropLayout.animate().translationY(height).setDuration(700);
            imgDone.setVisibility(View.VISIBLE);
            imgPreview.setImageBitmap(bmCopy);
            imgCrop.setVisibility(View.GONE);
            imgPreview.setVisibility(View.VISIBLE);
            bm = bmCopy.copy(bmCopy.getConfig(), true);
            isClose = false;

        });

        imgNoCrop.setOnClickListener(v -> {
            commonClr();

            imgNoCrop.setBackground(getResources().getDrawable(R.drawable.bg_crop_selected));
            imgCrop.setCropMode(CropImageView.CropMode.FIT_IMAGE);
        });

        txt1.setOnClickListener(v -> {
            commonClr();

            txt1.setBackground(getResources().getDrawable(R.drawable.bg_crop_selected));
            imgCrop.setCustomRatio(1, 1);

        });

        txt2.setOnClickListener(v -> {
            commonClr();

            txt2.setBackground(getResources().getDrawable(R.drawable.bg_crop_selected));
            imgCrop.setCustomRatio(4, 3);
        });

        txt3.setOnClickListener(v -> {
            commonClr();

            txt3.setBackground(getResources().getDrawable(R.drawable.bg_crop_selected));
            imgCrop.setCustomRatio(3, 4);
        });

        txt4.setOnClickListener(v -> {
            commonClr();

            txt4.setBackground(getResources().getDrawable(R.drawable.bg_crop_selected));
            imgCrop.setCustomRatio(5, 4);

        });

        txt5.setOnClickListener(v -> {
            commonClr();

            txt5.setBackground(getResources().getDrawable(R.drawable.bg_crop_selected));
            imgCrop.setCustomRatio(4, 5);
        });

        txt6.setOnClickListener(v -> {
            commonClr();

            txt6.setBackground(getResources().getDrawable(R.drawable.bg_crop_selected));
            imgCrop.setCustomRatio(2, 3);
        });

        txt7.setOnClickListener(v -> {
            commonClr();

            txt7.setBackground(getResources().getDrawable(R.drawable.bg_crop_selected));
            imgCrop.setCustomRatio(3, 2);
        });

        txt8.setOnClickListener(v -> {
            commonClr();

            txt8.setBackground(getResources().getDrawable(R.drawable.bg_crop_selected));
            imgCrop.setCustomRatio(9, 16);

        });

        txt9.setOnClickListener(v -> {
            commonClr();
            txt9.setBackground(getResources().getDrawable(R.drawable.bg_crop_selected));
            imgCrop.setCustomRatio(16, 9);
        });

        imgDoneCrop.setOnClickListener(view -> {
          /*  bm = imgCrop.getCroppedBitmap();
            bmCopy = bm.copy(bm.getConfig(), true);
            imgPreview.setImageBitmap(bm);
            imgDone.setVisibility(View.VISIBLE);

            llCropLayout.animate().translationY(height).setDuration(700);
            imgCrop.setVisibility(View.GONE);
            imgPreview.setVisibility(View.VISIBLE);
             isClose = false;*/
            doneCrop(imgCrop,imgDone,llCropLayout,height);
        });

        llFilter.setOnClickListener(view -> {
            if (!isClose) {
                isClose = true;
                llFilterLayout.animate().translationY(0).setDuration(500);
                imgDone.setVisibility(View.GONE);
            }
        });

        imgCloseFilter.setOnClickListener(v -> {
            llFilterLayout.animate().translationY(height).setDuration(700);
            imgDone.setVisibility(View.VISIBLE);
            imgPreview.setImageBitmap(bmCopy);
            imgCrop.setVisibility(View.GONE);
            imgPreview.setVisibility(View.VISIBLE);

            bm = bmCopy.copy(bmCopy.getConfig(), true);
            isClose = false;

        });

        imgDoneFilter.setOnClickListener(view -> {
            llFilterLayout.animate().translationY(height).setDuration(700);
            imgDone.setVisibility(View.VISIBLE);
            bmCopy = bm.copy(bm.getConfig(), true);
            isClose = false;

        });
    }

    public void doneCrop(CropImageView imgNoCrop,ImageView imgDone,LinearLayout llCropLayout,int height) {
        Glide.with(this).asBitmap().load(imgNoCrop.getCroppedBitmap()).addListener(new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                Utils.getInstance().showError(EditImageActivity.this, getResources().getString(R.string.something_went_wrong));
                onBackPressed();
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                bm = resource;
                bmCopy = bm.copy(bm.getConfig(), true);
                imgPreview.setImageBitmap(bm);
                imgDone.setVisibility(View.VISIBLE);

                llCropLayout.animate().translationY(height).setDuration(700);
                imgNoCrop.setVisibility(View.GONE);
                imgPreview.setVisibility(View.VISIBLE);
                isClose = false;
                return false;
            }
        }).into(imgPreview);

    }

    private void addfile(File file) {
        Utils.getInstance().addFiles(file.getAbsolutePath(), EditImageActivity.this);
        dismissSavingLoader();

        new InterAds().showInterAds(EditImageActivity.this, new InterAds.OnAdClosedListener() {
            @Override
            public void onAdClosed() {
                startActivity(new Intent(EditImageActivity.this, ImageShareActivity.class).putExtra("path", file.getAbsolutePath()));
                finish();
            }
        });
        if (ViewImageActivity.activity != null)
            ViewImageActivity.activity.finish();
    }

    @Override
    public void onBackPressed() {
        new BackInterAds().showInterAds(this, new BackInterAds.OnAdClosedListener() {
            @Override
            public void onAdClosed() {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        new ListBannerAds().showBannerAds(this, null, null);
    }

    public Bitmap createBitmapFromView(View view) {

        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        Bitmap createBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        view.draw(new Canvas(createBitmap));
        int i = view.getWidth();
        int i2 = view.getHeight();

        float f = (float) i;
        float width = (float) createBitmap.getWidth();
        float f2 = (float) i2;
        float height = (float) createBitmap.getHeight();
        float max = Math.max(f / width, f2 / height);
        width *= max;
        max *= height;
        f = (f - width) / 2.0f;
        f2 = (f2 - max) / 2.0f;
        RectF rectF = new RectF(f, f2, width + f, max + f2);
        Bitmap resizedBitmap = Bitmap.createBitmap(i, i2, createBitmap.getConfig());
        new Canvas(resizedBitmap).drawBitmap(createBitmap, null, rectF, null);
        return resizedBitmap;
    }

    private void setUpStickerView() {

        final BitmapStickerIcon deleteIcon = new BitmapStickerIcon(ContextCompat.getDrawable(this,
                R.drawable.ic_sticker_close),
                BitmapStickerIcon.LEFT_TOP);
        deleteIcon.setIconEvent(new DeleteIconEvent());

        BitmapStickerIcon zoomIcon = new BitmapStickerIcon(ContextCompat.getDrawable(this,
                R.drawable.ic_sticker_scale),
                BitmapStickerIcon.RIGHT_BOTOM);
        zoomIcon.setIconEvent(new ZoomIconEvent());

        BitmapStickerIcon flipIcon = new BitmapStickerIcon(ContextCompat.getDrawable(this,
                R.drawable.ic_sticker_flip),
                BitmapStickerIcon.RIGHT_TOP);
        flipIcon.setIconEvent(new FlipHorizontallyEvent());
        stickerView.setIcons(Arrays.asList(deleteIcon, zoomIcon, flipIcon/*, heartIcon*/));

        stickerView.setBackgroundColor(Color.TRANSPARENT);
        stickerView.setLocked(false);
        stickerView.setConstrained(true);

        stickerView.setOnStickerOperationListener(new StickerView.OnStickerOperationListener() {
            @Override
            public void onStickerAdded(@NonNull Sticker sticker) {

            }

            @Override
            public void onStickerClicked(@NonNull Sticker sticker) {

            }

            @Override
            public void onStickerDeleted(@NonNull Sticker sticker) {

            }

            @Override
            public void onStickerDragFinished(@NonNull Sticker sticker) {

            }

            @Override
            public void onStickerTouchedDown(@NonNull Sticker sticker) {

            }

            @Override
            public void onStickerZoomFinished(@NonNull Sticker sticker) {

            }

            @Override
            public void onStickerFlipped(@NonNull Sticker sticker) {

            }

            @Override
            public void onStickerDoubleTapped(@NonNull Sticker sticker) {

            }
        });
    }

    private void setupFilter() {

        List<Filter> filters = FilterPack.getFilterPack(this);

        for (Filter filter : filters) {
            ThumbnailItem item = new ThumbnailItem();
            item.image = bmthumb;
            item.filter = filter;
            item.filterName = filter.getName();
            ThumbnailsManager.addThumb(item);
        }

        RecyclerView listFilter = findViewById(R.id.listFilter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        layoutManager.scrollToPosition(0);
        listFilter.setLayoutManager(layoutManager);
        listFilter.setHasFixedSize(true);

        List<ThumbnailItem> thumbs = ThumbnailsManager.processThumbs(this);

        ThumbnailsAdapter adapter = new ThumbnailsAdapter(thumbs, filter -> {

            Bitmap bm1 = bmCopy.copy(bmCopy.getConfig(), true);
            bm = filter.processFilter(bm1);
            imgPreview.setImageBitmap(bm);
        });

        listFilter.setAdapter(adapter);

        adapter.notifyDataSetChanged();
        dismissLoader();
    }

    private void commonClr() {
        imgNoCrop.setBackground(getResources().getDrawable(R.drawable.bg_crop));
        txt1.setBackground(getResources().getDrawable(R.drawable.bg_crop));
        txt2.setBackground(getResources().getDrawable(R.drawable.bg_crop));
        txt3.setBackground(getResources().getDrawable(R.drawable.bg_crop));
        txt4.setBackground(getResources().getDrawable(R.drawable.bg_crop));
        txt5.setBackground(getResources().getDrawable(R.drawable.bg_crop));
        txt6.setBackground(getResources().getDrawable(R.drawable.bg_crop));
        txt7.setBackground(getResources().getDrawable(R.drawable.bg_crop));
        txt8.setBackground(getResources().getDrawable(R.drawable.bg_crop));
        txt9.setBackground(getResources().getDrawable(R.drawable.bg_crop));
    }

    public class ThumbnailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final String TAG = "THUMBNAILS_ADAPTER";
        private int lastPosition = -1;
        private ThumbnailCallback thumbnailCallback;
        private List<ThumbnailItem> dataSet;

        public ThumbnailsAdapter(List<ThumbnailItem> dataSet, ThumbnailCallback thumbnailCallback) {
            this.dataSet = dataSet;
            this.thumbnailCallback = thumbnailCallback;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_thumbnail, viewGroup, false);
            return new ThumbnailsViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder,int i) {
            final ThumbnailItem thumbnailItem = dataSet.get(i);
            ThumbnailsViewHolder thumbnailsViewHolder = (ThumbnailsViewHolder) holder;
            thumbnailsViewHolder.thumbnail.setImageBitmap(thumbnailItem.image);

            if (lastPosition == i) {
                thumbnailsViewHolder.thumbnail.setBackgroundResource(R.drawable.bg_crop_selected);
            } else {
                thumbnailsViewHolder.thumbnail.setBackground(null);
            }

            thumbnailsViewHolder.thumbnail.setOnClickListener(v -> {
                if (lastPosition != holder.getLayoutPosition()) {
                    thumbnailCallback.onThumbnailClick(thumbnailItem.filter);
                    lastPosition = holder.getLayoutPosition();
                }

                notifyDataSetChanged();
            });
        }

        @Override
        public int getItemCount() {
            return dataSet.size();
        }

        public class ThumbnailsViewHolder extends RecyclerView.ViewHolder {
            public ImageView thumbnail;

            public ThumbnailsViewHolder(View v) {
                super(v);
                this.thumbnail = (ImageView) v.findViewById(R.id.thumbnail);
            }
        }
    }
}