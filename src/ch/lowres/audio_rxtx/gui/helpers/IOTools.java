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

import java.io.*;

import java.util.*;
import java.util.jar.*;
import java.util.zip.*;

import java.awt.*;

import javax.swing.ImageIcon;
import javax.imageio.ImageIO;

import java.net.*;

/**
* Input/Output operations: read resources from JAR file, load / save settings, open in browser etc.
*/
//========================================================================
public class IOTools
{
	private static Main g;
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
							g.p("extracted '" + entry.getName()+"'");// to '"+destUri+"'" );
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
			g.e("'"+folderName+"' not found in jar '"+jarFileString+"'");
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
		catch(Exception e) {g.e("bad ----");}///
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
	public static Font createFontFromJar(String fontUriInJar, float fontSize)
	{
		InputStream is;
		Font f;
		try
		{
			is=IOTools.class.getResourceAsStream(fontUriInJar);
			f=Font.createFont(Font.TRUETYPE_FONT, is);
			f=f.deriveFont(fontSize);
			is.close();
		}
		catch(Exception e)
		{
			g.w("could not load built-in font. "+e.getMessage());
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
			g.w("could not load built-in imge. "+e.getMessage());
			return null;
		}
		return ii;
	}//end createImageIconFromJar

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
//dummy
					g.p("*** v"+v);
				}
				catch(Exception e)
				{
					g.e(e.getMessage());
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
	public static boolean loadProps(Properties props)
	{
		if(props!=null)
		{
try{
			if(props.getProperty("s._name")!=null){g.apis._name=props.getProperty("s._name");}
			if(props.getProperty("s._sname")!=null){g.apis._sname=props.getProperty("s._sname");}
			if(props.getProperty("s._connect")!=null){g.apis._connect=Boolean.parseBoolean(props.getProperty("s._connect"));}
			if(props.getProperty("s._nopause")!=null){g.apis._nopause=Boolean.parseBoolean(props.getProperty("s._nopause"));}
			if(props.getProperty("s.test_mode")!=null){g.apis.test_mode=Boolean.parseBoolean(props.getProperty("s.test_mode"));}
			if(props.getProperty("s._limit")!=null){g.apis._limit=Integer.parseInt(props.getProperty("s._limit"));}
			if(props.getProperty("s._drop")!=null){g.apis._drop=Integer.parseInt(props.getProperty("s._drop"));}
			if(props.getProperty("s.verbose")!=null){g.apis.verbose=Boolean.parseBoolean(props.getProperty("s.verbose"));}
			if(props.getProperty("s._update")!=null){g.apis._update=Integer.parseInt(props.getProperty("s._update"));}
			if(props.getProperty("s.lport_random")!=null){g.apis.lport_random=Boolean.parseBoolean(props.getProperty("s.lport_random"));}
			if(props.getProperty("s._lport")!=null){g.apis._lport=Integer.parseInt(props.getProperty("s._lport"));}

///////////////////
			if(props.getProperty("s.autostart")!=null){g.apis.autostart=Boolean.parseBoolean(props.getProperty("s.autostart"));}



			if(props.getProperty("s._16")!=null){g.apis._16=Boolean.parseBoolean(props.getProperty("s._16"));}
			if(props.getProperty("s._in")!=null){g.apis._in=Integer.parseInt(props.getProperty("s._in"));}
			if(props.getProperty("s._target_host")!=null){g.apis._target_host=props.getProperty("s._target_host");}
			if(props.getProperty("s._target_port")!=null){g.apis._target_port=Integer.parseInt(props.getProperty("s._target_port"));}

			if(props.getProperty("r._name")!=null){g.apir._name=props.getProperty("r._name");}
			if(props.getProperty("r._sname")!=null){g.apir._sname=props.getProperty("r._sname");}
			if(props.getProperty("r._connect")!=null){g.apir._connect=Boolean.parseBoolean(props.getProperty("r._connect"));}
			if(props.getProperty("r.test_mode")!=null){g.apir.test_mode=Boolean.parseBoolean(props.getProperty("r.test_mode"));}
			if(props.getProperty("r._limit")!=null){g.apir._limit=Integer.parseInt(props.getProperty("r._limit"));}
			if(props.getProperty("r.verbose")!=null){g.apir.verbose=Boolean.parseBoolean(props.getProperty("r.verbose"));}
			if(props.getProperty("r._update")!=null){g.apir._update=Integer.parseInt(props.getProperty("r._update"));}
			if(props.getProperty("r.lport_random")!=null){g.apir.lport_random=Boolean.parseBoolean(props.getProperty("r.lport_random"));}

			if(props.getProperty("r._offset")!=null){g.apir._offset=Integer.parseInt(props.getProperty("r._offset"));}
			if(props.getProperty("r._pre")!=null){g.apir._pre=Integer.parseInt(props.getProperty("r._pre"));}
			if(props.getProperty("r._max")!=null){g.apir._max=Integer.parseInt(props.getProperty("r._max"));}
			if(props.getProperty("r._rere")!=null){g.apir._rere=Boolean.parseBoolean(props.getProperty("r._rere"));}
			if(props.getProperty("r._reuf")!=null){g.apir._reuf=Boolean.parseBoolean(props.getProperty("r._reuf"));}
			if(props.getProperty("r._nozero")!=null){g.apir._nozero=Boolean.parseBoolean(props.getProperty("r._nozero"));}
			if(props.getProperty("r._norbc")!=null){g.apir._norbc=Boolean.parseBoolean(props.getProperty("r._norbc"));}
			if(props.getProperty("r._close")!=null){g.apir._close=Boolean.parseBoolean(props.getProperty("r._close"));}


//////////////
			if(props.getProperty("r.autostart")!=null){g.apir.autostart=Boolean.parseBoolean(props.getProperty("r.autostart"));}


			if(props.getProperty("r._16")!=null){g.apir._16=Boolean.parseBoolean(props.getProperty("r._16"));}
			if(props.getProperty("r._out")!=null){g.apir._out=Integer.parseInt(props.getProperty("r._out"));}
			if(props.getProperty("r._lport")!=null){g.apir._lport=Integer.parseInt(props.getProperty("r._lport"));}

			if(props.getProperty("s.gui_osc_port_random")!=null){g.gui_osc_port_random_s=Boolean.parseBoolean(props.getProperty("s.gui_osc_port_random"));}
			if(props.getProperty("s.gui_osc_port")!=null){g.gui_osc_port_s=Integer.parseInt(props.getProperty("s.gui_osc_port"));}

			if(props.getProperty("r.gui_osc_port_random")!=null){g.gui_osc_port_random_r=Boolean.parseBoolean(props.getProperty("r.gui_osc_port_random"));}
			if(props.getProperty("r.gui_osc_port")!=null){g.gui_osc_port_r=Integer.parseInt(props.getProperty("r.gui_osc_port"));}

			if(props.getProperty("keep_cache")!=null){g.keep_cache=Boolean.parseBoolean(props.getProperty("keep_cache"));}
			if(props.getProperty("show_both_panels")!=null){g.show_both_panels=Boolean.parseBoolean(props.getProperty("show_both_panels"));}

			return true;
}
catch(Exception e)
{///
}
		}//end if props not null
		return false;
	}//end loadProps

//========================================================================
	//http://www.drdobbs.com/jvm/readwrite-properties-files-in-java/231000005
	public static boolean loadSettings(String propertiesFileUri)
	{
		Properties props=new Properties();
		InputStream is=null;
 
		//try loading from the current directory
		try 
		{
			//loading default props from jar
			is = IOTools.class.getResourceAsStream("/resources/"+g.defaultPropertiesFileName);

			if(is!=null)
			{
				props.load(is);
				loadProps(props);
				g.p("default built-in settings loaded");
			}
			else
			{
				g.e("could not load built-in default settings");
				///
				System.exit(1);
			}

			//overload with given properties from filesystem
			//this file can contain a subset of all config keys
			if(propertiesFileUri!=null && !propertiesFileUri.equals(""))
			{
				File f=new File(propertiesFileUri);
				if(f.exists() && f.canRead())
				{
					is=new FileInputStream(f);

					if(is!=null)
					{
						props.load(is);
						loadProps(props);
						g.p("settings '"+propertiesFileUri+"' loaded");
						return true;
					}
					else
					{
						g.e("could not load settings '"+propertiesFileUri+"'");
					}
				}
				else
				{
					g.e("could not load settings '"+propertiesFileUri+"'");
				}
			}
			else
			{
				g.e("could not load settings '"+propertiesFileUri+"'");
			}
		}
		catch (Exception e)
		{
			is=null; 
			g.e("file '"+propertiesFileUri+"' not found");
		}
		return false;
	}//end loadSettings

//========================================================================
	public static boolean saveSettings(String propertiesFileUri)
	{
		try 
		{
			Properties props = new Properties();

			props.setProperty("s._name", g.apis._name);
			props.setProperty("s._sname", g.apis._sname);
			props.setProperty("s._connect", g.apis._connect ? "true" : "false");
			props.setProperty("s._nopause", g.apis._nopause ? "true" : "false");
			props.setProperty("s.test_mode", g.apis.test_mode ? "true" : "false");
			props.setProperty("s._limit", ""+g.apis._limit);
			props.setProperty("s._drop", ""+g.apis._drop);
			props.setProperty("s.verbose", g.apis.verbose ? "true" : "false");
			props.setProperty("s._update", ""+g.apis._update);
			props.setProperty("s.lport_random", g.apis.lport_random ? "true" : "false");
			props.setProperty("s._lport", ""+g.apis._lport);


//////////
			props.setProperty("s.autostart", ""+g.apis.autostart);



			props.setProperty("s._16", g.apis._16 ? "true" : "false");
			props.setProperty("s._in", ""+g.apis._in);
			props.setProperty("s._target_host", g.apis._target_host);
			props.setProperty("s._target_port", ""+g.apis._target_port);

			props.setProperty("r._name", g.apir._name);
			props.setProperty("r._sname", g.apir._sname);
			props.setProperty("r._connect", g.apir._connect ? "true" : "false");
			props.setProperty("r.test_mode", g.apir.test_mode ? "true" : "false");
			props.setProperty("r._limit", ""+g.apir._limit);
			props.setProperty("r.verbose", g.apir.verbose ? "true" : "false");
			props.setProperty("r._update", ""+g.apir._update);

			props.setProperty("r._offset", ""+g.apir._offset);
			props.setProperty("r._pre", ""+g.apir._pre);
			props.setProperty("r._max", ""+g.apir._max);
			props.setProperty("r._rere", g.apir._rere ? "true" : "false");
			props.setProperty("r._reuf", g.apir._reuf ? "true" : "false");
			props.setProperty("r._nozero", g.apir._nozero ? "true" : "false");
			props.setProperty("r._norbc", g.apir._norbc ? "true" : "false");
			props.setProperty("r._close", g.apir._close ? "true" : "false");

/////////
			props.setProperty("r.autostart", ""+g.apir.autostart);


			props.setProperty("r._16", g.apir._16 ? "true" : "false");
			props.setProperty("r._out", ""+g.apir._out);
			props.setProperty("r.lport_random", g.apir.lport_random ? "true" : "false"); //no on gui
			props.setProperty("r._lport", ""+g.apir._lport);

			props.setProperty("s.gui_osc_port_random", g.gui_osc_port_random_s ? "true" : "false");
			props.setProperty("s.gui_osc_port", ""+g.gui_osc_port_s);

			props.setProperty("r.gui_osc_port_random", g.gui_osc_port_random_r ? "true" : "false");
			props.setProperty("r.gui_osc_port", ""+g.gui_osc_port_r);

			props.setProperty("keep_cache", g.keep_cache ? "true" : "false");
			props.setProperty("show_both_panels", g.show_both_panels ? "true" : "false");

			File f = new File(propertiesFileUri);
			OutputStream out = new FileOutputStream(f);
			props.store(out, "This file is read by audio_rxtx GUI if found");

			g.p("settings '"+propertiesFileUri+"' saved");

			return true;
		}
		catch (Exception e ) 
		{
			g.e("could not save settings '"+propertiesFileUri+"'");
			e.printStackTrace();
		}
		return false;
	}//end saveSettings
}//end class IOTools