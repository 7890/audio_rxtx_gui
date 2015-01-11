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
import ch.lowres.audio_rxtx.gui.helpers.*;

import java.awt.*;
import java.awt.event.*;

import com.illposed.osc.*;
import java.net.InetAddress;

import java.io.File;

import javax.swing.*;

/**
* Extended JMenuBar, holding all menu items, actions and shortcuts.
*/
//========================================================================
public class AppMenu extends JMenuBar
{
/////should make shortcuts bigger
//http://stackoverflow.com/questions/27541334/how-to-change-the-color-font-of-the-shortcut-text-inside-a-java-jmenuitem-ctrl

	private static Main m;
	private static GUI g;
	private static Fonts f;
	private static Languages l;

	private static JMenu menu_main;
	private static JMenu menu_settings;
	private static JMenu menu_view;
	private static JMenu menu_help;

	private static JMenuItem mi_start_transmissionSend;
	private static JMenuItem mi_stop_transmissionSend;

	private static JMenuItem mi_start_transmissionReceive;
	private static JMenuItem mi_stop_transmissionReceive;

	private static JMenuItem mi_quit;

	private static JMenuItem mi_load_default_settings;
	private static JMenuItem mi_save_default_settings;
	private static JMenuItem mi_load_settings;
	private static JMenuItem mi_save_settings_as;
	private static JMenuItem mi_configure_dialog;

	private static JMenuItem mi_view_send;
	private static JMenuItem mi_view_receive;
	private static JMenuItem mi_view_both;

	private static JMenuItem mi_doc;
	private static JMenuItem mi_about;
	private static JMenuItem mi_info;
	private static JMenuItem mi_license;

