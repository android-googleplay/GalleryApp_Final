package image.gallery.organize.Fragment;


import static image.gallery.organize.MyApplication.allimages;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import image.gallery.organize.Activity.MainActivity;
import image.gallery.organize.Activity.SplashActivity;
import image.gallery.organize.Adapter.AllPhotosAdapter;
import image.gallery.organize.Helper.GridRecyclerView;
import image.gallery.organize.R;
import com.preference.PowerPreference;

import java.util.ArrayList;

public class AllPhotosFragment extends Fragment implements SplashActivity.DataLoad {

    long DURATION = 500;
    private boolean on_attach = true;

    private LottieAnimationView loader;
    private GridRecyclerView stickyList;
    public static AllPhotosAdapter mAdapter;
    Context context;

    public static LinearLayout llNoData;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context=context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_all_photos, null, false);

        setview(view);

        SplashActivity.dataLoadListener = this;
        return view;
    }

    private void setview(View view) {


        stickyList = view.findViewById(R.id.listPhotos);
        loader = view.findViewById(R.id.loader);
        llNoData = view.findViewById(R.id.llNoDataFound);

        GridLayoutManager manager = new GridLayoutManager(requireActivity(), 4);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mAdapter.isStickyHeader(position)) {
                    return 4;
                }
                return 1;
            }
        });
       /* StickyHeadersGridLayoutManager<AllPhotosAdapter> layoutManager = new StickyHeadersGridLayoutManager<>(requireContext(), 4);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {

            }
        });*/

        stickyList.setLayoutManager(manager);
        loader.setVisibility(View.GONE);
        stickyList.setVisibility(View.VISIBLE);

        ArrayList<String> data = new ArrayList<>();
        data.addAll(allimages);
        mAdapter = new AllPhotosAdapter((MainActivity)context, data);
        stickyList.setAdapter(mAdapter);
        stickyList.scheduleLayoutAnimation();
        stickyList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!SplashActivity.isDataLoaded) {
                        mAdapter.adddata(allimages);
                    } else {
                        if (!PowerPreference.getDefaultFile().getBoolean("isDataLoaded", false)) {
                            PowerPreference.getDefaultFile().putBoolean("isDataLoaded", true);
                            mAdapter.adddata(allimages);
                        }
                    }
                }
            }
        });

        refresh();

    }

    public static void refresh() {
        if (mAdapter.getItemCount() > 0) {
            llNoData.setVisibility(View.GONE);
        } else {
            llNoData.setVisibility(View.VISIBLE);
        }
    }

    private void reloadData() {

        if (SplashActivity.isDataLoaded) {
            loader.setVisibility(View.GONE);
            stickyList.setVisibility(View.VISIBLE);

            stickyList.setAdapter(mAdapter);

        } else {
            loader.setVisibility(View.VISIBLE);
            stickyList.setVisibility(View.GONE);
        }

        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDataLoaded() {
        if (getActivity() != null)
            getActivity().runOnUiThread(this::reloadData);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mAdapter != null) {
            mAdapter.adddata(allimages);
            mAdapter.notifyDataSetChanged();
        }

    }
}
