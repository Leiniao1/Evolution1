package com.example.hyin.evo3;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


public class MainActivity2Activity extends ActionBarActivity {

    public Bitmap bitmapArray[] = new Bitmap[16];
    public Bitmap bitmapMain = null, bitmap5 = null;

    public class Specie {
        String latin; // record the latin name
        String english; // record the english name
        int Evo_level; // record the evo level in the evo tree. Cyanobacteria has the level 0
        int Attack, Defence;
        String Size, Diet, Food[] = new String[9], Environ[] = new String[9];
        boolean available;
        Specie(){
            latin = "Nothing";
            english = "Nothing";
            Evo_level = 0;
            Attack = 0;
            Defence = 0;
            Size = "Micro";
            Diet = "Herbivore";
            for(int i=0; i<9; i++) {Food[i]=""; Environ[i]="";}
            available = false;
        }
    }

    public String English_Name[] = new String[200];
    public String Latin_Name[] = new String[200];
    public Specie currMainSpecie = new Specie();
    public Specie otherSpecie[] = new Specie[15];
    public int DNA = 1200, DNA_rate = 2; // TODO: change DNA back to 200 after testing
    public int currMainIndex = 0;

    public boolean AchievementRecord[] = new boolean[200];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Darwin's Tree");
        setContentView(R.layout.activity_main_activity2);
        readLatinEnglishFile();
        for(int i=0; i<15; i++) {otherSpecie[i]=new Specie();}
        read2App(); // reload user information data, read animal party and current DNA amount
        initialSet();
        // reload your party members:
        loadImage();
        achievement_check();
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


