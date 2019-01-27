DROP DATABASE stone_dal_ut;
CREATE DATABASE stone_dal_ut  DEFAULT CHARSET UTF8  COLLATE UTF8_GENERAL_CI;
USE stone_dal_ut;

CREATE TABLE `my_order` (
  `uuid` decimal(18),
  `order_no` varchar(255) DEFAULT NULL,
  `order_desc` varchar(255) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `disabled` decimal(1,0) DEFAULT NULL,
  PRIMARY KEY (`uuid`)
);