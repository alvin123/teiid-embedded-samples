package org.teiid.embedded.samples;

import static org.junit.Assert.assertEquals;

import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.TransactionManager;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestEmbeddedHelper {
	
	@BeforeClass
	public static void init() {
		System.setProperty("teiid.embedded.txStoreDir", "target/data");
	}
	
	@Test
	public void commitTransactionManager() throws Exception {
		
		TransactionManager tm = EmbeddedHelper.getTransactionManager();
		
		tm.begin();
		
		tm.commit();
	}
	
	@Test
	public void rollbackTransaction() throws Exception {

		TransactionManager tm = EmbeddedHelper.getTransactionManager();
		
		tm.begin();
		
		tm.rollback();
	}
	
	@Test(expected = RollbackException.class)
	public void setRollbackOnly() throws Exception {
		
		TransactionManager tm = EmbeddedHelper.getTransactionManager();
		
		tm.begin();
		
		tm.setRollbackOnly();
		
		tm.commit();
	}
	
	@Test
	public void transactionStatus() throws Exception {
		
		TransactionManager tm = EmbeddedHelper.getTransactionManager();
		
		tm.begin();
		
		assertEquals(Status.STATUS_ACTIVE, tm.getTransaction().getStatus());
		
		tm.setRollbackOnly();
		
		assertEquals(Status.STATUS_MARKED_ROLLBACK, tm.getTransaction().getStatus());
		
		tm.rollback();	
	}
	
	@Test(expected=RollbackException.class)
	public void transactionTimeout() throws Exception{
		
		TransactionManager tm = EmbeddedHelper.getTransactionManager();
		
		tm.setTransactionTimeout(3);
		
		tm.begin();
		
		Thread.sleep(1000 * 5);
		
		tm.commit();
	}

}
