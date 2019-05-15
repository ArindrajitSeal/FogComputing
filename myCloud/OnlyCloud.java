import java.io.*;

import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.nio.*;
import java.awt.image.*;
import javax.imageio.*;
 
public class OnlyCloud {
   ServerSocket myServerSocket;
   boolean ServerOn = true;
   public OnlyCloud() { 
      try {
         myServerSocket = new ServerSocket(6000);
      } catch(IOException ioe) { 
         System.out.println("Could not create server socket on port 6000. Quitting.");
         System.exit(-1);
      } 
/*		
      Calendar now = Calendar.getInstance();
      SimpleDateFormat formatter = new SimpleDateFormat(
         "E yyyy.MM.dd 'at' hh:mm:ss a zzz");
      System.out.println("It is now : " + formatter.format(now.getTime()));
*/
	System.out.println("Server is on and it is waiting .... ");      
      while(ServerOn) { 
         try { 
            Socket clientSocket = myServerSocket.accept();
            ClientServiceThread cliThread = new ClientServiceThread(clientSocket);
            cliThread.start(); 
         } catch(IOException ioe) { 
            System.out.println("Exception found on accept. Ignoring. Stack Trace :"); 
            ioe.printStackTrace(); 
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
      new OnlyCloud();        
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
      String sendReceiveInfo(Integer source,Integer dest,File f)throws Exception{
	Socket client=new Socket("10.16.76.46",6000);
	OutputStream outToCloud=client.getOutputStream();
	DataOutputStream out=new DataOutputStream(outToCloud);
	InputStream inFromCloud=client.getInputStream();
	DataInputStream in=new DataInputStream(inFromCloud);
	out.writeUTF(source.toString());
	out.writeUTF(dest.toString());
	BufferedImage image=ImageIO.read(f);
	ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
	ImageIO.write(image,"jpg",byteArrayOutputStream);
	byte[]size=ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
	outToCloud.write(size);
	outToCloud.write(byteArrayOutputStream.toByteArray());
	String path=in.readUTF();
	return path;
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
		//path=new String(); 
               //String clientCommand = in.readLine(); 
		//long startTime = System.currentTimeMillis();
		int source = Integer.parseInt(in.readUTF());
		int dest = Integer.parseInt(in.readUTF());
               	//System.out.println("Client Says :" + clientCommand);
		System.out.println("The source is "+source + " the destination is "+dest);
		byte[]sizeAr=new byte[4];
		inFromServer.read(sizeAr);
		int size=ByteBuffer.wrap(sizeAr).asIntBuffer().get();
		byte[]imageAr=new byte[size];
		inFromServer.read(imageAr);
		BufferedImage image=ImageIO.read(new ByteArrayInputStream(imageAr));
		System.out.println("Received Image");
		File f=new File("Image.jpg");
		ImageIO.write(image,"jpg",f);
		long startTime = System.currentTimeMillis();		
		//DijkstrasAlgorithm ob=new DijkstrasAlgorithm();
		//path=ob.dijkstra(source-1,dest-1);
		//Thread.sleep(23);//CNN classification average time
		path=sendReceiveInfo(source,dest,f);
		f.delete();
		//System.out.println(path);
		out.writeUTF(path);
		System.out.flush();
		long endTime = System.currentTimeMillis();
		System.out.println("The latency of computation is "+String.format("%.3f",(endTime-startTime)/1000.0));
		m_bRunThread = false;
		
		              
               if(!ServerOn) { 
                  System.out.print("Server has already stopped"); 
                 // out.println("Server has already stopped"); 
                  //out.flush(); 
                  m_bRunThread = false;
               } /*
               if(clientCommand.equalsIgnoreCase("quit")) {
                  m_bRunThread = false;
                  System.out.print("Stopping client thread for client : ");
               } else if(clientCommand.equalsIgnoreCase("end")) {
                  m_bRunThread = false;
                  System.out.print("Stopping client thread for client : ");
                  ServerOn = false;
               } else {
                  out.println("Server Says : " + clientCommand);
                  out.flush(); 
               } */
            } 
         } catch(Exception e) { 
            e.printStackTrace(); 
         } 
         finally { 
            try { 
               in.close(); 
               out.close(); 
               myClientSocket.close(); 
		path = "";
               System.out.println("...Stopped");
            } catch(IOException ioe) { 
               ioe.printStackTrace(); 
            } 
        // } 
      }
	}
   } 
}
