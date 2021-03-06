package com.example.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FloorsAdapter extends  RecyclerView.Adapter<FloorsAdapter.ViewHolder> {

    private List<Floor> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private Context context;

    // data is passed into the constructor
    FloorsAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.floor_item_recycler_list, parent, false);
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
    public void setItems(List<Floor> items) {
        mData = items;
    }

    // stores and recycles views as they are scrolled off screen
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_name;
        TextView txt_remain_total;
        Button showOccupancy;
        ImageView occupancyImageView;
        boolean shown = false;

        ViewHolder(View itemView) {
            super(itemView);
            txt_name = itemView.findViewById(R.id.item_name);
            txt_remain_total = itemView.findViewById(R.id.item_remain_total);
            showOccupancy = itemView.findViewById(R.id.show_occupancy_button);
            occupancyImageView = itemView.findViewById(R.id.occupancy_image_view);
        }

        void bindData(Floor item) {
            txt_name.setText(item.getName());
            int remain = item.getRemaining();
            int total = item.getTotal();

            txt_remain_total.setText(item.getRemaining() + " / " + item.getTotal() + " spots left!");
            double percentage = (double) remain / total;
            if (percentage < 0.15)
                txt_remain_total.setTextColor(Color.RED);
            else if (percentage < 0.4)
                txt_remain_total.setTextColor(Color.rgb(252,102,0));
            else
                txt_remain_total.setTextColor(Color.GREEN);

            if (item.getOccupancyImage()!=null) {
                showOccupancy.setVisibility(View.VISIBLE);
                Picasso.with(context).load(item.getOccupancyImage()).into(occupancyImageView);
                showOccupancy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        occupancyImageView.setVisibility(shown ? View.GONE : View.VISIBLE);
                        shown = !shown;
                    }
                });
            }


        }
    }
}