package com.hackgt17.foodapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.hackgt17.foodapp.R.id.parent;

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

        //listview
        mListView = (ListView) findViewById(R.id.listOfIngredients);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        ingredients = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, ingredients);
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

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // change the checkbox state
                CheckedTextView checkedTextView = (CheckedTextView) view;
                checkedTextView.setChecked(!checkedTextView.isChecked());
            }
        });
    }

    private void launchCameraActivity() {
        Intent i = new Intent(this, CameraActivity.class);
        startActivityForResult(i, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (0) : {
                if (resultCode == Activity.RESULT_OK) {
                    String newIngredient = data.getStringExtra("Ingredient");
                    ingredients.add(newIngredient);
                    mListView.setItemChecked(ingredients.size() - 1, true);
                    Toast.makeText(this, "Added " + newIngredient, Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }



}
