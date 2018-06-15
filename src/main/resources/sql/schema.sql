/*
SQLyog Ultimate v12.09 (64 bit)
MySQL - 5.7.20-log : Database - crondb
*********************************************************************
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`crondb` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `crondb`;

/*Table structure for table `tbl_task` */

DROP TABLE IF EXISTS `tbl_task`;

CREATE TABLE `tbl_task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务id，唯一标识。主键',
  `name` varchar(32) NOT NULL COMMENT '任务名称，简单表达该任务特征',
  `description` varchar(255) DEFAULT NULL COMMENT '任务描述，可详细描述任务',
  `cron_expression` varchar(255) NOT NULL COMMENT 'cron定时表达式',
  `url` varchar(255) NOT NULL COMMENT '调用接口url地址',
  `request_type` int(11) NOT NULL COMMENT '请求类型，0表示get，1表示post',
  `request_data` varchar(512) DEFAULT NULL COMMENT '请求参数，json格式字符串，仅当请求类型为post时将通过http body以json格式传递该参数',
  `is_enabled` int(11) NOT NULL COMMENT '是否启用',
  `create_time` bigint(20) NOT NULL COMMENT '创建时间',
  `modified_time` bigint(20) NOT NULL COMMENT '上次修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
