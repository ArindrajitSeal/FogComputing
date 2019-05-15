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
public class FinalFog {
			ServerSocket myServerSocket;
			static boolean locked=false;
			boolean ServerOn = true;
			static int counter=0;
			static List<Socket> CarInfo;
			Server sv;
			public FinalFog() { 
				CarInfo = new ArrayList();
				try {
					myServerSocket = new ServerSocket(16000);
				} catch(IOException ioe) { 
					System.out.println("Could not create server socket on port 16000. Quitting.");
					System.exit(-1);
				} 
				System.out.println("Server is on and it is waiting .... ");      
				while(ServerOn) { 
					try { 
						Socket clientSocket = myServerSocket.accept();
						Thread t=Thread.currentThread();
						counter++;
						if(counter<5){
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
										System.out.println("Sleeping");
									}catch(InterruptedException e){}
								}
								Socket soc=CarInfo.get(0);
								ClientServiceThread cliThread = new ClientServiceThread(soc);
								cliThread.start();
							}
						}
					} catch(IOException ioe) { 
						System.out.println("Exception found on accept. Ignoring. Stack Trace :"); 
						ioe.printStackTrace(); 
					}catch(Exception e){}  
				} 
				try { 
					myServerSocket.close(); 
					System.out.println("Server Stopped"); 
				} catch(Exception ioe) { 
					System.out.println("Error Found stopping server socket"); 
					System.exit(-1); 
				} 
			}
	
	
   public static void main (String[] args) { 
      new FinalFog(); 
   }
	class ClientServiceThread extends Thread { 
	 
      Socket myClientSocket;
      String MAC;
      boolean m_bRunThread = true; 
	List<Double> timer = new ArrayList();	
      public ClientServiceThread() { 
         super(); 
      } 
	  ClientServiceThread(Socket s){
		  myClientSocket = s;
	  }
	   public void run() {
		  if(FinalFog.counter>=5){
			call();
		  }
		  else{
			  call1();
		  }
		}
		/*void getImagesFromEdge(String path,String MAC,InputStream in){
			int counter = 1;
			String[]p=path.split(",");
			int n=p.length;
			OutputStream out = null;
			DataInputStream din = new DataInputStream(in);
			int length = 0;
			//while(n>0){				
				try {
					File dir=new File(MAC);
					System.out.println("This point has been reached");
					if(!dir.exists()){
						System.out.println("Directory created");
						dir.mkdir();
       		     				out = new FileOutputStream(new File(MAC+"/pic"+counter+".jpg"));
						counter++;
					}else{
						System.out.println("Directory entered");
						out = new FileOutputStream(new File(MAC+"/pic"+counter+".jpg"));
						counter++;
					}
				}catch (FileNotFoundException ex) {
           				System.out.println("File not found. ");
				}
				byte[] bytes = new byte[16*1024];
				int count;
				
				System.out.println("Reached this piece of code too");
				try{
					length = (int)din.readLong();
				}catch(IOException e){
					e.printStackTrace();
				}
				System.out.println("Length of the "+(counter-1)+" file is "+length);
				try{
					while (length > 0 && (count = (int)Math.min(bytes.length, length)) > 0) {
						out.write(bytes, 0, count);
						length-=count;
									
					}
				System.out.println("The value of counter is "+counter);
				n--;
				}catch(Exception e){
					e.printStackTrace();
				}
			//}
			
				
		}*/
		synchronized void call1(){
			System.out.println("Working on " + myClientSocket);
			InputStream inFromServer = null;
			OutputStream outToServer =null;
			DataInputStream in=null;
			DataOutputStream out=null;
			String path;
			FileWriter fw=null;
			BufferedWriter bw=null;
			System.out.println("Accepted Client Address - " + myClientSocket.getInetAddress().getHostName());
			try { 
            
				inFromServer =  myClientSocket.getInputStream();
				outToServer = myClientSocket.getOutputStream(); 
				in = new DataInputStream(inFromServer);
				out= new DataOutputStream(outToServer);
	
            			while(m_bRunThread) {
					//long startTime = System.currentTimeMillis();
					path=new String(); 
					MAC=in.readUTF();
		
					System.out.println("Car Id"+MAC);
					int source = Integer.parseInt(in.readUTF());
					int dest = Integer.parseInt(in.readUTF());
					String CloudIp = "167.99.237.84";
					int port = 16001;
					Socket client = new Socket(CloudIp,port);
					System.out.println("Just connected to "+client.getRemoteSocketAddress());
					InputStream inFromCloud=client.getInputStream();
					OutputStream outToCloud = client.getOutputStream();
					DataInputStream din=new DataInputStream(inFromCloud);
					DataOutputStream dout=new DataOutputStream(outToCloud);
					String S=String.valueOf(source);
					String D=String.valueOf(dest);
					dout.writeUTF(MAC);
					dout.writeUTF(S);
					dout.writeUTF(D);
					//Receive path from the cloud
					path = din.readUTF();
					//Send the path to the Car
					out.writeUTF(path);
					System.out.println("The path is " + path);
				/*try{
					/*Process p=Runtime.getRuntime().exec("java Server "+path+" "+MAC);
					int status=p.waitFor();
					String out1=(status==0)?"Success in receiving images":"Failure in receiving images";
					System.out.println(out1);
					sv.server(path,MAC);
					
		
					
					
				}catch(Exception e){}*/

					
					//getImagesFromEdge(path,MAC,inFromServer);
					int count = 0;
					int mem = 1;
					while(true){
						try{
							count = new File(MAC).list().length;
							Thread.sleep(1000);
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
				
					System.out.println("The number of images are "+(count));
					Thread.sleep(2000);
					String[]paths=path.split(",");
					fw=new FileWriter(new File(MAC+"/"+MAC+".txt"));
					bw=new BufferedWriter(fw);
					//Pipeline ob = new Pipeline();
					for(int i=0;i<count;i++){
						long startTime  =System.currentTimeMillis();
						//String result=RunPython.Prediction(MAC+"/pic"+i+".jpg");
						String predictionFromCoco = PredictCongestion.call(MAC+"/"+i+".jpg");	
						System.out.println(predictionFromCoco);
						CrashDBTest ob1 = new CrashDBTest(MAC);
						int st = ob1.m(MAC+"/pic"+i+".jpg");
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
						long endTime = System.currentTimeMillis();
						long diff = endTime-startTime;
						System.out.println("Time to classify an image based on two different yolo models are "+(diff/1000));
						timer.add((double)(diff/1000));
						bw.write(paths[i]+"\t"+"pic"+i+".jpg"+"\t"+predictionFromCoco);
						bw.newLine();
						bw.flush();
					}
					Process p=Runtime.getRuntime().exec("java Client1 "+MAC+"/"+MAC+".txt"+" "+MAC);
					if(p.waitFor()==0){
						System.out.println("successfully sent to cloud");
					}
					else{
						System.out.println("Sending to cloud not successful");
					}
					double sum = 0;
					//Calculate the average latency in image classification
					for(int i=0;i<timer.size();i++){
						double times = timer.get(i);
						sum+=times;
					}
					System.out.println("The average latency in image classification for a single car is is "+(sum/timer.size()));
					//Receive the path
					path=din.readUTF();
					System.out.println("The updated path is "+path);
					out.writeUTF(path);//To the edge
				
					long endTime = System.currentTimeMillis();
					//System.out.println("The latency of computation is "+String.format("%.3f",(endTime-startTime)/1000.0));
					dout.close();
					din.close();
					client.close();
					m_bRunThread = false;
		              
              		if(!ServerOn) { 
                 		 System.out.print("Server has already stopped"); 
                  		m_bRunThread = false;
               		} 
           	} 
         } catch(Exception e) { 
            e.printStackTrace(); 
         } 
         finally { 
            try { 
               in.close(); 
               out.close(); 
               myClientSocket.close(); 
				fw.close();
				bw.close();
               System.out.println("...Stopped");
				Process p=Runtime.getRuntime().exec("rm -r "+MAC);
				int status=p.waitFor();
				String ans=(status==0)?"Success":"Failure";
				System.out.println(ans);
            } catch(Exception ioe) { 
               ioe.printStackTrace(); 
            } 
      }
	  FinalFog.counter--;
	 
	  }
	  synchronized void call(){
		  FinalFog.locked=true;
		  /*System.out.println("Working on " + myClientSocket);
			InputStream inFromServer = null;
			OutputStream outToServer =null;
			DataInputStream in=null;
			DataOutputStream out=null;
			String path;
			FileWriter fw=null;
			BufferedWriter bw=null;
			System.out.println("Accepted Client Address - " + myClientSocket.getInetAddress().getHostName());
			try { 
            
				inFromServer =  myClientSocket.getInputStream();
				outToServer = myClientSocket.getOutputStream(); 
				in = new DataInputStream(inFromServer);
				out= new DataOutputStream(outToServer);
	
            while(m_bRunThread) {
				long startTime = System.currentTimeMillis();
				path=new String(); 
				MAC=in.readUTF();
		
				
				int source = Integer.parseInt(in.readUTF());
				int dest = Integer.parseInt(in.readUTF());
				System.out.println("Car Id"+MAC);
				System.out.println("The source is "+source + " the destination is "+dest);
				String CloudIp = "167.99.237.84";
				int port = 16000;
				Socket client = new Socket(CloudIp,port);
				System.out.println("Just connected to "+client.getRemoteSocketAddress());
				InputStream inFromCloud=client.getInputStream();
				OutputStream outToCloud = client.getOutputStream();
				DataInputStream din=new DataInputStream(inFromCloud);
				DataOutputStream dout=new DataOutputStream(outToCloud);
				dout.writeUTF(MAC);
				String S=String.valueOf(source);
				String D=String.valueOf(dest);
				
				dout.writeUTF(S);
				dout.writeUTF(D);
				//Receive path from the cloud
				path = din.readUTF();
				//Send the path to the Car
				out.writeUTF(path);
				System.out.println("The path is " + path);
				
				
				try{
					Process p=Runtime.getRuntime().exec("java Server "+path+" "+MAC);
					int status=p.waitFor();
					String out1=(status==0)?"Success":"Failure";
					System.out.println(out1);
				}catch(Exception e){}
				long count;
				try (Stream<Path> files = Files.list(Paths.get(MAC))) {
					count = files.count();
				}
				System.out.println("The number of images are "+count);
				String[]paths=path.split(",");
				fw=new FileWriter(new File(MAC+"/"+MAC+".txt"));
				bw=new BufferedWriter(fw);
				//Pipeline ob = new Pipeline();
				for(int i=1;i<=count;i++){
					String predictionFromCoco = PredictCongestion.call(MAC+"/pic"+i+".jpg");	
					System.out.println(predictionFromCoco);
					CrashDBTest ob1 = new CrashDBTest(MAC);
					int st = ob1.m(MAC+"/pic"+i+".jpg");
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
					bw.write(paths[i-1]+"\t"+"pic"+i+".jpg"+"\t"+predictionFromCoco);
					bw.newLine();
					bw.flush();
				}
				
				Process p=Runtime.getRuntime().exec("java Client "+MAC+"/"+MAC+".txt");
				if(p.waitFor()==0){
					System.out.println("successfully sent to cloud");
				}
				else{
					System.out.println("Sending to cloud not successful");
				}
				//Receive the path
				path=din.readUTF();
				System.out.println("The updated path is "+path);
				out.writeUTF(path);//To the edge
				
				long endTime = System.currentTimeMillis();
				System.out.println("The latency of computation is "+String.format("%.3f",(endTime-startTime)/1000.0));
				dout.close();
				din.close();
				client.close();
				m_bRunThread = false;
		              
               if(!ServerOn) { 
                  System.out.print("Server has already stopped"); 
                  m_bRunThread = false;
               } 
            } 
         } catch(Exception e) { 
            e.printStackTrace(); 
         } 
         finally { 
            try { 
               in.close(); 
               out.close(); 
               myClientSocket.close(); 
				fw.close();
				bw.close();
               System.out.println("...Stopped");
				Process p=Runtime.getRuntime().exec("rm -r "+MAC);
				int status=p.waitFor();
				String ans=(status==0)?"Success":"Failure";
				System.out.println(ans);
            } catch(Exception ioe) { 
               ioe.printStackTrace(); 
            } 
      }*/
	
	System.out.println("Working on " + myClientSocket);
			InputStream inFromServer = null;
			OutputStream outToServer =null;
			DataInputStream in=null;
			DataOutputStream out=null;
			String path;
			FileWriter fw=null;
			BufferedWriter bw=null;
			System.out.println("Accepted Client Address - " + myClientSocket.getInetAddress().getHostName());
			try { 
            
				inFromServer =  myClientSocket.getInputStream();
				outToServer = myClientSocket.getOutputStream(); 
				in = new DataInputStream(inFromServer);
				out= new DataOutputStream(outToServer);
	
            			while(m_bRunThread) {
					long startTime = System.currentTimeMillis();
					path=new String(); 
					MAC=in.readUTF();
		
					System.out.println("Car Id"+MAC);
					int source = Integer.parseInt(in.readUTF());
					int dest = Integer.parseInt(in.readUTF());
					String CloudIp = "167.99.237.84";
					int port = 16001;
					Socket client = new Socket(CloudIp,port);
					System.out.println("Just connected to "+client.getRemoteSocketAddress());
					InputStream inFromCloud=client.getInputStream();
					OutputStream outToCloud = client.getOutputStream();
					DataInputStream din=new DataInputStream(inFromCloud);
					DataOutputStream dout=new DataOutputStream(outToCloud);
					String S=String.valueOf(source);
					String D=String.valueOf(dest);
					dout.writeUTF(MAC);
					dout.writeUTF(S);
					dout.writeUTF(D);
					//Receive path from the cloud
					path = din.readUTF();
					//Send the path to the Car
					out.writeUTF(path);
					System.out.println("The path is " + path);
				/*try{
					/*Process p=Runtime.getRuntime().exec("java Server "+path+" "+MAC);
					int status=p.waitFor();
					String out1=(status==0)?"Success in receiving images":"Failure in receiving images";
					System.out.println(out1);
					sv.server(path,MAC);
					
		
					
					
				}catch(Exception e){}*/

					
					//getImagesFromEdge(path,MAC,inFromServer);
					int count = 0;
					int mem = 1;
					while(true){
						try{
							count = new File(MAC).list().length;
							Thread.sleep(1000);
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
				
					System.out.println("The number of images are "+(count));
					Thread.sleep(2000);
					String[]paths=path.split(",");
					fw=new FileWriter(new File(MAC+"/"+MAC+".txt"));
					bw=new BufferedWriter(fw);
					//Pipeline ob = new Pipeline();
					for(int i=0;i<count;i++){
						//String result=RunPython.Prediction(MAC+"/pic"+i+".jpg");
						String predictionFromCoco = PredictCongestion.call(MAC+"/"+i+".jpg");	
						System.out.println(predictionFromCoco);
						CrashDBTest ob1 = new CrashDBTest(MAC);
						int st = ob1.m(MAC+"/pic"+i+".jpg");
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
						bw.write(paths[i]+"\t"+"pic"+i+".jpg"+"\t"+predictionFromCoco);
						bw.newLine();
						bw.flush();
					}
					Process p=Runtime.getRuntime().exec("java Client1 "+MAC+"/"+MAC+".txt"+" "+MAC);
					if(p.waitFor()==0){
						System.out.println("successfully sent to cloud");
					}
					else{
						System.out.println("Sending to cloud not successful");
					}
					//Receive the path
					path=din.readUTF();
					System.out.println("The updated path is "+path);
					out.writeUTF(path);//To the edge
				
					long endTime = System.currentTimeMillis();
					System.out.println("The latency of computation is "+String.format("%.3f",(endTime-startTime)/1000.0));
					dout.close();
					din.close();
					client.close();
					m_bRunThread = false;
		              
              		if(!ServerOn) { 
                 		 System.out.print("Server has already stopped"); 
                  		m_bRunThread = false;
               		} 
           	} 
         } catch(Exception e) { 
            e.printStackTrace(); 
         } 
         finally { 
            try { 
               in.close(); 
               out.close(); 
               myClientSocket.close(); 
				fw.close();
				bw.close();
               System.out.println("...Stopped");
				Process p=Runtime.getRuntime().exec("rm -r "+MAC);
				int status=p.waitFor();
				String ans=(status==0)?"Success":"Failure";
				System.out.println(ans);
            } catch(Exception ioe) { 
               ioe.printStackTrace(); 
            } 
      }
	
	  FinalFog.CarInfo.remove(0);
	 FinalFog.counter--;
	  FinalFog.locked=false;

	  }
	}
}



