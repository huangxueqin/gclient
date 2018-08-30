package com.huangxueqin.gclient.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huangxueqin.gclient.R;
import com.huangxueqin.gclient.utils.ListUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangxueqin on 2018/5/2.
 */

public abstract class BaseFlowListAdapter<T, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_VIEW_TYPE_ITEM = 0;
    private static final int ITEM_VIEW_TYPE_LOAD = 1;

    private Context mContext;
    private List<T> mDataList;
    private boolean mCompleted;
    private LayoutInflater mLayoutInflater;
    private OnItemClickListener<T> mOnListItemClickListener;

    public BaseFlowListAdapter(@NonNull Context context) {
        this(context, null, false);
    }

    public BaseFlowListAdapter(@NonNull Context context, @Nullable List<T> presetDataList) {
        this(context, presetDataList, false);
    }

    public BaseFlowListAdapter(@NonNull Context context, @Nullable List<T> presetDataList, boolean completed) {
        mContext = context;
        mDataList = new ArrayList<>();
        if (ListUtil.isNotEmpty(presetDataList)) {
            mDataList.addAll(presetDataList);
        }
        mCompleted = completed;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    public Context getContext() {
        return mContext;
    }

    public void setOnItemClickListener(OnItemClickListener<T> listener) {
        mOnListItemClickListener = listener;
    }

    public void clear() {
        setDataList(null);
    }

    public void setDataList(@Nullable List<? extends T> newDataList) {
        setDataList(newDataList, false);
    }

    public void setDataList(@Nullable List<? extends T> newDataList, boolean completed) {
        mDataList = new ArrayList<>();
        if (ListUtil.isNotEmpty(newDataList)) {
            mDataList.addAll(newDataList);
        }
        mCompleted = completed;
        notifyDataSetChanged();
    }

    public void appendDataList(@NonNull List<? extends T> addedDataList) {
        appendDataList(addedDataList, false);
    }

    public void appendDataList(@NonNull List<? extends T> addedDataList, boolean completed) {
        mDataList.addAll(addedDataList);
        boolean completeChange = mCompleted != completed;
        notifyListDataAppend(addedDataList, completeChange);
    }

    public void setCompleted(boolean completed) {
        if (mCompleted != completed) {
            mCompleted = completed;
            notifyCompleteChange();
        }
    }

    private void notifyCompleteChange() {
        if (mCompleted) {
            // uncompleted -> completed，移除Loading View
            notifyItemRemoved(getLoadingItemPosition());
        } else {
            // completed -> uncompleted, 添加Loading View
            notifyItemInserted(getLoadingItemPosition());
        }
    }

    private void notifyListDataAppend(List<? extends T> appendList, boolean completeChange) {
        if (ListUtil.isEmpty(appendList) && !completeChange) {
            return;
        }
        if (ListUtil.isEmpty(appendList)) {
            notifyCompleteChange();
        } else if (completeChange) {
            // complete changed and append list if not empty , we just update all
            notifyDataSetChanged();
        } else {
            notifyItemRangeInserted(getDataItemCount()-appendList.size(), appendList.size());
        }
    }

    public T getDataItem(int position) {
        return mDataList.get(position);
    }

    public int getDataItemCount() {
        return mDataList.size();
    }

    private int getLoadingItemPosition() {
        return mDataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position < getDataItemCount() ? ITEM_VIEW_TYPE_ITEM : ITEM_VIEW_TYPE_LOAD;
    }

    @Override
    public int getItemCount() {
        return getDataItemCount() + (mCompleted ? 0 : 1);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_VIEW_TYPE_ITEM) {
            return onCreateItemViewHolder(parent, mLayoutInflater);
        } else {
            View itemView = mLayoutInflater.inflate(R.layout.view_list_item_article_loading, parent, false);
            return new LoadItemLoader(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == ITEM_VIEW_TYPE_ITEM) {
            onBindItemViewHolder((VH) holder, position);
            holder.itemView.setOnClickListener((v)->{
                if (mOnListItemClickListener != null) {
                    mOnListItemClickListener.onItemClicked(getDataItem(position), position);
                }
            });
        }
    }

    public abstract VH onCreateItemViewHolder(ViewGroup parent, LayoutInflater inflater);

    public abstract void onBindItemViewHolder(VH holder, int position);

    public interface OnItemClickListener<DataType> {
        void onItemClicked(DataType data, int position);
    }

    public class LoadItemLoader extends RecyclerView.ViewHolder {
        public LoadItemLoader(View itemView) {
            super(itemView);
        }
    }
}