    public void initialSet(){
        for(int i=0; i<16; i++) {bitmapArray[i] = null;}
        currMainIndex = 0;
        return;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity2, menu);
        return true;
    }

    public String Latin2English(String s){
        for(int i=0; i<200; i++){
            if(Latin_Name[i].equals(s)){
                return English_Name[i];
            }
        }
        return s;
    }

    public String English2Latin(String s){
        for(int i=0; i<200; i++){
            if(English_Name[i].equals(s)){
                return Latin_Name[i];
            }
        }
        return s;
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
                dialog.dismiss();
            }
        });

        dialog.show();

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

    public void achievement_check(){
        ReadAchievementRecord();
        if(AchievementRecord[1]==false){
            AchievementRecord[1]=true;
            AchievementAlert(1);
        }
        WriteAchievementRecord();
        return;
    }

    public void achievement_check2(){
        ReadAchievementRecord();
        if(AchievementRecord[2]==false){
            AchievementRecord[2]=true;
            AchievementAlert(2);
        }
        WriteAchievementRecord();
        return;
    }

    public void goEvo(View view){
        Intent intent = new Intent(this, MainActivity.class);
        write2File();
        recycleAll(1);
        String message = "Manage your party";
        startActivity(intent);
        finish();
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


    private void setImageMain(String origin1){
        ImageView IVmain = (ImageView) findViewById(R.id.imageView5);
        int res = getResources().getIdentifier(origin1, "drawable", getPackageName());
        IVmain.setImageBitmap(bitmapMain = decodeSampledBitmapFromResource(getResources(), res, IVmain.getWidth(), IVmain.getHeight(), 2));
        return;
    }

    private void setAImage(String s, ImageView IV){
        int res = getResources().getIdentifier(s, "drawable", getPackageName());
        IV.setImageBitmap(bitmapMain = decodeSampledBitmapFromResource(getResources(), res, IV.getWidth(), IV.getHeight(), 4));
        return;
    }

    public void loadImage(){
        setImageMain(English2Latin(currMainSpecie.latin).toLowerCase());
        TextView TVMain = (TextView) findViewById(R.id.textView25);
        TVMain.setText(Latin2English(currMainSpecie.english));
        for(int i=0; i<16; i++) {
            String viewName = "imageView" + (i+11) ;
            ImageView IMtemp = (ImageView) findViewById(getResources().getIdentifier(viewName, "id", getPackageName()));
            if(i==currMainIndex) {
                setAImage(English2Latin(currMainSpecie.latin).toLowerCase(), IMtemp);}
            else if(i<currMainIndex){
                setAImage(English2Latin(otherSpecie[i].latin).toLowerCase(), IMtemp);}
            else if(i>currMainIndex){
                setAImage(English2Latin(otherSpecie[i-1].latin).toLowerCase(), IMtemp);
            }
        }
        ImageView IVmain = (ImageView)findViewById(R.id.imageView11);
        IVmain.setPadding(2, 2, 2, 2);
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

    private void clearPadding(){
        for(int i=0; i<16; i++) {
            String viewName = "imageView" + (i+11) ;
            ImageView IMtemp = (ImageView) findViewById(getResources().getIdentifier(viewName, "id", getPackageName()));
            IMtemp.setPadding(0,0,0,0);
        }
        return;
    }


    public void alert1(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("No room in your party...");
        alertDialog.setMessage("No room in your party! Try die out a member you don't like!");
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        alertDialog.show();
        return;
    }

    public void clone(View view){
        achievement_check2();
        boolean suc = false;
        for(int i=0; i<15; i++) {
            if(otherSpecie[i].latin.toLowerCase().equals("nothing")){
                // Clone data
                deepCopy(otherSpecie[i], currMainSpecie);
                // Set image
                write2File();
                loadImage();
                suc = true;break;
            }
        }
        if(suc==false) {
            alert1();
        }
        return;
    }

    public void alert2(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Should keep one...");
        alertDialog.setMessage("You cannot die out your only party member!");
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        alertDialog.show();
        return;
    }

    public void dieOut(View view){
        clearPadding();
        if(otherSpecie[0].latin.equals("Nothing")){
            alert2();
        }
        else {
            int toIndex = 14;
            for(int i=0; i<15; i++){
                if(otherSpecie[i].latin.equals("Nothing")){
                    toIndex = i-1; break;
                }
            }
            deepCopy(currMainSpecie,otherSpecie[toIndex]);
            otherSpecie[toIndex] = new Specie();
            currMainIndex = toIndex;
            switchIndex(0,toIndex);
            write2File();
            loadImage();
        }
        return;
    }

    private boolean deepCopy(Specie to, Specie from){
        to.latin = from.latin;
        to.english = from.english;
        to.Evo_level = from.Evo_level;
        to.available = from.available;
        to.Attack = from.Attack;
        to.Defence = from.Defence;
        to.Diet = from.Diet;
        to.Size = from.Size;
        for(int i=0; i<9; i++) {to.Food[i] = from.Food[i]; to.Environ[i] = from.Environ[i];}
        return true;
    }

    private void switchIndex(int to, int from){
        currMainIndex = to;
        Specie SpecieTemp = new Specie();
        if(to == from) return;
        else if(to>from) {
            deepCopy(SpecieTemp, otherSpecie[to-1]);
            for(int i=to-1; i>from; i--) {
                // Deep Copy from Main Specie
                deepCopy(otherSpecie[i], otherSpecie[i-1]);
            }
            deepCopy(otherSpecie[from], currMainSpecie);
            deepCopy(currMainSpecie, SpecieTemp);
        }
        else {
            deepCopy(SpecieTemp, otherSpecie[to]);
            for(int i=to; i<from-1; i++) {
                // Deep Copy from Main Specie
                deepCopy(otherSpecie[i], otherSpecie[i+1]);
            }
            deepCopy(otherSpecie[from-1], currMainSpecie);
            deepCopy(currMainSpecie, SpecieTemp);
        }
        return;
    }

    public boolean detectNothing(int num){
        if(currMainIndex>num)  return (otherSpecie[num].latin.equals("Nothing"));
        if(currMainIndex==num) return false;
        if(currMainIndex<num) return (otherSpecie[num-1].latin.equals("Nothing"));
        return true;
    }

    public void switch11(View view){
        if(detectNothing(0)) return;
        clearPadding();
        view.setPadding(3, 3, 3, 3);
        switchIndex(0, currMainIndex);
        setImageMain(English2Latin(currMainSpecie.latin).toLowerCase());
        TextView TVmain = (TextView) findViewById(R.id.textView25);
        TVmain.setText(Latin2English(currMainSpecie.english));
        write2File();
        return;
    }

    public void switch12(View view){
        if(detectNothing(1)) return;
        clearPadding();
        view.setPadding(3,3, 3, 3);
        switchIndex(1, currMainIndex);
        setImageMain(English2Latin(currMainSpecie.latin).toLowerCase());
        TextView TVmain = (TextView) findViewById(R.id.textView25);
        TVmain.setText(Latin2English(currMainSpecie.english));
        write2File();
        return;
    }

    public void switch13(View view){
        if(detectNothing(2)) return;
        clearPadding();
        view.setPadding(3, 3, 3, 3);
        switchIndex(2, currMainIndex);
        setImageMain(English2Latin(currMainSpecie.latin).toLowerCase());
        TextView TVmain = (TextView) findViewById(R.id.textView25);
        TVmain.setText(Latin2English(currMainSpecie.english));
        write2File();
        return;
    }

    public void switch14(View view){
        if(detectNothing(3)) return;
        clearPadding();
        view.setPadding(3, 3, 3, 3);
        switchIndex(3, currMainIndex);
        setImageMain(English2Latin(currMainSpecie.latin).toLowerCase());
        TextView TVmain = (TextView) findViewById(R.id.textView25);
        TVmain.setText(Latin2English(currMainSpecie.english));
        write2File();
        return;
    }

    public void switch15(View view){
        if(detectNothing(4)) return;
        clearPadding();
        view.setPadding(3, 3, 3, 3);
        switchIndex(4, currMainIndex);
        setImageMain(English2Latin(currMainSpecie.latin).toLowerCase());
        TextView TVmain = (TextView) findViewById(R.id.textView25);
        TVmain.setText(Latin2English(currMainSpecie.english));
        write2File();
        return;
    }

    public void switch16(View view){
        if(detectNothing(5)) return;
        clearPadding();
        view.setPadding(3, 3, 3, 3);
        switchIndex(5, currMainIndex);
        setImageMain(English2Latin(currMainSpecie.latin).toLowerCase());
        TextView TVmain = (TextView) findViewById(R.id.textView25);
        TVmain.setText(Latin2English(currMainSpecie.english));
        write2File();
        return;
    }

    public void switch17(View view){
        if(detectNothing(6)) return;
        clearPadding();
        view.setPadding(3, 3, 3, 3);
        switchIndex(6, currMainIndex);
        setImageMain(English2Latin(currMainSpecie.latin).toLowerCase());
        TextView TVmain = (TextView) findViewById(R.id.textView25);
        TVmain.setText(Latin2English(currMainSpecie.english));
        write2File();
        return;
    }

    public void switch18(View view){
        if(detectNothing(7)) return;
        clearPadding();
        view.setPadding(3, 3, 3, 3);
        switchIndex(7, currMainIndex);
        setImageMain(English2Latin(currMainSpecie.latin).toLowerCase());
        TextView TVmain = (TextView) findViewById(R.id.textView25);
        TVmain.setText(Latin2English(currMainSpecie.english));
        write2File();
        return;
    }

    public void switch19(View view){
        if(detectNothing(8)) return;
        clearPadding();
        view.setPadding(3, 3, 3, 3);
        switchIndex(8, currMainIndex);
        setImageMain(English2Latin(currMainSpecie.latin).toLowerCase());
        TextView TVmain = (TextView) findViewById(R.id.textView25);
        TVmain.setText(Latin2English(currMainSpecie.english));
        write2File();
        return;
    }

    public void switch20(View view){
        if(detectNothing(9)) return;
        clearPadding();
        view.setPadding(3, 3, 3, 3);
        switchIndex(9, currMainIndex);
        setImageMain(English2Latin(currMainSpecie.latin).toLowerCase());
        TextView TVmain = (TextView) findViewById(R.id.textView25);
        TVmain.setText(Latin2English(currMainSpecie.english));
        write2File();
        return;
    }

    public void switch21(View view){
        if(detectNothing(10)) return;
        clearPadding();
        view.setPadding(3, 3, 3, 3);
        switchIndex(10, currMainIndex);
        setImageMain(English2Latin(currMainSpecie.latin).toLowerCase());
        TextView TVmain = (TextView) findViewById(R.id.textView25);
        TVmain.setText(Latin2English(currMainSpecie.english));
        write2File();
        return;
    }

    public void switch22(View view){
        if(detectNothing(11)) return;
        clearPadding();
        view.setPadding(3, 3, 3, 3);
        switchIndex(11, currMainIndex);
        setImageMain(English2Latin(currMainSpecie.latin).toLowerCase());
        TextView TVmain = (TextView) findViewById(R.id.textView25);
        TVmain.setText(Latin2English(currMainSpecie.english));
        write2File();
        return;
    }

    public void switch23(View view){
        if(detectNothing(12)) return;
        clearPadding();
        view.setPadding(3, 3, 3, 3);
        switchIndex(12, currMainIndex);
        setImageMain(English2Latin(currMainSpecie.latin).toLowerCase());
        TextView TVmain = (TextView) findViewById(R.id.textView25);
        TVmain.setText(Latin2English(currMainSpecie.english));
        write2File();
        return;
    }

    public void switch24(View view){
        if(detectNothing(13)) return;
        clearPadding();
        view.setPadding(3, 3, 3, 3);
        switchIndex(13, currMainIndex);
        setImageMain(English2Latin(currMainSpecie.latin).toLowerCase());
        TextView TVmain = (TextView) findViewById(R.id.textView25);
        TVmain.setText(Latin2English(currMainSpecie.english));
        write2File();
        return;
    }

    public void switch25(View view){
        if(detectNothing(14)) return;
        clearPadding();
        view.setPadding(3, 3, 3, 3);
        switchIndex(14, currMainIndex);
        setImageMain(English2Latin(currMainSpecie.latin).toLowerCase());
        TextView TVmain = (TextView) findViewById(R.id.textView25);
        TVmain.setText(Latin2English(currMainSpecie.english));
        write2File();
        return;
    }

    public void switch26(View view){
        if(detectNothing(15)) return;
        clearPadding();
        view.setPadding(3, 3, 3, 3);
        switchIndex(15, currMainIndex);
        setImageMain(English2Latin(currMainSpecie.latin).toLowerCase());
        TextView TVmain = (TextView) findViewById(R.id.textView25);
        TVmain.setText(Latin2English(currMainSpecie.english));
        write2File();
        return;
    }

    public void turnoffLight(int clear){
        // Set the backgroudn as black
        RelativeLayout RL = (RelativeLayout) findViewById(R.id.main2);
        RL.setBackgroundResource(android.R.color.black);
        // Set the loading text
        TextView TV2 = (TextView) findViewById(R.id.textView9);
        if(clear==1)
            TV2.setText("Go to the evolution interface...");
        else if(clear==2)
            TV2.setText("Go to the survival interface...");
        TV2.setTextColor(0xFFFFFFFF);
        // Reset the images to transparent first
        ImageView IBmain = (ImageView) findViewById(R.id.imageView5);
        IBmain.setImageResource(android.R.color.transparent);
        for(int i=0; i<16; i++){
            String viewName = "imageView"+(i+11);
            ImageView IBsub = (ImageView) findViewById(getResources().getIdentifier(viewName,"id",getPackageName()));
            IBsub.setImageResource(android.R.color.transparent);
        }
        Button B2 = (Button) findViewById(R.id.button2);
        B2.setVisibility(View.GONE);
        Button B3 = (Button) findViewById(R.id.button3);
        B3.setVisibility(View.GONE);
        LinearLayout LL6 = (LinearLayout) findViewById(R.id.LL6);
        LL6.setBackgroundResource(android.R.color.black);
        LL6.removeAllViews();
        RL.removeView(LL6);
        LinearLayout LL7 = (LinearLayout) findViewById(R.id.LL7);
        LL7.removeAllViews();
        RL.removeView(LL7);
        return;
    }

    public void recycleAll (int clear) {
        turnoffLight(clear);
        // Recycle the Bitmaps
        if(bitmapMain !=null && !bitmapMain.isRecycled()){
            bitmapMain.recycle();
            bitmapMain = null;
        }
        if(bitmap5 !=null && !bitmap5.isRecycled()){
            bitmap5.recycle();
            bitmap5 = null;
        }
        for(int i=0; i<16; i++) {
            if (bitmapArray[i] != null && !bitmapArray[i].isRecycled()) {
                bitmapArray[i].recycle();
                bitmapArray[i] = null;
            }
        }
        System.gc();
        return;
    }

}
