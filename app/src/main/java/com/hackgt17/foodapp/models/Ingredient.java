package com.hackgt17.foodapp.models;

import java.io.Serializable;

/**
 * Example of relevant part of return JSON
 * There is more to the returned JSON that I am ignoring
 *
 * "food_name": "apple pie",
 * "brand_name": null,
 * "serving_qty": 1,
 * "serving_unit": "piece (1/8 of 9\" dia)",
 * "serving_weight_grams": 125,
 * "nf_calories": 296.25,
 * "nf_total_fat": 13.75,
 * "nf_saturated_fat": 4.75,
 * "nf_cholesterol": 0,
 * "nf_sodium": 251.25,
 * "nf_total_carbohydrate": 42.5,
 * "nf_dietary_fiber": 2,
 * "nf_sugars": 19.56,
 * "nf_protein": 2.38,
 * "nf_potassium": 81.25,
 *
 *
 */

public class Ingredient implements Serializable {

    private String name;
    private int servingQuantity;
    private String servingUnit;
    private int servingWeightGrams;
    private double calories;
    private double totalFat;
    private double saturatedFat;
    private double cholesterol;
    private double sodium;
    private double carbohydrates;
    private double fiber;
    private double sugar;
    private double protein;
    private double potassium;

    public Ingredient(String name, int servingQuantity, String servingUnit, int servingWeightGrams,
                      double calories, double totalFat, double saturatedFat, double cholesterol,
                      double sodium, double carbohydrates, double fiber, double sugar,
                      double protein, double potassium) {
        this.name = name;
        this.servingQuantity = servingQuantity;
        this.servingUnit = servingUnit;
        this.servingWeightGrams = servingWeightGrams;
        this.calories = calories;
        this.totalFat = totalFat;
        this.saturatedFat = saturatedFat;
        this.cholesterol = cholesterol;
        this.sodium = sodium;
        this.carbohydrates = carbohydrates;
        this.fiber = fiber;
        this.sugar = sugar;
        this.protein = protein;
        this.potassium = potassium;
    }

    public String getName() {
        return name;
    }

    public int getServingQuantity() {
        return servingQuantity;
    }

    public String getServingUnit() {
        return servingUnit;
    }

    public int getServingWeightGrams() {
        return servingWeightGrams;
    }

    public double getCalories() {
        return calories;
    }

    public double getTotalFat() {
        return totalFat;
    }

    public double getSaturatedFat() {
        return saturatedFat;
    }

    public double getCholesterol() {
        return cholesterol;
    }

    public double getSodium() {
        return sodium;
    }

    public double getCarbohydrates() {
        return carbohydrates;
    }

    public double getFiber() {
        return fiber;
    }

    public double getSugar() {
        return sugar;
    }

    public double getProtein() {
        return protein;
    }

    public double getPotassium() {
        return potassium;
    }


    @Override
    public String toString() {
        return "Calories: " + calories + "\nTotal Fat: " + totalFat + "\nSugar: " + sugar
                + "\nProtein: " + protein;
    }
}
