## What's this

Teiid Embedded is a light-weight version of Teiid, it contain an easy-to-use JDBC Driver that can embed the Query Engine in any Java application. The Embedded mode supply almost all Teiid features without JEE Container involved, it supply a convenient way for Users who want integrate Teiid with their Application.

Primary purpose of this project contains:

* Demonstrating how simple Java Application interect multiple, heterogenous data sources/stores(Relational Databases, NO-SQL, In-memory Data Grid/Cache, Flat Files, Web Services, etc) with Teiid Embedded.
* Demonstrating how to develop new Translator/Connector for Teiid new Users or Teiid starter 

### Available samples

The following tables show all available samples:

| **Data Sources** | **Code Snippets** | **Blog Link** |
|:-----------|:-----------|:-----------|
|![CSV](metadata/img/csv.jpeg) ![XML](metadata/img/xml.jpeg) ![TXT](metadata/img/text_icon.gif) |[TestFileDataSource](src/test/java/com/teiid/embedded/samples/file/TestFileDataSource.java) |http://ksoong.org/teiid-embedded-file |
|![Mysql](metadata/img/mysql-icon.png) |[TestMysqDataSource](src/test/java/com/teiid/embedded/samples/mysql/TestMysqDataSource.java) |http://ksoong.org/teiid-embedded-mysql |



## The Procedure for integrate Teiid with Java Application.

### Step I: Add Dependencies

The `teiid-runtime` is necessary, it contain Teiid Embedded libraries, you can either download Teiid Embedded from [http://teiid.jboss.org/downloads/](http://teiid.jboss.org/downloads/), or add maven dependency:

~~~
		<dependency>
			<groupId>org.jboss.teiid</groupId>
			<artifactId>teiid-runtime</artifactId>
			<version>8.9.0.Alpha2-SNAPSHOT</version>
		</dependency>
~~~

The other dependencies should be connecor/translator which depend on the the data sources your are using.

### Step II: Start Teiid Embedded Server with connecor/translator configured

The following code snippets show a rough process:

~~~
EmbeddedServer server = new EmbeddedServer();
server.addTranslator("my-translator", myExecutionFactory);
server.addConnectionFactory("my-connector", myConnectionFactory)
server.start(config);
~~~

### Step III: Deploy VDB

Use Embedded Server API to deploy VDB:

~~~
server.deployVDB(new FileInputStream(new File("my-vdb.xml")));
~~~

### Step IV: Consume the data

Teiid Embedded have an easy-to-use JDBC Driver, we consume the data via it:

~~~
Connection conn = server.getDriver().connect("jdbc:teiid:MyVDB", null);
~~~


