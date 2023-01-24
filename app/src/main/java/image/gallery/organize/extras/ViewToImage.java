package image.gallery.organize.extras;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;


import image.gallery.organize.Helper.Constant;
import image.gallery.organize.Helper.Utils;

import java.io.File;
import java.io.FileOutputStream;

public class ViewToImage {
    Context context;
    ActionListeners listeners;
    String folderName = "DevelopersFolder";
    String fileName = "myFile";
    View view;
    Bitmap bitmap = null;
    String filePath = null;

    public ViewToImage(Context context, View view) {
        this.context = context;
        this.view = view;
        this.convert();
    }

    public ViewToImage(Context context, View view, ActionListeners listeners) {
        this.context = context;
        this.listeners = listeners;
        this.view = view;
        this.convert();
    }

    public ViewToImage(Context context, View view, String folderName, String fileName, ActionListeners listeners) {
        this.context = context;
        this.listeners = listeners;
        this.folderName = folderName;
        this.fileName = fileName;
        this.view = view;
        this.convert();
    }

    public ViewToImage(Context context, View view, String fileName, ActionListeners listeners) {
        this.context = context;
        this.listeners = listeners;
        this.fileName = fileName;
        this.view = view;
        this.convert();
    }

    public ViewToImage(Context context, Bitmap view, ActionListeners listeners) {
        this.context = context;
        this.bitmap = view;
        this.listeners = listeners;
        saveTheImage(view, fileName);
    }

    private void convert() {
        if (view != null && view.getHeight() > 0 && view.getWidth() > 0) {
            Bitmap bitmap = this.getBitmapFromView(this.view, this.view.getWidth(), this.view.getHeight());
            if (this.fileName.equals("myFile")) {
                this.saveTheImage(bitmap, (String) null);
            } else {
                this.saveTheImage(bitmap, this.fileName);
            }
        } else {
            if (this.listeners != null) {
                this.listeners.convertedWithError("Something went wrong");
            }
        }
    }

    private Bitmap getBitmapFromView(View view, int width, int height) {
        this.bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas mCanvas = new Canvas(this.bitmap);
        view.layout(0, 0, view.getLayoutParams().width, view.getLayoutParams().height);
        view.draw(mCanvas);
        return this.bitmap;
    }

    private void saveTheImage(Bitmap finalBitmap, String fileName) {

        if (fileName == null) {
            fileName = System.currentTimeMillis() + ".jpg";
        } else {
            fileName = System.currentTimeMillis() + ".jpg";
        }

        File file = new File(Constant.getOutputFolder(), fileName);
        if (file.exists()) {
            Utils.getInstance().deleteFile(context, file.getAbsolutePath());
        }

        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            this.filePath = Constant.getOutputFolder() + File.separator + fileName;

            if (this.listeners != null) {
                this.listeners.convertedWithSuccess(this.bitmap, this.filePath, file);
            }
        } catch (Exception var9) {
            var9.printStackTrace();
            if (this.listeners != null) {
                this.listeners.convertedWithError(var9.getMessage());
            }
        }

    }
}