CREATE TABLE `website` (
  `url` varchar(255),
  PRIMARY KEY (`url`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `key_website` (
  `url` varchar(255),
  PRIMARY KEY (`url`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `sms_sending_record` (
  `website` varchar(255),
  `target_page_url` varchar(65535),
  `has_captcha` tinyint(1) DEFAULT '0',
  `sms_request_url` varchar(65535),
  `exception` varchar(65535),
  `status` tinyint(2) DEFAULT '0',
  `task_id` varchar(36),
  `create_time` datetime,
  PRIMARY KEY (`website`,`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

