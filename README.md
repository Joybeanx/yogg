Yogg
============
![logo](docs/img/logo.png "logo")

Overview
-------------------
Yogg makes use of website's SMS Gateway to send verification code to target phone number.
A website store is necessary to launch it.Yogg consumes every website in the website store,simulating a series of manual operations on the website pages so as to send SMS to the target phone number.
The operation includes but is not limited to searching for the page that provides SMS function(e.g. a register page on which use can send verification code),target phone number input,captcha recognition,clicking "Send SMS" button etc.

![overview](docs/img/overview.gif "Yogg overview")

### Features
* Metadata driven architecture
* Flexible execution flow configuration
* Support multiple website sources
* Visualized execution statistics
* Schedule(being developed)

### How it works
Yogg uses state machine to describe the flow of sending SMS on a website.There are lots of operations in relation to page element discovery during the execution of the state machine.
The built-in execution flow is a very basic one as below:

![execution flow](docs/img/execution-flow.png "execution flow")

In consideration of extremely various of websites in the world,Yogg is definitely unable to adapt to all of them,but you can increase its accuracy by customizing your own state machine.
Effects of Yogg mainly depend on the number of websites the specified website store can offer and the flow configurations(based on state machine) which describe how to send SMS by browser.

Quick start
-------------------
### Prerequisite

The only requirement to run Yogg is to have the Java Development Kit (JDK)
version 8 installed on your system (not just the JRE, but the full JDK) which
you can download from the below link.

[JDK download](http://www.oracle.com/technetwork/java/javase/downloads/index.html)

### Download
Go to release page to download executable jar

### Configuration
+ threads: the number of threads used for sending SMS,each website consumes a thread
+ timeout: the timeout of attempt to send SMS by a website
+ proxy: the proxy by which Yogg access a website
+ DataSource:
    + Crawler:
        + start url: the url with which crawler starts to collect website
    + File:
        + file path: the local website file
    + Database:you should execute [SQL script](scripts "scripts") to initialize your database first
        + type: data source type
        + username: username of database
        + password: password of database

### Startup
Open command line,enter the directory in which your executable jar located,run:

    java -jar yogg-${version}-RELEASE.jar

Building Yogg
-------------------
Yogg is built using [Apache Maven](http://maven.apache.org/).
To build Yogg, run:

    mvn -DskipTests clean package

The artifact will be generated in: target

Limitations
-------------------
* Default configuration only support **Chinese website and Chinese phone number**,please custom your own state machine and page XPath configuration according to your requirement
* Yogg has a fairly low success rate of breaking captcha on a website,seriously impacting the effect of sending SMS