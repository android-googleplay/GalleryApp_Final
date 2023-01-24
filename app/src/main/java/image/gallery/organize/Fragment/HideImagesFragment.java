package image.gallery.organize.Fragment;


import static image.gallery.organize.Activity.MainActivity.imgUnlock;
import static image.gallery.organize.Activity.MainActivity.imgselectall;
import static image.gallery.organize.Activity.MainActivity.llSelectedOptions;
import static image.gallery.organize.Activity.MainActivity.relSelection;
import static image.gallery.organize.Activity.MainActivity.txtSelectedTitle;
import static image.gallery.organize.MyApplication.HiddenImages;
import static image.gallery.organize.MyApplication.allimages;
import static image.gallery.organize.MyApplication.isEnteredPwd;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import image.gallery.organize.Activity.MainActivity;
import image.gallery.organize.Activity.SplashActivity;
import image.gallery.organize.Activity.ViewImageActivity;
import image.gallery.organize.Adhelper.InterAds;
import image.gallery.organize.Adhelper.LargeNativeAds;
import image.gallery.organize.Helper.GridRecyclerView;
import image.gallery.organize.Helper.Utils;
import image.gallery.organize.MyApplication;
import image.gallery.organize.R;
import image.gallery.organize.library.ViewAnimator.ViewAnimator;
import image.gallery.organize.stickyheader.StickyHeaders;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class HideImagesFragment extends Fragment {

    public static HiddenPhotosAdapter mAdapter;

    Context context;
    private boolean isResetPwd;
    private boolean isEnterAgain = true;
    public static boolean isSelectionEnable = false;

    private LinearLayout llShowPassword;
    public static LinearLayout llNoDataFound;
    private LinearLayout llSetSecurity;
    private LinearLayout llSetSecurityQue;

    public static ArrayList<String> list = new ArrayList<>();

    public static GridRecyclerView stickyList;

    private TextView txt1;
    private TextView txt2;
    private TextView txt3;
    private TextView txt4;
    private TextView txtincorrect;
    private TextView txtTitlePwd;

    private String enterpwd;
    private int currentpos = 1;
    private int currentStep = 0;
    private int width;

    private View view;

    private ArrayList<String> selectedItems = new ArrayList<>();
    private EditText editAns;
    Dialog mLoadingDialog;

    boolean isForgot = false;
    MaterialSpinner spinner;

    String[] strings;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = getLayoutInflater().inflate(R.layout.activity_hide_images, null, false);

        strings = new String[]{getResources().getString(R.string.select_your_question)
                ,getResources().getString(R.string.what_s_your_father_s_name)
                ,getResources().getString(R.string.what_s_your_mother_s_name)
                ,getResources().getString(R.string.what_s_your_favourite_movie)
                ,getResources().getString(R.string.what_s_your_pets_name)
                ,getResources().getString(R.string.what_s_your_dream_job)
                ,getResources().getString(R.string.in_which_city_did_your_parents_meet)};
        setview(view);

        return view;
    }

    private void setview(View view) {

        spinner = view.findViewById(R.id.spinnerQuestions);
        spinner.setItems(strings);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;

        view.findViewById(R.id.cardAction).setVisibility(View.GONE);

        llSelectedOptions.setVisibility(View.GONE);
        relSelection.setVisibility(View.GONE);

        mAdapter = new HiddenPhotosAdapter(getActivity());

        txtTitlePwd = view.findViewById(R.id.txtTitlePwd);
        llSetSecurity = view.findViewById(R.id.llSetSecurity);
        llSetSecurityQue = view.findViewById(R.id.llSetSecurityQue);

        TextView txtForgotPassword = view.findViewById(R.id.txtForgotPassword);

        String htmlString = getResources().getString(R.string.forgot_pass);
        txtForgotPassword.setText(Html.fromHtml(htmlString));

        txtForgotPassword.setVisibility(View.INVISIBLE);

        llShowPassword = view.findViewById(R.id.llShowPassword);
        llSetSecurityQue.setX(width);
        llSetSecurityQue.setVisibility(View.VISIBLE);
        llNoDataFound = view.findViewById(R.id.llNoDataFound);

        llNoDataFound.setVisibility(View.GONE);

        stickyList = view.findViewById(R.id.listPhotos);
        stickyList.setVisibility(View.GONE);

        if (Utils.getInstance().isPasswordSetupDone(getContext())) {
            isResetPwd = false;

            if (isEnteredPwd) {
                passwordMatch();
            } else {
                llShowPassword.setVisibility(View.VISIBLE);
                llSetSecurity.setVisibility(View.GONE);
                llNoDataFound.setVisibility(View.GONE);
                llShowPassword.setX(0);
                txtForgotPassword.setVisibility(View.VISIBLE);
            }
        } else {

            isResetPwd = true;
            llShowPassword.setVisibility(View.GONE);
            llSetSecurity.setVisibility(View.VISIBLE);
            llNoDataFound.setVisibility(View.GONE);

            new LargeNativeAds().showNativeAds(getActivity(), null,view.findViewById(R.id.adFrameLarge), view.findViewById(R.id.adSpaceLarge));
             llShowPassword.setX(width);
        }

        LinearLayout llOne = view.findViewById(R.id.llOne);
        LinearLayout llTwo = view.findViewById(R.id.llTwo);
        LinearLayout llThree = view.findViewById(R.id.llThree);
        LinearLayout llFour = view.findViewById(R.id.llFour);
        LinearLayout llFive = view.findViewById(R.id.llFive);
        LinearLayout llSix = view.findViewById(R.id.llSix);
        LinearLayout llSeven = view.findViewById(R.id.llSeven);
        LinearLayout llEight = view.findViewById(R.id.llEight);
        LinearLayout llNine = view.findViewById(R.id.llNine);
        LinearLayout llZero = view.findViewById(R.id.llZero);
        LinearLayout llClear = view.findViewById(R.id.llClear);

        txt1 = view.findViewById(R.id.txt1);
        txt2 = view.findViewById(R.id.txt2);
        txt3 = view.findViewById(R.id.txt3);
        txt4 = view.findViewById(R.id.txt4);
        txtincorrect = view.findViewById(R.id.txtincorrect);
        TextView txtincorrectAns = view.findViewById(R.id.txtincorrectAns);
        TextView txtSubmitAns = view.findViewById(R.id.txtSubmitAns);

        editAns = view.findViewById(R.id.editAns);


        txtForgotPassword.setOnClickListener(view14 -> {

            llSetSecurityQue.animate().setDuration(300).translationX(0).start();
            llShowPassword.animate().setDuration(300).translationX(width).start();

            spinner.setSelectedIndex(Utils.getInstance().getSecurityQuesInt(getContext()));
            isForgot = true;

        });

       /* relQue.setOnClickListener(view12 -> {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            View popupView = layoutInflater.inflate(R.layout.popup_security_question, null);

            PopupWindow infoPopup = new PopupWindow(popupView, (int) getResources().getDimension(R.dimen._250sdp), ViewGroup.LayoutParams.WRAP_CONTENT);
            infoPopup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            infoPopup.setOutsideTouchable(true);

            TextView txtQue1 = popupView.findViewById(R.id.txtQue1);
            TextView txtQue2 = popupView.findViewById(R.id.txtQue2);
            TextView txtQue3 = popupView.findViewById(R.id.txtQue3);
            TextView txtQue4 = popupView.findViewById(R.id.txtQue4);
            TextView txtQue5 = popupView.findViewById(R.id.txtQue5);
            TextView txtQue6 = popupView.findViewById(R.id.txtQue6);

            txtQue1.setOnClickListener(view1 -> {
                txtQuestion.setText(txtQue1.getText());
                infoPopup.dismiss();
            });

            txtQue2.setOnClickListener(view1 -> {
                txtQuestion.setText(txtQue2.getText());
                infoPopup.dismiss();
            });

            txtQue3.setOnClickListener(view1 -> {
                txtQuestion.setText(txtQue3.getText());
                infoPopup.dismiss();
            });

            txtQue4.setOnClickListener(view1 -> {
                txtQuestion.setText(txtQue4.getText());
                infoPopup.dismiss();
            });

            txtQue5.setOnClickListener(view1 -> {
                txtQuestion.setText(txtQue5.getText());
                infoPopup.dismiss();
            });

            txtQue6.setOnClickListener(view1 -> {
                txtQuestion.setText(txtQue6.getText());
                infoPopup.dismiss();
            });

            Point size1 = new Point();
            getActivity().getWindowManager().getDefaultDisplay().getSize(size1);
            infoPopup.showAtLocation(view12, Gravity.TOP, 0,
                    view12.getBottom() + (int) getResources().getDimension(R.dimen._40sdp));
        });*/

        txtSubmitAns.setOnClickListener(view13 -> {
            editAns.clearFocus();

            if (strings[spinner.getSelectedIndex()].equalsIgnoreCase(strings[0])) {
                Utils.getInstance().showWarning(getContext(), getResources().getString(R.string.select_valid_question));
                return;
            } else if (editAns.getText() == null || editAns.getText().toString().isEmpty()) {
                Utils.getInstance().showWarning(getContext(), getResources().getString(R.string.answer_not_empty));
                return;
            }

            if (!isForgot) {
                llShowPassword.setVisibility(View.VISIBLE);

                llShowPassword.animate().setDuration(300).translationX(0).start();
                llSetSecurityQue.animate().setDuration(300).translationX(-width).start();

                currentStep = 2;

            } else {

                if (editAns.getText().toString().equalsIgnoreCase(Utils.getInstance().getSecurityAns(getContext()))) {
                    txtincorrectAns.setVisibility(View.GONE);

                    txt1.setText("");

                    txt2.setText("");

                    txt3.setText("");

                    txt4.setText("");

                    isResetPwd = true;

                    llShowPassword.animate().setDuration(300).translationX(0).start();
                    llSetSecurityQue.animate().setDuration(300).translationX(-width).start();

                } else {
                    txtincorrectAns.setVisibility(View.VISIBLE);
                    editAns.setText("");
                }
            }
        });

        TextView txtSetSecurity = view.findViewById(R.id.txtSetSecurity);

        txtSetSecurity.setOnClickListener(view1 -> {

            llSetSecurityQue.animate().setDuration(300).translationX(0).start();
            llSetSecurity.animate().setDuration(300).translationX(-width).start();

            currentStep = 1;
        });

        llOne.setOnClickListener(v -> clickNum(1));
        llTwo.setOnClickListener(v -> clickNum(2));
        llThree.setOnClickListener(v -> clickNum(3));
        llFour.setOnClickListener(v -> clickNum(4));
        llFive.setOnClickListener(v -> clickNum(5));
        llSix.setOnClickListener(v -> clickNum(6));
        llSeven.setOnClickListener(v -> clickNum(7));
        llEight.setOnClickListener(v -> clickNum(8));
        llNine.setOnClickListener(v -> clickNum(9));
        llZero.setOnClickListener(v -> clickNum(0));

        llClear.setOnClickListener(v -> {
            if (currentpos == 4) {
                currentpos = 3;
                txt3.setText("");
            } else if (currentpos == 3) {
                currentpos = 2;
                txt2.setText("");
            } else if (currentpos == 2) {
                currentpos = 1;
                txt1.setText("");
            }
        });

        if (Utils.getInstance().getPassword(getContext()).equals("")) {
            txtSelectedTitle.setText(getResources().getString(R.string.set_password));
        } else {
            txtSelectedTitle.setText(getResources().getString(R.string.enter_password));
        }

        imgUnlock.setOnClickListener(v -> {
            if (SplashActivity.isDataLoaded) {
                unlock();
            } else {
                Utils.getInstance().showImageLoadingDialog(getActivity(), () -> unlock());
            }
        });

    }

    private void unlock() {
        if (selectedItems.size() > 0) {

            final Dialog dialogProgress = new Dialog(getContext(), R.style.dialog);
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

            unhide task = new unhide(dialogProgress, progress, txtPerc, txtTotal);
            task.execute();

            txtCancelCopy.setOnClickListener(v15 -> {
                dialogProgress.dismiss();
                task.cancel(true);
            });

        } else {

            Utils.getInstance().showWarning(getActivity(), getResources().getString(R.string.select_image_to_unhide));
        }
    }

    @SuppressLint("SetTextI18n")
    private void clickNum(int i) {
        txtincorrect.setVisibility(View.INVISIBLE);

        if (currentpos == 1) {
            currentpos = 2;
            txt1.setText(i + "");
        } else if (currentpos == 2) {
            txt2.setText(i + "");
            currentpos = 3;
        } else if (currentpos == 3) {
            txt3.setText(i + "");
            currentpos = 4;
        } else {
            txt4.setText(i + "");
            new Handler().postDelayed(() -> getActivity().runOnUiThread(() -> {

                if (isResetPwd) {
                    if (isEnterAgain) {

                        txtTitlePwd.setText(getResources().getString(R.string.confirm_password));
                        isEnterAgain = false;

                        enterpwd = txt1.getText().toString() + txt2.getText().toString() +
                                txt3.getText().toString() + txt4.getText().toString();

                        txt1.setText("");

                        txt2.setText("");

                        txt3.setText("");

                        txt4.setText("");

                        currentpos = 1;
                    } else {

                        String reenteredPWd = txt1.getText().toString() + txt2.getText().toString() +
                                txt3.getText().toString() + txt4.getText().toString();

                        if (enterpwd.equals(reenteredPWd)) {
                            Utils.getInstance().setpassword(getContext(), reenteredPWd);

                            Utils.getInstance().setSecurityQuesInt(getContext(), spinner.getSelectedIndex());
                            Utils.getInstance().setSecurityAns(getContext(), editAns.getText().toString());

                            Utils.getInstance().setPasswordSetupDone(true, getContext());
                            passwordMatch();

                            Utils.getInstance().hidePhotos(list, (MainActivity) context, false, () -> {
                                getActivity().runOnUiThread(() -> passwordMatch());
                            });

                        } else {
                            currentpos = 1;
                            txtincorrect.setVisibility(View.VISIBLE);
                            txt1.setText("");

                            txt2.setText("");

                            txt3.setText("");

                            txt4.setText("");
                        }

                    }
                } else {

                    String enteredPWd = txt1.getText().toString() + txt2.getText().toString() +
                            txt3.getText().toString() + txt4.getText().toString();

                    if (enteredPWd.equals(Utils.getInstance().getPassword(getContext()))) {
                        passwordMatch();
                    } else {
                        txtincorrect.setVisibility(View.VISIBLE);
                        txtincorrect.setText(getResources().getString(R.string.password_not_matcg));

                        txt1.setText("");

                        txt2.setText("");

                        txt3.setText("");

                        txt4.setText("");

                        currentpos = 1;
                    }
                }
            }), 300);
        }
    }

    private void passwordMatch() {
        llShowPassword.setVisibility(View.GONE);

      /*  StickyHeadersGridLayoutManager<AllPhotosAdapter> layoutManager = new StickyHeadersGridLayoutManager<>(getContext(), 4);

        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mAdapter.isStickyHeader(position)) {
                    return 4;
                }
                return 1;
            }
        });*/

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

        stickyList.setLayoutManager(manager);
        stickyList.setAdapter(mAdapter);

        isEnteredPwd = true;

        txtSelectedTitle.setText("Hidden Images");
        currentpos = 1;

        if (HiddenImages.size() > 0) {
            stickyList.setVisibility(View.VISIBLE);
            llNoDataFound.setVisibility(View.GONE);
        } else {

            Log.e("TAG", "visible 1");
            stickyList.setVisibility(View.GONE);
            llNoDataFound.setVisibility(View.VISIBLE);
        }

        stickyList.setItemViewCacheSize(56);
    }

    public class HiddenPhotosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements StickyHeaders, StickyHeaders.ViewSetup {

        HashMap<Integer, Integer> params = new HashMap<>();
        private static final int HEADER_ITEM = 123;
        private static final int SUB_ITEM = 124;
        private final Activity context;

        long DURATION = 100;
        private boolean on_attach = true;

        public HiddenPhotosAdapter(Activity context) {
            this.context = context;

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

                params.put(i, i);
                if (!on_attach) {
                    i = -1;
                }
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

        public void onback() {
            if (!Utils.getInstance().isPasswordSetupDone(getContext())) {

                if (currentStep == 2) {
                    llSetSecurityQue.animate().setDuration(300).translationX(0).start();
                    llShowPassword.animate().setDuration(300).translationX(width).start();

                    currentStep = 1;
                } else if (currentStep == 1) {
                    llSetSecurity.animate().setDuration(300).translationX(0).start();
                    llSetSecurityQue.animate().setDuration(300).translationX(width).start();

                    currentStep = 0;
                } else {
                    MainActivity.viewPager.setCurrentItem(0, true);
                }

            } else {
                MainActivity.viewPager.setCurrentItem(0, true);
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == HEADER_ITEM) {
                View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header, parent, false);
                return new HeaderViewHolder(inflate);
            } else {
                View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photos, parent, false);
                return new MyViewHolder(inflate);
            }
        }


        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            if (holder instanceof HeaderViewHolder) {
                ((HeaderViewHolder) holder).textHeader.setText(new File(HiddenImages.get(position)).getName());
            } else if (holder instanceof MyViewHolder) {
                setAnimation(holder.itemView, position);
                if (isSelectionEnable) {
                    ((MyViewHolder) holder).imgSelection.setVisibility(View.VISIBLE);

                    if (selectedItems.contains(HiddenImages.get(position))) {
                        ((MyViewHolder) holder).imgSelection.setImageResource(R.drawable.ic_check);
                    } else {
                        ((MyViewHolder) holder).imgSelection.setImageResource(R.drawable.ic_allselecticon);
                    }
                } else {
                    ((MyViewHolder) holder).imgSelection.setVisibility(View.GONE);
                }

                Glide.with(context).load(R.drawable.img_placeholder).diskCacheStrategy(DiskCacheStrategy.ALL).into(((MyViewHolder) holder).imgThumbnail);

                boolean isImage = Utils.getInstance().isImageTypeForHidden(HiddenImages.get(position));

                if (isImage) {
                    ((MyViewHolder) holder).llVideo.setVisibility(View.GONE);

                } else {
                    ((MyViewHolder) holder).llVideo.setVisibility(View.VISIBLE);
                }

                Glide.with(context).load(HiddenImages.get(position)).addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        ((MyViewHolder) holder).imgThumbnail.setVisibility(View.GONE);
                        return false;
                    }

                }).into(((MyViewHolder) holder).imgthumb);

                holder.itemView.setOnClickListener(v -> {
                    if (isSelectionEnable) {

                        if (selectedItems.contains(HiddenImages.get(position))) {
                            selectedItems.remove(HiddenImages.get(position));
                        } else {
                            selectedItems.add(HiddenImages.get(position));
                        }

                        notifyItemChanged(position);
                        txtSelectedTitle.setText(selectedItems.size() + getResources().getString(R.string.selected));

                    } else {
                        new InterAds().showInterAds(context, new InterAds.OnAdClosedListener() {
                            @Override
                            public void onAdClosed() {
                                context.startActivity(new Intent(context, ViewImageActivity.class)
                                        .putExtra("position", position)
                                        .putExtra("path", HiddenImages.get(position)).putExtra("isFromHidden", true).putExtra("type", 2));

                            }
                        });
                    }
                });

                holder.itemView.setOnLongClickListener(v -> {
                    if (!isSelectionEnable) {

                        enableEdit();
                        selectedItems.add(HiddenImages.get(position));
                        notifyDataSetChanged();

                        txtSelectedTitle.setText(selectedItems.size() + getResources().getString(R.string.selected));
                    }

                    return false;
                });

            }
        }

        @Override
        public int getItemCount() {
            return HiddenImages.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (HiddenImages.get(position).contains(".Gallery")) {
                return SUB_ITEM;
            } else {
                return HEADER_ITEM;
            }
        }

        @Override
        public boolean isStickyHeader(int position) {
            return getItemViewType(position) == HEADER_ITEM;
        }

        @Override
        public void setupStickyHeaderView(View stickyHeader) {
            ViewCompat.setElevation(stickyHeader, 10);
        }

        @Override
        public void teardownStickyHeaderView(View stickyHeader) {
            ViewCompat.setElevation(stickyHeader, 0);
        }

        @Override
        public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
            super.onViewAttachedToWindow(holder);
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                if (isStickyHeader(holder.getLayoutPosition())) {
                    StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                    p.setFullSpan(true);
                }
            }
        }

        public void enableEdit() {

            // MainActivity.tabLayout.setVisibility(View.GONE);
            selectedItems.clear();
            isSelectionEnable = true;

            txtSelectedTitle.setText(selectedItems.size() + getResources().getString(R.string.selected));

            MainActivity.relActionbarMain.setVisibility(View.GONE);
            relSelection.setVisibility(View.VISIBLE);
            imgUnlock.setVisibility(View.VISIBLE);
            imgselectall.setVisibility(View.GONE);
            MainActivity.llHideOptions.setVisibility(View.VISIBLE);

            llSelectedOptions.setVisibility(View.GONE);
            MainActivity.viewPager.setPagingEnabled(false);
            notifyDataSetChanged();
        }

        public void disableEdit(boolean isFromMain) {
            selectedItems.clear();

            mAdapter.notifyDataSetChanged();
            txtSelectedTitle.setText("Hidden Images");
            MainActivity.viewPager.setPagingEnabled(true);
            isSelectionEnable = false;

            MainActivity.llHideOptions.setVisibility(View.GONE);
            if (!isFromMain)
                MainActivity.activity.onBackPressed();

            llSelectedOptions.setVisibility(View.GONE);
        }

        class HeaderViewHolder extends RecyclerView.ViewHolder {

            TextView textHeader;

            public HeaderViewHolder(View itemView) {
                super(itemView);

                textHeader = itemView.findViewById(R.id.text1);
            }
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            ImageView imgthumb;
            ImageView imgThumbnail;
            ImageView imgSelection;

            LinearLayout llVideo;

            public MyViewHolder(View itemView) {
                super(itemView);

                imgthumb = itemView.findViewById(R.id.imgThumb);
                imgSelection = itemView.findViewById(R.id.imgSelection);

                imgThumbnail = itemView.findViewById(R.id.imgThumbnail);
                llVideo = itemView.findViewById(R.id.llVideo);
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();

        if (view != null && MyApplication.isReloadHidden) {
            MyApplication.isReloadHidden = false;
            setview(view);
        }
    }

    private class unhide extends AsyncTask<Void, Void, Void> {

        Dialog dialog;
        TextView txtPercentage;
        TextView txtTotalVal;
        ProgressBar progress;

        public unhide(Dialog dialogProgress, ProgressBar progressBar, TextView txtPerc, TextView txtTotal) {
            dialog = dialogProgress;
            txtPercentage = txtPerc;
            txtTotalVal = txtTotal;
            progress = progressBar;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            getActivity().runOnUiThread(() -> dialog.show());
        }

        @Override
        protected Void doInBackground(Void... voids) {

            for (int i = 0; i < selectedItems.size(); i++) {

                Utils.getInstance().unhideImage(selectedItems.get(i), getContext());

                int temp = ((i + 1) * 100);
                temp = temp / selectedItems.size();

                int finalI = i;
                int finalTemp = temp;

                getActivity().runOnUiThread(() -> {

                    progress.setProgress(finalTemp);
                    txtPercentage.setText(finalTemp + "%");
                    txtTotalVal.setText(finalI + 1 + "/" + selectedItems.size());

                    if (finalI == selectedItems.size() - 1) {

                        Utils.getInstance().showSuccess(getContext(), getResources().getString(R.string.unhided) + selectedItems.size() + getResources().getString(R.string.items));
                        dialog.dismiss();
                    }

                    Utils.getInstance().scanMedia(getContext());

                    AllPhotosFragment.mAdapter.adddata(allimages);
                    AllPhotosFragment.mAdapter.notifyDataSetChanged();

                    FolderFragment.adapterDefault.notifyDataSetChanged();
                    FolderFragment.adapter.notifyDataSetChanged();
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            getActivity().runOnUiThread(() -> {
                dialog.dismiss();

                mAdapter.disableEdit(false);

                if (HiddenImages.size() > 0) {
                    stickyList.setVisibility(View.VISIBLE);
                    llNoDataFound.setVisibility(View.GONE);
                } else {
                    Log.e("TAG", "visible 2");
                    stickyList.setVisibility(View.GONE);
                    llNoDataFound.setVisibility(View.VISIBLE);
                }

                AllPhotosFragment.mAdapter.adddata(allimages);
                AllPhotosFragment.mAdapter.notifyDataSetChanged();
                FolderFragment.adapterDefault.notifyDataSetChanged();
                FolderFragment.adapter.notifyDataSetChanged();

                mAdapter.notifyDataSetChanged();
            });
        }
    }
}
