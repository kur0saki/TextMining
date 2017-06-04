package com.sg763.textmining;

/*
 * It is the Driver class, which drives all the other classes using their instances
 * Submitted By: Shravani Krishna Rau Gogineni
 * ID: 31376289 / SG763
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.Map.Entry;

public class SG763_DriverClass 
{
	static final String inputPath = "C:/Users/Shravni/Documents/NJIT/DM_FinalProject/InitialTextDoc";
	static final String outputPath = "C:/Users/Shravni/Documents/NJIT/DM_FinalProject/Keywords";
	static BufferedReader in;
	static PrintWriter out;
	static List<String> bestWords = new ArrayList<>();
	static Map<String, Set<String>> dataSet = new HashMap<String, Set<String>>();
	static int supportCount;
	static float minConfidence;
	static int k;

	public static void main(String[] args) throws IOException 
	{
		Scanner userInput = new Scanner(System.in);
		System.out.println("********************************************\n");
		System.out.println("************TEXT MINING SYSTEM**************\n\n");
		System.out.println("\nThis system is used for keyword based association discovery aong various terms in different documents in a document collection Dataset.\n");
		System.out.println("Data Set Used: Reuters 21578 Collection\n" + "Algorithms Used:\n1. Text mining algorithm : for keyword association mining\n" + "2. Tfidf algorithm : for identifying and extracting keywords\n" + "3. Apriori algorithm : for finding association rules\n");
		System.out.println("\nEnter the Minimum Support Count value (In Integer):");
		supportCount = userInput.nextInt();
		System.out.println("\nEnter the minimum confidence value (In Decimal):");
		minConfidence = userInput.nextFloat();
		System.out.println("\nEnter the no of top/best keywords you wantto select: ");
		k = userInput.nextInt();
		System.out.println("\n*************************************************");
		System.out.println("***************User Entered Values***************");
		System.out.println("\nMinimum SupportCount :" + supportCount + " \nMinimum Confidence :" + minConfidence + "\nTop k keywords :" + k);
		
		/*
		 * Extract key words from all the input files
		 */
		
		SG763_ExtractingKeywords keyword = new SG763_ExtractingKeywords();
		File[] inputFiles = new File(inputPath).listFiles();
		int fileIndex = 0;
		
		for (File file : inputFiles) 
		{
			if (file.isFile() && file.getName().contains(".txt"))
			{
				keyword.keywordExtract(file.getName(), fileIndex, inputPath, outputPath);
				fileIndex++;
			}
		}
		
		/*
		 * Creating an object of class TfIdf to calculate the tfidf values for
		 * all the words in all the documents
		 */
		
		SG763_TfIdf tf = new SG763_TfIdf(outputPath);
		tf.buildAllDocuments();
		File allwordFile = new File(outputPath + "/allwords.txt");
		File topWords = new File(outputPath + "/bestwords.txt");
		
		/*
		 * Reading all tfidf values and adding them to a map for further processing
		 * of all words
		 */
		
		Map<String, Double> map = new HashMap<String, Double>();
		in = new BufferedReader(new FileReader(allwordFile));
		String inputStr = null;
		
		while ((inputStr = in.readLine()) != null) 
		{
			String[] obj = inputStr.split(" ");
			map.put(obj[0], Double.parseDouble(obj[1]));
		}
		in.close();
		
		/*
		 * Sorting the map generated above to get the best words
		 */
		
		Set<Entry<String, Double>> set = map.entrySet();
		List<Entry<String, Double>> list = new ArrayList<Entry<String, Double>>(set);
		
		Collections.sort(list, new Comparator<Map.Entry<String, Double>>() 
		{
			public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) 
			{
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});
		
		out = new PrintWriter(new BufferedWriter(new FileWriter(topWords, false)));
		int i = 0;
		
		for (Map.Entry<String, Double> entry : list) 
		{
			if (i < 100) 
			{
				out.println(entry.getKey());
			}
			
			else
				break;
			i++;
		}
		out.close();
		
		/*
		 * Storing the best words and adding them to local list for comparing it with
		 * individual key word file
		 */
		
		in = new BufferedReader(new FileReader(topWords));
		
		while (in.readLine() != null) 
		{
			bestWords.add(in.readLine());
		}
		in.close();
		
		/*
		 * Invidual key word file are compared with the top words which will generate the
		 * data set for Apriori to work on
		 */
		
		File[] keyFiles = new File(outputPath).listFiles();
		int filenumber = 0;
		
		for (File file : keyFiles) 
		{
			if (file.isFile() && file.getName().contains("keyword")) 
			{
				Set<String> words = new HashSet<String>();
				in = new BufferedReader(new FileReader(file));
				String line = null;
			
				while ((line = in.readLine()) != null) 
				{
					if (bestWords.contains(line)) 
					{
						words.add(line);
					}
				}
				
				dataSet.put(file.getName(), words);
				in.close();
				filenumber++;
			}
		}
		
		File dataset = new File(outputPath + "/dataset.txt");
		out = new PrintWriter(new BufferedWriter(new FileWriter(dataset, false)));
		
		for (String s : dataSet.keySet()) 
		{
			if (!dataSet.get(s).isEmpty())
				out.println(s + " " + dataSet.get(s) + "\n");
		}
		out.close();
		
		/*
		 * Creating an instance of Apriori and applying it on the above generated data set
		 */
		
		SG763_Apriori apr = new SG763_Apriori(dataSet, supportCount, minConfidence);
		apr.InitProcess();
	}
}