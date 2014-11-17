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

//========================================================================
public class AboutDialog extends Dialog
{
	static Main g;

	static Panel form;
	static ImgComponent aboutImg;

	static AButton button_about_ok=new AButton ("OK");

//========================================================================
	public AboutDialog(Frame f,String title, boolean modality) 
	{
		super(f,title,modality);

		setBackground(Colors.form_background);
		setForeground(Colors.form_foreground);
		setLayout(new BorderLayout());

		setIconImage(g.appIcon);

		createForm();
		addActionListeners();
	}

//========================================================================
	void createForm()
	{
		form=new Panel();
		form.setLayout(new GridBagLayout());

		add(form,BorderLayout.NORTH);

		aboutImg=new ImgComponent();

		g.formUtility.addImage(aboutImg, form);

		button_about_ok.setFocusable(false);

		g.formUtility.addFullButton(button_about_ok, form, g.fontLarge);

		setUndecorated(true);

		pack();

		//center on screen
		setLocation(
			(int)((g.screenDimension.getWidth()-getWidth()) / 2),
			(int)((g.screenDimension.getHeight()-getHeight()) / 2)
		);

		setResizable(false);

		//done in calling object
		//setVisible(true);

	}//end createForm

//========================================================================
	void addActionListeners()
	{
		button_about_ok.addActionListener (new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				setVisible(false);
				g.mainframe.toFront();
			}
		});
	}//end addActionListeners
}//end class AboutDialog
