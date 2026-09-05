package calc.rest;

import calc.calculator.Calculator;
import calc.model.HistoryRecord;
import calc.persistence.HistoryManager;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CalculatorRestService {

    @POST
    @Path("/add")
    public Response add(OperationRequest request) {
        double result = Calculator.add(request.getA(), request.getB());
        HistoryManager.getInstance().save(request.getA(), "+", request.getB(), result, "REST");
        return Response.ok(buildResult(result)).build();
    }

    @POST
    @Path("/subtract")
    public Response subtract(OperationRequest request) {
        double result = Calculator.subtract(request.getA(), request.getB());
        HistoryManager.getInstance().save(request.getA(), "-", request.getB(), result, "REST");
        return Response.ok(buildResult(result)).build();
    }

    @POST
    @Path("/multiply")
    public Response multiply(OperationRequest request) {
        double result = Calculator.multiply(request.getA(), request.getB());
        HistoryManager.getInstance().save(request.getA(), "*", request.getB(), result, "REST");
        return Response.ok(buildResult(result)).build();
    }

    @POST
    @Path("/divide")
    public Response divide(OperationRequest request) {
        try {
            double result = Calculator.divide(request.getA(), request.getB());
            HistoryManager.getInstance().save(request.getA(), "/", request.getB(), result, "REST");
            return Response.ok(buildResult(result)).build();
        } catch (ArithmeticException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(buildError(e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/history")
    public Response history() {
        List<HistoryRecord> records = HistoryManager.getInstance().getAll();
        return Response.ok(records).build();
    }

    private static Map<String, Object> buildResult(double value) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("result", value);
        return body;
    }

    private static Map<String, String> buildError(String message) {
        Map<String, String> body = new LinkedHashMap<>();
        body.put("error", message);
        return body;
    }

    public static class OperationRequest {

        private double a;
        private double b;

        public OperationRequest() {
        }

        public double getA() {
            return a;
        }

        public void setA(double a) {
            this.a = a;
        }

        public double getB() {
            return b;
        }

        public void setB(double b) {
            this.b = b;
        }
    }
}
