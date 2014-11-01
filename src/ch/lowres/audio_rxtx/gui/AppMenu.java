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

package ch.lowres.audio_rxtx.gui;

import java.awt.*;
import java.awt.event.*;

import com.illposed.osc.*;
import java.net.InetAddress;

import java.io.File;

import com.magelang.tabsplitter.*;

//========================================================================
public class AppMenu extends MenuBar
{
	static Main g;

	static Menu menu_main;
	static Menu menu_settings;
	static Menu menu_view;
	static Menu menu_help;

	static MenuItem mi_start_transmission;
	static MenuItem mi_stop_transmission;
	static MenuItem mi_quit;

	static MenuItem mi_load_default_settings;
	static MenuItem mi_save_default_settings;
	static MenuItem mi_load_settings;
	static MenuItem mi_save_settings_as;
	static MenuItem mi_configure_dialog;

	static MenuItem mi_view_send;
	static MenuItem mi_view_receive;
	static MenuItem mi_view_both;

	static MenuItem mi_about;
	static MenuItem mi_license;
	static MenuItem mi_doc;

//========================================================================
	public AppMenu()
	{
		createMenu();
		addActionListeners();
	}

//========================================================================
	void createMenu()
	{
		//http://stackoverflow.com/questions/17767950/java-menubar-cuts-into-my-applet

		menu_main=new Menu("Main");
		menu_settings=new Menu("Settings");
		menu_view=new Menu("View");
		menu_help=new Menu("Help");

		add(menu_main);
		add(menu_settings);
		add(menu_view);
		add(menu_help);

		//main items
		mi_start_transmission=new MenuItem("Start Transmission",
			new MenuShortcut(KeyEvent.VK_A, false)
		);
		mi_stop_transmission=new MenuItem("Stop Transmission",
			new MenuShortcut(KeyEvent.VK_D, false)
		);
		mi_quit=new MenuItem("Quit",
			new MenuShortcut(KeyEvent.VK_Q, false)
		);

		menu_main.add(mi_start_transmission);
		menu_main.add(mi_stop_transmission);
		mi_stop_transmission.setEnabled(false);

		menu_main.add(new MenuItem("-"));
		menu_main.add(mi_quit);

		//settings items
		mi_load_default_settings=new MenuItem("Load Default",
			new MenuShortcut((KeyEvent.VK_COMMA), false)
		);
		mi_save_default_settings=new MenuItem("Save As Default",
			new MenuShortcut(KeyEvent.VK_PERIOD, false)
		);
		mi_load_settings=new MenuItem("Load...",
			new MenuShortcut(KeyEvent.VK_O, false)
		);
		mi_save_settings_as=new MenuItem("Save As...",
			new MenuShortcut(KeyEvent.VK_S, false)
		);
		mi_configure_dialog=new MenuItem("Configure...",
			new MenuShortcut(KeyEvent.VK_L, false)
		);

		menu_settings.add(mi_load_default_settings);
		menu_settings.add(mi_save_default_settings);
		menu_settings.add(new MenuItem("-"));
		menu_settings.add(mi_load_settings);
		menu_settings.add(mi_save_settings_as);
		menu_settings.add(new MenuItem("-"));
		menu_settings.add(mi_configure_dialog);

		//view items
		mi_view_send=new MenuItem("Show Send",
			new MenuShortcut(KeyEvent.VK_G, false)
		);
		mi_view_receive=new MenuItem("Show Receive",
			new MenuShortcut(KeyEvent.VK_H, false)
		);
		mi_view_both=new MenuItem("Show Both",
			new MenuShortcut(KeyEvent.VK_J, false)
		);

		menu_view.add(mi_view_send);
		menu_view.add(mi_view_receive);
		menu_view.add(mi_view_both);

		//help items
		mi_about=new MenuItem("About...");
		mi_license=new MenuItem("Liecense...");
		mi_doc=new MenuItem("Manual (PDF)...",
			new MenuShortcut(KeyEvent.VK_1, false)
		);

		menu_help.add(mi_about);
		menu_help.add(mi_license);
		menu_help.add(mi_doc);
	}//end createMenu

//========================================================================
	void addActionListeners()
	{
		mi_start_transmission.addActionListener(new ActionListener()
		{
			@Override public void actionPerformed(ActionEvent e)
			{

				if(g.frontSend.readForm())
				{
					g.startTransmission();
				}
			}
		});

		mi_stop_transmission.addActionListener(new ActionListener()
		{
			@Override public void actionPerformed(ActionEvent e)
			{
				g.stopTransmission();
			}
		});

		mi_quit.addActionListener(new ActionListener()
		{
			@Override public void actionPerformed(ActionEvent e)
			{
				//possibly needs confirmation when transmission running
				try 
				{
					OSCMessage msg=new OSCMessage("/quit");
					g.sender.send(msg);
				} 
				catch (Exception oscex) 
				{///
				}
				System.exit(0);
			}
		});

		//settings
		mi_load_default_settings.addActionListener(new ActionListener()
		{
			@Override public void actionPerformed(ActionEvent e)
			{
				IOTools.loadSettings(g.defaultPropertiesFileName);
				g.frontSend.setValues();
				g.configure.setValues();
				g.setStatus("Default Settings Loaded");
			}
		});

		mi_save_default_settings.addActionListener(new ActionListener()
		{
			@Override public void actionPerformed(ActionEvent e)
			{
				//get current data from front form
				if(g.frontSend.readForm())
				{
					IOTools.saveSettings(g.defaultPropertiesFileName);
					g.setStatus("Default Settings Saved");
				}
				else
				{
					g.setStatus("Nothing Saved. Host is invalid or was not found");
					g.frontSend.text_target_host.requestFocus();
				}
			}
		});

		mi_load_settings.addActionListener(new ActionListener()
		{
			@Override public void actionPerformed(ActionEvent e)
			{
				FileDialog chooser = new FileDialog(g.mainframe);
				//chooser.setFilenameFilter(new FolderFilter());
				chooser.setMode(FileDialog.LOAD);
				chooser.setVisible(true);
				String fileToLoad = chooser.getFile();

				if(fileToLoad==null)
				{
					g.setStatus("No File Selected, Nothing Loaded");
				}
				else
				{
					fileToLoad = chooser.getDirectory()+File.separator+chooser.getFile();
					//System.out.println("load settings from file: "+fileToLoad);
					File f=new File(fileToLoad);
					if(!f.canRead())
					{
						g.setStatus("Error: Could Not Read File");
					}
					else
					{
						g.setStatus("Loading File '"+fileToLoad+"'...");
						IOTools.loadSettings(fileToLoad);
						g.frontSend.setValues();
						g.configure.setValues();
						g.setStatus("Settings Loaded");
					}
				}

				//set values in forms
				g.frontSend.setValues();
				g.configure.setValues();

				g.frontSend.button_start_transmission.requestFocus();
			}
		});

		mi_save_settings_as.addActionListener(new ActionListener()
		{
			@Override public void actionPerformed(ActionEvent e)
			{
				//get current data from front form
				if(g.frontSend.readForm())
				{
					FileDialog chooser = new FileDialog(g.mainframe, "Save Settings As...");
					//chooser.setFilenameFilter(new FolderFilter());
					chooser.setMode(FileDialog.SAVE);
					chooser.setVisible(true);
					String fileToSave = chooser.getFile();

					if(fileToSave==null)
					{
						g.setStatus("No File Selected, Nothing Saved");
					}
					else
					{
						fileToSave = chooser.getDirectory()+File.separator+chooser.getFile();
						//System.out.println("save settings to file: "+fileToSave);
						File f=new File(fileToSave);
						if(f.exists())
						{
							//overwrite
							g.setStatus("Updating File '"+fileToSave+"'...");
							IOTools.saveSettings(fileToSave);
							g.setStatus("Settings Saved");
						}
						else
						{
							try
							{
								if(!f.createNewFile())
								{
									g.setStatus("Error: Could Not Write File");
								}
								else
								{
									g.setStatus("Saving File '"+fileToSave+"'...");
									IOTools.saveSettings(fileToSave);
									g.setStatus("Settings Saved");
								}
							}catch(Exception fileEx)
							{///
							}
						}
					}
				}
				else
				{
					g.setStatus("Nothing Saved. Host is invalid or was not found");
					g.frontSend.text_target_host.requestFocus();
					//text_target_host.selectAll();
				}
			}//end actionPerformed
		});

		mi_configure_dialog.addActionListener(new ActionListener()
		{
			@Override public void actionPerformed(ActionEvent e)
			{
				g.configure.setValues();
				g.configure.setVisible(true);
			}
		});

		//view
		mi_view_send.addActionListener(new ActionListener()
		{
			@Override public void actionPerformed(ActionEvent e)
			{

				SplitterPanel p=g.tabSplitter.getSplitterPanel();
				if(p!=null)
				{
					p.separate(g.tabSend);
				}
				else
				{
					g.tabSplitter.show(0);
				}
			}
		});

		mi_view_receive.addActionListener(new ActionListener()
		{
			@Override public void actionPerformed(ActionEvent e)
			{
				SplitterPanel p=g.tabSplitter.getSplitterPanel();
				if(p!=null)
				{
					p.separate(g.tabReceive);
				}
				else
				{
					g.tabSplitter.show(1);
				}
			}
		});

		mi_view_both.addActionListener(new ActionListener()
		{
			@Override public void actionPerformed(ActionEvent e)
			{

				g.tabSplitter.mergeTabs(0,1);
			}
		});

		//help
		mi_doc.addActionListener(new ActionListener()
		{
			@Override public void actionPerformed(ActionEvent e)
			{
				//ev simply open a textfile instead of pdf
				String docPdf=g.tmpDir+File.separator+"resources"
					+File.separator+"doc"+File.separator+"jack_audio_send.pdf";

				System.out.println("opening file "+docPdf);
				IOTools.openFile(new File(docPdf));
			}
		});

		mi_about.addActionListener(new ActionListener()
		{
			@Override public void actionPerformed(ActionEvent e)
			{
				g.about.setVisible(true);
			}
		});

		mi_license.addActionListener(new ActionListener()
		{
			@Override public void actionPerformed(ActionEvent e)
			{
				String docTxt=g.tmpDir+File.separator+"resources"
					+File.separator+"COPYING.txt";

				System.out.println("opening file "+docTxt);
				IOTools.openFile(new File(docTxt));
			}
		});
	}//end addActionListeners

//========================================================================
	static void setForRunning()
	{
		mi_load_default_settings.setEnabled(false);
		mi_load_settings.setEnabled(false);;
		mi_configure_dialog.setEnabled(false);
		mi_start_transmission.setEnabled(false);
		mi_stop_transmission.setEnabled(true);
	}

//========================================================================
	static void setForFrontScreen()
	{
		mi_load_default_settings.setEnabled(true);
		mi_load_settings.setEnabled(true);;
		mi_configure_dialog.setEnabled(true);
		mi_start_transmission.setEnabled(true);
		mi_stop_transmission.setEnabled(false);
	}
}//end AppMenu
