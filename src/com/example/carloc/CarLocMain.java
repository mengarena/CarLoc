package com.example.carloc;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.os.Build;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.*;
import android.content.pm.*;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Environment;
import android.os.Looper;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.Settings;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Toast;
import android.view.KeyEvent;
import android.view.View;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.*;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.GeomagneticField;
import android.util.*;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.wifi.*;
import android.content.BroadcastReceiver;
import android.location.*;
import android.app.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.ClientProtocolException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import android.os.StrictMode;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

import com.androidplot.util.PlotStatistics;
import com.androidplot.xy.*;
 
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Arrays;

public class CarLocMain extends Activity implements SensorEventListener {

	/////////////////////////////////////////////////////////////////////////////////
	private static final String TAG = "CarLoc";
			
	private static final double m_nGpsLat[] = {40.098373,       40.09831, 40.09827, 40.098244, 40.1011456953, 40.1042637278,  40.106003, 40.1089211577, 40.1103516000};
	private static final double m_nGpsLong[] = {-88.18974, -88.2097143548,-88.2142,-88.21881, -88.2190700000, -88.2191562485, -88.2200018935, -88.2238844511, -88.2289956000};
	
	private static final int POS_NUM = 9;
	private static int m_nPosIdx = 0;
	
	//private static String m_sGpsFilePath = "D:\\ZenDrive\\Data2015\\testGps.csv";
	
	private static String m_sGpsFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "CarLoc" + File.separator + "LocResult.csv";

	private static double m_fPreviousTime = 0;
	private static String m_sLine = "";
	
	static FileReader m_fr = null;
	static BufferedReader m_br = null;
	
	private static int PLAYSPEED = 2;
	private static Timer mTimer;
	private static TimerTask mDoAsyncTask;
	
	//Sensor data type
	private static final int DATA_TYPE_SENSOR = 1;
	private static final int DATA_TYPE_MIC = 2;
	private static final int DATA_TYPE_CELLULAR = 3;
	private static final int DATA_TYPE_GPS = 4;
	private static final int DATA_TYPE_WIFI = 5;

	//Sensor event type
	private static int SENSOR_EVENT_NULL = 0;
	private static int SENSOR_EVENT_ACCL = 1;
	private static int SENSOR_EVENT_LINEAR_ACCL = 2;
	private static int SENSOR_EVENT_GYRO = 3;
	private static int SENSOR_EVENT_ORIENT = 4;
	private static int SENSOR_EVENT_MAGNET = 5;
	private static int SENSOR_EVENT_LIGHT = 6;
	private static int SENSOR_EVENT_BAROMETER = 7;
	private static int SENSOR_EVENT_MIC = 8;
	private static int SENSOR_EVENT_CELLULAR = 9;
	private static int SENSOR_EVENT_GPS = 10;
	private static int SENSOR_EVENT_WIFI = 11;
	private static int SENSOR_EVENT_GRAVITY = 12;
	
	private static int SENSOR_DELAY_MODE = SensorManager.SENSOR_DELAY_FASTEST;
	
	private CarLocMain m_actHome = this;
	private SensorManager m_smCarLoc = null;
	private LocationManager m_locManager = null;
	private TelephonyManager m_tmCellular = null;
	private GsmCellLocation m_GsmCellLocation = null;
	private CdmaCellLocation m_CdmaCellLocation = null;

	private String m_sGPSProvider = LocationManager.GPS_PROVIDER; //GPS provider

	private CheckBox m_chkTurn, m_chkCurve, m_chkStop, m_chkBaro, m_chkWiFi, m_chkCell;
	
	private Button m_btnNext;
	private Button m_btnBack;
	
	private MapFragment mMapFragment;
	private GoogleMap mGoogleMap = null;
	private LatLngBounds AUSTRALIA = new LatLngBounds(new LatLng(-44, 113), new LatLng(-10, 154));
	private static final LatLng UIUC = new LatLng(40.0983730000,-88.1897400000);
	
	/* Sensor is available or not */
	private static boolean m_blnOrientPresent = false;
	private static boolean m_blnGyroPresent = false;
	private static boolean m_blnAcclPresent = false;
	private static boolean m_blnLinearAcclPresent = false;
	private static boolean m_blnGravityPresent = false;
	private static boolean m_blnLightPresent = false;
	private static boolean m_blnBarometerPresent = false;
	private static boolean m_blnMagnetPresent = false;
	
	//Signatures
	private static boolean m_blnTurnEnabled = false;
	private static boolean m_blnCurveEnabled = false;
	private static boolean m_blnStopEnabled = false;
	private static boolean m_blnBaroEnabled = false;
	private static boolean m_blnWiFiEnabled = false;
	private static boolean m_blnCellEnabled = false;
	
	private boolean m_blnWifiSignalEnabled = false; // true: Wifi signal is enabled
	private boolean m_blnGPSSignalEnabled = false; // true: GPS signal is enabled

	private WifiManager m_mainWifi = null;
	private WifiReceiver m_receiverWifi = null;
	List<ScanResult> m_lstWifiList; // Wifi List

	//For Sensor working after screen off
	private WakeLock m_wakeLock;
	private ResolveInfo m_riHome;
	
	private Thread m_wifiScanThread = null;
	private int m_nWiFiScanInterval = 500;   //500ms
	
    private Thread m_cellularThreadGsm = null;
    private Thread m_cellularThreadCdma = null;	
	
	private Location m_location;

	private Thread m_locUpdateThread = null;
	
	//Thresholds
	private static final int WIFI_COUNT = 20;  // How many Wifi SSID to record from the scanned list
	private static final int TURN_DEGREE_THRESHOLD = 45;  //When turn >= 45, it is a turn	
	private static final int CURVE_DEGREE_THRESHOLD = 10;   //When the direction change of the vehicle >= 10 and < 45, it is a curve

	private static final int TURN_CURVE_TIME_THRESHOLD = 5;     //If within this time, and the turn/curve degree meets the thresholds, it is a turn or curve
	
	////
	private float[] m_arrfAcclValues = null;	 // Store accelerometer values
	private float[] m_arrfMagnetValues = null; // Store magnetic values
	private float[] m_arrfOrientValues = new float[3]; // Store orientation values
	
	private static String m_sAccl = ",,,";
	private static String m_sLinearAccl = ",,,";
	private static String m_sGravity = ",,,";	
	private static String m_sGyro = ",,,";	
	private static String m_sOrient = ",,,";
	private static String m_sMagnet = ",,,";

