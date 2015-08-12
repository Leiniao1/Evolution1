package com.example.hyin.evo3;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;


public class about extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setTitle("Darwin's Tree");
        setContentView(R.layout.activity_about);
        initialSet();
        return;
    }

    @Override
    public void onBackPressed(){

        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            Intent myIntent = new Intent(getApplicationContext(), MainInterface.class);
            startActivityForResult(myIntent, 0);
            finish();
        } else {
            getFragmentManager().popBackStack();
        }

        return;
    }

    public void initialSet(){
        TextView TVMain = (TextView) findViewById(R.id.textViewAttribution);
        String longString = ""+'\n';

        // Read in License Information
        try{
            // Open File
            InputStream in = getResources().openRawResource(R.raw.imagecopyright);
            BufferedReader dataIO = new BufferedReader(new InputStreamReader(in,"UTF8"));
            // Find the index from document file
            String s_temp = "";
            int num=0;
            while((s_temp = dataIO.readLine())!=null){
                if(num%2==1){
                    longString = longString + s_temp + '\n' + '\n';}
                num++;
            }
            // Close File
            dataIO.close();
            in.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }

        TVMain.setText(longString);
        return;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_about, menu);
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

    public void goMainMenu(View view){
        Intent intent = new Intent(this, MainInterface.class);
        startActivity(intent);
        finish();
        return;
    }


}
