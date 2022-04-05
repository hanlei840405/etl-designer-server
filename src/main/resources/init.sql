/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80025
 Source Host           : 127.0.0.1:3306
 Source Schema         : nxin_etl

 Target Server Type    : MySQL
 Target Server Version : 80025
 File Encoding         : 65001

 Date: 03/04/2022 16:32:46
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for analysis_layout
-- ----------------------------
DROP TABLE IF EXISTS `analysis_layout`;
CREATE TABLE `analysis_layout` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `creator` varchar(255) DEFAULT NULL,
  `modifier` varchar(255) DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `version` int NOT NULL,
  `arrange` text,
  `code` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `resource_code` varchar(255) DEFAULT '' COMMENT '资源码',
  `tenant_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKmvv5byrkydhbjvr6awmg6w4wr` (`tenant_id`),
  CONSTRAINT `FKmvv5byrkydhbjvr6awmg6w4wr` FOREIGN KEY (`tenant_id`) REFERENCES `basic_tenant` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for analysis_metadata
-- ----------------------------
DROP TABLE IF EXISTS `analysis_metadata`;
CREATE TABLE `analysis_metadata` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `auto_increment` bit(1) DEFAULT NULL,
  `column_category` varchar(255) DEFAULT NULL,
  `column_code` varchar(255) DEFAULT NULL,
  `column_decimal` int DEFAULT NULL,
  `column_length` int DEFAULT NULL,
  `column_name` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `creator` varchar(255) DEFAULT NULL,
  `modifier` varchar(255) DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `not_null` bit(1) DEFAULT NULL,
  `primary_key` bit(1) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `version` int NOT NULL,
  `model_id` bigint DEFAULT NULL,
  `tenant_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK18m086gwjn7i8v4bs7ea01boy` (`model_id`),
  KEY `FK8yvnjys7du19ghnm835tt14or` (`tenant_id`),
  CONSTRAINT `FK18m086gwjn7i8v4bs7ea01boy` FOREIGN KEY (`model_id`) REFERENCES `analysis_model` (`id`),
  CONSTRAINT `FK8yvnjys7du19ghnm835tt14or` FOREIGN KEY (`tenant_id`) REFERENCES `basic_tenant` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for analysis_model
-- ----------------------------
DROP TABLE IF EXISTS `analysis_model`;
CREATE TABLE `analysis_model` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `creator` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `modifier` varchar(255) DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `version` int NOT NULL,
  `datasource_id` bigint DEFAULT NULL,
  `project_id` bigint DEFAULT NULL,
  `tenant_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKctggj19n5x0lb3tf2tah6jw4o` (`datasource_id`),
  KEY `FK5cu5he9033yn2wpncxi8d7fje` (`project_id`),
  KEY `FK7vyee9pb19g1khgwp1d34h47g` (`tenant_id`),
  CONSTRAINT `FK5cu5he9033yn2wpncxi8d7fje` FOREIGN KEY (`project_id`) REFERENCES `basic_project` (`id`),
  CONSTRAINT `FK7vyee9pb19g1khgwp1d34h47g` FOREIGN KEY (`tenant_id`) REFERENCES `basic_tenant` (`id`),
  CONSTRAINT `FKctggj19n5x0lb3tf2tah6jw4o` FOREIGN KEY (`datasource_id`) REFERENCES `designer_datasource` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for analysis_report
-- ----------------------------
DROP TABLE IF EXISTS `analysis_report`;
CREATE TABLE `analysis_report` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `creator` varchar(255) DEFAULT NULL,
  `modifier` varchar(255) DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `version` int NOT NULL,
  `code` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `script` text,
  `model_id` bigint DEFAULT NULL,
  `tenant_id` bigint DEFAULT NULL,
  `chart` varchar(255) DEFAULT NULL,
  `project_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKefmiorw0id2j9gi2ka5gf2k8n` (`model_id`),
  KEY `FKqqgj9beblwcr4kc8llpailobp` (`tenant_id`),
  KEY `FK1ht0ene93w6lforb2xkhs6dex` (`project_id`),
  CONSTRAINT `FK1ht0ene93w6lforb2xkhs6dex` FOREIGN KEY (`project_id`) REFERENCES `basic_project` (`id`),
  CONSTRAINT `FKefmiorw0id2j9gi2ka5gf2k8n` FOREIGN KEY (`model_id`) REFERENCES `analysis_model` (`id`),
  CONSTRAINT `FKqqgj9beblwcr4kc8llpailobp` FOREIGN KEY (`tenant_id`) REFERENCES `basic_tenant` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for analysis_report_dimension
-- ----------------------------
DROP TABLE IF EXISTS `analysis_report_dimension`;
CREATE TABLE `analysis_report_dimension` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `creator` varchar(255) DEFAULT NULL,
  `modifier` varchar(255) DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `version` int NOT NULL,
  `code` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `report_id` bigint DEFAULT NULL,
  `tenant_id` bigint DEFAULT NULL,
  `category` varchar(255) DEFAULT NULL,
  `unit` varchar(255) DEFAULT NULL,
  `graph` varchar(255) DEFAULT NULL,
  `anchor` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK13u13is4hee43k75kkj260tpc` (`report_id`),
  KEY `FKcewhh3d135y8799wge687at5g` (`tenant_id`),
  CONSTRAINT `FK13u13is4hee43k75kkj260tpc` FOREIGN KEY (`report_id`) REFERENCES `analysis_report` (`id`),
  CONSTRAINT `FKcewhh3d135y8799wge687at5g` FOREIGN KEY (`tenant_id`) REFERENCES `basic_tenant` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for auth_privilege
-- ----------------------------
DROP TABLE IF EXISTS `auth_privilege`;
CREATE TABLE `auth_privilege` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `last_update_time` datetime DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `version` int NOT NULL,
  `resource_id` bigint DEFAULT NULL,
  `tenant_id` bigint DEFAULT NULL,
  `creator` varchar(255) DEFAULT NULL,
  `modifier` varchar(255) DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKmq5sqnrnldwwuy9gsq4a4mb4f` (`resource_id`),
  KEY `FK20ta2ysxguhvo2a7fa5ywqngd` (`tenant_id`),
  CONSTRAINT `FK20ta2ysxguhvo2a7fa5ywqngd` FOREIGN KEY (`tenant_id`) REFERENCES `basic_tenant` (`id`),
  CONSTRAINT `FKmq5sqnrnldwwuy9gsq4a4mb4f` FOREIGN KEY (`resource_id`) REFERENCES `auth_resource` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for auth_resource
