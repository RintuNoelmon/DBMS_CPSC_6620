/* SQL Script developed by Rintu Noelmon and Anjana Sriram */
use Pizzeria;
create view ToppingPopularity as
select  topping.ToppingName as Topping , 
        count(topping.ToppingName) + sum(pizzatopping.ExtraTopping) as  ToppingCount from pizzatopping
        right join topping  on topping.ToppingID=pizzatopping.ToppingID 
        group by topping.ToppingName 
        order by ToppingCount desc;


create view ProfitByPizza as
select  pizzabaseprice.PizzaSize as 'Pizza Size', 
        pizzabaseprice.PizzaCrustType as 'Pizza Crust',
        sum(pizza.Pizzaprice-pizza.PizzaCost) as Profit, 
        DATE_FORMAT(max(pizza.PizzaTimeStamp), '%M %e %Y') as LastOrderDate from pizzabaseprice
right join pizza  on pizzabaseprice.PizzaSize=pizza.PizzaSize and pizzabaseprice.PizzaCrustType=pizza.PizzaCrustType 
group by pizzabaseprice.PizzaSize,pizzabaseprice.PizzaCrustType 
order by profit desc;

create view ProfitByOrderType as
select  OrderType as CustomerType, 
        DATE_FORMAT(OrderTimeStamp,'%Y %M') as OrderDate, 
        OrderPrice as TotalOrderPrice ,
        OrderCost as TotalOrderCost , 
	    (OrderPrice-OrderCost) as Profit from ordertable 
group by CustomerType,orderDate;


select * from ToppingPopularity;
select * from ProfitByPizza;
select * from ProfitByOrderType;


