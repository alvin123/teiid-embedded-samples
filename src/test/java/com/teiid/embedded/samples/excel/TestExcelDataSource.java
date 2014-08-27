package com.teiid.embedded.samples.excel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.teiid.resource.adapter.file.FileManagedConnectionFactory;
import org.teiid.translator.excel.ExcelExecutionFactory;

import com.teiid.embedded.samples.TestBase;
import com.teiid.embedded.samples.util.JDBCUtil;

public class TestExcelDataSource extends TestBase{
	
	@BeforeClass
	public static void init() throws Exception {
		
		init("excel", new ExcelExecutionFactory());
		
		FileManagedConnectionFactory managedconnectionFactory = new FileManagedConnectionFactory();
		managedconnectionFactory.setParentDirectory("metadata");
		server.addConnectionFactory("java:/excel-file", managedconnectionFactory.createConnectionFactory());
		
		start(false);
		
		server.deployVDB(new FileInputStream(new File("vdb/excel-vdb.xml")));
		
		conn = server.getDriver().connect("jdbc:teiid:ExcelVDB", null);
	}
	
	
	@Test
	public void testQuery() throws Exception {
		assertNotNull(conn);
		assertTrue(JDBCUtil.countResults(conn, "SELECT * FROM Sheet1") > 0);
		assertTrue(JDBCUtil.countResults(conn, "SELECT * FROM PersonalHoldings") > 0);
	}

}
