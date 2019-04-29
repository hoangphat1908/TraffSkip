package com.example.myapplication;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BookmarksAdapter extends RecyclerView.Adapter<BookmarksAdapter.ViewHolder> {

    private List<LocationSpotModel> mData = new ArrayList<>();;
    private LayoutInflater mInflater;


    // data is passed into the constructor
    BookmarksAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.bookmark_item_recycler_list, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindData(mData.get(position));
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // resets the list with a new set of data
    public void setItems(List<LocationSpotModel> items) {
        mData = items;
    }

    // stores and recycles views as they are scrolled off screen
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        TextView tv_address;
        TextView tv_open_close;

        ViewHolder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.item_name);
            tv_address = itemView.findViewById(R.id.item_address);
            tv_open_close = itemView.findViewById(R.id.item_open_close);
        }

        void bindData(LocationSpotModel item) {
            tv_name.setText(item.getLocationName());
            tv_address.setText(item.getLocationAddress());
            tv_open_close.setText(item.getOpenCloseTime());
        }
    }
}