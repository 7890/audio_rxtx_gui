/* part of audio_rxtx GUI
 * https://github.com/7890/audio_rxtx_gui
 *
 * Copyright (C) 2014 Thomas Brand <tom@trellis.ch>
 *
 * This program is free software; feel free to redistribute it and/or 
 * modify it.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. bla.
*/

package ch.lowres.audio_rxtx.gui.helpers;
import ch.lowres.audio_rxtx.gui.*;
import ch.lowres.audio_rxtx.gui.widgets.*;

import java.io.*;

import java.util.*;
import java.util.jar.*;
import java.util.zip.*;

import java.awt.*;
import javax.swing.*;

import javax.swing.ImageIcon;
import javax.imageio.ImageIO;

import java.net.*;

/**
* Input/Output operations: read resources from JAR file, load / save settings, open in browser etc.
*/
//========================================================================
public class IOTools
{
	private static Main m;
	private static GUI g;
	private static Languages l;

	private static String jarFileString="";

//========================================================================
	//default: read from own jar
	public IOTools()
	{
		//http://stackoverflow.com/questions/320542/how-to-get-the-path-of-a-running-jar-file
		jarFileString=IOTools.class.getProtectionDomain().getCodeSource().getLocation().getPath();
	}

//========================================================================
	public IOTools(String jarFileUri)
	{
		jarFileString=jarFileUri;
	}

//========================================================================
	public static String getJarMd5Sum()
	{
		try
		{
			return MD5Checksum.getMD5Checksum(jarFileString);
		}
		catch(Exception ign){}
		return "n/a";
	}

//========================================================================
	//https://community.oracle.com/thread/1188356?start=0&tstart=0
	//Copies an entire folder out of a jar to a physical location.
	public static boolean copyJarContent(String folderName, String destUri)
	{
		boolean found=false;
		//cut leading "/"
		if(folderName.length()>1 && folderName.substring(0,1).equals("/"))
		{
			folderName=folderName.substring(1,folderName.length());
		}

		try
		{
			ZipFile z=new ZipFile(jarFileString);
			Enumeration entries=z.entries();
			while(entries.hasMoreElements())
			{
				ZipEntry entry=(ZipEntry)entries.nextElement();

				if(entry.getName().contains(folderName))
				{
					found=true;

					File f=new File(destUri+"/"+entry.getName());

					if(entry.isDirectory())
					{
						f.mkdir();
					}
					else if(!f.exists())
					{
						if(copyFileFromJar("/"+entry.getName(), new File(destUri+"/"+entry.getName())))
						{
							m.p(l.tr("extracted '") + entry.getName()+"'");// to '"+destUri+"'" );
						}
					}
				}
			}//end while hasMoreElements
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		if(!found)
		{
			m.e("'"+folderName+"' not found in jar '"+jarFileString+"'");
		}
		return false;
	}//end copyContent

//========================================================================
	/* 
	* Copies a file out of the jar to a physical location.
	* Doesn't need to be private, uses a resource stream, so may have
	* security errors if ran from webstart application 
	*/
	public static boolean copyFileFromJar(String sResource, File fDest)
	{
		if(sResource==null || fDest==null) 
		{
			return false;
		}
		InputStream sIn=null;
		OutputStream sOut=null;
		try
		{
			fDest.getParentFile().mkdirs();
		}
		catch(Exception e) {m.e("bad ----");}///
		try 
		{
			int nLen=0;

			sIn=IOTools.class.getResourceAsStream(sResource);
			if(sIn==null)
			{
				throw new Exception("Error extracting from jar '"+sResource+"' to '"+fDest.getPath()+"'");
			}
			sOut=new FileOutputStream(fDest);
			byte[] bBuffer=new byte[1024];
			while((nLen=sIn.read(bBuffer))>0)
			{
				sOut.write(bBuffer, 0, nLen);
			}
			sOut.flush();
		}
		catch(Exception ex) 
		{
			ex.printStackTrace();
		}
		finally 
		{
			try 
			{
				if(sIn!=null) {sIn.close();}
				if(sOut!=null) {sOut.close();}
			}
			catch(Exception eError) 
			{
				eError.printStackTrace();
			}
		}
		return fDest.exists();
	}//end copyFileFromJar

//========================================================================
	public static Font getDefaultFont(float fontSize)
	{
		//return (new JLabel().getFont()).deriveFont(Font.PLAIN, fontSize);
		return createFontFromJar("/resources/fonts/Ubuntu-C.ttf",fontSize);
	}

//========================================================================
	public static Font getDefaultFont(float fontSize, int style) //Font.BOLD
	{
		//return (new JLabel().getFont()).deriveFont(style, fontSize);
		return createFontFromJar("/resources/fonts/Ubuntu-C.ttf",fontSize,style);
	}

//========================================================================
	public static Font createFontFromJar(String fontUriInJar, float fontSize)
	{
		return createFontFromJar(fontUriInJar, fontSize, Font.PLAIN);
	}
//========================================================================
	public static Font createFontFromJar(String fontUriInJar, float fontSize, int style)
	{
		InputStream is;
		Font f;
		try
		{
			is=IOTools.class.getResourceAsStream(fontUriInJar);
			f=Font.createFont(Font.TRUETYPE_FONT, is);
			f=f.deriveFont(style,fontSize);
			is.close();
		}
		catch(Exception e)
		{
			m.w("could not load built-in font. "+e.getMessage());
			return null;
		}
		return f;
	}//end createFontFromJar

//========================================================================
	public static ImageIcon createImageIconFromJar(String imageUriInJar)
	{
		InputStream is;
		ImageIcon ii;
		try
		{
			is=IOTools.class.getResourceAsStream(imageUriInJar);
			ii=new ImageIcon(ImageIO.read(is));
			is.close();
		}
		catch(Exception e)
		{
			m.w("could not load built-in image. "+e.getMessage());
			return null;
		}
		return ii;
	}//end createImageIconFromJar

//========================================================================
	public static Image createImageFromJar(String imageUriInJar)
	{
		InputStream is;
		Image ii;
		try
		{
			is=IOTools.class.getResourceAsStream(imageUriInJar);
			ii=ImageIO.read(is);
			is.close();
		}
		catch(Exception e)
		{
			m.w("could not load built-in image. "+e.getMessage());
			return null;
		}
		return ii;
	}//end createImageFromJar

//========================================================================
	//http://www.rgagnon.com/javadetails/java-0483.html
	public static boolean deleteDirectory(File path) 
	{
		if( path.exists() ) 
		{
			File[] files=path.listFiles();
			for(int i=0; i<files.length; i++) 
			{
				if(files[i].isDirectory()) 
				{
					deleteDirectory(files[i]);
				}
				else 
				{
					files[i].delete();
				}
			}
		}
		return(path.delete());
	}//end deleteDirectory

//========================================================================
	////http://stackoverflow.com/questions/2546968/open-pdf-file-on-fly-from-java-application
	public static boolean openFile(File f) 
	{
		Desktop desktop=Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if(desktop!=null && desktop.isSupported(Desktop.Action.OPEN)) 
		{
			try 
			{
				desktop.open(f);
				return true;
			}
			catch(Exception e) 
			{
				g.setStatus("Could Not Open PDF File");
				e.printStackTrace();
			}
		}
		else
		{
			g.setStatus("Could Not Open PDF File");
		}
		return false;
	}//end openFile

//========================================================================
	public static void checkForNewerVersion(String url)
	{
		final String url_=url;

		(new Thread()
		{
			public void run()
			{
				Reader reader=null;
				float v=0;
				try
				{
					InputStream in = new URL(url_).openStream();
					reader = new InputStreamReader(in, "UTF-8");
					Properties props = new Properties();
					props.load(reader);
					if(props.getProperty("version")!=null){v=Float.parseFloat(props.getProperty("version"));}
					reader.close();
/////dummy
					m.p("*** v"+v);
				}
				catch(Exception e)
				{
					m.e(e.getMessage());
				}
			}
 		}).start();
	}

//========================================================================
	//http://stackoverflow.com/questions/19375091/how-to-open-url-by-desktop-object-in-java
	public static boolean openInBrowser(String url) 
	{
		Desktop desktop=Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if(desktop!=null && desktop.isSupported(Desktop.Action.BROWSE)) 
		{
			try 
			{
				desktop.browse(new URI(url));
				return true;
			}
			catch(Exception e) 
			{
				g.setStatus("Could Not Browse URL");
				e.printStackTrace();
			}
		}
		else
		{
			g.setStatus("Could Not Browse URL");
		}
		return false;
	}//end openFile

//========================================================================
	//http://stackoverflow.com/questions/9481865/how-to-get-ip-address-of-current-machine-using-java
	public static InetAddress getLocalIPAddress()
	{
		try
		{
			//http://stackoverflow.com/questions/11797641/java-finding-network-interface-for-default-gateway
			DatagramSocket s=new DatagramSocket();
			s.connect(InetAddress.getByAddress(new byte[]{1,1,1,1}), 0);

			NetworkInterface f=NetworkInterface.getByInetAddress(s.getLocalAddress());
			Enumeration<InetAddress> e=f.getInetAddresses();
			InetAddress ifAddress=null;
			while(e.hasMoreElements())
			{
				ifAddress=e.nextElement();
				//m.p("ip bound to nic: "+ifAddress.getHostAddress());
			}
			//return last bound address on that interface
			m.p(l.tr("IP address")+": "+ifAddress.getHostAddress());
			return ifAddress;
		}
		catch (Exception e)
		{
			//e.printStackTrace();
		}
		return null;
	}
}//end class IOTools
