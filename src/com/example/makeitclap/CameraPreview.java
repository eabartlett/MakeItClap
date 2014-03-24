package com.example.makeitclap;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * TODO: document your custom view class.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    Size pSize;
    List<Size> supportedSizes;
    
    
    public CameraPreview(Context context, AttributeSet attrs) {
    	super(context, attrs);
    	Log.i("CameraPreview", "Camera was created");
        connectCamera();
    }
    
    public CameraPreview(Context context, AttributeSet attrs, int x){
    	super(context, attrs, x);
    	Log.i("CameraPreview", "Camera was created");
        connectCamera();
    }
    
    public CameraPreview(Context context) {
        super(context);
    	Log.i("CameraPreview", "Camera was created");
        connectCamera();
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(getContext().getString(R.string.app_name), "Error setting camera preview: " + e.getMessage());
        } catch (NullPointerException e) {
            Log.d(getContext().getString(R.string.app_name), "Error setting camera was null");

        	mCamera = Camera.open();
        }
    }


    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null){
          // preview surface does not exist
          return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
          // ignore: tried to stop a non-existent preview
        }
        
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        Log.i("Camera state", String.valueOf(mCamera == null));
        if(mCamera == null){
        	mCamera = Camera.open();
        }
        Camera.Parameters parameters = mCamera.getParameters();
        Camera.Size bestSize = getBestPreviewSize(w, h);
        parameters.setPreviewSize(bestSize.width, bestSize.height);
        requestLayout();
        mCamera.setParameters(parameters);

        // Important: Call startPreview() to start updating the preview surface.
        // Preview must be started before you can take a picture.
//        mCamera.startPreview();

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d(getContext().getString(R.string.app_name), "Error starting camera preview: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /** Method for getting the correct preview size for camera
     * found in answer to problem I was having at 
     * http://stackoverflow.com/questions/5802681/android-error-in-camera-surface
     * @param width
     * @param height
     * @return
     */
    private Camera.Size getBestPreviewSize(int width, int height)
    {
        Camera.Size result=null;    
        Camera.Parameters p = mCamera.getParameters();
        for (Camera.Size size : p.getSupportedPreviewSizes()) {
            if (size.width<=width && size.height<=height) {
                if (result==null) {
                    result=size;
                } else {
                    int resultArea=result.width*result.height;
                    int newArea=size.width*size.height;

                    if (newArea>resultArea) {
                        result=size;
                    }
                }
            }
        }
        return result;
    }
    
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        if (mCamera != null) {
            // Call stopPreview() to stop updating the preview surface.
            stopPreviewAndFreeCamera();
        }
    }

    /**
     * When this function returns, mCamera will be null.
     * Taken from Android Docs describing how to use the camera correctly
     */
    protected void stopPreviewAndFreeCamera() {

        if (mCamera != null) {
            // Call stopPreview() to stop updating the preview surface.
            mCamera.stopPreview();
        
            // Important: Call release() to release the camera for use by other
            // applications. Applications should release the camera immediately
            // during onPause() and re-open() it during onResume()).
            mCamera.release();        
            mCamera = null;
            Log.i("mCamera current state", String.valueOf(mCamera));

        }
    }
    
    /** Method that takes picture and displays preview on screen
     */
    public void takePicture(){
    	mCamera.takePicture(null, null, new Camera.PictureCallback() {
			
			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
				// TODO Send intent to go to send image
				
			}
		}, null);
    	mCamera.release();
    	mCamera = null;
    }
    
    /** Function returns whether mCamera has been successfully set
     * or not. Sets the camera's callback holder.
     * @return - true if successful, otherwise false
     */
    protected boolean connectCamera(){
    	try {
			mCamera = Camera.open();

			// Install a SurfaceHolder.Callback so we get notified when the
			// underlying surface is created and destroyed.
			mHolder = getHolder();
			mHolder.addCallback(this);
			// deprecated setting, but required on Android versions prior to 3.0
			mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			mCamera.setPreviewDisplay(mHolder);
			mCamera.startPreview();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
    	return true;
    }
}