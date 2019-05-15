import java.io.*; import java.net.*; import java.text.SimpleDateFormat; import java.util.Calendar; import java.nio.*; import java.awt.image.*; import 
javax.imageio.*; import java.util.*; import java.util.stream.*; import java.nio.file.Files; import java.nio.file.Path; import java.nio.file.Paths; 
public class SirfCloud {
			ServerSocket myServerSocket;
   
			/*InputStream inFromServer;
			OutputStream outToServer;
			DataInputStream in;
			DataOutputStream out;*/
			static boolean locked=false;
			boolean ServerOn = true;
			static int counter=0;
			//static Map<String,String> CarInfo;
			static List<Socket> CarInfo;
			public SirfCloud() {
				//CarInfo = new LinkedHashMap();
				CarInfo = new ArrayList();
				try {
					myServerSocket = new ServerSocket(16000);
				} catch(IOException ioe) {
					System.out.println("Could not create server socket on port 6000. Quitting.");
					System.exit(-1);
				} 
				System.out.println("Server is on and it is waiting .... ");
				while(ServerOn) {
					try {
						Socket clientSocket = myServerSocket.accept();
						Thread t=Thread.currentThread();
						counter++;
						//CarInfo.add(clientSocket);
						//System.out.println("The queue is "+CarInfo);
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
      new SirfCloud();
   } 
	
   class ClientServiceThread extends Thread {
	 
      Socket myClientSocket;
		String MAC;
	  //int source;
	  //int dest;
      boolean m_bRunThread = true;
	
      public ClientServiceThread() {
         super();
      } 
		/*
      ClientServiceThread(Socket s,String MAC,int source,int dest) {
         myClientSocket = s;
		 this.MAC=MAC;
		 this.source=source;
		 this.dest=dest;
      }*/
	  ClientServiceThread(Socket s){
		  myClientSocket =s;
	  }
	  synchronized void call1(){
			SirfCloud.locked = true;
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
            
				inFromServer = myClientSocket.getInputStream();
				outToServer = myClientSocket.getOutputStream();
				in = new DataInputStream(inFromServer);
				out= new DataOutputStream(outToServer);
	
        	    	while(m_bRunThread) {
				path=new String();
               //String clientCommand = in.readLine();
				long startTime = System.currentTimeMillis();
				MAC=in.readUTF();
		
				System.out.println("Car Id"+MAC);
				int source = Integer.parseInt(in.readUTF());
				int dest = Integer.parseInt(in.readUTF());
              // System.out.println("Client Says :" + clientCommand);
				System.out.println("The source is "+source + " the destination is "+dest);
		
				//long startTime = System.currentTimeMillis();
				DijkstrasAlgorithm ob=new DijkstrasAlgorithm();
				path=ob.dijkstra(source-1,dest-1);
		
				System.out.println(path);
				out.writeUTF(path);
				System.out.flush();
				try{
					Process p=Runtime.getRuntime().exec("java Receive "+path+" "+MAC);
					int status=p.waitFor();
					String out1=(status==0)?"Success":"Failure";
					System.out.println(out1);
				}catch(Exception e){}
				//Calculating the result
				long count;
				try (Stream<Path> files = Files.list(Paths.get(MAC))) {
					count = files.count();
				}
				System.out.println("The number of images are "+count);
				String[]paths=path.split(",");
				fw=new FileWriter(new File(MAC+"/"+MAC+".txt"));
				bw=new BufferedWriter(fw);
				for(int i=1;i<=count;i++){
					String result=RunPython.Prediction(MAC+"/pic"+i+".jpg");
					System.out.println(result);
					//bw.write("pic"+i+".jpg"+"\t"+result);
					bw.write(paths[i-1]+"\t"+"pic"+i+".jpg"+"\t"+result);
					bw.newLine();
					bw.flush();
				}
				//Readjust the weight matrix
				DijkstrasAlgorithm ob1=new DijkstrasAlgorithm();
				path=ob1.dijkstra(source-1,dest-1,MAC+"/"+MAC+".txt");
				System.out.println("The updated path is "+path);
				SirfCloud.CarInfo.remove(MAC);// Delete element from the queue
				//Delete the directory
				/*Process p=Runtime.getRuntime().exec("cmd /c rmdir "+MAC+" /S /Q");
				int status=p.waitFor();
				String ans=(status==0)?"Success":"Failure";
				System.out.println(ans);*/
				long endTime = System.currentTimeMillis();
				System.out.println("The latency of computation is "+String.format("%.3f",(endTime-startTime)/1000.0));
				m_bRunThread = false;
		              
               if(!ServerOn) {
                  System.out.print("Server has already stopped");
                 // out.println("Server has already stopped");
                  //out.flush();
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
				//Process p=Runtime.getRuntime().exec("cmd /c rmdir "+MAC+" /S /Q");
				Process p=Runtime.getRuntime().exec("rm -rf "+MAC);
				int status=p.waitFor();
				String ans=(status==0)?"Success":"Failure";
				System.out.println(ans);
				//System.out.println("The final queue " + OnlyFog.CarInfo);
            } catch(Exception ioe) {
               ioe.printStackTrace();
            } 
      }
	  SirfCloud.CarInfo.remove(0);
	  SirfCloud.locked=false;
	 
	  }
	   synchronized void call(){
			//OnlyFog.locked = true;
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
            
				inFromServer = myClientSocket.getInputStream();
				outToServer = myClientSocket.getOutputStream();
				in = new DataInputStream(inFromServer);
				out= new DataOutputStream(outToServer);
	
            while(m_bRunThread) {
				path=new String();
               //String clientCommand = in.readLine();
				long startTime = System.currentTimeMillis();
				MAC=in.readUTF();
		
				System.out.println("Car Id"+MAC);
				int source = Integer.parseInt(in.readUTF());
				int dest = Integer.parseInt(in.readUTF());
              // System.out.println("Client Says :" + clientCommand);
				System.out.println("The source is "+source + " the destination is "+dest);
		
				//long startTime = System.currentTimeMillis();
				DijkstrasAlgorithm ob=new DijkstrasAlgorithm();
				path=ob.dijkstra(source-1,dest-1);
		
				System.out.println(path);
				out.writeUTF(path);
				System.out.flush();
				try{
					Process p=Runtime.getRuntime().exec("java Receive "+path+" "+MAC);
					int status=p.waitFor();
					String out1=(status==0)?"Success":"Failure";
					System.out.println(out1);
				}catch(Exception e){}
				//Calculating the result
				long count;
				try (Stream<Path> files = Files.list(Paths.get(MAC))) {
					count = files.count();
				}
				System.out.println("The number of images are "+count);
				String[]paths=path.split(",");
				fw=new FileWriter(new File(MAC+"/"+MAC+".txt"));
				bw=new BufferedWriter(fw);
				for(int i=1;i<=count;i++){
					String result=RunPython.Prediction(MAC+"/pic"+i+".jpg");
					System.out.println(result);
					//bw.write("pic"+i+".jpg"+"\t"+result);
					bw.write(paths[i-1]+"\t"+"pic"+i+".jpg"+"\t"+result);
					bw.newLine();
					bw.flush();
				}
				//Readjust the weight matrix
				DijkstrasAlgorithm ob1=new DijkstrasAlgorithm();
				path=ob1.dijkstra(source-1,dest-1,MAC+"/"+MAC+".txt");
				System.out.println("The updated path is "+path);
				out.writeUTF(path);
				long endTime = System.currentTimeMillis();
				System.out.println("The latency of computation is "+String.format("%.3f",(endTime-startTime)/1000.0));
				m_bRunThread = false;
		              
               if(!ServerOn) {
                  System.out.print("Server has already stopped");
                 // out.println("Server has already stopped");
                  //out.flush();
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
				Process p=Runtime.getRuntime().exec("cmd /c rmdir "+MAC+" /S /Q");
				int status=p.waitFor();
				String ans=(status==0)?"Success":"Failure";
				System.out.println(ans);
				//System.out.println("The final queue " + OnlyFog.CarInfo);
            } catch(Exception ioe) {
               ioe.printStackTrace();
            } 
      }
	  //OnlyFog.CarInfo.remove(0);
	 // OnlyFog.locked=false;
	 
	  }	
      public void run() {
		  if(counter>2000){
			call1();
		  }
		  else{
			  call();
		  }
		}
		
	}
   } 
