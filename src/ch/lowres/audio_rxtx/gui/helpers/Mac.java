/* part of audio_rxtx GUI
 * https://github.com/7890/audio_rxtx_gui
 *
 * Copyright (C) 2015 Thomas Brand <tom@trellis.ch>
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

//========================================================================
public class Mac
{
	private static GUI g;

	//set dockname on osx:
	//java -Xdock:name="appname"

//========================================================================
	public static void init()
	{
		//AboutHandler, PreferencesHandler, AppReOpenedListener, OpenFilesHandler, PrintFilesHandler, QuitHandler, QuitResponse 
		com.apple.eawt.Application application = com.apple.eawt.Application.getApplication();

		 //need to enable the preferences option manually
		application.setEnabledPreferencesMenu(true);

		application.setAboutHandler(new com.apple.eawt.AboutHandler()
		{
			@Override
			public void handleAbout(com.apple.eawt.AppEvent.AboutEvent e)
			{
				//javax.swing.JOptionPane.showMessageDialog(null, "hello");
				if(g.about!=null)
				{
					g.about.pack();
					g.about.setVisible(true);
				}
			}
		});

		application.setPreferencesHandler(new com.apple.eawt.PreferencesHandler()
		{
			@Override
			public void handlePreferences(com.apple.eawt.AppEvent.PreferencesEvent e)
			{
				if(g.configure!=null)
				{
					g.configure.setVisible(true);
				}
			}
		});

		//only works with Aqua Look and Feel
		//application.setDefaultMenuBar(g.applicationMenu);
	}//end init
}//end class Mac
