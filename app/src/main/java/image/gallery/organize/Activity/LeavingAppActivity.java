package image.gallery.organize.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import image.gallery.organize.R;

public class LeavingAppActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaving_app);

        getWindow().setLayout(-1, -1);
        getWindow().setBackgroundDrawable(null);
        setFinishOnTouchOutside(false);

        stop();
    }

    public void stop() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            try {
                System.exit(0);
            } catch (Exception e) {
                finishAffinity();
                e.printStackTrace();
            }
        }, 4000);
    }

}