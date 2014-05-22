Installation of edition-base

Prerequisites
-------------

### Hardware

-   Min. 3GB HD (Minimal Linux system and application)
-   Additional disk space for your image and XML data
-   512 MB RAM

### Software

These installation instructions are based on a fresh minimal install of
[Ubuntu Linux Server 14.04 LTS](http://www.ubuntu.com/download/server).
For other systems, they might need to be modified accordingly.

These instructions assume you to have root priviledges.

The application relies on the following libraries/packages: Java 6
Runtime Environment

To install them, type

       apt-get install openjdk-6-jre

These instructions also assume that you have unzip installed, if that is
not the case, install it by typing

       apt-get install unzip

Quick Start using Pre-Built Binaries
------------------------------------

The easiest way to use the edition framework is to use the pre-built
binaries. If you want to build them yourself, please see section
[Building from Source](#Building_from_Source "wikilink")

### Creating Directories

By default, the application is looking for its installation files in
`/opt/edition-base` and for its data files (xml, images) in
`/opt/edition-base/data`. To change the location of these files, please
see section [Changing the
Configuration](#Changing_the_Configuration "wikilink") or work with
symbolic links.

To create the directory for the edition data and subdirectories for the
databases, type

       mkdir /opt/edition-base /var/opt/edition-base

### Downloading Example Data

A small set of example data is being maintaned at
<https://github.com/faustedition/data>

To download it, type

       cd /var/opt/edition-base
       wget --content-disposition https://codeload.github.com/faustedition/data/zip/master
       unzip data-master.zip
       mv data-master data

### Downloading the Binaries

       cd /opt/edition-base
       wget --no-check-certificate --content-disposition 'https://faustedition.uni-wuerzburg.de/nexus/service/local/artifact/maven/redirect?r=snapshots&g=de.faustedition&a=edition-base&c=app&p=zip&v=LATEST'
       unzip edition-base-*-app.zip

### Populating the Database from XML data

       rm -rf /var/opt/edition-base/db/*
       java -Xmx512m -Dfile.encoding=UTF-8 -cp /opt/edition-base/app/lib/edition-base-1.3-SNAPSHOT.jar de.faustedition.transcript.TranscriptBatchReader

### Starting the Server

       java -Xmx512m -server -Dfile.encoding=UTF-8 -jar /opt/edition-base/app/lib/edition-base-1.3-SNAPSHOT.jar

The web server runs on port 80 (HTTP) by default. If you want to change
this (for example for a reverse proxy setup), please see section
[Changing the Configuration](#Changing_the_Configuration "wikilink")

Editing the Edition Data
------------------------

The example XML and image files in `/var/opt/edition-base/data` can now
be replaced by your data files, using the same format.

Afterwards, it is necessary to stop the server and perform a database
update as described in section [Populating the Database from XML
data](#Populating_the_Database_from_XML_data "wikilink")

Editing the Website Content
---------------------------

To edit the website content, for example the "About the Project" page,
you can modify the FTL files in `/opt/edition-base/app/templates`

Changing the Configuration
--------------------------

General options such as data directories, server port, context path etc.
can be configured.

The edition contains a default config file that can be used as a
template. Extract it to the installation directory

       cd /opt/edition-base
       unzip app/lib/edition-base-1.3-SNAPSHOT.jar config-default.properties
       mv config-default.properties config-local.properties

After editing the `config-local.properties` file, start the server with
the config file path as its argument.

       java -Xmx512m -server -Dfile.encoding=UTF-8 -jar app/lib/edition-base-1.3-SNAPSHOT.jar /opt/edition-base/config-local.properties

Building from Source
--------------------

### Dependencies

-   Git <http://git-scm.com/>
-   Maven 2 <https://maven.apache.org/>
-   Open JDK 6 <http://openjdk.java.net/>

### Installing the Dependencies

       apt-get install git maven2 openjdk-6-jdk
       mkdir /opt/edition-base
       cd /opt/edition-base
       git clone https://github.com/faustedition/edition-base.git

### Compiling and Install the tei-odd-plugin for Maven

       cd edition-base/edition-base/tei-odd-plugin
       mvn install

### Compiling and Install the XML Schema

       cd ../faust-schema
       mvn install

### Building the Web Application

       cd ../faust
       mvn -Dmaven.test.skip=true install

### Starting the Web Application

       export MAVEN_OPTS="-server -Xmx1024m -Dspring.profiles.active=development"
       mvn exec:java -Dexec.mainClass="de.faustedition.Server" -Dexec.args="/Users/moz/d/faustedition/config.properties"

This relies on pre-build Maven artifacts from other servers. TODO: How
to build those.
