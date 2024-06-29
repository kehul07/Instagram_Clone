package com.kehuldroid.instagramclone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kehuldroid.instagramclone.Fragments.profile_frag;
import com.kehuldroid.instagramclone.MainActivity2;
import com.kehuldroid.instagramclone.Model.User;
import com.kehuldroid.instagramclone.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class userAdapter extends RecyclerView.Adapter<userAdapter.viewHolder> {
    private Context context;
    private List<User> mUsers;
    private boolean isFragment;
    private FirebaseUser firebaseUser;

    public userAdapter(Context context, List<User> mUsers, boolean isFragment) {
        this.context = context;
        this.mUsers = mUsers;
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item,parent,false);

        return new userAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        User user = mUsers.get(position);
        holder.btn_follow.setVisibility(View.VISIBLE);
        holder.username.setText(user.getUsername());
        holder.name.setText(user.getName());
      if(user.getImageUrl().equals("default")) {
          holder.image_profile.setImageResource(R.mipmap.ic_launcher);
      }else{
          Picasso.get().load(user.getImageUrl()).placeholder(R.mipmap.ic_launcher).into(holder.image_profile);

          }
        if(user.getId().equals(firebaseUser.getUid())){
            holder.btn_follow.setVisibility(View.GONE);
        }

        holder.btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.btn_follow.getText().toString().equals("follow")){
                    FirebaseDatabase.getInstance().getReference().child("follow").child(firebaseUser.getUid()).child("following").child(user.getId()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("follow").child(user.getId()).child("followers").child(firebaseUser.getUid()).
                            setValue(true);
                    addNotifications(user.getId());

                }else{
                    FirebaseDatabase.getInstance().getReference().child("follow").child(firebaseUser.getUid()).child("following").child(user.getId()).
                          removeValue();
                    FirebaseDatabase.getInstance().getReference().child("follow").child(user.getId()).child("followers").child(firebaseUser.getUid()).
                           removeValue();

                }
            }
        });
        isFollowed(user.getId(),holder.btn_follow);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isFragment){
                    context.getSharedPreferences("PROFILE" , Context.MODE_PRIVATE).edit().putString("profileId", user.getId()).apply();
                    ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.container,new profile_frag()).commit();
                }
                else{
                    Intent intent = new Intent(context , MainActivity2.class);
                    intent.putExtra("publisherId",user.getId());
                    context.startActivity(intent);
                }
            }
        });
    }

    private void isFollowed(String id, AppCompatButton btn_follow) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("follow").child(firebaseUser.getUid()).child("following");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(id).exists()){
                    btn_follow.setText("following");
                }else{
                    btn_follow.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        public CircleImageView image_profile;
        public TextView username,name;
        public AppCompatButton btn_follow;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            image_profile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            name = itemView.findViewById(R.id.name);
            btn_follow = itemView.findViewById(R.id.btn_follow);
        }
    }
    private void addNotifications(String id){
        HashMap<String , Object> map = new HashMap<>();
        map.put("userId" , id);
        map.put("text","started following you.");
        map.put("postId","");
        map.put("isPost",false);

        FirebaseDatabase.getInstance().getReference().child("Notifications").child(firebaseUser.getUid()).push().setValue(map);
    }
}
