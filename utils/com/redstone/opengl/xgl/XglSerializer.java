package com.redstone.opengl.xgl;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.log4j.BasicConfigurator;

import com.redstone.opengl.xgl.XglException;
import com.redstone.opengl.xgl.XglParser;
import com.redstone.opengl.xgl.XglWorld;

public class XglSerializer {
	
	/**
	 * Usage: XglSerializer [inputFile] [outputFile]
	 * @param args
	 * @throws XglException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws XglException, IOException { 
		BasicConfigurator.configure();
		
		System.out.println("XGL Serializer Utility\n");
		
		if(args.length < 2) { 
			System.out.println("Invalid number of arguments, Usage: XglSerializer [inputFile] [outputFile]");
		}
		
		String inputFile = args[0];
		String outputFile = args[1];
		
		System.out.println("Input: " + inputFile);
		System.out.println("Output: " + outputFile);
		
		System.out.println("");
		
		XglParser parser = new XglParser();
		
		System.out.println("Parsing " + inputFile + "...");
		
		XglWorld w = parser.parse(inputFile);
		
		FileOutputStream fos = new FileOutputStream(outputFile);
		GZIPOutputStream gzos = new GZIPOutputStream(fos);
		ObjectOutputStream oos = new ObjectOutputStream(gzos);
		
		System.out.println("Saving gzipped object graph to " + outputFile + "...");
		
		oos.writeObject(w);
		
		oos.close();
		gzos.close();
		fos.close();
		
		System.out.println("Done!");
	}
}
