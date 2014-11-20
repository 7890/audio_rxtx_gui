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

import java.net.InetAddress;

import javax.swing.JLabel;
import javax.swing.JPanel;

//========================================================================
public abstract class Card extends Panel implements CardInterface
{
	static Main g;
	Panel form;
	JLabel 				label_status=new JLabel("Ready");
	AButton 			button_default=new AButton("Default");

	boolean formCreated=false;

//========================================================================
	public Card() 
	{
		setBackground(Colors.form_background);
		setForeground(Colors.form_foreground);
		setLayout(new BorderLayout());

		createForm();
		addActionListeners();
	}

//========================================================================
	public abstract void setValues();

//========================================================================
	public void createForm()
	{
		if(formCreated){return;}

		form=new Panel();
		add(form,BorderLayout.NORTH);

		button_default.setBackground(Colors.button_background);
		button_default.setForeground(Colors.button_foreground);
		button_default.setFont(g.fontLarge);

		Font f=new Label().getFont();
		label_status.setFont(f);

		JPanel pSouth=new JPanel(new GridLayout(2,1));
		pSouth.setBackground(Colors.status_background);
		pSouth.setForeground(Colors.status_foreground);

		pSouth.add(label_status);
		pSouth.add(button_default);

		add(pSouth,BorderLayout.SOUTH);

		formCreated=true;
	}//end createForm

//========================================================================
	public abstract boolean readForm();

//========================================================================
	public void addActionListeners()
	{
		button_default.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				defaultAction();
			}
		});
	}//end addActionListeners

//========================================================================
	public abstract void defaultAction();

//========================================================================
	public void setStatus(String s)
	{
		if(label_status!=null)
		{
			label_status.setText(s);
		}
		//g.p("CARD STATUS "+s);//log
	}

//========================================================================
	public abstract void setLabel(int i,String s);

//========================================================================
	public abstract void focusFirstInputWidget();

}//end class Card
