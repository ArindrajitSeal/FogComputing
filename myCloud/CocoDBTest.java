import java.io.*;
import java.util.*;
class CocoDBTest{
	public static void main(String[]args)throws Exception{
		int count = 0;
		File f = new File("Coco_Result.txt");
		BufferedWriter bw = new BufferedWriter(new FileWriter(f));
		Process p = Runtime.getRuntime().exec("/home/arindrajit/darknet/darknet detector test /home/arindrajit/darknet/cfg/coco.data /home/arindrajit/darknet/cfg/yolov3.cfg /home/arindrajit/darknet/yolov3.weights "+args[0]+" -dont_show");
		
		int i = p.waitFor();
		if(i==0)System.out.println("Success");
		else System.out.println("Failure");
		BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String s = br.readLine();
		System.out.println(s);
		count++;
		while(s!=null){
			if(count>4){
				System.out.println(s);
				bw.write(s);
				bw.newLine();
				s=br.readLine();
				count++;
			}
			else{
				s=br.readLine();
				count++;
			}
		}
		br.close();
		bw.close();
		Random rand = new Random();
		int randomNum = rand.nextInt((10 - 1) + 1) + 1; // A useless step needed for the completion of the algo
		String encoding=new D().call();
		System.out.println(encoding+randomNum);
		f = new File("testCongestion.txt");
		bw = new BufferedWriter(new FileWriter(f));
		// If it is null then I purposely add all zeroes to make it low traffic.
		if(encoding==null){
			for(i=0;i<11;i++){
				bw.write("0"+",");
			}
			bw.write("0");
			bw.newLine();
			return;
		}
		bw.write(encoding+randomNum);
		bw.newLine();
		bw.close();
		p = Runtime.getRuntime().exec("rm Coco_Result.txt");
		p.waitFor();
		p = Runtime.getRuntime().exec("rm NewFile.txt");
		p.waitFor();
	}
}

