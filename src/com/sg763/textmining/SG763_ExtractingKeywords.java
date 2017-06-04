package com.sg763.textmining;

/* Filter out the text from the input files after parsing of the documents using
 * SNLP and removing stopwords.
 * Submitted By: Shravani Krishna Rau Gogineni
 * ID: 31376289 / SG763
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.*;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import com.google.common.io.Files;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SG763_ExtractingKeywords 
{
	static List<String> stopWords = new ArrayList<>();

	public SG763_ExtractingKeywords() throws IOException 
	{
		File stopFile = new File("C:/Users/Shravni/Documents/NJIT/DM_FinalProject/stop_words.txt");
		BufferedReader in = new BufferedReader(new FileReader(stopFile));
		
		while (in.readLine() != null) 
		{
			stopWords.add(in.readLine());
		}
		
		in.close();
	}
	
	public void keywordExtract(String filePath, int fileindex, String inputPath, String outputPath) 
	{
		/*
		 * Creates a StanfordCoreNLP object, with POS tagging, lemmatization,
		 * NER, parsing, and coreference resolution
		 */
	
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma, ner, depparse");
		props.put("ner.applyNumericClassifiers", "false");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		
		try 
		{
			/*
			 * read some text from the file..
			 */
			
			File inputFileObject = new File(inputPath + "/" + filePath);
			String text = Files.toString(inputFileObject, Charset.forName("UTF-8"));
			
			/*
			 * create an empty Annotation just with the given text
			 */
			
			Annotation document = new Annotation(text);
			
			/*
			 * run all Annotators on this text
			 */
			
			pipeline.annotate(document);
			File fileOutputXml = new File("C:/Users/Shravni/Documents/NJIT/DM_FinalProject/xmlLemma/" + fileindex + ".xml");
			File fileOutputTxt = new File("C:/Users/Shravni/Documents/NJIT/DM_FinalProject/xmlLemma/"+ fileindex + "lemma.txt");
			File fileOutputKeyword = new File(outputPath + "/keyword" + fileindex + ".txt");
			PrintWriter outxml = new PrintWriter(new BufferedWriter(new FileWriter(fileOutputXml, false)));
			PrintStream outtxt = new PrintStream(new FileOutputStream(fileOutputTxt, false));
			PrintWriter outkeyword = new PrintWriter(new BufferedWriter(new FileWriter(fileOutputKeyword, false)));
			pipeline.xmlPrint(document, outxml);
			outxml.close();
			
			/*
			 * Extracting the words from lemma tags generated by SNLP and putting them into
			 * text file
			 */
			
			try 
			{
				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser saxParser = factory.newSAXParser();
			
				DefaultHandler handler = new DefaultHandler() 
				{
					boolean lemma = false;
					public void startElement(String uri, String localName, String qName, Attributes attributes)throws SAXException 
					{
						if (qName.equalsIgnoreCase("LEMMA")) 
						{
							lemma = true;
						}
					}
					
					public void endElement(String uri, String localName, String qName) throws SAXException {	}
					
					public void characters(char ch[], int start, int length) throws SAXException 
					{
						if (lemma) 
						{
							System.out.println(new String(ch, start, length));
							System.setOut(outtxt);
							lemma = false;
						}
					}
				};
				
				saxParser.parse(fileOutputXml, handler);
			} 
			
			catch (Exception e) 
			{	}
			
			finally 
			{
				outtxt.close();
			}
			
			/*
			 * Removing the stop words and extracting only words constituting of
			 * alphabets
			 */
			
			BufferedReader in = new BufferedReader(new FileReader(fileOutputTxt));
			String inputStr = null;
			
			while ((inputStr = in.readLine()) != null) 
			{
				if (inputStr.matches("^[A-Zaz]+$") && !stopWords.contains(inputStr))
					outkeyword.println(inputStr);
			}
			
			in.close();
			outkeyword.close();
		} 
		
		catch (FileNotFoundException ex) 
		{
			ex.printStackTrace();
		} 
		
		catch (IOException ex) 
		{
			ex.printStackTrace();
		}
	}
}