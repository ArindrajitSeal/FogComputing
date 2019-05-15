import java.io.*;
import java.util.*;
class D{
	int[]n;
	D(){}
	D(int len){
		n = new int[len];
	}
	String[]append(String[]s1,String[]s2){
		String[]tmp=new String[s1.length+s2.length];
		int k=0;
		for(int i=0;i<s1.length;i++){
			tmp[i]=s1[i];
			k=i;
		}
		for(int i=0;i<s2.length;i++){
			tmp[++k]=s2[i];
		}
		return tmp;
	}
	void display(String[]arr){
		for(String x:arr){
			System.out.println(x);
		}
	}
	String display(){
		String finalString = new String();
		System.out.println();
		System.out.println("the value of finalString is "+ finalString);
		System.out.println("the length of n is "+n.length);
		for(int i=0;i<n.length;i++){
			System.out.print(n[i]+"\t");
			finalString+=n[i]+",";
		}
		return finalString;
		
	}
	void changeN(String[]arr3,String cl,int freq){
		//System.out.println("The class is "+cl);
		//System.out.println("The frequency is "+freq);
		//Change the n array
		for(int i=0;i<arr3.length;i++){
			if(cl.equalsIgnoreCase(arr3[i])){
				n[i] = freq;
			}
		}
		//display();
	}
	
	public String call()throws IOException{
		List<String> l = new ArrayList();
		
		
		String[]arr1 = {"person","bicycle","car","motorbike","bus","train","truck","traffic light","fire hydrant","stop sign","parking meter"};
		//String[]arr2 = {"accident","car up down","car on fire","ambulance","fire truck","police car"};
		//String[]arr3 = append(arr1,arr2); 
		D ob=new D(arr1.length);
		File f = new File("Coco_Result.txt");
		FileReader fr = new FileReader(f);
		BufferedReader br = new BufferedReader(fr);
		String s = br.readLine();
		String tmp = new String();
		//System.out.println(s);
		if(s==null){
			return s;
		}
		while(s!=null){
			tmp+=s;
			tmp+="\n";
			s=br.readLine();
		}
		br.close();
		fr.close();
		//tmp+="\n";
		//System.out.println(tmp);



/*
		f = new File("CrashResults.txt");
		fr = new FileReader(f);
		br = new BufferedReader(fr);
		s = br.readLine();
		while(s!=null){
			tmp+=s;
			tmp+="\n";
			s=br.readLine();
		}
		br.close();
		fr.close();*/
		System.out.println(tmp);
		f = new File("NewFile.txt");
		FileWriter fw = new FileWriter(f);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(tmp);
		bw.close();
		fw.close();
		
		//Encoding
		System.out.println();
		f = new File("NewFile.txt");
		fr = new FileReader(f);
		br = new BufferedReader(fr);
		s = br.readLine();
		while(s!=null){
			System.out.println(s);
			String[]t = s.split(":");
			if(t.length!=2)break;
			t[1] = t[1].replaceAll(" ","");
			int percent = Integer.parseInt(t[1].substring(0,t[1].indexOf('%')));
			if(percent>=50){
				l.add(t[0]);
			}
			s = br.readLine();
		}
		System.out.println("Dislaying l "+l);
		br.close();
		Set<String> distinct = new HashSet<>(l);
		ob.display();
		for(String x:distinct){
			System.out.println(x+":"+Collections.frequency(l,x));
			ob.changeN(arr1,x,Collections.frequency(l,x));
			
		}
		
		System.out.println("\nThe final encoding is\n");
		String result = ob.display();
		return result;
		
	}
}
