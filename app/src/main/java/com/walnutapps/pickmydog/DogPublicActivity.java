package com.walnutapps.pickmydog;

import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import static com.walnutapps.pickmydog.R.id.pager;

public class DogPublicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_public);

        ViewPager mImageViewPager = (ViewPager) findViewById(pager);
        PagerAdapter adapter = new PhotosAdapter();
        mImageViewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(mImageViewPager, true);
    }
}
