package ch.lowres.audio_rxtx.gui;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;

import java.util.jar.JarInputStream;
import java.util.jar.JarEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.util.Enumeration;

import java.awt.Font;

import javax.swing.ImageIcon;
import javax.imageio.ImageIO;

import java.awt.Desktop;

import java.util.Properties;

//tb/1410

//========================================================================
public class IOTools
{
	static jack_audio_send_GUI g;
	static jack_audio_send_cmdline_API api;

	static String jarFileString="";

//========================================================================
	//default: read from own jar
	public IOTools()
	{
		//http://stackoverflow.com/questions/320542/how-to-get-the-path-of-a-running-jar-file
		jarFileString=IOTools.class.getProtectionDomain().getCodeSource().getLocation().getPath();//.toURI();
	}

//========================================================================
	public IOTools(String jarFileUri)
	{
		jarFileString=jarFileUri;
	}

//========================================================================
	static void println(String s)
	{
		System.out.println(s);
	}

//========================================================================
	//uriInJar: without leading slash. leading slash will be cut
	static boolean copyJarContentToDisk(String uriInJar, String destUri)
	{
		//println("using jar: "+jarFileString);
		//println("trying to extract '"+uriInJar+"' to '"+destUri+"'");
		try
		{
			//InputStream 
			InputStream is=new FileInputStream(jarFileString);
			//http://stackoverflow.com/questions/10308221/how-to-copy-file-inside-jar-to-outside-the-jar
			JarInputStream jis=new JarInputStream(is);
			//get the first entry
			JarEntry entry=jis.getNextJarEntry();
			//we will loop through all the entries in the jar file
			while(entry!=null)
			{
				//test the entry.getName() against whatever you are looking for, etc
				if(1==2) //filter
				{
					println(entry.getName());
				}
				//get the next entry
				entry=jis.getNextJarEntry();
			}
			jis.close();

			copyJarFolder(jarFileString, uriInJar, destUri);

			return true;
		}
		catch(Exception e)
		{
			println("error reading from jar file and copy to disk: "+e.getMessage());
		};

		return false;
	}//end copyJarContentToDisk

//========================================================================
	//https://community.oracle.com/thread/1188356?start=0&tstart=0
	//Copies an entire folder out of a jar to a physical location.
	private static void copyJarFolder(String jarName, String folderName, String destUri)
	{
		boolean found=false;
		//cut leading "/"
		if(folderName.length()>1 && folderName.substring(0,1).equals("/"))
		{
			folderName=folderName.substring(1,folderName.length());
		}

		try
		{
			ZipFile z=new ZipFile(jarName);
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
						if(copyFromJar("/"+entry.getName(), new File(destUri+"/"+entry.getName())))
						{
							println("extracted '" + entry.getName()+"'");// to '"+destUri+"'" );
						}
					}
				}
			}//end while hasMoreElements
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		if(!found)
		{
			println("/!\\ '"+folderName+"' not found in jar '"+jarName+"'");
		}
	}//end copyJarFolder

//========================================================================
	/* 
	* Copies a file out of the jar to a physical location.
	* Doesn't need to be private, uses a resource stream, so may have
	* security errors if ran from webstart application 
	*/
	static boolean copyFromJar(String sResource, File fDest)
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
		catch(Exception e) {println("bad ----");}////
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
	}//end copyFromJar

//========================================================================
	static Font createFontFromJar(String fontUriInJar, float fontSize)
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
			println("could not load built-in font. "+e.getMessage());
			return null;
		}
		return f;
	}//end createFontFromJar

//========================================================================
	static ImageIcon createImageIconFromJar(String imageUriInJar)
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
			println("could not load built-in imge. "+e.getMessage());
			return null;
		}
		return ii;
	}//end createImageIconFromJar

