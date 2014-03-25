package com.example.makeitclap;

import android.hardware.Camera;
import android.os.Bundle;
import android.app.Activity;
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
		// TO-DO write code to save current preview and prepare to send
	}

}
