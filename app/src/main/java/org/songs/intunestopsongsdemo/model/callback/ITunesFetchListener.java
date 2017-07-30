package org.songs.intunestopsongsdemo.model.callback;

import org.songs.intunestopsongsdemo.model.pojo.Result;

import java.util.List;


public interface ITunesFetchListener {

    void onDeliverAllSongs(List<Result> songs);

    void onDeliverSong(Result song);

    void onHideDialog();

    void isNetworkAvailaible();
}