//========================================================================
	//http://www.rgagnon.com/javadetails/java-0483.html
	static boolean deleteDirectory(File path) 
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
	static boolean openFile(File f) 
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
	static boolean loadProps(Properties props)
	{
		if(props!=null)
		{
			if(props.getProperty("_name")!=null){api._name=props.getProperty("_name");}
			if(props.getProperty("_sname")!=null){api._sname=props.getProperty("_sname");}
			if(props.getProperty("_connect")!=null){api._connect=Boolean.parseBoolean(props.getProperty("_connect"));}
			if(props.getProperty("_nopause")!=null){api._nopause=Boolean.parseBoolean(props.getProperty("_nopause"));}
			if(props.getProperty("test_mode")!=null){api.test_mode=Boolean.parseBoolean(props.getProperty("test_mode"));}
			if(props.getProperty("_limit")!=null){api._limit=Integer.parseInt(props.getProperty("_limit"));}
			if(props.getProperty("_drop")!=null){api._drop=Integer.parseInt(props.getProperty("_drop"));}
			if(props.getProperty("verbose")!=null){api.verbose=Boolean.parseBoolean(props.getProperty("verbose"));}
			if(props.getProperty("_update")!=null){api._update=Integer.parseInt(props.getProperty("_update"));}
			if(props.getProperty("lport_random")!=null){api.lport_random=Boolean.parseBoolean(props.getProperty("lport_random"));}
			if(props.getProperty("_lport")!=null){api._lport=Integer.parseInt(props.getProperty("_lport"));}

			if(props.getProperty("gui_osc_port_random")!=null){g.gui_osc_port_random=Boolean.parseBoolean(props.getProperty("gui_osc_port_random"));}
			if(props.getProperty("gui_osc_port")!=null){g.gui_osc_port=Integer.parseInt(props.getProperty("gui_osc_port"));}
			if(props.getProperty("keep_cache")!=null){g.keep_cache=Boolean.parseBoolean(props.getProperty("keep_cache"));}

			if(props.getProperty("_16")!=null){api._16=Boolean.parseBoolean(props.getProperty("_16"));}
			if(props.getProperty("_in")!=null){api._in=Integer.parseInt(props.getProperty("_in"));}
			if(props.getProperty("_target_host")!=null){api._target_host=props.getProperty("_target_host");}
			if(props.getProperty("_target_port")!=null){api._target_port=Integer.parseInt(props.getProperty("_target_port"));}
//autostart
			return true;
		}//end if props not null
		return false;
	}//end loadProps

//========================================================================
	//http://www.drdobbs.com/jvm/readwrite-properties-files-in-java/231000005
	static boolean loadSettings(String propertiesFileUri)
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
				println("default built-in settings loaded");
			}
			else
			{
				println("/!\\ could not load built-in default settings");
				////////////////				
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
						println("settings '"+propertiesFileUri+"' loaded");
						return true;
					}
					else
					{
						println("/!\\ could not load settings '"+propertiesFileUri+"'");
					}
				}
			}
			else
			{
				println("/!\\ could not load settings '"+propertiesFileUri+"'");
			}
		}
		catch (Exception e)
		{
			is=null; 
			println("/!\\ file '"+propertiesFileUri+"' not found");
		}
		return false;
	}//end loadSettings

//========================================================================
	static boolean saveSettings(String propertiesFileUri)
	{
		try 
		{
			Properties props = new Properties();

			props.setProperty("_name", api._name);
			props.setProperty("_sname", api._sname);
			props.setProperty("_connect", api._connect ? "true" : "false");
			props.setProperty("_nopause", api._nopause ? "true" : "false");
			props.setProperty("test_mode", api.test_mode ? "true" : "false");
			props.setProperty("_limit", ""+api._limit);
			props.setProperty("_drop", ""+api._drop);
			props.setProperty("verbose", api.verbose ? "true" : "false");
			props.setProperty("_update", ""+api._update);
			props.setProperty("lport_random", api.lport_random ? "true" : "false");
			props.setProperty("_lport", ""+api._lport);

			props.setProperty("gui_osc_port_random", g.gui_osc_port_random ? "true" : "false");
			props.setProperty("gui_osc_port", ""+g.gui_osc_port);
			props.setProperty("keep_cache", g.keep_cache ? "true" : "false");

			props.setProperty("_16", api._16 ? "true" : "false");
			props.setProperty("_in", ""+api._in);
			props.setProperty("_target_host", api._target_host);
			props.setProperty("_target_port", ""+api._target_port);
//autostart
			File f = new File(propertiesFileUri);
			OutputStream out = new FileOutputStream(f);
			props.store(out, "This file is read by audio_rxtx GUI if found");

			println("settings '"+propertiesFileUri+"' saved");

			return true;
		}
		catch (Exception e ) 
		{
			println("/!\\ could not save settings '"+propertiesFileUri+"'");
			e.printStackTrace();
		}
		return false;
	}//end saveSettings
}//end class IOTools
