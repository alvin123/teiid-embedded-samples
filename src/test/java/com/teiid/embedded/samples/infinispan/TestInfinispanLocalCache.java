package com.teiid.embedded.samples.infinispan;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.resource.ResourceException;

import org.infinispan.Cache;
import org.junit.BeforeClass;
import org.junit.Test;
import org.teiid.resource.adapter.infinispan.InfinispanManagedConnectionFactory;
import org.teiid.translator.TranslatorException;
import org.teiid.translator.object.ObjectConnection;
import org.teiid.translator.object.ObjectExecutionFactory;

import com.teiid.embedded.samples.TestBase;
import com.teiid.embedded.samples.infinispan.model.LineItem;
import com.teiid.embedded.samples.infinispan.model.Order;
import com.teiid.embedded.samples.infinispan.model.Product;
import com.teiid.embedded.samples.util.JDBCUtil;

public class TestInfinispanLocalCache extends TestBase {
	
	static final String TEST_CACHE_NAME = "local-quickstart-cache";


	@BeforeClass
	public static void init() throws Exception {
		
		init("infinispan-local", new ObjectExecutionFactory());
		
		InfinispanManagedConnectionFactory managedConnectionFactory = new InfinispanManagedConnectionFactory();
		managedConnectionFactory.setConfigurationFileNameForLocalCache("src/test/resources/infinispan-config-local.xml");
		managedConnectionFactory.setCacheTypeMap(TEST_CACHE_NAME + ":" + Order.class.getName());
		server.addConnectionFactory("java:/infinispanTest", managedConnectionFactory.createConnectionFactory());
		
		start(false);
		
		loadCache(managedConnectionFactory);
		
		server.deployVDB(new FileInputStream(new File("vdb/infinispancache-vdb.xml")));
		
		conn = server.getDriver().connect("jdbc:teiid:orders", null);
	}
	
	@Test
	public void testQuery() throws Exception {
		assertNotNull(conn);
		assertEquals(10, JDBCUtil.countResults(conn, "select * from OrdersView"));
		assertEquals(7, JDBCUtil.countResults(conn, "select * from OrdersView where OrderNum > 3"));
	}
	
	protected static void loadCache(InfinispanManagedConnectionFactory managedConnectionFactory) throws TranslatorException, ResourceException {
		
		ObjectConnection conn = (ObjectConnection) managedConnectionFactory.createConnectionFactory().getConnection();
		Cache<String, Order> cache = conn.getCacheContainer().getCache(TEST_CACHE_NAME);
		cache.putAll(loadCache());		
	}
	
	public static final int NUM_ORDERS = 10;
	public static final int NUM_PRODUCTS = 3;
	
	protected static Map<String, Order> loadCache() {
		Map<String, Order> incache = new HashMap<String, Order>();
		List<Product> products = new ArrayList<Product>(NUM_PRODUCTS);
		products.add(new Product(1, "Shirt", 54.99)); 
		products.add(new Product(2, "Pants", 89.00)); 
		products.add(new Product(3, "Socks", 1.29)); 
		int lineitems = 1;
		for (int i = 1; i <= NUM_ORDERS; i++) {
			List<LineItem> items = new ArrayList<LineItem>();
			for (int j = 0, p = 0, q = 1; j < lineitems; j++) {
				LineItem item = new LineItem(j + 1, products.get(p), q);
				items.add(item);
				++p;
				++q;
			}
			Order order = new Order(i, new Date(), "Person " + i, items); 
			incache.put(String.valueOf(i), order);
			++lineitems;
			if (lineitems > NUM_PRODUCTS) {
				lineitems = 1;
			}
		}
		return incache;
	}
	
}
