package cpsc4620;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.*;
import java.util.Date;

/*
 * This file is where most of your code changes will occur You will write the code to retrieve
 * information from the database, or save information to the database
 * 
 * The class has several hard coded static variables used for the connection, you will need to
 * change those to your connection information
 * 
 * This class also has static string variables for pickup, delivery and dine-in. If your database
 * stores the strings differently (i.e "pick-up" vs "pickup") changing these static variables will
 * ensure that the comparison is checking for the right string in other places in the program. You
 * will also need to use these strings if you store this as boolean fields or an integer.
 * 
 * 
 */

/**
 * A utility class to help add and retrieve information from the database
 */

public final class DBNinja {
	private static Connection conn;

	// Change these variables to however you record dine-in, pick-up and delivery,
	// and sizes and
	// crusts
	public final static String pickup = "pickup";
	public final static String delivery = "delivery";
	public final static String dine_in = "dinein";

	public final static String size_s = "small";
	public final static String size_m = "medium";
	public final static String size_l = "Large";
	public final static String size_xl = "XLarge";

	public final static String crust_thin = "Thin";
	public final static String crust_orig = "Original";
	public final static String crust_pan = "Pan";
	public final static String crust_gf = "Gluten-Free";

	/**
	 * This function will handle the connection to the database
	 * 
	 * @return true if the connection was successfully made
	 * @throws SQLException
	 * @throws IOException
	 */
	private static boolean connect_to_db() throws SQLException, IOException {

		try {
			conn = DBConnector.make_connection();
			return true;
		} catch (SQLException e) {
			return false;
		} catch (IOException e) {
			return false;
		}

	}