	private static JMenuItem mi_report_issue;
	private static JMenuItem mi_check_for_update;

//========================================================================
	public AppMenu()
	{
		createMenu();
		addActionListeners();
	}

//========================================================================
	private void createMenu()
	{
		//http://stackoverflow.com/questions/17767950/java-menubar-cuts-into-my-applet
		//menmonics depend on language

		//on osx, in the native menu, preferences shortcut is "cmd-,"
		//cmd-h: hide cmd-q: quit

		setFont(f.fontNormal);
		menu_main=new JMenu(Languages.removeMnemonic(l.tr("_Transmission")));
		menu_main.setMnemonic(Languages.getMnemonicKeyEvent(l.tr("_Transmission")));
		menu_main.setFont(f.fontNormal);

		menu_settings=new JMenu(Languages.removeMnemonic(l.tr("_Settings")));
		menu_settings.setMnemonic(Languages.getMnemonicKeyEvent(l.tr("_Settings")));
		menu_settings.setFont(f.fontNormal);

		menu_view=new JMenu(Languages.removeMnemonic(l.tr("_View")));
		menu_view.setMnemonic(Languages.getMnemonicKeyEvent(l.tr("_View")));
		menu_view.setFont(f.fontNormal);

		menu_help=new JMenu(Languages.removeMnemonic(l.tr("_Help")));
		menu_help.setMnemonic(Languages.getMnemonicKeyEvent(l.tr("_Help")));
		menu_help.setFont(f.fontNormal);

		add(menu_main);
		add(menu_settings);
		add(menu_view);
		add(menu_help);

		//shortcuts used by textfields:
		//ctrl+a: select all
		//ctrl+c: copy to clipboard
		//ctrl+v: paste from clipboard
		//ctrl+x: cut to clipboard
		//on mac: command+..

		//main items
		mi_start_transmissionSend=new JMenuItem(l.tr("Start (Send)"));
		mi_start_transmissionSend.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_W, m.ctrlOrCmd));
		mi_start_transmissionSend.setFont(f.fontNormal);

		mi_stop_transmissionSend=new JMenuItem(l.tr("Stop (Send)"));
		mi_stop_transmissionSend.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_E, m.ctrlOrCmd));
		mi_stop_transmissionSend.setFont(f.fontNormal);
		mi_start_transmissionReceive=new JMenuItem(l.tr("Start (Receive)"));
		mi_start_transmissionReceive.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_R, m.ctrlOrCmd));
		mi_start_transmissionReceive.setFont(f.fontNormal);

		mi_stop_transmissionReceive=new JMenuItem(l.tr("Stop (Receive)"));
		mi_stop_transmissionReceive.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_T, m.ctrlOrCmd));
		mi_stop_transmissionReceive.setFont(f.fontNormal);

		mi_quit=new JMenuItem(l.tr("Quit Program"));
		mi_quit.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_Q, m.ctrlOrCmd));
		mi_quit.setFont(f.fontNormal);

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
		mi_load_default_settings=new JMenuItem(l.tr("Load Default"));
		mi_load_default_settings.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_M, m.ctrlOrCmd));
		mi_load_default_settings.setFont(f.fontNormal);

		mi_save_default_settings=new JMenuItem(l.tr("Save As Default"));
		mi_save_default_settings.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_PERIOD, m.ctrlOrCmd));
		mi_save_default_settings.setFont(f.fontNormal);

		mi_load_settings=new JMenuItem(l.tr("Load")+"...");
		mi_load_settings.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_O, m.ctrlOrCmd));
		mi_load_settings.setFont(f.fontNormal);

		mi_save_settings_as=new JMenuItem(l.tr("Save As")+"...");
		mi_save_settings_as.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_S, m.ctrlOrCmd));
		mi_save_settings_as.setFont(f.fontNormal);

		mi_configure_dialog=new JMenuItem(l.tr("Configure")+"...");
		mi_configure_dialog.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, m.ctrlOrCmd));
		mi_configure_dialog.setFont(f.fontNormal);

		menu_settings.add(mi_load_default_settings);
		menu_settings.add(mi_save_default_settings);
		menu_settings.add(new JSeparator());
		menu_settings.add(mi_load_settings);
		menu_settings.add(mi_save_settings_as);
		menu_settings.add(new JSeparator());
		menu_settings.add(mi_configure_dialog);

		//view items
		mi_view_send=new JMenuItem(l.tr("Show Send"));
		mi_view_send.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_F, m.ctrlOrCmd));
		mi_view_send.setFont(f.fontNormal);

		mi_view_receive=new JMenuItem(l.tr("Show Receive"));
		mi_view_receive.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_G, m.ctrlOrCmd));
		mi_view_receive.setFont(f.fontNormal);

		mi_view_both=new JMenuItem(l.tr("Show Both"));
		mi_view_both.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_J, m.ctrlOrCmd));
		mi_view_both.setFont(f.fontNormal);

		menu_view.add(mi_view_send);
		menu_view.add(mi_view_receive);
		menu_view.add(mi_view_both);

		//help items
		mi_doc=new JMenuItem(l.tr("Manual")+"...");
		mi_doc.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_1, m.ctrlOrCmd));
		mi_doc.setFont(f.fontNormal);

		mi_about=new JMenuItem(l.tr("About")+"...");
		mi_about.setFont(f.fontNormal);

		mi_info=new JMenuItem(l.tr("Build- & Runtime Info")+"...");
		mi_info.setFont(f.fontNormal);

		mi_license=new JMenuItem(l.tr("License")+"...");
		mi_license.setFont(f.fontNormal);

		mi_report_issue=new JMenuItem(l.tr("Report Issuse")+"...");
		mi_report_issue.setFont(f.fontNormal);

		mi_check_for_update=new JMenuItem(l.tr("Check For Updates")+"...");
		mi_check_for_update.setFont(f.fontNormal);

//test
		menu_help.add(mi_doc);
		menu_help.add(mi_about);
		menu_help.add(mi_info);
		menu_help.add(mi_license);
		menu_help.add(new JSeparator());
		menu_help.add(mi_report_issue);
//dummy
		menu_help.add(mi_check_for_update);

	}//end createMenu

