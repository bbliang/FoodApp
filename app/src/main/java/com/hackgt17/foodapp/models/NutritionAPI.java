package com.hackgt17.foodapp.models;

import android.content.Context;
import android.os.AsyncTask;

import com.hackgt17.foodapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
//import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NutritionAPI extends AsyncTask<Void, Void, Boolean> {

    private final String ingredient;
    private final Context context;
    protected Ingredient ingredientNutritionalInfo;

    public NutritionAPI(String food, Context context) {
        ingredient = food;
        this.context = context;
    }


    @Override
    protected Boolean doInBackground(Void... params) {

        System.out.println("--------1--------");
        URL url = null;
        try {
            url = new URL(context.getString(R.string.nutritionApiHttpPath));
        } catch (MalformedURLException e) {
            System.out.println("--- Error Here 1 ---");
            e.printStackTrace();
        }
        URLConnection con = null;
        try {
            con = url.openConnection();
        } catch (IOException e) {
            System.out.println("--- Error Here 2 ---");
            e.printStackTrace();
        }
        HttpURLConnection http = (HttpURLConnection) con;
        try {
            http.setRequestMethod("POST"); // PUT is another valid option
            System.out.println("--- Reached Here 3 ---");

        } catch (ProtocolException e) {
            e.printStackTrace();
        }

        http.setDoOutput(true);

        System.out.println("End of Part 1");

        Map<String, String> arguments = new HashMap<>();
        arguments.put("query", ingredient);
        arguments.put("use_branded_foods", "false");
        String result = "";
        for (Map.Entry<String, String> entry : arguments.entrySet()) {
            try {
                result += "&" + (URLEncoder.encode(entry.getKey(), "UTF-8") + "="
                        + URLEncoder.encode(entry.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                System.out.println("--- Error Here 4 ---");
                e.printStackTrace();
            }
        }
        result = result.substring(1);
        byte[] out = result.getBytes(StandardCharsets.UTF_8);
        int length = out.length;

        System.out.println("End of Part 2");

        http.setFixedLengthStreamingMode(length);
        http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        http.setRequestProperty("x-app-id",context.getString(R.string.nutritionApiID));
        http.setRequestProperty("x-app-key",context.getString(R.string.nutritionApiKey));
        try {
            http.connect();
        } catch (IOException e) {
            System.out.println("--- Error Here 5 ---");
            e.printStackTrace();
        }
        try {
            try (OutputStream os = http.getOutputStream()) {
                os.write(out);
            }
        } catch (IOException e) {
            System.out.println("--- Error Here 6 ---");
            e.printStackTrace();
        }

        System.out.println("End of Part 3");

        // Do something with http.getInputStream()

        BufferedInputStream bis = null;

        try {
            if (http.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
                bis = new BufferedInputStream(http.getInputStream());
                Map<String, List<String>> headerFields = http.getHeaderFields();
                //List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);
                System.out.println("Good response code (not an error code from backend.");

//                if (cookiesHeader != null) {
//                    for (String cookie : cookiesHeader) {
//                        cookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
//                    }
//                }
            } else {
                bis = new BufferedInputStream(http.getErrorStream());
                System.out.println("Bad response code (got an error code from backend.");
            }
        } catch (IOException e) {
            System.out.println("--- Error Here 7 ---");
            e.printStackTrace();
        }

        byte[] contents = new byte[1024];

        int bytesRead = 0;
        String response = "";
        try {
            while ((bytesRead = bis.read(contents)) != -1) {
                response += new String(contents, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("--------2--------");
        System.out.println(response);
        System.out.println("--------3--------");

        //System.out.println(response.contains(mUsername));
        //return (response.contains(mUsername) && !response.contains("Unauthorized"));
        boolean validData = response.contains("\"foods\"");
        // return that the response has at least 1 recipe because count != 0 but there is a count

        if (validData) {
            JSONObject json = null;
            try {

                json = new JSONObject(response);
                JSONArray jsonArray = json.getJSONArray("foods");

                JSONObject jsonFood = jsonArray.getJSONObject(0);
                extractNutrientInfo(jsonFood);

                return true;

            } catch (JSONException e) {
                System.out.println("Failed converting response to JSON!!!");
                e.printStackTrace();
            }

        } else {
            System.out.println("Request did not work.");

        }
        return validData;
    }

    private void extractNutrientInfo(JSONObject jsonFood) throws JSONException {

        String name = jsonFood.getString("food_name");
        int serving = jsonFood.getInt("serving_qty");
        String unit = jsonFood.getString("serving_unit");
        int weight = jsonFood.getInt("serving_weight_grams");
        double calories = jsonFood.getDouble("nf_calories");
        double totalFat = jsonFood.getDouble("nf_total_fat");
        double saturatedFat = jsonFood.getDouble("nf_saturated_fat");
        double cholesterol = jsonFood.getDouble("nf_cholesterol");
        double sodium = jsonFood.getDouble("nf_sodium");
        double carbohydrates = jsonFood.getDouble("nf_total_carbohydrate");
        double fiber = jsonFood.getDouble("nf_dietary_fiber");
        double sugar = jsonFood.getDouble("nf_sugars");
        double protein = jsonFood.getDouble("nf_protein");
        double potassium = jsonFood.getDouble("nf_potassium");

        ingredientNutritionalInfo = new Ingredient(name, serving, unit, weight, calories, totalFat,
                saturatedFat, cholesterol, sodium, carbohydrates, fiber, sugar, protein, potassium);
    }
}

