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
    <title>History — HISSAB</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css">
</head>
<body>
<%
    @SuppressWarnings("unchecked")
    List<ComputationTrace> historyItems = (List<ComputationTrace>) request.getAttribute("historyItems");
    String searchQuery = String.valueOf(request.getAttribute("searchQuery"));
    long total = request.getAttribute("statsTotal") == null ? 0L : (Long) request.getAttribute("statsTotal");
    long success = request.getAttribute("statsSuccess") == null ? 0L : (Long) request.getAttribute("statsSuccess");
    long errors = request.getAttribute("statsError") == null ? 0L : (Long) request.getAttribute("statsError");
    long verified = request.getAttribute("statsVerified") == null ? 0L : (Long) request.getAttribute("statsVerified");
    double successRate = total == 0 ? 0 : (success * 100.0 / total);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
%>
<main class="page-shell">
    <div class="container">
        <header class="brand-header">
            <div class="brand-row">
                <img src="<%= request.getContextPath() %>/assets/img/logo.png" alt="Logo HISSAB">
                <div>
                    <h1>HISSAB</h1>
                    <p class="subtitle">Full calculation history</p>
                </div>
            </div>
            <nav class="top-nav">
                <a href="<%= request.getContextPath() %>/index.jsp">Home</a>
                <a href="<%= request.getContextPath() %>/history.csv">Export CSV</a>
            </nav>
        </header>

        <section class="stats-grid">
            <article class="stat-card">
                <p>Total</p>
                <strong><%= total %></strong>
            </article>
            <article class="stat-card">
                <p>Success</p>
                <strong class="ok"><%= success %></strong>
            </article>
            <article class="stat-card">
                <p>Errors / wrong answers</p>
                <strong class="ko"><%= errors %></strong>
            </article>
            <article class="stat-card">
                <p>Success rate</p>
                <strong><%= String.format(java.util.Locale.US, "%.1f", successRate) %>%</strong>
            </article>
            <article class="stat-card">
                <p>With provided answer</p>
                <strong><%= verified %></strong>
            </article>
        </section>

        <section class="card">
            <form method="GET" action="<%= request.getContextPath() %>/history" class="search-form">
                <label for="q">Search by expression</label>
                <div class="search-row">
                    <input id="q" type="text" name="q" value="<%= h(searchQuery) %>" placeholder="e.g. 8*7, (5+2), division">
                    <button type="submit" class="btn btn-primary">Search</button>
                </div>
            </form>
        </section>

        <section class="card table-card">
            <div class="table-wrap">
                <table>
                    <thead>
                    <tr>
                        <th>Source</th>
                        <th>Extracted expression</th>
                        <th>Sanitized expression</th>
                        <th>Your answer</th>
                        <th>Result</th>
                        <th>Status</th>
                        <th>Date</th>
                    </tr>
                    </thead>
                    <tbody>
                    <% if (historyItems != null && !historyItems.isEmpty()) {
                        for (ComputationTrace trace : historyItems) {
                            boolean isError = "ERROR".equals(trace.getStatus());
                    %>
                    <tr class="<%= isError ? "row-error" : "row-success" %>">
                        <td><%= h(trace.getImageName()) %></td>
                        <td><%= h(trace.getExtractedExpression()) %></td>
                        <td><%= h(trace.getSanitizedExpression()) %></td>
                        <td><%= trace.getProvidedAnswer() == null || trace.getProvidedAnswer().isBlank() ? "-" : h(trace.getProvidedAnswer()) %></td>
                        <td><%= h(trace.getResult()) %></td>
                        <td>
                            <span class="status-badge <%= isError ? "badge-error" : "badge-success" %>">
                                <%= h(trace.getStatus()) %>
                            </span>
                        </td>
                        <td><%= trace.getComputedAt() == null ? "-" : h(formatter.format(trace.getComputedAt())) %></td>
                    </tr>
                    <% }
                    } else { %>
                    <tr>
                        <td colspan="7" class="muted">No items found.</td>
                    </tr>
                    <% } %>
                    </tbody>
                </table>
            </div>
        </section>
    </div>
</main>
</body>
</html>
