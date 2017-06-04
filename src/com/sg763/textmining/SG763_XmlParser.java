package com.sg763.textmining;

/*
 * A parser written to parse the text in BODY tages of the .sgm files in the Reuters dataset
 * into separate text files.
 * Submitted By: Shravani Krishna Rau Gogineni
 * ID: 31376289 / SG763
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class SG763_XmlParser 
{
	static final String inputPath = "C:/Users/Shravani/Documents/NJIT/DM_FinalProject/reuters21578/";
	static String word = "";
	@SuppressWarnings("resource")

	public static void main(String[] args) throws Exception 
	{
		File[] files = new File(inputPath).listFiles();
		int fileIndex = 0;
		
		for (File file : files) 
		{
			if (file.isFile()) 
			{
				try 
				{
					String content = new Scanner(file).useDelimiter("\\Z").next();
					word = "";
					findTextInTag(content);
					word.replaceAll("//s+", "");
					File fileOutput = new File("C:/Users/Shravani/Documents/NJIT/DM_FinalProject/InitialTextDoc/" + fileIndex + "story.txt");
					PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileOutput, false)));
					out.println(word.replaceAll("<BODY>", ""));
					out.close();
				} 
				
				catch (IOException e) 
				{
					e.printStackTrace();
				}
				
				fileIndex++;
			}
		}
	}
	
	static void findTextInTag(String content) 
	{
		StringBuilder sb = new StringBuilder(content);
		int firstIndex = sb.indexOf("<BODY>");
		int lastIndex = sb.indexOf("</BODY>");
	
		if (firstIndex < 0 || lastIndex < 0)
			return;
		
		try 
		{
			word += sb.substring(firstIndex, lastIndex);
			sb.delete(firstIndex, lastIndex + 7);
			findTextInTag(sb.toString());
		}
		
		catch (Exception ex) 
		{
			System.out.println(ex.getStackTrace());
		}
	}
}