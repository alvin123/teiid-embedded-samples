package org.teiid.embedded.samples;

import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.transaction.TransactionManager;

import com.arjuna.ats.arjuna.common.ObjectStoreEnvironmentBean;
import com.arjuna.ats.arjuna.common.arjPropertyManager;
import com.arjuna.common.internal.util.propertyservice.BeanPopulator;

public class EmbeddedHelper {
		
	public static TransactionManager getTransactionManager() throws Exception {
		
		arjPropertyManager.getCoreEnvironmentBean().setNodeIdentifier("1");
		arjPropertyManager.getCoreEnvironmentBean().setSocketProcessIdPort(0);
		arjPropertyManager.getCoreEnvironmentBean().setSocketProcessIdMaxPorts(10);
		
		arjPropertyManager.getCoordinatorEnvironmentBean().setEnableStatistics(false);
		arjPropertyManager.getCoordinatorEnvironmentBean().setDefaultTimeout(300);
		arjPropertyManager.getCoordinatorEnvironmentBean().setTransactionStatusManagerEnable(false);
		arjPropertyManager.getCoordinatorEnvironmentBean().setTxReaperTimeout(120000);
		
		String defDir = getSystemProperty("user.home") + File.separator + ".teiid/embedded/data";
		String storeDir = getSystemProperty("teiid.embedded.txStoreDir", defDir);
		
		arjPropertyManager.getObjectStoreEnvironmentBean().setObjectStoreDir(storeDir);
		BeanPopulator.getNamedInstance(ObjectStoreEnvironmentBean.class, "communicationStore").setObjectStoreDir(storeDir);
		
		return com.arjuna.ats.jta.TransactionManager.transactionManager();
	}

	
	private static String getSystemProperty(final String name, final String value) {
		return AccessController.doPrivileged(new PrivilegedAction<String>(){

			@Override
			public String run() {
				return System.getProperty(name, value);
			}});
	}
	
	private static String getSystemProperty(final String name) {
		return AccessController.doPrivileged(new PrivilegedAction<String>(){

			@Override
			public String run() {
				return System.getProperty(name);
			}});
	}
	


}
