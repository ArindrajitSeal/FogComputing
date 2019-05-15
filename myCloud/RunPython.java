import java.io.IOException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

/**
 * Hello world!
 *
 */
public class RunPython 
{
/*
    public static void main( String[] args ) throws IOException, InterruptedException
    {
	//String s= "argparse_tutorial.py"+" "+args[0];
    	 // System.out.println(s);  	
    	//Process p = Runtime.getRuntime().exec("python Prediction_without_training_four.py");		
	Process p = Runtime.getRuntime().exec("python3 Prediction_without_training_four.py  "+args[0]);
    	BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
    	String ret = in.readLine();
	while(ret!=null){
	System.out.println(ret);
	ret= in.readLine();

	}
 */

	static String Prediction(String fileName)throws Exception{
		Process p = Runtime.getRuntime().exec("python3 Prediction_without_training_four.py  "+fileName);
    		BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
    		String ret = in.readLine();
		return ret;
   	
  	}/*
	public static void main(String[]args)throws Exception{
		 System.out.println(Prediction("08-3E-8E-8E-37-A2/pic1.jpg"));
	}*/	
    	
}

