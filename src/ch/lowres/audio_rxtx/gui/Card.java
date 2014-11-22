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

//========================================================================
public abstract class Card extends APanel implements CardInterface
{
	static Main g;
	JPanel form;
	JLabel 				label_status=new JLabel("Ready");
	public AButton 			button_default=new AButton("Default");

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

		form=new JPanel();
		form.setBackground(Colors.form_background);

		form.setOpaque(false);

		add(form,BorderLayout.NORTH);

		button_default.setBackground(Colors.button_background);
		button_default.setForeground(Colors.button_foreground);
		button_default.setFont(g.fontLarge);

		Font f=new JLabel().getFont();
		label_status.setFont(f);

		JPanel pSouth=new JPanel(new GridLayout(2,1));
		pSouth.setBackground(Colors.status_background);
		pSouth.setForeground(Colors.status_foreground);

		pSouth.add(label_status);
		pSouth.add(button_default);

		//poorman style for a spring that fills space to bottom (label & button)
		JTextArea fill=new JTextArea();
		fill.setEnabled(false);
		fill.setVisible(false);
		add(fill,BorderLayout.CENTER);

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
