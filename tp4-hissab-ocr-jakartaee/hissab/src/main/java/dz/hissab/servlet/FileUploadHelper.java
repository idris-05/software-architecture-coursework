package dz.hissab.servlet;

import dz.hissab.exception.HissabException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.util.Set;

public final class FileUploadHelper {
    private static final Set<String> SUPPORTED_MIME_TYPES = Set.of(
            "image/png",
            "image/jpeg",
            "application/pdf"
    );

    private FileUploadHelper() {
    }

    public record UploadedFile(byte[] data, String filename, String mimeType) {
    }

    public static UploadedFile extract(HttpServletRequest request) throws HissabException {
        final Part part;
        try {
            part = request.getPart("file");
        } catch (IOException | ServletException e) {
            throw new HissabException("Failed to read uploaded file", e);
        }

        if (part == null || part.getSize() == 0) {
            throw new HissabException("No file uploaded or file is empty");
        }

        String filename = extractFileName(part);
        if (filename == null || filename.isBlank()) {
            throw new HissabException("Could not determine filename");
        }

        String mimeType = part.getContentType();
        if (!SUPPORTED_MIME_TYPES.contains(mimeType)) {
            throw new HissabException("Unsupported file type: " + mimeType + ". Allowed: PNG, JPEG, PDF");
        }

        try {
            byte[] data = part.getInputStream().readAllBytes();
            return new UploadedFile(data, filename, mimeType);
        } catch (IOException e) {
            throw new HissabException("Failed to read uploaded file bytes", e);
        }
    }

    private static String extractFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        if (contentDisposition == null || contentDisposition.isBlank()) {
            return null;
        }

        String filename = null;
        for (String segment : contentDisposition.split(";")) {
            String trimmed = segment.trim();
            if (trimmed.startsWith("filename=")) {
                filename = trimmed.substring("filename=".length()).trim();
                break;
            }
        }

        if (filename == null || filename.isBlank()) {
            return null;
        }

        if (filename.startsWith("\"") && filename.endsWith("\"") && filename.length() >= 2) {
            filename = filename.substring(1, filename.length() - 1);
        }

        return filename;
    }
}
