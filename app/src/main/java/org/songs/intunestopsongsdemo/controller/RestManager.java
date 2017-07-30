

package org.songs.intunestopsongsdemo.controller;

import org.songs.intunestopsongsdemo.model.callback.ITunesService;
import org.songs.intunestopsongsdemo.model.helper.Constants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RestManager {

    private ITunesService mITunesService;

    public ITunesService getITunesService() {
        if (mITunesService == null) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.HTTP.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            mITunesService = retrofit.create(ITunesService.class);
        }
        return mITunesService;
    }
}
