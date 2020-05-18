package com.sky21.booze_vendor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sky21.booze_vendor.Fragments.AccountFragment;
import com.sky21.booze_vendor.Fragments.CompleteFragment;
import com.sky21.booze_vendor.Fragments.CxpendingFragment;
import com.sky21.booze_vendor.Fragments.HomeFragment;
import com.sky21.booze_vendor.Fragments.PendingFragment;

public class MainActivity extends AppCompatActivity {
  String state;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        //   unselect_all_tv=findViewById(R.id.unselect_all_tv);


        final Intent intent = getIntent();
        state = intent.getStringExtra("STATE");

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.navigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        switch (item.getItemId()) {
                            case R.id.action_item1:
                                selectedFragment = new HomeFragment();
                                break;

                            case R.id.action_item3:
                                selectedFragment = new PendingFragment();
                                break;
                            case R.id.action_item4:
                                selectedFragment= new CxpendingFragment();
                                break;
                            case R.id.action_item5:
                                selectedFragment= new CompleteFragment();
                                break;
                            case R.id.action_item6:
                                selectedFragment= new AccountFragment();
                                break;

                        }
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.container, selectedFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        return true;
                    }
                });

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new HomeFragment());
        transaction.commit();
    }
}
