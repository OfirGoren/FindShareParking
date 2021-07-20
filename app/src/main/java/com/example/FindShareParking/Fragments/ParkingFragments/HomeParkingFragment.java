package com.example.FindShareParking.Fragments.ParkingFragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.FindShareParking.CallBacks.AllPostCallBack;
import com.example.FindShareParking.CallBacks.PostNotifyCallBack;
import com.example.FindShareParking.Compartors.FromBigToSmall;
import com.example.FindShareParking.Objects.FireStoreManager;
import com.example.FindShareParking.Objects.MyRecyclerViewAdapter;
import com.example.FindShareParking.Objects.Notify;
import com.example.FindShareParking.Objects.Post;
import com.example.FindShareParking.R;
import com.example.FindShareParking.Utils.AuthUtils;
import com.example.FindShareParking.Utils.SharedPrefUtils;
import com.example.FindShareParking.databinding.FragmentHomeParkingBinding;

import java.util.ArrayList;
import java.util.Collections;

public class HomeParkingFragment extends Fragment implements AllPostCallBack, MyRecyclerViewAdapter.ItemClickListener
        , SearchView.OnQueryTextListener {


    private PostNotifyCallBack postNotifyCallBack;
    private Context context;
    private boolean isFirstClickSearch;
    private boolean likeChanged;
    private FragmentHomeParkingBinding binding;
    private MyRecyclerViewAdapter adapter;
    private ArrayList<Post> posts;
    private FireStoreManager fireStoreManager;
    private ArrayList<Post> newPostWaitRefresh;
    private ArrayList<Integer> positionsItemsWasLikeChanges;
    private String currentUserIdWasMakeLike;
    private String filedIdPost;
    private ArrayList<Integer> indexInPostsWasRemoved;
    private ArrayList<String> whoChangedLike;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        binding = FragmentHomeParkingBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        initValues();
        initObjects();
        initColors();
        initRecyclerView();
        requestAllPostInFireStore();
        initCallBacks();
        return view;
    }

    private void requestAllPostInFireStore() {
        if (fireStoreManager != null) {
            fireStoreManager.getAllPost();
        }
    }

    private void initColors() {
        binding.swipeLayout.setColorSchemeResources(R.color.orange);
    }


    private void initRecyclerView() {
        binding.recyclerViewAllPost.setLayoutManager(new LinearLayoutManager(context.getApplicationContext()));
        adapter = new MyRecyclerViewAdapter(context, this.posts);
        adapter.setClickListener(this);
        binding.recyclerViewAllPost.setAdapter(adapter);
    }

    private void initValues() {
        likeChanged = false;
        isFirstClickSearch = true;
    }


    private void initCallBacks() {
        fireStoreManager.initAllPostCallBack(this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        fireStoreManager.stopAllPostSnapShotListener();

    }


    private void initObjects() {
        newPostWaitRefresh = new ArrayList<>();
        this.posts = new ArrayList<>();
        whoChangedLike = new ArrayList<>();
        fireStoreManager = new FireStoreManager();
        positionsItemsWasLikeChanges = new ArrayList<>();
        indexInPostsWasRemoved = new ArrayList<>();
    }

    private void makeRefreshToRecyclerViewList() {

        handleLikeChangedRv();
        handleNewPostRv();
        handleRemovePostsRv();

    }

    private void handleRemovePostsRv() {
        if (indexInPostsWasRemoved.size() > 0) {
            removedPostsFromRecyclerView();
            indexInPostsWasRemoved.clear();

        }
    }

    private void handleNewPostRv() {
        if (newPostWaitRefresh.size() > 0) {
            posts.addAll(0, newPostWaitRefresh);
            adapter.notifyItemRangeInserted(0, newPostWaitRefresh.size());
            newPostWaitRefresh.clear();
        }
    }


    private void handleLikeChangedRv() {
        if (likeChanged) {
            for (Integer position : positionsItemsWasLikeChanges) {
                adapter.notifyItemChanged(position);
            }
            likeChanged = false;
        }
        positionsItemsWasLikeChanges.clear();
    }

    private void removedPostsFromRecyclerView() {
        Collections.sort(indexInPostsWasRemoved, new FromBigToSmall());
        for (Integer position : indexInPostsWasRemoved) {
            this.posts.remove(position.intValue());
            adapter.notifyItemRemoved(position);

        }
    }

    SwipeRefreshLayout.OnRefreshListener swipeRefreshLayout = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            makeRefreshToRecyclerViewList();
            binding.swipeLayout.setRefreshing(false);
        }
    };

    public void initNewPostCallBack(PostNotifyCallBack postNotifyCallBack) {
        this.postNotifyCallBack = postNotifyCallBack;


    }

    private void listeners() {
        binding.swipeLayout.setOnRefreshListener(swipeRefreshLayout);
        binding.includeHomeParking.topAppBar.setOnMenuItemClickListener(clickItem);
    }


    private final Toolbar.OnMenuItemClickListener clickItem = item -> {
        int id = item.getItemId();
        if (id == R.id.addPost) {
            if (postNotifyCallBack != null) {
                postNotifyCallBack.openNewPostFragment();
            }
        } else if (id == R.id.search) {
            //activate Search listener once
            if (isFirstClickSearch) {
                textListener(item);
                isFirstClickSearch = false;
            }
        }
        return true;
    };

    private void textListener(MenuItem item) {
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                adapter.getFilter().filter(newText);
                return true;
            }
        });
    }


    @Override
    public void allPostsCallBack(ArrayList<Post> posts) {

        //insert the posts to recycler view

        filteringPostsAccordingTypeOfParking(posts);
        insertPostsToRecyclerView(posts);


    }

    @Override
    public void addNewPost(ArrayList<Post> newPosts) {
        if (this.posts == null) {
            this.posts = new ArrayList<>();
        }
        if (newPosts.size() > 0) {
            this.posts.addAll(0, newPosts);
            adapter.notifyItemRangeInserted(0, newPosts.size());

        }

        binding.swipeLayout.setRefreshing(false);
        binding.swipeLayout.setEnabled(true);


    }


    private void filteringPostsAccordingTypeOfParking(ArrayList<Post> allPost) {
        String typeOfParking = SharedPrefUtils.getInstance().getSummaryFromSettingsListPreferencePref();
        if (!typeOfParking.equals(getString(R.string.All))) {
            ArrayList<Post> posts = new ArrayList<>(allPost);
            for (Post post : posts) {
                if (!typeOfParking.equals(post.getTypeOfParking())) {
                    allPost.remove(post);
                }
            }
        }
    }

    @Override
    public void postsWasChanges(ArrayList<Post> allPostsWasChanges) {

        filteringPostsAccordingTypeOfParking(allPostsWasChanges);
        for (Post newPost : allPostsWasChanges) {
            //when post exists
            if (this.posts.contains(newPost)) {
                //get the position in allPostsWasChanges
                int index = this.posts.indexOf(newPost);
                //when the position is found
                if (index >= 0) {
                    notifyAndUpdateLikes(newPost, whoChangedLike, index);
                }
                // when the post doesn't exists
            } else {
                // when the post belong to current user
                if (newPost.getCurrentUserId().equals(AuthUtils.getCurrentUser())) {
                    //add the post to the list
                    addToRvNewPost(newPost);
                    binding.linearProgressBar.setVisibility(View.GONE);
                } else {
                    // check and handle if the post was remove , new like , new post
                    replaceRemoveOrAddNewPostToListRefresh(newPost);
                }
            }
        }
        // to notify that need to update the posts in list (handle search filter)
        adapter.setThereWasChangePostsInRV(true);
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            isNeedToRefreshNow();
        }

    }

    private void isNeedToRefreshNow() {
        int positionWhoChangedLike = whoChangedLike.indexOf(AuthUtils.getCurrentUser());
        if (positionWhoChangedLike >= 0) {

            makeRefreshToRecyclerViewList();
            whoChangedLike.clear();
        }
    }

    private void addToRvNewPost(Post newPost) {
        this.posts.add(0, newPost);
        adapter.notifyItemInserted(0);
    }

    private void addToPositionItemsWasLikeChangedList(int index) {
        if (!positionsItemsWasLikeChanges.contains(index)) {
            positionsItemsWasLikeChanges.add(index);
        }
    }

    private void replaceRemoveOrAddNewPostToListRefresh(Post newPost) {

        int positionNewPost = this.newPostWaitRefresh.indexOf(newPost);

        if (positionNewPost >= 0) {
            Post postWaitForRefresh = this.newPostWaitRefresh.get(positionNewPost);
            if (checkIfLikeWasChanged(newPost, postWaitForRefresh)) {
                this.newPostWaitRefresh.remove(positionNewPost);
                this.newPostWaitRefresh.add(positionNewPost, newPost);

                // the user that make the post delete the post
            } else {
                this.newPostWaitRefresh.remove(newPost);
            }
        } else {
            this.newPostWaitRefresh.add(newPost);
        }
    }


    private boolean checkIfLikeWasChanged(Post newPost, Post positionNewPost) {
        return Post.isNewLikeOnPost(positionNewPost, newPost) || Post.isDeleteLikeOnPost(positionNewPost, newPost);
    }


    private void notifyAndUpdateLikes(Post newPost, ArrayList<String> whoChangedLike, int index) {

        //when there is new like on post
        if (Post.isNewLikeOnPost(this.posts.get(index), newPost)) {
            //when this post belong to user
            if (AuthUtils.getCurrentUser().equals(this.posts.get(index).getCurrentUserId())) {
                //show notify
                createNewNotify(this.posts.get(index), newPost);
            }
            whoChangedLike.addAll(Post.getWhoDidLike(this.posts.get(index), newPost));
            saveAndUpdateWhoChangedLike(newPost, index);
            //when user delete like on post
        } else if (Post.isDeleteLikeOnPost(this.posts.get(index), newPost)) {
            //when this post belong to user
            if (AuthUtils.getCurrentUser().equals(this.posts.get(index).getCurrentUserId())) {
                //delete notify like
                deleteNotify(this.posts.get(index), newPost);
            }
            whoChangedLike.addAll(Post.getWhoRemoveLike(this.posts.get(index), newPost));
            saveAndUpdateWhoChangedLike(newPost, index);

        } else {
            addPositionsInPostsWasRemoved(index);

        }

    }

    private void saveAndUpdateWhoChangedLike(Post newPost, int index) {
        addToPositionItemsWasLikeChangedList(index);
        Post.copyMakeLikes(this.posts.get(index), newPost);
        likeChanged = true;
    }


    private void addPositionsInPostsWasRemoved(int index) {
        if (!indexInPostsWasRemoved.contains(index)) {
            indexInPostsWasRemoved.add(index);
        }

    }

    private void createNewNotify(Post beforeChangePost, Post afterChangePost) {
        ArrayList<String> whoDidLike = Post.getWhoDidLike(beforeChangePost, afterChangePost);
        for (String id : whoDidLike) {
            if (!id.equals(AuthUtils.getCurrentUser())) {
                this.currentUserIdWasMakeLike = afterChangePost.getCurrentUserId();
                this.filedIdPost = afterChangePost.getFiledId();
                getUserDetailWhoDidLikeCallBack(id);

            }
        }
    }


    public void getUserDetailWhoDidLikeCallBack(String id) {
        Notify notify = new Notify(id, this.currentUserIdWasMakeLike, this.filedIdPost);
        this.postNotifyCallBack.newLike(notify);


    }

    public void getUserDetailWhoRemoveLikeCallBack(String id) {
        Notify notify = new Notify(id, this.currentUserIdWasMakeLike, this.filedIdPost);
        this.postNotifyCallBack.deleteNotify(notify);
    }

    private void deleteNotify(Post beforeChangePost, Post afterChangePost) {
        ArrayList<String> whoDidLike = Post.getWhoRemoveLike(beforeChangePost, afterChangePost);
        for (String id : whoDidLike) {
            if (!id.equals(AuthUtils.getCurrentUser())) {
                this.currentUserIdWasMakeLike = afterChangePost.getCurrentUserId();
                this.filedIdPost = afterChangePost.getFiledId();
                getUserDetailWhoRemoveLikeCallBack(id);

            }

        }

    }

    @Override
    public void setRefreshingOff() {
        binding.swipeLayout.setRefreshing(false);

    }


    //insert the posts to recycler view
    private void insertPostsToRecyclerView(ArrayList<Post> posts) {
        if (this.posts == null) {
            this.posts = new ArrayList<>();
        }

        this.posts.clear();
        adapter.notifyItemRangeRemoved(0, this.posts.size());
        this.posts.addAll(posts);
        adapter.notifyItemRangeInserted(0, posts.size());
        //init listeners
        listeners();

    }


    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.getFilter().filter(newText);
        filterQuery(newText);

        return false;
    }


    private void filterQuery(String newText) {
        ArrayList<Post> filterPosts = new ArrayList<>();
        for (Post post : this.posts) {
            if (post.getLocationParking().contains(newText)) {
                filterPosts.add(post);
            }
        }
        adapter.setFilter(filterPosts);
    }


    public void activateDataChangedRecyclerView() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public void showOnlyPostsAccordingKindOfParking() {
        fireStoreManager.setReturnAllPost(true);
        fireStoreManager.getAllPost();
        // clear and copy all posts to allPostList (to make filter)
        adapter.setThereWasChangePostsInRV(true);

    }

    public void turnOnLinearProgressBar() {
        binding.linearProgressBar.setVisibility(View.VISIBLE);

    }


}