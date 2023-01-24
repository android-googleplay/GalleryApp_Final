package image.gallery.organize.Activity;

import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import image.gallery.organize.Adhelper.BackInterAds;
import image.gallery.organize.Adhelper.ListBannerAds;
import image.gallery.organize.Adapter.BgColorAdaptor;
import image.gallery.organize.Adapter.ColorAdaptor;
import image.gallery.organize.Adapter.FontAdaptor;
import image.gallery.organize.Helper.Utils;
import image.gallery.organize.Helper.onClickRecycle;
import image.gallery.organize.R;
import image.gallery.organize.sticker.DrawableSticker;
import image.gallery.organize.sticker.Sticker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditTextActivity extends AppCompatActivity {

    @BindView(R.id.btnDone)
    ImageView btnDone;
    @BindView(R.id.edText)
    EditText edText;
    @BindView(R.id.linSS)
    LinearLayout linSS;

    @BindView(R.id.LL_Edit)
    LinearLayout LL_Edit;

    @BindView(R.id.linFont)
    LinearLayout linFont;
    @BindView(R.id.linColor)
    LinearLayout linColor;
    @BindView(R.id.linBackground)
    LinearLayout linBackground;
    @BindView(R.id.linShadow)
    LinearLayout linShadow;

    @BindView(R.id.recycleFont)
    RecyclerView recycleFont;
    @BindView(R.id.recycleColor)
    RecyclerView recycleColor;

    @BindView(R.id.recycleBackground)
    RecyclerView recycleBackground;

    @BindView(R.id.layShadow)
    LinearLayout layShadow;
    @BindView(R.id.seekOpacity)
    SeekBar seekOpacity;
    @BindView(R.id.recycleShadowColor)
    RecyclerView recycleShadowColor;

    private int shadowProgress = 15;
    private int shadowColor = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_text);

        Utils.getInstance().colorStatusBar(this);
        ButterKnife.bind(this);

        getId();
    }

    private void getId() {

        SelectMenu(linFont);
        getFont();
        getTextColor();
        getBackgroundColor();
        getShadowColor();

    }

    @OnClick({R.id.imgBack, R.id.btnDone, R.id.linFont, R.id.linColor, R.id.linBackground, R.id.linShadow})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.imgBack:
                onBackPressed();
                break;

            case R.id.btnDone:
                getFinalText();
                break;

            case R.id.linFont:
                SelectMenu(linFont);
                break;

            case R.id.linColor:
                SelectMenu(linColor);
                break;

            case R.id.linBackground:
                SelectMenu(linBackground);
                break;

            case R.id.linShadow:
                SelectMenu(linShadow);
                break;
        }
    }

    private void SelectMenu(View view) {
        recycleFont.setVisibility(View.GONE);
        recycleColor.setVisibility(View.GONE);
        recycleBackground.setVisibility(View.GONE);
        layShadow.setVisibility(View.GONE);

        LL_Edit.getChildAt(LL_Edit.indexOfChild(linFont)).setActivated(false);
        LL_Edit.getChildAt(LL_Edit.indexOfChild(linColor)).setActivated(false);
        LL_Edit.getChildAt(LL_Edit.indexOfChild(linBackground)).setActivated(false);
        LL_Edit.getChildAt(LL_Edit.indexOfChild(linShadow)).setActivated(false);
        LL_Edit.getChildAt(LL_Edit.indexOfChild(view)).setActivated(true);

        if (view == findViewById(R.id.linFont)) {
            recycleFont.setVisibility(View.VISIBLE);
        } else if (view == findViewById(R.id.linColor)) {
            recycleColor.setVisibility(View.VISIBLE);
        } else if (view == findViewById(R.id.linBackground)) {
            recycleBackground.setVisibility(View.VISIBLE);
        } else if (view == findViewById(R.id.linShadow)) {
            layShadow.setVisibility(View.VISIBLE);
        }
    }

    private void getFont() {
        List<String> listfont = new ArrayList<>();
        try {
            String[] list = getAssets().list("font");
            for (String str : list) {
                String stringBuilder = "font" + File.separator + str;
                listfont.add(stringBuilder);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        recycleFont.setHasFixedSize(true);
        recycleFont.setLayoutManager(new GridLayoutManager(this, 4));
        recycleFont.setAdapter(new FontAdaptor(this, listfont));
        recycleFont.addOnItemTouchListener(
                new onClickRecycle(this, (view, position) -> edText.setTypeface(Typeface.createFromAsset(getAssets(), listfont.get(position))))
        );
    }

    private void getTextColor() {
        int[] listTextColor;

        TypedArray obtainTypedArray = getResources().obtainTypedArray(R.array.arrayTextColor);
        listTextColor = new int[obtainTypedArray.length()];
        for (int i = 0; i < obtainTypedArray.length(); i++) {
            listTextColor[i] = obtainTypedArray.getColor(i, 0);
        }

        recycleColor.setHasFixedSize(true);
        recycleColor.setLayoutManager(new GridLayoutManager(this, 5));
        recycleColor.setAdapter(new ColorAdaptor(this, listTextColor));

        recycleColor.addOnItemTouchListener(
                new onClickRecycle(this, (view, position) -> {
                    edText.setTextColor(listTextColor[position]);
                    edText.setHintTextColor(listTextColor[position]);
                })
        );
    }

    private void getBackgroundColor() {
        int[] listBackgroundColor;
        TypedArray obtainTypedArray = getResources().obtainTypedArray(R.array.arrayBackgroundColor);
        listBackgroundColor = new int[obtainTypedArray.length()];
        for (int i = 0; i < obtainTypedArray.length(); i++) {
            listBackgroundColor[i] = obtainTypedArray.getColor(i, 0);
        }

        recycleBackground.setHasFixedSize(true);
        recycleBackground.setLayoutManager(new GridLayoutManager(this, 5));
        recycleBackground.setAdapter(new BgColorAdaptor(this, listBackgroundColor));

        recycleBackground.addOnItemTouchListener(
                new onClickRecycle(this, (view, position) -> edText.setBackgroundColor(listBackgroundColor[position]))
        );
    }

    private void getShadowColor() {
        int[] listShadowColor;
        TypedArray obtainTypedArray = getResources().obtainTypedArray(R.array.arrayShadowColor);
        listShadowColor = new int[obtainTypedArray.length()];
        for (int i = 0; i < obtainTypedArray.length(); i++) {
            listShadowColor[i] = obtainTypedArray.getColor(i, 0);
        }

        seekOpacity.setProgress(15);
        seekOpacity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.w("progress", String.valueOf(progress));
                shadowProgress = progress;
                edText.setShadowLayer(shadowProgress, -1.0f, 1.0f, listShadowColor[shadowColor]);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        recycleShadowColor.setHasFixedSize(true);
        recycleShadowColor.setLayoutManager(new GridLayoutManager(this, 5));
        recycleShadowColor.setAdapter(new ColorAdaptor(this, listShadowColor));

        recycleShadowColor.addOnItemTouchListener(
                new onClickRecycle(this, (view, position) -> {
                    shadowColor = position;
                    edText.setShadowLayer(shadowProgress, -1.0f, 1.0f, listShadowColor[position]);
                })
        );
    }

    private void getFinalText() {
        if (edText.getText().toString().length() == 0) {
            Utils.getInstance().showWarning(this, getResources().getString(R.string.enter_some_text));
        } else {
            edText.setCursorVisible(false);
            Bitmap bitmapSS = takeScreenshot(linSS);

            Drawable d = new BitmapDrawable(getResources(), bitmapSS);
            EditImageActivity.stickerView.addSticker(new DrawableSticker(d), Sticker.Position.CENTER);

            finish();
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

    public Bitmap takeScreenshot(View view) {
        Bitmap createBitmap = null;
        try {
            createBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            view.draw(new Canvas(createBitmap));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return createBitmap;
    }

    @Override
    protected void onResume() {
        super.onResume();

        new ListBannerAds().showBannerAds(this, null, null);
    }
}