	private static String m_sLight = ",";
	private static String m_sBarometer = ",";
	
	private static String m_sGPS = ",,,,,,,";
	
	private static String m_sSouldLevel = ",";
	private static String m_sCellId = ",";
	private static String m_sWifi = ",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,";   //Original
	
		
	private static String m_sSensorAccuracy=",,";
	
	private static MarkerOptions m_moGps = null;
	private static MarkerOptions m_moCarLoc = null;	
	private static Marker m_MarkerGps = null;
	private static Marker m_MarkerCarLoc = null;
	
	private static boolean m_bStop = false;
		
	private static final int HISTORY_SIZE = 1250;
	private static XYPlot m_aprLocErrorPlot = null;
	private SimpleXYSeries m_errorSeries = null;
	
	private static double m_fMinX = 0;
	private static double m_fMaxX = 10.0;
	private static double m_fRangeX = 50.0;
	private static double m_fRangeY = 100.0;
	
	private static double m_fCurTimestamp = 0;
	
	/////////////////////////////////////////////////////////////////////////////////
    private class WifiReceiver extends BroadcastReceiver {
    	/* Record scanned Wifi information */
    	public void onReceive(Context c, Intent intent) {
    		int i,j, iCount,iRecordCount = 0;
    		WifiData wifiData = null;
    		WifiData tmpWifiData = null;
    		int nPos;

    		//PrintCurrentTime("WiFi Receiver");

    		m_lstWifiList = m_mainWifi.getScanResults();
    		    		
    		iCount = m_lstWifiList.size();
    		iRecordCount = iCount;

    		if (iRecordCount <= 0) return;
    		
    		List<WifiData> lstWifiData = new ArrayList<WifiData>();
    		
    		wifiData = new WifiData(m_lstWifiList.get(0).SSID, m_lstWifiList.get(0).BSSID, m_lstWifiList.get(0).level);
    		lstWifiData.add(wifiData); 
    		
    		for (i = 1; i < iRecordCount; i++) {
    			wifiData = new WifiData(m_lstWifiList.get(i).SSID, m_lstWifiList.get(i).BSSID, m_lstWifiList.get(i).level);
    			nPos = -1;
    			for (j=0; j < lstWifiData.size(); j++) {
    				tmpWifiData = lstWifiData.get(j);
    				if (m_lstWifiList.get(i).level > tmpWifiData.getSignalLevel()) {
    					nPos = j;
    					break;
    				}
    			}
    			
    			if (nPos == -1) {
    				lstWifiData.add(wifiData); 
    			} else {
    				lstWifiData.add(nPos, wifiData);
    			}
    		}
    		
        	SensorData senData = new SensorData(DATA_TYPE_WIFI, lstWifiData);
        	recordSensingInfo(senData);
        	
    	}
    }
	
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	   	
    	if (android.os.Build.VERSION.SDK_INT > 9) {
    	    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    	    StrictMode.setThreadPolicy(policy);
    	}
    	
    	try {
            Class.forName("android.os.AsyncTask");
    	} catch (ClassNotFoundException e) {
            e.printStackTrace();
    	}
    	
    	m_smCarLoc = (SensorManager) getSystemService(SENSOR_SERVICE);
    	
