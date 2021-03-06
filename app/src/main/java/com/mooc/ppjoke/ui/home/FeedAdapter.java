package com.mooc.ppjoke.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mooc.ppjoke.databinding.LayoutFeedTypeImageBinding;
import com.mooc.ppjoke.databinding.LayoutFeedTypeVideoBinding;
import com.mooc.ppjoke.model.Feed;
import com.mooc.ppjoke.ui.detail.FeedDetailActivity;
import com.mooc.ppjoke.view.ListPlayerView;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import us.bojie.libcommon.extention.LiveDataBus;

import static com.mooc.ppjoke.ui.home.InteractionPresenter.DATA_FROM_INTERACTION;

public class FeedAdapter extends PagedListAdapter<Feed, FeedAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private Context mContext;
    private String mCategory;
    private FeedObserver mFeedObserver;

    protected FeedAdapter(Context context, String category) {
        super(new DiffUtil.ItemCallback<Feed>() {
            @Override
            public boolean areItemsTheSame(@NonNull Feed oldItem, @NonNull Feed newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Feed oldItem, @NonNull Feed newItem) {
                return false;
            }
        });

        inflater = LayoutInflater.from(context);
        mContext = context;
        mCategory = category;
    }

    @Override
    public int getItemViewType(int position) {
        Feed feed = getItem(position);
        return feed.getItemType();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewDataBinding binding = null;
        if (viewType == Feed.TYPE_IMAGE) {
            binding = LayoutFeedTypeImageBinding.inflate(inflater);
        } else {
            binding = LayoutFeedTypeVideoBinding.inflate(inflater);
        }
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Feed feed = getItem(position);
        holder.bindData(feed);
        holder.itemView.setOnClickListener(v -> {
            FeedDetailActivity.startFeedDetailActivity(mContext, feed, mCategory);
            onStartFeedDetailActivity(feed);
            if (mFeedObserver == null) {
                mFeedObserver = new FeedObserver();
                LiveDataBus.get().with(DATA_FROM_INTERACTION).observe((LifecycleOwner) mContext, mFeedObserver);
            }
            mFeedObserver.setFeed(feed);
        });
    }

    public void onStartFeedDetailActivity(Feed feed) {

    }

    private class FeedObserver implements Observer<Feed> {

        private Feed mFeed;

        @Override
        public void onChanged(Feed newOne) {
            if (mFeed.getId() != newOne.getId()) {
                return;
            }
            mFeed.setAuthor(newOne.getAuthor());
            mFeed.setUgc(newOne.getUgc());
            mFeed.notifyChange();
        }

        public void setFeed(Feed feed) {
            mFeed = feed;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ViewDataBinding mBinding;
        private ListPlayerView listPlayerView;

        public ViewHolder(@NonNull View itemView, ViewDataBinding binding) {
            super(itemView);
            mBinding = binding;
        }

        public void bindData(Feed item) {
            if (mBinding instanceof LayoutFeedTypeImageBinding) {
                LayoutFeedTypeImageBinding imageBinding = (LayoutFeedTypeImageBinding) mBinding;
                imageBinding.setLifeCycleOwner((LifecycleOwner) mContext);
                imageBinding.setFeed(item);
                imageBinding.feedImage.bindData(item.getWidth(), item.getHeight(), 16, item.getCover());
            } else {
                LayoutFeedTypeVideoBinding videoBinding = (LayoutFeedTypeVideoBinding) mBinding;
                videoBinding.setLifeCycleOwner((LifecycleOwner) mContext);
                videoBinding.setFeed(item);
                listPlayerView = videoBinding.listPlayerView;
                listPlayerView.bindData(mCategory, item.getWidth(), item.getHeight(), item.getCover(), item.getUrl());
            }
        }

        public boolean isVideoItem() {
            return mBinding instanceof LayoutFeedTypeVideoBinding;
        }

        public ListPlayerView getListPlayerView() {
            return listPlayerView;
        }
    }
}
