package com.example.hyin.evo3;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.View;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;


import java.io.*;
import java.lang.String;

public class MainActivity extends ActionBarActivity {

    public final static String EXTRA_MESSAGE = "Go to party interface";

    public boolean AchievementRecord[] = new boolean[200];

    public class Specie {
        String latin; // record the latin name
        String english; // record the english name
        int Evo_level; // record the evo level in the evo tree. Cyanobacteria has the level 0
        Specie(){
            latin = "Nothing";
            english = "Nothing";
            Evo_level = 0;
        }
    }

    public String English_Name[] = new String[800];
    public String Latin_Name[] = new String[800];
    public Specie currMainSpecie = new Specie();
    public Specie otherSpecie[] = new Specie[15];
    public int DNA = 200, DNA_rate = 2;
    public int EvoLevel_DNA_Table[] = new int[200];
    public Bitmap bitmap1 = null, bitmap2 = null, bitmap3 = null, bitmap4 = null, bitmap5=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setTitle("Darwin's Tree");
        setContentView(R.layout.activity_main);
        readLatinEnglishFile();
        readEvoLevelFile();
        currMainSpecie.latin = "Cyanobacteria"; currMainSpecie.english = "Cyanobacteria"; currMainSpecie.Evo_level = 0;
        for(int i=0; i<15; i++) {otherSpecie[i]=new Specie();}
        read2App(); // reload user information data, read animal party and current DNA amount
        initialSet(); // set current animal
        DNA_reload();// reload DNA_rate according to DNA_rate_table, and show them on screen
        return;
    }

    @Override
    public void onBackPressed(){

        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            Intent myIntent = new Intent(getApplicationContext(), MainInterface.class);
            recycleAll(1);
            startActivityForResult(myIntent, 0);
            finish();
        } else {
            getFragmentManager().popBackStack();
        }

        return;
    }

    private void initialSet(){
        TextView txV1 = (TextView) findViewById(R.id.textView);
        String s = currMainSpecie.latin;
        txV1.setText(Latin2English(s));
        animal_update(s);
        // Picture process
        setImage1(s.toLowerCase());
        return;
    }

    public void readLatinEnglishFile() {
        try {
            InputStream in = getResources().openRawResource(R.raw.latinenglish);
            BufferedReader dataIO = new BufferedReader(new InputStreamReader(in));
            String s_temp;
            for(int i=0; i<800; i++) {English_Name[i]=""; Latin_Name[i]="";}
            int cnt=0;
            while ((s_temp = dataIO.readLine()) != null) {
                if (s_temp.equals("------------------")) {
                    Latin_Name[cnt] = dataIO.readLine();
                    English_Name[cnt] = dataIO.readLine();
                    cnt++;
                }
            }
            dataIO.close();
            in.close();
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
            dataIO.close();
            in.close();
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
        for(int i=0; i<800; i++){
            if(Latin_Name[i].equals(s)){
                return English_Name[i];
            }
        }
        return s;
    }

    public String English2Latin(String s){
        for(int i=0; i<800; i++){
            if(English_Name[i].equals(s)){
                return Latin_Name[i];
            }
        }
        return s;
    }

    public void animal_update(String s){
        // Image Recycle
        recycleAll(0);
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
            // Update animal evo names
            TextView txV2 = (TextView) findViewById(R.id.textView2);
            TextView txV3 = (TextView) findViewById(R.id.textView3);
            TextView txV4 = (TextView) findViewById(R.id.textView4);
            txV2.setText(Latin2English(sub2));
            txV3.setText(Latin2English(sub3));
            txV4.setText(Latin2English(sub4));
            // Update animal evo pictures
            setImage2(sub2.toLowerCase());
            setImage3(sub3.toLowerCase());
            setImage4(sub4.toLowerCase());
            // Close File
            dataIO.close();
            in.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return;
    }

    private void ReadAchievementRecord(){
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

    private void WriteAchievementRecord(){
        // Clear up achievement as well
        try {
            OutputStreamWriter fos = new OutputStreamWriter(openFileOutput("achievementinformation", Context.MODE_PRIVATE));
            for(int i=0; i<200; i++) {
                if(AchievementRecord[i]) {
                    fos.write("Yes");fos.write('\n'); // Set every achievement as not complete
                }
                else{
                    fos.write("No");fos.write('\n'); // Set every achievement as not complete
                }
            }
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void AchievementAlert(int N){
        String s_temp = null, sub2="", sub3="", sub4="";
        int num=0, achievement_num=0, prize=0;
        String title="", icon="";
        try{
            // Open File
            InputStream in = getResources().openRawResource(R.raw.achievement);
            BufferedReader dataIO = new BufferedReader(new InputStreamReader(in));
            // Find the index from document file
            while((s_temp = dataIO.readLine())!=null){
                if(num%5==1 && achievement_num==N){
                    title = s_temp;}
                else if(num%5==2 && achievement_num==N){
                    icon = s_temp;}
                else if(num%5==3 && achievement_num==N){
                    prize = Integer.parseInt(s_temp);}
                else if(num%5==4){
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

        DNA+=prize;

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialogachievement);
        dialog.setTitle("You Got A Prize...");

        TextView text = (TextView) dialog.findViewById(R.id.text);
        text.setText("You completed the achievement: " + title + " You get " + prize + " DNA as prize");
        ImageView image = (ImageView) dialog.findViewById(R.id.image);
        int res = getResources().getIdentifier(icon,"drawable", getPackageName());
        image.setImageBitmap(bitmap5 = decodeSampledBitmapFromResource(getResources(), res, 5, 5, 4));

        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DNA_reload();
                dialog.dismiss();
            }
        });

        dialog.show();

        return;
    }

    public void achievement_check(){
        ReadAchievementRecord();
        if(AchievementRecord[0]==false && currMainSpecie.Evo_level>=2){
            AchievementRecord[0]=true;
            AchievementAlert(0);
        }
        if(AchievementRecord[5]==false && currMainSpecie.Evo_level>=5){
            AchievementRecord[5]=true;
            AchievementAlert(5);
        }
        if(AchievementRecord[6]==false && currMainSpecie.latin.equals("Physalia")){
            AchievementRecord[6]=true;
            AchievementAlert(6);
        }
        if(AchievementRecord[7]==false && currMainSpecie.latin.equals("Cestum")){
            AchievementRecord[7]=true;
            AchievementAlert(7);
        }
        if(AchievementRecord[8]==false && currMainSpecie.Evo_level>=10){
            AchievementRecord[8]=true;
            AchievementAlert(8);
        }
        if(AchievementRecord[10]==false && currMainSpecie.latin.equals("Dugesia")){
            AchievementRecord[10]=true;
            AchievementAlert(10);
        }
        if(AchievementRecord[11]==false && currMainSpecie.latin.equals("Paragordius")){
            AchievementRecord[11]=true;
            AchievementAlert(11);
        }
        if(AchievementRecord[12]==false && currMainSpecie.Evo_level>=15){
            AchievementRecord[12]=true;
            AchievementAlert(12);
        }
        if(AchievementRecord[14]==false && currMainSpecie.latin.equals("Alvinellidae")){
            AchievementRecord[14]=true;
            AchievementAlert(14);
        }
        if(AchievementRecord[15]==false && currMainSpecie.latin.equals("Gari")){
            AchievementRecord[15]=true;
            AchievementAlert(15);
        }
        if(AchievementRecord[16]==false && currMainSpecie.latin.equals("Thecosomata")){
            AchievementRecord[16]=true;
            AchievementAlert(16);
        }
        if(AchievementRecord[17]==false && currMainSpecie.latin.equals("Sepiapharaonis")){
            AchievementRecord[17]=true;
            AchievementAlert(17);
        }
        if(AchievementRecord[19]==false && currMainSpecie.Evo_level>=25){
            AchievementRecord[19]=true;
            AchievementAlert(19);
        }
        if(AchievementRecord[20]==false && currMainSpecie.latin.equals("Hymenodora")){
            AchievementRecord[20]=true;
            AchievementAlert(20);
        }
        if(AchievementRecord[21]==false && currMainSpecie.latin.equals("Delias")){
            AchievementRecord[21]=true;
            AchievementAlert(21);
        }
        WriteAchievementRecord();
        return;
    }

    public void evo_update(String s){
        animal_update(s);
        // Update Curr Main Specie Structure
        currMainSpecie.Evo_level = currMainSpecie.Evo_level+1;
        currMainSpecie.english = Latin2English(s);
        currMainSpecie.latin = English2Latin(s);
        // Update DNA coefficients
        DNA = DNA-DNA_rate;
        DNA_reload();
        achievement_check();
        return;
    }

    public void ResetEvo(View v){
        alert2();
        return;
    }

    public void ResetEvoCore(){
        // Image Recycle
        recycleAll (0);
        // Reset Everything
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
        write2File();
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
        //options.inJustDecodeBounds = true;
        // BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        // options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inSampleSize = samplerate;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    private void setImage1(String origin1){
        ImageButton IBmain = (ImageButton) findViewById(R.id.imageButton);
        int res = getResources().getIdentifier(origin1, "drawable", getPackageName());
        IBmain.setImageBitmap(bitmap1 = decodeSampledBitmapFromResource(getResources(), res, IBmain.getWidth(), IBmain.getHeight(),1));
        return;
    }

    private void setImage2(String origin2){
        ImageButton IBsub2 = (ImageButton) findViewById(R.id.imageButton2);
        int res = getResources().getIdentifier(origin2,"drawable", getPackageName());
        IBsub2.setImageBitmap(bitmap2 = decodeSampledBitmapFromResource(getResources(), res, IBsub2.getWidth(), IBsub2.getHeight(),5));
        return;
    }

    private void setImage3(String origin3){
        ImageButton IBsub3 = (ImageButton) findViewById(R.id.imageButton3);
        int res = getResources().getIdentifier(origin3,"drawable", getPackageName());
        IBsub3.setImageBitmap(bitmap3 = decodeSampledBitmapFromResource(getResources(), res, IBsub3.getWidth(), IBsub3.getHeight(),5));
        return;
    }

    private void setImage4(String origin4) {
        ImageButton IBsub4 = (ImageButton) findViewById(R.id.imageButton4);
        int res = getResources().getIdentifier(origin4,"drawable", getPackageName());
        IBsub4.setImageBitmap(bitmap4 = decodeSampledBitmapFromResource(getResources(), res, IBsub4.getWidth(), IBsub4.getHeight(),5));
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
        write2File();
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
        write2File();
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
        write2File();
        return;
    }

    public void recycleAll (int clear) {
        if(clear == 2){
            turnoffLight(clear);
        }
        else if(clear==1){
            turnoffLight(clear);
        }
        else if(clear==0){
            // Reset the images to transparent first
            ImageButton IBmain = (ImageButton) findViewById(R.id.imageButton);
            IBmain.setImageResource(android.R.color.transparent);
            ImageButton IBsub2 = (ImageButton) findViewById(R.id.imageButton2);
            IBsub2.setImageResource(android.R.color.transparent);
            ImageButton IBsub3 = (ImageButton) findViewById(R.id.imageButton3);
            IBsub3.setImageResource(android.R.color.transparent);
            ImageButton IBsub4 = (ImageButton) findViewById(R.id.imageButton4);
            IBsub4.setImageResource(android.R.color.transparent);
        }
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
        if(bitmap5!=null && !bitmap5.isRecycled()){
            bitmap5.recycle();
            bitmap5 = null;
        }
        System.gc();
        return;
    }

    public void alert1(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("No enough DNA...");
        alertDialog.setMessage("No enough DNA! Go to survival mode for more!");
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

    public void goParty(View view){
        Intent intent = new Intent(this, MainActivity2Activity.class);
        write2File();
        recycleAll(2);
        startActivity(intent);
        finish();
        return;
    }

    public void goMainMenu(View view){
        Intent intent = new Intent(this, MainInterface.class);
        recycleAll(1);
        startActivity(intent);
        finish();
        return;
    }

    public void write2File(){
        try {
            OutputStreamWriter fos = new OutputStreamWriter(openFileOutput("playerinformation", Context.MODE_PRIVATE));
            fos.write(Integer.toString(DNA)); fos.write('\n');
            fos.write(currMainSpecie.latin); fos.write('\n');
            fos.write(Integer.toString(currMainSpecie.Evo_level)); fos.write('\n');
            for(int i=0; i<15; i++) {
                fos.write(otherSpecie[i].latin);fos.write('\n'); // Set Animal Latin Name
                fos.write(Integer.toString(otherSpecie[i].Evo_level)); fos.write('\n'); //Set Evo_level
            }
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }

    public void read2App(){
        try {
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(
                    openFileInput("playerinformation")));
            DNA = Integer.parseInt(inputReader.readLine());
            currMainSpecie.latin = inputReader.readLine();
            currMainSpecie.english = Latin2English(currMainSpecie.latin);
            currMainSpecie.Evo_level = Integer.parseInt(inputReader.readLine());
            for(int i=0; i<15; i++) {
                otherSpecie[i].latin = inputReader.readLine();
                otherSpecie[i].english = Latin2English(otherSpecie[i].latin);
                otherSpecie[i].Evo_level = Integer.parseInt(inputReader.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    @Override
    protected void onDestroy(){
        super.onDestroy();
        return;
    }

    public void turnoffLight(int clear){
        RelativeLayout RL = (RelativeLayout) findViewById(R.id.RL2);
        RL.setBackgroundResource(android.R.color.black);
        TextView TV2 = (TextView) findViewById(R.id.textViewDisplayLoading2);
        if(clear==1)
            TV2.setText("Exit to Main Menu...");
        else if(clear==2)
            TV2.setText("Manage Your Party...");
        View TX5 = (View) findViewById(R.id.textView5);
        TX5.setVisibility(View.GONE);
        View TX23 = (View) findViewById(R.id.textView23);
        TX23.setVisibility(View.GONE);
        LinearLayout LO1 = (LinearLayout) findViewById(R.id.LO1);
        LinearLayout LO2 = (LinearLayout) findViewById(R.id.LO2);
        LinearLayout LO3 = (LinearLayout) findViewById(R.id.LO3);
        LO1.removeAllViews();
        LO2.removeAllViews();
        LO3.removeAllViews();
        RL.removeView(LO1);
        RL.removeView(LO2);
        RL.removeView(LO3);
        return;
    }

    public void checkInfo(View view){

        String animal = currMainSpecie.latin;
        String output = "Thanks so much to the attribution of the image source:"+'\n';
        boolean find = false;

        // Read in License Information
        try{
            // Open File
            InputStream in = getResources().openRawResource(R.raw.imagecopyright);
            BufferedReader dataIO = new BufferedReader(new InputStreamReader(in,"UTF8"));
            // Find the index from document file
            String s_temp = "";
            int num=0;
            while((s_temp = dataIO.readLine())!=null){
                if(s_temp.equals(animal)){
                    num++;
                    s_temp = dataIO.readLine();
                    output = output + s_temp;
                    find = true;
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

        if(find){
            alertThanks(output);
        }

        return;
    }

    public void alertThanks(String sentence){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Thanks to the author");
        alertDialog.setMessage(sentence);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        alertDialog.show();
        return;
    }

}
