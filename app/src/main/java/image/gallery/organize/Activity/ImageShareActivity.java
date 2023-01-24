package image.gallery.organize.Activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import image.gallery.organize.Adhelper.BackInterAds;
import image.gallery.organize.Adhelper.ListBannerAds;
import image.gallery.organize.Helper.Utils;
import image.gallery.organize.R;

import java.io.File;

public class ImageShareActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_share);
        setview();
    }

    private void setview() {

        findViewById(R.id.imgBack).setOnClickListener(v -> onBackPressed());

        findViewById(R.id.imgHome).setOnClickListener(v -> {
            if (ViewFolderImagesActivity.activity != null) {
                ViewFolderImagesActivity.activity.finish();
            }
            onBackPressed();
        });

        ImageView imgPreview = findViewById(R.id.imgPreview);
        ImageView imgShare = findViewById(R.id.imgShare);
        ImageView imgwp = findViewById(R.id.imgwp);
        ImageView imgfb = findViewById(R.id.imgfb);
        ImageView imginsta = findViewById(R.id.imginsta);
        ImageView imgtwitter = findViewById(R.id.imgtwitter);
        ImageView imgmessenger = findViewById(R.id.imgmessenger);
        ImageView imggmail = findViewById(R.id.imggmail);

        String path = getIntent().getStringExtra("path");

        Glide.with(this).load(path).into(imgPreview);

        imgShare.setOnClickListener(view -> {
            try {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setType("image/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this, getPackageName() + ".provider", new File(path)));

                startActivity(Intent.createChooser(shareIntent, "Share Images Using"));
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        imgwp.setOnClickListener(v -> shareVideo("Whatsapp", "com.whatsapp", path));

        imgfb.setOnClickListener(v -> shareVideo("Facebook", "com.facebook.katana", path));

        imginsta.setOnClickListener(v -> shareVideo("Instagram", "com.instagram.android", path));

        imgmessenger.setOnClickListener(v -> shareVideo("Facebook Messenger", "com.facebook.orca", path));

        imggmail.setOnClickListener(v -> shareVideo("Gmail", "com.google.android.gm", path));

        imgtwitter.setOnClickListener(v -> shareVideo("Twitter", "com.twitter.android", path));

    }

    public void shareVideo(String name, String shareFlag, String createdVideo) {
        File mFile = new File(createdVideo);
        if (mFile.exists()) {
            PackageManager pm = getPackageManager();

            try {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());

                Uri uri = Uri.fromFile(mFile);
                Intent videoshare = new Intent(Intent.ACTION_SEND);
                videoshare.setType("image/*");
                if (shareFlag != null) videoshare.setPackage(shareFlag);
                videoshare.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                videoshare.putExtra(Intent.EXTRA_STREAM, uri);
                videoshare.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                startActivity(videoshare);
            } catch (Exception ignored) {
                Utils.getInstance().showError(this, name + getResources().getString(R.string.not_installed));
            }
        } else {
            Utils.getInstance().showError(this, getResources().getString(R.string.something_went_wrong));
        }
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
