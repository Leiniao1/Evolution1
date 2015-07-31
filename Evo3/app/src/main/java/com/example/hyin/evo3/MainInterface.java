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
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.OutputStreamWriter;


public class MainInterface extends ActionBarActivity {

    public Bitmap bitmap1 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
            fos.write(Integer.toString(1200)); fos.write('\n'); // TODO: change this number back to 200 after testing
            fos.write("Cyanobacteria"); fos.write('\n');
            fos.write(Integer.toString(0)); fos.write('\n');
            for(int i=0; i<9; i++) {
                fos.write("Nothing");fos.write('\n'); // Set Animal Latin Name
                fos.write(Integer.toString(0)); fos.write('\n'); //Set Evo_level
            }
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Launch Evo activity
        Intent intent = new Intent(this, MainActivity.class);
        String message = "Manage your party";
        startActivity(intent);
        finish();
        return;
    }

    public void goEvo(View view){
        Intent intent = new Intent(this, MainActivity.class);
        String message = "Manage your party";
        startActivity(intent);
        finish();
        return;
    }

    public void recycleBackground(){
        // Reset background image first
        ImageView IVmain = (ImageView) findViewById(R.id.imageView11);
        IVmain.setImageResource(android.R.color.transparent);
        // Recycle the Bitmap
        if(bitmap1!=null && !bitmap1.isRecycled()){
            bitmap1.recycle();
            bitmap1 = null;
        }
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
        recycleBackground();
        return;
    }
}
