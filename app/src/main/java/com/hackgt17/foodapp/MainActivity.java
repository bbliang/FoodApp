package com.hackgt17.foodapp;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.hackgt17.foodapp.helpers.NetworkHelper;
import com.hackgt17.foodapp.helpers.RequestPackage;
import com.hackgt17.foodapp.models.Prediction;
import com.hackgt17.foodapp.models.CustomData;
import com.hackgt17.foodapp.services.CustomService;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_CLICK_REQUEST_CODE = 3;
    private static final int GALLERY_CLICK_REQUEST_CODE = 4;
    private boolean networkOn;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int SELECT_PICTURE = 2;

    TextView resultTV;
    ImageButton photoButton, urlButton, nutritionButton, galleryButton, cropButton;
    EditText urlText;
    CropImageView image;
    Bitmap bitmap;
    Drawable d;
    Prediction food_result;
    LocalBroadcastManager broadcastManager;

    public final String URL = "url";
    public final String IMAGE = "image";
    public static final String TAG = "CUSTOM_LOGGER";
    /**
     * Connection point to the Microsoft Custom Vision Service
     */
    private final String ENDPOINT = "https://southcentralus.api.cognitive.microsoft.com/customvision/v1.0/Prediction/b2c5ee1f-815d-4b94-84a5-597f51ad5bc7/%s";
    public static final String CUSTOM_REQUEST = "CUSTOM_REQUEST";
    private Activity thisActivity;


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            Log.d("Checking", "Reached Here 1");
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(this, "There was an error in dispatchTakePictureIntent", Toast.LENGTH_LONG).show();
            }
            Log.d("Checking", "Reached Here 2");
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Log.d("Checking", "Reached Here 2.1");
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                Log.d("Checking", "Reached Here 2.2");
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                Log.d("Checking", "Reached Here 3");
                resultTV.setVisibility(View.GONE);

                Log.d("Checking", "Reached Here 4");
                //String fileImagePath = photoURI.getPath();

                /*

                Bitmap imageBitmap = null;
                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoURI);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("Bitmap URI error", "Error getting image from URI");
                }
                image.setImageBitmap(imageBitmap);


                 */

                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

            }
            Log.d("Checking", "Reached Here 5");
        }
    }



    //*****************************************************************************************

    String mCurrentPhotoPath = "";

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_FoodApp";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }



    //****************************************************************************************

    private BroadcastReceiver customReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (intent.getExtras().containsKey(CustomService.CUSTOM_SERVICE_ERROR)) {
                        String msg = intent.getStringExtra(CustomService.CUSTOM_SERVICE_ERROR);
                        resultTV.setText(msg);
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    } else if (intent.getExtras().containsKey(CustomService.CUSTOM_SERVICE_PAYLOAD)) {
                        CustomData customData = (CustomData) intent
                                .getParcelableExtra(CustomService.CUSTOM_SERVICE_PAYLOAD);
                        food_result = customData.getPredictions().get(0);
                        clearText();
                        String msg = String.format("I'm %.0f%% confident that this is a %s \n", food_result.getProbability() * 100, food_result.getClass_());
                        resultTV.append(msg);

                        for (int i = 0; i < customData.getPredictions().size(); i++) {
                            Log.i(TAG, "onReceive: " + customData.getPredictions().get(i).getClass_());
                        }
                    }
                }
            });

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        thisActivity = this;

        broadcastManager = LocalBroadcastManager.getInstance(this);
        networkOn = NetworkHelper.hasNetworkAccess(this);

        image = (CropImageView) findViewById(R.id.imageView);
        try {
            d = Drawable.createFromStream(getAssets().open("apple.jpg"), null);
            image.setImageBitmap(((BitmapDrawable) d).getBitmap());
            bitmap = ((BitmapDrawable) d).getBitmap();
        } catch (IOException e) {
            e.printStackTrace();
        }
        resultTV = (TextView) findViewById(R.id.resultText);
        photoButton = (ImageButton) findViewById(R.id.photoButon);
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                        (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                    ActivityCompat.requestPermissions(thisActivity,
                            new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_CLICK_REQUEST_CODE);
                } else {
                    resultTV.setVisibility(View.GONE);
                    takePhoto();
                }
            }
        });
        cropButton = (ImageButton) findViewById(R.id.useCrop);
        cropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCrop(v);
            }
        });
        urlButton = (ImageButton) findViewById(R.id.urlButton);
        urlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultTV.setVisibility(View.GONE);
                openUrl();
            }
        });
        galleryButton = (ImageButton) findViewById(R.id.galleryButton);
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                        (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                    ActivityCompat.requestPermissions(thisActivity,
                            new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, GALLERY_CLICK_REQUEST_CODE);
                } else {
                    resultTV.setVisibility(View.GONE);
                    openGallery();
                }
            }
        });
        urlText = (EditText) findViewById(R.id.urlText);
        urlText.setText("https://upload.wikimedia.org/wikipedia/commons/thumb/8/8a/Banana-Single.jpg/220px-Banana-Single.jpg");

        urlText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    new DownloadImageTask().execute(urlText.getText().toString());
                    return true;
                }
                return false;
            }
        });

        broadcastManager.registerReceiver(customReceiver, new IntentFilter(CustomService.CUSTOM_SERVICE_NAME));
    }

    private void clearText() {
        resultTV.setText("");
    }

    private void progressLoader() {
        resultTV.setVisibility(View.VISIBLE);
        resultTV.setText("Thinking...");
    }

    public void openUrl() {
        clearText();
        if (networkOn) {
            if (!urlText.getText().toString().equals("")) {
                progressLoader();
                requestCustomService(URL);
            } else {
                Toast.makeText(this, "Please enter url into text box above.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Network not available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        broadcastManager.unregisterReceiver(customReceiver);
    }

    /**
     * Send a request to CVS API to process image recognition
     * @param type two input options: picture through url or image
     */
    private void requestCustomService(final String type) {

        final Bitmap croppedImage = image.getCroppedImage();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                RequestPackage requestPackage = new RequestPackage();
                Intent intent = new Intent(MainActivity.this, CustomService.class);
                requestPackage.setParam(CUSTOM_REQUEST, "CUSTOM");

                if (type.equals(URL)) {
                    requestPackage.setEndPoint(String.format(ENDPOINT, URL));
                    requestPackage.setParam("Url", urlText.getText().toString());
                } else if (type.equals(IMAGE)) {
                    //CVS only accepts images as byte arrays
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    croppedImage.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                    byte[] byteArray = stream.toByteArray();
                    Log.d(TAG, "requestCustomService: byte array size = " + byteArray.length);
                    requestPackage.setEndPoint(String.format(ENDPOINT, IMAGE));
                    intent.putExtra(CustomService.REQUEST_IMAGE, byteArray);
                }

                requestPackage.setMethod("POST");
                intent.putExtra(CustomService.REQUEST_PACKAGE, requestPackage);

                try {
                    startService(intent);
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            resultTV.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Image too large.", Toast.LENGTH_LONG).show();
                        }
                    });

                    e.printStackTrace();
                }
            }
        });


    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        public DownloadImageTask() {
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            image.setImageBitmap(result);
        }
    }

    public void takePhoto() {
        clearText();
        if (networkOn) {
            if (Build.MODEL.contains("x86")) {
                requestCustomService(IMAGE);
                progressLoader();
            } else {
                dispatchTakePictureIntent();
            }
        } else {
            Toast.makeText(this, "Network not available", Toast.LENGTH_SHORT).show();
        }
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();

            image.setImageUriAsync(data.getData());
            bitmap = image.getCroppedImage();
//            bitmap = (Bitmap) extras.get("data");
//            image.setImageBitmap(bitmap);
        } else if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK) {
            if (data == null) {
                Toast.makeText(this, "Error Selecting Image", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(data.getData());
                if (inputStream != null) {
                    image.setImageUriAsync(data.getData());
                    bitmap = image.getCroppedImage();
//                    bitmap = BitmapFactory.decodeStream(new BufferedInputStream(inputStream));
//                    image.setImageBitmap(bitmap);
                } else {
                    Toast.makeText(this, "Error Selecting Image", Toast.LENGTH_SHORT).show();
                }
            } catch (FileNotFoundException e) {
                Toast.makeText(this, "Error Selecting Image", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == CAMERA_CLICK_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                takePhoto();
            }
        } else if (requestCode == GALLERY_CLICK_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            }
        }

    }

    public void getCrop(View v) {
        requestCustomService(IMAGE);
        progressLoader();
    }

    public void openGallery() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select An Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, SELECT_PICTURE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
