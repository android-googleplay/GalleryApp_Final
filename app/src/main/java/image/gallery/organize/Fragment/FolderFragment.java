package image.gallery.organize.Fragment;

import static android.app.Activity.RESULT_OK;
import static image.gallery.organize.Activity.MainActivity.activity;
import static image.gallery.organize.Activity.MainActivity.txtSelectedTitle;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import image.gallery.organize.Activity.MainActivity;
import image.gallery.organize.Activity.ShowDetailsActivity;
import image.gallery.organize.Activity.SplashActivity;
import image.gallery.organize.Activity.ViewFolderImagesActivity;
import image.gallery.organize.Adhelper.InterAds;
import image.gallery.organize.Adhelper.ListNative2Ads;
import image.gallery.organize.Helper.Constant;
import image.gallery.organize.Helper.Database;
import image.gallery.organize.Helper.GridRecyclerView;
import image.gallery.organize.Helper.Utils;
import image.gallery.organize.MyApplication;
import image.gallery.organize.R;
import image.gallery.organize.library.ViewAnimator.ViewAnimator;

import com.github.florent37.inlineactivityresult.InlineActivityResult;
import com.github.florent37.inlineactivityresult.Result;
import com.github.florent37.inlineactivityresult.callbacks.FailCallback;
import com.github.florent37.inlineactivityresult.callbacks.SuccessCallback;
import com.github.florent37.inlineactivityresult.request.Request;
import com.github.florent37.inlineactivityresult.request.RequestFabric;
import com.google.gson.Gson;
import com.preference.PowerPreference;

