package com.walnutapps.pickmydog;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Downloader;

import java.util.ArrayList;

import static com.walnutapps.pickmydog.R.id.dogPictureImageView2;
import static com.walnutapps.pickmydog.R.id.dogPictureImageView3;
import static com.walnutapps.pickmydog.R.id.dogPictureImageView4;
import static com.walnutapps.pickmydog.R.id.dogPictureImageView5;

/**
 * Created by Ivan on 2017-07-08.
 */

public class ViewPagerAdapter extends PagerAdapter{

    private Context context;
    private LayoutInflater layoutInflater;
    ImageView imageView;
    private Integer[] images;

    String[] photoNamesArray = {"floatingMainActionButton","floatingActionButton1", "floatingActionButton2", "floatingActionButton3", "floatingActionButton4", "floatingActionButton5"};
    ArrayList<Bitmap> imagesBitmapArrayList = new ArrayList<>();
    Bitmap[] imagesBitmapArray = new Bitmap[6];
    int dogNumber;
    final long ONE_MEGABYTE = 1024 * 1024;

    public static int LOOPS_COUNT = 1000;

    Bitmap bitmap;

    boolean morePics = true;
    boolean ready = false;
    int counter;





    public ViewPagerAdapter(Context context){
        //
        this.context = context;
//        this.Uid = Uid;
//        this.dogNumber = dogNumber;
        //DownloadFilesTask dt = new DownloadFilesTask();
       // dt.execute();
        //getImages();
    }


    @Override
    public int getCount() {
        if (imagesBitmapArrayList != null && imagesBitmapArrayList.size() > 0)
        {
            return imagesBitmapArrayList.size()*LOOPS_COUNT; // simulate infinite by big number of products
        }
        else
        {
            return 0;
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        Log.i("Positon      :", String.valueOf(position));
        Log.i("Size of Array     :", String.valueOf(imagesBitmapArrayList.size()));
        Log.i("Mod of the two above  :", " " + (position % imagesBitmapArrayList.size()));
//        int position2;
//        if(imagesBitmapArrayList.size() == 0) {
//        position2 = 0 ;
//        }else if(!this.morePics){
//            position2 = position % imagesBitmapArrayList.size();
//        }else{
//            position2 = imagesBitmapArrayList.size()-1;
//        }


//
        if(imagesBitmapArrayList != null && imagesBitmapArrayList.size() > 0){
            if(!this.morePics){
                position = position % imagesBitmapArrayList.size();
                counter = 0;
            }

            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View view = layoutInflater.inflate(R.layout.custom_layout, null);
            imageView = (ImageView) view.findViewById(R.id.customImageView);
            imageView.setImageBitmap(Bitmap.createScaledBitmap(imagesBitmapArrayList.get(position - counter), 300, 300, false));
//            imageView.setOnTouchListener(new View.OnTouchListener() {
//                private GestureDetector gestureDetector = new GestureDetector(view.getContext(), new GestureDetector.SimpleOnGestureListener(){
//                    @Override
//                    public boolean onDoubleTap(MotionEvent e) {
//                        Log.i("Please ", "work");
//                        refreshVsTab();
//                        givePointsToDog();
//                        return super.onDoubleTap(e);
//                    }
//                });
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    gestureDetector.onTouchEvent(event);
//                    return true;
//                }
//            });
            //imageView.setImageBitmap(imagesBitmapArrayList.get(position));

            ViewPager vp = (ViewPager) container;
            vp.addView(view, 0);
            //this.notifyDataSetChanged();

            Log.i("Finished", "Run");
            Log.i("Finished", "outside");
            counter ++;

            return view;

        }else{
            return null;
        }
//        if(!morePics){
//            position2 = position % imagesBitmapArrayList.size();
//            Log.i("More pics", "false");
//        }else{
//            position2 = imagesBitmapArrayList.size() -1;
//        }



    }

    private void refreshVsTab() {

    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        ViewPager vp = (ViewPager)container;
        View view = (View)object;

        vp.removeView(view);
    }

    public void addToBitmapArray(Bitmap bitmap){

        imagesBitmapArrayList.add(bitmap);
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();

    }
    public void noitfyNoMorePics() {
        this.morePics = false;
    }
    public void setReady(){

        this.ready = true;
    }

    @Override
    public void finishUpdate(ViewGroup container) {

        super.finishUpdate(container);
    }

}
