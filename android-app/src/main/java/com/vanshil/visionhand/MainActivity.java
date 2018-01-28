package com.vanshil.visionhand;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.florescu.android.rangeseekbar.RangeSeekBar;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.util.Random;

public class MainActivity  extends Activity implements CameraBridgeViewBase.CvCameraViewListener, View.OnTouchListener {
    static{
        System.loadLibrary("native-lib");
    }
    private static final String  TAG = "MainActivity";

    private static final boolean D = true;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_ENABLE_BT = 3;

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothChatService mChatService = null;

    private CustomizableCameraView mOpenCvCameraView;
    private VisionProcessor visionProcessor;

    private final Context context = this;

    private Preferences mPrefs;
    private int                  mGameWidth = 640;
    private int                  mGameHeight = 480;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");

                    /* Now enable camera view to start receiving frames */
                    mOpenCvCameraView.setOnTouchListener(MainActivity.this);

                    //mOpenCvCameraView.getCamera().getParameters().setExposureCompensation(Camera.Parameters.Mi);

                    mOpenCvCameraView.enableFpsMeter();
                    mOpenCvCameraView.enableView();

                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Looper.prepare();
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "Creating and setting view");
        mPrefs = new Preferences(this);
        mOpenCvCameraView = (CustomizableCameraView) findViewById(R.id.camera_view);
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        visionProcessor = new VisionProcessor(mPrefs);

        if (D)
            Log.e(TAG, "+++ ON CREATE +++");

        //setContentView(ll);

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available",
                    Toast.LENGTH_LONG).show();
            finish();
        }

        ActionStatus.getInstance().listener = new ActionStatus.Listener() {
            @Override
            public void onObjectAssigned(String object) {

            }
        };


    }


    @Override
    public void onPause()
    {
        super.onPause();
        if (D)
            Log.e(TAG, "- ON PAUSE -");

        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public synchronized  void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

        if (D)
            Log.e(TAG, "+ ON RESUME +");

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity
        // returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't
            // started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }

        Log.d("Firebase", "token "+ FirebaseInstanceId.getInstance().getToken());
    }

    @Override
    public void onStart() {
        super.onStart();
        if (D)
            Log.e(TAG, "++ ON START ++");

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else {
            if (mChatService == null)
                setupChat();
        }
    }


    private void setupChat() {
        Log.d(TAG, "setupChat()");

		/*uppButton = (Button) findViewById(R.id.button1);
		dwnButton = (Button) findViewById(R.id.button2);
		rytButton = (Button) findViewById(R.id.button3);
		lftButton = (Button) findViewById(R.id.button4);
		lineTrackButton = (Button) findViewById(R.id.button5);*/
/*
		uppButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Send a message using content of the edit text widget
				sendMessage("1");
			}
		});
		dwnButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Send a message using content of the edit text widget
				sendMessage("2");
			}
		});
		rytButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Send a message using content of the edit text widget
				sendMessage("3");
			}
		});
		lftButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Send a message using content of the edit text widget
				sendMessage("4");
			}
		});

		lineTrackButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Send a message using content of the edit text widget
				sendMessage("6");
			}
		});
*/
        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);

    }

    @Override
    public void onStop() {
        super.onStop();
        if (D)
            Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();

        // Stop the Bluetooth chat services
        if (mChatService != null)
            mChatService.stop();
        if (D)
            Log.e(TAG, "--- ON DESTROY ---");
    }

    private void ensureDiscoverable() {
        if (D)
            Log.d(TAG, "ensure discoverable");
        if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(
                    BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    /**
     * Sends a message.
     *
     * @param message
     *            A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Log.d(TAG, "BLUETOOTH NOT CONNECTED BUT TRIED TO SEND MESSAGE");
//         s
            return;
        }

        // Check that there's actually something to send
        if (D)
            Log.i(TAG, "Reached the message length");
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);
        }
    }
/*
	private final void setStatus(int resId) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			final ActionBar actionBar = getActionBar();
			actionBar.setSubtitle(resId);
		}
	}
	private final void setStatus(CharSequence subTitle) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			final ActionBar actionBar = getActionBar();
			actionBar.setSubtitle(subTitle);
		}
	}*/

    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if (D)
                        Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
				/*switch (msg.arg1) {
				case BluetoothChatService.STATE_CONNECTED:
					setStatus(getString(R.string.title_connected_to,
							mConnectedDeviceName));
					break;
				case BluetoothChatService.STATE_CONNECTING:
					setStatus(R.string.title_connecting);
					break;
				case BluetoothChatService.STATE_LISTEN:
				case BluetoothChatService.STATE_NONE:
					setStatus(R.string.title_not_connected);
					break;
				}*/
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(),
                            "Connected to " + mConnectedDeviceName,
                            Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(),
                            msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
                            .show();
                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (D)
            Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, "Bluetooth not enabled",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras().getString(
                DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.button, menu);
		return true;
	}
*/
	public void onBluetoothClicked(View view) {
		Intent serverIntent;// = null;
//		switch (item.getItemId()) {
//		case R.id.secure_connect_scan:
			// Launch the DeviceListActivity to see devices and do scan
        serverIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
        //return true;
//		case R.id.discoverable:
//			// Ensure this device is discoverable by others
//			ensureDiscoverable();
//			return true;
//		}
		//return false;
	}


    //~~~~~~~~~~~~~~~~~~~~~~~~CAMERA STUFF~~~~~~~~~~~~~~~~~~~~~

    public void onCameraViewStarted(int width, int height) {
        mGameWidth = width;
        mGameHeight = height;
        //visionProcessor.prepareGameSize(width, height);
    }

    public void onCameraViewStopped() {
    }
    char toSend = (char) 90;
    Random rand = new Random();
    public boolean onTouch(View view, MotionEvent event) {
        int xpos, ypos;

        xpos = (view.getWidth() - mGameWidth) / 2;
        xpos = (int)event.getX() - xpos;

        ypos = (view.getHeight() - mGameHeight) / 2;
        ypos = (int)event.getY() - ypos;

        if (xpos >=0 && xpos <= mGameWidth && ypos >=0  && ypos <= mGameHeight) {
            /* click is inside the picture. Deliver this event to processor */
            visionProcessor.deliverTouchEvent(xpos, ypos);
        }

        toSend -= 10;
        if((int)toSend < 0){
            toSend = 90;
        }

        return false;
    }

    public Mat onCameraFrame(Mat inputFrame) {
	    sendMessage(((int)ActionStatus.getInstance().getTheta() + ""));
        return visionProcessor.process(inputFrame);

    }

    //~~~~~~~~~~~~~~~~~~~~~~~~HSV THRESHOLD SLIDER~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public void openBottomSheet(View v) {
        View view = getLayoutInflater().inflate(R.layout.hsv_bottom_sheet, null);
        LinearLayout container = (LinearLayout) view.findViewById(R.id.popup_window);
        container.getBackground().setAlpha(20);


        final Dialog mBottomSheetDialog = new Dialog(context);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();

        final RangeSeekBar hSeekBar = (RangeSeekBar) view.findViewById(R.id.hSeekBar);
        setSeekBar(hSeekBar, getHRange());
        hSeekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> rangeSeekBar, Integer min, Integer max) {
                Log.i("H", min + " " + max);
                mPrefs.setThresholdHRange(min, max);
            }
        });

        final RangeSeekBar sSeekBar = (RangeSeekBar) view.findViewById(R.id.sSeekBar);
        setSeekBar(sSeekBar, getSRange());
        sSeekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> rangeSeekBar, Integer min, Integer max) {
                Log.i("S", min + " " + max);
                mPrefs.setThresholdSRange(min, max);
            }
        });

        final RangeSeekBar vSeekBar = (RangeSeekBar) view.findViewById(R.id.vSeekBar);
        setSeekBar(vSeekBar, getVRange());
        vSeekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> rangeSeekBar, Integer min, Integer max) {
                Log.i("V", min + " " + max);
                mPrefs.setThresholdVRange(min, max);
            }
        });

        Button restoreButton = (Button) view.findViewById(R.id.restoreDefaultsButton);
        restoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPrefs.restoreDefaults();
                setSeekBar(hSeekBar, getHRange());
                setSeekBar(sSeekBar, getSRange());
                setSeekBar(vSeekBar, getVRange());
            }
        });
    }
    private static void setSeekBar(RangeSeekBar<Integer> bar, Pair<Integer, Integer> values) {
        bar.setSelectedMinValue(values.first);
        bar.setSelectedMaxValue(values.second);
    }

    public Pair<Integer, Integer> getHRange() {
        return mPrefs.getThresholdHRange();
    }

    public Pair<Integer, Integer> getSRange() {
        return mPrefs.getThresholdSRange();
    }

    public Pair<Integer, Integer> getVRange() {
        return mPrefs.getThresholdVRange();
    }
}
