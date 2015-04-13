package org.jboss.teiid.embedded.samples;

import java.net.InetSocketAddress;
import java.sql.Connection;

import org.teiid.runtime.EmbeddedConfiguration;
import org.teiid.runtime.EmbeddedServer;
import org.teiid.translator.ExecutionFactory;
import org.teiid.translator.TranslatorException;
import org.teiid.transport.SocketConfiguration;
import org.teiid.transport.WireProtocol;

public abstract class ExampleBase {
	
	static {

        System.setProperty("java.util.logging.config.file", "src/test/resources/logging.properties");
	}
	
	protected EmbeddedServer server = null;
	protected Connection conn = null;
	
	protected void init(String name, ExecutionFactory<?, ?> factory) throws TranslatorException {
		server = new EmbeddedServer();
		factory.start();
		factory.setSupportsDirectQueryProcedure(true);
		server.addTranslator(name, factory);
	}
	
	protected void start(boolean isRemote){
		
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
	
	public void tearDown() throws Exception {
		if(null != conn) {
			conn.close();
			conn = null;
		}
		if(null != server) {
			server.stop();
			server = null;
		}
	}
	
	public abstract void execute() throws Exception;

}
