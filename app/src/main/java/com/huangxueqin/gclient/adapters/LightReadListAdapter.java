package com.huangxueqin.gclient.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huangxueqin.gclient.R;
import com.huangxueqin.gclient.model.LightReadPost;

/**
 * Created by huangxueqin on 2018/5/2.
 */

public class LightReadListAdapter extends BaseFlowListAdapter<LightReadPost, LightReadListAdapter.LrViewHolder> {

    public LightReadListAdapter(@NonNull Context context) {
        super(context);
    }

    @Override
    public LrViewHolder onCreateItemViewHolder(ViewGroup parent, LayoutInflater inflater) {
        View itemView = inflater.inflate(R.layout.view_list_item_article, parent, false);
        return new LrViewHolder(itemView);
    }

    @Override
    public void onBindItemViewHolder(LrViewHolder holder, int position) {
        LightReadPost lightReadItemInfo = getDataItem(position);
        if (lightReadItemInfo == null) {
            return;
        }
        holder.tvArticleDesc.setText(lightReadItemInfo.title);
        holder.tvArticleType.setText(lightReadItemInfo.sourceTitle);
        holder.tvArticleCreator.setVisibility(View.GONE);
        holder.ivArticleImage.setVisibility(View.GONE);
        holder.tvArticleDate.setText(lightReadItemInfo.time);
    }

    public static class LrViewHolder extends RecyclerView.ViewHolder {

        TextView tvArticleDesc;
        ImageView ivArticleImage;
        TextView tvArticleType;
        TextView tvArticleCreator;
        TextView tvArticleDate;

        public LrViewHolder(View itemView) {
            super(itemView);
            this.tvArticleDesc = itemView.findViewById(R.id.article_desc);
            this.ivArticleImage = itemView.findViewById(R.id.article_image);
            this.tvArticleType = itemView.findViewById(R.id.article_type);
            this.tvArticleCreator = itemView.findViewById(R.id.article_creator);
            this.tvArticleDate = itemView.findViewById(R.id.article_date);
        }
    }
}
