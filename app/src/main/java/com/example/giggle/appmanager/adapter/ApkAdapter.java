package com.example.giggle.appmanager.adapter;

import android.support.annotation.IntRange;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.giggle.appmanager.R;
import com.example.giggle.appmanager.bean.ApkInfo;
import com.example.giggle.appmanager.utils.DateUtils;
import com.example.giggle.appmanager.widget.ExpandableItemIndicator;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemViewHolder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by leishifang on 2017/4/18 19:22.
 */

public class ApkAdapter extends AbstractExpandableItemAdapter<ApkAdapter.GroupViewHolder, ApkAdapter
        .ChildViewHolder> {

    private List<ApkInfo> data;

    private interface Expandable extends ExpandableItemConstants {

    }

    public ApkAdapter(List<ApkInfo> apkInfoList) {
        setHasStableIds(true);
        data = apkInfoList;
    }

    public static class GroupViewHolder extends AbstractExpandableItemViewHolder {

        @BindView(R.id.indicator)
        ExpandableItemIndicator mIndicator;
        @BindView(R.id.img_icon)
        ImageView mImgIcon;
        @BindView(R.id.tv_lable)
        TextView mTvLable;
        @BindView(R.id.tv_size)
        TextView mTvSize;

        public GroupViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class ChildViewHolder extends AbstractExpandableItemViewHolder {

        @BindView(R.id.tv_update_time)
        TextView mTvUpdateTime;
        @BindView(R.id.tv_version)
        TextView mTvVersion;
        @BindView(R.id.tv_package_name)
        TextView mTvPackageName;
        @BindView(R.id.tv_apk_path)
        TextView mTvApkPath;

        public ChildViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setData(List<ApkInfo> infos) {
        data = infos;
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return data.size();
    }

    @Override
    public int getChildCount(int groupPosition) {
        return 1;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public GroupViewHolder onCreateGroupViewHolder(ViewGroup parent, @IntRange(from = -8388608L, to =
            8388607L) int viewType) {
        View groupView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_group_item, parent,
                false);
        return new GroupViewHolder(groupView);
    }

    @Override
    public ChildViewHolder onCreateChildViewHolder(ViewGroup parent, @IntRange(from = -8388608L, to =
            8388607L) int viewType) {
        View childView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_child_item_apk,
                parent, false);
        return new ChildViewHolder(childView);
    }

    @Override
    public void onBindGroupViewHolder(GroupViewHolder holder, int groupPosition, @IntRange(from =
            -8388608L, to = 8388607L) int viewType) {
        updateIndicatorState(holder);
        ApkInfo info = data.get(groupPosition);
        holder.mTvLable.setText(info.getLable());
        holder.mImgIcon.setImageDrawable(info.getIcon());
        holder.mTvSize.setText((info.getSize() >> 20) + "MB");
    }

    @Override
    public void onBindChildViewHolder(ChildViewHolder holder, int groupPosition, int childPosition,
                                      @IntRange(from = -8388608L, to = 8388607L) int viewType) {
        ApkInfo info = data.get(groupPosition);
        holder.mTvVersion.setText(info.getVersionName());
        holder.mTvUpdateTime.setText(DateUtils.convertTimeMill(info.getTime()));
        holder.mTvApkPath.setText(info.getApkPath());
        holder.mTvPackageName.setText(info.getPackageName());
    }

    @Override
    public boolean onCheckCanExpandOrCollapseGroup(GroupViewHolder holder, int groupPosition, int x, int y,
                                                   boolean expand) {
        return true;
    }

    private void updateIndicatorState(GroupViewHolder holder) {
        final int expandState = holder.getExpandStateFlags();

        if ((expandState & ExpandableItemConstants.STATE_FLAG_IS_UPDATED) != 0) {
            boolean isExpanded;
            boolean animateIndicator = ((expandState & Expandable
                    .STATE_FLAG_HAS_EXPANDED_STATE_CHANGED) !=
                    0);

            if ((expandState & Expandable.STATE_FLAG_IS_EXPANDED) != 0) {
                isExpanded = true;
            } else {
                isExpanded = false;
            }

            holder.mIndicator.setExpandedState(isExpanded, animateIndicator);
        }
    }
}
