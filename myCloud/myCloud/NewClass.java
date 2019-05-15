import java.io.*;

import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.net.*;
 
public class NewClass {
   String MAC;
   ServerSocket myServerSocket;
   boolean ServerOn = true;
  int counter;
   public NewClass() {
	this.MAC = new String(); 
	counter = 0;
      try {
         myServerSocket = new ServerSocket(4000);
	}catch(SocketTimeoutException e){
		ServerOn = false;
		System.exit(0);
	}
      catch(IOException ioe) { 
         System.out.println("Could not create server socket on port 4444. Quitting.");
         System.exit(-1);
      } 
		

      
      while(ServerOn) { 
         try { 
            Socket clientSocket = myServerSocket.accept();
            ClientServiceThread cliThread = new ClientServiceThread(clientSocket);
            cliThread.start(); 
         } catch(IOException ioe) { 
            System.out.println("Exception found on accept. Ignoring. Stack Trace :"); 
		System.exit(-1);
         }  
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
      new NewClass();        
   } 
	
   class ClientServiceThread extends Thread { 
	int counter;
      Socket myClientSocket;
      boolean m_bRunThread = true; 
      public ClientServiceThread() { 
         super(); 
      } 
		
      ClientServiceThread(Socket s) { 
         myClientSocket = s; 
      } 
		
      public void run() { 
        InputStream in = null;
	InputStream in1 = null;
	OutputStream out = null; 
	DataInputStream din = null;
         System.out.println(
            "Accepted Client Address - " + myClientSocket.getInetAddress().getHostName());
         try { 
            in = myClientSocket.getInputStream();
	in1 = myClientSocket.getInputStream();
            out = myClientSocket.getOutputStream();
            din = new DataInputStream(in1);
            
			
			MAC = din.readUTF();
			System.out.println("The MAC address is "+MAC);
			File dir=new File(MAC);
			System.out.println("This point has been reached");
			if(!dir.exists()){
				System.out.println("Directory created");
				dir.mkdir();
       			     	out = new FileOutputStream(new File(MAC+"/RoutingInfo.txt"));
				counter++;
			}else{
				System.out.println("Directory entered");
				out = new FileOutputStream(new File(MAC+"/RoutingInfo.txt"));
				counter++;
				
			}
		
			
		byte[] bytes = new byte[16*1024];
		int count;
		System.out.println("Reached this piece of code too");
		while ((count = in.read(bytes)) > 0) {
			out.write(bytes, 0, count);			
		}
		

		 
         } catch(Exception e) { 
            e.printStackTrace(); 
         } 
         finally { 
            try { 
               in.close(); 
		in1.close();
               out.close(); 
               myClientSocket.close(); 
               System.out.println("...Stopped"); 
            } catch(IOException ioe) { 
               ioe.printStackTrace(); 
            } 
         } 
      } 
   } 
}
