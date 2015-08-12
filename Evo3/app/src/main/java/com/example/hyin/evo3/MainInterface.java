package com.example.hyin.evo3;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.OutputStreamWriter;


public class MainInterface extends ActionBarActivity {

    public Bitmap bitmap1 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setTitle("Darwin's Tree");
        setContentView(R.layout.activity_main_interface);
        ImageView IV = (ImageView) findViewById(R.id.imageView11);
        int res = getResources().getIdentifier("darwinbackground", "drawable", getPackageName());
        bitmap1 = decodeSampledBitmapFromResource(getResources(), res, IV.getWidth(), IV.getHeight(), 1);
        IV.setImageBitmap(bitmap1);
        return;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight, int samplerate) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inJustDecodeBounds = true;
        // BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        // options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inSampleSize = samplerate;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public void ResetRecord(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainInterface.this);
        builder.setMessage("Are you sure to clear up your saved record?");
        builder.setTitle("Clear record...");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                clearRecord();
                return;
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                return;
            }
        });
        builder.create().show();
        return;
    }

    public void clearRecord(){
        // Clear up and initialize the record
        try {
            OutputStreamWriter fos = new OutputStreamWriter(openFileOutput("playerinformation", Context.MODE_PRIVATE));
            fos.write(Integer.toString(200)); fos.write('\n'); // TODO: change this number back to 200 after testing
            fos.write("Cyanobacteria"); fos.write('\n');
            fos.write(Integer.toString(0)); fos.write('\n');
            for(int i=0; i<15; i++) {
                fos.write("Nothing");fos.write('\n'); // Set Animal Latin Name
                fos.write(Integer.toString(0)); fos.write('\n'); //Set Evo_level
            }
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Clear up achievement as well
        try {
            OutputStreamWriter fos = new OutputStreamWriter(openFileOutput("achievementinformation", Context.MODE_PRIVATE));
            for(int i=0; i<200; i++) {
                fos.write("No");fos.write('\n'); // Set every achievement as not completed
            }
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Write player's Survival Level Infromation
        try {
            OutputStreamWriter fos = new OutputStreamWriter(openFileOutput("survivalinformation", Context.MODE_PRIVATE));
            fos.write(Integer.toString(0)); fos.write('\n');
            for(int i=0; i<100; i++) {
                fos.write("No"); fos.write('\n'); // Set every survival level as not completed
            }
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Launch Evo activity
        Intent intent = new Intent(this, MainActivity.class);
        String message = "Manage your party";
        // Change Interface to "Loading..." display
        turnoffLight();
        recycleBackground();
        startActivity(intent);
        finish();
        return;
    }

    public void goEvo(View view){
        Intent intent = new Intent(this, MainActivity.class);
        String message = "Manage your party";
        // Change Interface to "Loading..." display
        turnoffLight();
        recycleBackground();
        startActivity(intent);
        finish();
        return;
    }

    public void goAchievement(View view){
        Intent intent = new Intent(this, Achievement.class);
        String message = "See your game objectives";
        // Change Interface to "Loading..." display
        turnoffLight();
        recycleBackground();
        startActivity(intent);
        finish();
        return;
    }

    public void goAbout(View view){
        Intent intent = new Intent(this, about.class);
        String message = "Going to the about page";
        // Change Interface to "Loading..." display
        turnoffLight();
        recycleBackground();
        startActivity(intent);
        finish();
        return;
    }

    public void recycleBackground(){
        // Recycle the Bitmap
        if(bitmap1!=null && !bitmap1.isRecycled()){
            bitmap1.recycle();
            bitmap1 = null;
        }
        System.gc();
        return;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_interface, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        return;
    }

    public void turnoffLight(){
        ImageView IVmain = (ImageView) findViewById(R.id.imageView11);
        IVmain.setImageResource(android.R.color.black);
        View TV1 = (View) findViewById(R.id.textView10);
        TV1.setVisibility(View.GONE);
        TextView TV2 = (TextView) findViewById(R.id.textViewDisplayLoading);
        TV2.setText("Loading...");
        View B1 = (View) findViewById(R.id.button4);
        B1.setVisibility(View.GONE);
        View B2 = (View) findViewById(R.id.button5);
        B2.setVisibility(View.GONE);
        View B3 = (View) findViewById(R.id.button6);
        B3.setVisibility(View.GONE);
        View B4 = (View) findViewById(R.id.button7);
        B4.setVisibility(View.GONE);
        return;
    }

}
