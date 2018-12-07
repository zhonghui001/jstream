CREATE TABLE "t_jstream_job" (
  "id" int(11) NOT NULL AUTO_INCREMENT,
  "appName" varchar(255) DEFAULT NULL,
  "confPath" varchar(255) DEFAULT NULL,
  "jobState" varchar(255) DEFAULT NULL,
  "applicationId" varchar(255) DEFAULT NULL,
  "applicationState" varchar(255) CHARACTER SET koi8u DEFAULT NULL,
  "configuration" blob,
  "sqlStr" varchar(5000) CHARACTER SET utf8 DEFAULT NULL,
  PRIMARY KEY ("id")
);