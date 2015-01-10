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

//http://stackoverflow.com/questions/304268/getting-a-files-md5-checksum-in-java

package ch.lowres.audio_rxtx.gui.helpers;

import java.io.*;
import java.security.MessageDigest;

/**
* Create MD5 hash from a file
*/
public class MD5Checksum
{
//========================================================================
	private static byte[] createChecksum(String filename) throws Exception
	{
		InputStream fis=new FileInputStream(filename);

		byte[] buffer=new byte[1024];
		MessageDigest complete=MessageDigest.getInstance("MD5");
		int numRead;

		do
		{
			numRead=fis.read(buffer);
			if (numRead > 0)
			{
					complete.update(buffer, 0, numRead);
			}
		} while (numRead != -1);

		fis.close();
		return complete.digest();
	}//end createChecksum

//========================================================================
	public static String getMD5Checksum(String filename) throws Exception
	{
		byte[] b=createChecksum(filename);
		String result="";

		for (int i=0; i<b.length; i++)
		{
			result+=Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
		}
		return result;
	}

/*
//========================================================================
	public static void main(String args[])
	{
		try
		{
			System.out.println(getMD5Checksum("MD5Checksum.java"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
*/
}//end MD5Checksum
