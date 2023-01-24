package image.gallery.organize.Activity;

import static image.gallery.organize.MyApplication.folderData;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import image.gallery.organize.Adhelper.BackInterAds;
import image.gallery.organize.Adhelper.LargeNativeAds;
import image.gallery.organize.Helper.Utils;
import image.gallery.organize.R;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ShowDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show_details);

        setview();
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



        new LargeNativeAds().showNativeAds(this, null,null,null);
    }

    @SuppressLint("SetTextI18n")
    private void setview() {
        findViewById(R.id.imgBack).setOnClickListener(view -> onBackPressed());

        TextView txtTitle = findViewById(R.id.txtTitle);
        TextView txtTime = findViewById(R.id.txtTime);
        TextView txtSize = findViewById(R.id.txtSize);
        TextView txtHeight = findViewById(R.id.txtHeight);
        TextView txtWidth = findViewById(R.id.txtWidth);
        TextView txtPath = findViewById(R.id.txtPath);
        TextView txtTimeTitle = findViewById(R.id.txtTimeTitle);
        LinearLayout llHeight = findViewById(R.id.llHeight);
        LinearLayout llWidth = findViewById(R.id.llWidth);


        if (getIntent().hasExtra("isFromFolder")) {

            File file = new File(folderData.get(getIntent().getStringExtra("path")).get(0)).getParentFile();

            txtTimeTitle.setText(getResources().getString(R.string.count));
            llHeight.setVisibility(View.GONE);
            llWidth.setVisibility(View.GONE);

            try {

                txtTitle.setText(file.getName());
                txtPath.setText(file.getAbsolutePath());
                txtTime.setText("" + folderData.get(getIntent().getStringExtra("path")).size());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    try {
                        long size = Files.walk(file.toPath())
                                .map(f -> f.toFile())
                                .filter(f -> f.isFile())
                                .mapToLong(f -> f.length()).sum();


                        txtSize.setText(getFileSize(size));
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    txtSize.setText(getFileSize(getFolderSize(file)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            File file = new File(getIntent().getStringExtra("path"));
            if (file.getAbsolutePath().contains("storage")) {
                long date = Utils.getInstance().getimageInfo(file.getAbsolutePath(), this);

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(date);

                if (calendar.get(Calendar.YEAR) == 1970) {
                    calendar.setTimeInMillis(date * 1000);
                }

                SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                String datestr = timeStampFormat.format(calendar.getTime());
                txtTime.setText(datestr);

            } else {
                findViewById(R.id.llTime).setVisibility(View.GONE);
            }

            try {
                txtTitle.setText(file.getName().substring(0, file.getName().lastIndexOf(".")));
                txtSize.setText(getFileSize(file));

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                int imageHeight = options.outHeight;
                int imageWidth = options.outWidth;

                txtHeight.setText(imageHeight + "");
                txtWidth.setText(imageWidth + "");

                txtPath.setText(file.getAbsolutePath());

                boolean isImage;
                if (file.getAbsolutePath().endsWith(".nomedia")) {
                    isImage = Utils.getInstance().isImageTypeForHidden(file.getAbsolutePath());
                } else {
                    isImage = Utils.getInstance().isImageType(file.getAbsolutePath(), ShowDetailsActivity.this);
                }

                if (!isImage) {
                    llHeight.setVisibility(View.GONE);
                    llWidth.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public long getFolderSize(File dir) {
        if (dir.exists()) {
            long result = 0;
            File[] fileList = dir.listFiles();
            assert fileList != null;
            for (File file : fileList) {
                if (file.isDirectory()) {
                    result += getFolderSize(file);
                } else {
                    result += file.length();
                }
            }
            return result;
        }
        return 0;
    }


    private static final DecimalFormat format = new DecimalFormat("#.##");
    private static final long MiB = 1024 * 1024;
    private static final long KiB = 1024;

    public String getFileSize(File file) {

        if (!file.isFile()) {
            throw new IllegalArgumentException("Expected a file");
        }
        final double length = file.length();

        if (length > MiB) {
            return format.format(length / MiB) + " MB";
        }
        if (length > KiB) {
            return format.format(length / KiB) + " KB";
        }
        return format.format(length) + " B";
    }

    public String getFileSize(long length) {

        if (length > MiB) {
            return format.format(length / MiB) + " MB";
        }
        if (length > KiB) {
            return format.format(length / KiB) + " KB";
        }
        return format.format(length) + " B";
    }
}
