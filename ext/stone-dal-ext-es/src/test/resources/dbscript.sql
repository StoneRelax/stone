DROP DATABASE stone_dal_ut;
CREATE DATABASE stone_dal_ut  DEFAULT CHARSET UTF8  COLLATE UTF8_GENERAL_CI;
USE stone_dal_ut;

CREATE TABLE `my_order` (
  `uuid` decimal(18),
  `order_no` varchar(255) DEFAULT NULL,
  `order_desc` varchar(255) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `expire_date` date DEFAULT NULL,
  `disabled` decimal(1,0) DEFAULT NULL,
  `charge_amt` decimal(9,3) DEFAULT NULL,
  PRIMARY KEY (`uuid`)
);

insert into my_order (uuid,order_no,order_desc,create_date,expire_date,charge_amt,disabled) values(1,'M00001','TEST GOODS01',CURDATE(),CURDATE(),100.01,1);
insert into my_order (uuid,order_no,order_desc,create_date,expire_date,charge_amt,disabled) values(2,'M00002','TEST GOODS02',CURDATE(),CURDATE(),100.02,1);
insert into my_order (uuid,order_no,order_desc,create_date,expire_date,charge_amt,disabled) values(3,'M00003','TEST GOODS03',CURDATE(),CURDATE(),100.03,1);
insert into my_order (uuid,order_no,order_desc,create_date,expire_date,charge_amt,disabled) values(4,'M00004','TEST GOODS04',CURDATE(),CURDATE(),100.04,1);
insert into my_order (uuid,order_no,order_desc,create_date,expire_date,charge_amt,disabled) values(5,'M00005','TEST GOODS05',CURDATE(),CURDATE(),100.05,0);


CREATE TABLE `my_order_item` (
  `uuid` decimal(18),
  `item_name` varchar(50) DEFAULT NULL,
  `order_uuid` decimal(18),
  PRIMARY KEY (`uuid`)
);

insert into my_order_item (uuid,item_name,order_uuid) values(1,'ORDER1',1);
insert into my_order_item (uuid,item_name,order_uuid) values(2,'ORDER2',1);
insert into my_order_item (uuid,item_name,order_uuid) values(3,'ORDER3',1);

delete from person;

CREATE TABLE `person` (
  `uuid` decimal(18),
  `name` varchar(50) DEFAULT NULL,
  `descriptionUuid` varchar(64) DEFAULT NULL,
  `created_date` DATE DEFAULT NULL,
  `last_update_date` DATE DEFAULT NULL,
  PRIMARY KEY (`uuid`)
);

CREATE TABLE `bank_transaction` (
  `uuid` decimal(18),
  `user_name` varchar(50) DEFAULT NULL,
  `type` int DEFAULT NULL,
  `amount` int DEFAULT NULL,
  `score` int DEFAULT NULL,
  PRIMARY KEY (`uuid`)
);

CREATE TABLE `person_order` (
  `person_uuid` decimal(18),
  `order_uuid` decimal(18)
);

CREATE TABLE `goods` (
  `uuid` decimal(18),
  `name` varchar(50) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `label_id` decimal(18) DEFAULT NULL,
  PRIMARY KEY (`uuid`)
);

insert into goods (uuid,name) values(-1,'GOODS_1');