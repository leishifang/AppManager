package com.example.giggle.appmanager.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.IntRange;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.giggle.appmanager.MyApplication;
import com.example.giggle.appmanager.R;
import com.example.giggle.appmanager.bean.ApkInfo;
import com.example.giggle.appmanager.utils.DateUtils;
import com.example.giggle.appmanager.widget.ExpandableItemIndicator;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableSwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionMoveToSwipedDirection;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractSwipeableItemViewHolder;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by leishifang on 2017/4/18 19:22.
 */

public class ApkAdapter extends AbstractExpandableItemAdapter<ApkAdapter.GroupViewHolder, ApkAdapter
        .ChildViewHolder> implements ExpandableSwipeableItemAdapter<ApkAdapter.GroupViewHolder, ApkAdapter
        .ChildViewHolder> {

    private static final String TAG = "ApkAdapter";

    private List<ApkInfo> data;
    private Context mContext;
    private final RecyclerViewExpandableItemManager mRecyclerViewExpandableItemManager;

    private class MyLeftAndRightSwipeResultAction extends SwipeResultActionMoveToSwipedDirection {

        private final int mGroupPosition;
        private ApkAdapter mAdapter;
        private RecyclerView.ViewHolder mViewHolder;

        public MyLeftAndRightSwipeResultAction(ApkAdapter adapter, int position, RecyclerView.ViewHolder
                viewHolder) {
            this.mGroupPosition = position;
            this.mAdapter = adapter;
            this.mViewHolder = viewHolder;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();
            File file = new File(mAdapter.data.get(mGroupPosition).getApkPath());
            if (file.isFile() && file.exists()) {
                if (file.delete()) {
                    Snackbar.make(mViewHolder.itemView, mAdapter.data.get(mGroupPosition).getLable() +
                            "删除成功", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(mViewHolder.itemView, mAdapter.data.get(mGroupPosition).getLable() +
                            "删除失败", Snackbar.LENGTH_SHORT).show();
                }
            }
            mAdapter.data.remove(mGroupPosition);
            mAdapter.mRecyclerViewExpandableItemManager.notifyGroupItemRemoved(mGroupPosition);
        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
        }
    }

    public ApkAdapter(List<ApkInfo> apkInfoList, RecyclerViewExpandableItemManager manager, Context context) {
        setHasStableIds(true);
        mContext = context;
        setData(apkInfoList);
        mRecyclerViewExpandableItemManager = manager;
    }

    public static class GroupViewHolder extends AbstractSwipeableItemViewHolder implements
            ExpandableItemViewHolder {

        @BindView(R.id.indicator)
        ExpandableItemIndicator mIndicator;
        @BindView(R.id.img_icon)
        ImageView mImgIcon;
        @BindView(R.id.tv_lable)
        TextView mTvLable;
        @BindView(R.id.tv_size)
        TextView mTvSize;
        @BindView(R.id.container)
        FrameLayout mContainer;

        private int mExpandStateFlags;

        public GroupViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public View getSwipeableContainerView() {
            return mContainer;
        }

        @Override
        public int getExpandStateFlags() {
            return mExpandStateFlags;
        }

        @Override
        public void setExpandStateFlags(int flag) {
            mExpandStateFlags = flag;
        }
    }

    @Override
    public SwipeResultAction onSwipeGroupItem(GroupViewHolder holder, int groupPosition, int result) {
        if (result == SwipeableItemConstants.RESULT_SWIPED_LEFT || result == SwipeableItemConstants
                .RESULT_SWIPED_RIGHT) {
            return new MyLeftAndRightSwipeResultAction(this, groupPosition, holder);
        }
        return null;
    }

    @Override
    public SwipeResultAction onSwipeChildItem(ChildViewHolder holder, int groupPosition, int childPosition,
                                              int result) {
        return null;
    }

    @Override
    public int onGetGroupItemSwipeReactionType(GroupViewHolder holder, int groupPosition, int x, int y) {
        return SwipeableItemConstants.REACTION_CAN_SWIPE_BOTH_H;
    }

    @Override
    public int onGetChildItemSwipeReactionType(ChildViewHolder holder, int groupPosition, int
            childPosition, int x, int y) {
        return SwipeableItemConstants.REACTION_CAN_NOT_SWIPE_ANY;
    }

    @Override
    public void onSetGroupItemSwipeBackground(GroupViewHolder holder, int groupPosition, int type) {

    }

    @Override
    public void onSetChildItemSwipeBackground(ChildViewHolder holder, int groupPosition, int childPosition,
                                              int type) {

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
        @BindView(R.id.btn_install)
        Button mBtnInstall;
        @BindView(R.id.btn_delete)
        Button mBtnDelete;
        @BindView(R.id.btn_share)
        Button mBtnShare;

        public ChildViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setData(List<ApkInfo> infos) {
        data = infos;
        notifyDataSetChanged();
    }

    public List<ApkInfo> getData() {
        return data;
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
        return data.get(groupPosition).getId();
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
    public void onBindChildViewHolder(ChildViewHolder holder, final int groupPosition, int childPosition,
                                      @IntRange(from = -8388608L, to = 8388607L) int viewType) {
        final ApkInfo info = data.get(groupPosition);
        holder.mTvVersion.setText(info.getVersionName());
        holder.mTvUpdateTime.setText(DateUtils.convertTimeMill(info.getTime() * 1000));
        holder.mTvApkPath.setText(info.getApkPath());
        holder.mTvPackageName.setText(info.getPackageName());
        holder.mBtnInstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.parse("file://" + info.getApkPath()), "application/vnd.android" +
                        ".package-archive");
                MyApplication.getContext().startActivity(intent);
            }
        });
        holder.mBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(info.getApkPath());
                if (file.isFile() && file.exists()) {
                    if (file.delete()) {
                        Snackbar.make(view, "删除成功", Snackbar.LENGTH_SHORT).show();
                        removeItem(groupPosition);
                    } else {
                        Snackbar.make(view, "删除失败", Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        });
        holder.mBtnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(info.getApkPath())));
                intent.setType("application/vnd.android.package-archive");
                mContext.startActivity(Intent.createChooser(intent, String.format
                        ("发送%s安装包文件", info
                                .getLable()
                        )));
            }
        });
    }

    public void removeItem(int groupPosition) {
        data.remove(groupPosition);
        //不设置会导致删除项不折叠
        notifyItemRemoved(groupPosition);
        //不设置会导致列表顺序错乱
        notifyDataSetChanged();
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
            boolean animateIndicator = ((expandState & ExpandableItemConstants
                    .STATE_FLAG_HAS_EXPANDED_STATE_CHANGED) !=
                    0);

            if ((expandState & ExpandableItemConstants.STATE_FLAG_IS_EXPANDED) != 0) {
                isExpanded = true;
            } else {
                isExpanded = false;
            }

            holder.mIndicator.setExpandedState(isExpanded, animateIndicator);
        }
    }
}
