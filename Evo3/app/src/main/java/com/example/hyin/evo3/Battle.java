package com.example.hyin.evo3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Battle extends ActionBarActivity {

    public String English_Name[] = new String[500];
    public String Latin_Name[] = new String[500];
    public String Ability_Name[] = new String[200];
    public String Ability_Des[] = new String[200];

    public Specie MySpecie[] = new Specie[5];
    public Specie AISpecie[] = new Specie[5];

    public Bitmap MainBitmap = null, BitmapEnviron = null;
    public Bitmap[] MyBitmap = new Bitmap[5], AIBitmap = new Bitmap[5];

    public int DNA, HpLength=0;
    public int MyHP[] = new int[5], AIHP[] = new int[5];

    public String BattleRecord[] = new String[100];
    public int currAnimal=0;

    public class Specie {
        String latin; // record the latin name
        String english; // record the english name
        int Evo_level; // record the evo level in the evo tree. Cyanobacteria has the level 0
        int Attack, Defence;
        String Size, Diet, Food[] = new String[9], Environ[] = new String[9], Ability[] = new String[5], Label[] = new String[5];
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
            for(int i=0; i<5; i++) {Ability[i]=""; Label[i]="";}
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

    public void readAbilityFile() {
        try {
            InputStream in = getResources().openRawResource(R.raw.listofability);
            BufferedReader dataIO = new BufferedReader(new InputStreamReader(in));
            String s_temp;
            for(int i=0; i<200; i++) {Ability_Name[i]=""; Ability_Des[i]="";}
            int cnt=0;
            while ((s_temp = dataIO.readLine()) != null) {
                if (s_temp.equals("---------------")) {
                    Ability_Name[cnt] = dataIO.readLine();
                    Ability_Des[cnt] = dataIO.readLine();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Darwin's Tree");
        setContentView(R.layout.activity_battle);
        readLatinEnglishFile();
        readAbilityFile();
        for(int i = 0; i<5; i++) {MySpecie[i]=new Specie(); AISpecie[i] = new Specie();}
        initialSet(); // Contains Refresh

        final TextView TVHp = (TextView) findViewById(R.id.HPMy1);
        final ViewTreeObserver vto = TVHp.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Put your code here.
                TVHp.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                HpLength = TVHp.getMeasuredHeight();
            }
        });

    }

    public void initialSet(){
        for(int i=0; i<100; i++) {survivals[i] = new SurvivalLevel();}
        for(int i=0; i<5; i++) {MyBitmap[i] = null; AIBitmap[i] = null;}
        for(int i=0; i<5; i++) {MyHP[i]=0; AIHP[i]=0;}
        for(int i=0; i<100; i++) {BattleRecord[i] = "";}
        MainBitmap = null; BitmapEnviron = null;

        ReadBattle();

        return;
    }

    public void ReadBattle(){

        // Read Environment Level and Player's Team

        try {
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(
                    openFileInput("battleinformation")));
            String temp = (inputReader.readLine());
            DNA = Integer.parseInt(temp);
            temp = (inputReader.readLine());
            currLevel = Integer.parseInt(temp);
            for(int i=0; i<5; i++) {
                String animal = (inputReader.readLine());
                if(animal.equals("Nothing") || animal.equals("")) {continue;}

                try {
                    InputStream in = getResources().getAssets().open(animal.toLowerCase()+".txt");
                    BufferedReader dataIO = new BufferedReader(new InputStreamReader(in));
                    String s_temp;
                    int cnt=0;
                    while ((s_temp = dataIO.readLine()) != null) {
                        if(cnt==0) {MySpecie[i].latin = s_temp; MySpecie[i].english = Latin2English(s_temp);}
                        else if(cnt==2) {MySpecie[i].Attack = Integer.parseInt(s_temp); }
                        else if(cnt==3) {MySpecie[i].Defence = Integer.parseInt(s_temp);}
                        else if(cnt==4) {MySpecie[i].Size = s_temp;}
                        else if(cnt==5) {MySpecie[i].Diet = s_temp;}
                        else if(cnt<15 && (s_temp.equals("Ocean")||s_temp.equals("Seashore")||s_temp.equals("Wetland")||s_temp.equals("Forest")||s_temp.equals("Jungle")||s_temp.equals("Grassland")||s_temp.equals("Tundra")||s_temp.equals("Mountain")||s_temp.equals("Desert")))
                        {MySpecie[i].Environ[cnt-6]=s_temp;}
                        else if(cnt<20 && cnt>=15 && (!s_temp.equals("No")))
                        {MySpecie[i].Ability[cnt-15]= s_temp;}
                        else if(cnt<25 && cnt>=20 && (!s_temp.equals("X")))
                        {MySpecie[i].Label[cnt-20]= s_temp;}
                        else if(cnt>=25 && (!s_temp.equals("O")))
                        {MySpecie[i].Label[cnt-25] = s_temp;}
                        cnt++;
                    }
                    dataIO.close();
                    in.close();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        // Read Environment

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

        // Read AI's team

        for(int i=0; i<5; i++){
            String animal = survivals[currLevel].Animals[i];
            try {
                InputStream in = getResources().getAssets().open(animal.toLowerCase()+".txt");
                BufferedReader dataIO = new BufferedReader(new InputStreamReader(in));
                String s_temp;
                int cnt=0;
                while ((s_temp = dataIO.readLine()) != null) {
                    if(cnt==0) {AISpecie[i].latin = s_temp; AISpecie[i].english = Latin2English(s_temp);}
                    else if(cnt==2) {AISpecie[i].Attack = Integer.parseInt(s_temp); }
                    else if(cnt==3) {AISpecie[i].Defence = Integer.parseInt(s_temp);}
                    else if(cnt==4) {AISpecie[i].Size = s_temp;}
                    else if(cnt==5) {AISpecie[i].Diet = s_temp;}
                    else if(cnt<15 && (s_temp.equals("Ocean")||s_temp.equals("Seashore")||s_temp.equals("Wetland")||s_temp.equals("Forest")||s_temp.equals("Jungle")||s_temp.equals("Grassland")||s_temp.equals("Tundra")||s_temp.equals("Mountain")||s_temp.equals("Desert")))
                    {AISpecie[i].Environ[cnt-6]=s_temp;}
                    else if(cnt<20 && cnt>=15 && (!s_temp.equals("No")))
                    {AISpecie[i].Ability[cnt-15]= s_temp;}
                    else if(cnt<25 && cnt>=20 && (!s_temp.equals("X")))
                    {AISpecie[i].Label[cnt-20]= s_temp;}
                    else if(cnt>=25 && (!s_temp.equals("O")))
                    {AISpecie[i].Label[cnt-25] = s_temp;}
                    cnt++;
                }
                dataIO.close();
                in.close();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }

        // Set the background Image
        TextView TV1 = (TextView) findViewById(R.id.textViewLevel);
        TV1.setText("Level " + survivals[currLevel].levelNum + ": " + survivals[currLevel].Name + "!");
        ImageView IVBackground = (ImageView) findViewById(R.id.imageViewEnvironment1);
        setImage(survivals[currLevel].Environ.toLowerCase(), 4, IVBackground, BitmapEnviron);

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
        RelativeLayout RL5 = (RelativeLayout) findViewById(R.id.RL5);
        RL5.setBackgroundColor(Color.parseColor(ColorBack));
        //LinearLayout LL15 = (LinearLayout) findViewById(R.id.LL15);
        //LL15.setBackgroundColor(Color.parseColor(Color1));
        //ImageView IV = (ImageView) findViewById(R.id.imageAnimal);
        //IV.setBackgroundColor(Color.parseColor(Color1));
        //ScrollView SV = (ScrollView) findViewById(R.id.scrollView);
        //SV.setBackgroundColor(Color.parseColor(Color1));
        // Refresh the animal's image and HPs
        refresh();

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

    public void refresh() {

        for(int i=0; i<5; i++){
            String HPName = "HPMy" + (i+1);
            TextView TV = (TextView) findViewById(getResources().getIdentifier(HPName,"id",getPackageName()));
            if(MySpecie[i].latin.equals("Nothing")||MySpecie[i].latin.equals("")) {
                MyHP[i] = 0;
                TV.getLayoutParams().width = 0;
                continue;
            }
            else {
                MyHP[i] = 100;
            }
            String ViewName = "imageViewMy" + (i+1);
            ImageView IV = (ImageView) findViewById(getResources().getIdentifier(ViewName,"id",getPackageName()));
            setImage(MySpecie[i].latin.toLowerCase(),4,IV,MyBitmap[i]);
        }

        for(int i=0; i<5; i++){
            String HPName = "HPAI" + (i+1);
            TextView TV = (TextView) findViewById(getResources().getIdentifier(HPName,"id",getPackageName()));
            if(AISpecie[i].latin.equals("Nothing")||AISpecie[i].latin.equals("")) {
                AIHP[i] = 0;
                TV.getLayoutParams().width = 0;
                continue;
            }
            else {
                AIHP[i] = 100;
            }
            String ViewName = "imageViewAI" + (i+1);
            ImageView IV = (ImageView) findViewById(getResources().getIdentifier(ViewName,"id",getPackageName()));
            setImage(AISpecie[i].latin.toLowerCase(),4,IV,AIBitmap[i]);
        }

        for(int i=0; i<5; i++){
            if(MySpecie[i].latin.equals("") || MySpecie[i].latin.equals("Nothing")) continue;
            else currAnimal = i;
        }

        switch2Animal();

        return;
    }

    public void switch2Animal(){

        LinearLayout LL = (LinearLayout) findViewById(R.id.LL14);
        LL.setBackgroundColor(Color.parseColor("#ff050505"));
        ScrollView SV = (ScrollView) findViewById(R.id.scrollView);
        SV.setVisibility(View.GONE);
        ImageView IV = (ImageView) findViewById(R.id.imageAnimal);
        IV.setVisibility(View.VISIBLE);
        LinearLayout LL15 = (LinearLayout) findViewById(R.id.LL15);
        LL15.setVisibility(View.VISIBLE);

        setImage(MySpecie[currAnimal].latin.toLowerCase(), 2, IV, MainBitmap);

        TextView Name = (TextView) findViewById(R.id.textViewName);
        Name.setText(Latin2English(MySpecie[currAnimal].latin));

        TextView Data = (TextView) findViewById(R.id.textViewData);
        String CurrData = "Attack: "+MySpecie[currAnimal].Attack+ "   Defense: "+MySpecie[currAnimal].Defence + "   Size: "+MySpecie[currAnimal].Size+"\n" + MySpecie[currAnimal].Diet + "   Preys:  ";
        int cnt = 0;
        for(int i=0; i<9; i++) {
            String food = MySpecie[currAnimal].Food[i];
            if(food.equals("")||food.equals("O")||food.equals("Nothing")) break;
            CurrData = CurrData+food+"  ";
            cnt++;
        }
        if(cnt==0) CurrData = CurrData+"Nothing";
        Data.setText(CurrData);

        TextView Ability = (TextView) findViewById(R.id.textViewAbility);
        String CurrAbility = "Ability:   ";
        cnt = 0;
        for(int i=0; i<5; i++) {
            String ability = MySpecie[currAnimal].Ability[i];
            if(ability.equals("")||ability.equals("No")||ability.equals("Nothing")) break;
            CurrAbility = CurrAbility+ "[" + ability + "]  ";
            cnt++;
        }
        if(cnt==0) CurrAbility = CurrAbility+"No Ability";
        Ability.setText(CurrAbility);

        TextView Label = (TextView) findViewById(R.id.textViewLabel);
        String CurrLabel = "Labels:   ";
        cnt = 0;
        for(int i=0; i<5; i++) {
            String label = MySpecie[currAnimal].Label[i];
            if(label.equals("")||label.equals("X")||label.equals("Nothing")) break;
            CurrLabel = CurrLabel+"["+label+"]  ";
            cnt++;
        }
        if(cnt==0) CurrLabel = CurrLabel+"No Label";
        Label.setText(CurrLabel);

        // Highlight All Attackable Target
        for(int i=0; i<5; i++){
            String viewName = "imageViewAI"+(i+1);
            ImageView To = (ImageView) findViewById(getResources().getIdentifier(viewName,"id",getPackageName()));
            if(MyAttackable(currAnimal,i)){
                To.setBackgroundColor(Color.parseColor("#FFFF6104"));
            }
            else{
                To.setBackgroundColor(Color.parseColor("#00FFFFFF"));
            }
        }

        return;
    }

    public String DesOfAbility(String ability){
        for(int i=0; i<200; i++){
            if(ability.equals(Ability_Name[i])) {return Ability_Des[i];}
        }
        return "";
    }

    public void alert1(String[] abilities, int N){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Check Ability...");
        String Message = "";
        for(int i=0; i<N; i++) {
           Message = Message + abilities[i] + ": " + DesOfAbility(abilities[i]) + '\n';
        }
        alertDialog.setMessage(Message);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        alertDialog.show();
        return;
    }

    public void CheckAbility(View view){
        int cnt = 0;
        String abilities[] = new String[5];
        for(int i=0; i<5; i++) {
            if (MySpecie[currAnimal].Ability[i].equals("")||MySpecie[currAnimal].Ability[i].equals("No")){
                continue;
            }
            else abilities[i] = MySpecie[currAnimal].Ability[i];
        }
        if(cnt>0) {
            alert1(abilities, cnt);
        }
        return;
    }

    public void switch2Record(){

        LinearLayout LL = (LinearLayout) findViewById(R.id.LL14);
        LL.setBackgroundColor(Color.parseColor("#ffff1d06"));
        ScrollView SV = (ScrollView) findViewById(R.id.scrollView);
        SV.setVisibility(View.VISIBLE);
        ImageView IV = (ImageView) findViewById(R.id.imageAnimal);
        IV.setVisibility(View.GONE);
        LinearLayout LL15 = (LinearLayout) findViewById(R.id.LL15);
        LL15.setVisibility(View.GONE);

        TextView TV = (TextView) findViewById(R.id.textViewRecord);
        String content = "";
        for(int i=0; i<100; i++){
            if(BattleRecord[i]!="") {content = content+BattleRecord[i]+'\n';}
        }
        TV.setText(content);

        return;
    }

    public void click2Animal(View view){
        switch2Animal();
    }

    public void click2Record(View view){
        switch2Record();
    }

    public void switchCurr(View view){
        int id = view.getId();
        switch (id){
            case R.id.imageViewMy1: currAnimal = 0; break;
            case R.id.imageViewMy2: currAnimal = 1; break;
            case R.id.imageViewMy3: currAnimal = 2; break;
            case R.id.imageViewMy4: currAnimal = 3; break;
            case R.id.imageViewMy5: currAnimal = 4; break;
        }
        switch2Animal();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_battle, menu);
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

    public void goSurvival(View view){
        Intent intent = new Intent(this, Survival.class);
        recycleAll(1);
        startActivity(intent);
        finish();
        return;
    }

    public void recycleAll (int clear) {
        turnoffLight(clear);
        // Recycle the Bitmaps
        if(BitmapEnviron !=null && !BitmapEnviron.isRecycled()){
            BitmapEnviron.recycle();
            BitmapEnviron = null;
        }
        if(MainBitmap !=null && !MainBitmap.isRecycled()){
            MainBitmap.recycle();
            MainBitmap = null;
        }
        for(int i=0; i<5; i++) {
            if (MyBitmap[i] != null && !MyBitmap[i].isRecycled()) {
                MyBitmap[i].recycle();
                MyBitmap[i] = null;
            }
        }
        for(int i=0; i<5; i++) {
            if (AIBitmap[i] != null && !AIBitmap[i].isRecycled()) {
                AIBitmap[i].recycle();
                AIBitmap[i] = null;
            }
        }
        System.gc();
        return;
    }

    public void turnoffLight(int clear){
        // Set the backgroudn as black
        RelativeLayout RL = (RelativeLayout) findViewById(R.id.RL5);
        RL.setBackgroundResource(android.R.color.black);
        // Set the loading text
        TextView TV2 = (TextView) findViewById(R.id.textViewLevel);
        if(clear==1)
            TV2.setText("Go to the level selecting page...");
        TV2.setTextColor(0xFFFFFFFF);
        // Reset the images to transparent first
        ImageView IBE = (ImageView) findViewById(R.id.imageViewEnvironment1);
        IBE.setImageResource(android.R.color.transparent);
        ImageView IBcurr = (ImageView) findViewById(R.id.imageAnimal);
        IBcurr.setImageResource(android.R.color.transparent);
        for(int i=0; i<5; i++){
            String viewName = "imageViewMy"+(i+1);
            ImageView IBsub = (ImageView) findViewById(getResources().getIdentifier(viewName,"id",getPackageName()));
            IBsub.setImageResource(android.R.color.transparent);
        }
        for(int i=0; i<5; i++){
            String viewName = "imageViewAI"+(i+1);
            ImageView IBsub = (ImageView) findViewById(getResources().getIdentifier(viewName,"id",getPackageName()));
            IBsub.setImageResource(android.R.color.transparent);
        }

        TextView TVB = (TextView) findViewById(R.id.textViewBlack);
        TVB.setVisibility(View.GONE);
        TextView TVR = (TextView) findViewById(R.id.textViewRed);
        TVR.setVisibility(View.GONE);
        Button B20 = (Button) findViewById(R.id.button20);
        B20.setVisibility(View.GONE);
        Button B21 = (Button) findViewById(R.id.button21);
        B21.setVisibility(View.GONE);
        TextView HPMy1 = (TextView) findViewById(R.id.HPMy1);
        HPMy1.setVisibility(View.INVISIBLE);
        TextView HPMy2 = (TextView) findViewById(R.id.HPMy2);
        HPMy2.setVisibility(View.INVISIBLE);
        TextView HPMy3 = (TextView) findViewById(R.id.HPMy3);
        HPMy3.setVisibility(View.INVISIBLE);
        TextView HPMy4 = (TextView) findViewById(R.id.HPMy4);
        HPMy4.setVisibility(View.INVISIBLE);
        TextView HPMy5 = (TextView) findViewById(R.id.HPMy5);
        HPMy5.setVisibility(View.INVISIBLE);
        TextView HPAI1 = (TextView) findViewById(R.id.HPAI1);
        HPAI1.setVisibility(View.INVISIBLE);
        TextView HPAI2 = (TextView) findViewById(R.id.HPAI2);
        HPAI2.setVisibility(View.INVISIBLE);
        TextView HPAI3 = (TextView) findViewById(R.id.HPAI3);
        HPAI3.setVisibility(View.INVISIBLE);
        TextView HPAI4 = (TextView) findViewById(R.id.HPAI4);
        HPAI4.setVisibility(View.INVISIBLE);
        TextView HPAI5 = (TextView) findViewById(R.id.HPAI5);
        HPAI5.setVisibility(View.INVISIBLE);


        RelativeLayout RLsub = (RelativeLayout) findViewById(R.id.RL6);
        RLsub.setBackgroundResource(android.R.color.black);
        RLsub.removeAllViews();
        LinearLayout LL17 = (LinearLayout) findViewById(R.id.LL17);
        LL17.removeView(RLsub);
        LinearLayout LL13 = (LinearLayout) findViewById(R.id.LL13);
        LL13.removeAllViews();
        LL17.removeView(LL13);
        return;
    }

    public void MyHit (int from, int to, View fromView, View toView){
        LinearLayout LLsub1 = (LinearLayout) findViewById(R.id.LLsub1), LLsub2 = (LinearLayout) findViewById(R.id.LLsub2), LLsub3 = (LinearLayout) findViewById(R.id.LLsub3), LLsub4 = (LinearLayout) findViewById(R.id.LLsub4);
        LLsub1.setClipChildren(false); LLsub2.setClipChildren(false); LLsub3.setClipChildren(false); LLsub4.setClipChildren(false);
        int FromLLleft = 0, FromLLtop = 0, ToLLleft = 0, ToLLtop = 0;
        final Bitmap bitmapFrom=null, bitmapTo = null;

        switch (from){
            case 0:case 2: case 4: FromLLleft = LLsub1.getLeft(); FromLLtop = LLsub1.getTop();break;
            case 1:case 3: FromLLleft = LLsub2.getLeft(); FromLLtop =LLsub2.getTop();
        }
        switch (to){
            case 0:case 2: case 4: ToLLleft = LLsub4.getLeft(); ToLLtop = LLsub4.getTop();break;
            case 1:case 3: ToLLleft = LLsub3.getLeft(); ToLLtop =LLsub3.getTop();
        }

        setInvisible1();

        int X1 = fromView.getLeft()+FromLLleft, Y1 = fromView.getTop()+FromLLtop, X2 = toView.getLeft()+ToLLleft, Y2 = toView.getTop()+ToLLtop;

        final RelativeLayout RLback = (RelativeLayout) findViewById(R.id.RL6);
        final LinearLayout LLback = (LinearLayout) findViewById(R.id.LL11);
        final ImageView TempTo = new ImageView(this),  TempFrom = new ImageView(this);
        //LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        LLback.addView(TempTo); RLback.addView(TempFrom);
        //TempFrom.layout(X1, Y1, X1 + fromView.getWidth(), Y1 + fromView.getHeight()); TempTo.layout(X2, Y2, X2 + toView.getWidth(), Y2 + toView.getHeight());
        TempFrom.setVisibility(View.INVISIBLE); TempTo.setVisibility(View.INVISIBLE);
        TempFrom.setScaleType(ImageView.ScaleType.FIT_XY); TempTo.setScaleType(ImageView.ScaleType.FIT_XY);
        TempFrom.getLayoutParams().height = fromView.getHeight(); TempFrom.getLayoutParams().width = fromView.getWidth(); TempTo.getLayoutParams().height = toView.getHeight(); TempTo.getLayoutParams().width = toView.getWidth();
        setImage(MySpecie[from].latin.toLowerCase(), 2, TempFrom, bitmapFrom); setImage(AISpecie[to].latin.toLowerCase(), 2, TempTo, bitmapTo);

        final TranslateAnimation stay1 = new TranslateAnimation((float)X2,  (float)X2, (float)Y2, (float)Y2);
        stay1.setZAdjustment(Animation.ZORDER_BOTTOM);
        stay1.setDuration(2000);
        final TranslateAnimation goHit2 = new TranslateAnimation((float)X1,  (float)X2, (float)Y1, (float)Y2);
        goHit2.setDuration(1200);
        goHit2.setZAdjustment(Animation.ZORDER_TOP);
        final TranslateAnimation goBack2 = new TranslateAnimation((float)X2, (float)X1, (float)Y2, (float)Y1);
        goBack2.setDuration(800);
        goHit2.setZAdjustment(Animation.ZORDER_TOP);

        TempTo.startAnimation(stay1);

       goHit2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                TempFrom.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                TempFrom.startAnimation(goBack2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        stay1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                TempTo.setVisibility(View.VISIBLE);
                TempFrom.startAnimation(goHit2);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                TempTo.setVisibility(View.INVISIBLE);
                TempTo.setImageBitmap( null);
                LLback.removeView(TempTo);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        goBack2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                TempFrom.setVisibility(View.INVISIBLE);
                TempFrom.setImageBitmap(null);
                RLback.removeView(TempFrom);
                setVisible1();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        return;
    }

    public void setInvisible1() {
        LinearLayout LLsub1 = (LinearLayout) findViewById(R.id.LLsub1), LLsub2 = (LinearLayout) findViewById(R.id.LLsub2), LLsub3 = (LinearLayout) findViewById(R.id.LLsub3), LLsub4 = (LinearLayout) findViewById(R.id.LLsub4), LLsub5 = (LinearLayout)findViewById(R.id.LLsub5);
        LLsub1.setVisibility(View.GONE);
        LLsub2.setVisibility(View.GONE);
        LLsub3.setVisibility(View.GONE);
        LLsub4.setVisibility(View.GONE);
        LLsub5.setVisibility(View.GONE);
    }

    public void setVisible1() {
        LinearLayout LLsub1 = (LinearLayout) findViewById(R.id.LLsub1), LLsub2 = (LinearLayout) findViewById(R.id.LLsub2), LLsub3 = (LinearLayout) findViewById(R.id.LLsub3), LLsub4 = (LinearLayout) findViewById(R.id.LLsub4), LLsub5 = (LinearLayout)findViewById(R.id.LLsub5);
        LLsub1.setVisibility(View.VISIBLE);
        LLsub2.setVisibility(View.VISIBLE);
        LLsub3.setVisibility(View.VISIBLE);
        LLsub4.setVisibility(View.VISIBLE);
        LLsub5.setVisibility(View.VISIBLE);
    }

    public void alert2(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Failure Attack...");
        alertDialog.setMessage("The target animal you selected won't be affected by your attack! Change another target");
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        alertDialog.show();
        return;
    }

    /*public void alert3(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Success Attack...");
        alertDialog.setMessage("You made damage/affect to the target animal!");
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                setVisible1();

                return;
            }
        });
        alertDialog.show();
        return;
    }*/

    public void goAttack(View view){

        int targetIndex = 0;
        switch (view.getId()){
            case R.id.imageViewAI1: targetIndex=0; break;
            case R.id.imageViewAI2: targetIndex=1; break;
            case R.id.imageViewAI3: targetIndex=2; break;
            case R.id.imageViewAI4: targetIndex=3; break;
            case R.id.imageViewAI5: targetIndex=4; break;
        }

        // Judge if OK
        if(!MyAttackable(currAnimal,targetIndex)) {
            alert2();
            return;
        }

        String viewName = "imageViewMy"+(currAnimal+1);
        ImageView IV = (ImageView) findViewById(getResources().getIdentifier(viewName,"id",getPackageName()));

        // Animation
        MyHit(currAnimal, targetIndex, IV, view);

        return;

    }

    public boolean MyAttackable(int from, int to ){
        if(MySpecie[from].Diet.equals("Parasitic")){
            for(int i=0; i<9; i++){
                if(MySpecie[from].Food[i].equals("") || MySpecie[from].Food[i].equals("O") || MySpecie[from].Food[i].equals("Nothing")) {continue;}
                for(int j=0; j<5; j++) {
                    if(MySpecie[from].Food[i].equals(AISpecie[to].Label[j]))
                        return true;
                }
            }
            return false;
        }
        boolean Sniper = false;
        for(int i=0; i<5; i++) {if(MySpecie[from].Ability[i].equals("Sniper")) {Sniper=true; break;}}
        if(Sniper) return AttackableTableSniper(MySpecie[from].Size,AISpecie[to].Size);
        return AttackableTable(MySpecie[from].Size,AISpecie[to].Size);
    }

    public boolean AIAttackable(int from, int to ){
        if(AISpecie[from].Diet.equals("Parasitic")){
            for(int i=0; i<9; i++){
                if(AISpecie[from].Food[i].equals("") || AISpecie[from].Food[i].equals("O") || AISpecie[from].Food[i].equals("Nothing")) {continue;}
                for(int j=0; j<5; j++) {
                    if(AISpecie[from].Food[i].equals(MySpecie[to].Label[j]))
                        return true;
                }
            }
            return false;
        }
        boolean Sniper = false;
        for(int i=0; i<5; i++) {if(AISpecie[from].Ability[i].equals("Sniper")) {Sniper=true; break;}}
        if(Sniper) return AttackableTableSniper(AISpecie[from].Size,MySpecie[to].Size);
        return AttackableTable(AISpecie[from].Size, MySpecie[to].Size);
    }

    public boolean AttackableTable(String sizeFrom, String sizeTo){
        switch(sizeFrom) {
            case "Micro": {
                if (sizeTo.equals("Micro") || sizeTo.equals("XSmall")) return true;
                else return false;
            }
            case "XSmall": {
                if (sizeTo.equals("Micro") || sizeTo.equals("XSmall") || sizeTo.equals("Small"))
                    return true;
                else return false;
            }
            case "Small": {
                if (sizeTo.equals("Small+") || sizeTo.equals("XSmall") || sizeTo.equals("Small"))
                    return true;
                else return false;
            }
            case "Small+": {
                if (sizeTo.equals("Small") || sizeTo.equals("Small+") || sizeTo.equals("Medium"))
                    return true;
                else return false;
            }
            case "Medium": {
                if (sizeTo.equals("Small+") || sizeTo.equals("Medium+") || sizeTo.equals("Medium"))
                    return true;
                else return false;
            }
            case "Medium+": {
                if (sizeTo.equals("Medium") || sizeTo.equals("Medium+") || sizeTo.equals("Large"))
                    return true;
                else return false;
            }
            case "Large": {
                if (sizeTo.equals("Medium+") || sizeTo.equals("Large") || sizeTo.equals("XLarge"))
                    return true;
                else return false;
            }
            case "XLarge": {
                if (sizeTo.equals("Large") || sizeTo.equals("XLarge") || sizeTo.equals("Huge"))
                    return true;
                else return false;
            }
            case "Huge": {
                if (sizeTo.equals("Huge") || sizeTo.equals("XLarge"))
                    return true;
                else return false;
            }
        }
        return false;
    }

    public boolean AttackableTableSniper(String sizeFrom, String sizeTo){
        switch(sizeFrom) {
            case "Micro": {
                if (sizeTo.equals("Micro")) return true;
                else return false;
            }
            case "XSmall": {
                if (sizeTo.equals("Micro") || sizeTo.equals("XSmall"))
                    return true;
                else return false;
            }
            case "Small": {
                if (sizeTo.equals("Micro") || sizeTo.equals("XSmall") || sizeTo.equals("Small"))
                    return true;
                else return false;
            }
            case "Small+": {
                if (sizeTo.equals("Small") || sizeTo.equals("Small+") || sizeTo.equals("XSmall"))
                    return true;
                else return false;
            }
            case "Medium": {
                if (sizeTo.equals("Small+") || sizeTo.equals("Medium") || sizeTo.equals("Small"))
                    return true;
                else return false;
            }
            case "Medium+": {
                if (sizeTo.equals("Medium") || sizeTo.equals("Medium+") || sizeTo.equals("Small+"))
                    return true;
                else return false;
            }
            case "Large": {
                if (sizeTo.equals("Medium+") || sizeTo.equals("Large") || sizeTo.equals("Medium"))
                    return true;
                else return false;
            }
            case "XLarge": {
                if (sizeTo.equals("Large") || sizeTo.equals("XLarge") || sizeTo.equals("Medium+"))
                    return true;
                else return false;
            }
            case "Huge": {
                if (sizeTo.equals("Huge") || sizeTo.equals("XLarge") || sizeTo.equals("Large"))
                    return true;
                else return false;
            }
        }
        return false;
    }

}
