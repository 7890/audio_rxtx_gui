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

package ch.lowres.audio_rxtx.gui.widgets;
import ch.lowres.audio_rxtx.gui.*;
import ch.lowres.audio_rxtx.gui.helpers.*;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.*;

/**
* Extended ALabel for Status.
*/
//========================================================================
public class StatusLabel extends ALabel
{
	private static Main m;
	private static Fonts f;
	private Timer timer;

//========================================================================
	public StatusLabel()
	{
		super("");
		init();
	}

//========================================================================
	public StatusLabel(String label)
	{
		super(label);
		init();
	}

//========================================================================
	public StatusLabel(String label, int align)
	{
		super(label,align);
		init();
	}

//========================================================================
	void init()
	{
		setOpaque(true);
		setFont(f.fontNormal);
		ActionListener resetStatus = new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				setBackground(Colors.status_background);
				setForeground(Colors.status_foreground);
				StatusLabel.super.setText(m.tr("Ready"));
			}
		};
		timer=new Timer(0, resetStatus);
		timer.setRepeats(false);

		setBackground(Colors.status_background);
		setForeground(Colors.status_foreground);
	}

//========================================================================
	public void setStatus(String message,int milliseconds)
	{
		setBackground(Colors.status_background);
		setForeground(Colors.status_foreground);
		super.setText(""+message);
		timer.setInitialDelay(milliseconds);
		timer.restart();
	}

//========================================================================
	public void setStatusError(String message,int milliseconds)
	{
		setBackground(Colors.status_error_background);
		setForeground(Colors.status_error_foreground);
		super.setText(""+message);
		timer.setInitialDelay(milliseconds);
		timer.restart();
	}
}//end class StatusLabel
