package calculator.persistence;

import calculator.domain.TraceEntry;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileTraceRepository implements TraceRepository {
    private static final int OPERATION_WIDTH = 14;
    private static final int LEFT_WIDTH = 14;
    private static final int OPERATOR_WIDTH = 12;
    private static final int RIGHT_WIDTH = 14;
    private static final int RESULT_WIDTH = 22;
    private static final String ROW_FORMAT = "%-" + OPERATION_WIDTH + "s | %"
            + LEFT_WIDTH + "s | %-"
            + OPERATOR_WIDTH + "s | %"
            + RIGHT_WIDTH + "s | %"
            + RESULT_WIDTH + "s";
    private static final String HEADER_LINE = String.format(
            ROW_FORMAT, "Operation", "OpG", "Operateur", "OpD", "Resultat");
    private static final String SEPARATOR_LINE = "-".repeat(HEADER_LINE.length());

    private final Path traceDirectory;
    private final Path traceFile;

    public FileTraceRepository() {
        this.traceDirectory = resolveTraceDirectory();
        this.traceFile = traceDirectory.resolve("track.trc");
    }

    public FileTraceRepository(Path traceFile) {
        Path absoluteFile = traceFile.toAbsolutePath();
        this.traceFile = absoluteFile;
        this.traceDirectory = absoluteFile.getParent();
    }

    @Override
    public void save(TraceEntry entry) {
        try {
            Files.createDirectories(traceDirectory);
            ensureTraceFileInitialized();

            String row = formatRow(entry);
            Files.writeString(
                    traceFile,
                    row + System.lineSeparator(),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            throw new IllegalStateException("Unable to save operation trace.", e);
        }
    }

    @Override
    public String readAllAsText() {
        try {
            Files.createDirectories(traceDirectory);
            ensureTraceFileInitialized();

            String content = Files.readString(traceFile, StandardCharsets.UTF_8);
            if (!hasDataRows(content)) {
                return "No trace available.\n";
            }

            if (!content.endsWith(System.lineSeparator())) {
                content += System.lineSeparator();
            }
            return content;
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read operation trace.", e);
        }
    }

    private Path resolveTraceDirectory() {
        try {
            Path basePath = Paths.get(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());

            if (Files.isRegularFile(basePath)) {
                basePath = basePath.getParent();
            }

            if (basePath != null && basePath.getFileName() != null && "out".equals(basePath.getFileName().toString())) {
                basePath = basePath.getParent();
            }

            if (basePath != null) {
                return basePath.resolve("data");
            }
        } catch (URISyntaxException e) {
            // Falls back to user.dir below.
        }

        return Paths.get(System.getProperty("user.dir")).toAbsolutePath().resolve("data");
    }

    private void ensureTraceFileInitialized() throws IOException {
        boolean needsInitialization = Files.notExists(traceFile) || Files.size(traceFile) == 0;

        if (!needsInitialization && !startsWithExpectedHeader()) {
            needsInitialization = true;
        }

        if (needsInitialization) {
            String initialContent = HEADER_LINE
                    + System.lineSeparator()
                    + SEPARATOR_LINE
                    + System.lineSeparator();

            Files.writeString(
                    traceFile,
                    initialContent,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        }
    }

    private boolean startsWithExpectedHeader() throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(traceFile, StandardCharsets.UTF_8)) {
            String firstLine = reader.readLine();
            return HEADER_LINE.equals(firstLine);
        } catch (MalformedInputException e) {
            return false;
        }
    }

    private boolean hasDataRows(String content) {
        int nonEmptyLines = 0;
        for (String line : content.split("\\R")) {
            if (!line.isBlank()) {
                nonEmptyLines++;
            }
        }
        return nonEmptyLines > 2;
    }

    private String formatRow(TraceEntry entry) {
        return String.format(
                ROW_FORMAT,
                entry.getType().getDisplayName(),
                entry.getLeftOperandText(),
                entry.getOperatorText(),
                entry.getRightOperandText(),
                entry.getResultText()
        );
    }
}
