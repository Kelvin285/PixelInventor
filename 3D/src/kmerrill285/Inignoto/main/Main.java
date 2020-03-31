package kmerrill285.Inignoto.main;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintStream;
import java.util.Scanner;

import kmerrill285.Inignoto.Inignoto;

public class Main {
	
	public static boolean DEBUG = true;
	
	public static void main(String[] args) {
		if (!DEBUG)
		try {
			System.out.println("STARTING LOGGER");
			File file = new File("PixelInventor/logs/latest.log");
			if (!file.exists()) {
				String dir = "PixelInventor/logs/";
				new File(dir).mkdir();
				file.createNewFile();
			} else {
				String str = "";
				Scanner scanner = new Scanner(file);
				while (scanner.hasNext()) {
					str += scanner.nextLine()+"\n";
				}
				scanner.close();
				
				int i = 0;
				File f2 = new File("PixelInventor/logs/log"+i+".log");
				while (f2.exists()) {
					f2 = new File("PixelInventor/logs/log"+(i+1)+".log");
					i++;
				}
				f2.createNewFile();
				FileWriter writer = new FileWriter(f2);
				writer.write(str);
				writer.close();
				file.delete();
				file.createNewFile();
			}
			System.setOut(outputFile("PixelInventor/logs/latest.log"));
			System.setErr(outputFile("PixelInventor/logs/latest.log"));
			System.out.println("LOGGER STARTED!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		new Inignoto();
	}
	
	private static PrintStream outputFile(String name) throws Exception {
       return new PrintStream(new BufferedOutputStream(new FileOutputStream(name)), true);
   }
}
