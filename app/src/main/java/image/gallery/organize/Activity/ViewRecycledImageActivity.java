package image.gallery.organize.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import image.gallery.organize.Adhelper.BackInterAds;
import image.gallery.organize.Adhelper.InterAds;
import image.gallery.organize.Adhelper.ListBannerAds;
import image.gallery.organize.Fragment.HideImagesFragment;
import image.gallery.organize.MyApplication;
import image.gallery.organize.Helper.Constant;
import com.jsibbold.zoomage.ZoomageView;
import image.gallery.organize.Helper.CustomVideoView;
import image.gallery.organize.Helper.Database;
import image.gallery.organize.Helper.Utils;
import image.gallery.organize.Helper.ViewPagerFixed;
import image.gallery.organize.R;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;

public class ViewRecycledImageActivity extends AppCompatActivity {

    public static Activity activity;
    private String path;
    private boolean isstart = true;
    private boolean ispagescrolled = false;
    private ZoomageView imgPreview;
    Dialog mLoadingDialog;

    MediaMetadataRetriever mediaMetadataRetriever;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;
        setContentView(R.layout.activity_view_recycled_image);

        mediaMetadataRetriever = new MediaMetadataRetriever();
        path = getIntent().getStringExtra("path");

        setview();
    }

    private void setview() {

        findViewById(R.id.imgBack).setOnClickListener(view -> onBackPressed());

        ViewPagerFixed pager = findViewById(R.id.pager);

        pager.setOffscreenPageLimit(0);
        pager.setSaveFromParentEnabled(false);

        TextView txttitle = findViewById(R.id.txttitle);
        txttitle.setText(new File(path).getName());

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
                ispagescrolled = false;
                path = MyApplication.binImages.get(position);

                txttitle.setText(new File(path).getName());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                ispagescrolled = false;
            }
        });

        PagerAdapter adapter = new PagerAdapter() {

            @Override
            public int getCount() {
                return MyApplication.binImages.size();
            }

            @NonNull
            @NotNull
            @Override
            public Object instantiateItem(@NonNull @NotNull ViewGroup container, int position) {

                View inflate = LayoutInflater.from(ViewRecycledImageActivity.this).inflate(R.layout.item_view_photo, container, false);
                CustomVideoView videoview;

                String path = "";
                File filesrc;
                boolean isImage;
                path = MyApplication.binImages.get(position);

                filesrc = new File(path);

                if (path.endsWith(".nomedia")) {
                    isImage = Utils.getInstance().isImageTypeForHidden(path);
                } else {
                    isImage = Utils.getInstance().isImageType(path, ViewRecycledImageActivity.this);
                }

                videoview = inflate.findViewById(R.id.videoview);
                imgPreview = inflate.findViewById(R.id.imgPreview);
                RelativeLayout relVideoView = inflate.findViewById(R.id.relVideoView);

                if (isImage) {

                    relVideoView.setVisibility(View.GONE);
                    imgPreview.setVisibility(View.VISIBLE);

                    Glide.with(ViewRecycledImageActivity.this).load(path).into(imgPreview);

                } else {

                    relVideoView.setVisibility(View.VISIBLE);
                    imgPreview.setVisibility(View.GONE);

                    ImageView imgPlay = inflate.findViewById(R.id.imgPlay);

                    MediaController mediaController = new MediaController(ViewRecycledImageActivity.this);
                    mediaController.setAnchorView(videoview);

                    videoview.setMediaController(null);

                    try {
                        mediaMetadataRetriever.setDataSource(path);
                        if (mediaMetadataRetriever.getFrameAtTime() != null) {
                            videoview.setVideoURI(Uri.fromFile(filesrc));
                        } else {

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (mediaMetadataRetriever != null)
                            mediaMetadataRetriever.release();
                        mediaMetadataRetriever = new MediaMetadataRetriever();
                    }
                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {

                            if (ispagescrolled) {
                                if (videoview.isPlaying())
                                    videoview.pause();

                                if (mediaController.isShowing())
                                    mediaController.hide();

                            } else {
                                handler.removeCallbacks(this);
                            }

                            if (videoview.isPlaying())
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
                        videoview.setMediaController(mediaController);
                        videoview.start();
                    });
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
        pager.setCurrentItem(MyApplication.binImages.indexOf(path));

        findViewById(R.id.imgDetails).setOnClickListener(view -> {
            new InterAds().showInterAds(ViewRecycledImageActivity.this, new InterAds.OnAdClosedListener() {
                @Override
                public void onAdClosed() {
                    startActivity(new Intent(ViewRecycledImageActivity.this, ShowDetailsActivity.class).putExtra("path", path));
                }
            });
        });

        findViewById(R.id.llDelete).setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.delete));
            builder.setMessage(getResources().getString(R.string.sure_delete));
            builder.setPositiveButton(getResources().getString(R.string.ok), (dialog, which) -> {

                showLoader(ViewRecycledImageActivity.this,getResources().getString(R.string.delete_in_progress));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        new File(path).delete();
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (MyApplication.binImages.size() <= 1) {
                                    MyApplication.binImages.clear();
                                    adapter.notifyDataSetChanged();
                                    if (MyApplication.binImages.size() <= 0) {
                                        ViewRecycledImageActivity.this.onBackPressed();
                                    }
                                } else {
                                    MyApplication.binImages.remove(path);
                                    adapter.notifyDataSetChanged();
                                }

                                dismissLoader();
                            }
                        });
                    }
                }).start();


            });

            builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        findViewById(R.id.llRestore).setOnClickListener(v -> {

            showLoader(ViewRecycledImageActivity.this,getResources().getString(R.string.restore_progress));
            if (SplashActivity.isDataLoaded) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        restore();
                    }
                }).start();

            } else {
                Utils.getInstance().showImageLoadingDialog(ViewRecycledImageActivity.this, () -> {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            restore();
                        }
                    }).start();
                });
            }
        });
    }

    public void dismissLoader() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing())
            mLoadingDialog.dismiss();
    }

    private void showLoader(Activity mActivity,String text) {

        try {
            mLoadingDialog = new Dialog(mActivity);
            mLoadingDialog.setContentView(R.layout.dialog_loader);
            mLoadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mLoadingDialog.setCancelable(false);
            mLoadingDialog.setCanceledOnTouchOutside(false);
            mLoadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            mLoadingDialog.show();

            TextView textView = mLoadingDialog.findViewById(R.id.txt_Msg);
            textView.setText(text);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void restore() {

        File destFile = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            destFile = new File(Constant.getOutputFolder() + File.separator + new File(path).getName());
        } else {
            destFile = new File(Database.getInstance(this).getBinInfo(new File(path).getName()));
        }

        if (destFile.getParentFile() != null) {

            if (!destFile.getParentFile().exists()) {
                destFile.getParentFile().mkdirs();
            }

            Utils.getInstance().copyImageFile(this, new File(path), destFile);

            if (!destFile.getAbsolutePath().endsWith(".nomedia"))
                Utils.getInstance().addFiles(destFile.getAbsolutePath(), ViewRecycledImageActivity.this);
            else {
                Utils.getInstance().getHiddenImages(ViewRecycledImageActivity.this, new Utils.hidedone() {
                    @Override
                    public void hideComplete() {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (HideImagesFragment.stickyList != null && HideImagesFragment.llNoDataFound != null) {

                                    HideImagesFragment.mAdapter.notifyDataSetChanged();

                                    if (MyApplication.isEnteredPwd) {
                                        if (MyApplication.HiddenImages.size() > 0) {
                                            HideImagesFragment.stickyList.setVisibility(View.VISIBLE);
                                            HideImagesFragment.llNoDataFound.setVisibility(View.GONE);
                                        } else {
                                            HideImagesFragment.stickyList.setVisibility(View.GONE);
                                            HideImagesFragment.llNoDataFound.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }
                            }
                        });

                    }
                }, null);
            }
        } else {
            Utils.getInstance().deleteFile(ViewRecycledImageActivity.this, path);
        }

        MyApplication.binImages.remove(path);
        Utils.getInstance().deleteFile(ViewRecycledImageActivity.this, path);

        ArrayList<String> data = new ArrayList<>();
        data.add(path);
        Database.getInstance(ViewRecycledImageActivity.this).deleteFromBin(data);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Utils.getInstance().showSuccess(ViewRecycledImageActivity.this, getResources().getString(R.string.restored_sucessfully));
            }
        });
        finish();
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
}
