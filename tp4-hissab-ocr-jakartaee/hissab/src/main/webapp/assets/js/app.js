document.addEventListener("DOMContentLoaded", () => {
    setupUploadZone();
    setupCopyButtons();
});

function setupUploadZone() {
    const form = document.getElementById("uploadForm");
    const dropZone = document.getElementById("dropZone");
    const fileInput = document.getElementById("fileInput");
    const fileInfo = document.getElementById("fileInfo");
    const previewWrap = document.getElementById("previewWrap");
    const imagePreview = document.getElementById("imagePreview");
    const previewFallback = document.getElementById("previewFallback");
    const progressWrap = document.getElementById("progressWrap");
    const progressBarFill = document.getElementById("progressBarFill");
    const progressLabel = document.getElementById("progressLabel");

    if (!dropZone || !fileInput) {
        return;
    }

    dropZone.addEventListener("click", (event) => {
        if (event.target === fileInput || event.target.closest("input[type='file']")) {
            return;
        }
        fileInput.click();
    });
    dropZone.addEventListener("keydown", (event) => {
        if (event.key === "Enter" || event.key === " ") {
            event.preventDefault();
            fileInput.click();
        }
    });

    ["dragenter", "dragover"].forEach((eventName) => {
        dropZone.addEventListener(eventName, (event) => {
            event.preventDefault();
            dropZone.classList.add("drag-active");
        });
    });
    ["dragleave", "drop"].forEach((eventName) => {
        dropZone.addEventListener(eventName, (event) => {
            event.preventDefault();
            dropZone.classList.remove("drag-active");
        });
    });

    dropZone.addEventListener("drop", (event) => {
        const files = event.dataTransfer.files;
        if (files && files.length > 0) {
            fileInput.files = files;
            refreshPreview(files[0], fileInfo, previewWrap, imagePreview, previewFallback);
        }
    });

    fileInput.addEventListener("click", (event) => {
        event.stopPropagation();
    });

    fileInput.addEventListener("change", () => {
        const selectedFile = fileInput.files && fileInput.files[0];
        refreshPreview(selectedFile, fileInfo, previewWrap, imagePreview, previewFallback);
    });

    if (form && progressWrap && progressBarFill && progressLabel) {
        form.addEventListener("submit", () => {
            progressWrap.classList.remove("hidden");
            let value = 0;
            progressBarFill.style.width = "0%";
            progressLabel.textContent = "0%";
            const timer = setInterval(() => {
                value = Math.min(value + 8, 90);
                progressBarFill.style.width = value + "%";
                progressLabel.textContent = value + "%";
                if (value >= 90) {
                    clearInterval(timer);
                }
            }, 180);
        });
    }
}

function refreshPreview(file, fileInfo, previewWrap, imagePreview, previewFallback) {
    if (!file || !fileInfo || !previewWrap || !imagePreview || !previewFallback) {
        return;
    }

    fileInfo.textContent = `Selected file: ${file.name} (${Math.ceil(file.size / 1024)} KB)`;
    previewWrap.classList.remove("hidden");

    if (file.type.startsWith("image/")) {
        const reader = new FileReader();
        reader.onload = (event) => {
            imagePreview.src = event.target.result;
            imagePreview.classList.remove("hidden");
            previewFallback.classList.add("hidden");
        };
        reader.readAsDataURL(file);
    } else {
        imagePreview.classList.add("hidden");
        previewFallback.classList.remove("hidden");
        previewFallback.textContent = "Preview is unavailable for PDF. The file will be processed on the server.";
    }
}

function setupCopyButtons() {
    const buttons = document.querySelectorAll("[data-copy-result]");
    if (!buttons.length) {
        return;
    }

    buttons.forEach((button) => {
        button.addEventListener("click", () => {
            const row = button.closest("tr");
            if (!row) {
                return;
            }
            const valueElement = row.querySelector(".result-value");
            if (!valueElement) {
                return;
            }
            navigator.clipboard.writeText(valueElement.textContent.trim()).then(() => {
                const previousText = button.textContent;
                button.textContent = "Copied";
                setTimeout(() => {
                    button.textContent = previousText;
                }, 1300);
            });
        });
    });
}
