package dz.hissab.ocr;

import dz.hissab.exception.OcrProcessingException;
import jakarta.ejb.Local;

import java.util.List;

@Local
public interface OcrService {
    List<String> extractExpressions(byte[] fileData, String mimeType) throws OcrProcessingException;
}
