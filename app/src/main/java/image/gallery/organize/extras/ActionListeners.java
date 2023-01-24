package image.gallery.organize.extras;

import android.graphics.Bitmap;

import java.io.File;

public interface ActionListeners {
    void convertedWithSuccess(Bitmap var1, String var2, File file);

    void convertedWithError(String var1);
}