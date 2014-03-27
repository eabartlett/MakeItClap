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
import android.widget.Button;

import be.hogent.tarsos.dsp.AudioEvent;
import be.hogent.tarsos.dsp.pitch.PitchDetectionHandler;
import be.hogent.tarsos.dsp.pitch.PitchDetectionResult;
import be.hogent.tarsos.dsp.pitch.PitchProcessor;

public class TakePhoto extends Activity implements AudioProcess.OnAudioEventListener, PitchDetectionHandler {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_take_photo);
		mAudioProcess = new AudioProcess(SAMPLE_RATE);
		mAudioProcess.setOnAudioEventListener(this);
		mPitchProcessor = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.MPM, 
				SAMPLE_RATE, mAudioProcess.getBufferSize(), this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.take_photo, menu);
		return true;
	}


	@Override
	public void handlePitch(final PitchDetectionResult pitchDetectionResult,
			 AudioEvent audioEvent) {
		mSampleNumber++;
		
		runOnUiThread(new Runnable() {
			@Override
			public void run(){
				if(pitchDetectionResult.getPitch() > 2400){
//					Log.i("Pitch detected", "Frequency: " + String.valueOf(pitchDetectionResult.getPitch()));
//					Log.i("Current sample number", String.valueOf(mSampleNumber));
					long dt = mSampleNumber - prevSampleNumber;
					if (dt > 100 && dt < 500) {
						mAudioProcess.stop();
						wasRecording = true;
						savePhoto();
					}
					prevSampleNumber = mSampleNumber;
				}
			}
			
			public void savePhoto(){
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
		});
	}

	@Override
	public void processAudioProcessEvent(AudioEvent e) {
		mPitchProcessor.process(e);
	}

	/** Method overriding onPause functionality for this activity
	 */
	@Override
	protected void onPause(){
		CameraPreview view = (CameraPreview) findViewById(R.id.camera_preview);
		view.stopPreviewAndFreeCamera();
		if(mAudioProcess.isRecording()){
			wasRecording = true;
			mAudioProcess.stop();
		}
		super.onPause();
	}

	/** MEthod overriding onResume functionality for this activity
	 */
	@Override
	protected void onResume(){
		Log.i("Activity State", "Going onResume");
		CameraPreview view = (CameraPreview) findViewById(R.id.camera_preview);
		view.connectCamera();
		if(wasRecording){
			mAudioProcess.listen();
		}
		super.onResume();
	}

	public void startRecording(View view){
		Button stopRec = (Button) findViewById(R.id.stop_recording);
		view.setVisibility(View.INVISIBLE);
		stopRec.setVisibility(View.VISIBLE);
		mAudioProcess.listen();


	}

	public void stopRecording(View view){
		Button startRec = (Button) findViewById(R.id.start_recording);
		view.setVisibility(View.INVISIBLE);
		startRec.setVisibility(View.VISIBLE);
		mAudioProcess.stop();
	}

	

	/* Constants used for audio processing */
	private final static int SAMPLE_RATE = 16000;
	private final static int LOW_FREQ = 2200;
	private final static int HIGH_FREQ = 2800;

	private PitchProcessor mPitchProcessor;
	private AudioProcess mAudioProcess;
	private boolean wasRecording = false;
	private long mSampleNumber = 0;
	private long prevSampleNumber = Integer.MIN_VALUE;


}