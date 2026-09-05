package dz.hissab.ocr;

import dz.hissab.exception.OcrProcessingException;
import jakarta.ejb.Stateless;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Stateless
public class OcrEJB implements OcrService {
    private static final String TESSDATA_PATH = "/usr/share/tesseract-ocr/5/tessdata/";
    private static final String OCR_LANGUAGE = "fra+eng";
    private static final Set<String> SUPPORTED_MIME_TYPES = Set.of(
            "image/png",
            "image/jpeg",
            "application/pdf");

    @Override
    public List<String> extractExpressions(byte[] fileData, String mimeType) throws OcrProcessingException {
        if (!SUPPORTED_MIME_TYPES.contains(mimeType)) {
            throw new OcrProcessingException("Unsupported file type: " + mimeType);
        }

        File tempFile = null;
        try {
            if (mimeType.startsWith("image/")) {
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(fileData));
                if (image == null) {
                    throw new OcrProcessingException("OCR failed: Could not decode image");
                }
            }

            String suffix = switch (mimeType) {
                case "image/png" -> ".png";
                case "image/jpeg" -> ".jpg";
                case "application/pdf" -> ".pdf";
                default -> "";
            };

            tempFile = File.createTempFile("hissab_", suffix);
            Files.write(tempFile.toPath(), fileData);

            Tesseract tesseract = new Tesseract();
            tesseract.setDatapath(TESSDATA_PATH);
            tesseract.setLanguage(OCR_LANGUAGE);

            String rawText = tesseract.doOCR(tempFile);
            String[] lines = rawText.split("\\R");

            List<String> expressions = new ArrayList<>();
            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.matches(".*\\d+.*[+\\-*/×÷−].*\\d+.*")) {
                    expressions.add(trimmed);
                }
            }

            return expressions;
        } catch (TesseractException e) {
            throw new OcrProcessingException("OCR failed: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new OcrProcessingException("OCR failed: " + e.getMessage(), e);
        } finally {
            if (tempFile != null && tempFile.exists() && !tempFile.delete()) {
                tempFile.deleteOnExit();
            }
        }
    }
}
