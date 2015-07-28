package com.example.hyin.evo3;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.OutputStreamWriter;


public class MainInterface extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Darwin's Tree");
        setContentView(R.layout.activity_main_interface);
    }

    public void ResetRecord(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainInterface.this);
        builder.setMessage("Are you sure to clear up your saved record?");
        builder.setTitle("Clear record...");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                clearRecord();
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

    public void clearRecord(){
        // Clear up and initialize the record
        try {
            OutputStreamWriter fos = new OutputStreamWriter(openFileOutput("playerinformation", Context.MODE_PRIVATE));
            fos.write(Integer.toString(1200)); fos.write('\n'); // TODO: change this number back to 200 after testing
            fos.write("Cyanobacteria"); fos.write('\n');
            fos.write(Integer.toString(0)); fos.write('\n');
            for(int i=0; i<9; i++) {
                fos.write("Nothing");fos.write('\n'); // Set Animal Latin Name
                fos.write(Integer.toString(0)); fos.write('\n'); //Set Evo_level
            }
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Launch Evo activity
        Intent intent = new Intent(this, MainActivity.class);
        String message = "Manage your party";
        startActivity(intent);
        finish();
        return;
    }

    public void goEvo(View view){
        Intent intent = new Intent(this, MainActivity.class);
        String message = "Manage your party";
        startActivity(intent);
        finish();
        return;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_interface, menu);
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
