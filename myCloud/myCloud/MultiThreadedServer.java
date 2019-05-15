import java.io.*;
import java.net.*;

public class MultiThreadedServer implements Runnable {
	InputStream in;
	OutputStream out;
	DataInputStream din;
	DataOutputStream dout;
	static float[][]adjacencyMatrix;
	CreateAdjacencyM ob;
   Socket csocket;
   
   MultiThreadedServer(Socket csocket) {
      this.csocket = csocket;
	
   }
   public static void main(String args[]) throws Exception { 
	CreateAdjacencyM ob=new CreateAdjacencyM("Lower_Man_1stFogInstance.txt");
	adjacencyMatrix=CreateAdjacencyM.arr;
      ServerSocket ssock = new ServerSocket(6000);
      System.out.println("Listening");
      
      
      while (true) {
         Socket sock = ssock.accept();
         System.out.println("Connected");
         new Thread(new MultiThreadedServer(sock)).start();
      }
   }
   public void run() {
      try {
           			in=csocket.getInputStream();
				din=new DataInputStream(in);
				int source=Integer.parseInt(din.readUTF());
				int destination=Integer.parseInt(din.readUTF());
				System.out.println("Source is "+source+" Destination is "+destination);
				String path=DijkstrasAlgorithm.dijkstra(adjacencyMatrix,source-1,destination-1);
				System.out.println("The path is "+path);
				out=csocket.getOutputStream();
				dout=new DataOutputStream(out);
				dout.writeUTF(path);
				System.out.println("Path send");
				dout.close();
				din.close();
				path="";
      } catch (IOException e) {
         System.out.println(e);
      }
   }
}

