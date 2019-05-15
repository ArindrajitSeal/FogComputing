import java.io.*;
import java.util.*;
class CreateAdjacencyM{
	Random r=new Random();
	volatile static float[][]arr;
	CreateAdjacencyM(){
		//this.arr=arr;
		arr = new float[62][62];
		for(int i=0;i<arr.length;i++){
			for(int j=0;j<arr[i].length;j++){
				arr[i][j]=(float)-10.0;
			}
		}
		try{
			File f=new File("Lower_Man_1stFogInstance.txt");
                	BufferedReader br=new BufferedReader(new FileReader(f));
                	String s=br.readLine();
                	s=br.readLine();
               		while(s!=null){
                        	String[]tmp=s.split("\t");
                        	String node=tmp[0];
                        	String[]neighbours=tmp[2].split(",");
                        	//Fill array
                        	for(int i=0;i<neighbours.length;i++){
                                	arr[Integer.parseInt(node)-1][Integer.parseInt(neighbours[i])-1]=(float)0.0;
                                	//System.out.println("The value of arr["+(Integer.parseInt(node)-1)+"]["+(Integer.parseInt(neighbours[i])-1)+"] is initi$
                        	}
                        	s=br.readLine();
                	}
		}catch(Exception e){}

	}
	//CreateAdjacencyM(){
		//arr=new float[62][62];
		//initializeMat(arr);
		//File f=new File(filename);
		//BufferedReader br=new BufferedReader(new FileReader(f));
		/*String s=br.readLine();
		s=br.readLine();
		while(s!=null){
			String[]tmp=s.split("\t");
			String node=tmp[0];
			String[]neighbours=tmp[2].split(",");
			//Fill array
			for(int i=0;i<neighbours.length;i++){
				arr[Integer.parseInt(node)-1][Integer.parseInt(neighbours[i])-1]=(float)0.0;
				//System.out.println("The value of arr["+(Integer.parseInt(node)-1)+"]["+(Integer.parseInt(neighbours[i])-1)+"] is initialized");
			}
			s=br.readLine();
		}*/
		
	//}
	void displayNeighbours(){
		for(int i=0;i<arr.length;i++){
			System.out.println("Node "+(i+1)+" has neighbours");
			for(int j=0;j<arr[i].length;j++){
				if(arr[i][j]==0){
					System.out.print("\t"+(j+1));
				}
			}
			System.out.println();
		}
	}
	void changeArray(){
		for(int i=0;i<arr.length;i++){
			for(int j=0;j<arr[i].length;j++){
				if(arr[i][j]==(float)0.0){
					int n=r.nextInt(100)+1;
					arr[i][j]=(float)n/(float)100.0;//Introducing random cars 
				}	
			}
		}
	}
	void displayArray(){
		System.out.println("The size of the array is "+arr.length + " X "+ arr[1].length);
		for(int i=0;i<arr.length;i++){
			for(int j=0;j<arr[i].length;j++){
				System.out.print(arr[i][j]+" ");
			}
			System.out.println();
		}
	}
	void f(int index,String value){
		for(int i=0;i<arr.length;i++){
			if(i==index){
				for(int j=0;j<arr[i].length;j++){
					if(arr[i][j]!=(float)-10.0){
						if(value.equals("Low_Traffic")){
							arr[i][j]=(float)20/(float)100;
							arr[j][i]=arr[i][j];
							
						}
						else if(value.equals("High_Traffic")){
							arr[i][j]=(float)100/(float)100;
							arr[j][i]=arr[i][j];
						}
						else if(value.equals("Pedestrian")){
							arr[i][j]=(float)75/(float)100;
							arr[j][i]=arr[i][j];
						}
						else if(value.equals("Medium_Traffic")){
							arr[i][j]=(float)50/(float)100;
							arr[j][i]=arr[i][j];
						}
					}
				}
			}
		}
	}							
	void adjustArray(String fileName){
		Map<String,String> m=new HashMap();
		try{
			BufferedReader br=new BufferedReader(new FileReader(new File(fileName)));
			String s=br.readLine();
			while(s!=null){
				String[]tmp=s.split("\t");
				m.put(tmp[0],tmp[2]);
				s=br.readLine();
			}
			br.close();
		}catch(Exception e){}
			//Change the array 
			for (Map.Entry<String, String> entry : m.entrySet()) {
    				String key = entry.getKey();
				String value = entry.getValue().toString();
				int index = Integer.parseInt(key)-1;
				f(index,value);
			}
				
    // ...
	}
			
		/*
	public static void main(String[]args)throws Exception{
		CreateAdjacencyM ob=new CreateAdjacencyM();
		ob.displayNeighbours();
		ob.changeArray();
		ob.displayArray();
	}*/
}

