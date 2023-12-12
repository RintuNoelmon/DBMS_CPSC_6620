package cpsc4620;

public class Topping 
{
	/*
	 * 
	 * Standard Java object class. 
	 *  
	 * This file can be modified to match your design, or you can keep it as-is.
	 *
	 * 
	 * */
	
	private int ToppingID;
	private String ToppingName;
	private int ToppingInventory;
	private double PerAMT;
	private double MedAMT;
	private double LgAMT;
	private double XLAMT;
	private double CustPrice;
	private double BusPrice;
	private int MinINVT;
	private int CurINVT;
	private int PizzaSize;
	private int PizzaID;
	private String pizzaToppingSize;
	
	public Topping(int topID, String topName, double perAMT, double medAMT, double lgAMT, double xLAMT,
			double custPrice, double busPrice, int minINVT, int curINVT) {
		ToppingID = topID;
		ToppingName = topName;
		PerAMT = perAMT;
		MedAMT = medAMT;
		LgAMT = lgAMT;
		XLAMT = xLAMT;
		CustPrice = custPrice;
		BusPrice = busPrice;
		MinINVT = minINVT;
		CurINVT = curINVT;
	}

	public Topping(Integer toppingId, int Size, int pizzaID) {
		ToppingID = toppingId;
		setPizzaSize(Size);
		PizzaID = pizzaID;
		
	}
	public Topping(Integer toppingId, String Toppingname, int toppingInventory) {
		ToppingID = toppingId;
		ToppingName = Toppingname;
		ToppingInventory = toppingInventory;
		
	}
	public int getTopID() {
		return ToppingID;
	}
	public int getPizzaID() {
		return PizzaID;
	}
	public String getTopName() {
		return ToppingName;
	}
	
	public int getTopInv() {
		return ToppingInventory;
	}

	public double getPerAMT() {
		return PerAMT;
	}

	public double getMedAMT() {
		return MedAMT;
	}

	public double getLgAMT() {
		return LgAMT;
	}

	public double getXLAMT() {
		return XLAMT;
	}

	public double getCustPrice() {
		return CustPrice;
	}

	public double getBusPrice() {
		return BusPrice;
	}

	public int getMinINVT() {
		return MinINVT;
	}

	public int getCurINVT() {
		return CurINVT;
	}

	public void setTopID(int topID) {
		ToppingID = topID;
	}

	public void setTopName(String topName) {
		ToppingName = topName;
	}
	public void setTopInv(int topinv) {
		ToppingInventory = topinv;
	}

	public void setPerAMT(double perAMT) {
		PerAMT = perAMT;
	}

	public void setMedAMT(double medAMT) {
		MedAMT = medAMT;
	}

	public void setLgAMT(double lgAMT) {
		LgAMT = lgAMT;
	}

	public void setXLAMT(double xLAMT) {
		XLAMT = xLAMT;
	}

	public void setCustPrice(double custPrice) {
		CustPrice = custPrice;
	}

	public void setBusPrice(double busPrice) {
		BusPrice = busPrice;
	}

	public void setMinINVT(int minINVT) {
		MinINVT = minINVT;
	}

	public void setCurINVT(int curINVT) {
		CurINVT = curINVT;
	}
	
	public String getPizzaToppingSize() {
		return pizzaToppingSize;
	}
	
	public void setPizzaToppingSize(String pizzaToppingSize) {
		this.pizzaToppingSize = pizzaToppingSize;
	}

	@Override
	public String toString() {
		return "Topping [TopID=" + ToppingID + ", TopName=" + ToppingName + ", PerAMT=" + PerAMT + ", MedAMT=" + MedAMT
				+ ", LgAMT=" + LgAMT + ", XLAMT=" + XLAMT + ", CustPrice=" + CustPrice + ", BusPrice=" + BusPrice
				+ ", MinINVT=" + MinINVT + ", CurINVT=" + CurINVT + "]";
	}
	
	public String toInventString() {
		return "TopID=" + ToppingID + ", TopName=" + ToppingName + ", TopInv=" + ToppingInventory + " ";
	
		
	}

	public String getToppingSizeFromPizzaSize(Integer pizzaSize) {
		  
		String pizzaToppingSize = null;
		switch(pizzaSize)
		{
		
			case 1 :
				pizzaToppingSize =  "ToppingSmallUnitsUsed";
				break;
			case 2:
				pizzaToppingSize=  "ToppingMediumUnitUsed";
				break;
			case 3:
				pizzaToppingSize = "ToppingLargeUnitUsed";
				break;
			case 4:
				pizzaToppingSize = "ToppingxlargeUnitUsed";
				break;
		}
		return pizzaToppingSize;
	}

	public int getPizzaSize() {
		return PizzaSize;
	}

	public void setPizzaSize(int pizzaSize) {
		PizzaSize = pizzaSize;
	}

}
