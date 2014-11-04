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

import javax.swing.*;

//========================================================================
public class AppMenu extends JMenuBar
{
	static Main g;

	static JMenu menu_main;
	static JMenu menu_settings;
	static JMenu menu_view;
	static JMenu menu_help;

	static JMenuItem mi_start_transmissionSend;
	static JMenuItem mi_stop_transmissionSend;

	static JMenuItem mi_start_transmissionReceive;
	static JMenuItem mi_stop_transmissionReceive;

	static JMenuItem mi_quit;

	static JMenuItem mi_load_default_settings;
	static JMenuItem mi_save_default_settings;
	static JMenuItem mi_load_settings;
	static JMenuItem mi_save_settings_as;
	static JMenuItem mi_configure_dialog;

	static JMenuItem mi_view_send;
	static JMenuItem mi_view_receive;
	static JMenuItem mi_view_both;

	static JMenuItem mi_about;
	static JMenuItem mi_license;
	static JMenuItem mi_doc;

	static JMenuItem mi_report_issue;
	static JMenuItem mi_check_for_update;

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

		menu_main=new JMenu("Main",true);
		menu_main.setMnemonic(KeyEvent.VK_M);

		menu_settings=new JMenu("Settings");
		menu_settings.setMnemonic(KeyEvent.VK_S);

		menu_view=new JMenu("View");
		menu_view.setMnemonic(KeyEvent.VK_V);

		menu_help=new JMenu("Help");
		menu_help.setMnemonic(KeyEvent.VK_H);

		add(menu_main);
		add(menu_settings);
		add(menu_view);
		add(menu_help);

		//main items
		mi_start_transmissionSend=new JMenuItem("Start Transmission (Send)");
		mi_start_transmissionSend.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));


		mi_stop_transmissionSend=new JMenuItem("Stop Transmission (Send)");
		mi_stop_transmissionSend.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_MASK));

		mi_start_transmissionReceive=new JMenuItem("Start Transmission (Receive)");
		mi_start_transmissionReceive.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_MASK));

		mi_stop_transmissionReceive=new JMenuItem("Stop Transmission (Receive)");
		mi_stop_transmissionReceive.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));

		mi_quit=new JMenuItem("Quit");
		mi_quit.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));

		menu_main.add(mi_start_transmissionSend);
		menu_main.add(mi_stop_transmissionSend);
		menu_main.add(new JSeparator());
		menu_main.add(mi_start_transmissionReceive);
		menu_main.add(mi_stop_transmissionReceive);
		mi_stop_transmissionSend.setEnabled(false);
		mi_stop_transmissionReceive.setEnabled(false);
		menu_main.add(new JSeparator());
		menu_main.add(mi_quit);

		//settings items
		mi_load_default_settings=new JMenuItem("Load Default");
		mi_load_default_settings.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, InputEvent.CTRL_MASK));

		mi_save_default_settings=new JMenuItem("Save As Default");
		mi_save_default_settings.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_PERIOD, InputEvent.CTRL_MASK));

		mi_load_settings=new JMenuItem("Load...");
		mi_load_settings.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));

		mi_save_settings_as=new JMenuItem("Save As...");
		mi_save_settings_as.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));

		mi_configure_dialog=new JMenuItem("Configure...");
		mi_configure_dialog.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_MASK));

		menu_settings.add(mi_load_default_settings);
		menu_settings.add(mi_save_default_settings);
		menu_settings.add(new JSeparator());
		menu_settings.add(mi_load_settings);
		menu_settings.add(mi_save_settings_as);
		menu_settings.add(new JSeparator());
		menu_settings.add(mi_configure_dialog);

		//view items
		mi_view_send=new JMenuItem("Show Send");
		mi_view_send.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_MASK));

		mi_view_receive=new JMenuItem("Show Receive");
		mi_view_receive.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_MASK));

		mi_view_both=new JMenuItem("Show Both");
		mi_view_both.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_J, InputEvent.CTRL_MASK));

		menu_view.add(mi_view_send);
		menu_view.add(mi_view_receive);
		menu_view.add(mi_view_both);

		//help items
		mi_about=new JMenuItem("About...");
		mi_license=new JMenuItem("Liecense...");
		mi_doc=new JMenuItem("Manual (PDF)...");
		mi_doc.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_1, InputEvent.CTRL_MASK));

		mi_report_issue=new JMenuItem("Report Issuse...");
		mi_check_for_update=new JMenuItem("Check For Updates...");

		menu_help.add(mi_about);
		menu_help.add(mi_license);
		menu_help.add(mi_doc);
		menu_help.add(new JSeparator());
		menu_help.add(mi_report_issue);
//dummy
		menu_help.add(mi_check_for_update);

	}//end createMenu

