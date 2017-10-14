package com.hackgt17.foodapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hackgt17.foodapp.models.Recipe;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecipeActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    List<Recipe> recipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        RecyclerView rv = (RecyclerView)findViewById(R.id.rv);
        rv.setHasFixedSize(true);

        //wtf does context mean
        //TODO FIX THIS PARAMETER
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        RVAdapter adapter = new RVAdapter(recipes);
        rv.setAdapter(adapter);

        List<Recipe> recipes;
    }
}

class RVAdapter extends RecyclerView.Adapter<RVAdapter.RecipeViewHolder>{

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView title;
        TextView publisher;
        ImageView photo;

        RecipeViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            title = (TextView) itemView.findViewById(R.id.recipe_title);
            publisher = (TextView) itemView.findViewById(R.id.publisher_name);
            photo = (ImageView) itemView.findViewById(R.id.recipe_photo);
        }
    }

    List<Recipe> recipes;


    RVAdapter(List<Recipe> recipes){
        this.recipes = recipes;
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recipe_list_content, viewGroup, false);
        RecipeViewHolder rvh = new RecipeViewHolder(v);
        return rvh;
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder recipeViewHolder, int i) {
        recipeViewHolder.title.setText(recipes.get(i).getTitle());
        recipeViewHolder.publisher.setText(recipes.get(i).getPublisher());
        Picasso.with(recipeViewHolder.itemView.getContext()) //TODO EVALUATE
                .load(recipes.get(i).getImageUrl())
                .into(recipeViewHolder.photo);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}

