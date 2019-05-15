import java.io.*;
class DeleteFolder{
	public static void main(String[]args)throws Exception{
		Process p = Runtime.getRuntime().exec("rm -r "+args[0]);
		p.waitFor();
	}
}

