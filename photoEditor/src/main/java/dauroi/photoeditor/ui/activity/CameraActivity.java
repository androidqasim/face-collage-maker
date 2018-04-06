package dauroi.photoeditor.ui.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import dauroi.photoeditor.R;
import dauroi.photoeditor.view.CameraPreview;

@SuppressLint("DefaultLocale")
public class CameraActivity extends Activity {
	private static final String TAG = CameraActivity.class.getSimpleName();
	public static final String EXTRA_FLIP_IMAGE = "flipImage";

	private ImageView mSwitchButton;
	private ImageView mFlashButton;
	private ImageView mCaptureButton;
	private View mProgressBar;
	private boolean mFrontFacingCamera = false;
	private String mFlashMode = Camera.Parameters.FLASH_MODE_AUTO;

	CameraPreview preview;
	Camera camera;
	Activity act;
	Context ctx;
	int cameraId = -1;
	int frontFacingCameraId = -1;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ctx = this;
		act = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.photo_editor_activity_camera);

		preview = new CameraPreview(this, (SurfaceView) findViewById(R.id.surfaceView));
		preview.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		((FrameLayout) findViewById(R.id.layout)).addView(preview);
		preview.setKeepScreenOn(true);

		mProgressBar = findViewById(R.id.progressBar);
		mSwitchButton = (ImageView) findViewById(R.id.switchCameraButton);
		mSwitchButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mFrontFacingCamera = !mFrontFacingCamera;
				if (mFrontFacingCamera) {
					mSwitchButton.setImageResource(R.drawable.photo_editor_camera_rotation_back);
					cameraId = frontFacingCameraId;
				} else {
					mSwitchButton.setImageResource(R.drawable.photo_editor_camera_rotation_front);
					cameraId = -1;
				}

				stopCamera();
				startCamera();
			}
		});

		mCaptureButton = (ImageView) findViewById(R.id.captureButton);
		mCaptureButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Camera.Parameters params = camera.getParameters();
				params.setFlashMode(mFlashMode);
				try {
					camera.setParameters(params);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				camera.takePicture(shutterCallback, rawCallback, jpegCallback);
			}
		});

		mFlashButton = (ImageView) findViewById(R.id.flashButton);
		mFlashButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mFlashMode.equalsIgnoreCase(Camera.Parameters.FLASH_MODE_OFF)) {
					mFlashMode = Camera.Parameters.FLASH_MODE_AUTO;
					mFlashButton.setImageResource(R.drawable.photo_editor_camera_flash_on);
				} else if (mFlashMode.equalsIgnoreCase(Camera.Parameters.FLASH_MODE_AUTO)) {
					mFlashMode = Camera.Parameters.FLASH_MODE_ON;
					mFlashButton.setImageResource(R.drawable.photo_editor_camera_flash_off);
				} else if (mFlashMode.equalsIgnoreCase(Camera.Parameters.FLASH_MODE_ON)) {
					mFlashMode = Camera.Parameters.FLASH_MODE_OFF;
					mFlashButton.setImageResource(R.drawable.photo_editor_camera_flash_auto);
				}
			}
		});

		frontFacingCameraId = findFrontFacingCamera();
	}

	@Override
	protected void onResume() {
		super.onResume();
		startCamera();
	}

	@Override
	protected void onPause() {
		stopCamera();
		super.onPause();
	}

	private void startCamera() {
		int numCams = Camera.getNumberOfCameras();
		if (numCams > 0) {
			try {
				if (cameraId == -1) {
					camera = Camera.open();
				} else {
					camera = Camera.open(cameraId);
				}

				camera.startPreview();
				preview.setCamera(camera);
			} catch (RuntimeException ex) {
				Toast.makeText(ctx, getString(R.string.photo_editor_camera_not_found), Toast.LENGTH_LONG).show();
			}
		}
	}

	private void stopCamera() {
		if (camera != null) {
			camera.stopPreview();
			preview.setCamera(null);
			camera.release();
			camera = null;
		}
	}

	private static int findFrontFacingCamera() {
		int cameraId = -1;
		// Search for the front facing camera
		int numberOfCameras = Camera.getNumberOfCameras();
		for (int i = 0; i < numberOfCameras; i++) {
			Camera.CameraInfo info = new Camera.CameraInfo();
			Camera.getCameraInfo(i, info);
			if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				cameraId = i;
				break;
			}
		}
		return cameraId;
	}

	private void resetCam() {
		camera.startPreview();
		preview.setCamera(camera);
	}

	private Uri refreshGallery(File file) {
		Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		Uri uri = Uri.fromFile(file);
		mediaScanIntent.setData(uri);
		sendBroadcast(mediaScanIntent);
		return uri;
	}

	ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
			// Log.d(TAG, "onShutter'd");
		}
	};

	PictureCallback rawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			// Log.d(TAG, "onPictureTaken - raw");
		}
	};

	PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			new SaveImageTask().execute(data);
			resetCam();
			Log.d(TAG, "onPictureTaken - jpeg");
		}
	};

	private class SaveImageTask extends AsyncTask<byte[], Void, Uri> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected Uri doInBackground(byte[]... data) {
			FileOutputStream outStream = null;

			// Write to SD Card
			try {
				File sdCard = Environment.getExternalStorageDirectory();
				File dir = new File(sdCard.getAbsolutePath() + "/DCIM");
				dir.mkdirs();

				String fileName = String.format("%d.jpg", System.currentTimeMillis());
				File outFile = new File(dir, fileName);

				outStream = new FileOutputStream(outFile);
				outStream.write(data[0]);
				outStream.flush();
				outStream.close();

				Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length + " to " + outFile.getAbsolutePath());

				return refreshGallery(outFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
			}
			return null;
		}

		@Override
		protected void onPostExecute(Uri result) {
			super.onPostExecute(result);
			mProgressBar.setVisibility(View.GONE);
			Intent data = new Intent();
			data.setData(result);
			data.putExtra(EXTRA_FLIP_IMAGE, mFrontFacingCamera);
			setResult(RESULT_OK, data);
			finish();
		}
	}
}
