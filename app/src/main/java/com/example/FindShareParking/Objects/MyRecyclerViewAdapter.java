package com.example.FindShareParking.Objects;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.FindShareParking.CallBacks.GetUserCallBack;
import com.example.FindShareParking.R;
import com.google.android.gms.maps.MapView;
import com.google.android.material.button.MaterialButton;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;
import java.util.Collection;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder>
        implements Filterable, GetUserCallBack {

    private ArrayList<Post> postList;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private final Context context;
    private boolean thereWasChangePostsInRV = true;
    private final ArrayList<Post> allPostList;
    private final FireStoreManager fireStoreManager;
    private final ArrayList<User> users;


    // data is passed into the constructor
    public MyRecyclerViewAdapter(Context context, ArrayList<Post> data) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.postList = data;
        this.allPostList = new ArrayList<>();
        this.fireStoreManager = new FireStoreManager();
        this.fireStoreManager.initGetUserCallBack(this);
        this.users = new ArrayList<>();


    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_parking_main, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = postList.get(position);

        displayNameAndPhoto(holder, post);
        holder.kindOfParking.setText(post.getTypeOfParking());
        holder.textPost.setText(post.getLocationParking());
        holder.textMoreInfo.setText(post.getMoreInfo());
        holder.amountOfLikes.setText(String.valueOf(post.getAmountOfLikes()));
        layLikeListener(holder);
        likeBtnListener(holder, post);
        glideParkingPhoto(holder.parkingPhoto, post);
        isCurrentUserMakeLike(holder, post);
        initMap(holder.mapView, post.getLatitude(), post.getLongitude());
        navigateButtonListener(holder.navigateButton, post.getLatitude(), post.getLongitude());

    }


    private void displayNameAndPhoto(ViewHolder holder, Post post) {
        int index = isUserAlreadyInArray(post.getCurrentUserId());
        if (index < 0) {
            fireStoreManager.getUserFromFireStoreCallBack(holder, post);
        } else {
            User user = users.get(index);
            holder.nameOnPost.setText(user.getName());
            glideUserPhoto(holder.myPic, user);
        }

    }

    private int isUserAlreadyInArray(String currentUserId) {
        User user = new User().setUserId(currentUserId);
        return users.indexOf(user);
    }

    @Override
    public void getUserCallBack(User user, MyRecyclerViewAdapter.ViewHolder currentHolder, Post post) {
        if (user != null) {
            currentHolder.nameOnPost.setText(user.getName());
            glideUserPhoto(currentHolder.myPic, user);
            users.add(user);
        }
    }


    private void navigateButtonListener(MaterialButton navigateButton, String latitude, String longitude) {
        navigateButton.setOnClickListener(v -> openNavigateMap(latitude, longitude));

    }

    private void openNavigateMap(String latitude, String longitude) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        context.startActivity(mapIntent);
    }


    private void isCurrentUserMakeLike(ViewHolder holder, Post post) {
        if (post.isCurrentUserMakeLike()) {
            holder.textLike.setTextColor(context.getResources().getColor(R.color.orange));
            holder.likeBtn.setLiked(true);

        } else {
            holder.textLike.setTextColor(context.getResources().getColor(R.color.gray));
            holder.likeBtn.setLiked(false);
        }
    }


    private void glideParkingPhoto(ImageView parkingPhoto, Post post) {
        Glide
                .with(context)
                .load(post.getImageLocationStorage())
                .centerCrop()
                .into(parkingPhoto);


    }

    private void glideUserPhoto(ImageView myPic, User user) {
        if (!user.getUserPhoto().equals("")) {
            Glide
                    .with(context)
                    .load(user.getUserPhoto())
                    .centerCrop()
                    .into(myPic);
        }
    }


    private void layLikeListener(ViewHolder holder) {
        holder.layLike.setOnClickListener(v -> {
            //activate click on icon like
            holder.likeBtn.performClick();

        });

    }

    private void likeBtnListener(ViewHolder holder, Post post) {

        holder.likeBtn.setOnLikeListener(new OnLikeListener() {

            @Override
            public void liked(LikeButton likeButton) {
                holder.textLike.setTextColor(context.getResources().getColor(R.color.orange));
                FireStoreManager.updateLike(post);
                holder.amountOfLikes.setText(String.valueOf(post.getAmountOfLikes() + 1));


            }

            @Override
            public void unLiked(LikeButton likeButton) {
                holder.textLike.setTextColor(context.getResources().getColor(R.color.gray));
                if (post.getAmountOfLikes() > 0) {
                    holder.amountOfLikes.setText(String.valueOf(post.getAmountOfLikes() - 1));
                } else {
                    holder.amountOfLikes.setText(String.valueOf(0));
                }
                FireStoreManager.deleteLikeFromFireStore(post);

            }

        });


    }


    private void initMap(MapView mapView, String latitude, String longitude) {
        new MapManagerRecyclerView(mapView, latitude, longitude);


    }


    // total number of rows
    @Override
    public int getItemCount() {
        return postList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }


    public void setThereWasChangePostsInRV(boolean value) {
        thereWasChangePostsInRV = value;
    }

    private final Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            if (thereWasChangePostsInRV) {
                allPostList.clear();
                allPostList.addAll(postList);
                thereWasChangePostsInRV = false;
            }
            ArrayList<Post> filteredPost = new ArrayList<>();
            if (constraint.toString().isEmpty()) {
                filteredPost.addAll(allPostList);

            } else {
                for (Post post : allPostList) {
                    if (post.getLocationParking().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        filteredPost.add(post);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredPost;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            postList.clear();
            //noinspection unchecked
            postList.addAll((Collection<? extends Post>) results.values);
            notifyDataSetChanged();
        }
    };


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView myPic;
        private final TextView nameOnPost;
        private final TextView kindOfParking;
        private final TextView textPost;
        private final TextView textMoreInfo;
        private final TextView amountOfLikes;
        private final MapView mapView;
        private final TextView textLike;
        private final LikeButton likeBtn;
        private final MaterialButton navigateButton;
        private final ImageView parkingPhoto;
        private final LinearLayout layLike;


        public ViewHolder(View itemView) {
            super(itemView);
            myPic = itemView.findViewById(R.id.myPic);
            nameOnPost = itemView.findViewById(R.id.nameOnPost);
            kindOfParking = itemView.findViewById(R.id.kindOfParking);
            textPost = itemView.findViewById(R.id.textPost);
            textMoreInfo = itemView.findViewById(R.id.textMoreInfo);
            mapView = itemView.findViewById(R.id.map);
            parkingPhoto = itemView.findViewById(R.id.parkingPhoto);
            amountOfLikes = itemView.findViewById(R.id.amountOfLikes);
            layLike = itemView.findViewById(R.id.layLike);
            navigateButton = itemView.findViewById(R.id.navigateButton);
            textLike = itemView.findViewById(R.id.textLike);
            likeBtn = itemView.findViewById(R.id.likeBtn);
            itemView.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    // convenience method for getting data at click position
    Post getItem(int id) {
        return postList.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public void clear() {
        this.postList.clear();

    }

    public void setPostList(ArrayList<Post> postList) {
        this.postList = postList;
    }

    /* access modifiers changed from: 0000 */
    public void setFilter(ArrayList<Post> filteredPost) {
        this.postList = filteredPost;
        notifyDataSetChanged();
    }


}
