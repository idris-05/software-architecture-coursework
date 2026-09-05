<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>HISSAB — School Calculator</title>
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
                    <p class="subtitle">OCR-powered school calculator</p>
                </div>
            </div>
            <nav class="top-nav">
                <a href="<%= request.getContextPath() %>/index.jsp">Home</a>
                <a href="<%= request.getContextPath() %>/history">History</a>
            </nav>
        </header>

        <section class="card">
            <h2>Smart upload</h2>
            <form id="uploadForm" action="<%= request.getContextPath() %>/process" method="POST" enctype="multipart/form-data">
                <input type="hidden" name="mode" value="upload">
                <div id="dropZone" class="drop-zone" tabindex="0" role="button" aria-label="File drop zone">
                    <p class="drop-title">Drag and drop a file here</p>
                    <p class="drop-sub">or click to choose a PNG, JPG, or PDF</p>
                    <input id="fileInput" type="file" name="file" accept=".png,.jpg,.jpeg,.pdf" required>
                </div>
                <div id="fileInfo" class="file-info">No file selected.</div>
                <div id="previewWrap" class="preview-wrap hidden">
                    <img id="imagePreview" alt="File preview before processing">
                    <div id="previewFallback" class="preview-fallback hidden"></div>
                </div>
                <div id="progressWrap" class="progress-wrap hidden">
                    <div class="progress-head">
                        <span>Processing</span>
                        <span id="progressLabel">0%</span>
                    </div>
                    <div class="progress-bar">
                        <div id="progressBarFill" class="progress-fill"></div>
                    </div>
                </div>
                <button type="submit" class="btn btn-primary">Run OCR calculation</button>
            </form>
        </section>

        <section class="card">
            <h2>Manual expression entry</h2>
            <form action="<%= request.getContextPath() %>/process" method="POST" class="manual-form">
                <input type="hidden" name="mode" value="manual">
                <label for="expression">Expression</label>
                <input id="expression" name="expression" type="text" placeholder="e.g. (5 + 2) * 6 - 3" required>
                <label for="providedAnswer">Your answer (optional)</label>
                <input id="providedAnswer" name="providedAnswer" type="text" placeholder="e.g. 39">
                <button type="submit" class="btn btn-secondary">Calculate manually</button>
            </form>
        </section>

        <section class="card info-panel">
            <h2>Operator precedence rules</h2>
            <p><strong>1.</strong> Parentheses</p>
            <p><strong>2.</strong> Multiplication and division</p>
            <p><strong>3.</strong> Addition and subtraction</p>
            <p class="muted">HISSAB applies these rules automatically and shows each calculation step.</p>
        </section>
    </div>
</main>
<script src="<%= request.getContextPath() %>/assets/js/app.js"></script>
</body>
</html>
