package com.example.FindShareParking.Fragments.ParkingFragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.FindShareParking.CallBacks.GetAllNotifyCallBack;
import com.example.FindShareParking.CallBacks.PostNotifyCallBack;
import com.example.FindShareParking.Objects.FireStoreManager;
import com.example.FindShareParking.Objects.MyRecyclerViewNotify;
import com.example.FindShareParking.Objects.Notify;
import com.example.FindShareParking.databinding.NotifyFragmentBinding;

import java.util.ArrayList;

public class NotifyFragment extends Fragment implements MyRecyclerViewNotify.ItemClickListener, GetAllNotifyCallBack {

    private static final int BADGE_NOTIFY = 1;
    private NotifyFragmentBinding binding;
    private Context context;
    private MyRecyclerViewNotify adapter;
    private ArrayList<Notify> listNotify;
    private PostNotifyCallBack postNotifyCallBack;
    private FireStoreManager fireStoreManager;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = NotifyFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        initObjects();
        initCallBacks();
        getAllNotify();
        initRv();


        return view;
    }

    private void initRv() {
        binding.recyclerViewNotify.setLayoutManager(new LinearLayoutManager(context.getApplicationContext()));
        adapter = new MyRecyclerViewNotify(context, listNotify);
        adapter.setClickListener(this);
        binding.recyclerViewNotify.setAdapter(adapter);

    }

    private void getAllNotify() {
        fireStoreManager.getAllNotify();
    }

    private void initCallBacks() {
        fireStoreManager.initGetAllNotifyCallBack(this);
    }

    private void initObjects() {
        listNotify = new ArrayList<>();
        fireStoreManager = new FireStoreManager();
    }

    public void initPostNotifyCallBack(PostNotifyCallBack postNotifyCallBack) {
        this.postNotifyCallBack = postNotifyCallBack;
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    public void newLikeOnPosts(Notify notify) {
        this.listNotify.add(notify);
        fireStoreManager.uploadNotifyToFireStore(notify);
        adapter.notifyDataSetChanged();

        if (this.postNotifyCallBack != null) {
            this.postNotifyCallBack.increaseBadgeCallBack(BADGE_NOTIFY);
        }

    }

    //when the user delete his like
    public void deleteNotify(Notify notify) {

        listNotify.remove(notify);
        adapter.notifyDataSetChanged();
        fireStoreManager.deleteNotifyFromFireStore(notify);

        if (this.postNotifyCallBack != null) {
            this.postNotifyCallBack.decreaseBadgeCallBack(BADGE_NOTIFY);
        }
    }

    @Override
    public void getAllNotifyCallBack(ArrayList<Notify> notifies) {
        this.listNotify.addAll(0, notifies);
        adapter.notifyItemRangeInserted(0, notifies.size());

    }

}
