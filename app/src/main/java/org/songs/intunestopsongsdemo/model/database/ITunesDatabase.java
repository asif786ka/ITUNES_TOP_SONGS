package org.songs.intunestopsongsdemo.model.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.songs.intunestopsongsdemo.model.callback.ITunesFetchListener;
import org.songs.intunestopsongsdemo.model.helper.Constants;
import org.songs.intunestopsongsdemo.model.helper.Utils;
import org.songs.intunestopsongsdemo.model.pojo.Result;

import java.util.ArrayList;
import java.util.List;


public class ITunesDatabase extends SQLiteOpenHelper {

    private static final String TAG = ITunesDatabase.class.getSimpleName();

    public ITunesDatabase(Context context) {
        super(context, Constants.DATABASE.DB_NAME, null, Constants.DATABASE.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(Constants.DATABASE.CREATE_TABLE_QUERY);
        } catch (SQLException ex) {
            Log.d(TAG, ex.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(Constants.DATABASE.DROP_QUERY);
        this.onCreate(db);
    }

    public void addITunesSong(Result ituneSong) {

        Log.d(TAG, "Values Got " + ituneSong.getName());

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.DATABASE.NAME, ituneSong.getName());
        values.put(Constants.DATABASE.PHOTO_URL, ituneSong.getArtworkUrl100());
        values.put(Constants.DATABASE.PHOTO, Utils.getPictureByteOfArray(ituneSong.getPicture()));


        try {
            db.insert(Constants.DATABASE.TABLE_NAME, null, values);
        } catch (Exception e) {

        }
        db.close();
    }

    public void fetchITunesSongs(ITunesFetchListener listener) {
        ITunesSongsFetcher fetcher = new ITunesSongsFetcher(listener, this.getWritableDatabase());
        fetcher.start();
    }

    public class ITunesSongsFetcher extends Thread {

        private final ITunesFetchListener mListener;
        private final SQLiteDatabase mDb;

        public ITunesSongsFetcher(ITunesFetchListener listener, SQLiteDatabase db) {
            mListener = listener;
            mDb = db;
        }

        @Override
        public void run() {
            Cursor cursor = mDb.rawQuery(Constants.DATABASE.GET_ITUNES_QUERY, null);

            final List<Result> itunesSongsList = new ArrayList<>();

            if (cursor.getCount() > 0) {

                if (cursor.moveToFirst()) {
                    do {
                        Result itunesSong = new Result();
                        itunesSong.setFromDatabase(true);
                        itunesSong.setName(cursor.getString(cursor.getColumnIndex(Constants.DATABASE.NAME)));
                        itunesSong.setUrl(cursor.getString(cursor.getColumnIndex(Constants.DATABASE.PHOTO_URL)));
                        itunesSong.setPicture(Utils.getBitmapFromByte(cursor.getBlob(cursor.getColumnIndex(Constants.DATABASE.PHOTO))));


                        itunesSongsList.add(itunesSong);
                        publishITunesSong(itunesSong);

                    } while (cursor.moveToNext());
                }
            }
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mListener.onDeliverAllSongs(itunesSongsList);
                    mListener.onHideDialog();
                }
            });
        }

        public void publishITunesSong(final Result ituneSong) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mListener.onDeliverSong(ituneSong);
                }
            });
        }
    }
}