//========================================================================
	private void addActionListeners()
	{
		mi_start_transmissionSend.addActionListener(new ActionListener()
		{
			@Override public void actionPerformed(ActionEvent e)
			{
				if(g.frontSend.readForm())
				{
					m.startTransmissionSend();
				}
			}
		});

		mi_stop_transmissionSend.addActionListener(new ActionListener()
		{
			@Override public void actionPerformed(ActionEvent e)
			{
				m.stopTransmissionSend();
			}
		});


		mi_start_transmissionReceive.addActionListener(new ActionListener()
		{
			@Override public void actionPerformed(ActionEvent e)
			{
				if(g.frontReceive.readForm())
				{
					m.startTransmissionReceive();
				}
			}
		});

		mi_stop_transmissionReceive.addActionListener(new ActionListener()
		{
			@Override public void actionPerformed(ActionEvent e)
			{
				m.stopTransmissionReceive();
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
					m.portOutSend.send(msg);
					m.portOutReceive.send(msg);
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
				Settings.load(m.defaultPropertiesFileName);
				g.frontSend.setValues();
				g.frontReceive.setValues();
				g.configure.setValues();
				f.init();
				g.updateFont();
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
					Settings.save(m.defaultPropertiesFileName);
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
					File file=new File(fileToLoad);
					if(!file.canRead())
					{
						g.setStatus("Error: Could Not Read File");
					}
					else
					{
						g.setStatus("Loading File '"+fileToLoad+"'...");
						Settings.load(fileToLoad);
						//set values in forms
						g.frontSend.setValues();
						g.frontReceive.setValues();
						g.configure.setValues();
						f.init();
						g.updateFont();
						g.setStatus("Settings Loaded");
					}
				}
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
						File file=new File(fileToSave);
						if(file.exists())
						{
							//overwrite
							g.setStatus("Updating File '"+fileToSave+"'...");
							Settings.save(fileToSave);
							g.setStatus("Settings Saved");
						}
						else
						{
							try
							{
								if(!file.createNewFile())
								{
									g.setStatus("Error: Could Not Write File");
								}
								else
								{
									g.setStatus("Saving File '"+fileToSave+"'...");
									Settings.save(fileToSave);
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

//////////forces windows to be activated (if was deactivated)
/////////////////should distinguish: if only one panel visible
g.frontSend.button_default.requestFocus();
			}
		});

		//view
		mi_view_send.addActionListener(new ActionListener()
		{
			@Override public void actionPerformed(ActionEvent e)
			{
				FormHelper.viewSendPanel();
			}
		});

		mi_view_receive.addActionListener(new ActionListener()
		{
			@Override public void actionPerformed(ActionEvent e)
			{
				FormHelper.viewReceivePanel();
			}
		});

		mi_view_both.addActionListener(new ActionListener()
		{
			@Override public void actionPerformed(ActionEvent e)
			{
				FormHelper.viewBothPanels();
			}
		});

		//help
		mi_doc.addActionListener(new ActionListener()
		{
			@Override public void actionPerformed(ActionEvent e)
			{
				//ev simply open a textfile instead of pdf
				String docPdf=m.tmpDir+File.separator+"resources"
					+File.separator+"doc"+File.separator+"jack_audio_send.pdf";

				m.p("opening file "+docPdf);
				IOTools.openFile(new File(docPdf));
			}
		});

		mi_about.addActionListener(new ActionListener()
		{
			@Override public void actionPerformed(ActionEvent e)
			{
				g.about.pack();
				g.about.setVisible(true);
			}
		});

		mi_info.addActionListener(new ActionListener()
		{
			@Override public void actionPerformed(ActionEvent e)
			{
				g.info.updateHtml();
				g.info.pack();
				g.info.setVisible(true);
			}
		});

		mi_license.addActionListener(new ActionListener()
		{
			@Override public void actionPerformed(ActionEvent e)
			{
				String docTxt=m.tmpDir+File.separator+"resources"
					+File.separator+"COPYING.txt";

				m.p("opening file "+docTxt);
				IOTools.openFile(new File(docTxt));
			}
		});

		mi_report_issue.addActionListener(new ActionListener()
		{
			@Override public void actionPerformed(ActionEvent e)
			{
				IOTools.openInBrowser(m.reportIssueUrl);
			}
		});

		mi_check_for_update.addActionListener(new ActionListener()
		{
			@Override public void actionPerformed(ActionEvent e)
			{
				/////////test thread
				IOTools.checkForNewerVersion(m.newestVersionFileUrl);
			}
		});

	}//end addActionListeners

//========================================================================
	public static void setForRunningSend()
	{
		mi_load_default_settings.setEnabled(false);
		mi_load_settings.setEnabled(false);
		mi_configure_dialog.setEnabled(false);
		mi_start_transmissionSend.setEnabled(false);
		mi_stop_transmissionSend.setEnabled(true);
	}

//========================================================================
	public static void setForFrontScreenSend()
	{
		mi_load_default_settings.setEnabled(true);
		mi_load_settings.setEnabled(true);
		mi_configure_dialog.setEnabled(true);
		mi_start_transmissionSend.setEnabled(true);
		mi_stop_transmissionSend.setEnabled(false);
	}

//========================================================================
	public static void setForRunningReceive()
	{
		mi_load_default_settings.setEnabled(false);
		mi_load_settings.setEnabled(false);
		mi_configure_dialog.setEnabled(false);
		mi_start_transmissionReceive.setEnabled(false);
		mi_stop_transmissionReceive.setEnabled(true);
	}

//========================================================================
	public static void setForFrontScreenReceive()
	{
		mi_load_default_settings.setEnabled(true);
		mi_load_settings.setEnabled(true);
		mi_configure_dialog.setEnabled(true);
		mi_start_transmissionReceive.setEnabled(true);
		mi_stop_transmissionReceive.setEnabled(false);
	}
}//end AppMenu
