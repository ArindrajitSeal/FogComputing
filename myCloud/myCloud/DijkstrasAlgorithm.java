import java.io.*;
// A Java program for Dijkstra's
// single source shortest path 
// algorithm. The program is for
// adjacency matrix representation
// of the graph.
class DijkstrasAlgorithm {


	private static final int NO_PARENT = -1;
	String path;

	// Function that implements Dijkstra's
	// single source shortest path
	// algorithm for a graph represented 
	// using adjacency matrix
	// representation
	//float[][]adjacencyMatrix;
	//CreateAdjacencyM ob;
	/*void create(){
		try{
			ob=new CreateAdjacencyM("Lower_Man_1stFogInstance.txt");
		}catch(Exception e){}
		//adjacencyMatrix=CreateAdjacencyM.arr;
	}*/
	String dijkstra(int startVertex,int destination,String fileName){
		path = new String();
		//float[][]adjacencyMatrix;
		/*try{
			//CreateAdjacencyM ob=new CreateAdjacencyM("Lower_Man_1stFogInstance.txt");
			ob.adjustArray(fileName); 
		}catch(Exception e){}*/
		
		//adjacencyMatrix=CreateAdjacencyM.arr;
		//int nVertices = CreateAdjacencyM.arr[0].length;
		CreateAdjacencyM ob = new CreateAdjacencyM();
		ob.adjustArray(fileName);
		int nVertices = CreateAdjacencyM.arr[0].length;
		// shortestDistances[i] will hold the
		// shortest distance from src to i
		float[] shortestDistances = new float[nVertices];

		// added[i] will true if vertex i is
		// included / in shortest path tree
		// or shortest distance from src to 
		// i is finalized
		boolean[] added = new boolean[nVertices];

		// Initialize all distances as 
		// INFINITE and added[] as false
		for (int vertexIndex = 0; vertexIndex < nVertices; vertexIndex++)
		{
			shortestDistances[vertexIndex] = (float)Integer.MAX_VALUE;
			added[vertexIndex] = false;
		}
		
		// Distance of source vertex from
		// itself is always 0
		shortestDistances[startVertex] = (float)0;

		// Parent array to store shortest
		// path tree
		int[] parents = new int[nVertices];

		// The starting vertex does not 
		// have a parent
		parents[startVertex] = NO_PARENT;

		// Find shortest path for all 
		// vertices
		for (int i = 1; i < nVertices; i++)
		{

			// Pick the minimum distance vertex
			// from the set of vertices not yet
			// processed. nearestVertex is 
			// always equal to startNode in 
			// first iteration.
			int nearestVertex = -1;
			float shortestDistance = (float)Integer.MAX_VALUE;
			for (int vertexIndex = 0; vertexIndex < nVertices; vertexIndex++)
			{
				if (!added[vertexIndex] &&
					shortestDistances[vertexIndex] < shortestDistance) 
				{
					nearestVertex = vertexIndex;
					shortestDistance = shortestDistances[vertexIndex];
				}
			}

			// Mark the picked vertex as
			// processed
			added[nearestVertex] = true;

			// Update dist value of the
			// adjacent vertices of the
			// picked vertex.
			for (int vertexIndex = 0; vertexIndex < nVertices; vertexIndex++) 
			{
				float edgeDistance = CreateAdjacencyM.arr[nearestVertex][vertexIndex];
				
				if (edgeDistance > -10
					&& ((shortestDistance + edgeDistance) < 
						shortestDistances[vertexIndex])) 
				{
					parents[vertexIndex] = nearestVertex;
					shortestDistances[vertexIndex] = shortestDistance + edgeDistance;
				}
			}
		}

		String s=printSolution(startVertex, shortestDistances, parents,destination);
		return s;
	}

		
			
