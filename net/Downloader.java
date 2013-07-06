/**
*	HTTP Downloader lib
*	Copyright (C) 2012-2013  Dimitrios Desyllas (pc_magas)
*
*    This program is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    This program is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*Contact with me by main at thes address: pc_magas@yahoo.gr
*/
package net;

import net.CountingStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.*;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

/**
*Downloading files from web
*@authon Dimitrios Desyllas (aka: pc_magas)
*/
public class Downloader implements Runnable
{
	/**
	*Class for taking the progress and making into percentage
	*/
    private class ProgressListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            // e.getSource() gives you the object of CountingOutputStream
            // because you set it in the overriden method, afterWrite().
            if(total>0)
            {
           		 percent=100*((CountingStream) e.getSource()).getByteCount()/(float)total;
           	}
        }
    }
    
    /**
    *Class for using ssl
    */
    private class SSLManager implements X509TrustManager
    {
    	@Override
    	 public X509Certificate[] getAcceptedIssuers() 
    	 {
               return null;
         }
         
         @Override
         public void checkClientTrusted(X509Certificate[] certs, String authType) 
         {
         }
         
         @Override
         public void checkServerTrusted(X509Certificate[] certs, String authType) 
         {
         }
	}
	
	/**
	*Waiting Status
	*/
	public static final String WAITING="Waiting";
	
	/**
	*Error Status
	*/
	public static final String ERROR="Error";
	
	/**
	*Donwloading Status
	*/
	public static final String DOWNLOADING="Downloading";
	
	/**
	*Finished download Status
	*/
	public static final String FINISHED="Finished";
	
	/**
	*Shows the total size of file
	*/
	private long total=0;
	/**
	*Displays the total percent of file
	*/
	private float percent=0;
	/**
	*The remote url of file
	*/
	private URL dl;
	/**
	*The file that we will store
	*/
	private File fl;
	
	/**
	*Stores the status
	*/
	private String status="Waiting";
	
	/**
	*Stores the mime type
	*/
	private String mime="";
	
	/**
	*Tells if it is a sslk connection or not
	*/
	private boolean ssl=false;
	
	/**
	*Variable that configures the connection
	*/
	private URLConnection conn=null;
	
	/**
	*Output Stream of downloades file
	*/
	private OutputStream os=null;
	
	/**
	*InputStream
	*/
	private InputStream is = null;
	/**
	*Constructor Method
	*@param url: The url of the file we want to download from the web
	*@param path: The path of the file we want to store the file downloaded file. It may dbe different from the name of the file
	*/
	public Downloader(String url, String path)
	{
		this(url,path,null,(long)0);
	}
	
	/**
	*Constructor Method. You can select allowed mime and upper size limit to download a file.
	*@param url: The url of the file we want to download from the web
	*@param path: The path of the file we want to store the file downloaded file. It may dbe different from the name of the file
	*@param allowedType: The allowed mime types we want to download
	*@param allowedSize: The  maximum size of downloaded file in <strong>BYTES</strong> that we permit to download for no limit enter<br>
	*zero or negative valie  
	*/
	public Downloader(String url, String path,String[] allowedType,long allowedSize)
	{
		boolean ok=true;
		//Needed for ssl
			
		try
		{
			if(!url.startsWith("http://") && !url.startsWith("https://"))
			{
				url="http://"+url;
			}
			else if(url.startsWith("https://"))
			{
				ssl=true;
			}
			
			dl=new URL(url);
			fl=new File(path);
			
			conn=dl.openConnection();
			
			if(conn!=null)
			{
				mime=conn.getContentType();
				
				for(int i=0;allowedType!=null && mime!=null && i<allowedType.length;i++)
				{
					if(mime.startsWith(allowedType[i]))
					{
						//System.out.println("Mime OK");
						ok=true;
						break;
					}
					else
					{
						//System.out.println("Mime Not OK");
						ok=false;
					}
				}

				String s=conn.getHeaderField("Content-Length");
				if(s!=null)
				{
					total=Long.parseLong(s);
					if(ok)
					{
						if(allowedSize>0 && total<allowedSize)
						{
							System.out.println("OK");
							ok=true;
						}
						else if(allowedSize<=0)
						{
							System.out.println("No Limit");
							ok=true;
						}
						else
						{
							System.out.println("Not OK");
							ok=false;
						}
					}
				}
			}
			
			if(ok)
			{
				Thread t=new Thread(this);
				t.start();
			}
			else
			{
				status="Error";
			}
		}
		catch(UnknownHostException u)
		{
			System.err.println("Uknown Host");
			u.printStackTrace();
		}
		catch(Exception m)
		{
			m.printStackTrace();
		}
	}
	
	public void run()
	{
        os = null;
        is = null;

        ProgressListener progressListener = new ProgressListener();
        try 
        {
        	// Create a trust manager that does not validate certificate chains
        	TrustManager[] trustAllCerts = new TrustManager[] { new SSLManager()};
        		
        	// Install the all-trusting trust manager
			final SSLContext sc = SSLContext.getInstance("SSL");
        	sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
				
			// Create all-trusting host name verifier
        	HostnameVerifier allHostsValid = new HostnameVerifier() 
        	{
            	public boolean verify(String hostname, SSLSession session) 
            	{
               		return true;
            	}
        	};

			// Install the all-trusting host verifier
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

        	
            os = new FileOutputStream(fl);
            is = conn.getInputStream();

            CountingStream dcount = new CountingStream(os);
            dcount.setListener(progressListener);

			status="Downloading";
            // begin transfer by writing to dcount, not os.
            IOUtils.copy(is, dcount);
			
        }
        catch(UnknownHostException u)
        {
        	System.err.println("Uknown Host2");
			u.printStackTrace();
        } 
        catch (Exception e) 
        {
            System.out.println(e);
        } 
        finally 
        {
        	try
        	{
        		status="Finished";
        	    if (os != null) 
        	    { 
        	        os.close(); 
        	    }
        	    if (is != null) 
        	    { 
        	        is.close(); 
        	    }
        	}
        	catch(IOException e)
        	{
        		e.printStackTrace();
        	}
        }
	}
	
	/**
	*Retruns the progress as %
	*/
	public float getPercent()
	{
		return percent;
	}
	
	/**
	*Returns the status of Downloaded file
	*/
	public String getStatus()
	{
		return status;
	}
	
	/**
	*Returns The size of file in Bytes
	*/
	public long getSize()
	{
		return total;
	}
	
	/**
	*Returns the correct mime type of the file
	*/
	public String getMime()
	{
		return mime;
	}
	
	/**
	*Returns the OutputStream
	*/
	public OutputStream getOutputStream()
	{
		return os;
	}
	
	/**
	*Returns ImpurStream
	*/
	public InputStream getInputStream()
	{
		return is;
	}
}
