import java.io.*;
import java.net.*;
public class Server{
    public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = null;
		//int counter=1;
		try {
            		serverSocket = new ServerSocket(4000);
        	}catch (IOException ex) {
            		System.out.println("Can't setup server on this port number. ");
			
        	}
		Socket socket = null;
		InputStream in = null;
		OutputStream out = null;
		//while(counter>0){
			try {
				socket = serverSocket.accept();
			} catch (IOException ex) {
				System.out.println("Can't accept client connection. ");
				
			}

			try {
				in = socket.getInputStream();
			} catch (IOException ex) {
				System.out.println("Can't get socket input stream. ");
			}
			try {
				File dir=new File(args[0]);
				System.out.println("This point has been reached");
				if(!dir.exists()){
					System.out.println("Directory created");
					dir.mkdir();
       		     			out = new FileOutputStream(new File(args[0]+"/RoutingInfo.txt"));
					
				}else{
					System.out.println("Directory entered");
					out = new FileOutputStream(new File(args[0]+"/RoutingInfo.txt"));
				}
			} catch (FileNotFoundException ex) {
           			System.out.println("File not found. ");
			}
			byte[] bytes = new byte[16*1024];

			int count;
			while ((count = in.read(bytes)) > 0) {
				out.write(bytes, 0, count);
			}
			System.out.println("Received"); 
			out.close();
			in.close();
			socket.close();
			
		//}

	}
}
