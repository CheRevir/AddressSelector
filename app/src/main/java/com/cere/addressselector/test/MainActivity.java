package com.cere.addressselector.test;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.cere.addressselector.AddressSelectorDialog;
import com.cere.addressselector.OnAddressSelectorListener;
import com.cere.addressselector.test.ui.main.SectionsPagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements OnAddressSelectorListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(view -> AddressSelectorDialog.newInstance().show(getSupportFragmentManager()));
    }

    @Override
    public boolean onAddressSelected(String[] address) {
        Log.e("TAG", "MainActivity -> onAddressSelected: " + Arrays.toString(address));
        return false;
    }
}