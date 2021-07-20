package com.example.FindShareParking.Fragments.ParkingFragments;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.FindShareParking.CallBacks.MyPostsCallBack;
import com.example.FindShareParking.Objects.FireStoreManager;
import com.example.FindShareParking.Objects.MyRecyclerViewAdapter;
import com.example.FindShareParking.Objects.Post;
import com.example.FindShareParking.R;
import com.example.FindShareParking.databinding.FragmentMyPostBinding;

import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MyPostFragment extends Fragment implements MyPostsCallBack, MyRecyclerViewAdapter.ItemClickListener {


    private FragmentMyPostBinding binding;
    private Context context;
    private ArrayList<Post> posts;
    private MyRecyclerViewAdapter adapter;
    private ArrayList<Integer> itemsPositionChange;
    private boolean likeChanged;
    private ArrayList<Post> newPosts;
    private Post whichPostRemoved;
    private FireStoreManager fireStoreManager;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMyPostBinding.inflate(inflater, container, false);
        View view = binding.getRoot();


        initObjects();
        initRecyclerView();
        initValues();
        initCallBacks();
        getMyPostsFromFireStore();
        initDeleteFromRv();


        return view;
    }


    private void initDeleteFromRv() {
        new ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(binding.recyclerViewMyPosts);
    }


    private void getMyPostsFromFireStore() {
        fireStoreManager.getMyPost();
    }


    private void initValues() {
        likeChanged = false;
    }

    private void initObjects() {
        this.posts = new ArrayList<>();
        newPosts = new ArrayList<>();
        itemsPositionChange = new ArrayList<>();
        fireStoreManager = new FireStoreManager();
        whichPostRemoved = new Post();

    }

    private void initCallBacks() {
        fireStoreManager.initMyPostCallBack(this);
    }


    private void initRecyclerView() {
        binding.recyclerViewMyPosts.setLayoutManager(new LinearLayoutManager(context));
        adapter = new MyRecyclerViewAdapter(context, this.posts);
        adapter.setClickListener(this);
        binding.recyclerViewMyPosts.setAdapter(adapter);

    }


    @Override
    public void myPostsCallBack(ArrayList<Post> myPost) {
        this.posts.addAll(0, myPost);
        adapter.notifyItemRangeInserted(0, myPost.size());


    }

    private void addOrUpdatePostInRecyclerView(ArrayList<Post> myPost) {
        for (Post newPost : myPost) {
            //when post exists
            if (this.posts.contains(newPost)) {
                //get the position in posts
                int index = this.posts.indexOf(newPost);
                if (index >= 0) {
                    //when the current user make like or remove like on his post (from main list)
                    if (Post.isNewLikeOnPost(this.posts.get(index), newPost) || Post.isDeleteLikeOnPost(this.posts.get(index), newPost)) {
                        //when the position not in array
                        if (!itemsPositionChange.contains(index)) {
                            //add the position of item that was changed
                            itemsPositionChange.add(index);
                        }
                        //update also the like in the  My Post list
                        this.posts.get(index).getMakesLike().clear();
                        this.posts.get(index).getMakesLike().addAll(newPost.getMakesLike());
                        likeChanged = true;
                    }
                }
                //when the post doesn't exists
            } else {
                //when the post is new (not removed)
                if (!whichPostRemoved.equals(newPost)) {
                    //add to new post(recyclerView)
                    this.newPosts.add(newPost);
                    handleNotifyChanged();
                }


            }
        }

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            handleNotifyChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fireStoreManager.stopMyPostSnapShotListener();
    }

    private void handleNotifyChanged() {
        if (likeChanged) {
            for (Integer position : itemsPositionChange) {
                adapter.notifyItemChanged(position);
            }
        }
        if (newPosts.size() > 0) {
            posts.addAll(0, newPosts);
            adapter.notifyItemRangeInserted(0, newPosts.size());
            newPosts.clear();
        }

        itemsPositionChange.clear();
        likeChanged = false;

    }

    @Override
    public void postChanges(ArrayList<Post> posts) {


        addOrUpdatePostInRecyclerView(posts);


    }


    @Override
    public void onItemClick(View view, int position) {

    }

    public void activateDataChangedRecyclerView() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    /*
     handle deletePosts from Rv
     */
    ItemTouchHelper.SimpleCallback itemTouchHelperCallBack = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            whichPostRemoved = posts.get(viewHolder.getAdapterPosition());
            fireStoreManager.deletePost(posts.get(viewHolder.getAdapterPosition()));
            posts.remove(viewHolder.getAdapterPosition());
            adapter.notifyItemRemoved(viewHolder.getAdapterPosition());

        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(context, R.color.orange))
                    .addSwipeLeftActionIcon(R.drawable.delete_80dp)
                    .create()
                    .decorate();


        }
    };

}