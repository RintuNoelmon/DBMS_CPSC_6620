/* SQL Script developed by Rintu Noelmon and Anjana Sriram */


USE Pizzeria;

CREATE TABLE `customer` (
  `CustomerID` int NOT NULL AUTO_INCREMENT,
  `CustomerFName` varchar(15) NOT NULL,
  `CustomerLName` varchar(15) NOT NULL,
  `CustomerPhNum` varchar(15) NOT NULL,
  PRIMARY KEY (`CustomerID`),
  UNIQUE KEY `Customer_ID_UNIQUE` (`CustomerID`)
) ;
CREATE TABLE `ordertable` (
  `OrderID` int NOT NULL AUTO_INCREMENT,
  `CustomerID` int DEFAULT NULL,
  `OrderType` varchar(10) NOT NULL,
  `OrderTimeStamp` datetime NOT NULL,
  `OrderCompleteState` tinyint(1) NOT NULL,
  `OrderCost` decimal(8,2) NOT NULL,
  `OrderPrice` decimal(8,2) NOT NULL,
  PRIMARY KEY (`OrderId`),
  KEY `CustomerId` (`CustomerId`),
  CONSTRAINT `ordertable_ibfk_1` FOREIGN KEY (`CustomerId`) REFERENCES `customer` (`CustomerId`),
  CONSTRAINT `ordertable_chk_1` CHECK (((`ordertype` = _utf8mb4'dinein') or (`ordertype` = _utf8mb4'delivery') or (`ordertype` = _utf8mb4'pickup')))
);


CREATE TABLE `pizzabaseprice` (
  `PizzaSize` varchar(15) NOT NULL,
  `PizzaCrustType` varchar(15) NOT NULL,
  `PizzaPrice` decimal(4,2) NOT NULL,
  `PizzaCost` decimal(4,2) NOT NULL,
  PRIMARY KEY (`PizzaSize`,`PizzaCrustType`)
);

CREATE TABLE `topping` (
  `ToppingID` int NOT NULL AUTO_INCREMENT,
  `ToppingName` varchar(45) NOT NULL,
  `ToppingPrice` decimal(4,2) NOT NULL,
  `ToppingCost` decimal(4,2) NOT NULL,
  `ToppingInventory` int NOT NULL,
  `ToppingSmallUnitsUsed` decimal(4,2) NOT NULL,
  `ToppingMediumUnitUsed` decimal(4,2) NOT NULL,
  `ToppingLargeUnitUsed` decimal(4,2) NOT NULL,
  `ToppingxlargeUnitUsed` decimal(4,2) NOT NULL,
  PRIMARY KEY (`ToppingID`),
  UNIQUE KEY `ToppingsID_UNIQUE` (`ToppingID`)
) ;


CREATE TABLE `pizza` (
  `PizzaID` int NOT NULL AUTO_INCREMENT ,
  `OrderID` int NOT NULL,
  `PizzaSize` varchar(45) NOT NULL,
  `PizzaCompleteStateProfitByPizzaProfitByPizza` tinyint(1) NOT NULL,
  `PizzaTimeStamp` datetime DEFAULT NULL,
  `PizzaCost` decimal(4,2) NOT NULL,
  `PizzaPrice` decimal(4,2) NOT NULL,
  `PizzaCrustType` varchar(45) NOT NULL,
  PRIMARY KEY (`PizzaID`),
   KEY `OrderID` (`OrderID`),
  KEY `PizzaSize` (`PizzaSize`,`PizzaCrust`),
  FOREIGN KEY (`PizzaSize`,PizzaCrustType) REFERENCES `pizzabaseprice` (`PizzaSize`,PizzaCrustType),
  CONSTRAINT `FK_OrderID_PI` FOREIGN KEY (`OrderID`) REFERENCES `ordertable` (`OrderID`)
) ;


CREATE TABLE `discount` (
  `DiscountID` int NOT NULL AUTO_INCREMENT,
  `DiscountType` varchar(20) NOT NULL,
  `IsPercentOff` tinyint(1) DEFAULT NULL,
  `DiscountDollarOff` decimal(4,2) DEFAULT NULL,
  PRIMARY KEY (`DiscountID`)
) ;

CREATE TABLE `dinein` (
  `OrderID` int NOT NULL,
  `DineInTableNum` int NOT NULL,
  `DineInCount` int NOT NULL,
  PRIMARY KEY (`OrderID`),
  CONSTRAINT `FK_OrderID_dinein` FOREIGN KEY (`OrderId`) REFERENCES `ordertable` (`OrderId`)
);

CREATE TABLE `pickup` (
  `OrderID` int NOT NULL,
  PRIMARY KEY (`OrderID`),
  CONSTRAINT `FK_OrderID_pickup` FOREIGN KEY (`OrderId`) REFERENCES `ordertable` (`OrderId`)
  
);

CREATE TABLE `delivery` (
  `OrderID` int NOT NULL,
  `CustomerAddress` varchar(45) NOT NULL,
  PRIMARY KEY (`OrderID`),
  CONSTRAINT `FK_OrderID_Delivery` FOREIGN KEY (`OrderId`) REFERENCES `ordertable` (`OrderId`)
) ;

CREATE TABLE `pizzadiscount` (
  `PizzaID` int NOT NULL,
  `DiscountID` int NOT NULL,
  PRIMARY KEY (`PizzaID`,`DiscountID`),
  KEY `DiscountID` (`DiscountID`),
  FOREIGN KEY (`PizzaID`) REFERENCES `pizza` (`PizzaID`),
  FOREIGN KEY (`DiscountID`) REFERENCES `discount` (`DiscountID`)
);

CREATE TABLE `orderdiscount` (
  `OrderID` int NOT NULL,
  `DiscountID` int NOT NULL,
  PRIMARY KEY (`OrderID`,`DiscountID`),
  CONSTRAINT `FK_OrderID_OD` FOREIGN KEY (`OrderID`) REFERENCES `ordertable` (`OrderID`),
  CONSTRAINT `FK_DiscountID_OD` FOREIGN KEY (`DiscountID`) REFERENCES `discount` (`DiscountID`)
) ;
CREATE TABLE `pizzatopping` (
  `PizzaID` int NOT NULL,
  `ToppingID` int NOT NULL,
  `ExtraTopping` int NOT NULL,
  PRIMARY KEY (`PizzaID`,`ToppingID`),
  CONSTRAINT `FK_PizzaID_PT` FOREIGN KEY (`PizzaID`) REFERENCES `pizza` (`PizzaID`),
  CONSTRAINT `FK_ToppingID_PT` FOREIGN KEY (`ToppingID`) REFERENCES `topping` (`ToppingID`)
) ;
