package com.learn.crypto.util;

import java.util.Random;

public class MiscUtil {

	private static long seed = System.currentTimeMillis();
	private static Random r = new Random(seed);

	public static String getRandomString() {
		return getRandomString(8);
	};
	public static String getRandomString( int len )
	{
		StringBuilder sb = new StringBuilder();
		byte[] byteKeys = new byte[1];
		for(int count =0; count<len;)
		{
			r.nextBytes(byteKeys);
			byteKeys[0]%=128;
			if(byteKeys[0]<0)
				byteKeys[0]+=128;
			char ch= (char)(byteKeys[0]);
			if(Character.isLetterOrDigit(ch) && ch != 'l' && ch != 'L' && ch != '1' && ch != 'I' && ch != 'i' && ch != 'O' && ch != 'o' && ch != '0' )
			{
				sb.append(ch);
				count++;
			}
		}
		return sb.toString();
	}
}
