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
import ch.lowres.audio_rxtx.gui.*;
import ch.lowres.audio_rxtx.gui.widgets.*;


import java.awt.*;
import javax.swing.*;
import java.awt.geom.AffineTransform;

/**
* Holding colors to be accessed statically by any component.
*/
//========================================================================
public class Fonts
{
	static Main m;

	public static Font fontNormal;
	public static Font fontLarge;

	public static boolean use_internal_font=true;

	public static String fontName="Dialog";

	//~points
	public static float fontDefaultSize=18f;
	//relative to fontDefaultSize
	public static float fontLargeFactor=1.2f;

	//in native java @72dpi point units
	public static float fontNormalSize=dpiCorrectedPt(fontDefaultSize);
	public static float fontLargeSize=fontNormalSize*fontLargeFactor;

	public static int fontNormalStyle=Font.PLAIN;
	public static int fontLargeStyle=Font.BOLD;

	public static String[] styles = {m.tr("Regular"), m.tr("Bold"), m.tr("Italic"), m.tr("Bold Italic")};

//========================================================================
	public static void set(String font)
	{
///////////
		//check if exists on system
		fontName=font;
	}

//========================================================================
	public static String[] getAll()
	{
		GraphicsEnvironment gEnv=GraphicsEnvironment.getLocalGraphicsEnvironment();
		return gEnv.getAvailableFontFamilyNames();
	}

//========================================================================
	public static void recreate()//float fontsize, String fontname)
	{
		//in native java @72dpi point units
		fontNormalSize=dpiCorrectedPt(fontDefaultSize);
		fontLargeSize=fontNormalSize*fontLargeFactor;

		m.commonWidgetHeight=(int)(fontLargeSize*1.3);

		if(use_internal_font)
		{
			fontNormal=m.iot.getDefaultFont(fontNormalSize,fontNormalStyle);
			fontLarge=m.iot.getDefaultFont(fontLargeSize,fontLargeStyle);
		}
		else
		{
			fontNormal=new Font(fontName,fontNormalStyle,(int)fontNormalSize);
			fontLarge=new Font(fontName,fontLargeStyle,(int)fontLargeSize);
		}
	}

//========================================================================
	//http://www.java2s.com/Tutorials/Java/java.awt/GraphicsConfiguration/Java_GraphicsConfiguration_getNormalizingTransform_.htm
	//https://www.linkedin.com/pulse/20140715044516-153509697-java-interview-questions
	public static float dpiCorrectedPt(float at72)
	{
		//return (at72 * m.os.getDPI() / 72f);

		GraphicsEnvironment gEnv=GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gs = gEnv.getDefaultScreenDevice();
		GraphicsConfiguration gc = gs.getDefaultConfiguration();

		AffineTransform at=gc.getNormalizingTransform();
		return (float)(Math.max(at.getScaleX(),at.getScaleY()) * at72);
	}

//========================================================================
	//http://stackoverflow.com/questions/12730230/set-the-same-font-for-all-component-java
	//http://stackoverflow.com/questions/22494495/change-font-dynamically-causes-problems-on-some-components
	public static void change(Component component)
	{
		if (component instanceof JMenu)
		{
			//change menu font
			component.setFont(fontNormal);

			//menuitems need special treatment, getMenuComponents
			Component[] children=null;
			children=((JMenu)component).getMenuComponents();

			for(Component child : children)
			{
				child.setFont(fontNormal);
			}
			return;
		}
		//set new font and other font-related sizes via init()
		else if (component instanceof ACheckbox)
		{
			((ACheckbox)component).init();
			return;
		}
		else if (component instanceof ARadioButton)
		{
			((ARadioButton)component).init();
			return;
		}
		else if(component instanceof AButton)
		{
			component.setFont(fontLarge);
			return;
		}
		else if(component instanceof ListTextFieldWithLimit)
		{
			component.setFont(fontNormal);
			//also set ListDialog
			((ListTextFieldWithLimit)component).initLd();
			return;
		}
		else
		{
			component.setFont(fontNormal);
		}
		if(component instanceof Container)
		{
			for (Component child : ((Container)component).getComponents())
			{
				change(child);
			}
		}
	}//end change
}//end class Fonts
