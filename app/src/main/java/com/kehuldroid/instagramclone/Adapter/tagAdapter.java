package com.kehuldroid.instagramclone.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kehuldroid.instagramclone.R;

import java.util.List;

public class tagAdapter extends RecyclerView.Adapter<tagAdapter.viewHolder> {
    private Context context;
    private List<String> mTags;

    public tagAdapter(Context context, List<String> mTags, List<String> mNotag) {
        this.context = context;
        this.mTags = mTags;
        this.mNotag = mNotag;
    }

    private  List<String> mNotag;
    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.tag_item,parent,false);
        return  new tagAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        holder.hashtag.setText("#"+mTags.get(position));
        holder.num_tag.setText(mNotag.get(position)+"posts");
    }

    @Override
    public int getItemCount() {
        return  mTags.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        public TextView hashtag;
        public TextView num_tag;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            hashtag = itemView.findViewById(R.id.hashtag);
            num_tag = itemView.findViewById(R.id.num_tag);
        }
    }
    public void filter(List<String> filterTags,List<String> filterTagsCount){
        this.mTags = filterTags;
        this.mNotag = filterTagsCount;
        notifyDataSetChanged();
    }
}
