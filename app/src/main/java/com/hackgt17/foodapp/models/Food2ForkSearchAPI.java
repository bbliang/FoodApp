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

public class Food2ForkSearchAPI extends AsyncTask<Void, Void, Boolean> {

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    //public static java.net.CookieManager cookieManager = new java.net.CookieManager();

    private final String ingredients;
    private final Context context;
    protected List<Recipe> recipeList = new ArrayList<>();

    public Food2ForkSearchAPI(String food, Context context) {
        ingredients = food;
        this.context = context;
    }


    @Override
    protected Boolean doInBackground(Void... params) {

        System.out.println("--------1--------");
        URL url = null;
        try {
            url = new URL(context.getString(R.string.apiHttpPath));
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
        arguments.put("key", context.getString(R.string.apiKey));
        arguments.put("q", ingredients);
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
        boolean validData = response.contains("\"count\"");
        // return that the response has at least 1 recipe because count != 0 but there is a count

        if (validData) {
            JSONObject json = null;
            try {

                json = new JSONObject(response);
                JSONArray jsonArray = json.getJSONArray("recipes");

                for (int i = 0; i < json.getInt("count"); i++) {
                    JSONObject jsonRecipe = jsonArray.getJSONObject(i);
                    saveRecipeToList(jsonRecipe);
                }

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

    private void saveRecipeToList(JSONObject jsonRecipe) throws JSONException {
        String publisher = jsonRecipe.getString("publisher");
        String f2fUrl = jsonRecipe.getString("f2f_url");
        String title = jsonRecipe.getString("title");
        String sourceUrl = jsonRecipe.getString("source_url");
        String recipeId = jsonRecipe.getString("recipe_id");
        String imageUrl = jsonRecipe.getString("image_url");
        double socialRank = jsonRecipe.getDouble("social_rank");
        String publisherUrl = jsonRecipe.getString("publisher_url");

        Recipe myRecipe = new Recipe(publisher, f2fUrl, title, sourceUrl, recipeId,
                imageUrl, socialRank, publisherUrl);

        recipeList.add(myRecipe);
    }
}