//========================================================================
	void addActionListeners()
	{
		mi_start_transmissionSend.addActionListener(new ActionListener()
		{
			@Override public void actionPerformed(ActionEvent e)
			{
				if(g.frontSend.readForm())
				{
					g.startTransmissionSend();
				}
			}
		});

		mi_stop_transmissionSend.addActionListener(new ActionListener()
		{
			@Override public void actionPerformed(ActionEvent e)
			{
				g.stopTransmissionSend();
			}
		});


		mi_start_transmissionReceive.addActionListener(new ActionListener()
		{
			@Override public void actionPerformed(ActionEvent e)
			{

				if(g.frontReceive.readForm())
				{
					g.startTransmissionReceive();
				}
			}
		});

		mi_stop_transmissionReceive.addActionListener(new ActionListener()
		{
			@Override public void actionPerformed(ActionEvent e)
			{
				g.stopTransmissionReceive();
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
					g.OscOutSend.send(msg);
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
				g.frontReceive.setValues();
				g.configure.setValues();
				g.setStatus("Default Settings Loaded");
			}
		});

		mi_save_default_settings.addActionListener(new ActionListener()
		{
			@Override public void actionPerformed(ActionEvent e)
			{
				//get current data from front form
				if(g.frontSend.readForm() && g.frontReceive.readForm())
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
						g.setStatus("Settings Loaded");
					}
				}

				//set values in forms
				g.frontSend.setValues();
				g.frontReceive.setValues();
				g.configure.setValues();

				g.frontSend.button_default.requestFocus();
			}
		});

		mi_save_settings_as.addActionListener(new ActionListener()
		{
			@Override public void actionPerformed(ActionEvent e)
			{
				//get current data from front form
				if(g.frontSend.readForm() && g.frontReceive.readForm())
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
				g.mainframe.setSize(
					g.panelWidth+g.mainframe.getInsets().left+g.mainframe.getInsets().right,
					g.panelHeight+g.mainframe.getInsets().top+g.mainframe.getInsets().bottom
				);
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
				g.mainframe.setSize(
					g.panelWidth+g.mainframe.getInsets().left+g.mainframe.getInsets().right,
					g.panelHeight+g.mainframe.getInsets().top+g.mainframe.getInsets().bottom
				);
			}
		});

		mi_view_both.addActionListener(new ActionListener()
		{
			@Override public void actionPerformed(ActionEvent e)
			{
				g.tabSplitter.mergeTabs(0,1);
				g.mainframe.setSize(
					2*g.panelWidth+g.mainframe.getInsets().left+g.mainframe.getInsets().right,
					g.panelHeight+g.mainframe.getInsets().top+g.mainframe.getInsets().bottom
				);
			}
		});

		//help

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

				g.p("opening file "+docTxt);
				IOTools.openFile(new File(docTxt));
			}
		});

		mi_doc.addActionListener(new ActionListener()
		{
			@Override public void actionPerformed(ActionEvent e)
			{
				//ev simply open a textfile instead of pdf
				String docPdf=g.tmpDir+File.separator+"resources"
					+File.separator+"doc"+File.separator+"jack_audio_send.pdf";

				g.p("opening file "+docPdf);
				IOTools.openFile(new File(docPdf));
			}
		});

		mi_report_issue.addActionListener(new ActionListener()
		{
			@Override public void actionPerformed(ActionEvent e)
			{
				IOTools.openInBrowser(g.reportIssueUrl);
			}
		});

		mi_check_for_update.addActionListener(new ActionListener()
		{
			@Override public void actionPerformed(ActionEvent e)
			{
				/////////test thread
				IOTools.checkForNewerVersion(g.newestVersionFileUrl);
			}
		});

	}//end addActionListeners

//========================================================================
	static void setForRunningSend()
	{
		mi_load_default_settings.setEnabled(false);
		mi_load_settings.setEnabled(false);;
		mi_configure_dialog.setEnabled(false);
		mi_start_transmissionSend.setEnabled(false);
		mi_stop_transmissionSend.setEnabled(true);
	}

//========================================================================
	static void setForFrontScreenSend()
	{
		mi_load_default_settings.setEnabled(true);
		mi_load_settings.setEnabled(true);;
		mi_configure_dialog.setEnabled(true);
		mi_start_transmissionSend.setEnabled(true);
		mi_stop_transmissionSend.setEnabled(false);
	}

//========================================================================
	static void setForRunningReceive()
	{
		mi_load_default_settings.setEnabled(false);
		mi_load_settings.setEnabled(false);;
		mi_configure_dialog.setEnabled(false);
		mi_start_transmissionReceive.setEnabled(false);
		mi_stop_transmissionReceive.setEnabled(true);
	}

//========================================================================
	static void setForFrontScreenReceive()
	{
		mi_load_default_settings.setEnabled(true);
		mi_load_settings.setEnabled(true);;
		mi_configure_dialog.setEnabled(true);
		mi_start_transmissionReceive.setEnabled(true);
		mi_stop_transmissionReceive.setEnabled(false);
	}
}//end AppMenu
