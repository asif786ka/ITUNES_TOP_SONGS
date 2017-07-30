/*
 * Copyright (c) 2015-2016 Filippo Engidashet. All Rights Reserved.
 * <p>
 *  Save to the extent permitted by law, you may not use, copy, modify,
 *  distribute or create derivative works of this material or any part
 *  of it without the prior written consent of Filippo Engidashet.
 *  <p>
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 */

package org.songs.intunestopsongsdemo.model.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.songs.intunestopsongsdemo.R;
import org.songs.intunestopsongsdemo.model.pojo.Result;

import java.util.ArrayList;
import java.util.List;

public class ITunesSongsAdapter extends RecyclerView.Adapter<ITunesSongsAdapter.Holder> {

    private static final String TAG = ITunesSongsAdapter.class.getSimpleName();
    private final ITuneSongsClickListener mListener;
    private List<Result> mItunes;

    public ITunesSongsAdapter(ITuneSongsClickListener listener) {
        mItunes = new ArrayList<>();
        mListener = listener;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item, null, false);
        return new Holder(row);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {

        Result currFlower = mItunes.get(position);

        holder.mName.setText(currFlower.getName());

        if (currFlower.isFromDatabase()) {
            holder.mPhoto.setImageBitmap(currFlower.getPicture());
        } else {
            Picasso.with(holder.itemView.getContext()).load(currFlower.getArtworkUrl100()).into(holder.mPhoto);
        }
    }

    @Override
    public int getItemCount() {
        return mItunes.size();
    }

    public void addITuneSong(Result ituneSong) {
        mItunes.add(ituneSong);
        notifyDataSetChanged();
    }

    /**
     * @param position
     * @return
     */
    public Result getSelectedFlower(int position) {
        return mItunes.get(position);
    }

    public void reset() {
        mItunes.clear();
        notifyDataSetChanged();
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mPhoto;
        private TextView mName, mPrice;

        public Holder(View itemView) {
            super(itemView);
            mPhoto = (ImageView) itemView.findViewById(R.id.itunePhoto);
            mName = (TextView) itemView.findViewById(R.id.ituneName);
            mPrice = (TextView) itemView.findViewById(R.id.ituneTitle);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(getLayoutPosition());
        }
    }

    public interface ITuneSongsClickListener {

        void onClick(int position);
    }
}
