package com.tokudu.demo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

public class PushActivity extends Activity {
	public static final String NEWMESSAGE="com.tokudo.demo.NEWMESSAGE";
	
	
	private BroadcastReceiver react =new BroadcastReceiver(){

		public void redmessage(String m) {
			if(redtoggle==null)return;
			if(m.equalsIgnoreCase("0"))redtoggle.setChecked(false);
			else redtoggle.setChecked(true);
			
		}

		public void greenmessage(String m) {
			if(greentoggle==null)return;
			if(m.equalsIgnoreCase("0"))greentoggle.setChecked(false);
			else greentoggle.setChecked(true);
			
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("recieved intent in activity");
			if(intent.getAction().equalsIgnoreCase(NEWMESSAGE)){
				String topic=intent.getStringExtra("topic");
				String message=intent.getStringExtra("message");
				if(PushService.GREEN_TOPIC.equalsIgnoreCase(topic)){
					greenmessage(message);
				}else if(PushService.RED_TOPIC.equalsIgnoreCase(topic)){
					redmessage(message);
				}
			}
			
		}
		
	};
	
	
	private String mDeviceID;
	private EditText mOutEditText;
	private String mHost;
	private ToggleButton redtoggle;
	private ToggleButton greentoggle;
	// The action listener for the EditText widget, to listen for the return key
    private TextView.OnEditorActionListener mWriteListener =
        new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
               mHost=message;
            }
            
            return true;
        }

	
    };
    
    

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        registerReceiver(react, new IntentFilter(NEWMESSAGE));

        mDeviceID = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);         
  	  	((TextView) findViewById(R.id.target_text)).setText(mDeviceID);
 
  	  	final Button startButton = ((Button) findViewById(R.id.start_button));
  	  	final Button stopButton = ((Button) findViewById(R.id.stop_button));
  	  	redtoggle=((ToggleButton) findViewById(R.id.redtogglebutton));
  	  	greentoggle=((ToggleButton) findViewById(R.id.greentogglebutton));
  	  	
  	// Initialize the compose field with a listener for the return key
        mOutEditText = (EditText) findViewById(R.id.edit_text_out);
        mOutEditText.setOnEditorActionListener(mWriteListener);
  	  	startButton.setOnClickListener(new OnClickListener() {			
			//@Override
			public void onClick(View v) {
		    	Editor editor = getSharedPreferences(PushService.TAG, MODE_PRIVATE).edit();
		    	editor.putString(PushService.PREF_DEVICE_ID, mDeviceID);
		    	editor.commit();
				PushService.actionStart(getApplicationContext(),mHost);		        
		  		startButton.setEnabled(false);
		  		stopButton.setEnabled(true);				
			}
		});
  	  	stopButton.setOnClickListener(new OnClickListener() {
			//@Override
			public void onClick(View v) {
				PushService.actionStop(getApplicationContext());		        								
		  		startButton.setEnabled(true);
		  		stopButton.setEnabled(false);				
			}
		});
  	  	startButton.setEnabled(true);
  	  	stopButton.setEnabled(false);
    }
    public void onToggleClicked(View v) {
        // Perform action on clicks
    	ToggleButton button=((ToggleButton) v);
    	if(button.equals(greentoggle)){
    		if(button.isChecked()){
    			PushService.actionGreen(getApplicationContext(), "1");
    		} else {
    			PushService.actionGreen(getApplicationContext(), "0");
    		}
    	} else if(button.equals(redtoggle)){
    		if(button.isChecked()){
    			PushService.actionRed(getApplicationContext(), "1");
    		} else {
    			PushService.actionRed(getApplicationContext(), "0");
    		}
    	}
        
    }
    @Override
    protected void onResume() {
    	super.onResume();
  	  	
  	  	SharedPreferences p = getSharedPreferences(PushService.TAG, MODE_PRIVATE);
  	  	boolean started = p.getBoolean(PushService.PREF_STARTED, false);
  	  	
  		((Button) findViewById(R.id.start_button)).setEnabled(!started);
  		((Button) findViewById(R.id.stop_button)).setEnabled(started);
    }
}