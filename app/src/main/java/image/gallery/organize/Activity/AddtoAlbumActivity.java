package image.gallery.organize.Activity;

import static image.gallery.organize.MyApplication.folderData;

import android.app.Dialog;
import android.app.PendingIntent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import image.gallery.organize.Adhelper.BackInterAds;
import image.gallery.organize.Adhelper.ListBannerAds;
import image.gallery.organize.Fragment.FolderFragment;
import image.gallery.organize.Helper.Constant;
import image.gallery.organize.Helper.Database;
import image.gallery.organize.Helper.Utils;
import image.gallery.organize.R;
import com.github.florent37.inlineactivityresult.InlineActivityResult;
import com.github.florent37.inlineactivityresult.Result;
import com.github.florent37.inlineactivityresult.callbacks.FailCallback;
import com.github.florent37.inlineactivityresult.callbacks.SuccessCallback;
import com.github.florent37.inlineactivityresult.request.Request;
import com.github.florent37.inlineactivityresult.request.RequestFabric;
import com.google.gson.Gson;
import com.preference.PowerPreference;

import java.io.File;
import java.util.ArrayList;

public class AddtoAlbumActivity extends AppCompatActivity {

    private ArrayList<String> selectedItems = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_album);

        selectedItems = getIntent().getStringArrayListExtra("selected");
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

    private void setview() {

        findViewById(R.id.imgBack).setOnClickListener(v -> onBackPressed());
        LinearLayout llNewAlbum = findViewById(R.id.llNewAlbum);

        llNewAlbum.setOnClickListener(v -> Utils.getInstance().createAlbumPopup(this, name -> {

            File file = new File(Utils.getInstance().getImageSaveDirectory(), name);
            file.mkdirs();

            final Dialog dialog = new Dialog(this, R.style.dialog);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);

            dialog.setContentView(R.layout.dialog_confirm_copy);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            TextView txtOk = dialog.findViewById(R.id.txtOk);
            TextView txtCancel = dialog.findViewById(R.id.txtCancel);
            TextView txttitle = dialog.findViewById(R.id.txttitle);
            RadioButton radioDeleteFromOrig = dialog.findViewById(R.id.radioDeleteFromOrig);

            txttitle.setText(selectedItems.size() + getResources().getString(R.string.files_copy_to) + name);

            txtCancel.setOnClickListener(v12 -> dialog.dismiss());

            txtOk.setOnClickListener(v13 -> {
                dialog.dismiss();

                if (SplashActivity.isDataLoaded) {
                    copyPhotosToAlbum(file, radioDeleteFromOrig.isChecked(), true);
                } else {
                    Utils.getInstance().showImageLoadingDialog(AddtoAlbumActivity.this, () -> copyPhotosToAlbum(file, radioDeleteFromOrig.isChecked(), true));
                }
            });
            dialog.show();

        }));

        RecyclerView listFolder = findViewById(R.id.listFolder);
        listFolder.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        FolderAdapter adapter = new FolderAdapter();
        listFolder.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        new ListBannerAds().showBannerAds(this, null, null);
    }


    private class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.Viewholder> {

        @NonNull
        @Override
        public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_photos_to_album, parent, false);
            return new Viewholder(inflate);
        }

        @Override
        public void onBindViewHolder(@NonNull Viewholder holder, int position) {
            Object[] keys = folderData.keySet().toArray();

            String foldername = (String) keys[position];
            holder.txtFoldername.setText(foldername);

            holder.txtFolderCount.setText(folderData.get(foldername).size() + "");

            Glide.with(AddtoAlbumActivity.this).load(folderData.get(foldername).get(0)).into(holder.imgThumb);

            holder.itemView.setOnClickListener(v -> {
                File file = new File(folderData.get(foldername).get(0)).getParentFile();

                final Dialog dialog = new Dialog(AddtoAlbumActivity.this, R.style.dialog);
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);

                dialog.setContentView(R.layout.dialog_confirm_copy);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);


                TextView txtOk = dialog.findViewById(R.id.txtOk);
                TextView txtCancel = dialog.findViewById(R.id.txtCancel);
                TextView txttitle = dialog.findViewById(R.id.txttitle);
                RadioButton radioDeleteFromOrig = dialog.findViewById(R.id.radioDeleteFromOrig);

                txttitle.setText(selectedItems.size() + getResources().getString(R.string.files_copy_to) + foldername);

                txtCancel.setOnClickListener(v12 -> dialog.dismiss());

                txtOk.setOnClickListener(v13 -> {
                    dialog.dismiss();

                    if (SplashActivity.isDataLoaded) {
                        copyPhotosToAlbum(file, radioDeleteFromOrig.isChecked(), false);
                    } else {
                        Utils.getInstance().showImageLoadingDialog(AddtoAlbumActivity.this, () -> copyPhotosToAlbum(file, radioDeleteFromOrig.isChecked(), false));
                    }
                });
                dialog.show();
            });
        }

        public int getItemCount() {
            return folderData.size();
        }

        class Viewholder extends RecyclerView.ViewHolder {

            TextView txtFoldername;
            TextView txtFolderCount;
            ImageView imgThumb;

            public Viewholder(@NonNull View itemView) {
                super(itemView);

                txtFoldername = itemView.findViewById(R.id.txtFoldername);
                txtFolderCount = itemView.findViewById(R.id.txtFolderCount);
                imgThumb = itemView.findViewById(R.id.imgThumb);
            }
        }
    }

    private void copyPhotosToAlbum(File file, boolean isDelete, boolean isNewFolder) {

        final Dialog dialogProgress = new Dialog(this, R.style.dialog);

        dialogProgress.setCancelable(false);
        dialogProgress.setCanceledOnTouchOutside(false);

        dialogProgress.setContentView(R.layout.dialog_progress);
        dialogProgress.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        ProgressBar progress = dialogProgress.findViewById(R.id.progress);
        TextView txtPerc = dialogProgress.findViewById(R.id.txtPerc);
        TextView txtTotal = dialogProgress.findViewById(R.id.txtTotal);
        TextView txtCancelCopy = dialogProgress.findViewById(R.id.txtCancel);

        txtTotal.setText("0/" + selectedItems.size());
        txtPerc.setText("0%");

        progress.setMax(selectedItems.size());

        startCopyFiles task = new startCopyFiles(dialogProgress, progress, txtPerc, txtTotal, file, isDelete, isNewFolder);
        task.execute();

        txtCancelCopy.setOnClickListener(v15 -> {
            dialogProgress.dismiss();
            task.cancel(true);
        });
    }

    private class startCopyFiles extends AsyncTask<Void, Void, Void> {

        boolean isDeleteFromOrigin;
        boolean isFromNewFolder;
        Dialog dialog;
        TextView txtPercentage;
        TextView txtTotalVal;
        File file;
        ProgressBar progress;
        ArrayList<String> copiedlist = new ArrayList<>();

        public startCopyFiles(Dialog dialogProgress, ProgressBar progressBar, TextView txtPerc, TextView txtTotal, File file, boolean isDelete, boolean isnewFolder) {
            dialog = dialogProgress;
            txtPercentage = txtPerc;
            txtTotalVal = txtTotal;
            this.file = file;
            progress = progressBar;
            isDeleteFromOrigin = isDelete;
            isFromNewFolder = isnewFolder;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            runOnUiThread(() -> dialog.show());
        }

        @Override
        protected Void doInBackground(Void... voids) {

            for (int i = 0; i < selectedItems.size(); i++) {

                String path = selectedItems.get(i);
                File dest = new File(file, new File(path).getName());

                Utils.getInstance().copyImageFile(AddtoAlbumActivity.this, new File(path), dest);

                copiedlist.add(dest.getAbsolutePath());

                if (isDeleteFromOrigin && Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                    Utils.getInstance().recycleImage(path, AddtoAlbumActivity.this, false);
                    Utils.getInstance().removeImage(path, AddtoAlbumActivity.this);
                }

                Database.getInstance(AddtoAlbumActivity.this).changeAddToFav(path, dest.getAbsolutePath());

                int temp = ((i + 1) * 100);
                temp = temp / selectedItems.size();

                int finalI = i;
                int finalTemp = temp;

                Utils.getInstance().scanMedia(AddtoAlbumActivity.this, file.getAbsolutePath());

                runOnUiThread(() -> {
                    Utils.getInstance().addFiles(dest.getAbsolutePath(), AddtoAlbumActivity.this);

                    progress.setProgress(finalI);
                    txtPercentage.setText(finalTemp + "%");
                    txtTotalVal.setText(finalI + 1 + "/" + selectedItems.size());


                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            Utils.getInstance().scanMedia(AddtoAlbumActivity.this);
            if (isDeleteFromOrigin && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Utils.getInstance().showLoader(AddtoAlbumActivity.this);
                ArrayList<Uri> uris = new ArrayList<>();
                for (int i = 0; i < selectedItems.size(); i++) {
                    String path = selectedItems.get(i);

                    Utils.getInstance().recycleImage(selectedItems.get(i), AddtoAlbumActivity.this, true);
                    if (new File(selectedItems.get(i)).exists()) {
                        Uri uri = Utils.getInstance().getAppendedUri(selectedItems.get(i), AddtoAlbumActivity.this);
                        if (uri != null)
                            uris.add(uri);
                    } else {
                        Utils.getInstance().removeImage(path, AddtoAlbumActivity.this);
                    }
                }

                if (uris.size() > 0) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Utils.getInstance().dismissLoader();
                            PendingIntent intent = MediaStore.createDeleteRequest(getContentResolver(), uris);
                            Request request = RequestFabric.create(intent.getIntentSender(), null, 0, 0, 0, null);

                            new InlineActivityResult(AddtoAlbumActivity.this)
                                    .startForResult(request)
                                    .onSuccess(new SuccessCallback() {
                                        @Override
                                        public void onSuccess(Result result) {
                                            if (result.getResultCode() == RESULT_OK) {

                                                for (String path : selectedItems) {
                                                    Utils.getInstance().removeImage(path, AddtoAlbumActivity.this);
                                                }

                                                PowerPreference.getDefaultFile().putString(Constant.recycleList, new Gson().toJson(new ArrayList<String>()));
                                                if (isFromNewFolder)
                                                    folderData.put(file.getName(), copiedlist);
                                                Utils.getInstance().showSuccess(AddtoAlbumActivity.this, getResources().getString(R.string.copied) + selectedItems.size() + getResources().getString(R.string.items));
                                                FolderFragment.adapter.addData(folderData);
                                                dialog.dismiss();
                                                if (!getIntent().hasExtra("isFromView")) {
                                                    MainActivity.activity.onBackPressed();
                                                }
                                                finish();
                                            } else {
                                                Utils.getInstance().clearRecycledImage(AddtoAlbumActivity.this);
                                                if (isFromNewFolder)
                                                    folderData.put(file.getName(), copiedlist);
                                                Utils.getInstance().showSuccess(AddtoAlbumActivity.this, getResources().getString(R.string.copied) + selectedItems.size() + getResources().getString(R.string.items));
                                                FolderFragment.adapter.addData(folderData);
                                                dialog.dismiss();
                                                if (!getIntent().hasExtra("isFromView")) {
                                                    MainActivity.activity.onBackPressed();
                                                }
                                                finish();
                                            }

                                        }
                                    }).onFail(new FailCallback() {
                                        @Override
                                        public void onFailed(Result result) {
                                            Utils.getInstance().clearRecycledImage(AddtoAlbumActivity.this);
                                            if (isFromNewFolder)
                                                folderData.put(file.getName(), copiedlist);
                                            Utils.getInstance().showSuccess(AddtoAlbumActivity.this, getResources().getString(R.string.copied) + selectedItems.size() + getResources().getString(R.string.items));
                                            FolderFragment.adapter.addData(folderData);
                                            dialog.dismiss();
                                            if (!getIntent().hasExtra("isFromView")) {
                                                MainActivity.activity.onBackPressed();
                                            }
                                            finish();
                                        }
                                    });
                        }
                    });
                } else {
                    PowerPreference.getDefaultFile().putString(Constant.recycleList, new Gson().toJson(new ArrayList<String>()));
                    runOnUiThread(() -> {
                        Utils.getInstance().dismissLoader();
                        if (isFromNewFolder)
                            folderData.put(file.getName(), copiedlist);
                        Utils.getInstance().showSuccess(AddtoAlbumActivity.this, getResources().getString(R.string.copied) + selectedItems.size() + getResources().getString(R.string.items));
                        FolderFragment.adapter.addData(folderData);
                        dialog.dismiss();
                        if (!getIntent().hasExtra("isFromView")) {
                            MainActivity.activity.onBackPressed();
                        }
                        finish();
                    });
                }

            } else {
                runOnUiThread(() -> {
                    if (isFromNewFolder)
                        folderData.put(file.getName(), copiedlist);
                    Utils.getInstance().showSuccess(AddtoAlbumActivity.this, getResources().getString(R.string.copied) + selectedItems.size() + getResources().getString(R.string.items));
                    FolderFragment.adapter.addData(folderData);
                    dialog.dismiss();
                    if (getIntent().getBooleanExtra("isFromView",false)) {
                        ViewImageActivity.activity.onBackPressed();
                    }
                    finish();
                });
            }
        }
    }
}