import org.apache.commons.collections4.map.ListOrderedMap;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class FolderFragment extends Fragment {

    public static AlbumAdapter adapter;
    public static DefaultFolderAdapter adapterDefault;
    public HashMap<String, ArrayList<String>> foldercopy = new HashMap<>();

    Dialog mLoadingDialog;
    public ListOrderedMap<String, ArrayList<String>> datafolder = new ListOrderedMap<>();
    public ListOrderedMap<String, ArrayList<String>> datadefault = new ListOrderedMap<>();

    public static boolean isSelectionEnable = false;
    private ArrayList<String> selectedItems = new ArrayList<>();

    public static TextView txtAlbums;
    Context context;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_album, null, false);
        setview(view);
        return view;
    }

    private void setview(View view) {

        GridRecyclerView rvDefaultFolder = view.findViewById(R.id.rvDefaultFolder);
        GridRecyclerView rvAlbum = view.findViewById(R.id.rvAlbum);
        txtAlbums = view.findViewById(R.id.txtAlbum);

        GridLayoutManager manager = new GridLayoutManager(requireActivity(), 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (adapter.getItemViewType(position) == 1 || adapter.getItemViewType(position) == 191) {
                    return 2;
                }
                return 1;
            }
        });

        rvAlbum.setLayoutManager(manager);
        rvDefaultFolder.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        adapter = new AlbumAdapter();
        rvAlbum.setAdapter(adapter);

        adapterDefault = new DefaultFolderAdapter();
        rvDefaultFolder.setAdapter(adapterDefault);

        adapterDefault.addData(MyApplication.DefaultFolderData);
        adapter.addData(MyApplication.folderData);

        rvAlbum.scheduleLayoutAnimation();
        rvDefaultFolder.scheduleLayoutAnimation();

        ImageView imgMoreAlbum = view.findViewById(R.id.imgMoreAlbum);
        imgMoreAlbum.setOnClickListener(v -> Utils.getInstance().createAlbumPopup(requireActivity(), name -> {
            File file = new File(Utils.getInstance().getImageSaveDirectory(), name);
            file.mkdirs();

            MyApplication.folderData.put(name, new ArrayList<>());

            FolderFragment.adapter.addData(datafolder);

        }));

        rvDefaultFolder.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!SplashActivity.isFolderLoaded) {
                        adapterDefault.addData(MyApplication.DefaultFolderData);
                    } else {
                        if (!PowerPreference.getDefaultFile().getBoolean("isFolderLoaded", false)) {
                            PowerPreference.getDefaultFile().putBoolean("isFolderLoaded", true);
                            adapterDefault.addData(MyApplication.DefaultFolderData);
                        }
                    }
                }
            }
        });

        rvAlbum.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!SplashActivity.isFolderLoaded) {
                        adapter.addData(MyApplication.folderData);
                    } else {
                        if (!PowerPreference.getDefaultFile().getBoolean("isDFolderLoaded", false)) {
                            PowerPreference.getDefaultFile().putBoolean("isDFolderLoaded", true);
                            adapter.addData(MyApplication.folderData);
                        }
                    }
                }
            }
        });


        refresh();
        // rvAlbum.setItemViewCacheSize(50);
        // rvDefaultFolder.setItemViewCacheSize(50);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (adapter != null)
            adapter.notifyDataSetChanged();

        if (adapterDefault != null)
            adapterDefault.notifyDataSetChanged();


        refresh();
    }


    public class AlbumAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        HashMap<Integer, Integer> params = new HashMap<>();

        long DURATION = 100;
        private boolean on_attach = true;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folder, parent, false);
            return new Viewholder(inflate);
        }


        @Override
        public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    Log.d("TAG", "onScrollStateChanged: Called " + newState);
                    on_attach = false;
                    super.onScrollStateChanged(recyclerView, newState);
                }
            });

            super.onAttachedToRecyclerView(recyclerView);
        }

        public void addData(ListOrderedMap<String, ArrayList<String>> data) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {

                    datafolder.clear();
                    datafolder.putAll(data);

                    notifyDataSetChanged();
                });
            }
        }

        private void setAnimation(View itemView, int i) {
            if (!params.containsKey(i)) {
                if (!on_attach) {
                    i = -1;
                }
                params.put(i, i);
                boolean isNotFirstItem = i == -1;
                i++;

                itemView.setScaleX(0.f);
                itemView.setScaleY(0.f);

                ViewAnimator.animate(itemView)
                        .scale(0.f, 1.0f)
                        .startDelay(isNotFirstItem ? DURATION / 2 : (i * DURATION / 3))
                        .duration(100)
                        .start();
            }
        }


        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holderMain, int position) {


            Viewholder holder = (Viewholder) holderMain;

            try {
                ViewTreeObserver vto = holder.rlMain.getViewTreeObserver();
                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        holder.rlMain.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        int width = holder.rlMain.getMeasuredWidth();
                        holder.rlMain.setLayoutParams(new FrameLayout.LayoutParams(width, width));
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }


            Object[] keys = datafolder.keySet().toArray();

            String foldername = (String) keys[position];
            holder.txtFoldername.setText(foldername);

            holder.txtFolderCount.setText(datafolder.get(foldername).size() + " Photos");
            Glide.with(requireActivity()).load(datafolder.get(foldername).get(0)).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.imgThumb);

            if (isSelectionEnable) {
                holder.imgSelection.setVisibility(View.VISIBLE);
                if (selectedItems.contains(foldername)) {
                    holder.imgSelection.setImageResource(R.drawable.ic_check);
                } else {
                    holder.imgSelection.setImageResource(R.drawable.ic_allselecticon);
                }
            } else {
                holder.imgSelection.setVisibility(View.GONE);
            }

            holder.itemView.setOnClickListener(v -> {
                if (isSelectionEnable) {

                    if (selectedItems.contains(foldername)) {
                        selectedItems.remove(foldername);
                    } else {
                        selectedItems.add(foldername);
                    }

                    if (selectedItems.size() > 1) {
                        MainActivity.llShare.setAlpha(0.4f);
                        MainActivity.llMore.setAlpha(0.4f);
                    } else {
                        MainActivity.llShare.setAlpha(1);
                        MainActivity.llMore.setAlpha(1);
                    }

                    notifyItemChanged(position);
                    txtSelectedTitle.setText(selectedItems.size() + getResources().getString(R.string.selected));

                } else {

                    new InterAds().showInterAds(getActivity(), new InterAds.OnAdClosedListener() {
                        @Override
                        public void onAdClosed() {
                            startActivity(new Intent(getActivity(), ViewFolderImagesActivity.class).
                                    putExtra("name", new File(datafolder.get(foldername).get(0)).getParentFile().getAbsolutePath()));
                        }
                    });
                }
            });

            holder.itemView.setOnLongClickListener(v -> {

                if (!isSelectionEnable) {
                    enableEdit();

                    selectedItems.add(foldername);
                    notifyDataSetChanged();

                    txtSelectedTitle.setText(selectedItems.size() + getResources().getString(R.string.selected));

                }

                return false;
            });
        }

        @Override
        public int getItemCount() {
            return datafolder.size();
        }

        class Viewholder extends RecyclerView.ViewHolder {

            TextView txtFoldername;
            TextView txtFolderCount;
            ImageView imgThumb;
            ImageView imgSelection;
            RelativeLayout rlMain;

            public Viewholder(@NonNull View itemView) {
                super(itemView);

                txtFoldername = itemView.findViewById(R.id.txtFoldername);
                txtFolderCount = itemView.findViewById(R.id.txtFolderCount);
                imgSelection = itemView.findViewById(R.id.imgSelection);
                imgThumb = itemView.findViewById(R.id.imgThumb);
                rlMain = itemView.findViewById(R.id.rlMain);
            }
        }

        public void enableEdit() {
            selectedItems.clear();
            isSelectionEnable = true;

            foldercopy.clear();
            foldercopy.putAll(datafolder);

            MainActivity.imgselectall.setVisibility(View.VISIBLE);

            txtSelectedTitle.setText(selectedItems.size() + getResources().getString(R.string.selected));

            MainActivity.setupFolderEditOption();

            MainActivity.relSelection.setVisibility(View.VISIBLE);
            MainActivity.llSelectedOptions.setVisibility(View.VISIBLE);
            MainActivity.relActionbarMain.setVisibility(View.GONE);
            MainActivity.llHideOptions.setVisibility(View.VISIBLE);

            MainActivity.tabLayout.setVisibility(View.GONE);
            MainActivity.viewPager.setPagingEnabled(false);

            adapter.notifyDataSetChanged();
            adapterDefault.notifyDataSetChanged();
        }

        public void disableEdit() {
            selectedItems.clear();

            notifyDataSetChanged();
            adapterDefault.notifyDataSetChanged();

            MainActivity.activity.onBackPressed();
        }

        public void hidePhotos() {
            if (selectedItems.size() > 0) {

                ArrayList<String> paths = new ArrayList<>();

                for (int i = 0; i < selectedItems.size(); i++) {
                    paths.addAll(datafolder.get(selectedItems.get(i)));
                }

                Utils.getInstance().hidePhotos(paths, (MainActivity) context, true, () -> {
                    selectedItems.clear();

                    disableEdit();

                    notifyDataSetChanged();
                    adapterDefault.notifyDataSetChanged();
                });

            } else {

                Utils.getInstance().showWarning(getContext(), getResources().getString(R.string.select_albums));
            }
        }

        public void selectDeselctAll(boolean isBack) {
            if (isBack || selectedItems.size() == datafolder.size()) {
                selectedItems.clear();
            } else {
                selectedItems.clear();
                selectedItems.addAll(new ArrayList<>(datafolder.keySet()));
            }

            txtSelectedTitle.setText(selectedItems.size() + getResources().getString(R.string.selected));

            notifyDataSetChanged();
            adapterDefault.notifyDataSetChanged();
        }

        public void deletePhotos() {

            if (SplashActivity.isDataLoaded) {
                if (selectedItems.size() > 0) {

                    Constant.showBottomSheetDialog(context, new Constant.onItemClick() {
                        @Override
                        public void onSuccess() {
                            showLoader(getActivity());
                            new Handler(Looper.getMainLooper())
                                    .postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            removeImages();
                                        }
                                    }, 1000);
                        }

                        @Override
                        public void onFailed() {

                        }
                    });
                   /* AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Delete");
                    builder.setMessage("Are you sure to delete this Album?");
                    builder.setPositiveButton("OK", (dialog, which) -> {


                    });

                    builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss());
                    AlertDialog dialog = builder.create();
                    dialog.show();*/
                } else {
                    Utils.getInstance().showWarning(getContext(), getResources().getString(R.string.select_albums));
                }
            } else {
                Utils.getInstance().showImageLoadingDialog(getActivity(), () -> {
                    deletePhotos();
                });
            }
        }


        public void removeImages() {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        ArrayList<String> datatemp = new ArrayList<>();
                        for (String foldername : selectedItems) {
                            datatemp.addAll(datafolder.get(foldername));
                        }

                        ArrayList<Uri> uris = new ArrayList<>();
                        for (int i = 0; i < datatemp.size(); i++) {
                            String path = datatemp.get(i);

                            Utils.getInstance().recycleImage(datatemp.get(i), (MainActivity) context, true);
                            if (new File(datatemp.get(i)).exists()) {
                                Uri uri = Utils.getInstance().getAppendedUri(datatemp.get(i), (MainActivity) context);
                                if (uri != null)
                                    uris.add(uri);
                            } else {
                                Utils.getInstance().removeImage(path, context);
                            }
                        }

                        if (uris.size() > 0) {

                            ((MainActivity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dismissLoader();

                                    PendingIntent intent = MediaStore.createDeleteRequest(((MainActivity) context).getContentResolver(), uris);
                                    Request request = RequestFabric.create(intent.getIntentSender(), null, 0, 0, 0, null);

                                    new InlineActivityResult(((MainActivity) context))
                                            .startForResult(request)
                                            .onSuccess(new SuccessCallback() {
                                                @Override
                                                public void onSuccess(Result result) {
                                                    showLoader(((MainActivity) context));
                                                    if (result.getResultCode() == RESULT_OK) {

                                                        ArrayList<String> datatemp = new ArrayList<>();
                                                        for (String foldername : selectedItems) {
                                                            datatemp.addAll(datafolder.get(foldername));
                                                        }

                                                        for (String path : datatemp) {
                                                            Utils.getInstance().removeImage(path, getActivity());
                                                        }

                                                        PowerPreference.getDefaultFile().putString(Constant.recycleList, new Gson().toJson(new ArrayList<String>()));
                                                        deleteComplete();
                                                    } else {
                                                        Utils.getInstance().clearRecycledImage(((MainActivity) context));
                                                        dismissLoader();
                                                    }

                                                }
                                            }).onFail(new FailCallback() {
                                                @Override
                                                public void onFailed(Result result) {
                                                    Utils.getInstance().clearRecycledImage(((MainActivity) context));
                                                    dismissLoader();
                                                }
                                            });
                                }
                            });
                        } else {
                            PowerPreference.getDefaultFile().putString(Constant.recycleList, new Gson().toJson(new ArrayList<String>()));
                            deleteComplete();
                        }
                    } else {

                        ArrayList<String> datatemp = new ArrayList<>();
                        for (String foldername : selectedItems) {
                            datatemp.addAll(datafolder.get(foldername));
                        }

                        ArrayList<File> folderpath = new ArrayList<>();

                        for (String path : datatemp) {

                            Utils.getInstance().recycleImage(path, getContext(), false);
                            Utils.getInstance().removeImage(path, getContext());
                            folderpath.add(new File(path).getParentFile());
                        }

                        for (File file : folderpath) {
                            Utils.getInstance().deleteFile(context, file.getAbsolutePath());
                        }

                        deleteComplete();
                    }
                }
            }).start();
        }

        public void deleteComplete() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Utils.getInstance().showSuccess(((MainActivity) context), selectedItems.size() + context.getResources().getString(R.string.items_deleted));
                    dismissLoader();
                    disableEdit();
                }
            });
        }

        public void renameFolder() {
            if (selectedItems.size() > 0) {
                if (selectedItems.size() == 1) {
                    Utils.getInstance().renameAlbumPopup(getActivity(), new File(selectedItems.get(0)).getName(), name -> {

                        File oldFolder = new File(datafolder.get(selectedItems.get(0)).get(0)).getParentFile();
                        File newFolder = new File(oldFolder.getParentFile(), name);

                        boolean temp = oldFolder.renameTo(newFolder);

                        if (temp) {

                            int index = datafolder.indexOf(oldFolder.getName());

                            MyApplication.folderData.put(index, name, datafolder.get(oldFolder.getName()));
                            datafolder.put(index, name, datafolder.get(oldFolder.getName()));

                            datafolder.remove(oldFolder.getName());
                            MyApplication.folderData.remove(oldFolder.getName());

                            notifyItemChanged(index);
                            adapter.notifyDataSetChanged();



                        /*    int index = datafolder.indexOf(oldFolder.getName());

                            MyApplication.folderData.put(index, name, datafolder.get(oldFolder.getName()));

                            datafolder.put(index, name, datafolder.get(oldFolder.getName()));

                            datafolder.remove(oldFolder.getName());*/

                            //  notifyItemChanged(index);

                            disableEdit();
                            Utils.getInstance().scanMedia(getContext(), newFolder.getAbsolutePath());
                        }
                    });
                }
            } else {
                Utils.getInstance().showWarning(getContext(), getResources().getString(R.string.select_albums));
            }
        }

        public void details() {
            if (selectedItems.size() > 0) {
                if (selectedItems.size() == 1) {

                    new InterAds().showInterAds(getActivity(), new InterAds.OnAdClosedListener() {
                        @Override
                        public void onAdClosed() {
                            startActivity(new Intent(getActivity(), ShowDetailsActivity.class).putExtra("path", selectedItems.get(0)).putExtra("isFromFolder", true));

                        }
                    });
                    disableEdit();
                }
            } else {

                Utils.getInstance().showWarning(getContext(), getResources().getString(R.string.select_albums));
            }
        }
    }

    public static void refresh() {
        if (adapter.getItemCount() > 0) {
            txtAlbums.setVisibility(View.VISIBLE);
        } else {
            txtAlbums.setVisibility(View.GONE);
        }
    }


    public void dismissLoader() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing())
            mLoadingDialog.dismiss();
    }

    private void showLoader(Context mActivity) {

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

    public class DefaultFolderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


        private static final int HEADER_ITEM = 3;
        private static final int SUB_ITEM = 1;
        private static final int AD_ITEM = 191;

        long DURATION = 100;
        private boolean on_attach = true;
        HashMap<Integer, Integer> params = new HashMap<>();

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == AD_ITEM) {
                View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_ad_folder, parent, false);
                return new AdViewholder(inflate);
            } else {
                View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folder, parent, false);
                return new Viewholder(inflate);
            }

        }

        @Override
        public int getItemViewType(int position) {
            if (datadefault.size() - 1 == position) {
                return AD_ITEM;
            } else {
                return SUB_ITEM;
            }
        }


        @Override
        public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    Log.d("TAG", "onScrollStateChanged: Called " + newState);
                    on_attach = false;
                    super.onScrollStateChanged(recyclerView, newState);
                }
            });

            super.onAttachedToRecyclerView(recyclerView);
        }

        private void setAnimation(View itemView, int i) {
            if (!params.containsKey(i)) {
                if (!on_attach) {
                    i = -1;
                }
                params.put(i, i);
                boolean isNotFirstItem = i == -1;
                i++;

                itemView.setScaleX(0.f);
                itemView.setScaleY(0.f);

                ViewAnimator.animate(itemView)
                        .scale(0.f, 1.0f)
                        .startDelay(isNotFirstItem ? DURATION / 2 : (i * DURATION / 3))
                        .duration(100)
                        .start();
            }
        }


        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewholder, int position) {

            if (viewholder instanceof AdViewholder) {
                new ListNative2Ads().showListNativeAds(activity, ((AdViewholder) viewholder).frameLayout, null);
            } else {

                Viewholder holder = (Viewholder) viewholder;

                try {
                    ViewTreeObserver vto = holder.rlMain.getViewTreeObserver();
                    vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            holder.rlMain.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            int width = holder.rlMain.getMeasuredWidth();
                            holder.rlMain.setLayoutParams(new FrameLayout.LayoutParams(width, width));
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }


                Object[] keys = datadefault.keySet().toArray();
                String foldername = (String) keys[position];

                holder.txtFoldername.setText(foldername);

                int pos = datadefault.size() - 1;

                if (position != datadefault.size() - 2) {

                    holder.imgThumb.setVisibility(View.VISIBLE);
                    holder.imgFav.setVisibility(View.GONE);

                    holder.txtFolderCount.setText(datadefault.get(foldername).size() + " Photos");
                    try {
                        Glide.with(requireActivity()).load(datadefault.get(foldername).get(0)).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.imgThumb);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (isSelectionEnable) {

                        if (selectedItems.contains(foldername)) {
                            holder.imgSelection.setImageResource(R.drawable.ic_check);
                        } else {
                            holder.imgSelection.setImageResource(R.drawable.ic_allselecticon);
                        }
                    } else {
                        holder.imgSelection.setVisibility(View.GONE);
                    }

                    holder.itemView.setOnClickListener(v -> {

                        if (isSelectionEnable) {
                            if (selectedItems.contains(foldername)) {
                                selectedItems.remove(foldername);

                                if (selectedItems.size() <= 1) {
                                    MainActivity.llShare.setAlpha(1);
                                }
                            } else {
                                selectedItems.add(foldername);
                            }

                            if (selectedItems.size() > 1) {
                                MainActivity.llShare.setAlpha(0.4f);
                                MainActivity.llMore.setAlpha(0.4f);
                            } else {
                                if (selectedItems.size() > 0) {
                                    if (datadefault.containsKey(selectedItems.get(0))) {
                                        MainActivity.llShare.setAlpha(0.4f);
                                    }
                                } else {
                                    MainActivity.llShare.setAlpha(1);
                                }

                                MainActivity.llMore.setAlpha(1);
                            }

                            notifyItemChanged(position);
                            txtSelectedTitle.setText(selectedItems.size() + getResources().getString(R.string.selected));
                        } else {
                            new InterAds().showInterAds(getActivity(), new InterAds.OnAdClosedListener() {
                                @Override
                                public void onAdClosed() {
                                    startActivity(new Intent(getActivity(), ViewFolderImagesActivity.class).
                                            putExtra("name", new File(datadefault.get(foldername).get(0)).getParentFile().getAbsolutePath()));

                                }
                            });
                        }
                    });

                    holder.itemView.setOnLongClickListener(v -> {
                        adapter.enableEdit();
                        return false;
                    });

                } else {
                    holder.imgThumb.setVisibility(View.GONE);
                    holder.imgFav.setVisibility(View.VISIBLE);

                    holder.imgSelection.setVisibility(View.GONE);

                    holder.txtFolderCount.setText(Database.getInstance(getContext()).getFavouriteListWithoutDate().size() + "");

                    holder.itemView.setOnClickListener(v -> {
                        new InterAds().showInterAds(getActivity(), new InterAds.OnAdClosedListener() {
                            @Override
                            public void onAdClosed() {
                                startActivity(new Intent(getActivity(), ViewFolderImagesActivity.class));

                            }
                        });
                    });
                }
            }
        }

        public void addData(ListOrderedMap<String, ArrayList<String>> data) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    datadefault.clear();
                    datadefault.putAll(data);
                    datadefault.put("myask19991", new ArrayList<String>());
                    notifyDataSetChanged();
                });
            }
        }

        @Override
        public int getItemCount() {
            return datadefault.size();
        }

        class Viewholder extends RecyclerView.ViewHolder {

            TextView txtFoldername;
            TextView txtFolderCount;
            ImageView imgThumb;
            ImageView imgSelection;
            ImageView imgFav;
            RelativeLayout rlMain;

            public Viewholder(@NonNull View itemView) {
                super(itemView);

                txtFoldername = itemView.findViewById(R.id.txtFoldername);
                txtFolderCount = itemView.findViewById(R.id.txtFolderCount);
                imgThumb = itemView.findViewById(R.id.imgThumb);
                imgFav = itemView.findViewById(R.id.imgFav);
                imgSelection = itemView.findViewById(R.id.imgSelection);
                rlMain = itemView.findViewById(R.id.rlMain);
            }
        }

        class AdViewholder extends RecyclerView.ViewHolder {
            FrameLayout frameLayout;

            public AdViewholder(@NonNull View itemView) {
                super(itemView);

                frameLayout = itemView.findViewById(R.id.adFolderLarge);

            }
        }
    }
}
