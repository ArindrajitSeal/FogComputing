import java.io.*;
class FileOps{
	boolean rangeMatch(File[]list){
		int count = list.length;
		for (int i = 0; i < count; i++) {
			for(int j=0;j<count;j++){
				if(list[i].getName().equals(j+".jpg")){
					return true;
				}
			}
		}
		return false;
	}
	void convert(String MAC)throws IOException{
		File folder = new File(MAC);
        	File[] listOfFiles = folder.listFiles();
		
		//Check for range matching 
		if(rangeMatch(listOfFiles)){
			System.out.println("Match found.Exit..");
			return;
		}
		
		for (int i = 0; i < listOfFiles.length; i++) {
            		if (listOfFiles[i].isFile()){
				System.out.println(listOfFiles[i].getName());
				
                			File f = new File(MAC+"/"+listOfFiles[i].getName()); 
                			f.renameTo(new File(MAC+"/"+i+".jpg"));
				
				
					
				
            		}
        	}

        	System.out.println("conversion is done");
    	}
	public static void main(String[]args)throws IOException{
		FileOps ob = new FileOps();
		ob.convert(args[0]); //Pass the MAC address
	}
}	
			
