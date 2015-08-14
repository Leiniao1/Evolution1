package com.example.hyin.evo3;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Size;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class Battle extends ActionBarActivity {

    public String English_Name[] = new String[800];
    public String Latin_Name[] = new String[800];
    public String Ability_Name[] = new String[200];
    public String Ability_Des[] = new String[200];
    public String Result = "Unknown"; // Can be "Win", "Lose", "Tie" Also

    public Specie MySpecie[] = new Specie[5];
    public Specie AISpecie[] = new Specie[5];

    public Bitmap MainBitmap = null, BitmapEnviron = null;
    public Bitmap[] MyBitmap = new Bitmap[5], AIBitmap = new Bitmap[5];

    public int DNA, HpLength=0;
    public int MyHP[] = new int[5], AIHP[] = new int[5];

    public String BattleRecord[] = new String[100];
    public int currAnimal=0, turn =0;

    public Status MyStatus[]= new Status[5], AIStatus[] = new Status[5];

    public boolean AchievementRecord[] = new boolean[200];

    public class Specie {
        String latin; // record the latin name
        String english; // record the english name
        int Evo_level; // record the evo level in the evo tree. Cyanobacteria has the level 0
        int Attack, Defence;
        String Size, Diet, Food[] = new String[9], Environ[] = new String[9], Ability[] = new String[5], Label[] = new String[5];
        boolean available;
        String MimicTarget;
        Specie(){
            latin = "Nothing";
            english = "Nothing";
            Evo_level = 0;
            Attack = 0;
            Defence = 0;
            Size = "";
            Diet = "";
            for(int i=0; i<9; i++) {Food[i]=""; Environ[i]="";}
            for(int i=0; i<5; i++) {Ability[i]=""; Label[i]="";}
            available = false;
            MimicTarget = "";
        }
    }

    public class Status {
        int HP_up;
        boolean Toxic;
        boolean poisonous;
        int poisonous_down;
        boolean Be_leeched[] = new boolean[5];
        int HP_leeched[] = new int[5];
        int To_leech;
        int To_leech_HP;
        boolean hide, unaccessible;
        Status(){
            HP_up = 0;
            Toxic = false;
            poisonous = false;
            poisonous_down = 0;
            for(int i=0; i<5; i++) {Be_leeched[i]=false;HP_leeched[i]=0;}
            To_leech = -1;
            To_leech_HP = 0;
            hide = false;
            unaccessible = false;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setTitle("Darwin's Tree");
        setContentView(R.layout.activity_battle);
        readLatinEnglishFile();
        readAbilityFile();
        readSurvivalInfo();
        for(int i = 0; i<5; i++) {MySpecie[i]=new Specie(); AISpecie[i] = new Specie();}
        initialSet(); // Contains Refresh
        achievement_check();

        final TextView TVHp = (TextView) findViewById(R.id.HPMy1);
        final ViewTreeObserver vto = TVHp.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Put your code here.
                TVHp.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                HpLength = TVHp.getMeasuredWidth();
            }
        });

        AnimalInitialSet();
    }

    @Override
    public void onBackPressed(){

        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            if(Result.equals("Unknown"))    alertBack();
            if(Result.equals("Win"))  {writeSurvivalRecord(); readAndWriteDNA(survivals[currLevel].Prize);goSurvival1();}
            if(Result.equals("Lose")) {goSurvival1();}
        } else {
            getFragmentManager().popBackStack();
        }

        return;
    }

    public void AnimalInitialSet(){
        for(int i=0; i<5; i++){
            for(int j=0; j<5; j++){
                if(MySpecie[i].Ability[j].equals("Camouflage")){
                    MyStatus[i].hide = true;
                }
                if(AISpecie[i].Ability[j].equals("Camouflage")){
                    AIStatus[i].hide = true;
                    String HPName = "HPAI" + (i+1);
                    TextView TV = (TextView) findViewById(getResources().getIdentifier(HPName,"id",getPackageName()));
                    TV.setVisibility(View.INVISIBLE);
                    String ViewName = "imageViewAI" + (i+1);
                    ImageView IV = (ImageView) findViewById(getResources().getIdentifier(ViewName,"id",getPackageName()));
                    IV.setVisibility(View.INVISIBLE);
                }
            }
        }

        HashSet<String> ShellAbilities = new HashSet<String>();
        HashSet<String> CoralAbilities = new HashSet<String>();
        for(int i=0; i<5; i++){
            for(int j=0; j<5; j++){
                if(MySpecie[i].Label[j].equals("Shell")){
                    for(int k=0; k<5; k++){
                        if(MySpecie[i].Ability[k].equals("Shell Collector")||MySpecie[i].Ability[k].equals("Coral Collector")) continue;
                        ShellAbilities.add(MySpecie[i].Ability[k]);
                    }
                }
                if(AISpecie[i].Label[j].equals("Shell")){
                    for(int k=0; k<5; k++){
                        if(AISpecie[i].Ability[k].equals("Shell Collector")||AISpecie[i].Ability[k].equals("Coral Collector")) continue;
                        ShellAbilities.add(AISpecie[i].Ability[k]);
                    }
                }
                if(MySpecie[i].Label[j].equals("Coral")){
                    for(int k=0; k<5; k++){
                        if(MySpecie[i].Ability[k].equals("Coral Collector")||MySpecie[i].Ability[k].equals("Shell Collector")) continue;
                        CoralAbilities.add(MySpecie[i].Ability[k]);
                    }
                }
                if(AISpecie[i].Label[j].equals("Coral")){
                    for(int k=0; k<5; k++){
                        if(AISpecie[i].Ability[k].equals("Coral Collector")||AISpecie[i].Ability[k].equals("Shell Collector")) continue;
                        CoralAbilities.add(AISpecie[i].Ability[k]);
                    }
                }
            }
        }

        String Shells[] = ShellAbilities.toArray(new String[ShellAbilities.size()]);
        String Corals[] = CoralAbilities.toArray(new String[CoralAbilities.size()]);

        for(int i=0; i<5; i++){
            for(int j=0; j<5; j++){
                if(MySpecie[i].Ability[j].equals("Shell Collector")){
                    for(int k=j; k<5 && k<Shells.length; k++){
                        MySpecie[i].Ability[k] = Shells[k];
                    }
                }
                if(MySpecie[i].Ability[j].equals("Coral Collector")){
                    for(int k=j; k<5 && k<Corals.length; k++){
                        MySpecie[i].Ability[k] = Corals[k];
                    }
                }
                if(AISpecie[i].Ability[j].equals("Shell Collector")){
                    for(int k=j; k<5 && k<Shells.length; k++){
                        AISpecie[i].Ability[k] = Shells[k];
                    }
                }
                if(AISpecie[i].Ability[j].equals("Coral Collector")){
                    for(int k=j; k<5 && k<Corals.length; k++){
                        AISpecie[i].Ability[k] = Corals[k];
                    }
                }
            }
        }

        for(int i=0; i<5; i++){
            for(int j=0; j<5; j++){
                if(MySpecie[i].Ability[j].equals("Owl Mimicry")){
                    // TODO: Do something
                }
                else if(MySpecie[i].Ability[j].equals("Snake Mimicry")){
                    // TODO: Do something
                }
                else if(MySpecie[i].Ability[j].equals("Ant Mimicry")){
                    Random rd = new Random();
                    int next = rd.nextInt()%5;
                    switch(next) {
                        case 0: MySpecie[i].MimicTarget = "Camponotus";break;
                        case 1: MySpecie[i].MimicTarget = "Iridomyrmex";break;
                        case 2: MySpecie[i].MimicTarget = "Oecophylla";break;
                        case 3: MySpecie[i].MimicTarget = "Eciton";break;
                        case 4: MySpecie[i].MimicTarget = "Solenopsis";break;
                    }
                }
                else if(MySpecie[i].Ability[j].equals("Bee Mimicry")){
                    Random rd = new Random();
                    int next = rd.nextInt()%5;
                    switch(next) {
                        case 0: MySpecie[i].MimicTarget = "Apis";break;
                        case 1: MySpecie[i].MimicTarget = "Bumbus";break;
                        case 2: MySpecie[i].MimicTarget = "Andrena";break;
                        case 3: MySpecie[i].MimicTarget = "Euglossa";break;
                        case 4: MySpecie[i].MimicTarget = "Amegilla";break;
                    }
                }
                else if(MySpecie[i].Ability[j].equals("Vary Mimicry")){
                    ArrayList<String> targets = new ArrayList<String>();
                    for(int k=0; k<5; k++){
                        if(MySpecie[i].Size==MySpecie[k].Size && i!=k){
                            targets.add(MySpecie[k].latin);
                        }
                        if(MySpecie[i].Size==AISpecie[k].Size){
                            targets.add(AISpecie[k].latin);
                        }
                    }
                    Random rd = new Random();
                    int next = rd.nextInt()%targets.size();
                    MySpecie[i].MimicTarget = targets.get(next);
                }

                if(AISpecie[i].Ability[j].equals("Owl Mimicry")){
                    // TODO: Do something
                }
                else if(AISpecie[i].Ability[j].equals("Snake Mimicry")){
                    // TODO: Do something
                }
                else if(AISpecie[i].Ability[j].equals("Ant Mimicry")){
                    Random rd = new Random();
                    int next = rd.nextInt()%5;
                    switch(next) {
                        case 0: AISpecie[i].MimicTarget = "Camponotus";break;
                        case 1: AISpecie[i].MimicTarget = "Iridomyrmex";break;
                        case 2: AISpecie[i].MimicTarget = "Oecophylla";break;
                        case 3: AISpecie[i].MimicTarget = "Eciton";break;
                        case 4: AISpecie[i].MimicTarget = "Solenopsis";break;
                    }
                }
                else if(AISpecie[i].Ability[j].equals("Bee Mimicry")){
                    Random rd = new Random();
                    int next = rd.nextInt()%5;
                    switch(next) {
                        case 0: AISpecie[i].MimicTarget = "Apis";break;
                        case 1: AISpecie[i].MimicTarget = "Bumbus";break;
                        case 2: AISpecie[i].MimicTarget = "Andrena";break;
                        case 3: AISpecie[i].MimicTarget = "Euglossa";break;
                        case 4: AISpecie[i].MimicTarget = "Amegilla";break;
                    }
                }
                else if(AISpecie[i].Ability[j].equals("Vary Mimicry")){
                    ArrayList<String> targets = new ArrayList<String>();
                    for(int k=0; k<5; k++){
                        if(AISpecie[i].Size==AISpecie[k].Size && i!=k){
                            targets.add(AISpecie[k].latin);
                        }
                        if(AISpecie[i].Size==MySpecie[k].Size){
                            targets.add(MySpecie[k].latin);
                        }
                    }
                    Random rd = new Random();
                    int next = rd.nextInt()%targets.size();
                    AISpecie[i].MimicTarget = targets.get(next);
                }
            }
        }

        return;
    }

    public void readSurvivalInfo() {
        try {
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(
                    openFileInput("survivalinformation")));
            String temp = (inputReader.readLine());
            currLevel = Integer.parseInt(temp);
            for (int i = 0; i < 100; i++) {
                temp = (inputReader.readLine());
                if (temp.equals("No")) {
                    SurvivalRecord[i] = false;
                } else {
                    SurvivalRecord[i] = true;
                }
            }
            inputReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void achievement_check(){
        ReadAchievementRecord();
        if(AchievementRecord[4]==false){
            AchievementRecord[4]=true;
            AchievementAlert(4);
        }
        if(AchievementRecord[9]==false && currLevel>=5){
            AchievementRecord[9] = true;
            AchievementAlert(9);
        }
        if(AchievementRecord[13]==false && currLevel>=10){
            AchievementRecord[13] = true;
            AchievementAlert(13);
        }
        if(AchievementRecord[18]==false && currLevel>=25){
            AchievementRecord[18] = true;
            AchievementAlert(18);
        }
        WriteAchievementRecord();
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
            inputReader.close();
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

        readAndWriteDNA(prize);

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialogachievement);
        dialog.setTitle("You Got A Prize...");

        TextView text = (TextView) dialog.findViewById(R.id.text);
        text.setText("You completed the achievement: " + title + " You get " + prize + " DNA as prize");
        ImageView image = (ImageView) dialog.findViewById(R.id.image);
        int res = getResources().getIdentifier(icon,"drawable", getPackageName());
        image.setImageBitmap(decodeSampledBitmapFromResource(getResources(), res, 5, 5, 4));

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

    public void initialSet(){
        for(int i=0; i<100; i++) {survivals[i] = new SurvivalLevel();}
        for(int i=0; i<5; i++) {MyBitmap[i] = null; AIBitmap[i] = null;}
        for(int i=0; i<5; i++) {MyHP[i]=0; AIHP[i]=0;}
        for(int i=0; i<100; i++) {BattleRecord[i] = "";}
        for(int i=0; i<5; i++) {MyStatus[i]=new Status(); AIStatus[i] = new Status();}

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
                        {MySpecie[i].Food[cnt-25] = s_temp;}
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
                    {AISpecie[i].Food[cnt-25] = s_temp;}
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
            else if(turn==0){
                MyHP[i] = 100;
            }
            String ViewName = "imageViewMy" + (i+1);
            ImageView IV = (ImageView) findViewById(getResources().getIdentifier(ViewName,"id",getPackageName()));
            setImage(MySpecie[i].latin.toLowerCase(), 4, IV, MyBitmap[i]);
            if(MyStatus[i].hide || MySpecie[i].latin.equals("") || MySpecie[i].latin.equals("Nothing") || MyHP[i]<=0) {TV.setVisibility(View.INVISIBLE);IV.setVisibility(View.INVISIBLE);}
            else {TV.setVisibility(View.VISIBLE);IV.setVisibility(View.VISIBLE);}
            if(MySpecie[i].MimicTarget.equals("")|| MySpecie[i].MimicTarget.equals("Nothing")) {}
            else {setImage(MySpecie[i].MimicTarget.toLowerCase(),4,IV,MyBitmap[i]);}
        }

        for(int i=0; i<5; i++){
            String HPName = "HPAI" + (i+1);
            TextView TV = (TextView) findViewById(getResources().getIdentifier(HPName,"id",getPackageName()));
            if(AISpecie[i].latin.equals("Nothing")||AISpecie[i].latin.equals("")) {
                AIHP[i] = 0;
                TV.getLayoutParams().width = 0;
                continue;
            }
            else if(turn==0){
                AIHP[i] = 100;
            }
            String ViewName = "imageViewAI" + (i+1);
            ImageView IV = (ImageView) findViewById(getResources().getIdentifier(ViewName,"id",getPackageName()));
            setImage(AISpecie[i].latin.toLowerCase(),4,IV,AIBitmap[i]);
            if(AIStatus[i].hide || AISpecie[i].latin.equals("") || AISpecie[i].latin.equals("Nothing") || AIHP[i]<=0) {TV.setVisibility(View.INVISIBLE);IV.setVisibility(View.INVISIBLE);}
            else {TV.setVisibility(View.VISIBLE);IV.setVisibility(View.VISIBLE);}
            if(AISpecie[i].MimicTarget.equals("")|| AISpecie[i].MimicTarget.equals("Nothing")) {}
            else {setImage(AISpecie[i].MimicTarget.toLowerCase(),4,IV,AIBitmap[i]);}
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
        String CurrData = "Attack: "+MySpecie[currAnimal].Attack+ "   Defense: "+MySpecie[currAnimal].Defence + "   Size: "+MySpecie[currAnimal].Size+"\n";
        Data.setText(CurrData);

        TextView Ability = (TextView) findViewById(R.id.textViewAbility);
        String CurrAbility = "Ability:   ";
        int cnt = 0;
        for(int i=0; i<5; i++) {
            String ability = MySpecie[currAnimal].Ability[i];
            if(ability.equals("")||ability.equals("No")||ability.equals("Nothing")) break;
            CurrAbility = CurrAbility+ "[" + ability + "]  ";
            cnt++;
        }
        if(cnt==0) CurrAbility = CurrAbility+"No Ability";
        Ability.setText(CurrAbility);

        TextView Label = (TextView) findViewById(R.id.textViewLabel);
        String CurrLabel = MySpecie[currAnimal].Diet + ", Foods: ";
        cnt = 0;
        for(int i=0; i<9; i++) {
            String food = MySpecie[currAnimal].Food[i];
            if(food.equals("")||food.equals("O")||food.equals("Nothing")) break;
            CurrLabel = CurrLabel+"["+food+"]  ";
            cnt++;
        }
        if(cnt==0) CurrLabel = CurrLabel+"Not indicated";
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


    protected void alertBack() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Battle.this);
        builder.setMessage("Are you sure to surrender?");
        builder.setTitle("Surrender...");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                goSurvival1();
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

    public void writeSurvivalRecord(){
        // Write player's Survival Level Infromation
        SurvivalRecord[currLevel] = true;
        try {
            OutputStreamWriter fos = new OutputStreamWriter(openFileOutput("survivalinformation", Context.MODE_PRIVATE));
            fos.write(Integer.toString(currLevel)); fos.write('\n');
            for(int i=0; i<100; i++) {
                if(i==currLevel+1 || i==currLevel) {fos.write("Yes");}
                else if(SurvivalRecord[i]==false){fos.write("No");}
                else {fos.write("Yes");}
                fos.write('\n'); // Set every achievement as not complete
            }
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }

    public void readAndWriteDNA(int prize){
        String buffer[] = new String[32];
        try {
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(
                    openFileInput("playerinformation")));
            inputReader.readLine();
            for(int i=0; i<32; i++){
                buffer[i] = inputReader.readLine();
            }
            inputReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        DNA = DNA + prize; // survivals[currLevel].Prize;

        try {
            OutputStreamWriter fos = new OutputStreamWriter(openFileOutput("playerinformation", Context.MODE_PRIVATE));
            fos.write(Integer.toString(DNA)); fos.write('\n');
            for(int i=0; i<32; i++) {
                fos.write(buffer[i]);fos.write('\n'); // Set Animal Latin Name
            }
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return;
    }

    public void goSurvival(View view){
        if(Result.equals("Unknown"))    alertBack();
        if(Result.equals("Win"))  {writeSurvivalRecord(); readAndWriteDNA(survivals[currLevel].Prize);goSurvival1();}
        if(Result.equals("Lose")) {goSurvival1();}
        return;
    }

    public void goSurvival1(){
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

    public void MyHit (final int from, final int to, View fromView, View toView){
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
        LLback.addView(TempTo);
        RLback.addView(TempFrom);
        //TempFrom.layout(X1, Y1, X1 + fromView.getWidth(), Y1 + fromView.getHeight()); TempTo.layout(X2, Y2, X2 + toView.getWidth(), Y2 + toView.getHeight());
        TempFrom.setVisibility(View.INVISIBLE); TempTo.setVisibility(View.INVISIBLE);
        TempFrom.setScaleType(ImageView.ScaleType.FIT_XY); TempTo.setScaleType(ImageView.ScaleType.FIT_XY);
        TempFrom.getLayoutParams().height = fromView.getHeight(); TempFrom.getLayoutParams().width = fromView.getWidth(); TempTo.getLayoutParams().height = toView.getHeight(); TempTo.getLayoutParams().width = toView.getWidth();
        setImage(MySpecie[from].latin.toLowerCase(), 2, TempFrom, bitmapFrom);
        setImage(AISpecie[to].latin.toLowerCase(), 2, TempTo, bitmapTo);

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
                TempTo.setImageBitmap(null);
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

                // Damage Calculation
                MyDamage(from, to);

                processDeath();

                AIDecision();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        return;
    }


    public void AIHit (int from, int to, View fromView, View toView){
        LinearLayout LLsub1 = (LinearLayout) findViewById(R.id.LLsub1), LLsub2 = (LinearLayout) findViewById(R.id.LLsub2), LLsub3 = (LinearLayout) findViewById(R.id.LLsub3), LLsub4 = (LinearLayout) findViewById(R.id.LLsub4);
        LLsub1.setClipChildren(false); LLsub2.setClipChildren(false); LLsub3.setClipChildren(false); LLsub4.setClipChildren(false);
        int FromLLleft = 0, FromLLtop = 0, ToLLleft = 0, ToLLtop = 0;
        final Bitmap bitmapFrom=null, bitmapTo = null;

        switch (from){
            case 0:case 2: case 4: FromLLleft = LLsub4.getLeft(); FromLLtop = LLsub4.getTop();break;
            case 1:case 3: FromLLleft = LLsub3.getLeft(); FromLLtop =LLsub3.getTop();
        }
        switch (to){
            case 0:case 2: case 4: ToLLleft = LLsub1.getLeft(); ToLLtop = LLsub1.getTop();break;
            case 1:case 3: ToLLleft = LLsub2.getLeft(); ToLLtop =LLsub2.getTop();
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
        setImage(AISpecie[from].latin.toLowerCase(), 2, TempFrom, bitmapFrom); setImage(MySpecie[to].latin.toLowerCase(), 2, TempTo, bitmapTo);

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
                TempTo.setImageBitmap(null);
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

                StatusProcess();
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

    public void goAttack(View view){

        // clear BattleRecord
        for(int i=0; i<100; i++) BattleRecord[i] = "";

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

    public void AIAttack(View view){

        // clear BattleRecord
        for(int i=0; i<100; i++) BattleRecord[i] = "";

        AIDecision();

        return;
    }

    public void AIDecision(){

        int best_damage = -1, best_from = -1, best_to = -1;

        for(int i=0; i<5; i++){
            if(AISpecie[i].latin.equals("")||AISpecie[i].latin.equals("Nothing")){continue;}
            for(int j=0; j<5; j++){
                if(MySpecie[j].latin.equals("")||MySpecie[j].latin.equals("Nothing")) {continue;}
                if(AIDamageEstimate(i,j)>best_damage){
                    best_damage = AIDamageEstimate(i,j); best_from=i; best_to=j;
                }
            }
        }

        if(best_damage<=0) {
            BattleRecord[0] = BattleRecord[0] + "Turn "+turn + ": Enemies don't give any attack." + '\n';
            StatusProcess();
            return;
        }

        // AI's Hit
        String viewName1 = "imageViewAI"+(best_from+1);
        ImageView attackerView = (ImageView) findViewById(getResources().getIdentifier(viewName1,"id",getPackageName()));
        String viewName2 = "imageViewMy"+(best_to+1);
        ImageView targetView = (ImageView) findViewById(getResources().getIdentifier(viewName2,"id",getPackageName()));


        AIHit(best_from, best_to, attackerView, targetView);

        // Damage Calculation
        AIDamage(best_from, best_to);

        return;
    }

    public void StatusProcess(){

        for(int i=0; i<5; i++){
            MyStatus[i].HP_up=0;
            AIStatus[i].HP_up=0;
        }

        for(int i=0; i<5; i++){
            if(MySpecie[i].Diet.equals("Herbivore")) {MyStatus[i].HP_up = 10;}
            if(AISpecie[i].Diet.equals("Herbivore")) {AIStatus[i].HP_up = 10;}
        }
        int MyCoralCoe = 0, AICoralCoe = 0, MyTermiteCoe = 0, AITermiteCoe = 0;
        for(int i=0; i<5; i++){
            for(int j=0; j<5; j++){
                if(MySpecie[i].Ability[j].equals("Coral Reef")) {MyCoralCoe = 10;}
                if(AISpecie[i].Ability[j].equals("Coral Reef")) {AICoralCoe = 10;}
                if(MySpecie[i].Ability[j].equals("Termite Mound")) {MyTermiteCoe = 10;}
                if(AISpecie[i].Ability[j].equals("Termite Mound")) {AITermiteCoe = 10;}
                if(MySpecie[i].Ability[j].equals("Chemosynthesis")) {MyStatus[i].HP_up=20;}
                if(AISpecie[i].Ability[j].equals("Chemosynthesis")) {AIStatus[i].HP_up=20;}
            }
        }
        for(int i=0; i<5; i++){
            MyStatus[i].HP_up += MyCoralCoe;
            AIStatus[i].HP_up += AICoralCoe;
            if(Size2Num(MySpecie[i].Size)<=Size2Num("Small")){
                MyStatus[i].HP_up += MyTermiteCoe;
            }
            if(Size2Num(AISpecie[i].Size)<=Size2Num("Small")){
                AIStatus[i].HP_up += AITermiteCoe;
            }
        }
        for(int i=0; i<5; i++) {
            if (MyStatus[i].Toxic) {
                MyHP[i] = 0;
            }
            if (AIStatus[i].Toxic) {
                AIHP[i] = 0;
            }
        }
        for(int i=0; i<5; i++){
            if(MyStatus[i].poisonous){
                MyHP[i] -= MyStatus[i].poisonous_down;
                if(MyHP[i]<0) MyHP[i] = 0;
            }
            if(AIStatus[i].poisonous){
                AIHP[i] -= AIStatus[i].poisonous_down;
                if(AIHP[i]<0) AIHP[i] = 0;
            }
        }

        processDeath();

        for(int i=0; i<5; i++){
            if(MyStatus[i].To_leech>=0) {
                int ind = MyStatus[i].To_leech;
                MyHP[i] += (AIStatus[ind].HP_leeched[i] > AIHP[ind])? AIHP[ind]:AIStatus[ind].HP_leeched[i];
                AIHP[ind] -= (AIStatus[ind].HP_leeched[i] > AIHP[ind])? AIHP[ind]:AIStatus[ind].HP_leeched[i];
                BattleRecord[0] = BattleRecord[0] + "Turn "+turn + ": Our " + MySpecie[i].english + " leeches enemy's " + AISpecie[ind].english + "!"+ '\n';
            }

            if(AIStatus[i].To_leech>=0) {
                int ind = AIStatus[i].To_leech;
                AIHP[i] += (MyStatus[ind].HP_leeched[i] > MyHP[ind])? MyHP[ind]:MyStatus[ind].HP_leeched[i];
                MyHP[ind] -= (MyStatus[ind].HP_leeched[i] > MyHP[ind])? MyHP[ind]:MyStatus[ind].HP_leeched[i];
                BattleRecord[0] = BattleRecord[0] + "Turn "+turn + ": Enemy's " + AISpecie[i].english + " leeches our " + MySpecie[ind].english + "!"+ '\n';
            }
        }

        processDeath();

        for(int i=0; i<5; i++){
            if(MySpecie[i].english.equals("") || MySpecie[i].english.equals("Nothing")){ continue; }
            if(MyStatus[i].HP_up>0 && MyHP[i]<100){
                BattleRecord[0] = BattleRecord[0] + "Turn "+turn + ": Our " + MySpecie[i].english + " recovers a little!"+ '\n';
                MyHP[i] += MyStatus[i].HP_up;
                if(MyHP[i]>100) MyHP[i] = 100;
            }
        }
        for(int i=0; i<5; i++){
            if(AISpecie[i].english.equals("") || AISpecie[i].english.equals("Nothing")){ continue; }
            if(AIStatus[i].HP_up>0 && AIHP[i]<100) {
                BattleRecord[0] = BattleRecord[0] + "Turn " + turn + ": Enemy's " + AISpecie[i].english + " recovers a little!" + '\n';
                AIHP[i] += AIStatus[i].HP_up;
                if(AIHP[i]>100) AIHP[i] = 100;
            }
        }

        for(int i=0; i<5; i++){
            if(MyStatus[i].hide) {
                String viewName1 = "imageViewMy" + (i + 1);
                ImageView View1 = (ImageView) findViewById(getResources().getIdentifier(viewName1, "id", getPackageName()));
                View1.setVisibility(View.INVISIBLE);
                String viewName2 = "HPMy" + (i + 1);
                TextView View2 = (TextView) findViewById(getResources().getIdentifier(viewName2, "id", getPackageName()));
                View2.setVisibility(View.INVISIBLE);
            }
            if(AIStatus[i].hide){
                String viewName1 = "imageViewAI" + (i + 1);
                ImageView View1 = (ImageView) findViewById(getResources().getIdentifier(viewName1, "id", getPackageName()));
                View1.setVisibility(View.INVISIBLE);
                String viewName2 = "HPAI" + (i + 1);
                TextView View2 = (TextView) findViewById(getResources().getIdentifier(viewName2, "id", getPackageName()));
                View2.setVisibility(View.INVISIBLE);
            }
        }

        judgeWinOrLose();

        updateHP();

        turn++;

        refresh();

        switch2Record();

        return;
    }

    public void updateHP(){
        for(int i=0; i<5; i++){
            if(MySpecie[i].english.equals("") || MySpecie[i].english.equals("Nothing")) continue;
            String HPname = "HPMy" + (i + 1);
            TextView View2 = (TextView) findViewById(getResources().getIdentifier(HPname, "id", getPackageName()));
            View2.getLayoutParams().width = HpLength*MyHP[i]/100+1;
        }
        for(int i=0; i<5; i++){
            if(AISpecie[i].english.equals("") || AISpecie[i].english.equals("Nothing")) continue;
            String HPname = "HPAI" + (i + 1);
            TextView View2 = (TextView) findViewById(getResources().getIdentifier(HPname, "id", getPackageName()));
            View2.getLayoutParams().width = HpLength*AIHP[i]/100+1;
        }
        return;
    }

    public void alertWin(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("You Pass the Level...");
        alertDialog.setMessage("You passed the level and gain "+survivals[currLevel].Prize+ " DNA as prize! Click go back to level selecting page.");
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        alertDialog.show();
        return;
    }

    public void alertLose(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Failed to Pass the Level...");
        alertDialog.setMessage("You didn't pass the level, try another time maybe.");
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                goSurvival1();
                return;
            }
        });
        alertDialog.show();
        return;
    }

    public void judgeWinOrLose(){
        if(turn>=50) Result = "Lose";
        int dieNumMy = 0, dieNumAI =0;
        for(int i=0; i<5; i++) {
            if(MySpecie[i].english.equals("")||MySpecie[i].english.equals("Nothing")){
                dieNumMy++;
            }
            if(AISpecie[i].english.equals("")||AISpecie[i].english.equals("Nothing")){
                dieNumAI++;
            }
        }
        if(dieNumMy>=5) Result = "Lose";
        if(dieNumAI>=5) Result = "Win";
        if(Result.equals("Lose")){
            alertLose();
        }
        else if(Result.equals("Win")){
            alertWin();
        }
        return;
    }

    public void processDeath() {

        for (int i = 0; i < 5; i++) {
            if (MyHP[i] <= 0) {
                if(MySpecie[i].latin.equals("")||MySpecie[i].latin.equals("Nothing")) {}
                else{
                    for (int j = 0; j < 5; j++) {
                        if (MyStatus[i].Be_leeched[j]) {
                            AIStatus[j].To_leech = -1;
                            AIStatus[j].To_leech_HP = 0;
                        }
                    }

                    String viewName1 = "imageViewMy" + (i + 1);
                    ImageView View1 = (ImageView) findViewById(getResources().getIdentifier(viewName1, "id", getPackageName()));
                    View1.setVisibility(View.INVISIBLE);
                    String viewName2 = "HPMy" + (i + 1);
                    TextView View2 = (TextView) findViewById(getResources().getIdentifier(viewName2, "id", getPackageName()));
                    View2.setVisibility(View.INVISIBLE);
                    MySpecie[i] = new Specie();
                }
            }
            if (AIHP[i] <= 0) {
                if(AISpecie[i].latin.equals("")||AISpecie[i].latin.equals("Nothing")) {}
                else {
                    for (int j = 0; j < 5; j++) {
                        if (AIStatus[i].Be_leeched[j]) {
                            MyStatus[j].To_leech = -1;
                            MyStatus[j].To_leech_HP = 0;
                        }
                    }

                    String viewName1 = "imageViewAI" + (i + 1);
                    ImageView View1 = (ImageView) findViewById(getResources().getIdentifier(viewName1, "id", getPackageName()));
                    View1.setVisibility(View.INVISIBLE);
                    String viewName2 = "HPAI" + (i + 1);
                    TextView View2 = (TextView) findViewById(getResources().getIdentifier(viewName2, "id", getPackageName()));
                    View2.setVisibility(View.INVISIBLE);
                    AISpecie[i] = new Specie();
                }
            }
        }

        return;
    }

    public int AIDamageEstimate(int from, int to){

        if(!AIAttackable(from, to)) {return 0;}
        for(int i=0; i<5; i++){
            if(AISpecie[from].Label[i].equals("Toxic Shock")) {
                boolean anti=false;
                for(int j=0; j<5; j++){
                    if(MySpecie[to].Label[j].equals("Anti-Poison")) {anti=true;}
                }
                if(anti==false) return 1000;
            }
        }
        if(AIEatable(from, to)) return 1000;
        if(AISpecie[from].Diet.equals("Parasitic")) {
            return AISpecie[from].Attack * 3;
        }
        int damage = AISpecie[from].Attack*40/MySpecie[to].Defence+2;
        for(int k=0; k< Size2Num(AISpecie[from].Size)-Size2Num(MySpecie[to].Size); k++){
            damage = damage*2;
        }
        for(int k=0; k< Size2Num(MySpecie[to].Size)-Size2Num(AISpecie[from].Size); k++){
            damage = damage/2;
        }
        return damage;
    }

    public int AIDamage(int from, int to){
        if(!AIAttackable(from, to)) {return 0;}

        for(int i=0; i<5; i++){
            for(int j=0; j<5; j++){
                if(MySpecie[i].Ability[j].equals("Trap") && MyEatable(i,from)){
                    MyHP[i] += AIHP[from];
                    if(MyHP[i]>100) MyHP[i] = 100;
                    AIHP[from] = 0;
                    BattleRecord[0] = BattleRecord[0] + "Turn "+turn+ ": Enemy's "+ AISpecie[from].english + " falls into the trap of "+ MySpecie[i].english + " and is killed!"+ '\n';
                    return 0;
                }
            }
        }

        if(AIStatus[from].hide) {
            AIStatus[from].hide=false;
            String viewName1 = "imageViewAI" + (from + 1);
            ImageView View1 = (ImageView) findViewById(getResources().getIdentifier(viewName1, "id", getPackageName()));
            View1.setVisibility(View.VISIBLE);
            String viewName2 = "HPAI" + (from + 1);
            TextView View2 = (TextView) findViewById(getResources().getIdentifier(viewName2, "id", getPackageName()));
            View2.setVisibility(View.VISIBLE);
        }
        for(int i=0; i<5; i++){
            if(MySpecie[to].Ability[i].equals("Smog")){
                MyStatus[to].hide = true;
            }
        }
        if(AISpecie[from].MimicTarget.equals("") || AISpecie[from].MimicTarget.equals("Nothing")){}
        else {AISpecie[from].MimicTarget = "";}

        int realAIAttack = AISpecie[from].Attack, realAIDefense = AISpecie[from].Defence;
        int realMyAttack = MySpecie[to].Attack, realMyDefense = MySpecie[to].Defence;
        int SpongeCoe = 1, SocialCoe=1,CryptobiosisCoe=1, ShellCoe=1, TermiteCoe = 1;
        for(int i=0; i<5; i++){
            for(int j=0; j<5; j++){
                if(MySpecie[i].Ability[j].equals("Sponge Reef")) {SpongeCoe=2;break;}
                if(MySpecie[i].Ability[j].equals("Termite Mound") && Size2Num(MySpecie[to].Size)<=Size2Num("Small")) {TermiteCoe=2;break;}
                if(MySpecie[i].Ability[j].equals("Shell Colony") && (AISpecie[from].Size.equals("Huge")||AISpecie[from].Size.equals("XLarge")) ) {ShellCoe=2;break;}
            }
            if(MyHP[to]<100 && MySpecie[to].Ability.equals("Cryptobiosis")) CryptobiosisCoe=2;
        }
        for(int i=0; i<5; i++){
            if(i==from) continue;
            if(!MySpecie[i].english.equals(MySpecie[from].english)) continue;
            for(int j=0; j<5; j++){
                if(MySpecie[i].Ability[j].equals("Social")) {SocialCoe=2;break;}
            }
        }
        realMyAttack *= SocialCoe; realMyDefense *=SocialCoe*SpongeCoe*CryptobiosisCoe*ShellCoe*TermiteCoe;
        SpongeCoe = 1; SocialCoe=1;CryptobiosisCoe=1; TermiteCoe = 1;
        for(int i=0; i<5; i++){
            for(int j=0; j<5; j++){
                if(AISpecie[i].Ability[j].equals("Sponge Reef")) {SpongeCoe=2;break;}
            }
            if(AIHP[from]<100 && AISpecie[from].Ability.equals("Cryptobiosis")) CryptobiosisCoe=2;
        }
        for(int i=0; i<5; i++){
            if(i==from) continue;
            if(!AISpecie[i].english.equals(AISpecie[from].english)) continue;
            for(int j=0; j<5; j++){
                if(AISpecie[i].Ability[j].equals("Social")) {SocialCoe=2;break;}
            }
        }
        realAIAttack *= SocialCoe; realAIDefense *=SocialCoe*SpongeCoe*CryptobiosisCoe;

        for(int i=0; i<5; i++){
            if(MySpecie[to].Ability[i].equals("Spiky Body")) {AIHP[from]-=25;}
        }
        for(int i=0; i<5; i++){
            if(AISpecie[from].Ability[i].equals("Toxic Shock")) {
                boolean anti=false;
                for(int j=0; j<5; j++){
                    if(MySpecie[to].Ability[j].equals("Anti-Poison")) {anti=true;}
                }
                if(anti==false) {MyStatus[to].Toxic=true; BattleRecord[0] = BattleRecord[0] + "Turn "+turn+ ": Our "+ MySpecie[to].english + " touches toxic!" + '\n'; }
            }
        }
        for(int i=0; i<5; i++){
            if(MySpecie[to].Ability[i].equals("Toxic Body")) {
                boolean anti=false;
                for(int j=0; j<5; j++){
                    if(AISpecie[from].Ability[j].equals("Anti-Poison")) {anti=true;}
                }
                if(anti==false) {AIStatus[from].Toxic=true; BattleRecord[0] = BattleRecord[0] + "Turn "+turn+ ": Enemy's "+ AISpecie[from].english + " touches toxic!" + '\n';}
            }
        }
        for(int i=0; i<5; i++){
            if(AISpecie[from].Ability[i].equals("Poisonous Attack")) {
                boolean anti=false;
                for(int j=0; j<5; j++){
                    if(MySpecie[to].Ability[j].equals("Anti-Poison")) {anti=true;}
                }
                if(anti==false) {
                    MyStatus[to].poisonous=true;
                    int down = 20;
                    for(int k=0; k< Size2Num(AISpecie[from].Size)-Size2Num(MySpecie[to].Size); k++){
                        down = down*2;
                    }
                    for(int k=0; k< Size2Num(MySpecie[to].Size)-Size2Num(AISpecie[from].Size); k++){
                        down = down/2;
                    }
                    MyStatus[to].poisonous_down += down;
                    BattleRecord[0] = BattleRecord[0] + "Turn "+turn+ ": Our "+ MySpecie[to].english + " get poisoned!" + '\n';
                }
            }
        }
        for(int i=0; i<5; i++){
            if(MySpecie[to].Ability[i].equals("Poisonous Defense")) {
                boolean anti=false;
                for(int j=0; j<5; j++){
                    if(AISpecie[from].Ability[j].equals("Anti-Poison")) {anti=true;}
                }
                if(anti==false) {
                    AIStatus[from].poisonous=true;
                    int down = 20;
                    for(int k=0; k< Size2Num(MySpecie[to].Size)-Size2Num(AISpecie[from].Size); k++){
                        down = down*2;
                    }
                    for(int k=0; k< Size2Num(AISpecie[from].Size)-Size2Num(MySpecie[to].Size); k++){
                        down = down/2;
                    }
                    AIStatus[from].poisonous_down += down;
                    BattleRecord[0] = BattleRecord[0] + "Turn "+turn+ ": Enemy's "+ AISpecie[from].english + " get poisoned!" + '\n';
                }
            }
        }
        if(AISpecie[from].Diet.equals("Parasitic")) {
            if(AIStatus[from].To_leech>=0) {
                int ind = AIStatus[from].To_leech;
                MyStatus[ind].Be_leeched[from]=false; MyStatus[ind].HP_leeched[from] = 0;
            }
            MyStatus[to].Be_leeched[from] = true;
            MyStatus[to].HP_leeched[from] = realAIAttack;
            AIStatus[from].To_leech = to;
            AIStatus[from].To_leech_HP = realAIAttack;
            BattleRecord[0] = BattleRecord[0] + "Turn "+turn+ ": Our "+ MySpecie[to].english + " get parasitic by enemy's " + AISpecie[from].english + "!" + '\n';
            return 0;
        }
        else if(AIEatable(from, to)) {
            boolean stink = false;
            for(int i=0; i<5; i++) {if(MySpecie[to].Ability[i].equals("Stink")) stink = true;}
            if(!stink) AIHP[from]+=MyHP[to];
            else AIHP[from]-=10;
            MyHP[to] = 0;
            BattleRecord[0] = BattleRecord[0] + "Turn "+turn+ ": Our "+ MySpecie[to].english + " get eaten by enemy's " + AISpecie[from].english + "!" + '\n';
            return 100;
        }
        else {
            int damage = realAIAttack*40/realMyDefense+2;
            for(int k=0; k< Size2Num(AISpecie[from].Size)-Size2Num(MySpecie[to].Size); k++){
                damage = damage*2;
            }
            for(int k=0; k< Size2Num(MySpecie[to].Size)-Size2Num(AISpecie[from].Size); k++){
                damage = damage/2;
            }
            for(int i=0; i<5; i++){
                if(AISpecie[from].Ability[i].equals("Electrical Shock")) {damage = damage+25;}
            }
            MyHP[to] -=damage;
            BattleRecord[0] = BattleRecord[0] + "Turn "+turn+ ": Our "+ MySpecie[to].english + " is attacked by enemy's " + AISpecie[from].english +"!"+ '\n';
            if(MyHP[to]>0){
                for(int i=0; i<5; i++){
                    if(MySpecie[to].Ability[i].equals("Regenerator")) {
                        MyHP[to]+=15;
                        BattleRecord[0] = BattleRecord[0] + "Turn "+turn+ ": Our "+ MySpecie[to].english + " recovers a little by regeneration !"+ '\n';
                    }
                }
            }
            for(int i=0; i<5; i++){
                if(AISpecie[from].Ability.equals("Sucker")){
                    AIHP[from]+=damage;
                    BattleRecord[0] = BattleRecord[0] + "Turn "+turn+ ": Enemy's "+ AISpecie[from].english + " recovers a little by sucking us !"+ '\n';
                }
            }
            if(MyHP[to]<0) MyHP[to] = 0;
            return damage;
        }
    }

    public int MyDamage(int from, int to){
        if (!MyAttackable(from, to)) {return 0;}

        for(int i=0; i<5; i++){
            for(int j=0; j<5; j++){
                if(AISpecie[i].Ability[j].equals("Trap") && AIEatable(i, from)){
                    AIHP[i] += MyHP[from];
                    if(AIHP[i]>100) AIHP[i] = 100;
                    MyHP[from] = 0;
                    BattleRecord[0] = BattleRecord[0] + "Turn "+turn+ ": Our "+ MySpecie[from].english + " falls into the trap of "+ AISpecie[i].english + " and is killed!"+ '\n';
                    return 0;
                }
            }
        }

        if(MyStatus[from].hide) {
            MyStatus[from].hide=false;
            String viewName1 = "imageViewMy" + (from + 1);
            ImageView View1 = (ImageView) findViewById(getResources().getIdentifier(viewName1, "id", getPackageName()));
            View1.setVisibility(View.VISIBLE);
            String viewName2 = "HPMy" + (from + 1);
            TextView View2 = (TextView) findViewById(getResources().getIdentifier(viewName2, "id", getPackageName()));
            View2.setVisibility(View.VISIBLE);
        }
        for(int i=0; i<5; i++){
            if(AISpecie[to].Ability[i].equals("Smog")){
                AIStatus[to].hide = true;
            }
        }
        if(MySpecie[from].MimicTarget.equals("") || MySpecie[from].MimicTarget.equals("Nothing")){}
        else {MySpecie[from].MimicTarget = "";}

        int realMyAttack = MySpecie[from].Attack, realMyDefense = MySpecie[from].Defence;
        int realAIAttack = AISpecie[to].Attack, realAIDefense = AISpecie[to].Defence;
        int SpongeCoe = 1, SocialCoe=1, CryptobiosisCoe = 1, ShellCoe=1, TermiteCoe = 1;
        for(int i=0; i<5; i++){
            for(int j=0; j<5; j++){
                if(MySpecie[i].Ability[j].equals("Sponge Reef")) {SpongeCoe=2;break;}
            }
            if(MyHP[from]<100 && MySpecie[from].Ability[i].equals("Cryptobiosis")) {CryptobiosisCoe=2; break;}
        }
        for(int i=0; i<5; i++){
            if(i==from) continue;
            if(!MySpecie[i].english.equals(MySpecie[from].english)) continue;
            for(int j=0; j<5; j++){
                if(MySpecie[i].Ability[j].equals("Social")) {SocialCoe=2;break;}
            }
        }
        realMyAttack *= SocialCoe; realMyDefense *=SocialCoe*SpongeCoe*CryptobiosisCoe;
        SpongeCoe = 1; SocialCoe=1; CryptobiosisCoe = 1; TermiteCoe = 1;
        for(int i=0; i<5; i++){
            for(int j=0; j<5; j++){
                if(AISpecie[i].Ability[j].equals("Sponge Reef")) {SpongeCoe=2;break;}
                if(AISpecie[i].Ability[j].equals("Termite Mound") && Size2Num(AISpecie[i].Size)<=Size2Num("Small")) {TermiteCoe=2;break;}
                if(AISpecie[i].Ability[j].equals("Shell Colony") && (MySpecie[from].Size.equals("Huge")||MySpecie[from].Size.equals("XLarge")) ) {ShellCoe=2;break;}
            }
            if(AIHP[to]<100 && AISpecie[to].Ability[i].equals("Cryptobiosis")) {CryptobiosisCoe=2; break;}
        }
        for(int i=0; i<5; i++){
            if(i==from) continue;
            if(!AISpecie[i].english.equals(AISpecie[from].english)) continue;
            for(int j=0; j<5; j++){
                if(AISpecie[i].Ability[j].equals("Social")) {SocialCoe=2;break;}
            }
        }
        realAIAttack *= SocialCoe; realAIDefense *=SocialCoe*SpongeCoe*CryptobiosisCoe*ShellCoe*TermiteCoe;

        for(int i=0; i<5; i++){
            if(AISpecie[to].Ability[i].equals("Spiky Body")) {MyHP[from]-=25;}
        }
        for(int i=0; i<5; i++){
            if(MySpecie[from].Ability[i].equals("Toxic Shock")) {
                boolean anti=false;
                for(int j=0; j<5; j++){
                    if(AISpecie[to].Ability[j].equals("Anti-Poison")) {anti=true;}
                }
                if(anti==false) {AIStatus[to].Toxic=true; BattleRecord[0] = BattleRecord[0] + "Turn "+turn+ ": Enemy's "+ AISpecie[to].english + " touches toxic!" + '\n';}
            }
        }
        for(int i=0; i<5; i++){
            if(AISpecie[to].Ability[i].equals("Toxic Body")) {
                boolean anti=false;
                for(int j=0; j<5; j++){
                    if(MySpecie[from].Ability[j].equals("Anti-Poison")) {anti=true;}
                }
                if(anti==false) {MyStatus[from].Toxic=true;BattleRecord[0] = BattleRecord[0] + "Turn "+turn+ ": Our "+ MySpecie[from].english + " touches toxic!" + '\n';}
            }
        }
        for(int i=0; i<5; i++){
            if(MySpecie[from].Ability[i].equals("Poisonous Attack")) {
                boolean anti=false;
                for(int j=0; j<5; j++){
                    if(AISpecie[to].Ability[j].equals("Anti-Poison")) {anti=true;}
                }
                if(anti==false) {
                    AIStatus[to].poisonous=true;
                    int down = 20;
                    for(int k=0; k< Size2Num(MySpecie[from].Size)-Size2Num(AISpecie[to].Size); k++){
                        down = down*2;
                    }
                    for(int k=0; k< Size2Num(AISpecie[to].Size)-Size2Num(MySpecie[from].Size); k++){
                        down = down/2;
                    }
                    AIStatus[to].poisonous_down += down;
                    BattleRecord[0] = BattleRecord[0] + "Turn "+turn+ ": Enemy's "+ AISpecie[to].english + " get poisoned!" + '\n';
                }
            }
        }
        for(int i=0; i<5; i++){
            if(AISpecie[to].Ability[i].equals("Poisonous Defense")) {
                boolean anti=false;
                for(int j=0; j<5; j++){
                    if(MySpecie[from].Ability[j].equals("Anti-Poison")) {anti=true;}
                }
                if(anti==false) {
                    MyStatus[from].poisonous=true;
                    int down = 20;
                    for(int k=0; k< Size2Num(AISpecie[to].Size)-Size2Num(MySpecie[from].Size); k++){
                        down = down*2;
                    }
                    for(int k=0; k< Size2Num(MySpecie[from].Size)-Size2Num(AISpecie[to].Size); k++){
                        down = down/2;
                    }
                    MyStatus[from].poisonous_down += down;
                    BattleRecord[0] = BattleRecord[0] + "Turn "+turn+ ": Our "+ MySpecie[from].english + " get poisoned!" + '\n';
                }
            }
        }
        if(MySpecie[from].Diet.equals("Parasitic")) {
            if(MyStatus[from].To_leech>=0) {
                int ind = MyStatus[from].To_leech;
                AIStatus[ind].Be_leeched[from]=false; AIStatus[ind].HP_leeched[from] = 0;
            }
            AIStatus[to].Be_leeched[from] = true;
            AIStatus[to].HP_leeched[from] = realMyAttack;
            MyStatus[from].To_leech = to;
            MyStatus[from].To_leech_HP = realMyAttack;
            BattleRecord[0] = BattleRecord[0] + "Turn "+turn+ ": Enemy's "+ AISpecie[to].english + " get parasitic by our " + MySpecie[from].english + '\n';
            return 0;
        } else if (MyEatable(from, to)) {
            boolean stink = false;
            for(int i=0; i<5; i++) {if(AISpecie[to].Ability[i].equals("Stink")) stink = true;}
            if(!stink) MyHP[from]+=AIHP[to];
            else MyHP[from]-=10;
            AIHP[to] = 0;
            BattleRecord[0] = BattleRecord[0] + "Turn "+turn+ ": Enemy's "+ AISpecie[to].english + " get eaten by our " + MySpecie[from].english + '\n';
            return 100;
        }
        else {
            int damage = realMyAttack*40/realAIDefense+2;
            for(int k=0; k< Size2Num(MySpecie[from].Size)-Size2Num(AISpecie[to].Size); k++){
                damage = damage*2;
            }
            for(int k=0; k< Size2Num(AISpecie[to].Size)-Size2Num(MySpecie[from].Size); k++){
                damage = damage/2;
            }
            for(int i=0; i<5; i++){
                if(MySpecie[from].Ability[i].equals("Electrical Shock")) {damage = damage+25;}
            }
            AIHP[to] -=damage;
            BattleRecord[0] = BattleRecord[0] + "Turn "+turn+ ": Enemy's "+ AISpecie[to].english + " is attacked by our " + MySpecie[from].english + '\n';
            if(AIHP[to]>0){
                for(int i=0; i<5; i++){
                    if(AISpecie[to].Ability[i].equals("Regenerator")) {
                        AIHP[to]+=15;
                        BattleRecord[0] = BattleRecord[0] + "Turn "+turn+ ": Enemy's "+ AISpecie[to].english + " recovers a little by regeneration !"+ '\n';
                    }
                }
            }
            for(int i=0; i<5; i++){
                if(MySpecie[from].Ability.equals("Sucker")){
                    MyHP[from]+=damage;
                    BattleRecord[0] = BattleRecord[0] + "Turn "+turn+ ": Our "+ MySpecie[from].english + " recovers a little by sucking enemy !"+ '\n';
                }
            }
            if(AIHP[to]<0) AIHP[to] = 0;
            return damage;
        }
    }

    public boolean MyAttackable(int from, int to ){
        if(turn == 0){
            for(int j=0; j<5; j++){
                if(AISpecie[to].Ability[j].equals("Illuminator")) return false;
            }
        }
        for(int i=0; i<5; i++){
            if(MySpecie[from].Ability[i].equals("Intelligence")) return true;
        }
        if(AIStatus[to].unaccessible || AIStatus[to].hide) return false;
        if(AIStatus[to].To_leech>=0){
            for(int i=0; i<5; i++){
                if(AISpecie[to].Ability[i].equals("Inner Parasitic")) {return false;}
            }
        }
        if(MyStatus[from].To_leech>=0){
            for(int i=0; i<5; i++){
                if(MySpecie[from].Ability[i].equals("Inner Parasitic")) {return false;}
            }
        }
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
        if(MyEatable(from, to)) return true;
        for(int i=0; i<5; i++) {if(MySpecie[from].Ability[i].equals("Still")) return false;}
        for(int i=0; i<5; i++) {
            if(AISpecie[to].Ability[i].equals("Aerial")){
                boolean fly=false;
                for(int j=0; j<5; j++){
                    if(MySpecie[from].Ability[j].equals("Aerial")){
                        fly=true;
                    }
                }
                if(fly==false) return false;
            }
        }
        for(int i=0; i<5; i++) {
            if(AISpecie[to].Ability[i].equals("Hole Digger")){
                boolean ground=false;
                for(int j=0; j<5; j++){
                    if(MySpecie[from].Ability[j].equals("Hole Digger")){
                        ground=true;
                    }
                }
                if(ground==false) return false;
            }
        }
        boolean Sniper = false, Sucker = false, HighJumping = false;
        for(int i=0; i<5; i++) {if(MySpecie[from].Ability[i].equals("Sniper")) {Sniper=true; break;}}
        for(int i=0; i<5; i++) {if(MySpecie[from].Ability[i].equals("Sucker")) {Sucker=true;break;}}
        for(int i=0; i<5; i++) {if(AISpecie[to].Ability[i].equals("High Jumping")) {HighJumping=true;break;}}
        if(Sniper) return AttackableTableSniper(MySpecie[from].Size,AISpecie[to].Size);
        if(Sucker) return AttackableTableSucker(MySpecie[from].Size, AISpecie[to].Size);
        if(HighJumping) return AttackableTableHighJumping(MySpecie[from].Size, AISpecie[to].Size);
        return AttackableTable(MySpecie[from].Size,AISpecie[to].Size);
    }

    public boolean AIAttackable(int from, int to ){
        for(int i=0; i<5; i++){
            if(AISpecie[from].Ability[i].equals("Intelligence")) return true;
        }
        if(turn == 0){
            for(int j=0; j<5; j++) {
                if (MySpecie[to].Ability[j].equals("Illuminator")) return false;
            }
        }
        if(MyStatus[to].unaccessible || MyStatus[to].hide) return false;
        if(MyStatus[to].To_leech>=0){
            for(int i=0; i<5; i++){
                if(MySpecie[to].Ability[i].equals("Inner Parasitic")) {return false;}
            }
        }
        if(AIStatus[from].To_leech>=0){
            for(int i=0; i<5; i++){
                if(AISpecie[from].Ability[i].equals("Inner Parasitic")) {return false;}
            }
        }
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
        if (AIEatable(from, to)) return  true;
        for(int i=0; i<5; i++) {if(AISpecie[from].Ability[i].equals("Still")) return false;}
        for(int i=0; i<5; i++) {
            if(MySpecie[to].Ability[i].equals("Aerial")){
                boolean fly=false;
                for(int j=0; j<5; j++){
                    if(AISpecie[from].Ability[j].equals("Aerial")){
                        fly=true;
                    }
                }
                if(fly==false) return false;
            }
        }
        for(int i=0; i<5; i++) {
            if(MySpecie[to].Ability[i].equals("Hole Digger")){
                boolean ground=false;
                for(int j=0; j<5; j++){
                    if(AISpecie[from].Ability[j].equals("Hole Digger")){
                        ground=true;
                    }
                }
                if(ground==false) return false;
            }
        }
        boolean Sniper = false, Sucker = false, HighJumping = false;
        for(int i=0; i<5; i++) {if(AISpecie[from].Ability[i].equals("Sniper")) {Sniper=true; break;}}
        for(int i=0; i<5; i++) {if(AISpecie[from].Ability[i].equals("Sucker")) {Sucker=true; break;}}
        for(int i=0; i<5; i++) {if(MySpecie[to].Ability[i].equals("High Jumping")) {HighJumping=true; break;}}
        if(Sniper) return AttackableTableSniper(AISpecie[from].Size, MySpecie[to].Size);
        if(Sucker) return AttackableTableSucker(AISpecie[from].Size, MySpecie[to].Size);
        if(HighJumping) return AttackableTableHighJumping(AISpecie[from].Size, MySpecie[to].Size);
        return AttackableTable(AISpecie[from].Size, MySpecie[to].Size);
    }

    public boolean MyEatable(int from, int to){
        for(int i=0; i<5; i++){
            if(MySpecie[from].Ability[i].equals("Big Swallow") && Size2Num(MySpecie[from].Size)-Size2Num(AISpecie[to].Size)>=4 ){return true;}
        }
        for(int i=0; i<9; i++){
            if(MySpecie[from].Food[i].equals("") || MySpecie[from].Food[i].equals("O") || MySpecie[from].Food[i].equals("Nothing")) {continue;}
            for(int j=0; j<5; j++) {
                if(MySpecie[from].Food[i].equals(AISpecie[to].Label[j])) {
                    //exceptions for those can be eaten by much smaller animals, those never move or not so large as it looks like
                    if(AISpecie[to].Label[j].equals("Coral")||AISpecie[to].Label[j].equals("Sponge")  || AISpecie[to].Label[j].equals("Jellyfish")) return true;
                    //Normally Case
                    if(Size2Num(AISpecie[to].Size)-Size2Num(MySpecie[from].Size)<=0)  return true;
                }
            }
        }
        return false;
    }

    public boolean AIEatable(int from, int to){
        for(int i=0; i<5; i++){
            if(AISpecie[from].Ability[i].equals("Big Swallow") && Size2Num(AISpecie[from].Size)-Size2Num(MySpecie[to].Size)>=4 ){return true;}
        }
        for(int i=0; i<9; i++){
            if(AISpecie[from].Food[i].equals("") || AISpecie[from].Food[i].equals("O") || AISpecie[from].Food[i].equals("Nothing")) {continue;}
            for(int j=0; j<5; j++) {
                if(AISpecie[from].Food[i].equals(MySpecie[to].Label[j])) {
                    //exceptions for those can be eaten by much smaller animals, those never move or not so large as it looks like
                    if(MySpecie[to].Label[j].equals("Coral") ||MySpecie[to].Label[j].equals("Sponge") || MySpecie[to].Label[j].equals("Jellyfish")) return true;
                    //Normally Case
                    if(Size2Num(MySpecie[to].Size)-Size2Num(AISpecie[from].Size)<=0)  return true;
                }
            }
        }
        return false;
    }

    public boolean AttackableTable(String sizeFrom, String sizeTo){
        if((Size2Num(sizeFrom)-Size2Num(sizeTo))<=1 && (Size2Num(sizeTo)-Size2Num(sizeFrom))<=1) return true;
        return false;
    }

    public boolean AttackableTableSniper(String sizeFrom, String sizeTo){
        if((Size2Num(sizeFrom)-Size2Num(sizeTo))<=2 && (Size2Num(sizeFrom)>=Size2Num(sizeTo))) return true;
        return false;
    }

    public boolean AttackableTableSucker(String sizeFrom, String sizeTo){
        if((Size2Num(sizeFrom)-Size2Num(sizeTo))<=1 ) return true;
        return false;
    }

    public boolean AttackableTableHighJumping(String sizeFrom, String sizeTo){
        if((Size2Num(sizeFrom)-Size2Num(sizeTo))>=0 ) return true;
        return false;
    }

    public static int Size2Num(String size){
        switch (size){
            case "Micro": return 1;
            case "XSmall": return 2;
            case "Small": return 3;
            case "Small+": return 4;
            case "Medium": return 5;
            case "Medium+": return 6;
            case "Large": return 7;
            case "XLarge": return 8;
            case "Huge": return 9;
        }
        return 0;
    }
}
