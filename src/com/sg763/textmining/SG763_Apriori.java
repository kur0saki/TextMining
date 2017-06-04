package com.sg763.textmining;

/*
 * Implementation of Apriori Algorithm
 * Submitted By: Shravani Krishna Rau Gogineni
 * ID: 31376289 / SG763
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class SG763_Apriori 
{
	static Map<Set<Integer>, Integer> candidateSet = new HashMap<Set<Integer>, Integer>();
	static Map<Set<Integer>, Integer> frequentSet = new HashMap<Set<Integer>, Integer>();
	static Map<Set<Integer>, Integer> frequentSetTemp = new HashMap<Set<Integer>, Integer>();
	static Map<Set<Integer>, Set<Set<Integer>>> associationRules = new HashMap<Set<Integer>, Set<Set<Integer>>>();
	static Map<Integer, String> productName = new HashMap<Integer, String>();
	static Map<String, Float> confAR = new HashMap<String, Float>();
	static int supportCount;
	static int userSupport;
	static int size, itemSetNumber;
	static final String outputPath = "C:/Users/Shravni/Documents/NJIT/DM_FinalProject/Keywords";
	static final String aprioriOutPath = "C:/Users/Shravni/Documents/NJIT/DM_FinalProject/Apriori";
	static Map<String, Integer> testSet;
	Map<String, Set<Integer>> dataSet;
	static float minSupport;
	float minConfidence;
	File allwordFile;
	PrintWriter outkeyword = null;

	public SG763_Apriori(Map<String, Set<String>> map, int support, float confidence) 
	{
		Map<String, Set<Integer>> entrySet = new HashMap<String, Set<Integer>>();
		testSet = new HashMap<String, Integer>();
		allwordFile = new File(aprioriOutPath + "/NumberToWordMap.txt");
	
		try 
		{
			outkeyword = new PrintWriter(new BufferedWriter(new FileWriter(allwordFile, false)));
			int i = 0;
			
			for (String key : map.keySet()) 
			{
				Set<Integer> temp = new HashSet<Integer>();
			
				for (String value : map.get(key)) 
				{
					if (testSet.get(value) == null) 
					{
						i++;
						testSet.put(value, i);
						outkeyword.println("Word -> " + value + " : Integer Value -> " + i + "\n");
						temp.add(i);
					}
					
					else 
					{
						temp.add(testSet.get(value));
					}
				}
				
				entrySet.put(key, temp);
			}
			
			dataSet = entrySet;
			userSupport = support;
			minConfidence = confidence;
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
	
	public void InitProcess() 
	{
		size = 0;
		itemSetNumber = 0;
		populateC1();
		minSupport = new Float(userSupport) / new Float(size);
		
		try
		{
			allwordFile = new File(aprioriOutPath + "/C1.txt");
			outkeyword = new PrintWriter(new BufferedWriter(new FileWriter(allwordFile, false)));
	
			for (Set<Integer> key : candidateSet.keySet()) 
			{
				outkeyword.println(key + ": = " + candidateSet.get(key));
			}
			
			outkeyword.close();
			pruneBySupport();
			frequentItemSetGeneration();
			allwordFile = new File(aprioriOutPath + "/AllFrequentItemsets.txt");
			outkeyword = new PrintWriter(new BufferedWriter(new FileWriter(allwordFile, false)));
			outkeyword.println("\n*************************************************");
			outkeyword.println("**************All Frequent Itemsets**************");
			
			for (Set<Integer> k : frequentSet.keySet())
				outkeyword.println(k + ": = " + frequentSet.get(k));
			
			outkeyword.close();
			mineAssociationRules();
			getAssociationRules();
			allwordFile = new File(aprioriOutPath + "/Rules.txt");
			outkeyword = new PrintWriter(new BufferedWriter(new FileWriter(allwordFile, true)));
			outkeyword.println("\n*******************************************");
			outkeyword.println("**************Rule Confidence**************");
			
			for (String i : confAR.keySet())
				outkeyword.println(i + " : " + confAR.get(i));
			
			outkeyword.close();
		} 
		
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	/*
	 * Scanning the database to count the support of each item set
	 */
	
	int numberofOccurence(Set<Integer> set) 
	{
		int supportCount = 0;
	
		try 
		{
			for (String key : dataSet.keySet()) 
			{
				Set<Integer> row = new HashSet<Integer>();
			
				for (Integer i : dataSet.get(key)) 
				{
					row.add(i);
				}
				
				if (row.containsAll(set))
					supportCount++;
			}
		} 
		
		catch (Exception exp) 
		{
			System.out.println(exp);
		}
		
		return supportCount;
	}
	
	/*
	 * Pruning the Candidate Set to generate Frequent Item set based on Support
	 * value entered by user
	 */
	
	void pruneBySupport() 
	{
		itemSetNumber++;
		frequentSetTemp.clear();
	
		for (Set<Integer> key : candidateSet.keySet()) 
		{
			Float sup = new Float(candidateSet.get(key)) / new Float(size);
		
			if (sup >= minSupport) 
			{
				frequentSetTemp.put(key, candidateSet.get(key));
				frequentSet.put(key, candidateSet.get(key));
			}
		}
		
		try 
		{
			allwordFile = new File(aprioriOutPath + "/L.txt");
			outkeyword = new PrintWriter(new BufferedWriter(new FileWriter(allwordFile, true)));
		
			if (!frequentSetTemp.isEmpty()) 
			{
				outkeyword.println("\n*************************************************");
				outkeyword.println("************************L" + itemSetNumber+ "************************");
			
				for (Set<Integer> key : frequentSetTemp.keySet()) 
				{
					outkeyword.println(key + ": = " + frequentSetTemp.get(key));
				}
			} 
			
			else 
			{
				outkeyword.println("\n********************************");
				outkeyword.println("******No More Frequents Sets******");
			}
		} 
		
		catch (Exception e) 
		{	}
		
		finally 
		{
			outkeyword.close();
		}
	}
	
	/*
	 * Generate k+1 Candidate set from k Frequent Item Set
	 */
	
	void frequentItemSetGeneration() 
	{
		boolean next = true;
		int element = 0;
		int size = 1;
		Set<Set<Integer>> candidate = new HashSet<>();
	
		while (next) 
		{
			candidate.clear();
			candidateSet.clear();
		
			for (Set<Integer> l1 : frequentSetTemp.keySet()) 
			{
				Set<Integer> temp = l1;
			
				for (Set<Integer> l2 : frequentSetTemp.keySet()) 
				{
					for (Integer i : l2) 
					{
						try 
						{
							element = i;
						}
						
						catch (Exception e) 
						{
							break;
						}
						
						temp.add(element);
						
						if (temp.size() != size) 
						{
							Integer[] array = temp.toArray(new Integer[0]);
							Set<Integer> temp2 = new HashSet<>();
						
							for (Integer j : array)
								temp2.add(j);
							
							candidate.add(temp2);
							temp.remove(element);
						}
					}
				}
			}
			
			for (Set<Integer> s : candidate) 
			{
				if (pruneCandidate(s))
					candidateSet.put(s, numberofOccurence(s));
			}
			
			try 
			{
				allwordFile = new File(aprioriOutPath + "/C.txt");
				outkeyword = new PrintWriter(new BufferedWriter(new FileWriter(allwordFile, true)));
			
				if (!candidateSet.isEmpty()) 
				{
					outkeyword.println("\n*************************************************");
					outkeyword.println("***********************C*************************");
				
					for (Set<Integer> key : candidateSet.keySet())
						outkeyword.println(key + ": = " + candidateSet.get(key));
				} 
				
				else 
				{
					outkeyword.println("\n*************************************************");
					outkeyword.println("************No more Candidates*******************");
				}
			} 
			
			catch (Exception e) 
			{	}
			
			finally 
			{
				outkeyword.close();
			}
			
			pruneBySupport();
			
			if (frequentSetTemp.size() <= 1)
				next = false;
			
			size++;
		}
	}
	
	/*
	 * Mining the Association Rules from Frequent Item Sets
	 */
	
	void mineAssociationRules() 
	{
		for (Set<Integer> s : frequentSet.keySet()) 
		{
			if (s.size() > 1) 
			{
				mine(s);
			}
		}
	}
	
	/*
	 * Generating subset of entries in frequent item set
	 */
	
	void mine(Set<Integer> itemSet) 
	{
		// According to set symmetry only need to get half the proper subset
		int n = itemSet.size() / 2;
		
		for (int i = 1; i <= n; i++) 
		{
			Set<Set<Integer>> properSubset = SG763_ProperSubset.getProperSubset(i,itemSet);
		
			for (Set<Integer> s : properSubset) 
			{
				Set<Integer> finalset = new HashSet<Integer>();
				finalset.addAll(itemSet);
				finalset.removeAll(s);
				calculateConfidence(s, finalset);
			}
		}
	}
	
	/*
	 * Calculating Confidence of each rule and placing it in Rule Map if it
	 * satisfies the Confidence entered by user
	 */
	
	void calculateConfidence(Set<Integer> s1, Set<Integer> s2) 
	{
		int s1tos2Count = 0;
		int s2tos1Count = 0;
		int supportCount = 0;
	
		try 
		{
			for (String key : dataSet.keySet()) 
			{
				Set<Integer> row = new HashSet<Integer>();
				
				for (Integer i : dataSet.get(key)) 
				{
					row.add(i);
				}
				
				Set<Integer> set1 = new HashSet<Integer>();
				Set<Integer> set2 = new HashSet<Integer>();
				set1.addAll(s1);
				set1.removeAll(row);
				
				if (set1.isEmpty())
					s1tos2Count++;
				
				set2.addAll(s2);
				set2.removeAll(row);
				
				if (set2.isEmpty())
					s2tos1Count++;
				
				if (set1.isEmpty() && set2.isEmpty())
					supportCount++;
			}
		} 
		
		catch (Exception ex) 
		{
			System.out.println("Unable to generate rules" + ex.toString());
		}
		
		Float s1tos2Confidence = new Float(supportCount) / new Float(s1tos2Count);
		
		if (s1tos2Confidence >= minConfidence) 
		{
			if (associationRules.get(s1) == null) 
			{
				Set<Set<Integer>> s2Set = new HashSet<Set<Integer>>();
				s2Set.add(s2);
				confOfAssociationRuleMap(associationRules.put(s1, s2Set),s1tos2Confidence);
			}
			
			else
				associationRules.get(s1).add(s2);
		}
		
		Float s2tos1Confidence = new Float(supportCount) / new Float(s2tos1Count);
		if (s2tos1Confidence >= minConfidence) 
		{
			if (associationRules.get(s2) == null) 
			{
				Set<Set<Integer>> s2Set = new HashSet<Set<Integer>>();
				s2Set.add(s1);
				confOfAssociationRuleMap(associationRules.put(s2, s2Set), s2tos1Confidence);
			}
			
			else
				associationRules.get(s2).add(s1);
		}
	}
	
	/*
	 * Printing Association Rules
	 */
	
	void getAssociationRules() 
	{
		try 
		{
			if (!associationRules.isEmpty()) 
			{
				allwordFile = new File(aprioriOutPath + "/AssociationRules.txt");
				outkeyword = new PrintWriter(new BufferedWriter(new FileWriter(allwordFile, true)));
				outkeyword.println("\n***************************************************");
				outkeyword.println("****************Association Rules******************");
				
				for (Set<Integer> key : associationRules.keySet()) 
				{
					for (Set<Integer> value : associationRules.get(key))
						outkeyword.println(valueToName(key) + "-->" + valueToName(value) + "\n");
				}
			} 
			
			else 
			{
				outkeyword.println("\n**********************************************");
				outkeyword.println("*********No Association Rules Generated*******");
			}
		} 
		
		catch (Exception e) 
		{
			System.out.println(e);
		}
		
		finally 
		{
			outkeyword.close();
		}
	}
	
	/*
	 * Getting the name of the Item in Data set
	 */
	
	String valueToName(Set<Integer> key) 
	{
		String name = "";
	
		for (Map.Entry<String, Integer> e : testSet.entrySet()) 
		{
			int value = e.getValue();
		
			for (Integer i : key) 
			{
				if (i == value) 
				{
					name += e.getKey() + " ";
				}
			}
		}
		return name;
	}
	
	/*
	 * Mapping calculated confidence with the association rules
	 */
	
	void confOfAssociationRuleMap(Set<Set<Integer>> a, Float confidence) 
	{
		String Rule = "";
		Float conf;
	
		for (Set<Integer> key : associationRules.keySet()) 
		{
			for (Set<Integer> value : associationRules.get(key))
				Rule += valueToName(key) + "-->" + valueToName(value);
		}
		
		conf = confidence;
		confAR.put(Rule, conf);
	}
	
	/*
	 * Pruning the Supersets of infrequent item sets
	 */
	
	boolean pruneCandidate(Set<Integer> candidate) 
	{
		Set<Set<Integer>> properSubset = SG763_ProperSubset.getProperSubset(candidate.size() - 1, candidate);
	
		for (Set<Integer> s1 : properSubset) 
		{
			if (s1.size() == candidate.size() - 1) 
			{
				if (frequentSetTemp.get(s1) == null)
					return false;
			}
		}
		
		return true;
	}
	
	/*
	 * Computing C1
	 */
	
	void populateC1() 
	{
		try 
		{
			for (String key : dataSet.keySet()) 
			{
				for (Integer i : dataSet.get(key)) 
				{
					Set<Integer> item = new HashSet<Integer>();
					item.add(i);
					Integer count = candidateSet.get(item);
				
					if (count == null)
						candidateSet.put(item, 1);
					
					else
						candidateSet.put(item, ++count);
				}
				size++;
			}
			
			System.out.println(size);
		} 
		
		catch (Exception ex) 
		{
			System.out.println("Unable to populate C1" + ex.toString());
		}
	}
}