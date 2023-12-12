/*Script developed by Anjana sriram and Alexandria Rintu */

USE pizzeria;
Insert into pizzabaseprice(PizzaSize,PizzaCrustType,PizzaPrice,PizzaCost)
values
('small','thin',3,0.5),
('small','original',3,0.75),
('small','pan',3.5,1),
('small','gluten free',4,2),
('medium','thin',5,1),
('medium','original',5,1.5),
('medium','pan',6,2.25),
('medium','gluten free',6.25,3),
('large','thin',8,1.25),
('large','original',8,2),
('large','pan',9,3),
('large','gluten free',9.5,4),
('x-large','thin',10,2),
('x-large','original',10,3),
('x-large','pan',11.25,4.5),
('x-large','gluten free',12.25,6);

insert into customer(CustomerFName,CustomerLName,CustomerPhNum)
values
('Andrew','Wilkes-Krier','864-254-5861'),
('Matt','Engers','864-474-9953'),
('Frank','Turner','864-232-8944'),
('Milo','Auckerman ','864-878-5679.');


insert into ordertable(CustomerID,OrderForNumOfPizzas,OrderType,OrderPrice,OrderCost,OrderTimeStamp)values
(1,2,'dinein',13.50,3.68,'2022-03-05 12:03:00'),
(2,3,'dinein',10.60,3.23,'2022-04-03 12:05:00'),
(5,2,'pickup',10.75,3.30,'2022-03-03 21:30:00'),
(2,2,'delivery',14.50,5.59,'2022-04-20 19:11:00'),
(5,4,'pickup',16.85,7.85,'2022-03-02 17:30:00'),
(4,5,'delivery',13.25,3.20,'2022-03-02 18:17:00'),
(1,1,'delivery',12,3.75,'2022-04-13 20:32:00');

insert into discount (DiscountType,IsPercentOff,DiscountDollarOff)
values
('Employee',true,0.15),
('Lunch Special Medium',false,1),
('Lunch Special Large',false,2),
('Speciality Pizza	',false,1.5),
('Gameday Special',true,0.20);


insert into dinein (OrderID,DineInTableNum,DineInCount) values
(1,22,0),
(2,12,0),
(3,13,0),
(4,15,0);

insert into pickup (OrderID) values
(1),
(2),
(3);

insert into delivery (OrderID,CustomerAddress) values
(1,'220 Elm street,clemson 29631'),
(2,'102 Calhoun street, clemson 29631'),
(3,'221 Bakers street , clemson 29661');

insert into pizza(PizzaID,OrderID,PizzaSize,PizzaCrustType,PizzaState,PizzaCount,Pizzaprice,PizzaCost,PizzaTimeStamp) values
(1,2,'small','thin','Completed',1,10.5,5,'2022-03-05 12:03:00'),
(2,3,'small','gluten free','Completed',5,1,10,'2022-03-03 21:30:00'),
(3,6,'medium','original','Completed',15,6,15,'2022-03-03 21:30:00'),
(4,5,'medium','pan','Completed',2,25,12,'2022-03-05 12:03:00'),
(5,4,'medium','thin','Completed',5,25,13,'2022-03-03 21:30:00'),
(6,2,'large','gluten free','Completed',1,30,10.2,'2022-03-03 21:30:00'),
(7,4,'large','original','Completed',1,35,15.2,'2022-03-05 12:03:00'),
(8,2,'x-large','pan','Completed',2,30,10,'2022-03-03 21:30:00'),
(9,3,'x-large','thin','Completed',2,30,10,'2022-03-03 21:30:00'),
(10,4,'x-large','gluten free','Completed',6,3,20,'2022-03-05 12:03:00');

insert into pizzadiscount(PizzaID,DiscountID) values
(5,1),
(7,1),
(6,2),
(11,1),
(9,3);

insert into orderdiscount(OrderID,DiscountID) values
(1,1),
(2,2),
(3,3),
(4,1),
(5,2);

insert into pizzatopping(PizzaID,ToppingID,ExtraTopping)
values
(5,13,false),
(5,1,false),
(6,13,false),
(6,1,false),
(7,13,false),
(7,1,false);


insert into topping (ToppingName,ToppingPrice,ToppingCost,ToppingInventory,ToppingSmallUnitsUsed,ToppingMediumUnitUsed,ToppingLargeUnitUsed,ToppingxlargeUnitUsed)
values 
('Pepperoni',1.25,0.2,100,2,2.75,3.5,4.5),
('Sausage',1.25,0.15,100,2.5,3,3.5,4.25),
('Ham',1.5,0.15,78,2,2.5,3.25,4),
('Chicken',1.75,0.25,56,1.5,2,2.25,3),
('Green Pepper',0.5,0.02,79,1,1.5,2,2.5),
('Onion',0.5,0.02,85,1,1.5,2,2.75),
('Roma Tomato',0.75,0.03,86,2,3,3.5,4.5),
('Mushrooms',0.75,0.1,52,1.5,2,2.5,3),
('Black Olives',0.6,0.1,39,0.75,1,1.5,2),
('Pineapple',1,0.25,15,1,1.25,1.75,2),
('Jalapenos',0.5,0.05,64,0.5,0.75,1.25,1.75),
('Banana Peppers',0.5,0.05,36,0.6,1,1.3,1.75),
('Regular Cheese',1.5,0.12,250,2,3.5,5,7),
('Four Cheese Blend',2,0.15,150,2,3.5,5,7),
('Feta Cheese',2,0.18,75,1.75,3,4,5.5),
('Goat Cheese ',2,0.2,54,1.6,2.75,4,5.5),
('Bacon',1.5,0.25,89,1,1.5,2,3);





