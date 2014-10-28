package ch.lowres.audio_rxtx.gui;

//tb/141016

//http://www.rgagnon.com/javadetails/java-0226.html

import java.awt.*;

import javax.swing.ImageIcon;

import java.net.MalformedURLException;
import java.net.URL;

//========================================================================
class ImgComponent extends Component
{
	private ImageIcon audio_rxtx_logo;
	private int imageTop=370;
	private int preferredWidth=320;

	static jack_audio_send_GUI g;

//========================================================================
	@Override
	public Dimension getMinimumSize()
	{
		return new Dimension(preferredWidth,imageTop);
	}

//========================================================================
	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(preferredWidth,imageTop);
	}

//========================================================================
	@Override
	public Dimension getMaximumSize()
	{
		return new Dimension(preferredWidth,imageTop);
	}

//========================================================================
	public ImgComponent()
	{
		try
		{
			//audio_rxtx_logo = new ImageIcon(new URL("http://..."));
			//audio_rxtx_logo = new ImageIcon(new URL("file:///..."));
			audio_rxtx_logo=new IOTools().createImageIconFromJar("/resources/audio_rxtx_about_screen.png");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

//========================================================================
	public void paint(Graphics gfx)
	{
		//gfx.drawRect (10, 10, 200, 200);
		gfx.setColor(Color.WHITE);
		gfx.fillRect(0, 0, getWidth(),getHeight()); 

		int x = (getWidth() - audio_rxtx_logo.getIconWidth()) / 2;
		int y=0;

		gfx.drawImage(audio_rxtx_logo.getImage(), x, y, audio_rxtx_logo.getIconWidth(), audio_rxtx_logo.getIconHeight(), this);
		gfx.setColor(Color.BLACK);
		gfx.drawString("v"+g.progVersion, getWidth()-75, 25);
		//gfx.drawString("", getWidth()-75, 235);
		//gfx.drawString("", getWidth()-75, 268);

	}//end paint
}//end class ImgComponent
