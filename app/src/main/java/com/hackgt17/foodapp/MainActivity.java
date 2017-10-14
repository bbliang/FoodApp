package com.hackgt17.foodapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;

import com.hackgt17.foodapp.models.Food2ForkSearchAPI;
import com.hackgt17.foodapp.models.Recipe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.hackgt17.foodapp.R.id.parent;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> ingredients;
    private ArrayAdapter adapter;
    private ListView mListView;
    private LocalGetRecipesAPI localGetRecipesAPI;


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

    public void getRecipes() {

        SparseBooleanArray checked = mListView.getCheckedItemPositions();
        String ingredientsSelected = "";

        if (checked.size() == 0) {
            Toast.makeText(this, "Please select at least 1 ingredient", Toast.LENGTH_LONG).show();
            return;
        } else {
            for (int i = 0; i < checked.size(); i++) {
                if (checked.valueAt(i)) {
                    ingredientsSelected += ", " + ingredients.get(i);
                }
            }
            ingredientsSelected = ingredientsSelected.substring(2); // removes the first comma-space
        }

        localGetRecipesAPI = new LocalGetRecipesAPI(ingredientsSelected);
        localGetRecipesAPI.execute((Void) null);

    }

    class LocalGetRecipesAPI extends Food2ForkSearchAPI {

        // has "protected List<Recipe> recipeList = new ArrayList<>(); "
        // which we can access in this subclass

        public LocalGetRecipesAPI(String ingredientString) {
            super(ingredientString, getApplicationContext());
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Intent i = new Intent(getApplicationContext(), RecipeActivity.class);
                i.putExtra("recipeList", (Serializable) recipeList);
                // use getIntent().getSerializableExtra("recipeList") to get back that List

                startActivity(i);

            } else {
                Toast.makeText(getApplicationContext(), "Sorry, but there was an error with your request.", Toast.LENGTH_LONG).show();
            }
        }
    }

}
