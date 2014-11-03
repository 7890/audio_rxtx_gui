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

//========================================================================
public interface CardInterface //extends Panel
{
//========================================================================
	void defaultAction();

//========================================================================
	void setValues();

//========================================================================
	void createForm();

//========================================================================
	boolean readForm();

//========================================================================
	void addActionListeners();

//========================================================================
	void setStatus(String s);
}//end interface CardInterface
