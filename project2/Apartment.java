/**********************************************************************
*	Author:			Michael Adams
*	Last Edit:		11/6/17
*
*	Apartment data type used by priority queues in AptTracker
*
***********************************************************************/

import java.util.Scanner;

public class Apartment {
	//Apartment data fields
	private String address;
	private int num;
	private String city;
	private int zip;
	private int price;
	private int sqft;

	//Apartment constructor
	public Apartment() {
		Scanner inScan = new Scanner(System.in);

		System.out.print("Enter the street address: ");
		String addr = inScan.nextLine();
		setAddress(addr);

		System.out.print("Enter the city: ");
		String city = inScan.nextLine();
		setCity(city);

		System.out.print("Enter the zipcode: ");
		int zip = inScan.nextInt();
		setZip(zip);

		System.out.print("Enter the apartment number: ");
		int aptNum = inScan.nextInt();
		setNum(aptNum);

		System.out.print("Enter the monthly rent in whole USD: ");
		int price = inScan.nextInt();
		setPrice(price);

		System.out.print("Enter the size of the apartment in square feet: ");
		int sqft = inScan.nextInt();
		setSqft(sqft);
	}

	//Apartment getters and setters
	public String getAddress() { return address; }
	public void setAddress(String address) { this.address = address; }

	public int getNum() { return num; }
	public void setNum(int num) { this.num = num; }

	public String getCity() { return city; }
	public void setCity(String city) { this.city = city; }

	public int getZip() { return zip; }
	public void setZip(int zip) { this.zip = zip; }

	public int getPrice() { return price; }
	public void setPrice(int price) { this.price = price; }

	public int getSqft() { return sqft;	}
	public void setSqft(int sqft) {	this.sqft = sqft; }

	//compareTo style function for minPQ
	public int comparePrice(Apartment apartment) {
		if (this.price < apartment.price)
			return -1;
		else if (this.price == apartment.price)
			return 0;
		else
			return 1;
	}

	//compareTo style function for maxPQ
	public int compareSize(Apartment apartment) {
		if (this.sqft < apartment.sqft)
			return -1;
		else if (this.sqft == apartment.sqft)
			return 0;
		else
			return 1;
	}
}