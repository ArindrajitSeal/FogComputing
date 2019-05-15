import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.nio.*;
import java.awt.image.*;
import javax.imageio.*;
import java.util.*;
import java.util.stream.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class FinalCloud1{
	ServerSocket myServerSocket;
	static boolean locked=false;
	boolean ServerOn=true;
	static int counter=0;
	static List<Socket> CarInfo;
	DijkstrasAlgorithm ob;
	//CreateAdjacencyM ob1;
	public FinalCloud1(){
		CarInfo = new ArrayList();
		ob=new DijkstrasAlgorithm();
		try{
			myServerSocket = new ServerSocket(16001);
		}catch(IOException e){
			System.out.println("Could not create server socket on port 16000.Quitting");
			System.exit(-1);
		}
		System.out.println("System is on and is waiting");
		while(ServerOn){
			try{
				Socket clientSocket = myServerSocket.accept();
				Thread t = Thread.currentThread();
				counter++;
				if(counter<2000){
					ClientServiceThread cliThread = new ClientServiceThread(clientSocket);
					cliThread.start();
				}
				else{
					CarInfo.add(clientSocket);
					System.out.println("The queue is "+CarInfo);		
					if(!locked){
						ClientServiceThread cliThread = new ClientServiceThread(clientSocket);
						cliThread.start();
					}
					else{
						while(locked){
							try{
								t.sleep(1000);
								System.out.println("sleeping");
							}catch(InterruptedException e){}
						}
						Socket soc = CarInfo.get(0);
						ClientServiceThread cliThread = new ClientServiceThread(soc);
						cliThread.start();
					}
				}
			}catch(IOException ioe){
				System.out.println("Exception found on accept. Ignoring ");
				ioe.printStackTrace();
			}catch(Exception e){}
		}
		try{
			myServerSocket.close();
			System.out.println("Server Stopped");
		}catch(Exception e){
			System.out.println("Error found. stopping server socket");
			System.exit(-1);
		}
	}
	public static void main(String[]args){
		CreateAdjacencyM ob1 = new CreateAdjacencyM();
		//ob1.c();
		new FinalCloud1();
	}
	class ClientServiceThread extends Thread{
		List<Double> timer = new ArrayList();
		Socket myClientSocket;
		String MAC;
		boolean m_bRunThread = true;
		public ClientServiceThread(){
			super();
		}
		ClientServiceThread(Socket s){
			myClientSocket = s;
		}
		public void run(){
			if(FinalCloud1.counter>2000){
				call();
			}
			else{
				call1();
			}
		}
		void call1(){
			System.out.println("Working on " + myClientSocket);
			InputStream inFromServer = null;
			OutputStream outToServer = null;
			DataInputStream in = null;
			DataOutputStream out = null;
			String path;
			FileWriter fw = null;
			BufferedWriter bw = null;
			System.out.println("Accpeted Client Address - " + myClientSocket.getInetAddress().getHostName());
			double sum1=0,sum2=0,sum3=0,sum4=0;
			try{
				inFromServer = myClientSocket.getInputStream();
				outToServer = myClientSocket.getOutputStream();
				in = new DataInputStream(inFromServer);
				out = new DataOutputStream(outToServer);
				while(m_bRunThread) {
					long startTime = System.currentTimeMillis();
					path=new String(); 
					MAC=in.readUTF();
		
					//System.out.println("Car Id"+MAC);
					int source = Integer.parseInt(in.readUTF());
					int dest = Integer.parseInt(in.readUTF());
					System.out.println("Car Id"+MAC);
					System.out.println("The source is "+source + " the destination is "+dest);
					path = ob.dijkstra(source-1,dest-1);
					System.out.println("The path is "+path);

					//Send the path to the Car via the fog
					out.writeUTF(path);
					long endTime = System.currentTimeMillis();
					double diff = endTime - startTime;
					System.out.println("Path figure time" + diff/1000.0);
					System.out.println("The initial path sent to the car "+ path);

					
					String[]paths=path.split(",");
					int count = 0;
					int mem = 1;
					//Wait until the directory is there.
					while(true){
						if(new File(MAC).exists()){
							System.out.println("Directory created");
							break;
						}
						System.out.println("Waiting for creation of the directory");
					}
					while(true){
						try{
							count = new File(MAC).list().length;
							Thread.sleep(2000);
						}catch(Exception e){
							try{
								System.out.println("Sleep");
								Thread.sleep(1000);
							}catch(Exception ef){
								System.out.println("Interrupt");
							}	
						}
						if(count == new File(MAC).list().length){
							System.out.println("The number of images are "+count);
							break;
						}
					}
					
					//Renaming files in the specific directories 
					
					try{
						Process p = Runtime.getRuntime().exec("java FileOps "+MAC);
						int status = p.waitFor();
						if(status == 0){
							System.out.println("Successfully renamed");
						}
						else{
							System.out.println("Failure in renaming");
						}
					}catch(Exception e){}
					fw=new FileWriter(new File(MAC+"/"+MAC+".txt"));
					bw=new BufferedWriter(fw);

					for(int i=0;i<count;i++){
						startTime = System.currentTimeMillis();
						String predictionFromCoco = PredictCongestion.call(MAC+"/"+i+".jpg");	
						System.out.println(predictionFromCoco);
						long CocoTime = System.currentTimeMillis();
						long diff1 = CocoTime - startTime;
						sum1+=diff1/1000.0;//Time for Coco
						long CrashTime1 = System.currentTimeMillis();
						CrashDBTest ob1 = new CrashDBTest();
						int st = ob1.m(MAC+"/"+i+".jpg");
						long CrashTime2 = System.currentTimeMillis();
						long diff2 = CrashTime2 - CrashTime1;
						sum2+=diff2/1000.0;
						long RuleBasedTime1 = System.currentTimeMillis();
						if(st == 0 && predictionFromCoco.equalsIgnoreCase("High_Traffic")){
							predictionFromCoco = "Medium_Traffic";
						}
						else if(st == 1 && predictionFromCoco.equalsIgnoreCase("High_Traffic")){
							predictionFromCoco = "Medium_Traffic";	
						}
						else if(st == 2 && predictionFromCoco.equalsIgnoreCase("High_Traffic")){
							predictionFromCoco = "High_Traffic";
						}
						else if(st == 0 && predictionFromCoco.equalsIgnoreCase("Medium_Traffic")){
							predictionFromCoco = "Low_Traffic";
						}
						else if(st == 1 && predictionFromCoco.equalsIgnoreCase("Medium_Traffic")){
							predictionFromCoco = "Low_Traffic";
						}
						else if(st == 2 && predictionFromCoco.equalsIgnoreCase("Medium_Traffic")){
							predictionFromCoco = "Medium_Traffic";
						}
						else{
							predictionFromCoco = "Medium_Traffic";
						}
						System.out.println("The Predictions after the changes are "+predictionFromCoco);
						endTime = System.currentTimeMillis();
						long diff3 = endTime - RuleBasedTime1;
						sum3+=diff3/1000.0;
						diff = endTime - startTime;
						System.out.println("Prediction of a single image takes "+ diff/1000.0);
						timer.add((double)(diff/1000));
						bw.write(paths[i]+"\t"+"pic"+i+".jpg"+"\t"+predictionFromCoco);
						bw.newLine();
						bw.flush();
						System.out.println("iteration ends..");
					}
					path=ob.dijkstra(source-1,dest-1,MAC+"/"+MAC+".txt");
					System.out.println(path);
					out.writeUTF(path);
					//long endTime = System.currentTimeMillis();
					//System.out.println("The latency of computation is "+String.format("%.3f",(endTime-startTime)/1000.0));
					//Find out the average time for image classification
					double sum = 0;
					for(int i=0;i<timer.size();i++){
						sum+=timer.get(i);
					}
					System.out.println("The average prediction time for all the images are" + sum/timer.size());
					System.out.println("The average time  for Yolo v3 for "+MAC+" is "+sum1/count);
					System.out.println("The average time  for Tiny Yolo for "+MAC+" is "+sum2/count);
					System.out.println("The average time  for Rule Based for "+MAC+" is "+sum3/count);
					out.close();
					in.close();
						m_bRunThread = false;
					if(!ServerOn){
						System.out.println("Server has already stopped");
						m_bRunThread = false;
					}
				}
			}catch(Exception e ){
			}
			finally{
				try{
					in.close();
					out.close();
					myClientSocket.close();
					fw.close();
					bw.close();
					System.out.println("..Stopped");
					Process p=Runtime.getRuntime().exec("rm -r "+MAC);
					p.waitFor();
				
				}catch(Exception e){
				}
			}
			FinalCloud1.counter--;
		}
		void call(){
			FinalCloud1.locked = true;
			System.out.println("Working on " + myClientSocket);
			InputStream inFromServer = null;
			OutputStream outToServer = null;
			DataInputStream in = null;
			DataOutputStream out = null;
			String path;
			FileWriter fw = null;
			BufferedWriter bw = null;
			System.out.println("Accpeted Client Address - " + myClientSocket.getInetAddress().getHostName());
			double sum1=0,sum2=0,sum3=0,sum4=0;
			try{
				inFromServer = myClientSocket.getInputStream();
				outToServer = myClientSocket.getOutputStream();
				in = new DataInputStream(inFromServer);
				out = new DataOutputStream(outToServer);
				while(m_bRunThread) {
					long startTime = System.currentTimeMillis();
					path=new String(); 
					MAC=in.readUTF();
		
					//System.out.println("Car Id"+MAC);
					int source = Integer.parseInt(in.readUTF());
					int dest = Integer.parseInt(in.readUTF());
					System.out.println("Car Id"+MAC);
					System.out.println("The source is "+source + " the destination is "+dest);
					path = ob.dijkstra(source-1,dest-1);
					System.out.println("The path is "+path);

					//Send the path to the Car via the fog
					out.writeUTF(path);
					long endTime = System.currentTimeMillis();
					double diff = endTime - startTime;
					System.out.println("Path figure time" + diff/1000.0);
					System.out.println("The initial path sent to the car "+ path);

					
					String[]paths=path.split(",");
					int count = 0;
					int mem = 1;
					//Wait until the directory is there.
					while(true){
						if(new File(MAC).exists()){
							System.out.println("Directory created");
							break;
						}
						System.out.println("Waiting for creation of the directory");
					}
					while(true){
						try{
							count = new File(MAC).list().length;
							Thread.sleep(2000);
						}catch(Exception e){
							try{
								System.out.println("Sleep");
								Thread.sleep(1000);
							}catch(Exception ef){
								System.out.println("Interrupt");
							}	
						}
						if(count == new File(MAC).list().length){
							System.out.println("The number of images are "+count);
							break;
						}
					}
					
					//Renaming files in the specific directories 
					
					try{
						Process p = Runtime.getRuntime().exec("java FileOps "+MAC);
						int status = p.waitFor();
						if(status == 0){
							System.out.println("Successfully renamed");
						}
						else{
							System.out.println("Failure in renaming");
						}
					}catch(Exception e){}
					fw=new FileWriter(new File(MAC+"/"+MAC+".txt"));
					bw=new BufferedWriter(fw);

					for(int i=0;i<count;i++){
						startTime = System.currentTimeMillis();
						String predictionFromCoco = PredictCongestion.call(MAC+"/"+i+".jpg");	
						System.out.println(predictionFromCoco);
						long CocoTime = System.currentTimeMillis();
						long diff1 = CocoTime - startTime;
						sum1+=diff1/1000.0;//Time for Coco
						long CrashTime1 = System.currentTimeMillis();
						CrashDBTest ob1 = new CrashDBTest();
						int st = ob1.m(MAC+"/"+i+".jpg");
						long CrashTime2 = System.currentTimeMillis();
						long diff2 = CrashTime2 - CrashTime1;
						sum2+=diff2/1000.0;
						long RuleBasedTime1 = System.currentTimeMillis();
						if(st == 0 && predictionFromCoco.equalsIgnoreCase("High_Traffic")){
							predictionFromCoco = "Medium_Traffic";
						}
						else if(st == 1 && predictionFromCoco.equalsIgnoreCase("High_Traffic")){
							predictionFromCoco = "Medium_Traffic";	
						}
						else if(st == 2 && predictionFromCoco.equalsIgnoreCase("High_Traffic")){
							predictionFromCoco = "High_Traffic";
						}
						else if(st == 0 && predictionFromCoco.equalsIgnoreCase("Medium_Traffic")){
							predictionFromCoco = "Low_Traffic";
						}
						else if(st == 1 && predictionFromCoco.equalsIgnoreCase("Medium_Traffic")){
							predictionFromCoco = "Low_Traffic";
						}
						else if(st == 2 && predictionFromCoco.equalsIgnoreCase("Medium_Traffic")){
							predictionFromCoco = "Medium_Traffic";
						}
						else{
							predictionFromCoco = "Medium_Traffic";
						}
						System.out.println("The Predictions after the changes are "+predictionFromCoco);
						endTime = System.currentTimeMillis();
						long diff3 = endTime - RuleBasedTime1;
						sum3+=diff3/1000.0;
						diff = endTime - startTime;
						System.out.println("Prediction of a single image takes "+ diff/1000.0);
						timer.add((double)(diff/1000));
						bw.write(paths[i]+"\t"+"pic"+i+".jpg"+"\t"+predictionFromCoco);
						bw.newLine();
						bw.flush();
						System.out.println("iteration ends..");
					}
					path=ob.dijkstra(source-1,dest-1,MAC+"/"+MAC+".txt");
					System.out.println(path);
					out.writeUTF(path);
					//long endTime = System.currentTimeMillis();
					//System.out.println("The latency of computation is "+String.format("%.3f",(endTime-startTime)/1000.0));
					//Find out the average time for image classification
					double sum = 0;
					for(int i=0;i<timer.size();i++){
						sum+=timer.get(i);
					}
					System.out.println("The average prediction time for all the images are" + sum/timer.size());
					System.out.println("The average time  for Yolo v3 for "+MAC+" is "+sum1/count);
					System.out.println("The average time  for Tiny Yolo for "+MAC+" is "+sum2/count);
					System.out.println("The average time  for Rule Based for "+MAC+" is "+sum3/count);
					out.close();
					in.close();
						m_bRunThread = false;
					if(!ServerOn){
						System.out.println("Server has already stopped");
						m_bRunThread = false;
					}
				}
			}catch(Exception e ){
			}
			finally{
				try{
					in.close();
					out.close();
					myClientSocket.close();
					fw.close();
					bw.close();
					System.out.println("..Stopped");
					Process p=Runtime.getRuntime().exec("rm -r "+MAC);
					p.waitFor();
				
				}catch(Exception e){
				}
			}
			FinalCloud1.counter--;
			
			FinalCloud1.CarInfo.remove(0);
			FinalCloud1.locked = false; 
		}
	}
}


