CREATE TABLE `website` (
  `url` VARCHAR(255) COMMENT 'the url of the website',
  PRIMARY KEY (`url`)
)
  COMMENT ='stores website from which Yogg can take'
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `key_website` (
  `url` VARCHAR(255) COMMENT 'the url of the key website',
  PRIMARY KEY (`url`)
)
  COMMENT ='stores website by which Yogg sent SMS successfully'
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `sms_sending_record` (
  `website`         VARCHAR(255) COMMENT 'the website by which we send SMS to',
  `target_page_url` VARCHAR(65535) COMMENT 'the target page url of the website,such as a url of register page that maybe used for sending SMS',
  `has_captcha`     TINYINT(1) DEFAULT '0'
  COMMENT 'whether the target page contains captcha',
  `sms_request_url` VARCHAR(65535) COMMENT 'the url of requesting a SMS on the website,phone number in the url is replaced by "%s"',
  `exception`       VARCHAR(65535) COMMENT 'the exception during execution',
  `status`          TINYINT(2) DEFAULT '0'
  COMMENT 'the result status of sending SMS by website',
  `task_id`         VARCHAR(36) COMMENT 'the id of the task that current record belongs to',
  `create_time`     DATETIME COMMENT 'the create time of current record',
  PRIMARY KEY (`website`, `task_id`)
)
  COMMENT ='stores SMS sending details for every website'
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

