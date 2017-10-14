package com.hackgt17.foodapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> ingredients;
    private ArrayAdapter adapter;
    private ListView mListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //listview shit
        mListView = (ListView) findViewById(R.id.list);
        ingredients = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ingredients);
        mListView.setAdapter(adapter);
        adapter.setNotifyOnChange(true);

        FloatingActionButton camera = (FloatingActionButton) findViewById(R.id.cameraIcon);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                launchCameraActivity();

//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });
    }

    private void launchCameraActivity() {
        Intent i = new Intent(this, CameraActivity.class);
        startActivityForResult(i, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (0) : {
                if (resultCode == Activity.RESULT_OK) {
                    String newIngredient = data.getStringExtra("Ingredient");
                    ingredients.add(newIngredient);
                    //update listview
                    ((ArrayAdapter)mListView.getAdapter()).notifyDataSetChanged();
                    Toast.makeText(this, "Added " + newIngredient, Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }



}
