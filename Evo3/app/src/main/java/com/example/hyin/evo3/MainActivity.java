package com.example.hyin.evo3;

import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import android.view.View;
import java.lang.String;

public class MainActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void Generation2(View v){
        ImageButton IBmain = (ImageButton) findViewById(R.id.imageButton);
        ImageButton IBsub = (ImageButton) findViewById(R.id.imageButton2);
        TextView txV2 = (TextView) findViewById(R.id.textView2);
        String s = (String)txV2.getText();
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
