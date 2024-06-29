package com.kehuldroid.instagramclone.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.socialview.widget.SocialAutoCompleteTextView;
import com.kehuldroid.instagramclone.Adapter.tagAdapter;
import com.kehuldroid.instagramclone.Adapter.userAdapter;
import com.kehuldroid.instagramclone.Model.User;
import com.kehuldroid.instagramclone.R;

import java.util.ArrayList;
import java.util.List;




public class search_frag extends Fragment {

    private RecyclerView rv,rvt;
    private SocialAutoCompleteTextView searchbar;
    private List<User> mUsers;
    private userAdapter ua;
    private List<String> mHashTag;
    private List<String> mHashCount;
    private tagAdapter ta;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View view = inflater.inflate(R.layout.fragment_search_frag, container, false);
         rv= view.findViewById(R.id.rv);
         rvt= view.findViewById(R.id.rvt);
         rv.setHasFixedSize(true);
         rv.setLayoutManager(new LinearLayoutManager(getContext()));
         mUsers = new ArrayList<>();
         searchbar = view.findViewById(R.id.searchbar);
         readUsers();
         ua = new userAdapter(getContext(),mUsers,true);
         rv.setAdapter(ua);

        rvt.setHasFixedSize(true);
        rvt.setLayoutManager(new LinearLayoutManager(getContext()));
        mHashTag = new ArrayList<>();
        mHashCount = new ArrayList<>();
        ta = new tagAdapter(getContext(),mHashTag,mHashCount);
        rvt.setAdapter(ta);
         readTags();

         searchbar.addTextChangedListener(new TextWatcher() {
             @Override
             public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

             }

             @Override
             public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUser(charSequence.toString());
             }

             @Override
             public void afterTextChanged(Editable editable) {
                filter(editable.toString());
             }
         });

         return  view;
    }

    private void readTags() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("HashTags");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mHashTag.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    mHashTag.add(snapshot1.getKey());
                    mHashCount.add(snapshot1.getChildrenCount()+ " ");
                }
                ta.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readUsers() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(TextUtils.isEmpty(searchbar.getText().toString())){
                    mUsers.clear();
                    for(DataSnapshot snapshot1 : snapshot.getChildren()){
                        User user = snapshot1.getValue(User.class);
                        mUsers.add(user);
                    }
                    ua.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void searchUser(String s){
        Query query = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("username").startAt(s).endAt(s+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    User user = snapshot1.getValue(User.class);
                    mUsers.add(user);
                }
                ua.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private  void filter (String text){
        List<String> mSearchTags = new ArrayList<>();
        List<String> mSearchTagsCount = new ArrayList<>();
        for(String s : mHashTag){
            if(s.toLowerCase().contains(text.toLowerCase())){
                 mSearchTags.add(s);
                 mSearchTagsCount.add(mHashCount.get(mHashTag.indexOf(s)));
            }
        }
        ta.filter(mSearchTags,mSearchTagsCount);


    }
}