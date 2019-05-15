import java.io.*;
import java.util.*;
class CrashDBTest{
	public int m(String img)throws Exception{
		int status;
		int count = 0;	
		File f = new File("CrashResults.txt");
		BufferedWriter bw = new BufferedWriter(new FileWriter(f));	
		Process p = Runtime.getRuntime().exec("/home/arindrajit/darknet/darknet detector test /home/arindrajit/darknet/cfg/obj.data /home/arindrajit/darknet/cfg/yolov3-tiny-sumanta.cfg /home/arindrajit/darknet/backup/yolov3-tiny-sumanta_53100.weights "+img+" -dont_show");
		int i = p.waitFor();
		if(i==0)
			System.out.println("Success");
		else
			System.out.println("Failure");
		
		BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String s = br.readLine();
		count++;
		while(s!=null){
			if(count>4){
				bw.write(s);
				bw.newLine();
				//System.out.println(s);
				s = br.readLine();
				count++;
			}
			else{
				s = br.readLine();
				count++;
			}
		}
		br.close();
		bw.close();
		List<String> l = new ArrayList();
		br = new BufferedReader(new FileReader("CrashResults.txt"));
		if(br.readLine()==null){
			System.out.println("No accidents occured");
			status =0;

			br.close();
			return status;
		}
		
		//Check for categories of accident
		String[]arr2 = {"accident","car up down","car on fire","ambulance","fire truck","police car"};
		s = br.readLine();
		while(s!=null){
			String[]t = s.split(":");
			if(t.length!=2)break;
			t[1] = t[1].replaceAll(" ","");
			int percent = Integer.parseInt(t[1].substring(0,t[1].indexOf('%')));
			if(percent > 50)
				l.add(t[0]);
			s=br.readLine();
		}
		//Check if only police car is present or not
		count = 0;
		for(i=0;i<l.size();i++){
			if(l.get(i).equalsIgnoreCase("police car")){
				count++;
			}
		}
		if(l.size()==count){
			System.out.println("Only Police Car present.Ignore..");
			status = 1; 
		}
		else{
			System.out.println("Other factors exist");
			status=2;
		}
		p = Runtime.getRuntime().exec("rm CrashResults.txt");
		p.waitFor();
		return status;
	}
}

