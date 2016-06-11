import com.peterae86.proxy.HackProxyForJetty92;
import org.eclipse.jetty.alpn.server.ALPNServerConnectionFactory;
import org.eclipse.jetty.proxy.ConnectHandler;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.GzipFilter;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.servlet.DispatcherType;
import java.util.Arrays;
import java.util.EnumSet;

/**
 * Created by test on 2016/2/19.
 */
@SpringBootApplication(scanBasePackages = "com.peterae86")
public class Application {

    @Bean
    public SslContextFactory sslContextFactory() {
        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStoreResource(Resource.newClassPathResource("www.backkoms.com.jks"));
        sslContextFactory.setTrustStoreResource(Resource.newClassPathResource("www.backkoms.com.jks"));
        sslContextFactory.setKeyStorePassword("Peter26983045");
        sslContextFactory.setTrustStorePassword("Peter26983045");
        return sslContextFactory;
    }

    @Bean
    public HttpConfiguration httpConfig() {
        HttpConfiguration httpConfiguration = new HttpConfiguration();
        httpConfiguration.setSecureScheme("https");
        httpConfiguration.setSecurePort(8443);
        httpConfiguration.setOutputBufferSize(32768);
        httpConfiguration.setOutputAggregationSize(8192);
        httpConfiguration.setRequestHeaderSize(8192);
        httpConfiguration.setResponseHeaderSize(8192);
        httpConfiguration.setSendServerVersion(true);
        httpConfiguration.setSendDateHeader(false);
        httpConfiguration.setHeaderCacheSize(512);
        httpConfiguration.setDelayDispatchUntilContent(false);
        return httpConfiguration;
    }

    @Bean
    public HttpConfiguration sslHttpConfig(HttpConfiguration httpConfig) {
        HttpConfiguration httpConfiguration = new HttpConfiguration(httpConfig);
        httpConfiguration.setCustomizers(Arrays.asList(new SecureRequestCustomizer()));
        return httpConfiguration;
    }

    @Bean
    public SslConnectionFactory sslConnectionFactory(SslContextFactory sslContextFactory) {
        return new SslConnectionFactory(sslContextFactory, "http/1.1");
    }

    @Bean
    public HttpConnectionFactory httpConnectionFactory(HttpConfiguration sslHttpConfig) {
        return new HttpConnectionFactory(sslHttpConfig);
    }

    @Bean
    public ConnectHandler servletHandler() {
        ServletHandler servletHandler = new ServletHandler();
        ServletHolder servletHolder = servletHandler.addServletWithMapping(HackProxyForJetty92.class, "/*");
        servletHolder.setInitParameter("maxThreads", "128");
        servletHolder.setInitParameter("maxConnections", "256");
        servletHolder.setInitParameter("idleTimeout", "30000");
        servletHolder.setInitParameter("timeout", "30000");
        servletHandler.addFilterWithMapping(GzipFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
        ConnectHandler connectHandler = new ConnectHandler();
        connectHandler.setHandler(servletHandler);
        return connectHandler;
    }

    @Bean(destroyMethod = "destroy")
    public Server server(ConnectionFactory sslConnectionFactory,
                         ConnectionFactory httpConnectionFactory,
                         ConnectHandler connectHandler) throws Exception {
        Server server = new Server();
        ServerConnector spdyConnector = new ServerConnector(server, sslConnectionFactory,
                 httpConnectionFactory);
        spdyConnector.setPort(8443);
        spdyConnector.setIdleTimeout(20000);
        server.addConnector(spdyConnector);
        server.setHandler(connectHandler);
        server.start();
        return server;
    }


    public static void main(String[] args) throws ClassNotFoundException {
        System.setProperty("java.security.egd", "file:/dev/urandom");
        SpringApplication.run(Application.class, args);
    }
}

