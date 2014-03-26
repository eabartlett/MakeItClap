package com.example.makeitclap;

import java.io.File;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.ShareActionProvider;

public class SendPicture extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_picture);
		Intent imageIntent = getIntent();
		uriPath = imageIntent.getStringExtra("path");
		Bitmap bmp = BitmapFactory.decodeFile(uriPath);
		ImageView image = (ImageView) findViewById(R.id.photo);
		image.setImageBitmap(bmp);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.send_picture, menu);
		return true;
	}
	
	public void sharePhoto(View view){
		Intent shareIntent = new Intent();
		File img = new File(uriPath);
		shareIntent.setAction(Intent.ACTION_SEND);
		Uri uri = Uri.fromFile(img);
		Log.i("URI for photo", uri.toString());
		shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
		shareIntent.setType("image/jpeg");
		startActivity(Intent.createChooser(shareIntent, "Share your Photo"));
	}
	
	/* Path to image to be shared */
	String uriPath;
}
