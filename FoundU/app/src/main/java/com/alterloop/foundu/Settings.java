package com.alterloop.foundu;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.alterloop.util.Utility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class Settings extends ActionBarActivity {

    private static int RESULT_LOAD_IMAGE = 1;
    private String picturePath="";
    ImageView profilePic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Button buttonLoadImage = (Button) findViewById(R.id.buttonLoadPicture);
        buttonLoadImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                i.putExtra("outputX", 100);
                i.putExtra("outputY", 100);
                i.putExtra("aspectX", 1);
                i.putExtra("aspectY", 1);
                i.putExtra("scale", true);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        try {
            String img = Utility.readImageString(getApplicationContext());
            Bitmap myBitmap = this.ConvertToImage(img);
            if(!img.equals("null")) {
                profilePic = (ImageView) findViewById(R.id.imgView);

                profilePic.setImageBitmap(myBitmap);
            }
            String s="abc";
        }
        catch(Exception ex) {
            String b=ex.getMessage();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();

            ImageView imageView = (ImageView) findViewById(R.id.imgView);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }
    }

    public Bitmap ConvertToImage(String image) {
        try {
            //InputStream stream = new ByteArrayInputStream(image.getBytes());
            InputStream stream = new ByteArrayInputStream(Base64.decode(image.getBytes(), Base64.DEFAULT));
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_help) {
            startActivity(new Intent(getApplication(), Help.class));
            return true;
        } else if (id == R.id.action_notifications) {
            startActivity(new Intent(getApplication(), Notifications.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void pickProfilePic(View view) {

    }

    public void uploadProfilePic(View view) {
        if(!picturePath.equals("")) {
            Bitmap bitmapOrg = BitmapFactory.decodeFile(picturePath);
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 40, bao);
            byte[] ba = bao.toByteArray();
            String ba1 = Base64.encodeToString(ba, Base64.DEFAULT);
            Bitmap myBitmap = this.ConvertToImage(ba1);
            boolean flag = Utility.updateProfilePic(getApplicationContext(), ba1);
            Toast.makeText(getApplicationContext(), "Profile Image saved.", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(getApplicationContext(), "No image selected", Toast.LENGTH_SHORT).show();

    }
}