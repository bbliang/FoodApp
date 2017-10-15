package com.hackgt17.foodapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;

import com.github.ag.floatingactionmenu.OptionsFabLayout;
import com.hackgt17.foodapp.models.Food2ForkSearchAPI;
import com.hackgt17.foodapp.models.Recipe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.hackgt17.foodapp.R.id.parent;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> ingredients;
    private ArrayList<Boolean> chosenIngredients;
    private ArrayAdapter adapter;
    private ListView mListView;
    private LocalGetRecipesAPI localGetRecipesAPI;
    private OptionsFabLayout fabWithOptions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //listview
        mListView = (ListView) findViewById(R.id.listOfIngredients);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        chosenIngredients = new ArrayList<>();
        ingredients = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, ingredients);
        mListView.setAdapter(adapter);
        adapter.setNotifyOnChange(true);

        fabWithOptions = (OptionsFabLayout) findViewById(R.id.fab_l);
        //Set mini fab's colors.
        fabWithOptions.setMiniFabsColors(
                R.color.colorPrimary,
                R.color.green_fab);

        //Set main fab clicklistener.
        fabWithOptions.setMainFabOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fabWithOptions.isOptionsMenuOpened()) {
                    fabWithOptions.closeOptionsMenu();
                }
            }
        });

        //Set mini fabs clicklisteners.
        fabWithOptions.setMiniFabSelectedListener(new OptionsFabLayout.OnMiniFabSelectedListener() {
            @Override
            public void onMiniFabSelected(MenuItem fabItem) {
                switch (fabItem.getItemId()) {
                    case R.id.fab_cam:
                        launchCameraActivity();
                        break;
                    case R.id.fab_text:
                        Toast.makeText(getApplicationContext(),
                                fabItem.getTitle() + "clicked!",
                                Toast.LENGTH_SHORT).show();
                    default:
                        break;
                }
            }
        });

/*
        FloatingActionButton camera = (FloatingActionButton) findViewById(R.id.cameraIcon);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                launchCameraActivity();

//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });
*/
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // change the checkbox state

                CheckedTextView checkedTextView = (CheckedTextView) view;
                if(chosenIngredients.get(position)) {
                    chosenIngredients.add(position, Boolean.FALSE);
                } else {
                    chosenIngredients.add(position, Boolean.TRUE);
                }
                // remove value that used to be there at index = position, which is now toggled
                chosenIngredients.remove(position + 1);

                checkedTextView.setChecked(chosenIngredients.get(position));

                //checkedTextView.setChecked(checkedTextView.isChecked());
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
                    addIngredientToList(newIngredient);
                }
                break;
            }
        }
    }


    private void addIngredientToList(String newIngredient) {
        ingredients.add(newIngredient);
        mListView.setItemChecked(ingredients.size() - 1, true);
        chosenIngredients.add(true);

        ((ArrayAdapter)mListView.getAdapter()).notifyDataSetChanged();
        Toast.makeText(this, "Added " + newIngredient, Toast.LENGTH_LONG).show();
    }

    public void getRecipes(View view) {

        HashSet<String> includedFood = new HashSet<>();
        String ingredientsSelected = "";

        if (!chosenIngredients.contains((Boolean.TRUE))) {
            Toast.makeText(this, "Please select at least 1 ingredient", Toast.LENGTH_LONG).show();
            return;
        } else {
            for (int i = 0; i < chosenIngredients.size(); i++) {
                if (chosenIngredients.get(i) && !includedFood.contains(ingredients.get(i))) {
                    // if that index is set to true and that food is not a duplicate
                    ingredientsSelected += ", " + ingredients.get(i);
                    includedFood.add(ingredients.get(i));
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
//                for (Recipe r : recipeList) {
//                    Log.d("Recipe Info:", r.toString());
//                }

            } else {
                Toast.makeText(getApplicationContext(), "Sorry, but there was an error with your request.", Toast.LENGTH_LONG).show();
            }
        }
    }

}
