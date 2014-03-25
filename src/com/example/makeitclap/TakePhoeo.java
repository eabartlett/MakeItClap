package com.example.makeitclap;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.makeitclap.CameraPreview;
import android.os.Bundle;
import android.os.Environment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class TakePhoeo extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_take_phoeo);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.take_phoeo, menu);
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
		super.onResume();
		Log.i("Activity State", "Going onResume");
		CameraPreview view = (CameraPreview) findViewById(R.id.camera_preview);
		view.connectCamera();
	}

	public void startRecording(View view){
		CameraPreview preview = (CameraPreview) findViewById(R.id.camera_preview);
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

	private void savePhoto(){
		// TODO write code to save current preview and prepare to send
		File image;
		if(isExternalStorageWritable()){
			//TODO write photo to 
		}

	}

	/* Create file name/path for photo taken to be saved to
	 * and throw an IOException if there is a collision
	 */
	@SuppressLint("SimpleDateFormat")
	private File createImageFile() throws IOException{
		//create image file
		String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
		String fileName = "JPEG_" + timeStamp + "_";
		File storageDir = Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES);
		File image = File.createTempFile(fileName, ".jpg", storageDir);

		return image;
	}
	
	/* Checks if external storage is available for read and write 
	 * From Android API docs on saving files 
	 */
	public boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}
}
