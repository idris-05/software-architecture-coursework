<%@ page contentType="text/html; charset=UTF-8" isErrorPage="true" %>
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
    <title>Error — HISSAB</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css">
</head>
<body>
<main class="page-shell">
    <div class="container">
        <header class="brand-header">
            <div class="brand-row">
                <img src="<%= request.getContextPath() %>/assets/img/logo.png" alt="Logo HISSAB">
                <div>
                    <h1>HISSAB</h1>
                    <p class="subtitle">Processing error</p>
                </div>
            </div>
            <nav class="top-nav">
                <a href="<%= request.getContextPath() %>/index.jsp">Home</a>
                <a href="<%= request.getContextPath() %>/history">History</a>
            </nav>
        </header>

        <section class="card">
            <h2>We could not complete this calculation</h2>
            <div class="error-box">
                <%
                    Object msgAttr = request.getAttribute("errorMessage");
                    String message;
                    if (msgAttr != null) {
                        message = String.valueOf(msgAttr);
                    } else if (exception != null && exception.getMessage() != null) {
                        message = exception.getMessage();
                    } else {
                        message = "An unexpected error occurred.";
                    }
                    Object detailsAttr = request.getAttribute("errorDetails");
                %>
                <p><%= h(message) %></p>
                <% if (detailsAttr != null) { %>
                <p class="muted">Technical detail: <code><%= h(detailsAttr) %></code></p>
                <% } %>
            </div>
        </section>

        <section class="card">
            <h2>Retry with manual entry</h2>
            <form action="<%= request.getContextPath() %>/process" method="POST" class="manual-form">
                <input type="hidden" name="mode" value="manual">
                <label for="expression">Expression</label>
                <input id="expression" name="expression" type="text"
                       value="<%= h(request.getAttribute("lastExpression")) %>"
                       placeholder="e.g. (7 + 5) / 3" required>
                <button type="submit" class="btn btn-secondary">Recalculate</button>
            </form>
            <a class="back-link" href="<%= request.getContextPath() %>/index.jsp">&larr; Back to home</a>
        </section>
    </div>
</main>
</body>
</html>
