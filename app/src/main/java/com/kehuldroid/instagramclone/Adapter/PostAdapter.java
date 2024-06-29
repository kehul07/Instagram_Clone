package com.kehuldroid.instagramclone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.socialview.widget.SocialTextView;
import com.kehuldroid.instagramclone.CommentActivity;
import com.kehuldroid.instagramclone.FollowerActivity;
import com.kehuldroid.instagramclone.Fragments.PostDetailFragment;
import com.kehuldroid.instagramclone.Fragments.profile_frag;
import com.kehuldroid.instagramclone.Model.Post;
import com.kehuldroid.instagramclone.Model.User;
import com.kehuldroid.instagramclone.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;




public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private Context context;
    private List<Post> mPosts;
    private FirebaseUser firebaseUser;
    public PostAdapter(Context context, List<Post> mPosts) {
        this.context = context;
        this.mPosts = mPosts;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_item,parent,false);
        return  new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = mPosts.get(position);
        Picasso.get().load(post.getImageUrl()).into(holder.post_image);
//        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/instagramclone-bc006.appspot.com/o/Posts%2F1696841487380.null?alt=media&token=f37c848b-d838-4ecd-8389-9c573165a0e0").into(holder.post_image);
        holder.description.setText(post.getDescription());
        FirebaseDatabase.getInstance().getReference().child("Users").child(post.getPublisher()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user.getImageUrl().equals("default")){
                    holder.imageProfile.setImageResource(R.mipmap.ic_launcher);
                }else {
                    Picasso.get().load(user.getImageUrl()).placeholder(R.mipmap.ic_launcher).into(holder.imageProfile);

                }
                holder.username.setText(user.getUsername());
                holder.auther.setText(user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        isLike(post.getPostid(),holder.like);
        noOfLike(post.getPostid(),holder.no_of_likes);
        getComments(post.getPostid(),holder.no_of_comment);
        isSaved(post.getPostid(),holder.save);

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.like.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("likes").
                            child(post.getPostid()).child(firebaseUser.getUid()).setValue(true);

                    addNotification(post.getPostid(),post.getPublisher());

                }else{
                    FirebaseDatabase.getInstance().getReference().child("likes").
                            child(post.getPostid()).child(firebaseUser.getUid()).removeValue();

                }
            }
        });
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("postid",post.getPostid());
                intent.putExtra("authorid",post.getPublisher());
                context.startActivity(intent);
            }
        });
        holder.no_of_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("postid",post.getPostid());
                intent.putExtra("authorid",post.getPublisher());
                context.startActivity(intent);
            }
        });

        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.save.getTag().equals("save")){
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid()).
                            child(post.getPostid()).setValue(true);

                }else{
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                            .child(post.getPostid()).removeValue();

                }
            }
        });

        holder.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.getSharedPreferences("PROFILE",Context.MODE_PRIVATE).edit().putString("profileId",post.getPublisher()).apply();
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.container,new profile_frag()).commit();
            }
        });
        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.getSharedPreferences("PROFILE",Context.MODE_PRIVATE).edit().putString("profileId",post.getPublisher()).apply();
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.container,new profile_frag()).commit();
            }
        });
        holder.auther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.getSharedPreferences("PROFILE",Context.MODE_PRIVATE).edit().putString("profileId",post.getPublisher()).apply();
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.container,new profile_frag()).commit();
            }
        });

        holder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit().putString("postId", post.getPostid()).apply();
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.container , new PostDetailFragment()).commit();
            }
        });

        holder.no_of_likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FollowerActivity.class);
                intent.putExtra("id",post.getPublisher());
                intent.putExtra("title","likes");
                context.startActivity(intent);
            }
        });

    }



    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageProfile,post_image,like,save,comment,share,more;
        public TextView username,no_of_likes,no_of_comment,auther;
        SocialTextView description;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            more = itemView.findViewById(R.id.more);
            imageProfile = itemView.findViewById(R.id.profile);
            post_image = itemView.findViewById(R.id.postimage);
            no_of_likes = itemView.findViewById(R.id.no_of_likes);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            save = itemView.findViewById(R.id.save);
            share = itemView.findViewById(R.id.share);
            no_of_comment = itemView.findViewById(R.id.no_of_comment);
            description = itemView.findViewById(R.id.description);
            username = itemView.findViewById(R.id.username);
            auther = itemView.findViewById(R.id.auther);


        }
    }

    private  void  addNotification(String postId , String publisherId){
        HashMap<String , Object> map = new HashMap<>();
        map.put("userId" , publisherId);
        map.put("text","liked your post.");
        map.put("postId",postId);
        map.put("isPost",true);

        FirebaseDatabase.getInstance().getReference().child("Notifications").child(firebaseUser.getUid()).push().setValue(map);
    }
    private void isSaved(String postid, ImageView save) {
        FirebaseDatabase.getInstance().getReference().child("Saves").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(postid).exists()){
                    save.setImageResource(R.drawable.ic_saved);
                    save.setTag("saved");
                }else{
                    save.setImageResource(R.drawable.ic_save);
                    save.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void isLike(String postId,ImageView imageView){
        FirebaseDatabase.getInstance().getReference().child("likes").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.ic_fav);
                    imageView.setTag("liked");
                }else{
                    imageView.setImageResource(R.drawable.ic_like);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void noOfLike(String postId , TextView textView){
        FirebaseDatabase.getInstance().getReference().child("likes").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                textView.setText(snapshot.getChildrenCount() +" likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getComments(String postId , TextView textView){
        FirebaseDatabase.getInstance().getReference().child("Comments").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                textView.setText("View All " +snapshot.getChildrenCount()+" comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
