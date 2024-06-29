package com.kehuldroid.instagramclone;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kehuldroid.instagramclone.Fragments.home_frag;
import com.kehuldroid.instagramclone.Fragments.notification_frag;
import com.kehuldroid.instagramclone.Fragments.profile_frag;
import com.kehuldroid.instagramclone.Fragments.search_frag;


public class MainActivity2 extends AppCompatActivity {


    private BottomNavigationView bottom_nav;
    private Fragment selectorFrag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        bottom_nav = findViewById(R.id.bottom_nav);
        bottom_nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                 int id = item.getItemId();
                 if(id == R.id.nav_home){
                     selectorFrag = new home_frag();
                }
                else if(id == R.id.nav_search){
                     selectorFrag = new search_frag();
                }
                else if(id == R.id.nav_heart){
                     selectorFrag = new notification_frag();
                }
                else if(id == R.id.nav_profile){
                     selectorFrag = new profile_frag();
                }
                else{
                     selectorFrag=null;
                     startActivity(new Intent(MainActivity2.this,postActivity.class));
                 }

                if(selectorFrag !=null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.container,selectorFrag).commit();
                }
                return  true;
            }
        });
        Bundle intent = getIntent().getExtras();
        if(intent!=null){
            String profileID = intent.getString("publisherId");

            getSharedPreferences("PROFILE",MODE_PRIVATE).edit().putString("profileId",profileID).apply();
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new profile_frag()).commit();
            bottom_nav.setSelectedItemId(R.id.nav_profile);
        }else {

            getSupportFragmentManager().beginTransaction().replace(R.id.container, new home_frag()).commit();
        }
    }
}