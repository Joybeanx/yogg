CREATE TABLE IF NOT EXISTS key_website (
  url VARCHAR(255) COMMENT 'key website url',
  PRIMARY KEY (url)
);
COMMENT ON TABLE key_website IS 'Table used for testing stores website by which Yogg sent SMS successfully';

CREATE TABLE IF NOT EXISTS task_report (
  task_id VARCHAR(36) COMMENT 'task id that current report belongs to',
  PRIMARY KEY (task_id),
  content VARCHAR(65535) COMMENT 'report content'
);
COMMENT ON TABLE task_report IS 'Table stores website from which Yogg can take';

CREATE TABLE IF NOT EXISTS sms_sending_record (
  website         VARCHAR(255) COMMENT 'the website by which we send SMS to',
  target_page_url VARCHAR(65535) COMMENT 'target page url,such as a register page url that maybe used for sending SMS',
  has_captcha     TINYINT(1) DEFAULT '0'
  COMMENT 'whether the target page contains captcha',
  sms_request_url VARCHAR(65535) COMMENT 'the url of requesting a SMS on the website,phone number in the url is replaced by "%s"',
  exception       VARCHAR(65535) COMMENT 'the exception during execution',
  status          TINYINT(2) DEFAULT '0'
  COMMENT 'the result status of sending SMS by website',
  task_id         VARCHAR(36) COMMENT 'task id that current record belongs to',
  create_time     DATETIME COMMENT 'the create time of current record',
  PRIMARY KEY (website, task_id)
);
COMMENT ON TABLE sms_sending_record IS 'Table stores SMS sending details for every website';

