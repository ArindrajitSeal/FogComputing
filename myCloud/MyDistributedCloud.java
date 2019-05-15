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


	public class MyDistributedCloud{
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
                                                                ClientServiceThread cliThread = new ClientServiceThread(clientSocket$
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
      new MyDistributedCloud();        
   } 
	
   class ClientServiceThread extends Thread { 
      Socket myClientSocket;
      boolean m_bRunThread = true; 
	
      public ClientServiceThread() { 
         super(); 
      } 
		
      ClientServiceThread(Socket s) { 
         myClientSocket = s;
      }
    	
      public void run() { 
	//synchronized(this){
         //BufferedReader in = null; 
         //PrintWriter out = null;
	InputStream inFromServer =null;
	OutputStream outToServer =null;
	DataInputStream in=null;
	DataOutputStream out=null;
	String path=null;
	FileWriter fw=null;
	BufferedWriter bw=null;
         System.out.println(
            "Accepted Client Address - " + myClientSocket.getInetAddress().getHostName());
         try { 
            /*in = new BufferedReader(
               new InputStreamReader(myClientSocket.getInputStream()));
            out = new PrintWriter(
               new OutputStreamWriter(myClientSocket.getOutputStream()));
            */
		inFromServer =  myClientSocket.getInputStream();
		outToServer = myClientSocket.getOutputStream(); 
		in = new DataInputStream(inFromServer);
		out= new DataOutputStream(outToServer);
	
            while(m_bRunThread) {
		 long startTime = System.currentTimeMillis();
		//path=new String(); 
               //String clientCommand = in.readLine(); 
		//long startTime = System.currentTimeMillis();
		//String MAC=in.readUTF();
		
		//System.out.println("Car Id"+MAC);
		int source = Integer.parseInt(in.readUTF());
		int dest = Integer.parseInt(in.readUTF());
               	//System.out.println("Client Says :" + clientCommand);
		System.out.println("The source is "+source + " the destination is "+dest);
		
		 //long startTime = System.currentTimeMillis();
		Process p=Runtime.getRuntime().exec("java Server");
		if(p.waitFor()==0){
			System.out.println("File Successfully Received");
		}
		else{
			System.out.println("File not received");
		}
		DijkstrasAlgorithm ob=new DijkstrasAlgorithm();
		path=ob.dijkstra(source-1,dest-1,"RoutingInfo.txt");
		
		System.out.println(path);
		out.writeUTF(path);
		//System.out.flush();
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
              // myClientSocket.close(); 
		//path = "";
		//fw.close();
		//bw.close();
               System.out.println("...Stopped");
		
            } catch(IOException ioe) { 
               ioe.printStackTrace(); 
            } 
        // } 
      }
	}
   } 
}
