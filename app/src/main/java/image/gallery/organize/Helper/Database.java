package image.gallery.organize.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import image.gallery.organize.Fragment.FolderFragment;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Database extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "GalleryDbV1";

    private final String tbl_fav = "tbl_favourite";
    private final String fav_id = "fav_id";
    private final String fav_path = "fav_path";
    private final String fav_date = "fav_date";

    private final String tbl_bin = "tbl_bin";
    private final String bin_id = "bin_id";
    private final String bin_filename = "bin_filename";
    private final String bin_file_orig_path = "bin_file_orig_path";

    private final String tbl_hide = "tbl_hide";
    private final String hide_id = "hide_id";
    private final String hide_filename = "hide_filename";
    private final String hide_file_folder = "hide_file_folder";

    public static Database dbhelper;

    public static Database getInstance(Context context) {
        if (dbhelper == null)
            dbhelper = new Database(context);

        return dbhelper;
    }

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_FAV_TABLE = "CREATE TABLE if not exists " + tbl_fav + "(" + fav_id + " INTEGER PRIMARY KEY," + fav_path + " TEXT, " + fav_date + " TEXT)";
        String CREATE_BIN_TABLE = "CREATE TABLE if not exists " + tbl_bin + "(" + bin_id + " INTEGER PRIMARY KEY," + bin_filename + " TEXT, " + bin_file_orig_path + " TEXT)";
        String CREATE_HIDE_TABLE = "CREATE TABLE if not exists " + tbl_hide + "(" + hide_id + " INTEGER PRIMARY KEY," + hide_filename + " TEXT, " + hide_file_folder + " TEXT)";

        db.execSQL(CREATE_FAV_TABLE);
        db.execSQL(CREATE_BIN_TABLE);
        db.execSQL(CREATE_HIDE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addToFavourite(String path, boolean isfav) {

        if (!path.contains("storage"))
            return;

        SQLiteDatabase db = this.getWritableDatabase();

        if (isfav) {

            String selectQuery = "SELECT  * FROM " + tbl_fav + " where " + fav_path + " = '" + path + "'";

            Cursor cursor = null;
            boolean check = true;
            try {
                cursor = db.rawQuery(selectQuery, null);
                if (cursor != null) {
                    if (cursor.getCount() > 0) {
                        check = false;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null)
                    cursor.close();
            }


            if (check) {
                SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy-MM-dd");
                String datestr = timeStampFormat.format(Calendar.getInstance().getTime());

                ContentValues values = new ContentValues();
                values.put(fav_path, path);
                values.put(fav_date, datestr);

                db.insert(tbl_fav, null, values);
            }
        } else {
            String selectQuery = "SELECT  * FROM " + tbl_fav + " where " + fav_path + " = '" + path + "'";

            Cursor cursor = null;

            try {
                cursor = db.rawQuery(selectQuery, null);

                if (cursor != null) {
                    if (cursor.getCount() > 0) {
                        db.execSQL("DELETE FROM " + tbl_fav + " WHERE " + fav_path + " = '" + path + "'");
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null)
                    cursor.close();
            }

        }

        FolderFragment.adapterDefault.notifyDataSetChanged();
    }

    public boolean isAddedToFav(String path) {
        String deleteQuery = "select * FROM " + tbl_fav + " where " + fav_path + " = '" + path + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(deleteQuery, null);
            if (cursor != null) {
                if (cursor.getCount() > 0)
                    return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return false;
    }

    public void addToHideen(String filename, String oldpath) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(hide_filename, filename);
        values.put(hide_file_folder, oldpath);

        db.insert(tbl_hide, null, values);
    }

    public void ReplaceHideen(String filename, String oldpath) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(hide_filename, filename);
        values.put(hide_file_folder, oldpath);

        db.insert(tbl_hide, null, values);
    }

    public void changeHidden(String oldName, String newName) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = " SELECT * FROM " + tbl_hide + " WHERE " + hide_filename + " =  '" + oldName + "'";

        Cursor cursor = null;

        try {
            cursor = db.rawQuery(query, null);

            if (cursor.getCount() > 0) {
                query = " Update " + tbl_hide + " SET " + hide_filename + " = '" + newName + "' where " + hide_id + " = " + cursor.getInt(cursor.getColumnIndex(hide_id)) + "";
                db.execSQL(query);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }

    }


    public void addToBin(String filename, String oldpath) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(bin_filename, filename);
        values.put(bin_file_orig_path, oldpath);

        db.insert(tbl_bin, null, values);
    }

    public void deleteFromBin(ArrayList<String> selectedItems) {

        String where = " where ";

        for (int i = 0; i < selectedItems.size(); i++) {
            if (i == selectedItems.size() - 1)
                where = where + bin_filename + " = '" + new File(selectedItems.get(i)).getName() + "'";
            else
                where = where + bin_filename + " = '" + new File(selectedItems.get(i)).getName() + "' OR ";
        }

        String deleteQuery = "delete FROM " + tbl_bin + where;

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(deleteQuery);
    }

    public void deleteFromHidden(ArrayList<String> selectedItems) {

        String where = " where ";

        for (int i = 0; i < selectedItems.size(); i++) {
            if (i == selectedItems.size() - 1)
                where = where + hide_filename + " = '" + new File(selectedItems.get(i)).getName() + "'";
            else
                where = where + hide_filename + " = '" + new File(selectedItems.get(i)).getName() + "' OR ";
        }

        String deleteQuery = "delete FROM " + tbl_hide + where;

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(deleteQuery);
    }

    public String getBinInfo(String name) {
        String selectQuery = "select * from " + tbl_bin + " where " + bin_filename + " = '" + name + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        String path = "";

        try {
            cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    path = cursor.getString(cursor.getColumnIndex(bin_file_orig_path));
                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }


        return path;
    }

    public String getHiddenFileFoldername(String name) {

        String folder = null;
        String deleteQuery = "select * FROM " + tbl_hide + " where " + hide_filename + " = '" + name + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(deleteQuery, null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    folder = cursor.getString(cursor.getColumnIndex(hide_file_folder));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }


        return folder;
    }

    public int getfavCount() {
        String deleteQuery = "select * FROM " + tbl_fav;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        int tot = 0;
        try {
            cursor = db.rawQuery(deleteQuery, null);


            if (cursor != null)
                tot = cursor.getCount();
            else
                tot = 0;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return tot;
    }

    public void checkFavList() {
        String deleteQuery = "select * FROM " + tbl_fav;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        int tot = 0;
        try {
            cursor = db.rawQuery(deleteQuery, null);


            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        if (new File(cursor.getString(cursor.getColumnIndex(fav_path))).exists()) {
                            db.execSQL("DELETE FROM " + tbl_fav + " WHERE " + fav_path + " = '" + cursor.getString(cursor.getColumnIndex(fav_path)) + "'");
                        }
                    } while (cursor.moveToNext());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    public ArrayList<String> getFavouriteList() {
        ArrayList<String> data = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        String query = " SELECT * FROM " + tbl_fav + " ORDER by " + fav_date + " desc";

        Cursor cursor = null;

        try {
            cursor = db.rawQuery(query, null);
            if (cursor != null) {
                String Date = null;
                if (cursor.moveToFirst()) {
                    do {
                        String datestr = convertDate(cursor.getString(cursor.getColumnIndex(fav_date)));
                        String path = cursor.getString(cursor.getColumnIndex(fav_path));

                        if (new File(path).exists()) {
                            if (Date == null) {
                                Date = datestr;
                                data.add(datestr);
                            }

                            if (!datestr.equals(Date)) {
                                data.add(datestr);
                                Date = datestr;
                            }
                            data.add(path);
                        }
                    } while (cursor.moveToNext());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return data;
    }

    public ArrayList<String> getFavouriteListWithoutDate() {
        ArrayList<String> data = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        String query = " SELECT * FROM " + tbl_fav + " ORDER by " + fav_date + " desc";

        Cursor cursor = null;


        try {

            cursor = db.rawQuery(query, null);

            if (cursor != null) {
                String Date = null;
                if (cursor.moveToFirst()) {
                    do {
                        String path = cursor.getString(cursor.getColumnIndex(fav_path));

                        if (new File(path).exists()) {
                            data.add(path);
                        }
                    } while (cursor.moveToNext());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }


        return data;
    }

    private String convertDate(String date) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        Date d = null;
        try {
            d = f.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat f2 = new SimpleDateFormat("MMM dd,yyyy");
        return f2.format(d);
    }

    public void changeAddToFav(String path, String absolutePath) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = " SELECT * FROM " + tbl_fav + " ORDER by " + fav_date + " desc";

        Cursor cursor = null;

        try {
            cursor = db.rawQuery(query, null);

            if (cursor.getCount() > 0) {
                query = " Update " + tbl_fav + " SET " + fav_path + " = '" + absolutePath + "' where " + fav_path + " = '" + path + "'";
                db.execSQL(query);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }

    }

}
