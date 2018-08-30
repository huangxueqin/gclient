package com.huangxueqin.gclient.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huangxueqin.gclient.R;
import com.huangxueqin.gclient.model.NewsPost;
import com.huangxueqin.gclient.utils.ArrayUtil;
import com.huangxueqin.gclient.utils.DateUtil;
import com.huangxueqin.gclient.utils.ImageUtil;


/**
 * Created by huangxueqin on 2018/4/30.
 */

public class PostListAdapter extends BaseFlowListAdapter<NewsPost, PostListAdapter.PostViewHolder> {

    public PostListAdapter(@NonNull Context context) {
        super(context);
    }

    @Override
    public PostViewHolder onCreateItemViewHolder(ViewGroup parent, LayoutInflater inflater) {
        View itemView = inflater.inflate(R.layout.view_list_item_article, parent, false);
        return new PostViewHolder(itemView);
    }

    @Override
    public void onBindItemViewHolder(PostViewHolder holder, int position) {
        NewsPost postItem = getDataItem(position);
        if (postItem == null) {
            return;
        }
        holder.tvArticleDesc.setText(postItem.desc);
        holder.tvArticleType.setText(postItem.type);
        holder.tvArticleCreator.setText(postItem.who);
        holder.tvArticleCreator.setVisibility(TextUtils.isEmpty(postItem.who) ? View.GONE : View.VISIBLE);
        holder.tvArticleDate.setText(DateUtil.parseReadableDate(postItem.publishedAt));
        holder.ivArticleImage.setVisibility(ArrayUtil.isEmpty(postItem.images) ? View.GONE : View.VISIBLE);
        if (ArrayUtil.isNotEmpty(postItem.images)) {
            ImageUtil.loadImage(holder.ivArticleImage, postItem.images[0]);
        }
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        TextView tvArticleDesc;
        ImageView ivArticleImage;
        TextView tvArticleType;
        TextView tvArticleCreator;
        TextView tvArticleDate;

        public PostViewHolder(View itemView) {
            super(itemView);
            this.tvArticleDesc = itemView.findViewById(R.id.article_desc);
            this.ivArticleImage = itemView.findViewById(R.id.article_image);
            this.tvArticleType = itemView.findViewById(R.id.article_type);
            this.tvArticleCreator = itemView.findViewById(R.id.article_creator);
            this.tvArticleDate = itemView.findViewById(R.id.article_date);
        }
    }
}
