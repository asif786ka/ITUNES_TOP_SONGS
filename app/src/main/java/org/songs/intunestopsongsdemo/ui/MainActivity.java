
package org.songs.intunestopsongsdemo.ui;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.songs.intunestopsongsdemo.R;
import org.songs.intunestopsongsdemo.controller.RestManager;
import org.songs.intunestopsongsdemo.model.callback.ITunesFetchListener;
import org.songs.intunestopsongsdemo.model.adapter.ITunesSongsAdapter;
import org.songs.intunestopsongsdemo.model.database.ITunesDatabase;
import org.songs.intunestopsongsdemo.model.helper.Utils;
import org.songs.intunestopsongsdemo.model.pojo.ITunesSongsResponse;
import org.songs.intunestopsongsdemo.model.pojo.Result;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class MainActivity extends AppCompatActivity implements ITunesSongsAdapter.ITuneSongsClickListener, ITunesFetchListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private RestManager mManager;
    private ITunesSongsAdapter mITunesSongsAdapter;
    private ITunesDatabase mDatabase;
    private Button mReload;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configViews();

        mManager = new RestManager();
        mDatabase = new ITunesDatabase(this);

        loadITunesFeed();

        mReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadITunesFeed();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(getNetworkAvailability()){
            mReload.setEnabled(true);
        }
        else{
            mReload.setEnabled(false);
        }
    }

    private void loadITunesFeed() {

        mDialog = new ProgressDialog(MainActivity.this);
        mDialog.setMessage("Loading ITune Songs...");
        mDialog.setCancelable(true);
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setIndeterminate(true);

        mITunesSongsAdapter.reset();

        mDialog.show();

        if (getNetworkAvailability()) {
            getFeed();
        } else {
            getFeedFromDatabase();
        }
    }

    private void getFeedFromDatabase() {
        mDatabase.fetchITunesSongs(this);
    }

    private void configViews() {
        mReload = (Button) findViewById(R.id.reload);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setRecycledViewPool(new RecyclerView.RecycledViewPool());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        mITunesSongsAdapter = new ITunesSongsAdapter(this);

        mRecyclerView.setAdapter(mITunesSongsAdapter);
    }

    @Override
    public void onClick(int position) {

    }

    public void getFeed() {

        Call<ITunesSongsResponse> listCall = mManager.getITunesService().getAllITuneSongs();
        listCall.enqueue(new Callback<ITunesSongsResponse>() {
            @Override
            public void onResponse(Call<ITunesSongsResponse> call, Response<ITunesSongsResponse> response) {
                if (response.isSuccessful()) {
                    List<Result> ituneList = response.body().getFeed().getResults();



                   for (int i = 0; i < ituneList.size(); i++) {
                        Result ituneSong = ituneList.get(i);

                        SaveIntoDatabase task = new SaveIntoDatabase();
                        task.execute(ituneSong);

                        mITunesSongsAdapter.addITuneSong(ituneSong);
                    }
                } else {
                    int sc = response.code();
                    switch (sc) {
                        case 400:
                            Log.e("Error 400", "Bad Request");
                            break;
                        case 404:
                            Log.e("Error 404", "Not Found");
                            break;
                        default:
                            Log.e("Error", "Generic Error");
                    }
                }
                mDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ITunesSongsResponse> call, Throwable t) {
                mDialog.dismiss();
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }




        });
    }

    public boolean getNetworkAvailability() {
        return Utils.isNetworkAvailable(getApplicationContext());
    }

    @Override
    public void onDeliverAllSongs(List<Result> flowers) {

    }

    @Override
    public void onDeliverSong(Result flower) {
        mITunesSongsAdapter.addITuneSong(flower);
    }

    @Override
    public void onHideDialog() {
        mDialog.dismiss();
    }

    @Override
    public void isNetworkAvailaible() {
        if(getNetworkAvailability()){
            mReload.setEnabled(true);
        }
        else{
            mReload.setEnabled(false);
        }
    }

    public class SaveIntoDatabase extends AsyncTask<Result, Void, Void> {


        private final String TAG = SaveIntoDatabase.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Result... params) {

            Result flower = params[0];

            InputStream stream = null;
            try {
                stream = new URL(flower.getArtworkUrl100()).openStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            flower.setPicture(bitmap);

            try {
                mDatabase.addITunesSong(flower);

            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }

            return null;
        }
    }
}
