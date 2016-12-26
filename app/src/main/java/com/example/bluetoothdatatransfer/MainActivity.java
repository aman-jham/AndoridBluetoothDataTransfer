package com.example.bluetoothdatatransfer;

import java.io.File;
import java.util.List;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

	private static final int DISCOVER_DURATION = 300;
	private static final int REQUEST_BLU = 1;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void sendViaBluetooth(View v) {
    
    	BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    	
    	if(btAdapter == null) {
    		Toast.makeText(this, "Bluetooth is not supported on this device", Toast.LENGTH_LONG).show();
    	} else {
    		enableBluetooth();
    	}
    }
    
    public void enableBluetooth() {
    	
    	Intent discoveryIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
    	
    	discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVER_DURATION);
    	
    	startActivityForResult(discoveryIntent, REQUEST_BLU);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		File f = null;
    	if(resultCode == DISCOVER_DURATION && requestCode == REQUEST_BLU) {
			try {
				final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
				mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
				List<ResolveInfo> apps = getPackageManager().queryIntentActivities(mainIntent, 0);
				for (ResolveInfo info : apps) {
					f = new File(info.activityInfo.applicationInfo.publicSourceDir);
					// Copy the .apk file to wherever
				}

				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_SEND);
				intent.setType("text/plain");
				intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));

				PackageManager pm = getPackageManager();
				List<ResolveInfo> appsList = pm.queryIntentActivities(intent, 0);

				if(appsList.size() > 0) {
					String packageName = null;
					String className = null;
					boolean found = false;

					for(ResolveInfo info : appsList) {
						packageName = info.activityInfo.packageName;
						if(packageName.equals("com.android.bluetooth")) {
							className = info.activityInfo.name;
							found = true;
							break;
						}
					}

					if (!found) {
						Toast.makeText(this, "Bluetooth havn't been found",
								Toast.LENGTH_LONG).show();
					} else {
						intent.setClassName(packageName, className);
						startActivity(intent);
					}
				}
			}catch (Exception e){
				e.printStackTrace();
			}

		} else {
			Toast.makeText(this, "Bluetooth is cancelled", Toast.LENGTH_LONG)
					.show();
		}
    }
}
