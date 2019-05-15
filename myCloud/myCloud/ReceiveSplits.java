import java.net.*;
import java.io.*;
class ReceiveSplits extends Thread{
	ServerSocket ss;
	Socket server;
	InputStream in;
	OutputStream out;
	DataInputStream din;
	DataOutputStream dout;
	float[][]adjacencyMatrix;
	CreateAdjacencyM ob;
	//int count;
	ReceiveSplits(){}
	ReceiveSplits(int port)throws Exception{
		ss=new ServerSocket(port);
		ob=new CreateAdjacencyM("Lower_Man_1stFogInstance.txt");
		adjacencyMatrix=CreateAdjacencyM.arr;
		ss.setSoTimeout(200000);
		//count=1;
	}
	public void run(){
		while(true){
			try{
				server=ss.accept();
				in=server.getInputStream();
				din=new DataInputStream(in);
				int source=Integer.parseInt(din.readUTF());
				int destination=Integer.parseInt(din.readUTF());
				System.out.println("Source is "+source+" Destination is "+destination);
				//String path=DijkstrasAlgorithm.dijkstra(adjacencyMatrix,source-1,destination-1);
				String path=compute(source,destination,adjacencyMatrix);
				System.out.println("The path is "+path);
				
				out=server.getOutputStream();
				dout=new DataOutputStream(out);
				dout.writeUTF(compute(source,destination,adjacencyMatrix));
				System.out.println("Path send");
				//path=new String();
				dout.close();
				din.close();
				//path=new String();
				//break;
			}
			catch(Exception e){
				System.exit(0);
			}
		}
		
	}
	public synchronized String compute(int source,int destination,float[][]adjacencyMatrix){
		String path=DijkstrasAlgorithm.dijkstra(adjacencyMatrix,source-1,destination-1);
		return path;	
	}
	public static void main(String[]args)throws Exception{
		int port=6000;
		Thread t=new ReceiveSplits(port);
		t.start();
			//t.join();
	}
}
