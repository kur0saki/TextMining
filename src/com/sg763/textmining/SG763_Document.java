package com.sg763.textmining;

/*
 * Class that represents a text document
 * Keeps track of the number of times a word appears in the document and it's tfidfs
 * Submitted By: Shravani Krishna Rau Gogineni
 * ID: 31376289 / SG763
 *
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

public class SG763_Document 
{
	public TreeMap<String, Double[]> words;
	int totalWords;
	File outputFile = new File(outputPath + "/allwords.txt");
	static PrintWriter outkeyword;
	static final String outputPath = "C:/Users/Shravni/Documents/NJIT/DM_FinalProject/Keywords";
	
	/*
	 * Constructor for document class that is used by the parent TfIdf class
	 */
	
	public SG763_Document(BufferedReader br, SG763_TfIdf parent) 
	{
		String line;
		String word;
		totalWords = 0;
		Double[] tempdata;
		words = new TreeMap<String, Double[]>();
	
		try 
		{
			while ((line = br.readLine()) != null) 
			{
				if (line.length() < 2)
					continue;
			
				if (words.get(line) == null) 
				{
					tempdata = new Double[] { 1.0, 0.0, 0.0 };
					words.put(line, tempdata);
				}
				
				else 
				{
					tempdata = words.get(line);
					tempdata[0]++;
					words.put(line, tempdata);
				}
				
				totalWords++;
			}
		} 
		
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		// Iterate through the words to fill their tf's
		for (Iterator<String> it = words.keySet().iterator(); it.hasNext();) 
		{
			word = it.next();
			tempdata = words.get(word);
			tempdata[1] = tempdata[0] / (float) totalWords;
			words.put(word, tempdata);
			parent.addWordOccurence(word);
		}
	}
	
	/*
	 * Calculates the tfidf of the words after called by the parent TfIdf class
	 */
	
	public void calculateTfIdf(SG763_TfIdf parent) 
	{
		String word;
		Double[] corpusdata;
		Double[] worddata;
		double tfidf;
	
		try 
		{
			outkeyword = new PrintWriter(new BufferedWriter(new FileWriter(outputFile, true)));
			
			for (Iterator<String> it = words.keySet().iterator(); it.hasNext();) 
			{
				word = it.next();
				corpusdata = parent.allwords.get(word);
				worddata = words.get(word);
				tfidf = worddata[1] * corpusdata[1];
				worddata[2] = tfidf;
				words.put(word, worddata);
				outkeyword.println(word + " " + worddata[2]);
			}
		} 
		
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		finally 
		{
			outkeyword.close();
		}
	}
	
	/*
	 * Gives the most important numWords words
	 */
	
	public String[] bestWordList(int numWords) 
	{
		SortedMap<String, Double[]> sortedWords = new TreeMap<String, Double[]>(new SG763_Document.ValueComparer(words));
		sortedWords.putAll(words);
		int counter = 0;
		String[] bestwords = new String[numWords];
	
		for (Iterator<String> it = sortedWords.keySet().iterator(); it.hasNext() && (counter < numWords); counter++) 
		{
			bestwords[counter] = it.next();
		}
		
		return bestwords;
	}
	
	/*
	 * Inner class to do sorting of the map
	 */
	
	private static class ValueComparer implements Comparator<String> 
	{
		private TreeMap<String, Double[]> _data = null;
		public ValueComparer(TreeMap<String, Double[]> data) 
		{
			super();
			_data = data;
		}
		
		public int compare(String o1, String o2) 
		{
			double e1 = ((Double[]) _data.get(o1))[2];
			double e2 = ((Double[]) _data.get(o2))[2];
		
			if (e1 > e2)
				return -1;
			
			if (e1 == e2)
				return 0;
			
			if (e1 < e2)
				return 1;
			
			return 0;
		}
	}
}