-- ----------------------------
DROP TABLE IF EXISTS `auth_resource`;
CREATE TABLE `auth_resource` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `version` int NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `creator` varchar(255) DEFAULT NULL,
  `modifier` varchar(255) DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `tenant_id` bigint DEFAULT NULL,
  `category` varchar(255) DEFAULT NULL,
  `level` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKel7r3hqqtwd8iwad3cri530oi` (`tenant_id`),
  CONSTRAINT `FKel7r3hqqtwd8iwad3cri530oi` FOREIGN KEY (`tenant_id`) REFERENCES `basic_tenant` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for auth_user_privilege
-- ----------------------------
DROP TABLE IF EXISTS `auth_user_privilege`;
CREATE TABLE `auth_user_privilege` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `creator` varchar(255) DEFAULT NULL,
  `last_update_time` datetime DEFAULT NULL,
  `modifier` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `version` int NOT NULL,
  `privilege_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `tenant_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKcsc7ffvwgppj3ihkqehplkmmb` (`privilege_id`),
  KEY `FKbt6mc2fg1gqk2pv7sllpimjjf` (`user_id`),
  KEY `FK1wrmso6cwptosh1u6gqhgakd3` (`tenant_id`),
  CONSTRAINT `FK1wrmso6cwptosh1u6gqhgakd3` FOREIGN KEY (`tenant_id`) REFERENCES `basic_tenant` (`id`),
  CONSTRAINT `FKbt6mc2fg1gqk2pv7sllpimjjf` FOREIGN KEY (`user_id`) REFERENCES `basic_user` (`id`),
  CONSTRAINT `FKcsc7ffvwgppj3ihkqehplkmmb` FOREIGN KEY (`privilege_id`) REFERENCES `auth_privilege` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for basic_project
-- ----------------------------
DROP TABLE IF EXISTS `basic_project`;
CREATE TABLE `basic_project` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `creator` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `last_update_time` datetime DEFAULT NULL,
  `modifier` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `scope` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `version` int NOT NULL,
  `modify_time` datetime DEFAULT NULL,
  `tenant_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKoj2mbu3lm98ryg0bt21f2d08e` (`tenant_id`),
  CONSTRAINT `FKoj2mbu3lm98ryg0bt21f2d08e` FOREIGN KEY (`tenant_id`) REFERENCES `basic_tenant` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for basic_project_user
