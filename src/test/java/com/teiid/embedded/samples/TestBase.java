package com.teiid.embedded.samples;

import java.net.InetSocketAddress;
import java.sql.Connection;

import org.junit.AfterClass;
import org.teiid.runtime.EmbeddedConfiguration;
import org.teiid.runtime.EmbeddedServer;
import org.teiid.translator.ExecutionFactory;
import org.teiid.translator.TranslatorException;
import org.teiid.transport.SocketConfiguration;
import org.teiid.transport.WireProtocol;

public class TestBase {
	
	protected static EmbeddedServer server = null;
	protected static Connection conn = null;
	
	protected static void init(String name, ExecutionFactory<?, ?> factory) throws TranslatorException {
		server = new EmbeddedServer();
		factory.start();
		factory.setSupportsDirectQueryProcedure(true);
		server.addTranslator(name, factory);
	}
	
	protected static void start(boolean isRemote){
		
		if(isRemote) {
			SocketConfiguration s = new SocketConfiguration();
			InetSocketAddress addr = new InetSocketAddress("localhost", 31000);
			s.setBindAddress(addr.getHostName());
			s.setPortNumber(addr.getPort());
			s.setProtocol(WireProtocol.teiid);
			EmbeddedConfiguration config = new EmbeddedConfiguration();
			config.addTransport(s);
			server.start(config);
		} else {
			server.start(new EmbeddedConfiguration());
		}
	}
	
	@AfterClass
	public static void tearDown() throws Exception {
		if(null != conn) {
			conn.close();
			conn = null;
		}
		if(null != server) {
			server.stop();
			server = null;
		}
	}

}
