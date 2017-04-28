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

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.giggle.appmanager.MyApplication;
import com.example.giggle.appmanager.R;
import com.example.giggle.appmanager.bean.AppInfo;
import com.example.giggle.appmanager.ui.AppListActivity;
import com.example.giggle.appmanager.utils.DateUtils;
import com.example.giggle.appmanager.widget.ExpandableItemIndicator;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemViewHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AppAdapter
        extends AbstractExpandableItemAdapter<AppAdapter.MyGroupViewHolder,
        AppAdapter.MyChildViewHolder> implements Filterable {

    private static final String TAG = "AppAdapter";

    private List<AppInfo> mAppInfos;
    private List<AppInfo> mAllInfos;

    private AppListActivity mContext;
    /**
     * 卸载应用的position
     */
    private int currentPosition;
    private EventListener mEventListener;

    public interface EventListener {

        void updataAppCount(int count);
    }

    private interface Expandable extends ExpandableItemConstants {

    }

    public AppAdapter() {
        // ExpandableItemAdapter requires stable ID, and also
        // have to implement the getGroupItemId()/getChildItemId() methods appropriately.
        setHasStableIds(true);
    }

    public AppAdapter(List<AppInfo> infos, AppListActivity context, EventListener e) {
        this();

        mContext = context;
        mEventListener = e;
        setData(infos);
    }

    public void setData(List<AppInfo> infos) {
        this.mAppInfos = new ArrayList<>();
        mAppInfos.addAll(infos);

        this.mAllInfos = infos;

        notifyDataSetChanged();
        getEventListener().updataAppCount(mAppInfos.size());
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
        @BindView(R.id.btn_launch)
        Button mBtnLaunch;
        @BindView(R.id.btn_uninstall)
        Button mBtnUninstall;
        @BindView(R.id.btn_share)
        Button mBtnShare;

        public MyChildViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                List<AppInfo> result = new ArrayList<>();

                for (AppInfo appInfo : mAllInfos) {
                    if (appInfo.getLable().toLowerCase().contains(charSequence)) {
                        result.add(appInfo);
                    }
                }
                filterResults.values = result;
                filterResults.count = result.size();
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mAppInfos = (List<AppInfo>) filterResults.values;
                mEventListener.updataAppCount(mAppInfos.size());
                notifyDataSetChanged();
            }
        };
    }

    public void setEventListener(EventListener eventListener) {
        mEventListener = eventListener;
    }

    public EventListener getEventListener() {
        return mEventListener;
    }

    public List<AppInfo> getData() {
        return mAppInfos;
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

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    @Override
    public MyGroupViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.list_group_item, parent, false);
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
    public void onBindChildViewHolder(MyChildViewHolder holder, final int groupPosition, int childPosition,
                                      int viewType) {
        final AppInfo info = mAppInfos.get(groupPosition);
        holder.mTvPackageName.setText(info.getPackageName());
        holder.mTvVersion.setText(info.getVersion());
        holder.mTvInstallTime.setText(DateUtils.convertTimeMill(info.getFirstInstallTime()));
        holder.mTvUpdateTime.setText(DateUtils.convertTimeMill(info.getLastUpdateTime()));
        holder.mBtnLaunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = MyApplication.getContext().getPackageManager().getLaunchIntentForPackage
                        (info.getPackageName());
                if (intent != null) {
                    MyApplication.getContext().startActivity(intent);
                } else {
                    Snackbar.make(view, "无法启动此应用", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        holder.mBtnUninstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
                intent.setData(Uri.parse("package:" + info.getPackageName()));
                intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                intent.putExtra(AppListActivity.POSITIOIN, groupPosition);
                setCurrentPosition(groupPosition);
                mContext.startActivityForResult(intent, AppListActivity.UNINSTALL_REQUEST_CODE);
            }
        });
        // TODO: 2017/4/18 改APK文件名
        holder.mBtnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(info.getApkPath())));
                intent.setType("application/vnd.android.package-archive");
                mContext.startActivity(Intent.createChooser(intent, String.format("发送%s的安装包文件", info
                        .getLable()
                )));
//                mContext.startActivity(intent);
            }
        });
    }

    public void removeItem(int groupPosition) {
        mContext.getAppInfosAll().remove(mContext.getAppInfosAll().indexOf(mAppInfos.get(groupPosition)));
        mAllInfos.remove(mAllInfos.indexOf(mAppInfos.get(groupPosition)));
        mAppInfos.remove(groupPosition);
        //不设置会导致删除项不折叠
        notifyItemRemoved(groupPosition);
        //不设置会导致列表顺序错乱
        notifyDataSetChanged();
        getEventListener().updataAppCount(mAppInfos.size());
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
