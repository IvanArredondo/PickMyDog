package com.walnutapps.pickmydog;


import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;



public class DogPublicActivity extends AppCompatActivity {

    private static final int NUM_PAGES = 5;
    int numberOfDogs;
    String Uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_public);

       // ViewPager mImageViewPager = (ViewPager) findViewById(pager);
        Uid = getIntent().getStringExtra("Uid");
        getIntent().getIntExtra("numberOfDogs", numberOfDogs);

//        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, Uid, numberOfDogs);
//        mImageViewPager.setAdapter(viewPagerAdapter);



    }


    //    TabLayout tabLayout = (TabLayout) findViewById(R.id.tabDots);
//        tabLayout.setupWithViewPager(mImageViewPager, true);


}
