package net.neurocomm;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

public class ReceptionActivity extends Activity implements Callback {
    /** Called when the activity is first created. */
	
	public Handler mHandler;
	public String commNumber;
	private TextView t;
	private int activityRequestCode = 0;
	private static final String FILENAME = "neuroconn_setting";
	private Intent i; 
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        i = new Intent(Intent.ACTION_VIEW);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        
        getCommNumber();
        
        //Setting the commNumber in the textview
        t=new TextView(this);
        t=(TextView)findViewById(R.id.commnumber); 
        t.append(commNumber);
        
        
        try
        {            
            mHandler = new Handler(this);   
            /*We pass the reference of mHandler of this class to messageHandle so 
             * the later will use it to call the first one back
             */
            MessageHandle messageHandle = new MessageHandle(commNumber, mHandler);
         	new Thread(messageHandle).start();
        }
        catch(IOException ioe)
        {
        	Log.e("MYAPP", "exception", ioe);
        }
    }
    
    public void getCommNumber(){
        try
        {
        	byte[] fileContent = new byte[1024];
        	FileInputStream fos = openFileInput(FILENAME);
        	fos.read(fileContent);
        	//commNumber = fileContent.toString();
        	commNumber = "qwerty";
        	fos.close();
        	
        }
        catch(FileNotFoundException	fnfe)
        {
        	//If the file is not found we create it
        	setCommNumber();
        }
        catch(IOException ioe)
        {
        	
        }
    }
    
    public void setCommNumber(){
    	String uuid = UUID.randomUUID().toString();
    	try
    	{
    		FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
    		fos.write(uuid.getBytes());
    		commNumber = uuid;
    		fos.close();
    	}
    	catch(FileNotFoundException	fnfe)
    	{
    		
    	}
    	catch(IOException ioe)
        {
        	
        }
    }
    
    //This is claaed by the callback function of the MessageHandler 
    public boolean handleMessage(Message msg) {    	
    	activityRequestCode = 1987;
    	String videoId = msg.obj.toString();
    	Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://"+videoId));
    	startActivity(i);
    	return true;
    }
    
}