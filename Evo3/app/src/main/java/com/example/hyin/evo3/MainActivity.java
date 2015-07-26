package com.example.hyin.evo3;

import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.preference.DialogPreference;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import android.view.View;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;


import java.io.*;
import java.lang.String;

public class MainActivity extends ActionBarActivity {

    public class Specie {
        String latin; // record the latin name
        String english; // record the english name
        int Evo_level; // record the evo level in the evo tree. Cyanobacteria has the level 0
        Specie(){
            latin = "";
            english = "";
            Evo_level = 0;
        }
    }

    public String English_Name[] = new String[200];
    public String Latin_Name[] = new String[200];
    public Specie currMainSpecie = new Specie();
    public int DNA = 200, DNA_rate = 2; // TODO: change DNA back to 200 after testing
    public int EvoLevel_DNA_Table[] = new int[200];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        readLatinEnglishFile();
        readEvoLevelFile();
        currMainSpecie.latin = "Cyanobacteria"; currMainSpecie.english = "Cyanobacteria"; currMainSpecie.Evo_level = 0;
        DNA_reload();
        return;
    }

    public void readLatinEnglishFile() {
        try {
            InputStream in = getResources().openRawResource(R.raw.latinenglish);
            BufferedReader dataIO = new BufferedReader(new InputStreamReader(in));
            String s_temp;
            for(int i=0; i<200; i++) {English_Name[i]=""; Latin_Name[i]="";}
            int cnt=0;
            while ((s_temp = dataIO.readLine()) != null) {
                if (s_temp.equals("------------------")) {
                    Latin_Name[cnt] = dataIO.readLine();
                    English_Name[cnt] = dataIO.readLine();
                    cnt++;
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return;
    }

    public void readEvoLevelFile() {
        try {
            InputStream in = getResources().openRawResource(R.raw.evolevel);
            BufferedReader dataIO = new BufferedReader(new InputStreamReader(in));
            String s_temp;
            for(int i=0; i<200; i++) {EvoLevel_DNA_Table[i]=2;}
            int cnt=0;
            while ((s_temp = dataIO.readLine()) != null) {
                EvoLevel_DNA_Table[cnt] = Integer.parseInt(s_temp);
                cnt++;
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return;
    }

    public void DNA_reload(){
        DNA_rate = EvoLevel_DNA_Table[currMainSpecie.Evo_level];
        TextView txV5 = (TextView) findViewById(R.id.textView5);
        txV5.setText("DNA: "+DNA);
        TextView txV6 = (TextView) findViewById(R.id.textView6);
        txV6.setText("DNA: -"+DNA_rate);
        TextView txV7 = (TextView) findViewById(R.id.textView7);
        txV7.setText("DNA: -"+DNA_rate);
        TextView txV8 = (TextView) findViewById(R.id.textView8);
        txV8.setText("DNA: -"+DNA_rate);
        return;
    }

    public String Latin2English(String s){
        for(int i=0; i<100; i++){
            if(Latin_Name[i].equals(s)){
                return English_Name[i];
            }
        }
        return s;
    }

    public String English2Latin(String s){
        for(int i=0; i<100; i++){
            if(English_Name[i].equals(s)){
                return Latin_Name[i];
            }
        }
        return s;
    }

    public void evo_update(String s){
        try{
            // Open File
            InputStream in = getResources().openRawResource(R.raw.index);
            BufferedReader dataIO = new BufferedReader(new InputStreamReader(in));
            // Find the index from document file
            String s_temp = null, sub2="", sub3="", sub4="";
            while((s_temp = dataIO.readLine())!=null){
                if(s_temp.equals(s+" Evo")){
                    sub2 = dataIO.readLine();
                    sub3 = dataIO.readLine();
                    sub4 = dataIO.readLine();
                    break;
                }
            }
            // Update evo names
            TextView txV2 = (TextView) findViewById(R.id.textView2);
            TextView txV3 = (TextView) findViewById(R.id.textView3);
            TextView txV4 = (TextView) findViewById(R.id.textView4);
            txV2.setText(Latin2English(sub2));
            txV3.setText(Latin2English(sub3));
            txV4.setText(Latin2English(sub4));
            // Update evo pictures
            setImage2(sub2.toLowerCase());
            setImage3(sub3.toLowerCase());
            setImage4(sub4.toLowerCase());
            // Update Curr Main Specie Structure
            currMainSpecie.Evo_level = currMainSpecie.Evo_level+1;
            currMainSpecie.english = Latin2English(s);
            currMainSpecie.latin = English2Latin(s);
            // Update DNA coefficients
            DNA = DNA-DNA_rate;
            DNA_reload();
            // Close File
            dataIO.close();
            in.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return;
    }

    public void ResetEvo(View v){
        alert2();
        return;
    }

    public void ResetEvoCore(){
        String origin1 = "Cyanobacteria", origin2 = "Euglena", origin3 = "Amoeba", origin4 = "Tintinnids";
        TextView txV1 = (TextView) findViewById(R.id.textView);
        TextView txV2 = (TextView) findViewById(R.id.textView2);
        TextView txV3 = (TextView) findViewById(R.id.textView3);
        TextView txV4 = (TextView) findViewById(R.id.textView4);
        txV1.setText(Latin2English(origin1));
        txV2.setText(Latin2English(origin2));
        txV3.setText(Latin2English(origin3));
        txV4.setText(Latin2English(origin4));
        setImage1(origin1.toLowerCase());
        setImage2(origin2.toLowerCase());
        setImage3(origin3.toLowerCase());
        setImage4(origin4.toLowerCase());
        currMainSpecie.latin = "Cyanobacteria"; currMainSpecie.english = "Cyanobacteria"; currMainSpecie.Evo_level = 0;
        DNA_reload();
        return;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight, int samplerate) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        //options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inSampleSize = samplerate;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    private void setImage1(String origin1){
        ImageButton IBmain = (ImageButton) findViewById(R.id.imageButton);
        int res = getResources().getIdentifier(origin1, "drawable", getPackageName());
        IBmain.setImageBitmap(decodeSampledBitmapFromResource(getResources(), res, IBmain.getWidth(), IBmain.getHeight(),1));
        return;
    }

    private void setImage2(String origin2){
        ImageButton IBsub2 = (ImageButton) findViewById(R.id.imageButton2);
        int res = getResources().getIdentifier(origin2,"drawable", getPackageName());
        IBsub2.setImageBitmap(decodeSampledBitmapFromResource(getResources(), res, IBsub2.getWidth(), IBsub2.getHeight(),4));
        return;
    }

    private void setImage3(String origin3){
        ImageButton IBsub3 = (ImageButton) findViewById(R.id.imageButton3);
        int res = getResources().getIdentifier(origin3,"drawable", getPackageName());
        IBsub3.setImageBitmap(decodeSampledBitmapFromResource(getResources(), res, IBsub3.getWidth(), IBsub3.getHeight(),4));
        return;
    }

    private void setImage4(String origin4) {
        ImageButton IBsub4 = (ImageButton) findViewById(R.id.imageButton4);
        int res = getResources().getIdentifier(origin4,"drawable", getPackageName());
        IBsub4.setImageBitmap(decodeSampledBitmapFromResource(getResources(), res, IBsub4.getWidth(), IBsub4.getHeight(),4));
        return;
    }

    public void Generation2(View v){
        if(DNA<DNA_rate) {alert1(); return;}
        ImageButton IBmain = (ImageButton) findViewById(R.id.imageButton);
        TextView txV2 = (TextView) findViewById(R.id.textView2);
        String s = (String)txV2.getText();
        s = English2Latin(s);
        // Name process
        if(s.equals("Nothing")) return;
        TextView txV1 = (TextView) findViewById(R.id.textView);
        txV1.setText(Latin2English(s));
        // Evolution process
        evo_update(s);
        // Picture process
        setImage1(s.toLowerCase());
        return;
    }

    public void Generation3(View v){
        if(DNA<DNA_rate) {alert1(); return;}
        ImageButton IBmain = (ImageButton) findViewById(R.id.imageButton);
        TextView txV3 = (TextView) findViewById(R.id.textView3);
        String s = (String)txV3.getText();
        s = English2Latin(s);
        // Name Process
        if(s.equals("Nothing")) return;
        TextView txV1 = (TextView) findViewById(R.id.textView);
        txV1.setText(Latin2English(s));
        // Evolution Process
        evo_update(s);
        // Picture Process
        setImage1(s.toLowerCase());
        return;
    }

    public void Generation4(View v){
        if(DNA<DNA_rate) {alert1(); return;}
        ImageButton IBmain = (ImageButton) findViewById(R.id.imageButton);
        TextView txV4 = (TextView) findViewById(R.id.textView4);
        String s = (String)txV4.getText();
        s = English2Latin(s);
        // Name Process
        if(s.equals("Nothing")) return;
        TextView txV1 = (TextView) findViewById(R.id.textView);
        txV1.setText(Latin2English(s));
        // Evolution Process
        evo_update(s);
        // Picture Process
        setImage1(s.toLowerCase());
        return;
    }

    public void alert1(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("No enough DNA...");
        alertDialog.setMessage("No enough DNA! Go to survive mode for more!");
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        alertDialog.show();
        return;
    }

    protected void alert2() {
        AlertDialog.Builder builder = new Builder(MainActivity.this);
        builder.setMessage("Are you sure to start over from Cyanobacteria?");
        builder.setTitle("Reset...");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ResetEvoCore();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