        PackageManager pm = getPackageManager();
        m_riHome = pm.resolveActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME),0);
        
        
        m_tmCellular = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        
        checkSensorAvailability();
        
    	
    	/* When the power button is pressed and the screen goes off, the sensors will stop work by default,
    	 * Here keep the CPU on to keep sensor alive and also use SCREEN_OFF notification to re-enable GPS/WiFi
    	 */
    	PowerManager pwrManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
    	m_wakeLock = pwrManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
    	IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
    	filter.addAction(Intent.ACTION_SCREEN_OFF);
    	
    	registerReceiver(m_ScreenOffReceiver,filter);
    	
    	
    	//Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    	show_setting();	
    }


    public void show_setting() {
    	int i;
    	Location location = null;

        setContentView(R.layout.car_loc_main);

        m_chkTurn = (CheckBox)findViewById(R.id.chkTurn);
        m_chkCurve = (CheckBox)findViewById(R.id.chkCurve);
        m_chkStop = (CheckBox)findViewById(R.id.chkStop);        
        m_chkBaro = (CheckBox)findViewById(R.id.chkBaro);
        m_chkWiFi = (CheckBox)findViewById(R.id.chkWiFi);
        m_chkCell = (CheckBox)findViewById(R.id.chkCell);
 
        m_chkTurn.setOnCheckedChangeListener(m_chkSensorEnableListener);
        m_chkCurve.setOnCheckedChangeListener(m_chkSensorEnableListener);
        m_chkStop.setOnCheckedChangeListener(m_chkSensorEnableListener);        
        m_chkBaro.setOnCheckedChangeListener(m_chkSensorEnableListener);        
        m_chkWiFi.setOnCheckedChangeListener(m_chkSensorEnableListener);
        m_chkCell.setOnCheckedChangeListener(m_chkSensorEnableListener);
                    
        configSensorOptionStatus();
        
		m_mainWifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		if (m_mainWifi.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
			m_blnWifiSignalEnabled = true;
		} else {
			m_blnWifiSignalEnabled = false;
			m_chkWiFi.setEnabled(false);
		}
		
		this.registerReceiver(m_brcvWifiStateChangedReceiver, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
		
		m_locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		m_blnGPSSignalEnabled = m_locManager.isProviderEnabled(m_sGPSProvider);
				
		if ((m_locManager != null) && (m_blnGPSSignalEnabled == true)) {
			location = m_locManager.getLastKnownLocation(m_sGPSProvider);
			if (location != null) {
				float fLat = (float)(location.getLatitude());
				float fLng = (float)(location.getLongitude());
				if (location.hasAltitude()) {
					float fAlt = (float)(location.getAltitude());
				}
			}
		}
				
        m_btnNext = (Button) findViewById(R.id.btNext);
        m_btnNext.setOnClickListener(m_btnNext_Listener);
          	
		m_chkTurn.setChecked(m_blnTurnEnabled);
		m_chkCurve.setChecked(m_blnCurveEnabled);
		m_chkStop.setChecked(m_blnStopEnabled);
		m_chkBaro.setChecked(m_blnBaroEnabled);
		m_chkWiFi.setChecked(m_blnWiFiEnabled);
		m_chkCell.setChecked(m_blnCellEnabled);
		
		Settings.System.putInt(getContentResolver(), Settings.System.WIFI_SLEEP_POLICY,  Settings.System.WIFI_SLEEP_POLICY_NEVER);
   	
    }
    
    
    public void show_map() {
        setContentView(R.layout.map_window);
        
        m_btnBack = (Button) findViewById(R.id.btBack);
        m_btnBack.setOnClickListener(m_btnBack_Listener);
        
        // setup the APR History plot:
        m_aprLocErrorPlot = (XYPlot) findViewById(R.id.aprLocErrorPlot);
 
        m_errorSeries = new SimpleXYSeries(null);
        //m_errorSeries.useImplicitXVals();
                
        m_aprLocErrorPlot.setRangeBoundaries(0, m_fRangeY, BoundaryMode.AUTO);
//        m_aprLocErrorPlot.setDomainBoundaries(m_fMinX, m_fMaxX, BoundaryMode.SHRINNK);
//        m_aprLocErrorPlot.setDomainBoundaries(20, 120, BoundaryMode.FIXED);

        //m_aprLocErrorPlot.setDomainBoundaries(m_fMinX, BoundaryMode.FIXED, m_fMaxX, BoundaryMode.FIXED);
        
        m_aprLocErrorPlot.addSeries(m_errorSeries, new LineAndPointFormatter(Color.rgb(0, 0, 255), Color.RED, null, null));

        //m_aprLocErrorPlot.setDomainStepValue(nTicksX);
        //m_aprLocErrorPlot.setTicksPerRangeLabel(2);
        m_aprLocErrorPlot.setRangeStepValue(6);
        
//        m_aprLocErrorPlot.getDomainLabelWidget().pack();
//        m_aprLocErrorPlot.getRangeLabelWidget().pack();
        
//        final PlotStatistics histStats = new PlotStatistics(1000, false);
//        m_aprLocErrorPlot.addListener(histStats);
//        m_aprLocErrorPlot.setLayerType(View.LAYER_TYPE_NONE, null);
        //histStats.setAnnotatePlotEnabled(true);
        
        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mymap);
        
        mGoogleMap = mMapFragment.getMap();
        if (mGoogleMap != null) {
        	
        	System.out.println("###############################################################");
        	
        	mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        	mGoogleMap.setPadding(0, 0, 0, 0);
        	mGoogleMap.setMyLocationEnabled(true);
        	
//        	Location myLocation = mGoogleMap.getMyLocation();
////            LatLng myLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
//            LatLng myLatLng = new LatLng(-44, 113);
// 
//            CameraPosition myPosition = new CameraPosition.Builder().target(myLatLng).zoom(17).bearing(90).tilt(30).build();
//            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(myPosition));
        	
        	m_moGps = new MarkerOptions().position(UIUC).draggable(true);
        	m_moCarLoc = new MarkerOptions().position(UIUC).draggable(true);
        	
//            m_MarkerGps = mGoogleMap.addMarker(m_moGps.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).title("GPS"));
//            m_MarkerCarLoc = mGoogleMap.addMarker(m_moCarLoc.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).title("CarLoc"));
        	BitmapDescriptor iconRed = BitmapDescriptorFactory.fromResource(R.drawable.reddot);
           	BitmapDescriptor iconBlue = BitmapDescriptorFactory.fromResource(R.drawable.bluedot);

        	
            m_MarkerGps = mGoogleMap.addMarker(m_moGps.icon(iconBlue).title("GPS"));
            m_MarkerCarLoc = mGoogleMap.addMarker(m_moCarLoc.icon(iconRed).title("CarLoc"));

            m_MarkerGps.showInfoWindow();
            m_MarkerCarLoc.showInfoWindow();


//        	CameraPosition cameraPosition = new CameraPosition.Builder()
//          .target(GOLDEN_GATE_BRIDGE) // Sets the center of the map to
//                                      // Golden Gate Bridge
//              .zoom(17)                   // Sets the zoom
//              .bearing(90) // Sets the orientation of the camera to east
//              .tilt(30)    // Sets the tilt of the camera to 30 degrees
//              .build();    // Creates a CameraPosition from the builder
//      	mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));        	
           
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(UIUC, 15));
            m_bStop = false;
            
            openGpsFile();
        	
            //startUpdateLocAsy();
            startUpdateLocAsySimple();

              //startUpdateLoc();
            
//            for (int i=0; i<POS_NUM; i++) {
//            	LatLng myLatlng = new LatLng(m_nGpsLat[i],m_nGpsLong[i]);
//            	m_MarkerGps.setPosition(myLatlng);
//            	mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatlng, 16));
//            	
//         	  	try {
//          	  		Thread.sleep(1000);
//          	  	} catch (InterruptedException e) {
//          	  	
//          	  	}
//          	
//            }
            
           
//        	mGoogleMap.addMarker(new MarkerOptions().position(GOLDEN_GATE_BRIDGE).title("Golden Gate Bridge").snippet("San Francisco").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher)));
//        	
//        	CameraPosition cameraPosition = new CameraPosition.Builder()
//            .target(GOLDEN_GATE_BRIDGE) // Sets the center of the map to
//                                        // Golden Gate Bridge
//                .zoom(17)                   // Sets the zoom
//                .bearing(90) // Sets the orientation of the camera to east
//                .tilt(30)    // Sets the tilt of the camera to 30 degrees
//                .build();    // Creates a CameraPosition from the builder
//        	mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));        	
        }
    	
    }
    
    public boolean openGpsFile() {
       	//FileReader fr = null;
    	//BufferedReader br = null;
    	    	
		try {
			File fl = new File(m_sGpsFilePath);

	    	if (fl.exists() == false) {
	    		System.out.println("-----------------1");
	    		return false;
	    	}
			
			m_fr = new FileReader(m_sGpsFilePath);
			m_br = new BufferedReader(m_fr);
			System.out.println("-----------------2");
			return true;
		} catch (Exception e) {
			System.out.println("-----------------3");
			return false;
		}
    	
    }
    
	public String[] getFields(String sLine) {
		String[] fields = null;
		
		if (sLine.length() > 0) {
			fields = sLine.split(",");
		}
		
		return fields;
	}

    public String getGps() {
    	String sLine = "";
    	
		try {
	    	if ((sLine = m_br.readLine()) != null) {	
	    		//System.out.println("-----------------444: " + sLine);			
			} 
		} catch (Exception e) {
			
		}
		
		return sLine;
    }
	
    private void startUpdateLocAsySimple() {
    	final Handler handler = new Handler();
    	
    	m_sLine = getGps();
    	
		if (m_sLine == null || m_sLine.length() == 0) {
    		m_bStop = true;
    		
            try {
            	m_fr.close();
            	m_fr = null;
            	m_br = null;
            } catch (Exception e) {
            	
            }
    		
    		return;	            			
		}
   	
        handler.post(new Runnable() {
	            @Override
	            public void run() {
	            		
	            	if (m_sLine == null || m_sLine.length() == 0 || mGoogleMap == null || m_bStop == true) {
	            		return;
	            	}
	            	
            		String[] fields = null;
            			    
            		fields = getFields(m_sLine);
                	double fLatCarLoc = Double.valueOf(fields[1]).doubleValue();
                	double fLongCarLoc = Double.valueOf(fields[2]).doubleValue();
                	
		        	LatLng myLatlngCarLoc = new LatLng(fLatCarLoc,fLongCarLoc);
		        	m_MarkerCarLoc.setPosition(myLatlngCarLoc);     //RED

                	double fLatGps = Double.valueOf(fields[3]).doubleValue();
                	double fLongGps = Double.valueOf(fields[4]).doubleValue();
		        	LatLng myLatlngGps = new LatLng(fLatGps, fLongGps);
			        m_MarkerGps.setPosition(myLatlngGps);           //BLUE
		        	
			        double fCentroidLat = (fLatCarLoc + fLatGps)/2;
			        double fCentroidLong = (fLongCarLoc + fLongGps)/2;
			        LatLng myLatlngCamera = new LatLng(fCentroidLat, fCentroidLong);
		        	mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatlngCamera, 15));
		        	
		        	double fError = Double.valueOf(fields[5]).doubleValue();
		        	
		        	m_fCurTimestamp = Double.valueOf(fields[0]).doubleValue();
		        	System.out.println("-------------==============  " + m_fCurTimestamp);
		        	
		        	updatePLot(m_fCurTimestamp, fError);

		        	m_sLine = getGps();
		        	
            		if (m_sLine == null || m_sLine.length() == 0) {
                		m_bStop = true;
                		
                        try {
                        	m_fr.close();
                        	m_fr = null;
                        	m_br = null;
                        } catch (Exception e) {
                        	
                        }
                		
                		return;	            			
            		}
		        	
            		fields = getFields(m_sLine);
            		double fNextTimeStamp = Double.valueOf(fields[0]).doubleValue();
            		
                	long nTimeGap = (long)((fNextTimeStamp - m_fCurTimestamp)*1000/PLAYSPEED);
                	
                	System.out.println("----------Sleep:   " + nTimeGap);
                	
                	handler.postDelayed(this, nTimeGap);
	
	            }
        });

   	
    }

    
    private synchronized void updatePLot(double fCurTimestamp, double fError) {
    	// update instantaneous data:
 
        // get rid the oldest sample in history:
        if (m_errorSeries.size() > HISTORY_SIZE) {
        	m_errorSeries.removeFirst();
        }
 
        // add the latest history sample:
        m_errorSeries.addLast(fCurTimestamp, fError);

       // m_aprLocErrorPlot.setDomainBoundaries(fMinX, BoundaryMode.FIXED, fMaxX, BoundaryMode.FIXED);

        // redraw the Plots:
        m_aprLocErrorPlot.redraw();
        
        if (fCurTimestamp > m_fRangeX) {
        	m_aprLocErrorPlot.setDomainBoundaries(fCurTimestamp-m_fRangeX, fCurTimestamp, BoundaryMode.FIXED);
        	m_aprLocErrorPlot.setDomainStepValue(6);
        } else {
        	m_aprLocErrorPlot.setDomainBoundaries(0, m_fRangeX, BoundaryMode.FIXED);
        	m_aprLocErrorPlot.setDomainStepValue(6);
        }
        	
    }
    
    
    private void startUpdateLocAsy() {

    	final Handler handler = new Handler();
    	
    	mTimer = new Timer();
    	mDoAsyncTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                    		
                        	String sLine = getGps();
                        	
                        	System.out.println("----------GPS:   " + sLine);
                        	
                        	if (sLine == null || sLine.length() == 0) {
                        		m_bStop = true;
                        		
                                try {
                                	m_fr.close();
                                	m_fr = null;
                                	m_br = null;
                                } catch (Exception e) {
                                	
                                }
                        		
                        		mDoAsyncTask.cancel();
                        		return;
                        	} 
                        	
                        	
                        	String[] fields = null;
                        	fields = getFields(sLine);
                        	double fLatCarLoc = Double.valueOf(fields[1]).doubleValue();
                        	double fLongCarLoc = Double.valueOf(fields[2]).doubleValue();
                        	
    			        	LatLng myLatlngCarLoc = new LatLng(fLatCarLoc,fLongCarLoc);
    			        	m_MarkerCarLoc.setPosition(myLatlngCarLoc);     //RED

                        	double fLatGps = Double.valueOf(fields[3]).doubleValue() + 0.001;
                        	double fLongGps = Double.valueOf(fields[4]).doubleValue() + 0.001;
	   			        	LatLng myLatlngGps = new LatLng(fLatGps, fLongGps);
					        m_MarkerGps.setPosition(myLatlngGps);           //BLUE
				        	
					        double fCentroidLat = (fLatCarLoc + fLatGps)/2;
					        double fCentroidLong = (fLongCarLoc + fLongGps)/2;
					        LatLng myLatlngCamera = new LatLng(fCentroidLat, fCentroidLong);
				        	mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatlngCamera, 15));		
				        	
                    }
                });
            }
        };

        if (m_bStop == false) {
        	mTimer.schedule(mDoAsyncTask,0,4);
        } else {
        	mTimer.cancel();
        	//mDoAsyncTask.cancel();
        	
        }
   	
    }
    

    private void startUpdateLoc() {
    		
    	m_locUpdateThread = new Thread(new Runnable() {
											public void run() {
												updateMarkerLocation();
											}
									},"Update Location Thread");
	
    	m_locUpdateThread.start();
    	
    }

    private void updateMarkerLocation() {
    	while (m_locUpdateThread != null) {
    		m_MarkerGps = mGoogleMap.addMarker(m_moGps);
        	LatLng myLatlng = new LatLng(m_nGpsLat[m_nPosIdx],m_nGpsLong[m_nPosIdx]);
        	m_MarkerGps.setPosition(myLatlng);
        	mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatlng, 15));
   		  		
    		m_nPosIdx = m_nPosIdx + 1;
    		if (m_nPosIdx >= POS_NUM) {
    				m_nPosIdx = 0;
    		}
    		
      	  	try {
      	  		Thread.sleep(1000);
      	  	} catch (InterruptedException e) {
      	  	
      	  	}
    		  
    	}
    }
    
    private Button.OnClickListener m_btnNext_Listener = new Button.OnClickListener() {
    	public void onClick(View v) {
    		//Check whether any sensor is selected   		
    		if ((m_blnTurnEnabled == false) && 
    			(m_blnCurveEnabled == false) &&
    			(m_blnStopEnabled == false) &&    			
    			(m_blnBaroEnabled == false) && 
    			(m_blnWiFiEnabled == false) &&
    			(m_blnCellEnabled == false)) {
    			
    			Toast.makeText(getApplicationContext(), "Please select sensor!", Toast.LENGTH_SHORT).show();
    			return;
    		}
    		
    		UpdateSensorServiceRegistration();    	
    		
    		show_map();
    		
    	}
    };
   
    
    private Button.OnClickListener m_btnBack_Listener = new Button.OnClickListener() {
    	public void onClick(View v) {
    		//Check whether any sensor is selected   		
    		m_locUpdateThread = null;
    		
    		if (m_fr != null) {
	    		 try {
	             	m_fr.close();
	             } catch (Exception e) {
	             	
	             }
    		}
    		
    		getFragmentManager().beginTransaction().remove(mMapFragment).commit();
    		
    		if (m_bStop == false) {
    			m_bStop = true;
    		
	            try {
	            	m_fr.close();
	            	m_fr = null;
	            	m_br = null;
	            } catch (Exception e) {
	            	
	            }
	    		
//	    		mDoAsyncTask.cancel();
//	    		mTimer.cancel();
    		}
    		
    		mGoogleMap = null;
    		
    		show_setting();
    	}
    };   
 
    private void UpdateSensorServiceRegistration() {
		//Register sensors according to the enable/disable status

    	m_smCarLoc.registerListener(m_actHome, m_smCarLoc.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SENSOR_DELAY_MODE);

 		m_smCarLoc.registerListener(m_actHome, m_smCarLoc.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SENSOR_DELAY_MODE);

 		m_smCarLoc.registerListener(m_actHome, m_smCarLoc.getDefaultSensor(Sensor.TYPE_GRAVITY), SENSOR_DELAY_MODE);
 	
 		m_smCarLoc.registerListener(m_actHome, m_smCarLoc.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SENSOR_DELAY_MODE);
 
 		m_smCarLoc.registerListener(m_actHome, m_smCarLoc.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SENSOR_DELAY_MODE);

 		m_smCarLoc.registerListener(m_actHome, m_smCarLoc.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SENSOR_DELAY_MODE);

 		m_smCarLoc.registerListener(m_actHome, m_smCarLoc.getDefaultSensor(Sensor.TYPE_PRESSURE), SENSOR_DELAY_MODE);
	 				    	 
    }
    
    private BroadcastReceiver m_brcvWifiStateChangedReceiver = new BroadcastReceiver() {
    	/* Monitor Wifi Enable/Disable status */
    	public void onReceive(Context context, Intent intent) {
    		int iExtraWifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
    		
    		if (iExtraWifiState == WifiManager.WIFI_STATE_ENABLED) {
    			m_blnWifiSignalEnabled = true;
    			m_chkWiFi.setEnabled(true);
    		} else if (iExtraWifiState == WifiManager.WIFI_STATE_DISABLED) {
    			m_blnWifiSignalEnabled = false;
    			m_chkWiFi.setEnabled(false);
    		}
    	}
    };

    
    private void configSensorOptionStatus() {
    	if (m_blnBarometerPresent == false) {
    		m_chkBaro.setEnabled(false);
    	}    	

    }
    
	/* Event for sensor enable/disable */
	private OnCheckedChangeListener m_chkSensorEnableListener = new OnCheckedChangeListener() {
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			if (buttonView == m_chkTurn) {
				if (isChecked == true) {
					m_blnTurnEnabled = true;
				} else {
					m_blnTurnEnabled = false;
				}
			}
			
			if (buttonView == m_chkCurve) {
				if (isChecked == true) {
					m_blnCurveEnabled = true;
				} else {
					m_blnCurveEnabled = false;
				}
			}
			
			if (buttonView == m_chkStop) {
				if (isChecked == true) {
					m_blnStopEnabled = true;
				} else {
					m_blnStopEnabled = false;
				}
			}

			if (buttonView == m_chkBaro) {
				if (isChecked == true) {
					m_blnBaroEnabled = true;
				} else {
					m_blnBaroEnabled = false;
				}
			}
			
			if (buttonView == m_chkWiFi) {
				if (isChecked == true) {
					m_blnWiFiEnabled = true;
				} else {
					m_blnWiFiEnabled = false;
				}
			}
			
			
			if (buttonView == m_chkCell) {
				if (isChecked == true) {
					m_blnCellEnabled = true;
				} else {
					m_blnCellEnabled = false;
				}
			}
													
		}			
	};
   
    
	/* Check the availability of sensors, disable relative widgets */
	private void checkSensorAvailability() {
		List<Sensor> lstSensor = m_smCarLoc.getSensorList(Sensor.TYPE_ORIENTATION);
		if (lstSensor.size() > 0) {
			m_blnOrientPresent = true;
		} else {
			m_blnOrientPresent = false;
		}

		lstSensor = m_smCarLoc.getSensorList(Sensor.TYPE_GYROSCOPE);
		if (lstSensor.size() > 0) {
			m_blnGyroPresent = true;
		} else {
			m_blnGyroPresent = false;
		}

		lstSensor = m_smCarLoc.getSensorList(Sensor.TYPE_ACCELEROMETER);
		if (lstSensor.size() > 0) {
			m_blnAcclPresent = true;
		} else {
			m_blnAcclPresent = false;
		}

		lstSensor = m_smCarLoc.getSensorList(Sensor.TYPE_LINEAR_ACCELERATION);
		if (lstSensor.size() > 0) {
			m_blnLinearAcclPresent = true;
		} else {
			m_blnLinearAcclPresent = false;
		}

		lstSensor = m_smCarLoc.getSensorList(Sensor.TYPE_GRAVITY);
		if (lstSensor.size() > 0) {
			m_blnGravityPresent = true;
		} else {
			m_blnGravityPresent = false;
		}
		
		lstSensor = m_smCarLoc.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
		if (lstSensor.size() > 0) {
			m_blnMagnetPresent = true;
		} else {
			m_blnMagnetPresent = false;
		}


		lstSensor = m_smCarLoc.getSensorList(Sensor.TYPE_PRESSURE);
		if (lstSensor.size() > 0) {
			m_blnBarometerPresent = true;
		} else {
			m_blnBarometerPresent = false;
		}

	}    
    
	//When the power button is pushed, screen goes off, to still keep the service running, some need to be re-enabled
    public BroadcastReceiver m_ScreenOffReceiver = new BroadcastReceiver() {
    	
    	public void onReceive(Context context, Intent intent) {
    		
    		if (!intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
    			return;
    		}
    		    		
    			    			
			if ((m_blnWifiSignalEnabled == true) && (m_blnWiFiEnabled == true)) {
	    		if (m_receiverWifi != null) {
	    			unregisterReceiver(m_receiverWifi);
	    			registerReceiver(m_receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	 				//m_mainWifi.startScan();
	 				startWiFiScan();
	    		}
			}
			
 			if (m_blnGPSSignalEnabled == true) {
 				if (m_locManager != null) {
 					m_locManager.removeUpdates(m_locListener);
 					m_locManager.requestLocationUpdates(m_sGPSProvider, 0L, 0.0f, m_locListener);
 				}
 			}
	
    			
    	}
    	
    };
   
    private LocationListener m_locListener = new LocationListener() {
    	public void onLocationChanged(Location location) {
    		if (location != null) {    		
    			recordLocation(location);
    		}
    	}

    	public void onProviderDisabled(String provider) {    		 
    		if (provider.equals(m_sGPSProvider)) {
    			m_blnGPSSignalEnabled = false;
    		}
    	}
    	 
    	public void onProviderEnabled(String provider) {
    		if (provider.equals(m_sGPSProvider)) {
    			m_blnGPSSignalEnabled = true;
    		} 
     	}
    	 
    	public void onStatusChanged(String provider, int status, Bundle extras) {
    		if (provider.equals(m_sGPSProvider)) {
    			if (status == LocationProvider.OUT_OF_SERVICE) {
    				m_blnGPSSignalEnabled = false;
    			} else {
    				m_blnGPSSignalEnabled = true;
    			}
    		}	 
    	}
    	 
    };
 
    
    private void startWiFiScan() {
    	if (m_mainWifi != null) {
    		
	    	m_wifiScanThread = new Thread(new Runnable() {
											public void run() {
												scanWiFi();
											}
									},"Scanning WiFi Thread");
	
	    	m_wifiScanThread.start();
    	}
    }

    private void scanWiFi() {
    	while (m_wifiScanThread != null && m_mainWifi != null) {
    		m_mainWifi.startScan();
//    		try {
//    			Method startScanActiveMethod = WifiManager.class.getMethod("startScanActive");
//    			startScanActiveMethod.invoke(m_mainWifi);  		
//    		} catch (Exception e) {
//    			
//    		}
    		//PrintCurrentTime("StartScan");
    		
      	  	try {
      	  		Thread.sleep(m_nWiFiScanInterval);
      	  	} catch (InterruptedException e) {
    		  
      	  	}
   		
    	}
   	
    }
    
    private void stopWiFiScan() {
    	m_wifiScanThread = null;
    	if (m_receiverWifi != null) {
    		unregisterReceiver(m_receiverWifi);
    	}
    }
    
    private void startGettingCellularNetworkInfo() {
        
        if (m_tmCellular != null) {
        	
        	CellLocation cellLoc = m_tmCellular.getCellLocation();
        	
        	if (cellLoc instanceof GsmCellLocation) {   //GSM compatible network
        		
        		m_GsmCellLocation = (GsmCellLocation) cellLoc;
	
	        	if (m_GsmCellLocation != null) {
	            	m_cellularThreadGsm = new Thread(new Runnable() {
													public void run() {
														getGsmCellularInfo();
													}
											},"GSM Cellular Thread");
	
	            	m_cellularThreadGsm.start();
	        	}
        	} else if (cellLoc instanceof CdmaCellLocation) {   //CDMA compatible network
        		
        		m_CdmaCellLocation = (CdmaCellLocation) cellLoc;
        		
	        	if (m_CdmaCellLocation != null) {
	            	m_cellularThreadCdma = new Thread(new Runnable() {
													public void run() {
														getCdmaCellularInfo();
													}
											},"CDMA Cellular Thread");
	
	            	m_cellularThreadCdma.start();
	        	}
        		
        	}
        }
        
    }
    
    private void getGsmCellularInfo() {
    	
    	while (m_GsmCellLocation != null) {
    		m_GsmCellLocation.requestLocationUpdate();
    		
    		int nCellId = m_GsmCellLocation.getCid();
    		
    		recordCellInfo(nCellId);
    		
      	  	try {
      	  		Thread.sleep(500);
      	  	} catch (InterruptedException e) {
    		  
      	  	}
   		
    	}
    }

    private void getCdmaCellularInfo() {
    	
    	while (m_CdmaCellLocation != null) {
    		m_CdmaCellLocation.requestLocationUpdate();
    		
    		int nCellId = m_CdmaCellLocation.getBaseStationId();
    		
    		recordCellInfo(nCellId);
    		
      	  	try {
      	  		Thread.sleep(500);
      	  	} catch (InterruptedException e) {
    		  
      	  	}
   		
    	}
    }
   
    
    private void stopGettingCellularNetworkInfo() {
    	if (m_GsmCellLocation != null) {
    		m_GsmCellLocation = null;
    	}
    	
    	if (m_CdmaCellLocation != null) {
    		m_CdmaCellLocation = null;
    	}
    	
    	m_cellularThreadGsm = null;
    	m_cellularThreadCdma = null;
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.car_loc_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.car_loc_main, container, false);
            return rootView;
        }
    }

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

    /* Calculate orientation from accelerometer and magnetic field value */
    private boolean calculateOrientation() {
    	float[] arrfValues = new float[3];
    	float[] arrfR = new float[9];
    	float[] arrfI = new float[9];
    	
        if ((m_arrfAcclValues == null) || (m_arrfMagnetValues == null)) {
    		return false;
    	}
    	    	
    	if (SensorManager.getRotationMatrix(arrfR, arrfI, m_arrfAcclValues, m_arrfMagnetValues)) {
    		SensorManager.getOrientation(arrfR, arrfValues);
    		m_arrfOrientValues[0] = (float)Math.toDegrees(arrfValues[0]);
    		m_arrfOrientValues[1] = (float)Math.toDegrees(arrfValues[1]);
    		m_arrfOrientValues[2] = (float)Math.toDegrees(arrfValues[2]);
    		
    		if (m_arrfOrientValues[0] < 0) {
    			m_arrfOrientValues[0] = m_arrfOrientValues[0] + 360;  // Make Azimuth 0 ~ 360
    		}

    		return true;
    	} else {
    		return false;
    	}
    	
    }
	
	
    //Record GPS Readings
    public void recordLocation(Location location) {
    	SensorData senData = new SensorData(DATA_TYPE_GPS, location);
    	recordSensingInfo(senData);
    }
	
    //Record Cellular Network ID
    public void recordCellInfo(int nCellId) {
    	SensorData senData = new SensorData(DATA_TYPE_CELLULAR, nCellId);
    	recordSensingInfo(senData);
    }
    
    //Organize the sensing information and save into CSV file
    public void recordSensingInfo(SensorData senData) {
    	String sRecordLine;
    	String sTimeField;
    	Date dtCurDate;
    	int i;
    	long lStartTime = 0;
    	long lCurrentTime = 0;
		SimpleDateFormat spdRecordTime,spdCurDateTime;
        final String DATE_FORMAT = "yyyyMMddHHmmss";
		final String DATE_FORMAT_S = "yyMMddHHmmssSSS"; //"yyyyMMddHHmmssSSS"
		int nSensorReadingType = SENSOR_EVENT_NULL; 
		int nSensorDataType;
		
        dtCurDate = new Date();
		        
		// Timestamp for the record
        spdRecordTime = new SimpleDateFormat(DATE_FORMAT_S);
		sTimeField = spdRecordTime.format(dtCurDate);
				
		nSensorDataType = senData.getSensorDataType();
		
		if (nSensorDataType == DATA_TYPE_SENSOR) {
			SensorEvent event;
			
			event = senData.getSensorEvent();
			
			synchronized(this) {
	    		switch (event.sensor.getType()){
				    			
		    		case Sensor.TYPE_ACCELEROMETER:
		    			//X, Y, Z
		    				m_sAccl = Float.toString(event.values[0]) + "," + 
		    						Float.toString(event.values[1]) + "," + 
		    						Float.toString(event.values[2]) + ",";
		    			
		    				nSensorReadingType = SENSOR_EVENT_ACCL;
		    			
			    			m_arrfAcclValues = event.values.clone();
		    				
			    			if (calculateOrientation()) {
			    				//Azimuth (rotation around z-axis); Pitch (rotation around x-axis), Roll (rotation around y-axis)
		    					m_sOrient = Float.toString(m_arrfOrientValues[0]) + "," + 
		    								Float.toString(m_arrfOrientValues[1]) + "," + 
		    								Float.toString(m_arrfOrientValues[2]) + ",";
		    					
		    					nSensorReadingType = SENSOR_EVENT_ORIENT;
		    					
		    				}
		    			break;
		    			
		    		case Sensor.TYPE_LINEAR_ACCELERATION:
		    			//X,Y,Z
		    				m_sLinearAccl = Float.toString(event.values[0]) + "," + 
		    								Float.toString(event.values[1]) + "," + 
		    								Float.toString(event.values[2]) + ",";
		    			
		    				nSensorReadingType = SENSOR_EVENT_LINEAR_ACCL;
		    			
		    			break;
		    			
		    		case Sensor.TYPE_GRAVITY:
		    			//X,Y,Z
		    				m_sGravity = Float.toString(event.values[0]) + "," + 
		    							 Float.toString(event.values[1]) + "," + 
		    							 Float.toString(event.values[2]) + ",";
		    				
		    				nSensorReadingType = SENSOR_EVENT_GRAVITY;
		    			
	    				break;

		    		case Sensor.TYPE_GYROSCOPE:
		    			//X,Y,Z
		    			m_sGyro = Float.toString(event.values[0]) + "," + 
		    					  Float.toString(event.values[1]) + "," + 
		    					  Float.toString(event.values[2]) + ",";
		    			nSensorReadingType = SENSOR_EVENT_GYRO;
		    			break;
		    			
		    		case Sensor.TYPE_MAGNETIC_FIELD:
		    			// Values are in micro-Tesla (uT) and measure the ambient magnetic field 
		    				m_sMagnet = Float.toString(event.values[0]) + "," + 
		    							Float.toString(event.values[1]) + "," + 
		    							Float.toString(event.values[2]) + ",";
		    				
		    				nSensorReadingType = SENSOR_EVENT_MAGNET;

		    				m_arrfMagnetValues = event.values.clone();
		    				
		    				if (calculateOrientation()) {
		    					//Azimuth (rotation around z-axis); Pitch (rotation around x-axis), Roll (rotation around y-axis)
		    					m_sOrient = Float.toString(m_arrfOrientValues[0]) + "," + 
	    									Float.toString(m_arrfOrientValues[1]) + "," + 
	    									Float.toString(m_arrfOrientValues[2]) + ",";
		    							    					
		    					if (nSensorReadingType != SENSOR_EVENT_MAGNET) {
		    						nSensorReadingType = SENSOR_EVENT_ORIENT;
		    					}
		    				}
		    			
		    			break;
		    		
		    		
		    		case Sensor.TYPE_LIGHT:
		    			// Ambient light level in SI lux units 
		    			m_sLight = Float.toString(event.values[0]) + ",";
		    			nSensorReadingType = SENSOR_EVENT_LIGHT;
		    			break;
		    		
		    		case Sensor.TYPE_PRESSURE:
		    			// Atmospheric pressure in hPa (millibar)
		    			m_sBarometer = Float.toString(event.values[0]) + ",";
		    			nSensorReadingType = SENSOR_EVENT_BAROMETER;
		    			break;
		    			
	    		}
	    	}
		} else if (nSensorDataType == DATA_TYPE_GPS){
			Location locationGps;
			locationGps = senData.getGpsLocation();
			
			if (locationGps != null) {

				m_location = new Location(locationGps);

				//Change from double to float
				m_sGPS = Float.valueOf((float)(locationGps.getLatitude())).toString() + "," +
						Float.valueOf((float)(locationGps.getLongitude())).toString() + ",";
				if (locationGps.hasAltitude()) {
					m_sGPS = m_sGPS + Float.valueOf((float)(locationGps.getAltitude())).toString() + ",";
					GeomagneticField geoField = new GeomagneticField(
								Double.valueOf(locationGps.getLatitude()).floatValue(),
								Double.valueOf(locationGps.getLongitude()).floatValue(),
								Double.valueOf(locationGps.getAltitude()).floatValue(),
								System.currentTimeMillis());
					// Append Declination, in Degree
					m_sGPS = m_sGPS + Float.valueOf((float)(geoField.getDeclination())).toString() + "," + Float.valueOf((float)(geoField.getInclination())).toString() + ",";
				} else {
					m_sGPS = m_sGPS + ",,,";
					//m_sGPS = m_sGPS + ",";
				}
				
				//New add 201408270009
				if (locationGps.hasSpeed()) {
					m_sGPS = m_sGPS + Float.valueOf((float)(locationGps.getSpeed())).toString() + ",";
				} else {
					m_sGPS = m_sGPS + ",";
				}
				
				if (locationGps.hasBearing()) {
					m_sGPS = m_sGPS + Float.valueOf((float)(locationGps.getBearing())).toString() + ",";
				} else {
					m_sGPS = m_sGPS + ",";
				}
				
				nSensorReadingType = SENSOR_EVENT_GPS;
			}
		} else if (nSensorDataType == DATA_TYPE_MIC) {
			double fSoundLevelDb;
			fSoundLevelDb = senData.getSoundLevelDb();
			m_sSouldLevel = new BigDecimal(fSoundLevelDb).setScale(0, BigDecimal.ROUND_HALF_UP) + ",";
			
			nSensorReadingType = SENSOR_EVENT_MIC;
			
		} else if (nSensorDataType == DATA_TYPE_CELLULAR) {
			int nCellId;
			nCellId = senData.getCellId();
			m_sCellId = Integer.valueOf(nCellId).toString() + ",";
			nSensorReadingType = SENSOR_EVENT_CELLULAR;
			
		} else if (nSensorDataType == DATA_TYPE_WIFI) {
			List<WifiData> lstWifiData = senData.getListWifiData();
			int nWifiCnt = Math.min(WIFI_COUNT, lstWifiData.size());
			m_sWifi = "";
			for (i=0; i< nWifiCnt; i++) {
				//m_sWifi = m_sWifi + lstWifiData.get(i).getSSID() + "," + lstWifiData.get(i).getBSSID() + "," + lstWifiData.get(i).getSignalLevel() + ",";
				m_sWifi = m_sWifi + lstWifiData.get(i).getBSSID() + "," + lstWifiData.get(i).getSignalLevel() + ",";

			}
			
			for (i=1; i<=WIFI_COUNT-nWifiCnt; i++) {
				//m_sWifi = m_sWifi + ",,,";
				m_sWifi = m_sWifi + ",,";
			}
			
			nSensorReadingType = SENSOR_EVENT_WIFI;
		}
		
		if (nSensorReadingType == SENSOR_EVENT_NULL) {
			return;
		}
		
		sRecordLine = sTimeField + ",";
		
		
		sRecordLine = sRecordLine + Integer.valueOf(nSensorReadingType) + ",";
		

		sRecordLine = sRecordLine  + m_sAccl + m_sLinearAccl + m_sGravity + m_sGyro + m_sOrient + 
									m_sMagnet + m_sLight + m_sBarometer +  
									m_sSouldLevel + m_sCellId + m_sGPS + m_sWifi;
		
		////////////////////////////
//		String sAngle = calculateRot(m_sAccl, m_sGravity);
//		String sarrAngle[] = sAngle.split(",");
//		String sShow = sarrAngle[0] + "\n" + sarrAngle[1];
		
		String sShow = "";
		
		if (m_sGravity.length() > 3) {
			String sarrAngle[] = m_sGravity.split(",");
			double fX = Double.valueOf(sarrAngle[0]).doubleValue();
			double fY = Double.valueOf(sarrAngle[1]).doubleValue();
			double fZ = Double.valueOf(sarrAngle[2]).doubleValue();
			
			double fTotal = Math.sqrt(fX*fX + fY*fY + fZ*fZ);
			
			double fAngleZ = Math.acos(fZ/fTotal)/Math.PI*180;
			double fAngleY = 90 - Math.acos(fY/fTotal)/Math.PI*180;
			double fAngleX = 90 - Math.acos(fX/fTotal)/Math.PI*180;
			
			sShow = "X:  " +  fAngleX + "\n";
			sShow = sShow + "Y:  " +  fAngleY + "\n";
			sShow = sShow + "Z:  " +  fAngleZ;
			
							
		}
		
//		if (m_sGravity.length() > 3) {
//			String sarrAngle[] = m_sGravity.split(",");
//			double fX = Double.valueOf(sarrAngle[0]).doubleValue();
//			double fY = Double.valueOf(sarrAngle[1]).doubleValue();
//			double fZ = Double.valueOf(sarrAngle[2]).doubleValue();
//			
//			int nSymbol = 0;
//			if (fX < 0)  {
//				sShow = sShow + "- X" + "\n";
//			} else if (fX > 0) {
//				sShow = sShow + "+ X" + "\n";
//			}
//			
//			if (fY < 0)  {
//				sShow = sShow + "- Y" + "\n";
//			} else if (fY > 0) {
//				sShow = sShow + "+ Y" + "\n";
//			}
//			
//			if (fZ < 0)  {
//				sShow = sShow + "- Z";
//			} else if (fZ > 0) {
//				sShow = sShow + "+ Z";
//			}
//							
//		}
//		
//		if (m_sGyro.length() > 3) {
//			String sarrAngle[] = m_sGyro.split(",");
//			double fX = Double.valueOf(sarrAngle[0]).doubleValue();
//			double fY = Double.valueOf(sarrAngle[1]).doubleValue();
//			double fZ = Double.valueOf(sarrAngle[2]).doubleValue();
//			
//			int nSymbol = 0;
//			if (fX < 0)  {
//				nSymbol = -1;
//			} else if (fX > 0) {
//				nSymbol = 1;
//			}
//			
//			if (fY < 0)  {
//				nSymbol = nSymbol + (-1);
//			} else if (fY > 0) {
//				nSymbol = nSymbol + 1;
//			}
//			
//			if (fZ < 0)  {
//				nSymbol = nSymbol + (-1);
//			} else if (fZ > 0) {
//				nSymbol = nSymbol + 1;
//			}
//				
//			if (nSymbol < 0) {
//				nSymbol = -1;
//			} else if (nSymbol > 0) {
//				nSymbol = 1;
//			}
//			
//			sShow = sShow + "\n\n" + nSymbol + "";
//		}
		
		////////////////////////////
		
    	sRecordLine = sRecordLine + System.getProperty("line.separator");
    	

    }
	
}
