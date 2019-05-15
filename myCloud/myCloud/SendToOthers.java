import java.io.*;
import java.net.*;

public class SendToOthers {
    public static void main(String[] args) throws IOException {
        Socket socket = null;
        //String host = "127.0.0.1";
	String[] slaves = {"engr-hdpn4","engr-hdpn6","engr-hdpn7","engr-hdpn8"};
	for(int i=0;i<slaves.length;i++){
		String host = slaves[i];
        	socket = new Socket(host, 4444);

        	File file = new File("A.txt");
        	// Get the size of the file
        	long length = file.length();
        	byte[] bytes = new byte[16 * 1024];
        	InputStream in = new FileInputStream(file);
        	OutputStream out = socket.getOutputStream();

        	int count;
        	while ((count = in.read(bytes)) > 0) {
            		out.write(bytes, 0, count);
        	}

        	out.close();
        	in.close();
       		socket.close();
    	}
	}
}