	/**
	 *
	 * @param o order that needs to be saved to the database
	 * @throws SQLException
	 * @throws IOException
	 * @requires o is not NULL. o's ID is -1, as it has not been assigned yet. The
	 *           pizzas do not exist in the database yet, and the topping inventory
	 *           will allow for these pizzas to be made
	 * @ensures o will be assigned an id and added to the database, along with all
	 *          of it's pizzas. Inventory levels will be updated appropriately
	 */
	
	
	public static void addOrder(Order o) throws SQLException, IOException {
		connect_to_db();
		/*
		 * add code to add the order to the DB. Remember that we're not just
		 * adding the order to the order DB table, but we're also recording
		 * the necessary data for the delivery, dinein, and pickup tables
		 */
		Integer customerId = o.getCustID();
		String[] generatedId = { "ID" };
		
		String insertStatement = "INSERT INTO ordertable"
		        + "(CustomerID,OrderCompleteState,orderType,OrderPrice,OrderCost,OrderTimeStamp) " + "VALUES (" + customerId + ",false,'"
		        + o.getOrderType() + "',0.00,0.00,'" + o.getOrderTimeStamp() + "')";
		
		PreparedStatement preparedStatement = conn.prepareStatement(insertStatement, generatedId);
		
		int result = preparedStatement.executeUpdate();
		
		if (result > 0) {
			try {
				ResultSet resultSet = preparedStatement.getGeneratedKeys();
				if (resultSet.next()) {
					o.setOrderID(resultSet.getInt(1));
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				try {
					conn.close();
				}
				catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	
	public static void addPizza(Pizza p) throws SQLException, IOException
	{
		connect_to_db();
		/*
		 * Add the code needed to insert the pizza into into the database.
		 * Keep in mind adding pizza discounts to that bridge table and 
		 * instance of topping usage to that bridge table if you have't accounted
		 * for that somewhere else.
		 */
		String crustType = p.getCrustType();
		String size = p.getSize();
		Integer orderId = p.getOrderID();
		String PizzaState = p.getPizzaState();
		String PizzaDate=p.getPizzaDate();
		Double CustPrice=p.getCustPrice();
		Double BusPrice=p.getBusPrice();

		String[] generatedId = { "ID" };
		DBNinja.getBasePizzaDetails(p);
		String insertStatement ="insert into pizza(OrderID,PizzaSize,PizzaCrustType,PizzaCompleteState,PizzaTimeStamp,PizzaCost,PizzaPrice)\n"
				+ "values('"+orderId+"','"+size+"','"+ crustType+"',false,'" +PizzaDate +"','"+CustPrice+"','"+BusPrice +"')";
		
        PreparedStatement preparedStatement = conn.prepareStatement(insertStatement, generatedId);
		
		int result = preparedStatement.executeUpdate();
		
		if (result > 0) {
			try {
				ResultSet resultSet = preparedStatement.getGeneratedKeys();
				if (resultSet.next()) {
					p.setPizzaID(resultSet.getInt(1));
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				try {
					conn.close();
				}
				catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	
	public static Hashtable<String, Boolean> getOrderTopping(Topping topping) {
		Hashtable<String, Boolean> resultBooleans = new Hashtable<>();
		Boolean isToppingExist = false;
		Boolean isThisExtraTopping = false;
		try {
			connect_to_db();
			try (Statement statement = conn.createStatement();
			        ResultSet resultSet = statement.executeQuery("select * from pizzatopping where PizzaID = "
			                + topping.getPizzaID() + " and ToppingID = " + topping.getTopID() + ";")) {
				
				while (resultSet.next()) {
					isToppingExist = true;
					isThisExtraTopping = resultSet.getBoolean("ExtraTopping");
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				conn.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		resultBooleans.put("isToppingExist", isToppingExist);
		resultBooleans.put("isThisExtraTopping", isThisExtraTopping);
		
		return resultBooleans;
	}
	
	
	
	public static void getToppingDetails(Topping topping) {
		Double toppingQuantity = 0d;
		
		String toppingSize = topping.getToppingSizeFromPizzaSize(topping.getPizzaSize());
		try {
			connect_to_db();
			
			try (Statement statement = conn.createStatement();
			        ResultSet resultSet = statement
			                .executeQuery("select * from topping where ToppingId = " + topping.getTopID() + ";")) {
				
				while (resultSet.next()) {
					toppingQuantity = resultSet.getDouble(toppingSize);
					topping.setBusPrice(resultSet.getDouble("ToppingCost") * toppingQuantity);
					topping.setCustPrice(resultSet.getDouble("ToppingPrice") * toppingQuantity);
				}
			}
			updateInventory(topping.getTopID(), -1 * (toppingQuantity));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				conn.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	
	
	public static void addOrderTopping(Topping topping) {
		try {
			connect_to_db();
			String insertStatement = "insert into pizzatopping(PizzaID,ToppingID,ExtraTopping)" + "values" + "("
			        + topping.getPizzaID() + "," + topping.getTopID() + ",false);";
			
			PreparedStatement preparedStatement;
			
			preparedStatement = conn.prepareStatement(insertStatement);
			preparedStatement.executeUpdate();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				conn.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	
	public static void updateIsExtraBoolean(Topping topping) {
		try {
			connect_to_db();
			
			String updateStatement = "update pizzatopping set ExtraTopping = true where OrderID = ? and ToppingID = ?;";
			
			PreparedStatement preparedStatement = conn.prepareStatement(updateStatement);
			
			preparedStatement.setInt(1, topping.getPizzaID());
			
			preparedStatement.setInt(2, topping.getTopID());
			
			preparedStatement.executeUpdate();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				conn.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	
	
	public static int getMaxPizzaID() throws SQLException, IOException
	{
		connect_to_db();
		/*
		 * A function I needed because I forgot to make my pizzas auto increment in my DB.
		 * It goes and fetches the largest PizzaID in the pizza table.
		 * You wont need this function if you didn't forget to do that
		 */
		
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return -1;
	}
	
	public static void useTopping(Pizza p, Topping t, boolean isDoubled) throws SQLException, IOException //this function will update toppings inventory in SQL and add entities to the Pizzatops table. Pass in the p pizza that is using t topping
	{
		connect_to_db();
		/*
		 * This function should 2 two things.
		 * We need to update the topping inventory every time we use t topping (accounting for extra toppings as well)
		 * and we need to add that instance of topping usage to the pizza-topping bridge if we haven't done that elsewhere
		 * Ideally, you should't let toppings go negative. If someone tries to use toppings that you don't have, just print
		 * that you've run out of that topping.
		 */
		
		
		
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}
	
	
	public static void usePizzaDiscount(Pizza p, Discount d) throws SQLException, IOException
	{
		connect_to_db();
		/*
		 * Helper function I used to update the pizza-discount bridge table. 
		 * You might use this, you might not depending on where / how to want to update
		 * this table
		 */
		
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}
	
	public static void useDiscount(Discount d) throws SQLException, IOException
	{
		connect_to_db();
		/*
		 * Helper function I used to update the pizza-discount bridge table. 
		 * You might use this, you might not depending on where / how to want to update
		 * this table
		 */
		try {
			connect_to_db();
			try (Statement statement = conn.createStatement();
			        ResultSet resultSet = statement
			                .executeQuery("select * from discount where DiscountID = " + d.getDiscountID() + ";")) {
				
				while (resultSet.next()) {
					d.setAmount(resultSet.getDouble("DiscountDollarOff"));
					d.setDiscountName(resultSet.getString("DiscountType"));
					d.setPercent(resultSet.getBoolean("IsPercentOff"));
				}
				
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				conn.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void updatePizzaDetails(Pizza pizza) {
		
		try {
			connect_to_db();
			String updateStatement = "update pizza set PizzaCost = ?, PizzaPrice = ? where PizzaID = ? ;";
			
			PreparedStatement preparedStatement = conn.prepareStatement(updateStatement);
			
			preparedStatement.setDouble(1, pizza.getCustPrice());
			
			preparedStatement.setDouble(2, pizza.getBusPrice());
			
			preparedStatement.setInt(3, pizza.getPizzaID());
			
			preparedStatement.executeUpdate();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				conn.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void updateOrderDetails(Order ordertable) {
		try {
			connect_to_db();
			String updateStatement = "update ordertable set OrderCost = ?, OrderPrice = ? where OrderId = ? ;";
			
			PreparedStatement preparedStatement = conn.prepareStatement(updateStatement);
			
			preparedStatement.setDouble(1, ordertable.getCustPrice());
			
			preparedStatement.setDouble(2, ordertable.getBusPrice());
			
			preparedStatement.setInt(3, ordertable.getOrderID());
			
			preparedStatement.executeUpdate();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				conn.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void insertInPizzaDiscount(int pizzaID, Integer discountId) {
		String insertStatement = "insert into pizzadiscount(PizzaID,DiscountID)" + "values" + "(" + pizzaID + ","
		        + discountId + ")";
		
		PreparedStatement preparedStatement;
		try {
			connect_to_db();
			preparedStatement = conn.prepareStatement(insertStatement);
			preparedStatement.executeUpdate();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				conn.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void insertInOrderDiscount(int orderId, int discountId) {
		String insertStatement = "insert into orderdiscount(OrderID,DiscountID)" + "values" + "(" + orderId + ","
		        + discountId + ")";
		
		PreparedStatement preparedStatement;
		try {
			connect_to_db();
			preparedStatement = conn.prepareStatement(insertStatement);
			preparedStatement.executeUpdate();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				conn.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}


	//add customer in the database
	public static void addCustomer(Customer c) throws SQLException, IOException {
		connect_to_db();
		String firstname = c.getFName();
		String lastname = c.getLName();
		String phoneNumber = c.getPhone();
		Integer customerId = null;
		
		String[] generatedId = { "ID" };
		
		String insertStatement = "INSERT INTO customer(CustomerFName,CustomerLName,CustomerPhNum) VALUES (?,?,?)";
		
		PreparedStatement preparedStatement = conn.prepareStatement(insertStatement, generatedId);
		
		preparedStatement.setString(1, firstname);
		preparedStatement.setString(2, lastname);
		preparedStatement.setString(3, phoneNumber);
		
		int result = preparedStatement.executeUpdate();
		
		if (result > 0) {
			try {
				ResultSet rs = preparedStatement.getGeneratedKeys();
				if (rs.next()) {
					customerId = rs.getInt(1);
				}
			}
			catch (Exception e) {
				
			}
			finally {
				try {
					conn.close();
				}
				catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	
	
	
	public static void CompleteOrder(Order o) throws SQLException, IOException {
		connect_to_db();
		
		
		/*
		 * add code to mark an order as complete in the DB. You may have a boolean field
		 * for this, or maybe a completed time timestamp. However you have it.
		 */
		


		
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}


	
	//add the inventory to the current inventory
	public static void AddToInventory(int toppingNum, int toppingCount) throws SQLException, IOException {
		connect_to_db();
		try 
		{
		
		String query = "update topping set ToppingInventory = ToppingInventory+ ? where ToppingId=?" ;
		PreparedStatement preparedStatement = conn.prepareStatement(query);
		
		preparedStatement.setInt(1, toppingCount);
		preparedStatement.setInt(2, toppingNum);
		
		int result = preparedStatement.executeUpdate();
	
		}
		catch (SQLException e) {
			System.out.println(e);
		}
	}

	

	public void printInventory() throws SQLException, IOException {
		connect_to_db();
		
	}
	
	
	//get inventory from DB
	public static ArrayList<Topping> getInventory() throws SQLException, IOException {
		ArrayList<Topping> inventory = new ArrayList<Topping>();
		connect_to_db();
		
		
		try (Statement statement = conn.createStatement();
		        ResultSet resultSet = statement.executeQuery("select * from topping order by ToppingName;")) {
			
			while (resultSet.next()) {
				Integer toppingId = resultSet.getInt("ToppingID");
				String toppingName = resultSet.getString("ToppingName");
				Integer toppingInventory = resultSet.getInt("ToppingInventory");
				
				inventory.add(new Topping(toppingId, toppingName, toppingInventory));
			
			}
			
		}
		catch (SQLException e) {
			System.out.println(e);
		}
        return inventory;
	}
	
	
	
	public static void updateInventory(Integer toppingId, Double value) throws SQLException, IOException {
		connect_to_db();
		
		String updateStatement = "update topping set ToppingInventory = ToppingInventory +(?) where ToppingId = ? ;";
		
		PreparedStatement preparedStatement = conn.prepareStatement(updateStatement);
		
		preparedStatement.setDouble(1, value);
		
		preparedStatement.setInt(2, toppingId);
		
		preparedStatement.executeUpdate();
		
	}
	
	public static ArrayList<Order> getCurrentOrders() throws SQLException, IOException {
		return getCurrentOrders(null, null);
	}
	
	
	//display orders with the restricted date
	public static ArrayList<Order> getCurrentOrders(String date) throws SQLException, IOException {
		return getCurrentOrders(date, null);
		
	}
	
	
	public static ArrayList<Order> getCurrentOrders(Integer status) throws SQLException, IOException {
		return getCurrentOrders(null, status);
		
	}


	public static ArrayList<Order> getCurrentOrders(String date, Integer status) throws SQLException, IOException {
        ArrayList<Order> orders = new ArrayList<Order>();
		
		try {
			connect_to_db();
			
			String selectQuery = "select * from ordertable";
			if (date != null) {
				selectQuery += " where (OrderTimeStamp >= '" + date + " 00:00:00')";
			} else if(status != null) {
				selectQuery += " where OrderCompleteState = " + status;
			}
			selectQuery += " order by OrderID desc;";
			
			Statement statement = conn.createStatement();
			
			ResultSet resultSet = statement.executeQuery(selectQuery);
			if(resultSet == null)
			{
				System.out.println("There are no open orders currently....Returning to menu");
			}
			while (resultSet.next()) {
				Integer orderId = resultSet.getInt("OrderID");
				Integer customerId = resultSet.getInt("CustomerID");
				String orderType = resultSet.getString("OrderType");
				String orderTimeStamp = resultSet.getString("OrderTimeStamp");
				Integer OrderCompleteState = resultSet.getInt("OrderCompleteState");
				Double orderCost = resultSet.getDouble("OrderCost");
				Double orderPrice = resultSet.getDouble("OrderPrice");
				
				orders.add(
				    new Order(orderId, customerId, orderType, orderTimeStamp, orderCost, orderPrice, OrderCompleteState));
				
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				conn.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return orders;
	}
	
	
	public static void markOrderAsComplete(Integer orderId) {
		try {
			connect_to_db();
			
			String queryStatement = "update ordertable set OrderCompleteState = 1 where OrderId = " + orderId + " ;";
			PreparedStatement preparedStatement = conn.prepareStatement(queryStatement);
	        preparedStatement.executeUpdate();
			
			String PizzaQueryStatement = "update pizza set PizzaCompleteState = 1 where OrderId = " + orderId + " ;";
		    PreparedStatement pizzaPreparedStatement = conn.prepareStatement(PizzaQueryStatement);
			pizzaPreparedStatement.executeUpdate();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				conn.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	
	public static ArrayList<Order> sortOrders(ArrayList<Order> list)
	{
		/*
		 * This was a function that I used to sort my arraylist based on date.
		 * You may or may not need this function depending on how you fetch
		 * your orders from the DB in the getCurrentOrders function.
		 */
		
		
		
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return null;
		
	}
	
	public static boolean checkDate(int year, int month, int day, String dateOfOrder)
	{
		//Helper function I used to help sort my dates. You likely wont need these
		
		
		
		
		
		
		
		
		return false;
	}
	
	
	/*
	 * The next 3 private functions help get the individual components of a SQL datetime object. 
	 * You're welcome to keep them or remove them.
	 */
	private static int getYear(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(0,4));
	}
	private static int getMonth(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(5, 7));
	}
	private static int getDay(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(8, 10));
	}



	
	
	
	public static double getBaseCustPrice(String size, String crust) throws SQLException, IOException {
		connect_to_db();
		double bp = 0.0;
		// add code to get the base price (for the customer) for that size and crust pizza Depending on how
		// you store size & crust in your database, you may have to do a conversion
		
		
		
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return bp;
	}
	
	public static String getCustomerName(int CustID) throws SQLException, IOException
	{
		/*
		 *This is a helper function I used to fetch the name of a customer
		 *based on a customer ID. It actually gets called in the Order class
		 *so I'll keep the implementation here. You're welcome to change
		 *how the order print statements work so that you don't need this function.
		 */
		connect_to_db();
		String ret = "";
		String query = "Select CustomerFName, CustomerLName From customer WHERE CustomerID=" + CustID + ";";
		Statement stmt = conn.createStatement();
		ResultSet rset = stmt.executeQuery(query);
		
		while(rset.next())
		{
			ret = rset.getString(1) + " " + rset.getString(2);
		}
		conn.close();
		return ret;
	}
	public static String getCustomerAddress(int CustID) throws SQLException, IOException
	{
		/*
		 *This is a helper function I used to fetch the name of a customer
		 *based on a customer ID. It actually gets called in the Order class
		 *so I'll keep the implementation here. You're welcome to change
		 *how the order print statements work so that you don't need this function.
		 */
		connect_to_db();
		String ret = "";
		String query = "Select CustomerAddress From customer WHERE CustomerID=" + CustID + ";";
		Statement stmt = conn.createStatement();
		ResultSet rset = stmt.executeQuery(query);
		
		while(rset.next())
		{
			ret = rset.getString("CustomerAddress");
		}
		conn.close();
		return ret;
	}
	
	public static double getBaseBusPrice(String size, String crust) throws SQLException, IOException {
		connect_to_db();
		double bp = 0.0;
		// add code to get the base cost (for the business) for that size and crust pizza Depending on how
		// you store size and crust in your database, you may have to do a conversion
		
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return bp;
	}

	
	public static ArrayList<Discount> getDiscountList() throws SQLException, IOException {
		ArrayList<Discount> discs = new ArrayList<Discount>();
		connect_to_db();
		try (Statement statement = conn.createStatement();
		        ResultSet resultSet = statement.executeQuery("select * from discount;")) {
			
			while (resultSet.next()) {
				Integer discountId = resultSet.getInt("DiscountID");
				String discountType = resultSet.getString("DiscountType");
				Double DiscountDollarOff = resultSet.getDouble("DiscountDollarOff");
				Boolean IsPercentOff = resultSet.getBoolean("IsPercentOff");
				
				discs.add(new Discount(discountId, discountType, DiscountDollarOff, IsPercentOff));
			
			}
		}
		catch (SQLException e) {
			System.out.println(e);
		}
		return discs;
	}
	
	
	
    public static ArrayList<Customer> getCustomerList() throws SQLException, IOException {
			ArrayList<Customer> customers = new ArrayList<Customer>();
			connect_to_db();
			
			try (Statement statement = conn.createStatement();
			        ResultSet resultSet = statement.executeQuery("select * from customer;")) {
				
				while (resultSet.next()) {
					Integer customerId = resultSet.getInt("CustomerId");
					String customerFName = resultSet.getString("CustomerFName");
					String customerLName = resultSet.getString("CustomerLName");
					String customerPhone = resultSet.getString("CustomerPhNum");                           //format for phNum(##########)
					
					customers.add(new Customer(customerId, customerFName,customerLName, customerPhone));
				
				}
			}
			catch (SQLException e) {
				System.out.println(e);
			}
			return customers;
		}
    
    
    
	//get all the orders from the database	
    public static ArrayList<Order> getOrderList() throws SQLException, IOException {
		ArrayList<Order> orders = new ArrayList<Order>();
		connect_to_db();
		
		try (Statement statement = conn.createStatement();
		        ResultSet resultSet = statement.executeQuery("select * from ordertable;")) {
			
			while (resultSet.next()) {
				Integer orderId = resultSet.getInt("OrderID");
				Integer customerID = resultSet.getInt("CustomerID");
				String OrderType = resultSet.getString("orderType");
				String date=resultSet.getString("OrderTimeStamp");
				Double CustPrice =resultSet.getDouble("OrderPrice");
				Double BusPrice =resultSet.getDouble("OrderCost");
				Integer isComplete =resultSet.getInt("OrderCompleteState");
				

				
				orders.add(new Order(orderId, customerID, OrderType,date,CustPrice,BusPrice,isComplete));
			
			}
		}
		catch (SQLException e) {
			System.out.println(e);
		}
		return orders;
	}
    
    
    
    
    //Complete Details of the selected OrderID
    public static ArrayList<Order> OrderDetail(int orderNum) throws SQLException, IOException {
		ArrayList<Order> orders = new ArrayList<Order>();
		connect_to_db();
		
		try (Statement statement = conn.createStatement();
		        ResultSet resultSet = statement.executeQuery("select OrderID,CustomerID,orderType, OrderPrice, OrderCost, OrderTimeStamp,OrderCompleteState from ordertable where OrderID=" + orderNum + ";")) {
			
			while (resultSet.next()) {
				Integer orderId = resultSet.getInt("OrderID");
				Integer customerID = resultSet.getInt("CustomerID");
				String OrderType = resultSet.getString("orderType");
				String date=resultSet.getString("OrderTimeStamp");
				Double CustPrice =resultSet.getDouble("OrderPrice");
				Double BusPrice =resultSet.getDouble("OrderCost");
				Integer isComplete =resultSet.getInt("OrderCompleteState");
				

				
				orders.add(new Order(orderId, customerID, OrderType,date,CustPrice,BusPrice,isComplete));
			
			}
		}
		catch (SQLException e) {
			System.out.println(e);
		}
		return orders;
	}
	
    
    public static void getBasePizzaDetails(Pizza pizza) {
		//to fetch the base pizza details 
		
		try {
			String pizzaCrust = pizza.getCrustType();
			String pizzaSize = pizza.getSize();
			
			try (Statement statement = conn.createStatement();
			        ResultSet resultSet = statement.executeQuery("select * from pizzabaseprice where PizzaCrustType = '" + pizzaCrust
			                + "' and PizzaSize = '" + pizzaSize + "';")) {
				
				while (resultSet.next()) {
					
					pizza.setCustPrice(resultSet.getDouble("PizzaPrice"));
					pizza.setBusPrice(resultSet.getDouble("PizzaCost"));
					
				}
			}
		}
		catch (Exception e) {
			
		}
		

	}
    
	public static int getNextOrderID() throws SQLException, IOException
	{
		/*
		 * A helper function I had to use because I forgot to make
		 * my OrderID auto increment...You can remove it if you
		 * did not forget to auto increment your orderID.
		 */
		
		
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return -1;
	}
	
	public static void printToppingReport() throws SQLException, IOException
	{
		connect_to_db();
		/*
		 * Prints the ToppingPopularity view. Remember that these views
		 * need to exist in your DB, so be sure you've run your createViews.sql
		 * files on your testing DB if you haven't already.
		 * 
		 * I'm not picky about how they print (other than that it should
		 * be in alphabetical order by name), just make sure it's readable.
		 */
		try {
			connect_to_db();
			
			Statement statement = conn.createStatement();
			String query =  "select  topping.ToppingName as Topping , \n"
					+ "        count(topping.ToppingName) + sum(pizzatopping.ExtraTopping) as  ToppingCount from pizzatopping\n"
					+ "        right join topping  on topping.ToppingID=pizzatopping.ToppingID \n"
					+ "        group by topping.ToppingName \n"
					+ "        order by ToppingCount desc;";
			ResultSet resultSet = statement.executeQuery(query);
			System.out.printf("%-20s | %-4s |%n", "Topping", "ToppingCount");
			while (resultSet.next()) {
				
				Integer count = resultSet.getInt("ToppingCount");
				String topping = resultSet.getString("Topping");
				System.out.printf("%-20s | %-4s |%n", topping, count);
				
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				conn.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void printProfitByPizzaReport() throws SQLException, IOException
	{
		connect_to_db();
		try {
			Statement statement = conn.createStatement();
			String query = " select  pizzabaseprice.PizzaSize as 'Pizza Size', \n"
					+ "        pizzabaseprice.PizzaCrustType as 'Pizza Crust',\n"
					+ "        sum(pizza.Pizzaprice-pizza.PizzaCost) as Profit, \n"
					+ "        DATE_FORMAT(max(pizza.PizzaTimeStamp), '%M %e %Y') as LastOrderDate from pizzabaseprice\n"
					+ "right join pizza  on pizzabaseprice.PizzaSize=pizza.PizzaSize and pizzabaseprice.PizzaCrustType=pizza.PizzaCrustType \n"
					+ "group by pizzabaseprice.PizzaSize,pizzabaseprice.PizzaCrustType \n"
					+ "order by profit desc;";
			ResultSet resultSet = statement.executeQuery(query);
			System.out.printf("%-15s | %-15s | %-10s| %-30s%n", "Pizza Size", "Pizza Crust", "Profit", "Last Order Date");
			while (resultSet.next()) {
				
				String size = resultSet.getString("Pizza Size");
				String crust = resultSet.getString("Pizza Crust");
				String lastOrderDate = resultSet.getString("LastOrderDate");
				Double profit = resultSet.getDouble("Profit");
				System.out.printf("%-15s | %-15s | %-10s| %-30s%n", size, crust, profit, lastOrderDate);
				
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				conn.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void printProfitByOrderType() throws SQLException, IOException
	{
		connect_to_db();
		try {
			connect_to_db();
			
			Statement statement = conn.createStatement();
			String query = "select  OrderType as CustomerType, \n"
					+ "        DATE_FORMAT(OrderTimeStamp,'%Y %M') as OrderDate, \n"
					+ "        OrderPrice as TotalOrderPrice ,\n"
					+ "        OrderCost as TotalOrderCost , \n"
					+ "	    (OrderPrice-OrderCost) as Profit from ordertable \n"
					+ "group by CustomerType,orderDate;";
			ResultSet resultSet = statement.executeQuery(query);
			System.out.printf("%-15s | %-15s | %-18s| %-18s| %-8s%n", "Customer Type", "Order Month", "Total Order Price",
			    "Total Order Cost", "Profit");
			System.out.printf("-----------------------------------------------------------------------------------\n");
			while (resultSet.next()) {
				
				String customerType = resultSet.getString("CustomerType");
				String orderMonth = resultSet.getString("OrderDate");
				Double totalOrderPrice = resultSet.getDouble("TotalOrderPrice");
				Double totalOrderCost = resultSet.getDouble("TotalOrderCost");
				Double profit = resultSet.getDouble("Profit");
				System.out.printf("%-15s | %-15s | %-18s| %-18s| %-8s%n", customerType, orderMonth, totalOrderPrice,
				    totalOrderCost, profit);
				
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				conn.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}	
	}
}

	
	


