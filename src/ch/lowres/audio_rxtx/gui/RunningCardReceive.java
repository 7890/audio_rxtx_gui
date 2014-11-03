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
public class RunningCardReceive extends Card
{
	static Label label_1=new Label("");
	static Label label_2=new Label("");
	static Label label_3=new Label("");
	static Label label_4=new Label("");
	static Label label_5=new Label("");
	static Label label_6=new Label("");
	static Label label_7=new Label("");
	static Label label_8=new Label("");
	static Label label_9=new Label("");

//========================================================================
	public RunningCardReceive() 
	{
		button_default.setLabel("Stop Transmission");
	}

//========================================================================
	public void setValues()
	{
		label_1.setText("");
		label_2.setText("");
		label_3.setText("");
		label_4.setText("");
		label_5.setText("");
		label_6.setText("");
		label_7.setText("");
		label_8.setText("");
		label_9.setText("");
		button_default.setLabel("Stop Transmission");
	}

//========================================================================
	public void createForm()
	{
		super.createForm();

		form.setLayout(new GridBagLayout());

		g.formUtility.addLastLabel(label_1, form);
		g.formUtility.addLastLabel(label_2, form);
		g.formUtility.addLastLabel(label_3, form);
		g.formUtility.addLastLabel(label_4, form);
		g.formUtility.addLastLabel(label_5, form);
		g.formUtility.addLastLabel(label_6, form);
		g.formUtility.addLastLabel(label_7, form);
		g.formUtility.addLastLabel(label_8, form);
		g.formUtility.addLastLabel(label_9, form);
	}//end createForm

//========================================================================
	public boolean readForm()
	{
		return true;
	}

//========================================================================
	public void defaultAction()
	{
		g.stopTransmissionReceive();
	}

//========================================================================
	public void setLabel(int i,String s)
	{
		if(i==1)
		{
			label_1.setText(s);
		}
		else if(i==2)
		{
			label_2.setText(s);
		}
		else if(i==3)
		{
			label_3.setText(s);
		}
		else if(i==4)
		{
			label_4.setText(s);
		}
		else if(i==5)
		{
			label_5.setText(s);
		}
		else if(i==6)
		{
			label_6.setText(s);
		}
		else if(i==7)
		{
			label_7.setText(s);
		}
		else if(i==8)
		{
			label_8.setText(s);
		}
		else if(i==9)
		{
			label_9.setText(s);
		}
	}

//=============================================================================
/*
	public void addActionListeners()
	{
	}//end addActionListeners
*/
}//end class RunningCardReceive
