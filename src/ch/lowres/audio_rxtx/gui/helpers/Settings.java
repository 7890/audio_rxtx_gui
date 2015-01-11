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

/**
* 
*/
//persistent settings read/write
//========================================================================
public class Settings
{
	private static Main m;
	private static GUI g;
	private static Fonts f;
	private static Languages l;

//========================================================================
	public static boolean loadProps(Properties props)
	{
		if(props==null)
		{
			return false;
		}
		try
		{
			if(props.getProperty("s._name")!=null){m.apis._name=props.getProperty("s._name");}
			if(props.getProperty("s._sname")!=null){m.apis._sname=props.getProperty("s._sname");}
			if(props.getProperty("s._connect")!=null){m.apis._connect=Boolean.parseBoolean(props.getProperty("s._connect"));}
			if(props.getProperty("s._nopause")!=null){m.apis._nopause=Boolean.parseBoolean(props.getProperty("s._nopause"));}
			if(props.getProperty("s.test_mode")!=null){m.apis.test_mode=Boolean.parseBoolean(props.getProperty("s.test_mode"));}
			if(props.getProperty("s._limit")!=null){m.apis._limit=Integer.parseInt(props.getProperty("s._limit"));}
			if(props.getProperty("s._drop")!=null){m.apis._drop=Integer.parseInt(props.getProperty("s._drop"));}
			if(props.getProperty("s.verbose")!=null){m.apis.verbose=Boolean.parseBoolean(props.getProperty("s.verbose"));}
			if(props.getProperty("s._update")!=null){m.apis._update=Integer.parseInt(props.getProperty("s._update"));}
			if(props.getProperty("s.lport_random")!=null){m.apis.lport_random=Boolean.parseBoolean(props.getProperty("s.lport_random"));}
			if(props.getProperty("s._lport")!=null){m.apis._lport=Integer.parseInt(props.getProperty("s._lport"));}
			if(props.getProperty("s.autostart")!=null){m.apis.autostart=Boolean.parseBoolean(props.getProperty("s.autostart"));}

			if(props.getProperty("s._16")!=null){m.apis._16=Boolean.parseBoolean(props.getProperty("s._16"));}
			if(props.getProperty("s._in")!=null){m.apis._in=Integer.parseInt(props.getProperty("s._in"));}
			if(props.getProperty("s._target_host")!=null){m.apis._target_host=props.getProperty("s._target_host");}
			if(props.getProperty("s._target_port")!=null){m.apis._target_port=Integer.parseInt(props.getProperty("s._target_port"));}

			if(props.getProperty("r._name")!=null){m.apir._name=props.getProperty("r._name");}
			if(props.getProperty("r._sname")!=null){m.apir._sname=props.getProperty("r._sname");}
			if(props.getProperty("r._connect")!=null){m.apir._connect=Boolean.parseBoolean(props.getProperty("r._connect"));}
			if(props.getProperty("r.test_mode")!=null){m.apir.test_mode=Boolean.parseBoolean(props.getProperty("r.test_mode"));}
			if(props.getProperty("r._limit")!=null){m.apir._limit=Integer.parseInt(props.getProperty("r._limit"));}
			if(props.getProperty("r.verbose")!=null){m.apir.verbose=Boolean.parseBoolean(props.getProperty("r.verbose"));}
			if(props.getProperty("r._update")!=null){m.apir._update=Integer.parseInt(props.getProperty("r._update"));}
			if(props.getProperty("r.lport_random")!=null){m.apir.lport_random=Boolean.parseBoolean(props.getProperty("r.lport_random"));}

			if(props.getProperty("r._offset")!=null){m.apir._offset=Integer.parseInt(props.getProperty("r._offset"));}
			if(props.getProperty("r._pre")!=null){m.apir._pre=Integer.parseInt(props.getProperty("r._pre"));}
			if(props.getProperty("r._max")!=null){m.apir._max=Integer.parseInt(props.getProperty("r._max"));}
			if(props.getProperty("r._rere")!=null){m.apir._rere=Boolean.parseBoolean(props.getProperty("r._rere"));}
			if(props.getProperty("r._reuf")!=null){m.apir._reuf=Boolean.parseBoolean(props.getProperty("r._reuf"));}
			if(props.getProperty("r._nozero")!=null){m.apir._nozero=Boolean.parseBoolean(props.getProperty("r._nozero"));}
			if(props.getProperty("r._norbc")!=null){m.apir._norbc=Boolean.parseBoolean(props.getProperty("r._norbc"));}
			if(props.getProperty("r._close")!=null){m.apir._close=Boolean.parseBoolean(props.getProperty("r._close"));}
			if(props.getProperty("r.autostart")!=null){m.apir.autostart=Boolean.parseBoolean(props.getProperty("r.autostart"));}

			if(props.getProperty("r._16")!=null){m.apir._16=Boolean.parseBoolean(props.getProperty("r._16"));}
			if(props.getProperty("r._out")!=null){m.apir._out=Integer.parseInt(props.getProperty("r._out"));}
			if(props.getProperty("r._lport")!=null){m.apir._lport=Integer.parseInt(props.getProperty("r._lport"));}

			if(props.getProperty("s.gui_osc_port_random")!=null){g.gui_osc_port_random_s=Boolean.parseBoolean(props.getProperty("s.gui_osc_port_random"));}
			if(props.getProperty("s.gui_osc_port")!=null){g.gui_osc_port_s=Integer.parseInt(props.getProperty("s.gui_osc_port"));}

			if(props.getProperty("r.gui_osc_port_random")!=null){g.gui_osc_port_random_r=Boolean.parseBoolean(props.getProperty("r.gui_osc_port_random"));}
			if(props.getProperty("r.gui_osc_port")!=null){g.gui_osc_port_r=Integer.parseInt(props.getProperty("r.gui_osc_port"));}

			if(props.getProperty("keep_cache")!=null){g.keep_cache=Boolean.parseBoolean(props.getProperty("keep_cache"));}
			if(props.getProperty("show_both_panels")!=null){g.show_both_panels=Boolean.parseBoolean(props.getProperty("show_both_panels"));}

			if(props.getProperty("language")!=null){Languages.set(props.getProperty("language"));}

			if(props.getProperty("use_internal_font")!=null){f.use_internal_font=Boolean.parseBoolean(props.getProperty("use_internal_font"));}
			if(props.getProperty("font_name")!=null){f.set(props.getProperty("font_name"));}
			if(props.getProperty("font_size")!=null){f.fontDefaultSize=Float.parseFloat(props.getProperty("font_size"));}
			if(props.getProperty("font_style")!=null){f.fontNormalStyle=Integer.parseInt(props.getProperty("font_style"));}

			return true;
		}//end try
		catch(Exception e)
		{///
		}
		return false;
	}//end loadProps

//========================================================================
	//http://www.drdobbs.com/jvm/readwrite-properties-files-in-java/231000005
	public static boolean load(String propertiesFileUri)
	{
		Properties props=new Properties();
		InputStream is=null;
 
		//try loading from the current directory
		try 
		{
			//loading default props from jar
			is = Settings.class.getResourceAsStream("/resources/etc/"+m.defaultPropertiesFileName);

			if(is!=null)
			{
				props.load(is);
				loadProps(props);
				m.p(l.tr("Default built-in settings loaded"));
			}
			else
			{
				m.e("could not load built-in default settings");
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
						m.p(l.tr("Settings '")+propertiesFileUri+l.tr("' loaded"));
						return true;
					}
					else
					{
						m.e(l.tr("Could not load settings '")+propertiesFileUri+"'");
					}
				}
				else
				{
					m.e(l.tr("Could not load settings '")+propertiesFileUri+"'");
				}
			}
			else
			{
				m.e(l.tr("Could not load settings '")+propertiesFileUri+"'");
			}
		}
		catch (Exception e)
		{
			is=null;
			m.e(l.tr("File '")+propertiesFileUri+l.tr("' not found"));
		}
		return false;
	}//end loadSettings

