package org.songs.intunestopsongsdemo.model.callback;

import org.songs.intunestopsongsdemo.model.pojo.ITunesSongsResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ITunesService {

    @GET("itunes-music/top-songs/50/explicit/json")
    Call<ITunesSongsResponse> getAllITuneSongs();
}
