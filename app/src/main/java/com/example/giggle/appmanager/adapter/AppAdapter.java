/*
 *    Copyright (C) 2015 Haruki Hasegawa
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.example.giggle.appmanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.giggle.appmanager.R;
import com.example.giggle.appmanager.bean.AppInfo;
import com.example.giggle.appmanager.utils.DateUtils;
import com.example.giggle.appmanager.widget.ExpandableItemIndicator;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemViewHolder;

import java.text.DateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AppAdapter
        extends AbstractExpandableItemAdapter<AppAdapter.MyGroupViewHolder,
        AppAdapter.MyChildViewHolder> {

    private static final String TAG = "AppAdapter";
    List<AppInfo> mAppInfos;

    private interface Expandable extends ExpandableItemConstants {

    }

    public static class MyGroupViewHolder extends AbstractExpandableItemViewHolder {

        public ExpandableItemIndicator mIndicator;
        private ImageView iconImageView;
        private TextView lableTextView;
        private TextView sizeTextView;

        public MyGroupViewHolder(View v) {
            super(v);
            mIndicator = (ExpandableItemIndicator) v.findViewById(R.id.indicator);
            iconImageView = (ImageView) v.findViewById(R.id.img_icon);
            lableTextView = (TextView) v.findViewById(R.id.tv_lable);
            sizeTextView = (TextView) v.findViewById(R.id.tv_size);
        }
    }

    public static class MyChildViewHolder extends AbstractExpandableItemViewHolder {

        @BindView(R.id.tv_package_name)
        TextView mTvPackageName;
        @BindView(R.id.tv_version)
        TextView mTvVersion;
        @BindView(R.id.tv_install_time)
        TextView mTvInstallTime;
        @BindView(R.id.tv_update_time)
        TextView mTvUpdateTime;

        public MyChildViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    public AppAdapter() {
        setHasStableIds(true);
    }

    public AppAdapter(List<AppInfo> infos) {
        this();
        this.mAppInfos = infos;
        // ExpandableItemAdapter requires stable ID, and also
        // have to implement the getGroupItemId()/getChildItemId() methods appropriately.
    }

    public void setData(List<AppInfo> infos) {
        mAppInfos = infos;
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return mAppInfos.size();
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
    public int getGroupItemViewType(int groupPosition) {
        return 0;
    }

    @Override
    public int getChildItemViewType(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public MyGroupViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.list_group_item_app, parent, false);
        return new MyGroupViewHolder(v);
    }

    @Override
    public MyChildViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.list_child_item_app, parent, false);
        return new MyChildViewHolder(v);
    }

    @Override
    public void onBindGroupViewHolder(MyGroupViewHolder holder, int groupPosition, int viewType) {
        updateIndicatorState(holder);
        AppInfo info = mAppInfos.get(groupPosition);
        holder.lableTextView.setText(info.getLable());
        holder.iconImageView.setImageDrawable(info.getIcon());
        holder.sizeTextView.setText((info.getSize() >> 20) + " MB");
    }

    @Override
    public void onBindChildViewHolder(MyChildViewHolder holder, int groupPosition, int childPosition, int
            viewType) {
        AppInfo info = mAppInfos.get(groupPosition);
        holder.mTvPackageName.setText(info.getPackageName());
        holder.mTvVersion.setText(info.getVersion());
        holder.mTvInstallTime.setText(DateUtils.convertTimeMill(info.getFirstInstallTime()));
        holder.mTvUpdateTime.setText(DateUtils.convertTimeMill(info.getLastUpdateTime()));
    }

    private void updateIndicatorState(MyGroupViewHolder holder) {
        final int expandState = holder.getExpandStateFlags();

        if ((expandState & ExpandableItemConstants.STATE_FLAG_IS_UPDATED) != 0) {
            boolean isExpanded;
            boolean animateIndicator = ((expandState & Expandable.STATE_FLAG_HAS_EXPANDED_STATE_CHANGED) !=
                    0);

            if ((expandState & Expandable.STATE_FLAG_IS_EXPANDED) != 0) {
                isExpanded = true;
            } else {
                isExpanded = false;
            }

            holder.mIndicator.setExpandedState(isExpanded, animateIndicator);
        }
    }

    @Override
    public boolean onCheckCanExpandOrCollapseGroup(MyGroupViewHolder holder, int groupPosition, int x, int
            y, boolean expand) {

        return true;
    }
}
