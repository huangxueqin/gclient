package com.huangxueqin.gclient.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.huangxueqin.gclient.model.HotPicPost;
import com.huangxueqin.gclient.widget.PreviewImageView;
import com.huangxueqin.gclient.utils.ImageUtil;

import java.util.List;

/**
 * Created by huangxueqin on 2018/5/5.
 */

public class HotPicPagerAdapter extends PagerAdapter {

    private Context mContext;
    private List<HotPicPost> mHotPicList;
    private ActionListener mActionListener;

    public HotPicPagerAdapter(Context context, List<HotPicPost> hotPicList) {
        mContext = context;
        mHotPicList = hotPicList;
    }

    public void setActionListener(ActionListener actionListener) {
        this.mActionListener = actionListener;
    }

    @Override
    public int getCount() {
        return mHotPicList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return object == view;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        HotPicPost hotPicInfo = mHotPicList.get(position);
        ImageView imageView = new PreviewImageView(mContext);
        container.addView(imageView);
        ImageUtil.loadImage(imageView, hotPicInfo.url, hotPicInfo.width, hotPicInfo.height);
        imageView.setOnClickListener((v)->{
            if (mActionListener != null) {
                mActionListener.onItemClicked(v, position);
            }
        });
        imageView.setOnLongClickListener((v)->{
            if (mActionListener != null) {
                mActionListener.onItemLongPressed(v, position);
            }
            return true;
        });
        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ImageView imageView = (ImageView) object;
        container.removeView(imageView);
    }

    public interface ActionListener {
        void onItemClicked(View v, int position);
        void onItemLongPressed(View v, int position);
    }

    public static class SimpleActionListener implements ActionListener {
        @Override
        public void onItemClicked(View v, int position) {

        }

        @Override
        public void onItemLongPressed(View v, int position) {

        }
    }
}
