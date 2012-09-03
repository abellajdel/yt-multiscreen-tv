package net.neurocomm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class MessageHandle implements Runnable{

	//private Receive receive;
    private String commNumber;
    private Handler mHandler;
    
    /*
     * commNumber stands for communication number
     * it's the unique name of an exchange that connects a device to Synapse 
     */
    
    /*
     * pHandler is the Handler that receives messages for the UI Looper queue
     * to be processed in the UI thread
     */
    public MessageHandle(String pCommNumber, Handler pHandler) throws java.io.IOException
    {
    	//commNumber = pCommNumber;
    	commNumber = pCommNumber;
    	mHandler = pHandler;
        //receive = new Receive(this, commNumber);
    }
    
    //This starts the thread that listens to RabbitMq messages
    public void run()
    {
    	
    	try
    	{
    		//receive = new Receive(this, commNumber);
    		//receive.
    		this.listenToMessages();
    	}
    	catch(Exception ioe)
    	{
    		Log.e("MYAPP", "exception", ioe);
    	}
    	
    }	
    
    //Receive.listenToMessage automatically calls the CallBack(Map) of this class
    public void listenToMessages() throws IOException, InterruptedException
    {
    	Socket recvSocket = new Socket("184.72.57.4", 1338);
		InputStream ins;
		OutputStream ops;
		ins = recvSocket.getInputStream();
		ops = recvSocket.getOutputStream();
		String addCommand = "{\"addr\":\""+commNumber+"\"}";
		ops.write(addCommand.getBytes());
		while(true){
			byte[] buffer = new byte[1024];
			System.out.println("Waiting for message...");
			ins.read(buffer, 0, 1024);
			try{
				JSONObject jsonObject = new JSONObject(new String(buffer));
				HashMap<String, String> receivedHashMap = hashMapFromJSONObject(jsonObject);
                this.callBack(receivedHashMap);
			}catch (Exception jsonEsception){
				
			}
		}
    }
    
    //Converting the Json to HashMap 
    private HashMap<String, String> hashMapFromJSONObject(JSONObject pJsonObject) throws JSONException
    {
        HashMap<String,String> map = new HashMap<String,String>();
        Iterator iter = pJsonObject.keys();
        while(iter.hasNext())
        {
            String key = (String)iter.next();
            String value = pJsonObject.getString(key);
            map.put(key,value);
        }
        return map;
    }
    
    /*
     * Called after a message is received by receive.ListenToMessage() to be handled(non-Javadoc)
     * It gets data from the Map to strings, and send them back to 
     * the UI thread via mHandler to be treated
     */
    
    public void callBack(Map map)
    {
        Set set = map.entrySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext())
        {
            Map.Entry entry = (Map.Entry)iterator.next();
            String receivedMessage = entry.getValue().toString();
            Message message = Message.obtain();
            message.obj = receivedMessage;
            mHandler.sendMessage(message);
        }
    }
    
}
