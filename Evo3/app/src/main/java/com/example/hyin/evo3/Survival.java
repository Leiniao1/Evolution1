package com.example.hyin.evo3;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.HorizontalScrollView;
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


public class Survival extends ActionBarActivity {

    public Bitmap bitmap1=null, bitmap2=null, bitmap3=null, bitmapEnviron=null;
    public Bitmap[] bitmapParty = new Bitmap[16];
    public Bitmap[] bitmapSelect = new Bitmap[5];

    public String English_Name[] = new String[500];
    public String Latin_Name[] = new String[500];

    public Specie TeamSpecie[] = new Specie[16];
    public Specie BattleSpecie[] = new Specie[5];

    public int[] FitList = new int[16];
    public int currBattleNum = 0;
    public int DNA, WantedHeight;

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

    public class SurvivalLevel{
        public int levelNum;
        public String Name;
        public String Environ;
        public int Prize;
        public String[] Animals;
        SurvivalLevel(){
            levelNum=0;
            Name = "Level Unknown";
            Environ = "Ocean";
            Prize = 0;
            Animals = new String[5];
            for(int i=0; i<5; i++) {Animals[i] = "Nothing";}
        }
    }

    public SurvivalLevel[] survivals = new SurvivalLevel[100];
    public boolean[] SurvivalRecord = new boolean[100];
    public int currLevel = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Darwin's Tree");
        setContentView(R.layout.activity_survival);
        readLatinEnglishFile();
        for(int i = 0; i<16; i++) {TeamSpecie[i]=new Specie();}
        read2App();
        initialSet(); // Contains Refresh