-- ----------------------------
DROP TABLE IF EXISTS `basic_project_user`;
CREATE TABLE `basic_project_user` (
  `projects_id` bigint NOT NULL,
  `users_id` bigint NOT NULL,
  KEY `FKp1nwtcervwk8k6g6wxucr13op` (`users_id`),
  KEY `FKn7397fc0pdv388g0ohrtd42f1` (`projects_id`),
  CONSTRAINT `FKn7397fc0pdv388g0ohrtd42f1` FOREIGN KEY (`projects_id`) REFERENCES `basic_project` (`id`),
  CONSTRAINT `FKp1nwtcervwk8k6g6wxucr13op` FOREIGN KEY (`users_id`) REFERENCES `basic_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for basic_tenant
-- ----------------------------
DROP TABLE IF EXISTS `basic_tenant`;
CREATE TABLE `basic_tenant` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `domain` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `last_update_time` datetime DEFAULT NULL,
  `logo` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `probation_end_date` datetime DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `telephone` varchar(255) DEFAULT NULL,
  `version` int NOT NULL,
  `creator` varchar(255) DEFAULT NULL,
  `modifier` varchar(255) DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for basic_user
-- ----------------------------
DROP TABLE IF EXISTS `basic_user`;
CREATE TABLE `basic_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `birth_date` datetime DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `creator` varchar(255) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `gender` varchar(255) DEFAULT NULL,
  `modift_time` datetime DEFAULT NULL,
  `master` bit(1) DEFAULT NULL,
  `mobile` varchar(255) DEFAULT NULL,
  `modifier` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `version` int NOT NULL,
  `wechat` varchar(255) DEFAULT NULL,
  `tenant_id` bigint DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email_UNIQUE` (`email`),
  KEY `FKrrjaqdpn64fk7whmge6rd4ism` (`tenant_id`),
  CONSTRAINT `FKrrjaqdpn64fk7whmge6rd4ism` FOREIGN KEY (`tenant_id`) REFERENCES `basic_tenant` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for designer_attachment
-- ----------------------------
DROP TABLE IF EXISTS `designer_attachment`;
CREATE TABLE `designer_attachment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `creator` varchar(255) DEFAULT NULL,
  `modifier` varchar(255) DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `path` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `step_name` varchar(255) DEFAULT NULL,
  `version` int NOT NULL,
  `shell_id` bigint DEFAULT NULL,
  `tenant_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK55pwo30spuoko0q1lbsdf49cf` (`shell_id`),
  KEY `FK6l41av9pfy35856qi4bbdesyg` (`tenant_id`),
  CONSTRAINT `FK55pwo30spuoko0q1lbsdf49cf` FOREIGN KEY (`shell_id`) REFERENCES `designer_shell` (`id`),
  CONSTRAINT `FK6l41av9pfy35856qi4bbdesyg` FOREIGN KEY (`tenant_id`) REFERENCES `basic_tenant` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for designer_datasource
-- ----------------------------
DROP TABLE IF EXISTS `designer_datasource`;
CREATE TABLE `designer_datasource` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `category` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `creator` varchar(255) DEFAULT NULL,
  `data_space` varchar(255) DEFAULT NULL,
  `host` varchar(255) DEFAULT NULL,
  `index_space` varchar(255) DEFAULT NULL,
  `modifier` varchar(255) DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `parameter` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `pool_initial` int DEFAULT NULL,
  `pool_initial_size` int DEFAULT NULL,
  `pool_max_active` int DEFAULT NULL,
  `pool_max_idle` int DEFAULT NULL,
  `pool_max_size` int DEFAULT NULL,
  `pool_max_wait` int DEFAULT NULL,
  `pool_min_idle` int DEFAULT NULL,
  `port` int DEFAULT NULL,
  `schema_name` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `use_cursor` bit(1) DEFAULT NULL,
  `use_pool` bit(1) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `version` int NOT NULL,
  `project_id` bigint DEFAULT NULL,
  `tenant_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKa8n528lbw5237hvlcv6b4ar4h` (`project_id`),
  KEY `FK5t48c1i6fk46wgpc5ypy0wrm7` (`tenant_id`),
  CONSTRAINT `FK5t48c1i6fk46wgpc5ypy0wrm7` FOREIGN KEY (`tenant_id`) REFERENCES `basic_tenant` (`id`),
  CONSTRAINT `FKa8n528lbw5237hvlcv6b4ar4h` FOREIGN KEY (`project_id`) REFERENCES `basic_project` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for designer_shell
-- ----------------------------
DROP TABLE IF EXISTS `designer_shell`;
CREATE TABLE `designer_shell` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `category` varchar(255) DEFAULT NULL,
  `content` text,
  `create_time` datetime DEFAULT NULL,
  `creator` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `executable` bit(1) DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `modifier` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `reference` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `streaming` varchar(255) DEFAULT NULL,
  `version` int NOT NULL,
  `xml` varchar(255) DEFAULT NULL,
  `project_id` bigint DEFAULT NULL,
  `parent_id` bigint DEFAULT NULL,
  `tenant_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKmxgnnii25dklrqruttvqcwtxj` (`project_id`),
  KEY `FKc6mmcnulhp327b69mqdajx4o6` (`parent_id`),
  KEY `FK91a9m9og267ac8xm1bgt7x1fh` (`tenant_id`),
  CONSTRAINT `FK91a9m9og267ac8xm1bgt7x1fh` FOREIGN KEY (`tenant_id`) REFERENCES `basic_tenant` (`id`),
  CONSTRAINT `FKc6mmcnulhp327b69mqdajx4o6` FOREIGN KEY (`parent_id`) REFERENCES `designer_shell` (`id`),
  CONSTRAINT `FKmxgnnii25dklrqruttvqcwtxj` FOREIGN KEY (`project_id`) REFERENCES `basic_project` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for designer_shell_publish
-- ----------------------------
DROP TABLE IF EXISTS `designer_shell_publish`;
CREATE TABLE `designer_shell_publish` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `business_id` varchar(255) DEFAULT NULL,
  `content` text,
  `create_time` datetime DEFAULT NULL,
  `creator` varchar(255) DEFAULT NULL,
  `deploy_time` datetime DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `prod` varchar(255) DEFAULT NULL,
  `prod_path` varchar(255) DEFAULT NULL,
  `reference` varchar(255) DEFAULT NULL,
  `streaming` varchar(255) DEFAULT NULL,
  `task_id` varchar(255) DEFAULT NULL,
  `xml` varchar(255) DEFAULT NULL,
  `shell_id` bigint DEFAULT NULL,
  `modifier` varchar(255) DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `version` int NOT NULL,
  `tenant_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKhmxjbkb0l3gf34rrqxitvjaar` (`shell_id`),
  KEY `FK8b3sj9se6iyhl90ulacc8826e` (`tenant_id`),
  CONSTRAINT `FK8b3sj9se6iyhl90ulacc8826e` FOREIGN KEY (`tenant_id`) REFERENCES `basic_tenant` (`id`),
  CONSTRAINT `FKhmxjbkb0l3gf34rrqxitvjaar` FOREIGN KEY (`shell_id`) REFERENCES `designer_shell` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for history_log_etl_job
-- ----------------------------
DROP TABLE IF EXISTS `history_log_etl_job`;
CREATE TABLE `history_log_etl_job` (
  `ID_JOB` int DEFAULT NULL,
  `CHANNEL_ID` varchar(255) DEFAULT NULL,
  `JOBNAME` varchar(255) DEFAULT NULL,
  `STATUS` varchar(15) DEFAULT NULL,
  `LINES_READ` bigint DEFAULT NULL,
  `LINES_WRITTEN` bigint DEFAULT NULL,
  `LINES_UPDATED` bigint DEFAULT NULL,
  `LINES_INPUT` bigint DEFAULT NULL,
  `LINES_OUTPUT` bigint DEFAULT NULL,
  `LINES_REJECTED` bigint DEFAULT NULL,
  `ERRORS` bigint DEFAULT NULL,
  `STARTDATE` datetime DEFAULT NULL,
  `ENDDATE` datetime DEFAULT NULL,
  `LOGDATE` datetime DEFAULT NULL,
  `DEPDATE` datetime DEFAULT NULL,
  `REPLAYDATE` datetime DEFAULT NULL,
  `LOG_FIELD` longtext,
  KEY `IDX_log_etl_job_1` (`ID_JOB`),
  KEY `IDX_log_etl_job_2` (`ERRORS`,`STATUS`,`JOBNAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for history_log_etl_job_entry
-- ----------------------------
DROP TABLE IF EXISTS `history_log_etl_job_entry`;
CREATE TABLE `history_log_etl_job_entry` (
  `ID_BATCH` int DEFAULT NULL,
  `CHANNEL_ID` varchar(255) DEFAULT NULL,
  `LOG_DATE` datetime DEFAULT NULL,
  `TRANSNAME` varchar(255) DEFAULT NULL,
  `STEPNAME` varchar(255) DEFAULT NULL,
  `LINES_READ` bigint DEFAULT NULL,
  `LINES_WRITTEN` bigint DEFAULT NULL,
  `LINES_UPDATED` bigint DEFAULT NULL,
  `LINES_INPUT` bigint DEFAULT NULL,
  `LINES_OUTPUT` bigint DEFAULT NULL,
  `LINES_REJECTED` bigint DEFAULT NULL,
  `ERRORS` bigint DEFAULT NULL,
  `RESULT` varchar(5) DEFAULT NULL,
  `NR_RESULT_ROWS` bigint DEFAULT NULL,
  `NR_RESULT_FILES` bigint DEFAULT NULL,
  KEY `IDX_log_etl_job_entry_1` (`ID_BATCH`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for history_log_etl_transform
-- ----------------------------
DROP TABLE IF EXISTS `history_log_etl_transform`;
CREATE TABLE `history_log_etl_transform` (
  `ID_BATCH` int DEFAULT NULL,
  `CHANNEL_ID` varchar(255) DEFAULT NULL,
  `TRANSNAME` varchar(255) DEFAULT NULL,
  `STATUS` varchar(15) DEFAULT NULL,
  `LINES_READ` bigint DEFAULT NULL,
  `LINES_WRITTEN` bigint DEFAULT NULL,
  `LINES_UPDATED` bigint DEFAULT NULL,
  `LINES_INPUT` bigint DEFAULT NULL,
  `LINES_OUTPUT` bigint DEFAULT NULL,
  `LINES_REJECTED` bigint DEFAULT NULL,
  `ERRORS` bigint DEFAULT NULL,
  `STARTDATE` datetime DEFAULT NULL,
  `ENDDATE` datetime DEFAULT NULL,
  `LOGDATE` datetime DEFAULT NULL,
  `DEPDATE` datetime DEFAULT NULL,
  `REPLAYDATE` datetime DEFAULT NULL,
  `LOG_FIELD` longtext,
  KEY `IDX_log_etl_transform_1` (`ID_BATCH`),
  KEY `IDX_log_etl_transform_2` (`ERRORS`,`STATUS`,`TRANSNAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for history_log_etl_transform_channel
-- ----------------------------
DROP TABLE IF EXISTS `history_log_etl_transform_channel`;
CREATE TABLE `history_log_etl_transform_channel` (
  `ID_BATCH` int DEFAULT NULL,
  `CHANNEL_ID` varchar(255) DEFAULT NULL,
  `LOG_DATE` datetime DEFAULT NULL,
  `LOGGING_OBJECT_TYPE` varchar(255) DEFAULT NULL,
  `OBJECT_NAME` varchar(255) DEFAULT NULL,
  `OBJECT_COPY` varchar(255) DEFAULT NULL,
  `REPOSITORY_DIRECTORY` varchar(255) DEFAULT NULL,
  `FILENAME` varchar(255) DEFAULT NULL,
  `OBJECT_ID` varchar(255) DEFAULT NULL,
  `OBJECT_REVISION` varchar(255) DEFAULT NULL,
  `PARENT_CHANNEL_ID` varchar(255) DEFAULT NULL,
  `ROOT_CHANNEL_ID` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for history_log_etl_transform_step
-- ----------------------------
DROP TABLE IF EXISTS `history_log_etl_transform_step`;
CREATE TABLE `history_log_etl_transform_step` (
  `ID_BATCH` int DEFAULT NULL,
  `CHANNEL_ID` varchar(255) DEFAULT NULL,
  `LOG_DATE` datetime DEFAULT NULL,
  `TRANSNAME` varchar(255) DEFAULT NULL,
  `STEPNAME` varchar(255) DEFAULT NULL,
  `STEP_COPY` int DEFAULT NULL,
  `LINES_READ` bigint DEFAULT NULL,
  `LINES_WRITTEN` bigint DEFAULT NULL,
  `LINES_UPDATED` bigint DEFAULT NULL,
  `LINES_INPUT` bigint DEFAULT NULL,
  `LINES_OUTPUT` bigint DEFAULT NULL,
  `LINES_REJECTED` bigint DEFAULT NULL,
  `ERRORS` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for log_etl_job
-- ----------------------------
DROP TABLE IF EXISTS `log_etl_job`;
CREATE TABLE `log_etl_job` (
  `ID_JOB` int DEFAULT NULL,
  `CHANNEL_ID` varchar(255) DEFAULT NULL,
  `JOBNAME` varchar(255) DEFAULT NULL,
  `STATUS` varchar(15) DEFAULT NULL,
  `LINES_READ` bigint DEFAULT NULL,
  `LINES_WRITTEN` bigint DEFAULT NULL,
  `LINES_UPDATED` bigint DEFAULT NULL,
  `LINES_INPUT` bigint DEFAULT NULL,
  `LINES_OUTPUT` bigint DEFAULT NULL,
  `LINES_REJECTED` bigint DEFAULT NULL,
  `ERRORS` bigint DEFAULT NULL,
  `STARTDATE` datetime DEFAULT NULL,
  `ENDDATE` datetime DEFAULT NULL,
  `LOGDATE` datetime DEFAULT NULL,
  `DEPDATE` datetime DEFAULT NULL,
  `REPLAYDATE` datetime DEFAULT NULL,
  `LOG_FIELD` longtext,
  KEY `IDX_log_etl_job_1` (`ID_JOB`),
  KEY `IDX_log_etl_job_2` (`ERRORS`,`STATUS`,`JOBNAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for log_etl_job_entry
-- ----------------------------
DROP TABLE IF EXISTS `log_etl_job_entry`;
CREATE TABLE `log_etl_job_entry` (
  `ID_BATCH` int DEFAULT NULL,
  `CHANNEL_ID` varchar(255) DEFAULT NULL,
  `LOG_DATE` datetime DEFAULT NULL,
  `TRANSNAME` varchar(255) DEFAULT NULL,
  `STEPNAME` varchar(255) DEFAULT NULL,
  `LINES_READ` bigint DEFAULT NULL,
  `LINES_WRITTEN` bigint DEFAULT NULL,
  `LINES_UPDATED` bigint DEFAULT NULL,
  `LINES_INPUT` bigint DEFAULT NULL,
  `LINES_OUTPUT` bigint DEFAULT NULL,
  `LINES_REJECTED` bigint DEFAULT NULL,
  `ERRORS` bigint DEFAULT NULL,
  `RESULT` varchar(5) DEFAULT NULL,
  `NR_RESULT_ROWS` bigint DEFAULT NULL,
  `NR_RESULT_FILES` bigint DEFAULT NULL,
  KEY `IDX_log_etl_job_entry_1` (`ID_BATCH`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for log_etl_transform
-- ----------------------------
DROP TABLE IF EXISTS `log_etl_transform`;
CREATE TABLE `log_etl_transform` (
  `ID_BATCH` int DEFAULT NULL,
  `CHANNEL_ID` varchar(255) DEFAULT NULL,
  `TRANSNAME` varchar(255) DEFAULT NULL,
  `STATUS` varchar(15) DEFAULT NULL,
  `LINES_READ` bigint DEFAULT NULL,
  `LINES_WRITTEN` bigint DEFAULT NULL,
  `LINES_UPDATED` bigint DEFAULT NULL,
  `LINES_INPUT` bigint DEFAULT NULL,
  `LINES_OUTPUT` bigint DEFAULT NULL,
  `LINES_REJECTED` bigint DEFAULT NULL,
  `ERRORS` bigint DEFAULT NULL,
  `STARTDATE` datetime DEFAULT NULL,
  `ENDDATE` datetime DEFAULT NULL,
  `LOGDATE` datetime DEFAULT NULL,
  `DEPDATE` datetime DEFAULT NULL,
  `REPLAYDATE` datetime DEFAULT NULL,
  `LOG_FIELD` longtext,
  KEY `IDX_log_etl_transform_1` (`ID_BATCH`),
  KEY `IDX_log_etl_transform_2` (`ERRORS`,`STATUS`,`TRANSNAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for log_etl_transform_channel
-- ----------------------------
DROP TABLE IF EXISTS `log_etl_transform_channel`;
CREATE TABLE `log_etl_transform_channel` (
  `ID_BATCH` int DEFAULT NULL,
  `CHANNEL_ID` varchar(255) DEFAULT NULL,
  `LOG_DATE` datetime DEFAULT NULL,
  `LOGGING_OBJECT_TYPE` varchar(255) DEFAULT NULL,
  `OBJECT_NAME` varchar(255) DEFAULT NULL,
  `OBJECT_COPY` varchar(255) DEFAULT NULL,
  `REPOSITORY_DIRECTORY` varchar(255) DEFAULT NULL,
  `FILENAME` varchar(255) DEFAULT NULL,
  `OBJECT_ID` varchar(255) DEFAULT NULL,
  `OBJECT_REVISION` varchar(255) DEFAULT NULL,
  `PARENT_CHANNEL_ID` varchar(255) DEFAULT NULL,
  `ROOT_CHANNEL_ID` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for log_etl_transform_metrics
-- ----------------------------
DROP TABLE IF EXISTS `log_etl_transform_metrics`;
CREATE TABLE `log_etl_transform_metrics` (
  `ID_BATCH` int DEFAULT NULL,
  `CHANNEL_ID` varchar(255) DEFAULT NULL,
  `LOG_DATE` datetime DEFAULT NULL,
  `METRICS_DATE` datetime DEFAULT NULL,
  `METRICS_CODE` varchar(255) DEFAULT NULL,
  `METRICS_DESCRIPTION` varchar(255) DEFAULT NULL,
  `METRICS_SUBJECT` varchar(255) DEFAULT NULL,
  `METRICS_TYPE` varchar(255) DEFAULT NULL,
  `METRICS_VALUE` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for log_etl_transform_performance
-- ----------------------------
DROP TABLE IF EXISTS `log_etl_transform_performance`;
CREATE TABLE `log_etl_transform_performance` (
  `ID_BATCH` int DEFAULT NULL,
  `SEQ_NR` int DEFAULT NULL,
  `LOGDATE` datetime DEFAULT NULL,
  `TRANSNAME` varchar(255) DEFAULT NULL,
  `STEPNAME` varchar(255) DEFAULT NULL,
  `STEP_COPY` int DEFAULT NULL,
  `LINES_READ` bigint DEFAULT NULL,
  `LINES_WRITTEN` bigint DEFAULT NULL,
  `LINES_UPDATED` bigint DEFAULT NULL,
  `LINES_INPUT` bigint DEFAULT NULL,
  `LINES_OUTPUT` bigint DEFAULT NULL,
  `LINES_REJECTED` bigint DEFAULT NULL,
  `ERRORS` bigint DEFAULT NULL,
  `INPUT_BUFFER_ROWS` bigint DEFAULT NULL,
  `OUTPUT_BUFFER_ROWS` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for log_etl_transform_step
-- ----------------------------
DROP TABLE IF EXISTS `log_etl_transform_step`;
CREATE TABLE `log_etl_transform_step` (
  `ID_BATCH` int DEFAULT NULL,
  `CHANNEL_ID` varchar(255) DEFAULT NULL,
  `LOG_DATE` datetime DEFAULT NULL,
  `TRANSNAME` varchar(255) DEFAULT NULL,
  `STEPNAME` varchar(255) DEFAULT NULL,
  `STEP_COPY` int DEFAULT NULL,
  `LINES_READ` bigint DEFAULT NULL,
  `LINES_WRITTEN` bigint DEFAULT NULL,
  `LINES_UPDATED` bigint DEFAULT NULL,
  `LINES_INPUT` bigint DEFAULT NULL,
  `LINES_OUTPUT` bigint DEFAULT NULL,
  `LINES_REJECTED` bigint DEFAULT NULL,
  `ERRORS` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for qrtz_blob_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_blob_triggers`;
CREATE TABLE `qrtz_blob_triggers` (
  `sched_name` varchar(120) NOT NULL,
  `trigger_name` varchar(190) NOT NULL,
  `trigger_group` varchar(190) NOT NULL,
  `blob_data` blob,
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  KEY `sched_name` (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_blob_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for qrtz_calendars
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_calendars`;
CREATE TABLE `qrtz_calendars` (
  `sched_name` varchar(120) NOT NULL,
  `calendar_name` varchar(190) NOT NULL,
  `calendar` blob NOT NULL,
  PRIMARY KEY (`sched_name`,`calendar_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for qrtz_cron_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_cron_triggers`;
CREATE TABLE `qrtz_cron_triggers` (
  `sched_name` varchar(120) NOT NULL,
  `trigger_name` varchar(190) NOT NULL,
  `trigger_group` varchar(190) NOT NULL,
  `cron_expression` varchar(120) NOT NULL,
  `time_zone_id` varchar(80) DEFAULT NULL,
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_cron_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for qrtz_fired_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_fired_triggers`;
CREATE TABLE `qrtz_fired_triggers` (
  `sched_name` varchar(120) NOT NULL,
  `entry_id` varchar(95) NOT NULL,
  `trigger_name` varchar(190) NOT NULL,
  `trigger_group` varchar(190) NOT NULL,
  `instance_name` varchar(190) NOT NULL,
  `fired_time` bigint NOT NULL,
  `sched_time` bigint NOT NULL,
  `priority` int NOT NULL,
  `state` varchar(16) NOT NULL,
  `job_name` varchar(190) DEFAULT NULL,
  `job_group` varchar(190) DEFAULT NULL,
  `is_nonconcurrent` varchar(1) DEFAULT NULL,
  `requests_recovery` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`sched_name`,`entry_id`),
  KEY `idx_qrtz_ft_trig_inst_name` (`sched_name`,`instance_name`),
  KEY `idx_qrtz_ft_inst_job_req_rcvry` (`sched_name`,`instance_name`,`requests_recovery`),
  KEY `idx_qrtz_ft_j_g` (`sched_name`,`job_name`,`job_group`),
  KEY `idx_qrtz_ft_jg` (`sched_name`,`job_group`),
  KEY `idx_qrtz_ft_t_g` (`sched_name`,`trigger_name`,`trigger_group`),
  KEY `idx_qrtz_ft_tg` (`sched_name`,`trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for qrtz_job_details
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_job_details`;
CREATE TABLE `qrtz_job_details` (
  `sched_name` varchar(120) NOT NULL,
  `job_name` varchar(190) NOT NULL,
  `job_group` varchar(190) NOT NULL,
  `description` varchar(250) DEFAULT NULL,
  `job_class_name` varchar(250) NOT NULL,
  `is_durable` varchar(1) NOT NULL,
  `is_nonconcurrent` varchar(1) NOT NULL,
  `is_update_data` varchar(1) NOT NULL,
  `requests_recovery` varchar(1) NOT NULL,
  `job_data` blob,
  PRIMARY KEY (`sched_name`,`job_name`,`job_group`),
  KEY `idx_qrtz_j_req_recovery` (`sched_name`,`requests_recovery`),
  KEY `idx_qrtz_j_grp` (`sched_name`,`job_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for qrtz_locks
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_locks`;
CREATE TABLE `qrtz_locks` (
  `sched_name` varchar(120) NOT NULL,
  `lock_name` varchar(40) NOT NULL,
  PRIMARY KEY (`sched_name`,`lock_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for qrtz_paused_trigger_grps
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_paused_trigger_grps`;
CREATE TABLE `qrtz_paused_trigger_grps` (
  `sched_name` varchar(120) NOT NULL,
  `trigger_group` varchar(190) NOT NULL,
  PRIMARY KEY (`sched_name`,`trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for qrtz_scheduler_state
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_scheduler_state`;
CREATE TABLE `qrtz_scheduler_state` (
  `sched_name` varchar(120) NOT NULL,
  `instance_name` varchar(190) NOT NULL,
  `last_checkin_time` bigint NOT NULL,
  `checkin_interval` bigint NOT NULL,
  PRIMARY KEY (`sched_name`,`instance_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for qrtz_simple_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_simple_triggers`;
CREATE TABLE `qrtz_simple_triggers` (
  `sched_name` varchar(120) NOT NULL,
  `trigger_name` varchar(190) NOT NULL,
  `trigger_group` varchar(190) NOT NULL,
  `repeat_count` bigint NOT NULL,
  `repeat_interval` bigint NOT NULL,
  `times_triggered` bigint NOT NULL,
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_simple_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for qrtz_simprop_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_simprop_triggers`;
CREATE TABLE `qrtz_simprop_triggers` (
  `sched_name` varchar(120) NOT NULL,
  `trigger_name` varchar(190) NOT NULL,
  `trigger_group` varchar(190) NOT NULL,
  `str_prop_1` varchar(512) DEFAULT NULL,
  `str_prop_2` varchar(512) DEFAULT NULL,
  `str_prop_3` varchar(512) DEFAULT NULL,
  `int_prop_1` int DEFAULT NULL,
  `int_prop_2` int DEFAULT NULL,
  `long_prop_1` bigint DEFAULT NULL,
  `long_prop_2` bigint DEFAULT NULL,
  `dec_prop_1` decimal(13,4) DEFAULT NULL,
  `dec_prop_2` decimal(13,4) DEFAULT NULL,
  `bool_prop_1` varchar(1) DEFAULT NULL,
  `bool_prop_2` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_simprop_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for qrtz_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_triggers`;
CREATE TABLE `qrtz_triggers` (
  `sched_name` varchar(120) NOT NULL,
  `trigger_name` varchar(190) NOT NULL,
  `trigger_group` varchar(190) NOT NULL,
  `job_name` varchar(190) NOT NULL,
  `job_group` varchar(190) NOT NULL,
  `description` varchar(250) DEFAULT NULL,
  `next_fire_time` bigint DEFAULT NULL,
  `prev_fire_time` bigint DEFAULT NULL,
  `priority` int DEFAULT NULL,
  `trigger_state` varchar(16) NOT NULL,
  `trigger_type` varchar(8) NOT NULL,
  `start_time` bigint NOT NULL,
  `end_time` bigint DEFAULT NULL,
  `calendar_name` varchar(190) DEFAULT NULL,
  `misfire_instr` smallint DEFAULT NULL,
  `job_data` blob,
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  KEY `idx_qrtz_t_j` (`sched_name`,`job_name`,`job_group`),
  KEY `idx_qrtz_t_jg` (`sched_name`,`job_group`),
  KEY `idx_qrtz_t_c` (`sched_name`,`calendar_name`),
  KEY `idx_qrtz_t_g` (`sched_name`,`trigger_group`),
  KEY `idx_qrtz_t_state` (`sched_name`,`trigger_state`),
  KEY `idx_qrtz_t_n_state` (`sched_name`,`trigger_name`,`trigger_group`,`trigger_state`),
  KEY `idx_qrtz_t_n_g_state` (`sched_name`,`trigger_group`,`trigger_state`),
  KEY `idx_qrtz_t_next_fire_time` (`sched_name`,`next_fire_time`),
  KEY `idx_qrtz_t_nft_st` (`sched_name`,`trigger_state`,`next_fire_time`),
  KEY `idx_qrtz_t_nft_misfire` (`sched_name`,`misfire_instr`,`next_fire_time`),
  KEY `idx_qrtz_t_nft_st_misfire` (`sched_name`,`misfire_instr`,`next_fire_time`,`trigger_state`),
  KEY `idx_qrtz_t_nft_st_misfire_grp` (`sched_name`,`misfire_instr`,`next_fire_time`,`trigger_group`,`trigger_state`),
  CONSTRAINT `qrtz_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `job_name`, `job_group`) REFERENCES `qrtz_job_details` (`sched_name`, `job_name`, `job_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for running_process
-- ----------------------------
DROP TABLE IF EXISTS `running_process`;
CREATE TABLE `running_process` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `category` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `instance_id` varchar(255) DEFAULT NULL,
  `instance_name` varchar(255) DEFAULT NULL,
  `owner` varchar(255) DEFAULT NULL,
  `prod` varchar(255) DEFAULT NULL,
  `shell_id` bigint DEFAULT NULL,
  `shell_publish_id` bigint DEFAULT NULL,
  `version` int NOT NULL,
  `creator` varchar(255) DEFAULT NULL,
  `modifier` varchar(255) DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `tenant_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK80yxdva0jjk9o4lqnwnom5f52` (`shell_id`),
  KEY `FKj0k0bkecsaxov7or3sfujb561` (`shell_publish_id`),
  KEY `FKm2t3r5uha2qm9effcawc6k07y` (`tenant_id`),
  CONSTRAINT `FK80yxdva0jjk9o4lqnwnom5f52` FOREIGN KEY (`shell_id`) REFERENCES `designer_shell` (`id`),
  CONSTRAINT `FKj0k0bkecsaxov7or3sfujb561` FOREIGN KEY (`shell_publish_id`) REFERENCES `designer_shell_publish` (`id`),
  CONSTRAINT `FKm2t3r5uha2qm9effcawc6k07y` FOREIGN KEY (`tenant_id`) REFERENCES `basic_tenant` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for shell_publish_log
-- ----------------------------
DROP TABLE IF EXISTS `shell_publish_log`;
CREATE TABLE `shell_publish_log` (
  `log_channel_id` varchar(255) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `shell_publish_id` bigint DEFAULT NULL,
  PRIMARY KEY (`log_channel_id`),
  KEY `FKaw6aibi0n1nki3gt9n2c4eyf8` (`shell_publish_id`),
  CONSTRAINT `FKaw6aibi0n1nki3gt9n2c4eyf8` FOREIGN KEY (`shell_publish_id`) REFERENCES `designer_shell_publish` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

SET FOREIGN_KEY_CHECKS = 1;
INSERT INTO `auth_resource` (`id`, `code`, `name`, `status`, `version`, `create_time`, `creator`, `modifier`, `modify_time`, `tenant_id`, `category`, `level`) VALUES (1, 'ROOT', 'ROOT', '1', 1, NULL, NULL, NULL, NULL, NULL, 'ROOT', '0');
INSERT INTO `auth_resource` (`id`, `code`, `name`, `status`, `version`, `create_time`, `creator`, `modifier`, `modify_time`, `tenant_id`, `category`, `level`) VALUES (2, 'HOME', '首页', '1', 0, NULL, NULL, NULL, NULL, NULL, '门户', '1');
INSERT INTO `auth_resource` (`id`, `code`, `name`, `status`, `version`, `create_time`, `creator`, `modifier`, `modify_time`, `tenant_id`, `category`, `level`) VALUES (3, 'PROJECT', '工作区管理', '1', 0, NULL, NULL, NULL, NULL, NULL, '基础数据管理', '1');
INSERT INTO `auth_resource` (`id`, `code`, `name`, `status`, `version`, `create_time`, `creator`, `modifier`, `modify_time`, `tenant_id`, `category`, `level`) VALUES (4, 'DATASOURCE', '数据源管理', '1', 0, NULL, NULL, NULL, NULL, NULL, '基础数据管理', '1');
INSERT INTO `auth_resource` (`id`, `code`, `name`, `status`, `version`, `create_time`, `creator`, `modifier`, `modify_time`, `tenant_id`, `category`, `level`) VALUES (5, 'ATTACHMENT', '下载中心', '1', 0, NULL, NULL, NULL, NULL, NULL, '基础数据管理', '1');
INSERT INTO `auth_resource` (`id`, `code`, `name`, `status`, `version`, `create_time`, `creator`, `modifier`, `modify_time`, `tenant_id`, `category`, `level`) VALUES (6, 'DESIGNER', '在线设计', '1', 0, NULL, NULL, NULL, NULL, NULL, 'ETL管理', '1');
INSERT INTO `auth_resource` (`id`, `code`, `name`, `status`, `version`, `create_time`, `creator`, `modifier`, `modify_time`, `tenant_id`, `category`, `level`) VALUES (7, 'PUBLISH', '脚本发布', '1', 0, NULL, NULL, NULL, NULL, NULL, 'ETL管理', '1');
INSERT INTO `auth_resource` (`id`, `code`, `name`, `status`, `version`, `create_time`, `creator`, `modifier`, `modify_time`, `tenant_id`, `category`, `level`) VALUES (8, 'BATCH', '批处理任务', '1', 0, NULL, NULL, NULL, NULL, NULL, '任务管理', '1');
INSERT INTO `auth_resource` (`id`, `code`, `name`, `status`, `version`, `create_time`, `creator`, `modifier`, `modify_time`, `tenant_id`, `category`, `level`) VALUES (9, 'STREAMING', '流处理任务', '1', 0, NULL, NULL, NULL, NULL, NULL, '任务管理', '1');
INSERT INTO `auth_resource` (`id`, `code`, `name`, `status`, `version`, `create_time`, `creator`, `modifier`, `modify_time`, `tenant_id`, `category`, `level`) VALUES (10, 'PROCESS', '进程管理', '1', 0, NULL, NULL, NULL, NULL, NULL, '任务管理', '1');
INSERT INTO `auth_resource` (`id`, `code`, `name`, `status`, `version`, `create_time`, `creator`, `modifier`, `modify_time`, `tenant_id`, `category`, `level`) VALUES (11, 'LOG', '日志跟踪', '1', 0, NULL, NULL, NULL, NULL, NULL, '任务管理', '1');
INSERT INTO `auth_resource` (`id`, `code`, `name`, `status`, `version`, `create_time`, `creator`, `modifier`, `modify_time`, `tenant_id`, `category`, `level`) VALUES (12, 'MODEL', '模型设计', '1', 0, NULL, NULL, NULL, NULL, NULL, '在线报表', '1');
INSERT INTO `auth_resource` (`id`, `code`, `name`, `status`, `version`, `create_time`, `creator`, `modifier`, `modify_time`, `tenant_id`, `category`, `level`) VALUES (13, 'REPORT', '统计设置', '1', 0, NULL, NULL, NULL, NULL, NULL, '在线报表', '1');
INSERT INTO `auth_resource` (`id`, `code`, `name`, `status`, `version`, `create_time`, `creator`, `modifier`, `modify_time`, `tenant_id`, `category`, `level`) VALUES (14, 'LAYOUT', '布局设置', '1', 0, NULL, NULL, NULL, NULL, NULL, '在线报表', '1');
INSERT INTO `auth_resource` (`id`, `code`, `name`, `status`, `version`, `create_time`, `creator`, `modifier`, `modify_time`, `tenant_id`, `category`, `level`) VALUES (15, 'MEMBER', '成员管理', '1', 0, NULL, NULL, NULL, NULL, NULL, '系统管理', '1');
INSERT INTO `auth_resource` (`id`, `code`, `name`, `status`, `version`, `create_time`, `creator`, `modifier`, `modify_time`, `tenant_id`, `category`, `level`) VALUES (16, 'SETTING', '用户管理', '1', 0, NULL, NULL, NULL, NULL, NULL, '系统管理', '1');
INSERT INTO `auth_resource` (`id`, `code`, `name`, `status`, `version`, `create_time`, `creator`, `modifier`, `modify_time`, `tenant_id`, `category`, `level`) VALUES (17, 'METRICS', '集群性能', '1', 0, NULL, NULL, NULL, NULL, NULL, '系统管理', '1');
