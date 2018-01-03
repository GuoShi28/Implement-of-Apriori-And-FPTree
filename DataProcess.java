// Created by Guo Shi at 171220
// process dataset from IBMGenerator
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.*;

public class DataProcess {
	private String DataPath;
	private Vector<Vector<Integer>> DataSet;
	
	public DataProcess(String path) throws IOException {
		SetDataPath(path);
		this.DataSet = new Vector<Vector<Integer>>();
		ReadDate();
	}
	
	private void SetDataPath(String path) {
		this.DataPath = path;
	}
	
	private void ReadDate() throws IOException {
		 File file = new File(this.DataPath);
		 try {
			 FileReader reader = new FileReader(file); 
			 BufferedReader br = new BufferedReader(reader);
			 String str = null;
			 while ((str=br.readLine())!=null) {
				 Str2Vector(str);		 
			 }	 
			 reader.close();
		 } catch (FileNotFoundException e) {
			 System.out.println("File doesn't exist!");
		 } 		 
	}
	
	private void Str2Vector(String str) {
		Pattern p = Pattern.compile("\\d{1,}");
		Matcher m = p.matcher(str);
		Vector<Integer> list = new Vector<Integer>();
		while (m.find()) {
			list.add(new Integer(m.group()));
		}
		this.DataSet.add(list);
	}
	
	public void PrintDataSet() {
		for (int i = 0; i < this.DataSet.size(); i++) {
			Vector<Integer> buf = this.DataSet.elementAt(i);
			for (int j = 0; j < buf.size(); j++) {
				System.out.print(buf.elementAt(j)+" ");
			}
			System.out.println();
		}
	}
	
	public Vector<Vector<Integer>> GetDataSet() {
		return this.DataSet;
	}

	public static void main(String[] args) throws IOException {
		// TODO read dataset from .data
		String path = "/Users/mac/Desktop/Paper/course/DataMining/IBMGenerator-master/T1000L10Item20P20L4.data";
		DataProcess FileData = new DataProcess(path);
		FileData.PrintDataSet();
	}
}
