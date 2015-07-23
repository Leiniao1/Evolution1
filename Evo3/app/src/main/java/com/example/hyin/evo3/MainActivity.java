package com.example.hyin.evo3;

import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import android.view.View;

import java.io.*;
import java.lang.String;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void evo_update(String s){
        try{
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
            txV2.setText(sub2);
            txV3.setText(sub3);
            txV4.setText(sub4);
            // Update evo pictures
            ImageButton IBsub2 = (ImageButton) findViewById(R.id.imageButton2);
            ImageButton IBsub3 = (ImageButton) findViewById(R.id.imageButton3);
            ImageButton IBsub4 = (ImageButton) findViewById(R.id.imageButton4);
            Drawable Pic2 = getDrawable(getResources()
                    .getIdentifier(sub2.toLowerCase(), "drawable", getPackageName()));
            IBsub2.setBackground(Pic2);
            Drawable Pic3 = getDrawable(getResources()
                    .getIdentifier(sub3.toLowerCase(), "drawable", getPackageName()));
            IBsub3.setBackground(Pic3);
            Drawable Pic4 = getDrawable(getResources()
                    .getIdentifier(sub4.toLowerCase(), "drawable", getPackageName()));
            IBsub4.setBackground(Pic4);
            dataIO.close();
            in.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return;
    }

    public void Generation2(View v){
        ImageButton IBmain = (ImageButton) findViewById(R.id.imageButton);
        ImageButton IBsub = (ImageButton) findViewById(R.id.imageButton2);
        TextView txV2 = (TextView) findViewById(R.id.textView2);
        String s = (String)txV2.getText();
        // Name process
        if(s.equals("Nothing")) return;
        TextView txV1 = (TextView) findViewById(R.id.textView);
        txV1.setText(s);
        // Evolution process
        evo_update(s);
        // Picture process
        s = s.toLowerCase();
        Drawable Pic1 = getDrawable(getResources()
                .getIdentifier(s, "drawable", getPackageName()));
        IBmain.setBackground(Pic1);
        return;
    }

    public void Generation3(View v){
        ImageButton IBmain = (ImageButton) findViewById(R.id.imageButton);
        ImageButton IBsub = (ImageButton) findViewById(R.id.imageButton3);
        TextView txV3 = (TextView) findViewById(R.id.textView3);
        String s = (String)txV3.getText();
        // Name Process
        if(s.equals("Nothing")) return;
        TextView txV1 = (TextView) findViewById(R.id.textView);
        txV1.setText(s);
        // Evolution Process
        evo_update(s);
        // Picture Process
        s = s.toLowerCase();
        Drawable Pic1 = getDrawable(getResources()
                .getIdentifier(s, "drawable", getPackageName()));
        IBmain.setBackground(Pic1);
        return;
    }

    public void Generation4(View v){
        ImageButton IBmain = (ImageButton) findViewById(R.id.imageButton);
        ImageButton IBsub = (ImageButton) findViewById(R.id.imageButton4);
        TextView txV4 = (TextView) findViewById(R.id.textView4);
        String s = (String)txV4.getText();
        // Name Process
        if(s.equals("Nothing")) return;
        TextView txV1 = (TextView) findViewById(R.id.textView);
        txV1.setText(s);
        // Evolution Process
        evo_update(s);
        // Picture Process
        s = s.toLowerCase();
        Drawable Pic1 = getDrawable(getResources()
                .getIdentifier(s, "drawable", getPackageName()));
        IBmain.setBackground(Pic1);
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
