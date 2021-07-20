package com.example.FindShareParking.Objects;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.FindShareParking.CallBacks.GetWhoDidLikeCallBack;
import com.example.FindShareParking.R;

import java.util.ArrayList;

public class MyRecyclerViewNotify extends RecyclerView.Adapter<MyRecyclerViewNotify.ViewHolder> implements GetWhoDidLikeCallBack {

    private final ArrayList<Notify> mData;
    private final LayoutInflater mInflater;
    private final Context context;
    private ItemClickListener mClickListener;
    private final FireStoreManager fireStoreManager;
    private final ArrayList<String> whoDidLike;
    private final ArrayList<User> userWhoDidLike;

    // data is passed into the constructor
    public MyRecyclerViewNotify(Context context, ArrayList<Notify> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
        this.fireStoreManager = new FireStoreManager();
        this.whoDidLike = new ArrayList<>();
        this.userWhoDidLike = new ArrayList<>();
        this.fireStoreManager.initGetWhoDidLikeCallBack(this);
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_notify, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notify notify = mData.get(position);
        handleDisplay(notify, holder);

    }


    private void handleDisplay(Notify notify, ViewHolder holder) {
        int index = this.whoDidLike.indexOf(notify.getWhoDidLikeId());
        if (index >= 0) {
            User user = this.userWhoDidLike.get(index);
            holder.nameWhoDidLike.setText(user.getName());
            glide(user.getUserPhoto(), holder.imageRecyclerNotify);
        } else {
            fireStoreManager.getNameAndPhotoWhoDidLikeCallBack(notify.getWhoDidLikeId(), holder);
        }
    }

    private void glide(String userPhoto, ImageView imageRecyclerNotify) {
        Glide
                .with(context)
                .load(userPhoto)
                .centerCrop()
                .into(imageRecyclerNotify);

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void getWhoDidLikeCallBack(User user, ViewHolder holder) {
        whoDidLike.add(user.getUserId());
        userWhoDidLike.add(user);
        glide(user.getUserPhoto(), holder.imageRecyclerNotify);
        holder.nameWhoDidLike.setText(user.getName());

    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView nameWhoDidLike;
        ImageView imageRecyclerNotify;

        ViewHolder(View itemView) {
            super(itemView);
            nameWhoDidLike = itemView.findViewById(R.id.nameRecyclerNotify);
            imageRecyclerNotify = itemView.findViewById(R.id.imageRecyclerNotify);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public Notify getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}