package image.gallery.organize.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import image.gallery.organize.Adhelper.BackInterAds;
import image.gallery.organize.Adhelper.ListBannerAds;
import image.gallery.organize.MyApplication;
import image.gallery.organize.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SlideShowActivity extends AppCompatActivity {


    private ArrayList<String> data = new ArrayList<>();

    int pos = 0;

    @BindView(R.id.imgBack)
    ImageView ivBack;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    ImageAdapter imageAdapter;
    @BindView(R.id.progress)
    ProgressBar progressBar;

    int statee;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_show);

        ButterKnife.bind(this);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imageAdapter = new ImageAdapter();

        recyclerView.setLayoutManager(new SpeedyLinearLayoutManager(this, SpeedyLinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(imageAdapter);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadData();
            }
        }, 500);
    }

    @Override
    protected void onResume() {
        super.onResume();



        new ListBannerAds().showBannerAds(this, null, null);
    }


    public void loadData() {

        pos = getIntent().getIntExtra("pos", 0);
        if (getIntent().getIntExtra("type", 0) == 1) {
            data.addAll(MyApplication.allimagesCopyWithoutDates);
        } else if (getIntent().getIntExtra("type", 0) == 2) {
            data.addAll(MyApplication.HiddenImagesWithoutFolder);
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

        imageAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
        recyclerView.scrollToPosition(pos);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                recyclerView.smoothScrollToPosition(data.size() - 1);
            }
        }, 500);
    }

    public class SpeedyLinearLayoutManager extends LinearLayoutManager {

        private static final float MILLISECONDS_PER_INCH = 2500f; //default is 25f (bigger = slower)

        public SpeedyLinearLayoutManager(Context context) {
            super(context);
        }

        public SpeedyLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        public SpeedyLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        public void onScrollStateChanged(int state) {
            super.onScrollStateChanged(state);
            statee = state;
        }

        @Override
        public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {

            final LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {

                @Override
                public PointF computeScrollVectorForPosition(int targetPosition) {
                    return super.computeScrollVectorForPosition(targetPosition);
                }

                @Override
                protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                    return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
                }
            };

            linearSmoothScroller.setTargetPosition(position);
            startSmoothScroll(linearSmoothScroller);
        }
    }


    public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public ImageAdapter() {
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.ivImage);
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(SlideShowActivity.this).inflate(R.layout.item_image, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            Glide.with(SlideShowActivity.this).load(data.get(position)).diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(((ViewHolder) holder).imageView);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }
}