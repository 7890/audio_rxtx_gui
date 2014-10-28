package ch.lowres.audio_rxtx.gui;

import java.awt.*;
import java.awt.event.*;

//tb/1410

public class AboutDialog extends Dialog
{
	static jack_audio_send_GUI g;
//	static jack_audio_send_cmdline_API api;

	static Panel form;
	static ImgComponent aboutImg;

	static Button button_about_ok=new Button ("OK");

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
		button_about_ok.addActionListener ( new ActionListener()
		{
			public void actionPerformed( ActionEvent e )
			{
				setVisible(false);
				g.mainframe.toFront();
			}
		});
	}//end addActionListeners
}//end class AboutDialog
