/******************************************************************************
 *	Michael Adams
 *	Last Edit: 21 November 2017
 *
 *  Compilation:  javac NetworkAnalysis.java
 *  Execution:    java NetworkAnalysis <network data text file>
 *  
 *  Terminal based application that allows a user to perform basic network analysis
 *
 ******************************************************************************/

import java.io.*;
import java.util.*;

public class NetworkAnalysis
{
	private static EdgeWeightedGraph ewg;

	public static void main(String[] args)
	{
		//get graph from text file specified at command line
		File file;
		try
		{
			file = new File(args[0]);
			Scanner fScan = new Scanner(file);
			ewg = new EdgeWeightedGraph(fScan);
		}
		catch(FileNotFoundException e)
		{
			System.out.println("Exception: " + e);
			return; 
		}
		
		System.out.println();
		System.out.println("----- This application allows you to analyze a network -----");
		showOptions();
		Scanner fScan = new Scanner(System.in);
		int userChoice = fScan.nextInt();
		while(userChoice != 6)
		{
			switch(userChoice)
			{
				case 1: 
					lowestLatencyPath();
					break;
				case 2: 
					System.out.println("2: Copper-Only Connected");
					//use Kruskal to see if non copper edge exists 
					KruskalMST k1 = new KruskalMST(ewg);
					boolean allCopper = true;
					for(Edge e : k1.edges())
					{
						if(!e.isCopper())
							allCopper = false;
					} 
					if(allCopper)
					{
						System.out.println("The Graph is Copper-Only Connected");
						System.out.println();
					}
					else 
					{
						System.out.println("The Graph is not Copper-Only Connected");
						System.out.println();
					}
					break;
				case 3: 
					maxDataAmount();
					try
					{
						Scanner fScan2 = new Scanner(file);
						ewg = new EdgeWeightedGraph(fScan2);
					}
					catch(FileNotFoundException e)
					{
						System.out.println("Exception: " + e);
						return;
					} 
					break;
				case 4: 
					//use Kruskals to find edges of lowest latency spanning tree 
					System.out.println("4: Lowest Average Latency Spanning Tree");
					KruskalMST k2 = new KruskalMST(ewg);
					int cnt = 0;
					System.out.print("Edges: ");
					for(Edge e : k2.edges())
					{
						System.out.print(e.toString() + " ");
						cnt++;
					}
					System.out.println();
					System.out.println("Average Tree Latency: " + (k2.weight()/cnt) + " seconds");
					System.out.println();
					break;
				case 5:  
					//use DFS to check if any vertex in could cause disconnect
					System.out.println("5: Failure Vertices");
					DepthFirstSearch dfs = new DepthFirstSearch(ewg, 0);
					if(dfs.fail())
						System.out.println("There is a failure point in the graph");
					else 
						System.out.println("There is no failure point in this graph");
					System.out.println();
					break;
				default: 
					System.out.println("bad input");
					break; 
			}
			showOptions();
			userChoice = fScan.nextInt();
		}
		
	}
	//show application options to user
	public static void showOptions()
	{
		System.out.println("Choose an Option:");
		System.out.println("1: Lowest Latency Path");
		System.out.println("2: Copper-Only Connected");
		System.out.println("3: Maximum Amount of Data");
		System.out.println("4: Lowest Average Latency Spanning Tree");
		System.out.println("5: Failure Vertices");
		System.out.println("6: Quit");
		System.out.println();
	}
	//find lowest latency path
	public static void lowestLatencyPath()
	{
		//get src and end vertices 
		System.out.println("1: Lowest Latency Path");
		System.out.println("Enter source vertex:");
		Scanner fScan = new Scanner(System.in);
		int src = fScan.nextInt();
		System.out.println("Enter end vertex:");
		int end = fScan.nextInt();
		double min = 10000000000000000000.0;
		//use Dijkstras to find min bandwidth in path
		DijkstraSP dsp = new DijkstraSP(ewg, src);
		System.out.print("Edges: ");
		for(Edge e : dsp.pathTo(end))
		{
			System.out.print(e.toString() + " ");
			if(min > e.weight())
				min = e.weight();
			
		} 
		//print min bandwith and lowest latency tree 
		System.out.printf("\nLowest Bandwidth: %.0f\n", min);
		System.out.println("Lowest Latency from " + src + " to " + end + ": " + dsp.distTo(end) + " seconds");
		System.out.println();
	}
	  
	public static void maxDataAmount()
	{
		//get src and end vertices
		System.out.println("3: Max Amount of Data");
		System.out.println("Enter source vertex:");
		Scanner fScan = new Scanner(System.in);
		int src = fScan.nextInt();
		System.out.println("Enter end vertex:");
		int end = fScan.nextInt();
		//use FF to find max data flow
		FordFulkerson maxFlow = new FordFulkerson(ewg, src, end);
		System.out.printf("Max Data Flow: %.0f\n", maxFlow.value());
		System.out.println();
	}
}