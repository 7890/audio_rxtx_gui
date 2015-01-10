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

package ch.lowres.audio_rxtx.gui.helpers;

import java.awt.event.KeyEvent;

import org.xnap.commons.i18n.*;

import java.util.Locale;

/**
* Holding Languages
*/
//========================================================================
public class Languages
{
	private static I18n i18n;

/////////
	//half-baked, need get/set to make sure in sync
	public static String[] languages={"English", "Deutsch"};
	public static String lang="en";
	public static int langIndex=0;

	public static Locale locale=java.util.Locale.ENGLISH;

//========================================================================
	public static void set(String s)
	{
		if(s.toLowerCase().equals("de") || s.toLowerCase().equals("deutsch"))
		{
			lang=s;
			langIndex=1;
			if(locale!=java.util.Locale.GERMAN)
			{
				locale=java.util.Locale.GERMAN;
				i18n = I18nFactory.getI18n(Languages.class, "ch.lowres.audio_rxtx.gui.i18n.Messages", locale);
			}
		}
		else
		{
			lang="en";
			langIndex=0;
			locale=java.util.Locale.ENGLISH;
			i18n = I18nFactory.getI18n(Languages.class, "ch.lowres.audio_rxtx.gui.i18n.Messages", locale);
		}
	}

//========================================================================
	public static String tr(String text)
	{
		//create on first request
		if(i18n==null)
		{
			i18n = I18nFactory.getI18n(Languages.class, "ch.lowres.audio_rxtx.gui.i18n.Messages", locale);
		}

		return i18n.tr(text);
	}

//========================================================================
	public static String removeMnemonic(String s)
	{
		//no _ found (indicator that next char is mnemonic)
		if(s.indexOf('_')<0)
		{
			return s;
		}
		return s.replace("_","");
	}

//========================================================================
	public static char getMnemonicChar(String s)
	{
		if(s.indexOf('_')!=-1 && s.length()>s.indexOf('_')+1)
		{
			return s.charAt(s.indexOf('_')+1);
		}

		return '_';
	}

//========================================================================
	public static int getMnemonicKeyEvent(String s)
	{
		return getKeyEvent(getMnemonicChar(s));
	}

//========================================================================
	public static int getKeyEvent(char character)
	{
		switch (character) 
		{
			case 'a': return KeyEvent.VK_A;
			case 'b': return KeyEvent.VK_B;
			case 'c': return KeyEvent.VK_C;
			case 'd': return KeyEvent.VK_D;
			case 'e': return KeyEvent.VK_E;
			case 'f': return KeyEvent.VK_F;
			case 'g': return KeyEvent.VK_G;
			case 'h': return KeyEvent.VK_H;
			case 'i': return KeyEvent.VK_I;
			case 'j': return KeyEvent.VK_J;
			case 'k': return KeyEvent.VK_K;
			case 'l': return KeyEvent.VK_L;
			case 'm': return KeyEvent.VK_M;
			case 'n': return KeyEvent.VK_N;
			case 'o': return KeyEvent.VK_O;
			case 'p': return KeyEvent.VK_P;
			case 'q': return KeyEvent.VK_Q;
			case 'r': return KeyEvent.VK_R;
			case 's': return KeyEvent.VK_S;
			case 't': return KeyEvent.VK_T;
			case 'u': return KeyEvent.VK_U;
			case 'v': return KeyEvent.VK_V;
			case 'w': return KeyEvent.VK_W;
			case 'x': return KeyEvent.VK_X;
			case 'y': return KeyEvent.VK_Y;
			case 'z': return KeyEvent.VK_Z;
			case 'A': return KeyEvent.VK_A;
			case 'B': return KeyEvent.VK_B;
			case 'C': return KeyEvent.VK_C;
			case 'D': return KeyEvent.VK_D;
			case 'E': return KeyEvent.VK_E;
			case 'F': return KeyEvent.VK_F;
			case 'G': return KeyEvent.VK_G;
			case 'H': return KeyEvent.VK_H;
			case 'I': return KeyEvent.VK_I;
			case 'J': return KeyEvent.VK_J;
			case 'K': return KeyEvent.VK_K;
			case 'L': return KeyEvent.VK_L;
			case 'M': return KeyEvent.VK_M;
			case 'N': return KeyEvent.VK_N;
			case 'O': return KeyEvent.VK_O;
			case 'P': return KeyEvent.VK_P;
			case 'Q': return KeyEvent.VK_Q;
			case 'R': return KeyEvent.VK_R;
			case 'S': return KeyEvent.VK_S;
			case 'T': return KeyEvent.VK_T;
			case 'U': return KeyEvent.VK_U;
			case 'V': return KeyEvent.VK_V;
			case 'W': return KeyEvent.VK_W;
			case 'X': return KeyEvent.VK_X;
			case 'Y': return KeyEvent.VK_Y;
			case 'Z': return KeyEvent.VK_Z;
			case '0': return KeyEvent.VK_0;
			case '1': return KeyEvent.VK_1;
			case '2': return KeyEvent.VK_2;
			case '3': return KeyEvent.VK_3;
			case '4': return KeyEvent.VK_4;
			case '5': return KeyEvent.VK_5;
			case '6': return KeyEvent.VK_6;
			case '7': return KeyEvent.VK_7;
			case '8': return KeyEvent.VK_8;
			case '9': return KeyEvent.VK_9;

/*
			case '`': KeyEvent.VK_BACK_QUOTE;
			case '-': KeyEvent.VK_MINUS;
			case '=': KeyEvent.VK_EQUALS;
			case '~': KeyEvent.VK_SHIFT, VK_BACK_QUOTE;
			case '!': KeyEvent.VK_EXCLAMATION_MARK;
			case '@': KeyEvent.VK_AT;
			case '#': KeyEvent.VK_NUMBER_SIGN;
			case '$': KeyEvent.VK_DOLLAR;
			case '%': KeyEvent.VK_SHIFT, VK_5;
			case '^': KeyEvent.VK_CIRCUMFLEX;
			case '&': KeyEvent.VK_AMPERSAND;
			case '*': KeyEvent.VK_ASTERISK;
			case '(': KeyEvent.VK_LEFT_PARENTHESIS;
			case ')': KeyEvent.VK_RIGHT_PARENTHESIS;
			case '_': KeyEvent.VK_UNDERSCORE;
			case '+': KeyEvent.VK_PLUS;
			case '\t': KeyEvent.VK_TAB;
			case '\n': KeyEvent.VK_ENTER;
			case '[': KeyEvent.VK_OPEN_BRACKET;
			case ']': KeyEvent.VK_CLOSE_BRACKET;
			case '\\': KeyEvent.VK_BACK_SLASH;
			case '{': KeyEvent.VK_SHIFT, VK_OPEN_BRACKET;
			case '}': KeyEvent.VK_SHIFT, VK_CLOSE_BRACKET;
			case '|': KeyEvent.VK_SHIFT, VK_BACK_SLASH;
			case ';': KeyEvent.VK_SEMICOLON;
			case ':': KeyEvent.VK_COLON;
			case '\'': KeyEvent.VK_QUOTE;
			case '"': KeyEvent.VK_QUOTEDBL;
			case ',': KeyEvent.VK_COMMA;
			case '<': KeyEvent.VK_SHIFT, VK_COMMA;
			case '.': KeyEvent.VK_PERIOD;
			case '>': KeyEvent.VK_SHIFT, VK_PERIOD;
			case '/': KeyEvent.VK_SLASH;
			case '?': KeyEvent.VK_SHIFT, VK_SLASH;
			case ' ': KeyEvent.VK_SPACE
*/
			default: break;
		}
		return 0;
	}//end getKeyEvent
}//end class Languages
