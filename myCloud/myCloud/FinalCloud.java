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
public class FinalCloud{
	ServerSocket myServerSocket;
	static boolean locked=false;
	boolean ServerOn=true;
	static int counter=0;
	static List<Socket> CarInfo;
	DijkstrasAlgorithm ob;
	public FinalCloud(){
		CarInfo = new ArrayList();
		ob=new DijkstrasAlgorithm();
		try{
			myServerSocket = new ServerSocket(16001);
		}catch(IOException e){
			System.out.println("Could not create server socket on port 16001.Quitting");
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
		new FinalCloud();
	}
	class ClientServiceThread extends Thread{
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
			if(FinalCloud.counter>2000){
				call();
			}
			else{
				call1();
			}
		}
		synchronized void call1(){
			System.out.println("Working on " + myClientSocket);
			InputStream inFromServer = null;
			OutputStream outToServer = null;
			DataInputStream in = null;
			DataOutputStream out = null;
			String path;
			System.out.println("Accpeted Client Address - " + myClientSocket.getInetAddress().getHostName());
			try{
				inFromServer = myClientSocket.getInputStream();
				outToServer = myClientSocket.getOutputStream();
				in = new DataInputStream(inFromServer);
				out = new DataOutputStream(outToServer);
				while(m_bRunThread){
					long startTime = System.currentTimeMillis();
					path=new String();
					System.out.println("This point is reached");
					MAC = in.readUTF();
					System.out.println("MAC address is "+MAC);
					int source = Integer.parseInt(in.readUTF());
					int dest = Integer.parseInt(in.readUTF());
					//Checking old Dijkstra matrix
					
					System.out.println("The source and destination are "+source+" "+dest);
					path = ob.dijkstra(source-1,dest-1);
					long endTime = System.currentTimeMillis();
					double diff = endTime - startTime;
					System.out.println("Active time of the cloud(receiving data and figuring out path"+ (diff/1000) + "for "+MAC);
					out.writeUTF(path);//Send to the fog
					System.out.println("The path is "+path);
					/*try{
						Process p=Runtime.getRuntime().exec("java Server "+MAC);
						int status = p.waitFor();
               					String out1=(status==0)?"Success in receiving file":"Failure in receiving file";
						System.out.println(out1);
					}catch(Exception e){
						System.out.println("Something wrong with process creation");
					}*/
					//check whether the file exists or not.
					while(true){
						try{
							File f = new File(MAC+"/RoutingInfo.txt");
							if(f.exists()){
								System.out.println("File found");
								break;
							}
						}catch(Exception e){
							System.out.println("File still not found so wait...");
							try{
								Thread.sleep(5000);
							}catch(Exception ef){}
						}
					}
					System.out.println("This point is reached");
					//Call Dijkstra
                			path=ob.dijkstra(source-1,dest-1,MAC+"/RoutingInfo.txt");
					System.out.println(path);
                			out.writeUTF(path);
					//long endTime = System.currentTimeMillis();
					//System.out.println("The latency of computation is "+String.format("%.3f",(endTime-startTime)/1000.0));
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
					int status=10000;
					in.close();
					out.close();
					myClientSocket.close();
					//fw.close();
					//bw.close();
					System.out.println("..Stopped");
					try{
						Process p=Runtime.getRuntime().exec("rm -r "+MAC);
                                		status=p.waitFor();
					}catch(InterruptedException e){}
                                	String ans=(status==0)?"Success":"Failure";
                                	System.out.println(ans);

				}catch(Exception e){
				}
			}
			FinalCloud.counter--;
		}
		synchronized void call(){
			FinalCloud.locked = true;
			System.out.println("Working on " + myClientSocket);
                        InputStream inFromServer = null;
                        OutputStream outToServer = null;
                        DataInputStream in = null;
                        DataOutputStream out = null;
                        String path;
                        FileWriter fw = null;
                        BufferedWriter bw = null;
                        System.out.println("Accpeted Client Address - " + myClientSocket.getInetAddress().getHostName());
                        try{
                                inFromServer = myClientSocket.getInputStream();
                                outToServer = myClientSocket.getOutputStream();
                                in = new DataInputStream(inFromServer);
                                out = new DataOutputStream(outToServer);
                                while(m_bRunThread){
                                        long startTime = System.currentTimeMillis();
                                        path=new String();
                                        int source = Integer.parseInt(in.readUTF());
                                        int dest = Integer.parseInt(in.readUTF());

                                    //    DijkstrasAlgorithm ob=new DijkstrasAlgorithm();
                                        path = ob.dijkstra(source-1,dest-1);
                                        //float[][]tmp1 = new float[62][62];
    //                                    tmp1 = CreateAdjacencyM.arr;
                                        System.out.println("The path is "+path);
                                        out.writeUTF(path);//Send to the fog
                                        Process p=Runtime.getRuntime().exec("java Server");
                                        if(p.waitFor()==0){
						 System.out.println("File Successfully Received");
                                        }
                                        else{
                                                System.out.println("File not received");
                                        }
                                        //Call Dijkstra
                                        path=ob.dijkstra(source-1,dest-1,"RoutingInfo.txt");
                                        //float[][]tmp2 = new float[62][62];
                                        //tmp2 = CreateAdjacencyM.arr;
                                        System.out.println(path);
                                        out.writeUTF(path);
                                        long endTime = System.currentTimeMillis();
                                        System.out.println("The latency of computation is "+String.format("%.3f",(endTime-startTime)/1000.0));
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
                                }catch(Exception e){
                                }
                        }
			FinalCloud.CarInfo.remove(0);
			FinalCloud.locked = false; 

		}
	}
}

