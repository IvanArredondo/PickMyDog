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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class ViewPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private Integer[] images;
    String Uid = "";
    String[] photoNamesArray = {"floatingMainActionButton","floatingActionButton1", "floatingActionButton2", "floatingActionButton3", "floatingActionButton4", "floatingActionButton5"};
    ArrayList<Bitmap> imagesBitmapArrayList = new ArrayList<>();
    Bitmap[] imagesBitmapArray = new Bitmap[6];
    int dogNumber;
    final long ONE_MEGABYTE = 1024 * 1024;


    Bitmap bitmap;

    private StorageReference mStorageRef;



    public ViewPagerAdapter(Context context){
        mStorageRef = FirebaseStorage.getInstance().getReference();
        //

        this.context = context;
//        this.Uid = Uid;
//        this.dogNumber = dogNumber;
        //DownloadFilesTask dt = new DownloadFilesTask();
       // dt.execute();
        //getImages();
    }

    private void getImages() {
        int count = 0;

        for(final String photoName : photoNamesArray) {
            StorageReference getDogPicturesStorageReference = mStorageRef.child(Uid).child(String.valueOf(dogNumber)).child(photoName);

            final int finalCount = count;
            getDogPicturesStorageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap dogPictureBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imagesBitmapArray[finalCount] = dogPictureBitmap;


                    // Data for "images/island.jpg" is returns, use this as needed
                }
            });
            count ++;
        }

    }

    @Override
    public int getCount() {

        return imagesBitmapArrayList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        //Log.i("bitmap:", imagesBitmapArray[0].toString());


        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.custom_layout, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.customImageView);
        imageView.setImageBitmap(Bitmap.createScaledBitmap(imagesBitmapArrayList.get(position), context.getApplicationContext().getResources().getConfiguration().screenWidthDp, context.getApplicationContext().getResources().getConfiguration().screenWidthDp, false));
        //imageView.setImageBitmap(imagesBitmapArrayList.get(position));

        ViewPager vp =  (ViewPager)container;
        vp.addView(view, 0);

        Log.i("Finished", "Run");
        Log.i("Finished", "outside");

        return view;

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
}
