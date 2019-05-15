import java.io.*;
import java.net.*;

public class Server1 {
    public static void main(String[] args) throws IOException {
	int counter=1;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(4444);
        } catch (IOException ex) {
            System.out.println("Can't setup server on this port number. ");
        }

        Socket socket = null;
        InputStream in = null;
        OutputStream out = null;
		String[]path=args[0].split(",");
		int n=path.length;
		while(n>0){
			System.out.println("Reached");
        		try {
        	    		socket = serverSocket.accept();
				//serverSocket.setSoTimeout(200000);
       			 } catch (IOException ex) {
           		 System.out.println("Can't accept client connection. ");
        		}

        	try {
         	   	in = socket.getInputStream();
        	} catch (IOException ex) {
            	System.out.println("Can't get socket input stream. ");
        	}
			System.out.println("This point has been reached");
        	try {
				File dir=new File(args[1]);
				System.out.println("This point has been reached");
				if(!dir.exists()){
					System.out.println("Directory created");
					dir.mkdir();
       		     	out = new FileOutputStream(new File(args[1]+"/pic"+counter+".jpg"));
					counter++;
				}else{
					System.out.println("Directory entered");
					out = new FileOutputStream(new File(args[1]+"/pic"+counter+".jpg"));
					counter++;
				}
			} catch (FileNotFoundException ex) {
           	System.out.println("File not found. ");
			}
			
			byte[] bytes = new byte[16*1024];
			int count;
			System.out.println("Reached this piece of code too");
			while ((count = in.read(bytes)) > 0) {
				out.write(bytes, 0, count);			
			}
			System.out.println("The value of counter is "+counter);
			n--;
			out.close();
			in.close();
			socket.close();
			}
			//serverSocket.close();
		}
	}
