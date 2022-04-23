package com.uno.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.uno.R;
import com.uno.fragments.AccountFragment;
import com.uno.fragments.HomeFragment;
import com.uno.fragments.MessageFragment;
import com.uno.fragments.SearchFragment;
import com.uno.providers.AuthProvider;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView mBtnNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mBtnNavigation = findViewById(R.id.bottom_navigation);
        mBtnNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        openFragment(new HomeFragment());
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    if(item.getItemId() == R.id.itemHome){
                        //FRAGMENT HOME
                        openFragment(new HomeFragment());

                    }else if(item.getItemId() == R.id.itemSearch){
                        //FRAGMENT SEARCH
                        openFragment(new SearchFragment());

                    }else if(item.getItemId() == R.id.itemMessage){
                        //FRAGMENT MESSAGE
                        openFragment(new MessageFragment());

                    }else if(item.getItemId() == R.id.itemAccount){
                        //FRAGMENT ACCOUNT
                        openFragment(new AccountFragment());

                    }
                    return true;
                }
            };

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}