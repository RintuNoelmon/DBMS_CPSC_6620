package cpsc4620;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;

/*
 * This file is where the front end magic happens.
 * 
 * You will have to write the functionality of each of these menu options' respective functions.
 * 
 * This file should need to access your DB at all, it should make calls to the DBNinja that will do all the connections.
 * 
 * You can add and remove functions as you see necessary. But you MUST have all 8 menu functions (9 including exit)
 * 
 * Simply removing menu functions because you don't know how to implement it will result in a major error penalty (akin to your program crashing)
 * 
 * Speaking of crashing. Your program shouldn't do it. Use exceptions, or if statements, or whatever it is you need to do to keep your program from breaking.
 * 
 * 
 */

public class Menu {
	public static void main(String[] args) throws SQLException, IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Welcome to Taylor's Pizzeria!");
		
		int menu_option = 0;

		// present a menu of options and take their selection
		PrintMenu();
		String option = reader.readLine();
		menu_option = Integer.parseInt(option);

		while (menu_option != 9) {
			switch (menu_option) {
			case 1:// enter order
				EnterOrder();
				break;
			case 2:// view customers
				viewCustomers();
				break;
			case 3:// enter customer
				EnterCustomer();
				break;
			case 4:// view order
				// open/closed/date
				ViewOrders();
				break;
			case 5:// mark order as complete
				MarkOrderAsComplete();
				break;
			case 6:// view inventory levels
				ViewInventoryLevels();
				break;
			case 7:// add to inventory
				AddInventory();
				break;
			case 8:// view reports
				PrintReports();
				break;
			}
			PrintMenu();
			option = reader.readLine();
			menu_option = Integer.parseInt(option);
		}

	}

	public static void PrintMenu() {
		System.out.println("\n\nPlease enter a menu option:");
		System.out.println("1. Enter a new order");
		System.out.println("2. View Customers ");
		System.out.println("3. Enter a new Customer ");
		System.out.println("4. View orders");
		System.out.println("5. Mark an order as completed");
		System.out.println("6. View Inventory Levels");
		System.out.println("7. Add Inventory");
		System.out.println("8. View Reports");
		System.out.println("9. Exit\n\n");
		System.out.println("Enter your option: ");
	}

	// allow for a new order to be placed
	public static void EnterOrder() throws SQLException, IOException 
	{
		/*
		 * EnterOrder should do the following:
		 * Ask if the order is for an existing customer -> If yes, select the customer. If no -> create the customer (as if the menu option 2 was selected).
		 * 
		 * Ask if the order is delivery, pickup, or dinein (ask for orderType specific information when needed)
		 * 
		 * Build the pizza (there's a function for this)
		 * 
		 * ask if more pizzas should be be created. if yes, go back to building your pizza. 
		 * 
		 * Apply order discounts as needed (including to the DB)
		 * 
		 * apply the pizza to the order (including to the DB)
		 * 
		 * return to menu
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		ArrayList<Customer> customers;
		ArrayList<Topping> inventory;
		ArrayList<Discount> discount;
		
		double orderTotalCost = 0;
		double orderTotalPrice = 0;
		
		try {
			Order order = new Order();
			String timeStamp = getTimeStamp();
			order.setOrderTimeStamp(timeStamp);                                    //TimeStamp
			
			Scanner sc= new Scanner(System.in);   
			System.out.print("Is this order for an existing customer? Answer y/n: "); 
			char EnterOptions= sc.next().charAt(0);
			
			
			if(EnterOptions == 'y')
			{
				System.out.print("Here is the list of current customer:\n"); 
				customers = DBNinja.getCustomerList();                              //ViewCustomer
				for (Customer customer : customers) {
					System.out.println(customer.toString());
				}
				
				System.out.print("Select the customer ID:"); 
				int CusId= sc.nextInt(); 
				order.setCustID(CusId);                                             //CustomerID
			}
			else
			{
				EnterCustomer();
			}
			
			
			System.out.println("Is this order for : \n1.Dine-In\n2.Pick-Up\n3.Delivery\nselect an option");
			int DeliveryType = sc.nextInt();
			String OrderType = order.getOrderTypeFromInt(DeliveryType);
			order.setOrderType(OrderType);                       //OrderType          
			DBNinja.addOrder(order);
			
			int OrderID = order.getOrderID();
			
			System.out.println("Let's build a pizzas"); 
			
			//AddPizza
			boolean addPizza=true;
		    while(addPizza)
			{
			
			     System.out.println("Enter the details of the pizza\nWhat is the size of the pizza : \n1.Small\n2.Medium\n3.Large\n4.Extra_Large \nPlease enter corresponding num:");
			     int PizzaSize = sc.nextInt();                                    //PizzaSize
			
			     System.out.println("What is the crust of the pizza : \n1.Thin\n2.Original\n3.Pan\n4.Gluten-Free \nPlease enter the corresponding num:");
			     int PizzaCrust = sc.nextInt();                                   //PizzaCrust
			
			     String pizzaTimeStamp = getTimeStamp();                          //PizzaTimeStamp
		
			    Pizza pizza =new Pizza(OrderID, Pizza.getPizzaSizeFromInt(PizzaSize),Pizza.getPizzaCrustFromInt(PizzaCrust),pizzaTimeStamp);		
			    DBNinja.addPizza(pizza);		
		
			
			    //addToppings
			    inventory = DBNinja.getInventory();
			    for (Topping invent : inventory) {
				    System.out.println(invent.toInventString());
			    }
			    System.out.println("Which topping do you want to add?Enter TopID. Enter -1 to stop adding toppings:");
			    int TopId = sc.nextInt();

				while(TopId != -1)
				{
					Topping topping = new Topping(TopId, PizzaSize, pizza.getPizzaID());
					updatingInventoryForPizza(topping);
					pizza.addToppings(topping,false);
					
					System.out.println("Do you want to add extra toppings? Enter y/n");
					char options= sc.next().charAt(0);
					if(options == 'y')
					{
						System.out.println("Printing Current topping list...");
						inventory = DBNinja.getInventory();
						
						for (Topping invent : inventory) {
							System.out.println(invent.toInventString());
						}
						System.out.println("Which topping do you want to add?Enter TopID. Enter -1 to stop adding toppings:");
						TopId = sc.nextInt();
						
					}else
					{	 
						TopId =-1;
					}
				}	
				
				
				
			    //pizzadiscount	
			    System.out.println("Do you want to add discounts to your pizza? Enter y/n");
			    char opt= sc.next().charAt(0);
			
			    while(opt == 'y')
			    {
				    discount = DBNinja.getDiscountList();
				    System.out.println("Getting discount list....");
				    for (Discount Discount : discount) {
					    System.out.println(Discount.toString());
				    }
				
				    System.out.println("Please enter the DiscountID. Enter -1 to stop adding discounts");
				    int DiscId = sc.nextInt();
				
				    if(DiscId != -1)
				    {
					    Discount pizzaDiscount = new Discount(DiscId);
					    DBNinja.useDiscount(pizzaDiscount);
					    DBNinja.insertInPizzaDiscount(pizza.getPizzaID(), DiscId);
					
					    pizza.addDiscounts(pizzaDiscount);
					
					    discount = DBNinja.getDiscountList();
					    for (Discount Discount : discount) {
						    System.out.println(Discount.toString());
					    }
				    }
				    else
				   {
					   break;
				   }
				   System.out.println("Do you want to add discounts to your pizza? Enter y/n");
				   opt= sc.next().charAt(0);
			    }
			    
			    DBNinja.updatePizzaDetails(pizza);
			    orderTotalCost += pizza.getBusPrice();
				orderTotalPrice += pizza.getCustPrice();
			    System.out.println("Do you want to add another pizza : type y/n:");
				addPizza = "y".equals(reader.readLine());
		  }
		    
		    order.setCustPrice(orderTotalPrice);
			order.setBusPrice(orderTotalCost);  
		 //orderDiscount
		 
	     boolean OrderDiscount=false;
	     System.out.println("Do you want to add Discount to the order: type y/n:");
	     char option= sc.next().charAt(0);
	     while (OrderDiscount)
	     {
				 
	    	     ArrayList<Discount> discounts = DBNinja.getDiscountList();
	    	     System.out.println("Getting discount list....");
			     for (Discount Discount : discounts) {
					    System.out.println(Discount.toString());
				 }                                                                                                                                                                                                                                                                                                                                                           
			     
				 System.out.println("Please enter a discount id or -1 if you don't want to add a discount");
				 int OrdDisId = sc.nextInt();
				
				if (OrdDisId == -1) {
					break;
				} 
				else {
					
					Discount pizzaDiscount = new Discount(OrdDisId);
					DBNinja.useDiscount(pizzaDiscount);
					DBNinja.insertInOrderDiscount(order.getOrderID(), OrdDisId);
					order.addDiscount(pizzaDiscount);
				}
				System.out.println("Do you want to add more discount to the order: type y/n:");
				OrderDiscount = "y".equals(reader.readLine());
					
		  }
	     
	      DBNinja.updateOrderDetails(order);
	      System.out.println("Finished adding order...Returning to menu...");
			
		}
		catch (SQLException | IOException e) {
			System.out.println("There was error in displaying the cusotmers " + e);
			e.printStackTrace();
		}
	}
	
	
	private static String getTimeStamp() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		return dtf.format(now);
	}
	
	
	private static void updatingInventoryForPizza(Topping topping) {
       Hashtable<String, Boolean> toppingBooleans = DBNinja.getOrderTopping(topping);
		
		if (toppingBooleans.get("isToppingExist")) {
			if (toppingBooleans.get("isThisExtraTopping")) {
				System.out.println("Already added the topic twice");
			} else {
				DBNinja.getToppingDetails(topping);
				DBNinja.updateIsExtraBoolean(topping);
				topping.setBusPrice(topping.getBusPrice() * 2);
				topping.setCustPrice(topping.getCustPrice() * 2);
			}
		} else {
			DBNinja.getToppingDetails(topping);
			//adding toping in orderTopping table
			DBNinja.addOrderTopping(topping);
		}
		
	}
	
	
	
	
	//view all the customers 
	public static void viewCustomers()
	{
		ArrayList<Customer> customers;
		try {
			customers = DBNinja.getCustomerList();
		
			for (Customer customer : customers) {
				System.out.println(customer.toString());
			}
		}
		catch (SQLException | IOException e) {
			System.out.println("There is an error in displaying the cusotmers " + e);
			e.printStackTrace();
		}
	}
	

	
	
	// Enter a new customer
	public static void EnterCustomer() throws SQLException, IOException 
	{ 
		
		Scanner sc= new Scanner(System.in); 
		System.out.print("Please Enter the Customer first name: ");                           
		String frstname= sc.next();
		System.out.print("Please Enter the Customer last name: ");  
		String lastname= sc.next();
		System.out.print("What is this customer phone number(##########) (No dash/space): ");   //format for PhNum(XXXXXXXXXXX)  
		String phoneNumber= sc.next();
		Customer customer = new Customer(0,frstname,lastname,phoneNumber);
		DBNinja.addCustomer(customer);
		
	}
	
	
	
	

	// View any orders that are not marked as completed
	public static void ViewOrders() throws SQLException, IOException 
	{
		
		ArrayList<Order> orders;
		ArrayList<Order> OrderDetails;
		try {
			Scanner sc= new Scanner(System.in);   
			System.out.print("Would u like to: ");  
			System.out.print("\n(a) display all orders "); 
			System.out.print("\n(b) display orders since a specific date\n");  
			char EnterOptions= sc.next().charAt(0);
			if(EnterOptions == 'a')
			{
				orders = DBNinja.getOrderList();
				
				for (Order order : orders) {
					System.out.println(order.toSimplePrint());
			    }
			}
			else
			{
				System.out.print("What is the date you want to restrict by?(FORMAT YYYY-MM-DD)");     
				String date = sc.next();
				orders = DBNinja.getCurrentOrders(date);
				for (Order order : orders) {
					System.out.println(order.toSimplePrint());
				}
				
			}
			System.out.print("Which order would you like to see in detail? Enter the number:");  
			int OrderNum= sc.nextInt();
			OrderDetails =  DBNinja.OrderDetail(OrderNum); 
			 
			 for (Order orderdetails : OrderDetails) {
					System.out.println(orderdetails.PrintOrderDetail());
		     }
			
			  		
		}
		catch(Exception e)
		{
			System.out.println("There was error in displaying the Orders " + e);
			e.printStackTrace();
		
		}
	}

	
	
	public static void MarkOrderAsComplete() throws SQLException, IOException 
	{
		Scanner sc= new Scanner(System.in);   
		ArrayList<Order> orders = DBNinja.getCurrentOrders(0);
		for (Order order : orders) {
			System.out.println(order.toSimplePrint());
		}
		
		
		System.out.println("Which order would you like to mark as complete:");
		int OrderId= sc.nextInt();
		DBNinja.markOrderAsComplete(OrderId);
	}

	
	
	
	//view inventoryLevels
	public static void ViewInventoryLevels() throws SQLException, IOException 
	{
		ArrayList<Topping> inventory;
		try {
			inventory = DBNinja.getInventory();
		
			System.out.printf(" %-4s  %-17s  %20s %n", "Id", "Topping", "CurINVT");
			for (Topping invent : inventory) {
				    System.out.printf(" %-4s  %-17s  %20s %n", invent.getTopID(), invent.getTopName(), invent.getTopInv());
			}
		}
		catch (SQLException | IOException e) {
			System.out.println("There was error in displaying the inventoryLevels " + e);
			e.printStackTrace();
		}
		
	}

	
	
	
	//add toppings to the current inventory
	public static void AddInventory() throws SQLException, IOException 
	{
		ArrayList<Topping> inventory;
		try {
			inventory = DBNinja.getInventory();
		
			System.out.printf(" %-4s  %-17s  %20s %n", "Id", "Topping", "CurINVT");
			for (Topping invent : inventory) {
				    System.out.printf(" %-4s  %-17s  %20s %n", invent.getTopID(), invent.getTopName(), invent.getTopInv());
			}
			Scanner sc= new Scanner(System.in);  
			System.out.print("which topping you want to add inventory to?Enter the number: ");  
			Integer ToppingNum= sc.nextInt();
			System.out.print("how many units would you like to have: ");  
			Integer ToppingCount= sc.nextInt();
			DBNinja.AddToInventory(ToppingNum,ToppingCount);
			
			
		}
		catch (SQLException | IOException e) {
			System.out.println("There was error in displaying the cusotmers " + e);
			e.printStackTrace();
		}
		
		
		
	}
	
	
	public static void PrintReports() throws SQLException, NumberFormatException, IOException
	{
		Scanner sc= new Scanner(System.in);  
		System.out.print("Which report do you wish to print? Enter\n "); 
		System.out.println("1.)Topping Popularity\n2.)ProfitByPizza\n3.)ProfitByOrderType\n");
		Integer report= sc.nextInt();
		switch (report) {
		case 1:
			DBNinja.printToppingReport();
			
			break;
		case 2:
			DBNinja.printProfitByPizzaReport();
			
			break;
		case 3:
			DBNinja.printProfitByOrderType();
			
			break;
	}
		
	}

}
