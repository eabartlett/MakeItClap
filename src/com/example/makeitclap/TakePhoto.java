package com.example.makeitclap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

public class TakePhoto extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_take_photo);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.take_photo, menu);
		return true;
	}


	/** Method overriding onPause functionality for this activity
	 */
	@Override
	protected void onPause(){
		CameraPreview view = (CameraPreview) findViewById(R.id.camera_preview);
		view.stopPreviewAndFreeCamera();
		super.onPause();
	}

	/** MEthod overriding onResume functionality for this activity
	 */
	@Override
	protected void onResume(){
		Log.i("Activity State", "Going onResume");
		CameraPreview view = (CameraPreview) findViewById(R.id.camera_preview);
		view.connectCamera();
		super.onResume();
	}

	public void startRecording(View view){
		Button stopRec = (Button) findViewById(R.id.stop_recording);
		view.setVisibility(View.INVISIBLE);
		stopRec.setVisibility(View.VISIBLE);
		//do recording stuff


	}

	public void stopRecording(View view){
		Button startRec = (Button) findViewById(R.id.start_recording);
		view.setVisibility(View.INVISIBLE);
		startRec.setVisibility(View.VISIBLE);
		//do the recording stuff
	}

	public void savePhoto(View view){
		Camera.PictureCallback callback = new Camera.PictureCallback() {
			
			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
				Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
				try {
					/* Writing file to memory for sending, used from Stack Overflow
					 * @ http://stackoverflow.com/questions/11274715/save-bitmap-to-file-function
					 */
					File image = createImageFile();
					FileOutputStream fOut = new FileOutputStream(image);
					bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
					fOut.flush();
					fOut.close();
					
					Intent photoIntent = new Intent(TakePhoto.this, SendPicture.class);
					photoIntent.putExtra("path", image.getAbsolutePath());
					startActivity(photoIntent);
					// TODO Pass intent to new activity to send the photo or go back to camera
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			/* Create file name/path for photo taken to be saved to
			 * and throw an IOException if there is a collision
			 */
			@SuppressLint("SimpleDateFormat")
			private File createImageFile() throws IOException{
				//create image file
				String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
				String fileName = "PNG_" + timeStamp + "_";
				File storageDir = Environment.getExternalStoragePublicDirectory(
						Environment.DIRECTORY_PICTURES);
				File image = File.createTempFile(fileName, ".jpg", storageDir);
				return image;
			}
		};
		CameraPreview preview = (CameraPreview) findViewById(R.id.camera_preview);
		preview.takePicture(callback);
	}


}