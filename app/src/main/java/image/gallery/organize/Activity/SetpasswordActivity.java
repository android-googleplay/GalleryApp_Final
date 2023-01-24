package image.gallery.organize.Activity;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import image.gallery.organize.Adhelper.BackInterAds;
import image.gallery.organize.Adhelper.LargeNativeAds;
import image.gallery.organize.MyApplication;
import com.jaredrummler.materialspinner.MaterialSpinner;
import image.gallery.organize.Helper.Utils;
import image.gallery.organize.R;

import static image.gallery.organize.Activity.MainActivity.txtSelectedTitle;

public class SetpasswordActivity extends AppCompatActivity {

    private boolean isEnterAgain = true;

    private LinearLayout llShowPassword;
    private LinearLayout llSetSecurity;
    private LinearLayout llSetSecurityQue;
    int currentStep;

    private TextView txt1;
    private TextView txt2;
    private TextView txt3;
    private TextView txt4;
    private TextView txtincorrect;
    private TextView txtTitlePwd;

    private String enterpwd;
    private int currentpos = 1;
    private int width;


    String[] strings;
    MaterialSpinner spinner;
    private EditText editAns;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hide_images);
        strings = new String[]{getResources().getString(R.string.select_your_question)
                ,getResources().getString(R.string.what_s_your_father_s_name)
                ,getResources().getString(R.string.what_s_your_mother_s_name)
                ,getResources().getString(R.string.what_s_your_favourite_movie)
                ,getResources().getString(R.string.what_s_your_pets_name)
                ,getResources().getString(R.string.what_s_your_dream_job)
                ,getResources().getString(R.string.in_which_city_did_your_parents_meet)};
        setview();
    }


    @Override
    protected void onResume() {
        super.onResume();


        new LargeNativeAds().showNativeAds(this, null, null, null);


    }

    @SuppressLint("SetTextI18n")
    private void setview() {
        spinner = findViewById(R.id.spinnerQuestions);
        spinner.setItems(strings);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;

        findViewById(R.id.cardAction).setVisibility(View.VISIBLE);

        findViewById(R.id.imgBack).setOnClickListener(view -> onBackPressed());

        txtTitlePwd = findViewById(R.id.txtTitlePwd);
        llSetSecurity = findViewById(R.id.llSetSecurity);
        llSetSecurityQue = findViewById(R.id.llSetSecurityQue);

        findViewById(R.id.txtForgotPassword).setVisibility(View.INVISIBLE);

        llShowPassword = findViewById(R.id.llShowPassword);
        llSetSecurityQue.setX(width);
        llSetSecurityQue.setVisibility(View.VISIBLE);

        findViewById(R.id.listPhotos).setVisibility(View.GONE);

        llShowPassword.setVisibility(View.GONE);
        llSetSecurity.setVisibility(View.VISIBLE);

        llShowPassword.setX(width);

        findViewById(R.id.llNoDataFound).setVisibility(View.GONE);

        LinearLayout llOne = findViewById(R.id.llOne);
        LinearLayout llTwo = findViewById(R.id.llTwo);
        LinearLayout llThree = findViewById(R.id.llThree);
        LinearLayout llFour = findViewById(R.id.llFour);
        LinearLayout llFive = findViewById(R.id.llFive);
        LinearLayout llSix = findViewById(R.id.llSix);
        LinearLayout llSeven = findViewById(R.id.llSeven);
        LinearLayout llEight = findViewById(R.id.llEight);
        LinearLayout llNine = findViewById(R.id.llNine);
        LinearLayout llZero = findViewById(R.id.llZero);
        LinearLayout llClear = findViewById(R.id.llClear);

        txt1 = findViewById(R.id.txt1);
        txt2 = findViewById(R.id.txt2);
        txt3 = findViewById(R.id.txt3);
        txt4 = findViewById(R.id.txt4);
        txtincorrect = findViewById(R.id.txtincorrect);
        TextView txtSubmitAns = findViewById(R.id.txtSubmitAns);

        editAns = findViewById(R.id.editAns);

      /*  relQue.setOnClickListener(view12 -> {
            LayoutInflater layoutInflater = LayoutInflater.from(SetpasswordActivity.this);
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
            getWindowManager().getDefaultDisplay().getSize(size1);
            infoPopup.showAtLocation(view12, Gravity.TOP, 0,
                    view12.getBottom() + (int) getResources().getDimension(R.dimen._40sdp));
        });
*/
        txtSubmitAns.setOnClickListener(view13 -> {
            if (strings[spinner.getSelectedIndex()].equalsIgnoreCase(strings[0])) {
                Utils.getInstance().showWarning(SetpasswordActivity.this,getResources().getString(R.string.select_question));
                return;
            } else if (editAns.getText() == null || editAns.getText().toString().isEmpty()) {

                Utils.getInstance().showWarning(SetpasswordActivity.this, getResources().getString(R.string.answer_not_empty));
                return;
            }

            llShowPassword.setVisibility(View.VISIBLE);

            llShowPassword.animate().setDuration(300).translationX(0).start();
            llSetSecurityQue.animate().setDuration(300).translationX(-width).start();

            currentStep = 2;
        });

        TextView txtSetSecurity = findViewById(R.id.txtSetSecurity);

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

        if (Utils.getInstance().getPassword(SetpasswordActivity.this).equals("")) {
            txtSelectedTitle.setText(getResources().getString(R.string.set_password));
        } else {
            txtSelectedTitle.setText(getResources().getString(R.string.enter_password));
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

            new Handler().postDelayed(() -> runOnUiThread(() -> {

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
                        Utils.getInstance().setpassword(SetpasswordActivity.this, reenteredPWd);

                        Utils.getInstance().setSecurityQuesInt(SetpasswordActivity.this, spinner.getSelectedIndex());
                        Utils.getInstance().setSecurityAns(SetpasswordActivity.this, editAns.getText().toString());

                        Utils.getInstance().setPasswordSetupDone(true, SetpasswordActivity.this);

                        MyApplication.isReloadHidden = true;

                        finish();

                        // Utils.getInstance().hidePhotos(getIntent().getStringArrayListExtra("list"), SetpasswordActivity.this, false, () -> finish());

                    } else {
                        currentpos = 1;
                        txtincorrect.setVisibility(View.VISIBLE);
                        txt1.setText("");

                        txt2.setText("");

                        txt3.setText("");

                        txt4.setText("");
                    }
                }
            }), 300);
        }
    }

    @Override
    public void onBackPressed() {

        if (currentStep == 2) {
            llSetSecurityQue.animate().setDuration(300).translationX(0).start();
            llShowPassword.animate().setDuration(300).translationX(width).start();

            currentStep = 1;
        } else if (currentStep == 1) {
            llSetSecurity.animate().setDuration(300).translationX(0).start();
            llSetSecurityQue.animate().setDuration(300).translationX(width).start();

            currentStep = 0;
        } else {
            new BackInterAds().showInterAds(this, new BackInterAds.OnAdClosedListener() {
                @Override
                public void onAdClosed() {
                    finish();
                }
            });
        }
    }
}