        final LinearLayout RL = (LinearLayout) findViewById(R.id.LLTeam);
        final ViewTreeObserver vto = RL.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Put your code here.
                RL.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                WantedHeight = RL.getMeasuredHeight();
                reloadFitAnimal(); // Always call this after Refresh Survival Level Information
            }
        });

        return;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_survival, menu);
        return true;
    }

    public void readLatinEnglishFile() {
        try {
            InputStream in = getResources().openRawResource(R.raw.latinenglish);
            BufferedReader dataIO = new BufferedReader(new InputStreamReader(in));
            String s_temp;
            for(int i=0; i<500; i++) {English_Name[i]=""; Latin_Name[i]="";}
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

    public void read2App(){
        try {
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(
                    openFileInput("playerinformation")));
            DNA = Integer.parseInt(inputReader.readLine());
            for(int i=0; i<16; i++) {
                TeamSpecie[i].latin = inputReader.readLine();
                TeamSpecie[i].english = Latin2English(TeamSpecie[i].latin);
                TeamSpecie[i].Evo_level = Integer.parseInt(inputReader.readLine());
            }
            inputReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(int i=0; i<16; i++){
            if(!TeamSpecie[i].latin.equals("Nothing")){
                readAnimal(TeamSpecie[i].latin, i);
            }
        }

        return;
    }

    public void readAnimal(String animal, int index) {
        boolean findIt=false;

        try {
            InputStream in = getResources().openRawResource(R.raw.listofbattleanimal);
            BufferedReader dataIO = new BufferedReader(new InputStreamReader(in));
            String s_temp;

            while ((s_temp = dataIO.readLine()) != null) {
                if (s_temp.equals(animal)) {
                    findIt=true;
                    break;
                }
            }
            dataIO.close();
            in.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }

        if(!findIt) {
            TeamSpecie[index].available = false;
            return;
        }

        String Attack="", Defense="", Size="", Diet="", Environ="";
        TeamSpecie[index].available = true;

        try {
            InputStream in = getResources().getAssets().open(animal.toLowerCase()+".txt");
            BufferedReader dataIO = new BufferedReader(new InputStreamReader(in));
            String s_temp;
            int cnt=0;
            while ((s_temp = dataIO.readLine()) != null) {
                if(cnt==2) {Attack = s_temp; TeamSpecie[index].Attack = Integer.parseInt(Attack);}
                else if(cnt==3) {Defense=s_temp;TeamSpecie[index].Defence = Integer.parseInt(Defense);}
                else if(cnt==4) {Size=s_temp;TeamSpecie[index].Size = Size;}
                else if(cnt==5) {Diet=s_temp;TeamSpecie[index].Diet = Diet;}
                else if(cnt<15 && (s_temp.equals("Ocean")||s_temp.equals("Seashore")||s_temp.equals("Wetland")||s_temp.equals("Forest")||s_temp.equals("Jungle")||s_temp.equals("Grassland")||s_temp.equals("Tundra")||s_temp.equals("Mountain")||s_temp.equals("Desert")))
                {TeamSpecie[index].Environ[cnt-6]=s_temp;}
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


    public void initialSet(){

        for(int i=0; i<100; i++) {survivals[i] = new SurvivalLevel();}
        for(int i=0; i<16; i++) {bitmapParty[i] = null;}

        try{
            // Open File
            InputStream in = getResources().openRawResource(R.raw.survivallevel);
            BufferedReader dataIO = new BufferedReader(new InputStreamReader(in));
            // Find the index from document file
            String s_temp = null;
            int num=0;
            while((s_temp = dataIO.readLine())!=null){
                if(num%9==0){
                    survivals[num/9].levelNum = num/9;
                    survivals[num/9].Name = s_temp;}
                else if(num%9==1){
                    survivals[num/9].Environ = s_temp;}
                else if(num%9==2){
                    survivals[num/9].Prize = Integer.parseInt(s_temp);}
                else if(num%9>=3 && num%9<=7){
                    survivals[num/9].Animals[num%9-3] = s_temp;
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

        try {
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(
                    openFileInput("survivalinformation")));
            String temp = (inputReader.readLine());
            currLevel = Integer.parseInt(temp);
            for(int i=0; i<100; i++) {
                temp = (inputReader.readLine());
                if(temp.equals("No")) {SurvivalRecord[i]=false;}
                else {SurvivalRecord[i]=true;}
            }
            inputReader.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }

        refresh();

        return;
    }

    public String Latin2English(String s){
        for(int i=0; i<500; i++){
            if(Latin_Name[i].equals(s)){
                return English_Name[i];
            }
        }
        return s;
    }

    public String English2Latin(String s){
        for(int i=0; i<500; i++){
            if(English_Name[i].equals(s)){
                return Latin_Name[i];
            }
        }
        return s;
    }

    public void refresh(){

        // Set the background picture

        TextView TV1 = (TextView) findViewById(R.id.textViewEnviron);
        TV1.setText("Level " + survivals[currLevel].levelNum + ": " + survivals[currLevel].Name);
        TextView TV2 = (TextView) findViewById(R.id.textViewPrize);
        TV2.setText("Prize: " + survivals[currLevel].Prize + " DNA");
        ImageView IVBackground = (ImageView) findViewById(R.id.imageViewEnvironment);
        setImage(survivals[currLevel].Environ.toLowerCase(), 8, IVBackground, bitmapEnviron);

        // Set the level connections and animal pictures
        ImageView IVAnimalPro = (ImageView) findViewById(R.id.imageViewPro);
        TextView Line1 = (TextView) findViewById(R.id.ShortLine1);
        TextView Line2 = (TextView) findViewById(R.id.ShortLine2);
        if(currLevel==0){
            setImage("nothing", 8, IVAnimalPro, bitmap1);
            IVAnimalPro.setVisibility(View.INVISIBLE);
            Line1.setVisibility(View.INVISIBLE);
            Line2.setVisibility(View.INVISIBLE);
        }
        else {
            setImage(survivals[currLevel-1].Animals[0].toLowerCase(), 8, IVAnimalPro, bitmap1);
            IVAnimalPro.setVisibility(View.VISIBLE);
            Line1.setVisibility(View.VISIBLE);
            Line2.setVisibility(View.VISIBLE);
        }

        ImageView IVAnimalCurr = (ImageView) findViewById(R.id.imageViewCurr);
        setImage(survivals[currLevel].Animals[0].toLowerCase(), 4, IVAnimalCurr, bitmap2);
        IVAnimalCurr.setVisibility(View.VISIBLE);

        ImageView IVAnimalPost = (ImageView) findViewById(R.id.imageViewPost);
        TextView Line3 = (TextView) findViewById(R.id.ShortLine3);
        TextView Line4 = (TextView) findViewById(R.id.ShortLine4);
        if(currLevel==99){
            setImage("nothing", 8, IVAnimalPost, bitmap3);
            IVAnimalPost.setVisibility(View.INVISIBLE);
            Line3.setVisibility(View.INVISIBLE);
            Line4.setVisibility(View.INVISIBLE);
        }
        else {
            setImage(survivals[currLevel+1].Animals[0].toLowerCase(), 8, IVAnimalPost, bitmap3);
            IVAnimalPost.setVisibility(View.VISIBLE);
            Line3.setVisibility(View.VISIBLE);
            Line4.setVisibility(View.VISIBLE);
        }

        // Set the theme color based on the background
        String ColorBack="#97096ede", Color1="#951079ff", Color2="#69081357";
        if(survivals[currLevel].Environ.equals("Ocean")) {
            ColorBack="#97096ede"; Color1="#951079ff"; Color2="#69081357";
        }
        else if(survivals[currLevel].Environ.equals("Seashore")) {
            ColorBack="#470bcdde"; Color1="#bf7ccec5"; Color2="#63086e74";
        }
        else if(survivals[currLevel].Environ.equals("Wetland")) {
            ColorBack="#a381b793"; Color1="#e1a4d03e"; Color2="#c94c9548";
        }
        else if(survivals[currLevel].Environ.equals("Grassland")) {
            ColorBack="#5b90bb19"; Color1="#d5ced04c"; Color2="#a5769a0a";
        }
        else if(survivals[currLevel].Environ.equals("Forest")) {
            ColorBack="#7205830a"; Color1="#833c962d"; Color2="#7d083c03";
        }
        else if(survivals[currLevel].Environ.equals("Jungle")) {
            ColorBack="#82018351"; Color1="#83968667"; Color2="#86845b3d";
        }
        else if(survivals[currLevel].Environ.equals("Desert")) {
            ColorBack="#72e7a10a"; Color1="#7ba22106"; Color2="#81ffffff";
        }
        else if(survivals[currLevel].Environ.equals("Mountain")) {
            ColorBack="#5c907e6c"; Color1="#b0c8b483"; Color2="#7b776552";
        }
        else if(survivals[currLevel].Environ.equals("Tundra")) {
            ColorBack="#5c907e6c"; Color1="#b0c8b483"; Color2="#7b776552";
        }
        RelativeLayout RL4 = (RelativeLayout) findViewById(R.id.RL4);
        RL4.setBackgroundColor(Color.parseColor(ColorBack));
        Line1.setBackgroundColor(Color.parseColor(Color1));
        Line2.setBackgroundColor(Color.parseColor(Color1));
        IVAnimalPro.setBackgroundColor(Color.parseColor(Color1));
        IVAnimalCurr.setBackgroundColor(Color.parseColor(Color1));
        if(currLevel>=99 || SurvivalRecord[currLevel+1]==false){
            Line3.setBackgroundColor(Color.parseColor(Color2));
            Line4.setBackgroundColor(Color.parseColor(Color2));
            IVAnimalPost.setBackgroundColor(Color.parseColor(Color2));
        }
        else {
            Line3.setBackgroundColor(Color.parseColor(Color1));
            Line4.setBackgroundColor(Color.parseColor(Color1));
            IVAnimalPost.setBackgroundColor(Color.parseColor(Color1));
        }

        // Finally Write the record
        writeSurvivalRecord();
    }

    public void writeSurvivalRecord(){
        // Write player's Survival Level Infromation
        try {
            OutputStreamWriter fos = new OutputStreamWriter(openFileOutput("survivalinformation", Context.MODE_PRIVATE));
            fos.write(Integer.toString(currLevel)); fos.write('\n');
            for(int i=0; i<100; i++) {
                if(SurvivalRecord[i]==false){fos.write("No");}
                else {fos.write("Yes");}
                fos.write('\n'); // Set every achievement as not complete
            }
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }

    public void reloadFitAnimal(){
        String currEnviron = survivals[currLevel].Environ;

        int cnt = 0;
        for(int i=0; i<16; i++){FitList[i]=-1;}
        for(int i=0; i<16; i++){
            boolean repeated = false;
            for(int k=0; k<cnt; k++){
                if(TeamSpecie[FitList[k]].latin.equals(TeamSpecie[i].latin)){repeated = true; break;}
            }
            if(repeated) continue;
            for(int j=0; j<9; j++){
                if(currEnviron.equals(TeamSpecie[i].Environ[j])){
                    FitList[cnt] = i;
                    cnt++;
                    break;
                }
            }
        }

        // Reload Fitted Images
        for(int i=0; i<16; i++){
            String ViewName = "imageViewParty" + (i+1);
            ImageView IV = (ImageView) findViewById(getResources().getIdentifier(ViewName,"id",getPackageName()));
            IV.setImageBitmap(bitmapParty[i] = null);
        }
        for(int i=0; i<cnt; i++){
            String ViewName = "imageViewParty" + (i+1);
            ImageView IV = (ImageView) findViewById(getResources().getIdentifier(ViewName,"id",getPackageName()));
            setImage(TeamSpecie[FitList[i]].latin.toLowerCase(), 6, IV, bitmapParty[i]);
            IV.getLayoutParams().width = WantedHeight;
        }

        for(int i=0; i<5; i++){
            currBattleNum = 0;
            BattleSpecie[i] = new Specie();
            String ViewName = "imageViewMember" + (i+1);
            ImageView IV = (ImageView) findViewById(getResources().getIdentifier(ViewName,"id",getPackageName()));
            setImage("nothing", 6, IV, bitmapParty[i]);
        }
        return;
    }

    public void pro1(View view){
        if(currLevel<=0) return;
        currLevel--;
        refresh();
        reloadFitAnimal();
        return;
    }

    public void post1(View view){
        if(currLevel>=99) return;
        if(SurvivalRecord[currLevel+1]==false){
            alert1();
            return;
        }
        currLevel++;
        refresh();
        reloadFitAnimal();
        return;
    }

    public void alert1(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Not Available...");
        alertDialog.setMessage("This level is not available for you now! Pass the current level first!");
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        alertDialog.show();
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

    private void setImage(String s, int ratio, ImageView view, Bitmap bitmappointer){
        int res = getResources().getIdentifier(s, "drawable", getPackageName());
        view.setImageBitmap(bitmappointer = decodeSampledBitmapFromResource(getResources(), res, view.getWidth(), view.getHeight(), ratio));
        return;
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

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = 12;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public void goParty(View view){
        Intent intent = new Intent(this, MainActivity2Activity.class);
        recycleAll(1);
        startActivity(intent);
        finish();
        return;
    }

    public void goBattle(View view){
        writeBattleInfo();
        Intent intent = new Intent(this, Battle.class);
        recycleAll(2);
        startActivity(intent);
        finish();
        return;
    }

    public void turnoffLight(int clear){
        // Set the backgroudn as black
        RelativeLayout RL = (RelativeLayout) findViewById(R.id.RL4);
        RL.setBackgroundResource(android.R.color.black);
        // Set the loading text
        TextView TV2 = (TextView) findViewById(R.id.textView30);
        if(clear==1)
            TV2.setText("Go to the party page...");
        else if(clear==2)
            TV2.setText("Go to the battle page...");
        TV2.setTextColor(0xFFFFFFFF);
        // Reset the images to transparent first
        ImageView IBE = (ImageView) findViewById(R.id.imageViewEnvironment);
        IBE.setImageResource(android.R.color.transparent);
        ImageView IBcurr = (ImageView) findViewById(R.id.imageViewCurr);
        IBcurr.setImageResource(android.R.color.transparent);
        ImageView IBpro = (ImageView) findViewById(R.id.imageViewPro);
        IBpro.setImageResource(android.R.color.transparent);
        ImageView IBpost = (ImageView) findViewById(R.id.imageViewPost);
        IBpost.setImageResource(android.R.color.transparent);
        for(int i=0; i<16; i++){
            String viewName = "imageViewParty"+(i+1);
            ImageView IBsub = (ImageView) findViewById(getResources().getIdentifier(viewName,"id",getPackageName()));
            IBsub.setImageResource(android.R.color.transparent);
        }
        for(int i=0; i<5; i++){
            String viewName = "imageViewMember"+(i+1);
            ImageView IBsub = (ImageView) findViewById(getResources().getIdentifier(viewName,"id",getPackageName()));
            IBsub.setImageResource(android.R.color.transparent);
        }

        TextView TVEn = (TextView) findViewById(R.id.textViewEnviron);
        TVEn.setVisibility(View.GONE);
        Button B12 = (Button) findViewById(R.id.button12);
        B12.setVisibility(View.GONE);
        Button B13 = (Button) findViewById(R.id.button13);
        B13.setVisibility(View.GONE);
        Button B14 = (Button) findViewById(R.id.button14);
        B14.setVisibility(View.INVISIBLE);
        TextView Line1 = (TextView) findViewById(R.id.ShortLine1);
        Line1.setVisibility(View.INVISIBLE);
        TextView Line2 = (TextView) findViewById(R.id.ShortLine2);
        Line2.setVisibility(View.INVISIBLE);
        TextView Line3 = (TextView) findViewById(R.id.ShortLine3);
        Line3.setVisibility(View.INVISIBLE);
        TextView Line4 = (TextView) findViewById(R.id.ShortLine4);
        Line4.setVisibility(View.INVISIBLE);

        LinearLayout F33 = (LinearLayout) findViewById(R.id.f33);
        RelativeLayout RLsub = (RelativeLayout) findViewById(R.id.RLsub);
        RLsub.setBackgroundResource(android.R.color.black);
        RLsub.removeAllViews();
        F33.removeView(RLsub);
        LinearLayout LLsub = (LinearLayout) findViewById(R.id.LLsub);
        LLsub.removeAllViews();
        F33.removeView(LLsub);
        return;
    }

    public void recycleAll (int clear) {
        turnoffLight(clear);
        // Recycle the Bitmaps
        if(bitmapEnviron !=null && !bitmapEnviron.isRecycled()){
            bitmapEnviron.recycle();
            bitmapEnviron = null;
        }
        if(bitmap1 !=null && !bitmap1.isRecycled()){
            bitmap1.recycle();
            bitmap1 = null;
        }
        if(bitmap2 !=null && !bitmap2.isRecycled()){
            bitmap2.recycle();
            bitmap2 = null;
        }
        if(bitmap3 !=null && !bitmap3.isRecycled()){
            bitmap3.recycle();
            bitmap3 = null;
        }
        for(int i=0; i<16; i++) {
            if (bitmapParty[i] != null && !bitmapParty[i].isRecycled()) {
                bitmapParty[i].recycle();
                bitmapParty[i] = null;
            }
        }
        for(int i=0; i<5; i++) {
            if (bitmapSelect[i] != null && !bitmapSelect[i].isRecycled()) {
                bitmapSelect[i].recycle();
                bitmapSelect[i] = null;
            }
        }
        System.gc();
        return;
    }

    public void Select1(View view){
        if(currBattleNum>=5 ) return;
        int id = view.getId();
        int index = 0;
        switch (id){
            case R.id.imageViewParty1: index = 0; break;
            case R.id.imageViewParty2: index = 1; break;
            case R.id.imageViewParty3: index = 2; break;
            case R.id.imageViewParty4: index = 3; break;
            case R.id.imageViewParty5: index = 4; break;
            case R.id.imageViewParty6: index = 5; break;
            case R.id.imageViewParty7: index = 6; break;
            case R.id.imageViewParty8: index = 7; break;
            case R.id.imageViewParty9: index = 8; break;
            case R.id.imageViewParty10: index = 9; break;
            case R.id.imageViewParty11: index = 10; break;
            case R.id.imageViewParty12: index = 11; break;
            case R.id.imageViewParty13: index = 12; break;
            case R.id.imageViewParty14: index = 13; break;
            case R.id.imageViewParty15: index = 14; break;
            case R.id.imageViewParty16: index = 15; break;
        }
        if(FitList[index]<0) return;

        int target_index = 0;
        for(int i=0; i<5; i++){
            String TextName = "textViewDes" + (i+1);
            TextView TV = (TextView) findViewById(getResources().getIdentifier(TextName,"id",getPackageName()));
            if(TV.getText().equals("")){
                target_index = i;
                break;
            }
        }

        String ViewName = "imageViewMember" + (target_index+1);
        String TextName = "textViewDes" + (target_index+1);
        String AnimalName = TeamSpecie[FitList[index]].latin;
        ImageView IV = (ImageView) findViewById(getResources().getIdentifier(ViewName,"id",getPackageName()));
        setImage(AnimalName.toLowerCase(), 6, IV, bitmapParty[currBattleNum]);
        TextView TV = (TextView) findViewById(getResources().getIdentifier(TextName,"id",getPackageName()));
        TV.setText(Latin2English(AnimalName));
        currBattleNum++;
        return;
    }

    public void Delete1(View view){
        if(currBattleNum<=0) return;
        int id = view.getId();
        int index = 0;
        switch (id){
            case R.id.imageViewMember1: index = 0; break;
            case R.id.imageViewMember2: index = 1; break;
            case R.id.imageViewMember3: index = 2; break;
            case R.id.imageViewMember4: index = 3; break;
            case R.id.imageViewMember5: index = 4; break;
        }

        String TextName = "textViewDes" + (index+1);
        TextView TV = (TextView) findViewById(getResources().getIdentifier(TextName,"id",getPackageName()));
        if(TV.getText().equals("") || TV.getText().equals("Nothing")) return;
        TV.setText("");

        String ViewName = "imageViewMember" + (index+1);
        ImageView IV = (ImageView) findViewById(getResources().getIdentifier(ViewName,"id",getPackageName()));
        setImage("nothing", 6, IV, bitmapParty[index]);

        currBattleNum--;
        return;
    }

    public void writeBattleInfo(){
        try {
            OutputStreamWriter fos = new OutputStreamWriter(openFileOutput("battleinformation", Context.MODE_PRIVATE));
            fos.write(Integer.toString(DNA)); fos.write('\n');
            fos.write(Integer.toString(currLevel)); fos.write('\n');
            for(int i=0; i<5; i++) {
                String TextName = "textViewDes" + (i+1);
                TextView TV = (TextView) findViewById(getResources().getIdentifier(TextName,"id",getPackageName()));
                String s = English2Latin(TV.getText().toString());
                fos.write(s);fos.write('\n'); // Set Animal Latin Name
            }
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }

}
