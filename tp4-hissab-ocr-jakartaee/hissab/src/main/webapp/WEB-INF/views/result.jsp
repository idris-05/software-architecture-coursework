<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List, java.time.format.DateTimeFormatter, dz.hissab.trace.ComputationTrace" %>
<%!
    private String h(Object value) {
        if (value == null) {
            return "";
        }
        return String.valueOf(value)
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Results — HISSAB</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css">
</head>
<body>
<%
    @SuppressWarnings("unchecked")
    List<ComputationTrace> results = (List<ComputationTrace>) request.getAttribute("results");
    String filename = String.valueOf(request.getAttribute("filename"));
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
%>
<main class="page-shell">
    <div class="container">
        <header class="brand-header">
            <div class="brand-row">
                <img src="<%= request.getContextPath() %>/assets/img/logo.png" alt="Logo HISSAB">
                <div>
                    <h1>HISSAB</h1>
                    <p class="subtitle">Results for: <strong><%= h(filename) %></strong></p>
                </div>
            </div>
            <nav class="top-nav">
                <a href="<%= request.getContextPath() %>/index.jsp">New calculation</a>
                <a href="<%= request.getContextPath() %>/history">History</a>
            </nav>
        </header>

        <section class="card table-card">
            <h2>Calculation details</h2>
            <div class="table-wrap">
                <table>
                    <thead>
                    <tr>
                        <th>Extracted expression</th>
                        <th>Sanitized expression</th>
                        <th>Your answer</th>
                        <th>Result</th>
                        <th>Status</th>
                        <th>Computed at</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <% if (results != null && !results.isEmpty()) {
                        for (ComputationTrace trace : results) {
                            boolean isError = "ERROR".equals(trace.getStatus());
                            String rowClass = isError ? "row-error" : "row-success";
                            String badgeClass = isError ? "badge-error" : "badge-success";
                    %>
                    <tr class="<%= rowClass %>">
                        <td><%= h(trace.getExtractedExpression()) %></td>
                        <td><%= h(trace.getSanitizedExpression()) %></td>
                        <td><%= trace.getProvidedAnswer() == null || trace.getProvidedAnswer().isBlank() ? "-" : h(trace.getProvidedAnswer()) %></td>
                        <td class="result-cell"><span class="result-value"><%= h(trace.getResult()) %></span></td>
                        <td><span class="status-badge <%= badgeClass %>"><%= h(trace.getStatus()) %></span></td>
                        <td><%= trace.getComputedAt() == null ? "-" : h(formatter.format(trace.getComputedAt())) %></td>
                        <td>
                            <div class="table-actions">
                                <button type="button" class="btn btn-ghost btn-small" data-copy-result>Copy</button>
                                <form action="<%= request.getContextPath() %>/process" method="POST" class="inline-form">
                                    <input type="hidden" name="mode" value="manual">
                                    <input type="hidden" name="expression" value="<%= h(trace.getSanitizedExpression()) %>">
                                    <button type="submit" class="btn btn-ghost btn-small">Retry</button>
                                </form>
                            </div>
                        </td>
                    </tr>
                    <tr class="steps-row">
                        <td colspan="7">
                            <% if (trace.getBreakdownSteps() != null && !trace.getBreakdownSteps().isEmpty()) { %>
                            <div class="steps-box">
                                <h3>Step-by-step breakdown</h3>
                                <ol>
                                    <% for (String step : trace.getBreakdownSteps()) { %>
                                    <li><%= h(step) %></li>
                                    <% } %>
                                </ol>
                            </div>
                            <% } else { %>
                            <div class="steps-box muted">
                                <p>No breakdown is available for this row (check or correct the expression).</p>
                            </div>
                            <% } %>
                        </td>
                    </tr>
                    <% }
                    } else { %>
                    <tr>
                        <td colspan="7" class="muted">No results to display.</td>
                    </tr>
                    <% } %>
                    </tbody>
                </table>
            </div>
        </section>

        <section class="card info-panel">
            <h2>Operator precedence (reminder)</h2>
            <p><strong>Parentheses</strong> &rarr; <strong>* /</strong> &rarr; <strong>+ -</strong></p>
            <p class="muted">Displayed steps follow this exact evaluation order.</p>
        </section>
    </div>
</main>
<script src="<%= request.getContextPath() %>/assets/js/app.js"></script>
</body>
</html>
