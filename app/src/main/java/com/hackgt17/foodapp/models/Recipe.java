package com.hackgt17.foodapp.models;

import java.io.Serializable;

public class Recipe implements Serializable {

    /* Example of returned json within a recipe object

        "publisher": "All Recipes",
        "f2f_url": "http://food2fork.com/view/c50b36",
        "title": "Colorful Garlic Orzo",
        "source_url": "http://allrecipes.com/Recipe/Colorful-Garlic-Orzo/Detail.aspx",
        "recipe_id": "c50b36",
        "image_url": "http://static.food2fork.com/952851d33a.jpg",
        "social_rank": 38.59859078491047,
        "publisher_url": "http://allrecipes.com"

    */

    private final String publisher;
    private final String f2fUrl;
    private final String title;
    private final String sourceUrl;
    private final String recipeId;
    private final String imageUrl;
    private final double socialRank;
    private final String publisherUrl;

    public Recipe(String pub, String f2f, String recipe_title, String source, String id,
                    String image, double rank, String pub_url) {
        publisher = pub;
        f2fUrl = f2f;
        title = recipe_title;
        sourceUrl = source;
        recipeId = id;
        imageUrl = image;
        socialRank = rank;
        publisherUrl = pub_url;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getF2fUrl() {
        return f2fUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public double getSocialRank() {
        return socialRank;
    }

    public String getPublisherUrl() {
        return publisherUrl;
    }

    public String toString() {
        return ("Publisher: " + publisher + "\nTitle: " + title + "\nSource: " + sourceUrl + "\n");
    }


}
