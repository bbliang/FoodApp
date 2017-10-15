package com.hackgt17.foodapp;

import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class RecipeWebViewActivity extends AppCompatActivity {

    private WebView recipeWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_web_view);

        final Uri recipeUri = (Uri) getIntent().getParcelableExtra("recipeUri");

        recipeWebView = (WebView) findViewById(R.id.recipeWebView);
        recipeWebView.getSettings().setBuiltInZoomControls(true);
        recipeWebView.getSettings().setLoadWithOverviewMode(true);
        recipeWebView.getSettings().setJavaScriptEnabled(true);

        Log.d("Recipe URL", recipeUri.toString());
        recipeWebView.setWebViewClient(new WebViewClient() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                recipeWebView.loadUrl(request.getUrl().toString());
                return true;
            }
        });

        recipeWebView.loadUrl(recipeUri.toString());

    }
}
