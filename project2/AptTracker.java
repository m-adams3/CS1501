/**********************************************************************
*	Author:			Michael Adams
*	Last Edit:		11/6/17
*
*	Compilation:	javac AptTracker.java
*	Execution: 		java AptTracker
*	Dependencies:	Apartment.java, IndexMinPQ.java, IndexMaxPQ.java
*
*	User driven apartment listing application backed by priority queues
*
***********************************************************************/

import java.io.*;
import java.util.*;

public class AptTracker {
	private static IndexMinPQ minPQ;
	private static IndexMaxPQ maxPQ;
	static Scanner inScan = new Scanner(System.in);
	static int size = 0;

	public static void main(String[] args) {
		int maxN = 0;
		boolean done = false;
		int action = 0;

		System.out.println("----- welccome to AptTracker -----");
		System.out.println("This service helps you find apartments");
		//System.out.println("First, enter max number of listings to consider: ");
		//maxN = inScan.nextInt();
		maxN = 20;

		minPQ = new IndexMinPQ(maxN);
		maxPQ = new IndexMaxPQ(maxN);

		//Interact with user
		while(!done) {
			System.out.println();
			System.out.println("Enter a number to choose an action: ");
			System.out.println("1: Add a listing");
			System.out.println("2: Update a listing");
			System.out.println("3: Remove a listing");
			System.out.println("4: Find cheapest listing");
			System.out.println("5: Find largest listing");
			System.out.println("6: Find cheapest listing in a specific city");
			System.out.println("7: Find largest listing in a specific city");
			System.out.println("8: Exit the application");
			System.out.println();
			action = inScan.nextInt();

			switch (action) {
				case 1: addListing(); break;
				case 2: updateListing(); break;
				case 3: removeListing(); break;
				case 4: findCheapest(); break;
				case 5: findLargest(); break;
				case 6: findCheapestIn(); break;
				case 7: findLargestIn(); break;
				case 8: done = true; break;
			}
		}
	}

	//ask user for info about new apartment then add to PQs
	private static void addListing() {
		size++;
		Apartment apartment = new Apartment();
		minPQ.insert(size, apartment);
		maxPQ.insert(size, apartment);
	}

	//ask user for addr, num, zip, find apartment, then (if found) update price
	private static void updateListing() {
		inScan.nextLine();
		System.out.print("Enter the street address: ");
		String addr = inScan.nextLine();
		
		System.out.print("Enter the apartment number: ");
		int aptNum = inScan.nextInt();
		
		System.out.print("Enter the zipcode: ");
		int zip = inScan.nextInt();

		int aptIndex = minPQ.findApartment(addr, aptNum, zip);

		if (aptIndex == -1)
			System.out.println("Apartment listing does not exist, failed to update price");
		else {
			inScan.nextLine();
			System.out.println();
			System.out.println("Enter the new monthly rent in whole USD: ");
			int price = inScan.nextInt();

			minPQ.apartments[aptIndex].setPrice(price);
			maxPQ.apartments[aptIndex].setPrice(price);

			minPQ.change(aptIndex, minPQ.apartments[aptIndex]);
			minPQ.change(aptIndex, minPQ.apartments[aptIndex]);
		}
	}

	//ask user for addr, num, zip, find apartment, then remove from queue
	private static void removeListing() {
		inScan.nextLine();
		
		System.out.print("Enter the street address: ");
		String addr = inScan.nextLine();
		
		System.out.print("Enter the apartment number: ");
		int aptNum = inScan.nextInt();
		
		System.out.print("Enter the zipcode: ");
		int zip = inScan.nextInt();

		int aptIndex = minPQ.findApartment(addr, aptNum, zip);
		minPQ.delete(aptIndex);
		maxPQ.delete(aptIndex);

		size--;
	}

	//retrieve lowest priority key from minPQ
	private static void findCheapest() {
		Apartment cheapestApt = minPQ.minKey();
		System.out.println();
		System.out.println("The cheapest available apartment: ");
		System.out.println("Address: " + cheapestApt.getAddress() + ", " + cheapestApt.getCity() + " " + cheapestApt.getZip());
		System.out.println("Apartment Number: " + cheapestApt.getNum());
		System.out.println("Price: $" + cheapestApt.getPrice() + ".00");
		System.out.println("Size: " + cheapestApt.getSqft() + " sq ft");
	}

	//retrieve highest priority key from maxPQ
	private static void findLargest() {
		Apartment largestApt = maxPQ.maxKey();
		System.out.println();
		System.out.println("The largest available apartment: ");
		System.out.println("Address: " + largestApt.getAddress() + ", " + largestApt.getCity() + " " + largestApt.getZip());
		System.out.println("Apartment Number: " + largestApt.getNum());
		System.out.println("Price: $" + largestApt.getPrice() + ".00");
		System.out.println("Size: " + largestApt.getSqft() + " sq ft");
	}

	//ask user for city then find the first item in the minPQ that matches city
	private static void findCheapestIn() {
		inScan.nextLine();
		System.out.println();
		System.out.print("Enter the city: ");
		String city = inScan.nextLine();

		Apartment apt = minPQ.findCheapestIn(city);
		System.out.println();
		System.out.println("The cheapest available apartment in " + city + ":");
		System.out.println("Address: " + apt.getAddress() + ", " + apt.getZip());
		System.out.println("Apartment Number: " + apt.getNum());
		System.out.println("Price: $" + apt.getPrice() + ".00");
		System.out.println("Size: " + apt.getSqft() + " sq ft");
	}

	//ask user for city then find the first item in the maxPQ that matches city
	private static void findLargestIn() {
		inScan.nextLine();
		System.out.print("\nEnter the city: ");
		String city = inScan.nextLine();

		Apartment apt = maxPQ.findLargestIn(city);
		System.out.println();
		System.out.println("The largest available apartment in " + city + ":");
		System.out.println("Address: " + apt.getAddress() + ", " + apt.getZip());
		System.out.println("Apartment Number: " + apt.getNum());
		System.out.println("Price: $" + apt.getPrice() + ".00");
		System.out.println("Size: " + apt.getSqft() + " sq ft");
	}
}