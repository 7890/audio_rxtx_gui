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
import ch.lowres.audio_rxtx.gui.widgets.*;
import ch.lowres.audio_rxtx.gui.helpers.*;

import java.awt.*;
import java.awt.event.*;

import java.net.InetAddress;

import javax.swing.*;

import java.awt.geom.*;

/**
* Abstract {@link CardInterface}, extended {@link APanel}, handling common card aspects.
*/
//========================================================================
public abstract class Card extends APanel implements CardInterface
{
	static Main m;
	static GUI g;
	static Fonts f;
	static Languages l;

	JPanel form;
	StatusLabel			label_status=new StatusLabel(l.tr("Ready"));
	public AButton 			button_default=new AButton("Default");

//	boolean formCreated=false;

//========================================================================
	public Card() 
	{
		init();
	}

//========================================================================
	public void init() 
	{
		createForm();
		addActionListeners();
	}

//========================================================================
	public abstract void setValues();{}

//========================================================================
	public void createForm()
	{
		setLayout(new BorderLayout());
		setOpaque(true);
		setBackground(Colors.form_background);
		setForeground(Colors.form_foreground);

//		if(formCreated){return;}

		form=new JPanel();
		form.setOpaque(true);
		form.setBackground(Colors.form_background);
		form.setForeground(Colors.form_foreground);

		add(form,BorderLayout.NORTH);

		JPanel pSouth=new JPanel(new BorderLayout());

		pSouth.add(label_status,BorderLayout.NORTH);
		pSouth.add(button_default,BorderLayout.SOUTH);

		//poorman style for a spring that fills space to bottom (label & button)
		JTextArea fill=new JTextArea();
		fill.setEnabled(false);
		fill.setVisible(false);
		add(fill,BorderLayout.CENTER);

		add(pSouth,BorderLayout.SOUTH);

//		formCreated=true;
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
	public void setStatus(String message)
	{
		if(label_status!=null)
		{
			label_status.setStatus(message,2000);
		}
	}

//========================================================================
	public void setStatusError(String message)
	{
		if(label_status!=null)
		{
			label_status.setStatusError(message,2000);
		}
	}

//========================================================================
	public abstract void setLabel(int i,String s);

//========================================================================
	public abstract void focusFirstInputWidget();
}//end class Card
