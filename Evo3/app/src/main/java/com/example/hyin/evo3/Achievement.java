package com.example.hyin.evo3;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Achievement extends ActionBarActivity {

    public int totalRowNum = 0;

    public Bitmap bitmap1=null, bitmap2=null, bitmap3=null, bitmap4=null, bitmap5=null;

    public boolean AchievementRecord[] = new boolean[200];

    private void ReadAchievementRecord(){
        for(int i=0; i<200; i++){
            AchievementRecord[i] = false;
        }

        try {
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(
                    openFileInput("achievementinformation")));
            for(int i=0; i<200; i++) {
                String temp = (inputReader.readLine());
                if(temp.equals("No")) {AchievementRecord[i]=false;}
                else {AchievementRecord[i]=true;}
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Darwin's Tree");
        setContentView(R.layout.activity_achievement);
        ReadAchievementRecord();
        initialSet();
        return;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_achievement, menu);
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

    public void initialSet(){

        String title[] = new String[200];
        int prize[] = new int[200];
        String icon[] = new String[200];
        String Description[] = new String[200];
        int achievement_num = 0;

        try{
            // Open File
            InputStream in = getResources().openRawResource(R.raw.achievement);
            BufferedReader dataIO = new BufferedReader(new InputStreamReader(in));
            // Find the index from document file
            String s_temp = null, sub2="", sub3="", sub4="";
            int num=0;
            while((s_temp = dataIO.readLine())!=null){
                if(num%5==1){
                    title[num/5] = s_temp;}
                else if(num%5==2){
                    icon[num/5] = s_temp;}
                else if(num%5==3){
                    prize[num/5] = Integer.parseInt(s_temp);}
                else if(num%5==4){
                    Description[num/5] = s_temp;
                    achievement_num++;
                }
                num++;
            }
            // Close File
            dataIO.close();
            in.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }

        int i=0;

        while(AchievementRecord[i]==true) {i++; achievement_num--;}
        if(achievement_num>=1) {
            ImageView IV1 = (ImageView) findViewById(R.id.imageView27);
            int res = getResources().getIdentifier(icon[i], "drawable", getPackageName());
            IV1.setImageBitmap(bitmap1 = decodeSampledBitmapFromResource(getResources(), res, IV1.getWidth(), IV1.getHeight(), 1));
            TextView TV1 = (TextView) findViewById(R.id.textView27);
            TV1.setText(title[i]);
            TextView TV2 = (TextView) findViewById(R.id.textView270);
            TV2.setText("Prize: " + prize[i] + " DNA");
            TextView TV3 = (TextView) findViewById(R.id.textView271);
            TV3.setText(Description[i]);
            i++;
        }
        while(AchievementRecord[i]==true) {i++; achievement_num--;}
        if(achievement_num>=2) {
            ImageView IV1 = (ImageView) findViewById(R.id.imageView28);
            int res = getResources().getIdentifier(icon[i], "drawable", getPackageName());
            IV1.setImageBitmap(bitmap2 = decodeSampledBitmapFromResource(getResources(), res, IV1.getWidth(), IV1.getHeight(), 1));
            TextView TV1 = (TextView) findViewById(R.id.textView28);
            TV1.setText(title[i]);
            TextView TV2 = (TextView) findViewById(R.id.textView280);
            TV2.setText("Prize: "+ prize[i]+" DNA");
            TextView TV3 = (TextView) findViewById(R.id.textView281);
            TV3.setText(Description[i]);
            i++;
        }
        while(AchievementRecord[i]==true) {i++; achievement_num--;}
        if(achievement_num>=3) {
            ImageView IV1 = (ImageView) findViewById(R.id.imageView29);
            int res = getResources().getIdentifier(icon[i], "drawable", getPackageName());
            IV1.setImageBitmap(bitmap3 = decodeSampledBitmapFromResource(getResources(), res, IV1.getWidth(), IV1.getHeight(), 1));
            TextView TV1 = (TextView) findViewById(R.id.textView29);
            TV1.setText(title[i]);
            TextView TV2 = (TextView) findViewById(R.id.textView290);
            TV2.setText("Prize: "+ prize[i]+" DNA");
            TextView TV3 = (TextView) findViewById(R.id.textView291);
            TV3.setText(Description[i]);
            i++;
        }
        while(AchievementRecord[i]==true) {i++; achievement_num--;}
        if(achievement_num>=4) {
            ImageView IV1 = (ImageView) findViewById(R.id.imageView30);
            int res = getResources().getIdentifier(icon[i], "drawable", getPackageName());
            IV1.setImageBitmap(bitmap4 = decodeSampledBitmapFromResource(getResources(), res, IV1.getWidth(), IV1.getHeight(), 1));
            TextView TV1 = (TextView) findViewById(R.id.textView30);
            TV1.setText(title[i]);
            TextView TV2 = (TextView) findViewById(R.id.textView300);
            TV2.setText("Prize: "+ prize[i]+" DNA");
            TextView TV3 = (TextView) findViewById(R.id.textView301);
            TV3.setText(Description[i]);
            i++;
        }
        while(AchievementRecord[i]==true) {i++; achievement_num--;}
        if(achievement_num>=5) {
            ImageView IV1 = (ImageView) findViewById(R.id.imageView31);
            int res = getResources().getIdentifier(icon[i], "drawable", getPackageName());
            IV1.setImageBitmap(bitmap5 = decodeSampledBitmapFromResource(getResources(), res, IV1.getWidth(), IV1.getHeight(), 1));
            TextView TV1 = (TextView) findViewById(R.id.textView31);
            TV1.setText(title[i]);
            TextView TV2 = (TextView) findViewById(R.id.textView310);
            TV2.setText("Prize: "+ prize[i]+" DNA");
            TextView TV3 = (TextView) findViewById(R.id.textView311);
            TV3.setText(Description[i]);
        }

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

    private List<Map<String, Object>> getData(){
        List<Map<String,Object>> list = new ArrayList<Map<String, Object>>();
        Map<String,Object> map = new HashMap<String, Object>();

        String title[] = new String[200];
        int prize[] = new int[200];
        String icon[] = new String[200];
        String Description[] = new String[200];
        int achievement_num = 0;

        try{
            // Open File
            InputStream in = getResources().openRawResource(R.raw.achievement);
            BufferedReader dataIO = new BufferedReader(new InputStreamReader(in));
            // Find the index from document file
            String s_temp = null, sub2="", sub3="", sub4="";
            int num=0;
            while((s_temp = dataIO.readLine())!=null){
                if(num%5==1){
                    title[num/5] = s_temp;}
                else if(num%5==2){
                    icon[num/5] = s_temp;}
                else if(num%5==3){
                    prize[num/5] = Integer.parseInt(s_temp);}
                else if(num%5==4){
                    Description[num/5] = s_temp;
                    achievement_num++;
                }
                num++;
            }
            // Close File
            dataIO.close();
            in.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }

        for(int i=0; i<achievement_num; i++) {
            map = new HashMap<String, Object>();
            map.put("title", title[i]+" Prize: " + prize[i] + " DNA");
            map.put("info", Description[i]);
            int image = getResources().getIdentifier(icon[i],"drawable",getPackageName());
            map.put("img", image);
            list.add(map);
        }

        totalRowNum = achievement_num;

        return list;
    }

    public void goMainMenu(View view){
        Intent intent = new Intent(this, MainInterface.class);
        recycleAll(1);
        startActivity(intent);
        finish();
        return;
    }

    public void turnoffLight(int clear){
        RelativeLayout RL = (RelativeLayout) findViewById(R.id.RL3);
        RL.setBackgroundResource(android.R.color.black);
        TextView TV2 = (TextView) findViewById(R.id.textViewDisplayLoading3);
        if(clear==1)
            TV2.setText("Exit to Main Menu...");
        else if(clear==2)
            TV2.setText("Manage Your Party...");
        View TX5 = (View) findViewById(R.id.textViewGoMenu);
        TX5.setVisibility(View.GONE);
        View TX26 = (View) findViewById(R.id.textView26);
        TX26.setVisibility(View.GONE);
        View TXLeft = (View) findViewById(R.id.textViewLeft);
        TXLeft.setVisibility(View.GONE);
        ImageView IV1 = (ImageView) findViewById(R.id.imageView27);
        IV1.setImageResource(android.R.color.transparent);
        ImageView IV2 = (ImageView) findViewById(R.id.imageView28);
        IV2.setImageResource(android.R.color.transparent);
        ImageView IV3 = (ImageView) findViewById(R.id.imageView29);
        IV3.setImageResource(android.R.color.transparent);
        ImageView IV4 = (ImageView) findViewById(R.id.imageView30);
        IV4.setImageResource(android.R.color.transparent);
        ImageView IV5 = (ImageView) findViewById(R.id.imageView31);
        IV5.setImageResource(android.R.color.transparent);
        LinearLayout LL1 = (LinearLayout) findViewById(R.id.LL1);
        LL1.removeAllViews();
        RL.removeView(LL1);
        LinearLayout LL2 = (LinearLayout) findViewById(R.id.LL2);
        LL2.removeAllViews();
        RL.removeView(LL2);
        LinearLayout LL3 = (LinearLayout) findViewById(R.id.LL3);
        LL3.removeAllViews();
        RL.removeView(LL3);
        LinearLayout LL4 = (LinearLayout) findViewById(R.id.LL4);
        LL4.removeAllViews();
        RL.removeView(LL4);
        LinearLayout LL5 = (LinearLayout) findViewById(R.id.LL5);
        LL5.removeAllViews();
        RL.removeView(LL5);
        System.gc();
        return;
    }

    public void recycleAll (int clear) {
        turnoffLight(clear);
        // Recycle the Bitmaps
        if(bitmap1 !=null && !bitmap1.isRecycled()){
            bitmap1.recycle();
            bitmap1 = null;
        }
        if(bitmap2 !=null && !bitmap2.isRecycled()) {
            bitmap2.recycle();
            bitmap3 = null;
        }
        if(bitmap3 !=null && !bitmap3.isRecycled()) {
            bitmap3.recycle();
            bitmap3 = null;
        }
        if(bitmap4 !=null && !bitmap4.isRecycled()) {
            bitmap4.recycle();
            bitmap4 = null;
        }
        if(bitmap5 !=null && !bitmap5.isRecycled()) {
            bitmap5.recycle();
            bitmap5 = null;
        }
        System.gc();
        return;
    }
}