	 String dijkstra(int startVertex,int destination)
	{
		//ob=new CreateAdjacencyM();
		path = new String();
		//create();
		//int nVertices = CreateAdjacencyM.arr[0].length;
		int nVertices = CreateAdjacencyM.arr[0].length;
		// shortestDistances[i] will hold the
		// shortest distance from src to i
		float[] shortestDistances = new float[nVertices];

		// added[i] will true if vertex i is
		// included / in shortest path tree
		// or shortest distance from src to 
		// i is finalized
		boolean[] added = new boolean[nVertices];

		// Initialize all distances as 
		// INFINITE and added[] as false
		for (int vertexIndex = 0; vertexIndex < nVertices; vertexIndex++)
		{
			shortestDistances[vertexIndex] = (float)Integer.MAX_VALUE;
			added[vertexIndex] = false;
		}
		
		// Distance of source vertex from
		// itself is always 0
		shortestDistances[startVertex] = (float)0;

		// Parent array to store shortest
		// path tree
		int[] parents = new int[nVertices];

		// The starting vertex does not 
		// have a parent
		parents[startVertex] = NO_PARENT;

		// Find shortest path for all 
		// vertices
		for (int i = 1; i < nVertices; i++)
		{

			// Pick the minimum distance vertex
			// from the set of vertices not yet
			// processed. nearestVertex is 
			// always equal to startNode in 
			// first iteration.
			int nearestVertex = -1;
			float shortestDistance = (float)Integer.MAX_VALUE;
			for (int vertexIndex = 0; vertexIndex < nVertices; vertexIndex++)
			{
				if (!added[vertexIndex] &&
					shortestDistances[vertexIndex] < shortestDistance) 
				{
					nearestVertex = vertexIndex;
					shortestDistance = shortestDistances[vertexIndex];
				}
			}

			// Mark the picked vertex as
			// processed
			added[nearestVertex] = true;

			// Update dist value of the
			// adjacent vertices of the
			// picked vertex.
			for (int vertexIndex = 0; vertexIndex < nVertices; vertexIndex++) 
			{
				float edgeDistance = CreateAdjacencyM.arr[nearestVertex][vertexIndex];
				
				if (edgeDistance > -10
					&& ((shortestDistance + edgeDistance) < 
						shortestDistances[vertexIndex])) 
				{
					parents[vertexIndex] = nearestVertex;
					shortestDistances[vertexIndex] = shortestDistance + edgeDistance;
				}
			}
		}

		String s=printSolution(startVertex, shortestDistances, parents,destination);
		return s;
	}

	// A utility function to print 
	// the constructed distances
	// array and shortest paths
	private String printSolution(int startVertex,float[] distances,int[] parents,int destination)
	{
		int nVertices = distances.length;
		//System.out.print("Vertex\t Distance\tPath");
		/*
		for (int vertexIndex = 0; vertexIndex < nVertices; vertexIndex++) 
		{
			if (vertexIndex != startVertex) 
			{
				System.out.print("\n" + startVertex + " -> ");
				System.out.print(vertexIndex + " \t\t ");
				System.out.print(distances[vertexIndex] + "\t\t");
				printPath(vertexIndex, parents);
			}
		}*/
		System.out.println("The source is "+startVertex);
		System.out.println("The destination is "+destination);
		String s=printPath(destination,parents);
		int index=s.lastIndexOf(",");
		s=s.substring(0,index);
		//Check whether two matrices are equal
		System.out.println("Testing whether the image predictions actually had any effect");
		return s;
	}

	// Function to print shortest path
	// from source to currentVertex
	// using parents array
	private String printPath(int currentVertex,int[] parents)
	{
		// Base case : Source node has
		// been processed
		
		if (currentVertex == NO_PARENT)
		{
			return path;
		}
		//printPath(parents[currentVertex], parents);
		//System.out.print(currentVertex + " ");
		path+= (currentVertex+1) + ",";
		//path+= (currentVertex) + " ";
		return printPath(parents[currentVertex], parents);
	}
/*
	public static void main(String[] args)throws Exception
	{
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		/*int[][] adjacencyMatrix = { { -10, 4, -10, -10, -10, -10, -10, 8, -10 },
									{ 4, -10, 8, -10, -10, -10, -10, 11, -10 },
									{ -10, 8, -10, 7, -10, 4, -10, -10, 2 },
									{ -10, -10, 7, -10, 9, 14, -10, -10, -10 },
									{ -10, -10, -10, 9, -10, 10, -10, -10, -10 },
									{ -10, -10, 4, -10, 10, -10, 2, -10, -10 },
									{ -10, -10, -10, 14, -10, 2, -10, 1, 6 },
									{ 8, 11, -10, -10, -10, -10, 1, -10, 7 },
									{ -10, -10, 2, -10, -10, -10, 6, 7, -10 } };
		//CreateAdjacencyM ob=new CreateAdjacencyM("1.txt");
		//float[][] adjacencyMatrix=CreateAdjacencyM.arr;
		DijkstrasAlgorithm ob=new DijkstrasAlgorithm();
		System.out.println("Enter source and destination");
		String s=ob.dijkstra(Integer.parseInt(br.readLine())-1,Integer.parseInt(br.readLine())-1);
		System.out.print(s);
		//dijkstra(adjacencyMatrix,0,5);
	}*/
	
}

// This article is Contributed by Harikrishnan Rajan

