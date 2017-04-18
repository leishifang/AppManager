package com.example.giggle.appmanager.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.giggle.appmanager.R;
import com.example.giggle.appmanager.bean.BaseInfo;
import com.example.giggle.appmanager.widget.ExpandableItemIndicator;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemViewHolder;

import java.util.List;

/**
 * Created by leishifang on 2017/4/18 9:28.
 */

public class BaseExpandableAdapter<T extends BaseInfo> extends
        AbstractExpandableItemAdapter<BaseExpandableAdapter
                .BaseGroupViewHolder, BaseExpandableAdapter.BaseChildViewHolder> {

    private static final String TAG = "BaseExpandableAdapter";
    protected List<T> mInfos;
    protected Activity mContext;
    /**
     * 当前position
     */
    protected int currentPosition;

    private interface Expandable extends ExpandableItemConstants {

    }

    public BaseExpandableAdapter() {
        // ExpandableItemAdapter requires stable ID, and also
        // have to implement the getGroupItemId()/getChildItemId() methods appropriately.
        setHasStableIds(true);
    }

    public BaseExpandableAdapter(List<T> infos, Activity context) {
        this();
        this.mInfos = infos;
        mContext = context;
    }

    public static class BaseGroupViewHolder extends AbstractExpandableItemViewHolder {

        public ExpandableItemIndicator mIndicator;

        public BaseGroupViewHolder(View v) {
            super(v);
        }
    }

    public static class BaseChildViewHolder extends AbstractExpandableItemViewHolder {

        public BaseChildViewHolder(View v) {
            super(v);
        }
    }

    public void setData(List<T> infos) {
        mInfos = infos;
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return mInfos.size();
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
    public BaseGroupViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.list_group_item, parent, false);
        return new BaseGroupViewHolder(v);
    }

    @Override
    public BaseChildViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindGroupViewHolder(BaseGroupViewHolder holder, int groupPosition, int viewType) {
        updateIndicatorState(holder);
    }

    @Override
    public void onBindChildViewHolder(BaseChildViewHolder holder, final int groupPosition, int
            childPosition, int viewType) {
    }

    public void removeItem(int groupPosition) {
        Log.d(TAG, "removeItem: " + groupPosition);
        mInfos.remove(groupPosition);
        //不设置会导致删除项不折叠
        notifyItemRemoved(groupPosition);
        //不设置会导致列表顺序错乱
        notifyDataSetChanged();
    }

    private void updateIndicatorState(BaseGroupViewHolder holder) {
        final int expandState = holder.getExpandStateFlags();

        if ((expandState & ExpandableItemConstants.STATE_FLAG_IS_UPDATED) != 0) {
            boolean isExpanded;
            boolean animateIndicator = ((expandState & BaseExpandableAdapter.Expandable
                    .STATE_FLAG_HAS_EXPANDED_STATE_CHANGED) !=
                    0);

            if ((expandState & BaseExpandableAdapter.Expandable.STATE_FLAG_IS_EXPANDED) != 0) {
                isExpanded = true;
            } else {
                isExpanded = false;
            }

            holder.mIndicator.setExpandedState(isExpanded, animateIndicator);
        }
    }

    @Override
    public boolean onCheckCanExpandOrCollapseGroup(BaseGroupViewHolder holder, int groupPosition, int x,
                                                   int y, boolean expand) {
        return true;
    }
}