//========================================================================
	public static boolean save(String propertiesFileUri)
	{
		try 
		{
			Properties props = new Properties();

			props.setProperty("s._name", m.apis._name);
			props.setProperty("s._sname", m.apis._sname);
			props.setProperty("s._connect", m.apis._connect ? "true" : "false");
			props.setProperty("s._nopause", m.apis._nopause ? "true" : "false");
			props.setProperty("s.test_mode", m.apis.test_mode ? "true" : "false");
			props.setProperty("s._limit", ""+m.apis._limit);
			props.setProperty("s._drop", ""+m.apis._drop);
			props.setProperty("s.verbose", m.apis.verbose ? "true" : "false");
			props.setProperty("s._update", ""+m.apis._update);
			props.setProperty("s.lport_random", m.apis.lport_random ? "true" : "false");
			props.setProperty("s._lport", ""+m.apis._lport);
			props.setProperty("s.autostart", ""+m.apis.autostart);

			props.setProperty("s._16", m.apis._16 ? "true" : "false");
			props.setProperty("s._in", ""+m.apis._in);
			props.setProperty("s._target_host", m.apis._target_host);
			props.setProperty("s._target_port", ""+m.apis._target_port);

			props.setProperty("r._name", m.apir._name);
			props.setProperty("r._sname", m.apir._sname);
			props.setProperty("r._connect", m.apir._connect ? "true" : "false");
			props.setProperty("r.test_mode", m.apir.test_mode ? "true" : "false");
			props.setProperty("r._limit", ""+m.apir._limit);
			props.setProperty("r.verbose", m.apir.verbose ? "true" : "false");
			props.setProperty("r._update", ""+m.apir._update);

			props.setProperty("r._offset", ""+m.apir._offset);
			props.setProperty("r._pre", ""+m.apir._pre);
			props.setProperty("r._max", ""+m.apir._max);
			props.setProperty("r._rere", m.apir._rere ? "true" : "false");
			props.setProperty("r._reuf", m.apir._reuf ? "true" : "false");
			props.setProperty("r._nozero", m.apir._nozero ? "true" : "false");
			props.setProperty("r._norbc", m.apir._norbc ? "true" : "false");
			props.setProperty("r._close", m.apir._close ? "true" : "false");
			props.setProperty("r.autostart", ""+m.apir.autostart);

			props.setProperty("r._16", m.apir._16 ? "true" : "false");
			props.setProperty("r._out", ""+m.apir._out);
			props.setProperty("r.lport_random", m.apir.lport_random ? "true" : "false");//not on gui
			props.setProperty("r._lport", ""+m.apir._lport);

			props.setProperty("s.gui_osc_port_random", g.gui_osc_port_random_s ? "true" : "false");
			props.setProperty("s.gui_osc_port", ""+g.gui_osc_port_s);

			props.setProperty("r.gui_osc_port_random", g.gui_osc_port_random_r ? "true" : "false");
			props.setProperty("r.gui_osc_port", ""+g.gui_osc_port_r);

			props.setProperty("keep_cache", g.keep_cache ? "true" : "false");
			props.setProperty("show_both_panels", g.show_both_panels ? "true" : "false");

			props.setProperty("language",Languages.lang);

			props.setProperty("use_internal_font", f.use_internal_font ? "true" : "false");
			props.setProperty("font_name", f.fontName);
			props.setProperty("font_size", ""+f.fontDefaultSize);
			props.setProperty("font_style", ""+f.fontNormalStyle);

			File f = new File(propertiesFileUri);
			OutputStream out = new FileOutputStream(f);
			props.store(out, "This file is read by audio_rxtx GUI if found");

			m.p(l.tr("Settings '")+propertiesFileUri+l.tr("' saved"));

			return true;
		}
		catch (Exception e ) 
		{
			m.e(l.tr("Could not save settings '")+propertiesFileUri+"'");
			e.printStackTrace();
		}
		return false;
	}//end saveSettings
}//end class IOTools
