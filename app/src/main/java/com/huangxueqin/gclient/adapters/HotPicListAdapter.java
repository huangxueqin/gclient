package com.huangxueqin.gclient.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.huangxueqin.gclient.R;
import com.huangxueqin.gclient.model.HotPicPost;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangxueqin on 2017/2/4.
 */

public class HotPicListAdapter extends RecyclerView.Adapter<HotPicListAdapter.ViewHolder> {

    private static final String TAG = "HotPicListAdapter";

    private Context mContext;
    private int mPicWidth;
    private List<HotPicPost> mHotPicList;
    private ActionListener mActionListener;

    public HotPicListAdapter(Context context, int picWidth) {
        mContext = context;
        mPicWidth = picWidth;
        mHotPicList = new ArrayList<>();
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    public void setImages(@NonNull List<? extends HotPicPost> picList) {
        mHotPicList.clear();
        mHotPicList.addAll(picList);
        notifyDataSetChanged();
    }

    public void appendImages(@NonNull List<? extends HotPicPost> picList) {
        int insertPosition = mHotPicList.size();
        mHotPicList.addAll(picList);
        notifyItemRangeInserted(insertPosition, picList.size());
    }

    public List<HotPicPost> getHotPicList() {
        return mHotPicList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.view_list_item_hot_pic, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HotPicPost hotPicInfo = mHotPicList.get(position);
        int picWidth = mPicWidth;
        int picHeight = Math.round(1f * hotPicInfo.height * mPicWidth / hotPicInfo.width);
        holder.itemView.getLayoutParams().height = picHeight;
        Glide.with(mContext)
                .load(hotPicInfo.url)
                .apply(new RequestOptions().override(picWidth, picHeight))
                .into(holder.image);
        holder.itemView.setOnClickListener((v) -> {
            if (mActionListener != null) {
                mActionListener.onItemClicked(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mHotPicList.size();
    }

    public interface ActionListener {
        void onItemClicked(View v, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
        }
    }
}
