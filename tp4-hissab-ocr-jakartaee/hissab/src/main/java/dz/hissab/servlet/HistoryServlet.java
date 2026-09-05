package dz.hissab.servlet;

import dz.hissab.trace.ComputationTrace;
import dz.hissab.trace.TraceService;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@WebServlet(urlPatterns = { "/history", "/history.csv" })
public class HistoryServlet extends HttpServlet {
    @EJB
    private TraceService traceService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String servletPath = req.getServletPath();
        if ("/history.csv".equals(servletPath)) {
            exportCsv(resp);
            return;
        }

        String query = req.getParameter("q");
        List<ComputationTrace> traces = (query == null || query.isBlank())
                ? traceService.findAll()
                : traceService.searchByExpression(query.trim());

        long total = traceService.countAll();
        long success = traceService.countByStatus("SUCCESS");
        long errors = traceService.countByStatus("ERROR");
        long verified = traceService.countWithProvidedAnswer();

        req.setAttribute("historyItems", traces);
        req.setAttribute("searchQuery", query == null ? "" : query.trim());
        req.setAttribute("statsTotal", total);
        req.setAttribute("statsSuccess", success);
        req.setAttribute("statsError", errors);
        req.setAttribute("statsVerified", verified);
        req.getRequestDispatcher("/WEB-INF/views/history.jsp").forward(req, resp);
    }

    private void exportCsv(HttpServletResponse resp) throws IOException {
        List<ComputationTrace> traces = traceService.findAll();
        String filename = URLEncoder.encode("hissab-history.csv", StandardCharsets.UTF_8);

        resp.setContentType("text/csv; charset=UTF-8");
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        try (PrintWriter writer = resp.getWriter()) {
            writer.println("id,image_name,extracted_expression,sanitized_expression,provided_answer,result,status,computed_at");
            for (ComputationTrace trace : traces) {
                writer.printf("%s,%s,%s,%s,%s,%s,%s,%s%n",
                        csv(trace.getId()),
                        csv(trace.getImageName()),
                        csv(trace.getExtractedExpression()),
                        csv(trace.getSanitizedExpression()),
                        csv(trace.getProvidedAnswer()),
                        csv(trace.getResult()),
                        csv(trace.getStatus()),
                        csv(trace.getComputedAt()));
            }
        }
    }

    private String csv(Object value) {
        String text = value == null ? "" : String.valueOf(value);
        if (text.matches("^[\\t\\r\\n ]*[=+\\-@].*")) {
            text = "'" + text;
        }
        return "\"" + text.replace("\"", "\"\"") + "\"";
    }
}
