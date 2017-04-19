package com.example.giggle.appmanager.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.giggle.appmanager.R;
import com.example.giggle.appmanager.bean.ProcessInfo;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractSwipeableItemViewHolder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by leishifang on 2017/4/19 18:48.
 */

public class ProcessesAdapter extends RecyclerView.Adapter<ProcessesAdapter.MyViewHolder> implements
        SwipeableItemAdapter<ProcessesAdapter.MyViewHolder> {

    private static final String TAG = "ProcessesAdapter";

    private List<ProcessInfo> mInfos;

    public ProcessesAdapter(List<ProcessInfo> infos) {
        setHasStableIds(true);
        mInfos = infos;
    }

    public static class MyViewHolder extends AbstractSwipeableItemViewHolder {

        @BindView(R.id.img_icon)
        ImageView mImgIcon;
        @BindView(R.id.tv_app_name)
        TextView mTvAppName;
        @BindView(R.id.tv_process_name)
        TextView mTvProcessName;

        public FrameLayout mContainerProcess;

        public MyViewHolder(View itemView) {
            super(itemView);
            mContainerProcess = (FrameLayout) itemView.findViewById(R.id.container_process);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public View getSwipeableContainerView() {
            return mContainerProcess;
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View rootView = inflater.inflate(R.layout.list_item_process, parent, false);
        return new MyViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ProcessInfo info = mInfos.get(position);
        holder.mImgIcon.setImageDrawable(info.getIcon());
        holder.mTvAppName.setText(info.getLable());
        holder.mTvProcessName.setText(info.getProcessName());
        // set swiping properties
        holder.setSwipeItemHorizontalSlideAmount(0);
    }

    @Override
    public int getItemCount() {
        return mInfos.size();
    }

    @Override
    public int onGetSwipeReactionType(MyViewHolder holder, int position, int x, int y) {
        return SwipeableItemConstants.REACTION_CAN_SWIPE_RIGHT;
    }

    @Override
    public void onSetSwipeBackground(MyViewHolder holder, int position, int type) {

    }

    @Override
    public SwipeResultAction onSwipeItem(MyViewHolder holder, int position, int result) {
        Log.d(TAG, "onSwipeItem: " + position);
        return null;
    }
}
