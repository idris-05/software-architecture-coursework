package dz.hissab.servlet;

import dz.hissab.calc.CalculationDetails;
import dz.hissab.calc.CalcService;
import dz.hissab.exception.CalcException;
import dz.hissab.exception.HissabException;
import dz.hissab.exception.OcrProcessingException;
import dz.hissab.ocr.OcrService;
import dz.hissab.trace.ComputationTrace;
import dz.hissab.trace.TraceService;
import dz.hissab.util.MathSanitizer;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@WebServlet("/process")
@MultipartConfig(maxFileSize = 10 * 1024 * 1024, maxRequestSize = 10 * 1024 * 1024)
public class HissabServlet extends HttpServlet {
    private static final double COMPARISON_EPSILON = 1e-9;
    private record ParsedExpressionInput(String expression, String providedAnswer) {
    }

    @EJB
    private OcrService ocrService;

    @EJB
    private CalcService calcService;

    @EJB
    private TraceService traceService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String mode = req.getParameter("mode");
        if ("manual".equalsIgnoreCase(mode)) {
            processManual(req, resp);
            return;
        }
        processUpload(req, resp);
    }

    private void processUpload(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final FileUploadHelper.UploadedFile file;
        try {
            file = FileUploadHelper.extract(req);
        } catch (HissabException e) {
            forwardError(req, resp, e.getMessage(), null);
            return;
        }

        final List<String> expressions;
        try {
            expressions = ocrService.extractExpressions(file.data(), file.mimeType());
        } catch (OcrProcessingException e) {
            forwardError(req, resp, e.getMessage(), null);
            return;
        }

        if (expressions.isEmpty()) {
            forwardError(req, resp, "No arithmetic expression found in the uploaded file.", null);
            return;
        }

        List<ComputationTrace> resultList = new ArrayList<>();
        for (String rawExpr : expressions) {
            resultList.add(evaluateExpression(file.filename(), rawExpr, null));
        }

        req.setAttribute("results", resultList);
        req.setAttribute("filename", file.filename());
        req.getRequestDispatcher("/WEB-INF/views/result.jsp").forward(req, resp);
    }

    private void processManual(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String expression = req.getParameter("expression");
        String providedAnswer = getProvidedAnswer(req);

        if (expression == null || expression.isBlank()) {
            forwardError(req, resp, "Expression is null or empty", expression);
            return;
        }

        List<ComputationTrace> resultList = List.of(evaluateExpression("manual", expression, providedAnswer));
        req.setAttribute("results", resultList);
        req.setAttribute("filename", "Manual entry");
        req.getRequestDispatcher("/WEB-INF/views/result.jsp").forward(req, resp);
    }

    private ComputationTrace evaluateExpression(String sourceName, String rawExpr, String providedAnswer) {
        ParsedExpressionInput parsedInput = parseExpressionInput(rawExpr, providedAnswer);
        ComputationTrace trace = new ComputationTrace();
        trace.setImageName(sourceName);
        trace.setExtractedExpression(rawExpr);
        trace.setSanitizedExpression(parsedInput.expression());
        trace.setBreakdownSteps(List.of());
        trace.setProvidedAnswer(null);

        try {
            String sanitized = MathSanitizer.sanitize(parsedInput.expression());
            CalculationDetails details = calcService.evaluateDetailed(sanitized);

            trace.setSanitizedExpression(sanitized);
            trace.setBreakdownSteps(details.steps());

            if (parsedInput.providedAnswer() != null && !parsedInput.providedAnswer().isBlank()) {
                double providedValue = parseProvidedAnswer(parsedInput.providedAnswer());
                trace.setProvidedAnswer(formatNumber(providedValue));
                if (Math.abs(providedValue - details.result()) > COMPARISON_EPSILON) {
                    trace.setStatus("ERROR");
                    trace.setResult("Your answer " + formatNumber(providedValue)
                            + " is wrong. The correct result is " + formatNumber(details.result()) + ".");
                } else {
                    trace.setStatus("SUCCESS");
                    trace.setResult(formatNumber(details.result()));
                }
            } else {
                trace.setStatus("SUCCESS");
                trace.setResult(formatNumber(details.result()));
            }
        } catch (CalcException e) {
            trace.setStatus("ERROR");
            trace.setResult("Error: " + toFriendlyMessage(e.getMessage()));
        }

        traceService.save(trace);
        return trace;
    }

    private ParsedExpressionInput parseExpressionInput(String rawExpr, String explicitProvidedAnswer) {
        String expressionPart = rawExpr == null ? "" : rawExpr.trim();
        String inlineProvidedAnswer = null;

        if (rawExpr != null) {
            int equalIndex = rawExpr.indexOf('=');
            if (equalIndex >= 0) {
                String left = rawExpr.substring(0, equalIndex).trim();
                String right = rawExpr.substring(equalIndex + 1).trim();
                if (!left.isBlank()) {
                    expressionPart = left;
                }
                if (!right.isBlank()) {
                    inlineProvidedAnswer = right;
                }
            }
        }

        String providedAnswer = explicitProvidedAnswer;
        if (providedAnswer == null || providedAnswer.isBlank()) {
            providedAnswer = inlineProvidedAnswer;
        }
        return new ParsedExpressionInput(expressionPart, providedAnswer);
    }

    private double parseProvidedAnswer(String rawValue) throws CalcException {
        String normalized = rawValue.trim().replace(',', '.');
        try {
            return Double.parseDouble(normalized);
        } catch (NumberFormatException e) {
            throw new CalcException("Provided answer is not a valid number: " + rawValue, e);
        }
    }

    private String getProvidedAnswer(HttpServletRequest req) {
        String providedAnswer = req.getParameter("providedAnswer");
        if (providedAnswer == null || providedAnswer.isBlank()) {
            providedAnswer = req.getParameter("expectedResult");
        }
        return providedAnswer;
    }

    private String formatNumber(double value) {
        if (value == Math.rint(value)) {
            return String.valueOf((long) value);
        }
        return String.format(Locale.US, "%.6f", value).replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    private void forwardError(HttpServletRequest req, HttpServletResponse resp, String message, String expression)
            throws ServletException, IOException {
        req.setAttribute("errorMessage", toFriendlyMessage(message));
        req.setAttribute("errorDetails", message);
        req.setAttribute("lastExpression", expression);
        req.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(req, resp);
    }

    private String toFriendlyMessage(String message) {
        if (message == null || message.isBlank()) {
            return "An unexpected error occurred. Please try again.";
        }
        String normalized = message.toLowerCase(Locale.ROOT);
        if (normalized.contains("unknown character")) {
            return "Unrecognized symbols were detected. Try manual entry or upload a clearer image.";
        }
        if (normalized.contains("malformed expression")) {
            return "The expression appears incomplete. Check parentheses and operators.";
        }
        if (normalized.contains("no arithmetic expression found")) {
            return "No arithmetic expression was recognized in the uploaded file.";
        }
        if (normalized.contains("unsupported file type")) {
            return "Unsupported file type. Please use PNG, JPEG, or PDF.";
        }
        if (normalized.contains("no file uploaded")) {
            return "No valid file was received. Drop an image or PDF before submitting.";
        }
        if (normalized.contains("division by zero")) {
            return "Division by zero is not allowed. Correct the expression and try again.";
        }
        if (normalized.contains("provided answer is not a valid number")) {
            return "The provided answer must be a valid number.";
        }
        return message;
    }
}
