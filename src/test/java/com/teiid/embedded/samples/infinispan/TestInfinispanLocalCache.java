package com.teiid.embedded.samples.infinispan;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.resource.ResourceException;

import org.junit.Test;
import org.teiid.resource.adapter.infinispan.InfinispanManagedConnectionFactory;
import org.teiid.runtime.EmbeddedConfiguration;
import org.teiid.runtime.EmbeddedServer;
import org.teiid.translator.TranslatorException;
import org.teiid.translator.object.ObjectConnection;
import org.teiid.translator.object.ObjectExecutionFactory;

import com.teiid.embedded.samples.infinispan.model.LineItem;
import com.teiid.embedded.samples.infinispan.model.Order;
import com.teiid.embedded.samples.infinispan.model.Product;

public class TestInfinispanLocalCache {
	
	static final String TEST_CACHE_NAME = "local-quickstart-cache";

	static EmbeddedServer server = null;
	private static Connection conn = null;
	
	@Test
	public void testConnection() throws ResourceException, TranslatorException {
		InfinispanManagedConnectionFactory factory = new InfinispanManagedConnectionFactory();
		factory.setConfigurationFileNameForLocalCache("src/test/resources/infinispan_local.xml");
		factory.setCacheTypeMap(TEST_CACHE_NAME + ":" + "java.lang.Long;longValue");
		ObjectConnection conn = factory.createConnectionFactory().getConnection();
		Map<Object, Object> cache = conn.getCacheContainer().getCache(TEST_CACHE_NAME);
		cache.put("key", "value");
		assertEquals("value", conn.getCacheContainer().get(TEST_CACHE_NAME, "key"));
		cache.remove("key");
		assertEquals(0, cache.size());
		conn.getType(TEST_CACHE_NAME);
	}

	protected void init() throws Exception {

		server = new EmbeddedServer();
		
		ObjectExecutionFactory executionFactory = new ObjectExecutionFactory() ;
		server.addTranslator("infinispan", executionFactory);
		
		InfinispanManagedConnectionFactory managedConnectionFactory = new InfinispanManagedConnectionFactory();
		managedConnectionFactory.setConfigurationFileNameForLocalCache("src/test/resources/infinispan_local.xml");
		managedConnectionFactory.setCacheTypeMap(TEST_CACHE_NAME + ":" + "java.lang.Long;longValue");
//		managedConnectionFactory.createConnectionFactory().getConnection().getCacheContainer().getCache(TEST_CACHE_NAME).putAll(loadCache());
		server.addConnectionFactory("java:/infinispanTest", managedConnectionFactory.createConnectionFactory());
		
		EmbeddedConfiguration config = new EmbeddedConfiguration();
		server.start(config);
		
		server.deployVDB(new FileInputStream(new File("src/vdb/infinispancache-vdb.xml")));
		
		conn = server.getDriver().connect("jdbc:teiid:orders", null);
	}
	
	@Test
	public void testQuery() throws Exception {
		init();
		
	}
	
	
	public static final int NUM_ORDERS = 10;
	public static final int NUM_PRODUCTS = 3;
	
	protected Map<Object, Object> loadCache() {
		Map<Object, Object> incache = new HashMap<Object, Object>();
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
	
	public static void main(String[] args) throws Exception { 
		
		TestInfinispanLocalCache test = new TestInfinispanLocalCache();
		test.testQuery();
	}

}
