package com.kehuldroid.instagramclone.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kehuldroid.instagramclone.Adapter.PhotoAdapter;
import com.kehuldroid.instagramclone.EditProfileActivity;
import com.kehuldroid.instagramclone.FollowerActivity;
import com.kehuldroid.instagramclone.Model.Post;
import com.kehuldroid.instagramclone.Model.User;
import com.kehuldroid.instagramclone.OptionsActivity;
import com.kehuldroid.instagramclone.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class profile_frag extends Fragment {

    private CircleImageView imageProfile;
    private ImageView options;
    private TextView posts,followers,following,fullname,bio,username;
    private ImageButton myPictures,savedPictures;
    private FirebaseUser firebaseUser;
    private AppCompatButton editProfile ;
    String profileId;

    private RecyclerView rvpost;
    private PhotoAdapter photoAdapter;
    private List<Post> myPostlist;

    private  RecyclerView rvsave;
    private PhotoAdapter photoAdapter2;
    private List<Post> mySavedPosts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_frag, container, false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String data = getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).getString("profileId","none");

        if(data.equals("none")){
            profileId = firebaseUser.getUid();
        }else{
            profileId = data;
        }




        imageProfile = view.findViewById(R.id.image_profile);
        options = view.findViewById(R.id.options);
        posts = view.findViewById(R.id.posts);
        followers = view.findViewById(R.id.followers);
        following = view.findViewById(R.id.following);
        fullname  = view.findViewById(R.id.fullname);
        username = view.findViewById(R.id.username);
        myPictures = view.findViewById(R.id.my_pictures);
        savedPictures = view.findViewById(R.id.saved_pictures);
        bio = view.findViewById(R.id.bio);
        editProfile = view.findViewById(R.id.edit_profile);

        rvpost = view.findViewById(R.id.rv_pictures);
        rvpost.setHasFixedSize(true);
        rvpost.setLayoutManager(new GridLayoutManager(getContext(),3));
        myPostlist = new ArrayList<>();
        photoAdapter = new PhotoAdapter(getContext(), myPostlist);
        rvpost.setAdapter(photoAdapter);

        rvsave=view.findViewById(R.id.rv_saved);
        rvsave.setHasFixedSize(true);
        rvsave.setLayoutManager(new GridLayoutManager(getContext(),3));
        mySavedPosts = new ArrayList<>();
        photoAdapter2 = new PhotoAdapter(getContext(),mySavedPosts);
        rvsave.setAdapter(photoAdapter2);

        MyPhotos();
        userInfo();
        getFollowersAndFollowingCount();
        getPostCount();
        getSavedPosts();

        if(profileId.equals(firebaseUser.getUid())){
            editProfile.setText("Edit Profile");
        }else{
            checkFollowingStatus();
        }
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btn = editProfile.getText().toString();
                if(btn.equals("Edit Profile")){
                        startActivity(new Intent(getContext(), EditProfileActivity.class));
                }else{
                    if(btn.equals("follow")){
                        FirebaseDatabase.getInstance().getReference().child("follow").child(firebaseUser.getUid())
                                .child("following").child(profileId).setValue(true);
                        FirebaseDatabase.getInstance().getReference().child("follow")
                                .child(profileId).child("followers").child(firebaseUser.getUid()).setValue(true);
                    }else{
                        FirebaseDatabase.getInstance().getReference().child("follow").child(firebaseUser.getUid())
                                .child("following").child(profileId).removeValue();
                        FirebaseDatabase.getInstance().getReference().child("follow")
                                .child(profileId).child("followers").child(firebaseUser.getUid()).removeValue();
                    }
                }
            }
        });
        rvpost.setVisibility(View.VISIBLE);
        rvsave.setVisibility(View.GONE);


        myPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                rvpost.setVisibility(View.VISIBLE);
                rvsave.setVisibility(View.GONE);
            }
        });
        savedPictures.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             rvpost.setVisibility(View.GONE);
             rvsave.setVisibility(View.VISIBLE);
         }
     });

        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), FollowerActivity.class);
                intent.putExtra("id",profileId);
                intent.putExtra("title","followers");
                startActivity(intent);
            }
        });
        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), FollowerActivity.class);
                intent.putExtra("id",profileId);
                intent.putExtra("title","following");
                startActivity(intent);
            }
        });

        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), OptionsActivity.class));
            }
        });

        return view;
    }

    private void getSavedPosts() {
        List<String> savedIds = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    savedIds.add(snapshot1.getKey());
                }
                FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot
                    ) {
                        mySavedPosts.clear();
                        for(DataSnapshot snapshot2 : dataSnapshot.getChildren()){
                            Post post = snapshot2.getValue(Post.class);
                            for(String id : savedIds){
                                if(post.getPostid().equals(id)){
                                    mySavedPosts.add(post);
                                }
                            }
                        }
                        photoAdapter2.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void MyPhotos() {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myPostlist.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    Post post = snapshot1.getValue(Post.class);
                    if(post.getPublisher().equals(profileId)){
                        myPostlist.add(post);
                    }
                }
                Collections.reverse(myPostlist);
                photoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkFollowingStatus() {
        FirebaseDatabase.getInstance().getReference().child("follow").child(firebaseUser.getUid()).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(profileId).exists()){
                    editProfile.setText("following");
                }else{
                    editProfile.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPostCount() {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    Post post = snapshot1.getValue(Post.class);
                    if(post.getPublisher().equals(profileId)){
                        count++;
                    }
                }
                posts.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowersAndFollowingCount() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("follow").child(profileId);
        ref.child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followers.setText(""+ snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ref.child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                following.setText(""+ snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void userInfo() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(profileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user.getImageUrl().equals("default")) {
                        imageProfile.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Picasso.get().load(user.getImageUrl()).into(imageProfile);
                }

                username.setText(user.getUsername());
                fullname.setText(user.getName());
                bio.setText(user.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}