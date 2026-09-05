package calc;

import calc.rest.CalculatorRestService;
import calc.rest.CorsFilter;
import calc.rest.StaticResource;
import calc.soap.CalculatorSoapServiceImpl;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import jakarta.xml.ws.Endpoint;
import jakarta.xml.ws.soap.SOAPBinding;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;

public final class Main {

    private static final String BASE_URI = "http://0.0.0.0:8080/";
    private static final String SOAP_HOST = "0.0.0.0";
    private static final int SOAP_PORT = 8081;
    private static final String SOAP_PATH = "/calc";

    private Main() {
    }

    public static void main(String[] args) throws IOException {
        System.out.println("[CALC] Starting REST server on http://localhost:8080/");
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.register(CalculatorRestService.class);
        resourceConfig.register(StaticResource.class);
        resourceConfig.register(CorsFilter.class);
        org.glassfish.grizzly.http.server.HttpServer restServer =
                GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), resourceConfig);

        System.out.println("[CALC] Starting SOAP server on http://localhost:" + SOAP_PORT + SOAP_PATH);
        HttpServer soapServer = buildSoapServer(new CalculatorSoapServiceImpl());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("[CALC] Shutdown signal received.");
            restServer.shutdownNow();
            soapServer.stop(0);
            System.out.println("[CALC] Stopped.");
        }, "calc-shutdown"));

        System.out.println("[CALC] All services up. Press ENTER to stop.");
        try {
            System.in.read();
        } catch (IOException ignored) {
        }
        restServer.shutdownNow();
        soapServer.stop(0);
        System.out.println("[CALC] Stopped.");
    }

    private static HttpServer buildSoapServer(CalculatorSoapServiceImpl impl) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(SOAP_HOST, SOAP_PORT), 0);

        HttpContext ctx = server.createContext(SOAP_PATH);
        ctx.getFilters().add(new CorsFilterAdapter());

        Endpoint endpoint = Endpoint.create(SOAPBinding.SOAP11HTTP_BINDING, impl);
        endpoint.publish(ctx);

        server.start();
        return server;
    }

    private static final class CorsFilterAdapter extends Filter {

        @Override
        public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                addCorsHeaders(exchange.getResponseHeaders());
                exchange.sendResponseHeaders(204, -1);
                exchange.close();
                return;
            }
            addCorsHeaders(exchange.getResponseHeaders());
            chain.doFilter(exchange);
        }

        @Override
        public String description() {
            return "CORS preflight + headers";
        }
    }

    private static void addCorsHeaders(com.sun.net.httpserver.Headers headers) {
        if (headers.getFirst("Access-Control-Allow-Origin") == null) {
            headers.add("Access-Control-Allow-Origin", "*");
            headers.add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            headers.add("Access-Control-Allow-Headers", "Content-Type, SOAPAction");
            headers.add("Access-Control-Max-Age", "86400");
        }
    }
}
