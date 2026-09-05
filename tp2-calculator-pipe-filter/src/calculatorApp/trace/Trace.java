package calculatorApp.trace;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Persists the history of executed operations independently from calculation
 * logic.
 */
public class Trace {

    private static final Path TRACE_DIRECTORY = resolveTraceDirectory();
    private static final Path TRACE_FILE = TRACE_DIRECTORY.resolve("track.trc");

    public static void savop(char op, int opg, int opd, long res) {
        appendOperation(TRACE_FILE, new Operation(op, opg, opd, res));
    }

    public static String loadTraceString() {
        return toTraceString(readOperations(TRACE_FILE));
    }

    private static void appendOperation(Path targetFile, Operation operation) {
        try {
            Files.createDirectories(TRACE_DIRECTORY);
            boolean isNewFile = Files.notExists(targetFile) || Files.size(targetFile) == 0;

            // Reuse stream without rewriting header when appending serialized objects.
            try (FileOutputStream fos = new FileOutputStream(targetFile.toFile(), true);
                    ObjectOutputStream oos = isNewFile
                            ? new ObjectOutputStream(fos)
                            : new AppendableObjectOutputStream(fos)) {
                oos.writeObject(operation);
            }
        } catch (IOException e) {
            System.out.println("Error saving operation.");
        }
    }

    private static List<Operation> readOperations(Path sourceFile) {
        List<Operation> operations = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(sourceFile.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)) {
            // Read until EOF because object-stream files have no item count prefix.
            while (true) {
                Operation op = (Operation) ois.readObject();
                operations.add(op);
            }
        } catch (EOFException e) {
            // End file
        } catch (FileNotFoundException e) {
            // No trace yet for this file.
        } catch (Exception e) {
            System.out.println("Error reading trace.");
        }

        return operations;
    }

    private static String toTraceString(List<Operation> operations) {
        StringBuilder content = new StringBuilder();

        if (operations.isEmpty()) {
            content.append("No trace available.\n");
            return content.toString();
        }

        for (Operation op : operations) {
            content.append(formatTimestamp(op)).append(" ")
                    .append(formatOperation(op)).append("\n");
        }

        return content.toString();
    }

    private static String formatTimestamp(Operation op) {
        if (op.savedAt == null || op.savedAt.isBlank()) {
            return "[unknown-time]";
        }
        return "[" + op.savedAt + "]";
    }

    private static String formatOperation(Operation op) {
        switch (op.o) {
            case 's':
                return op.g + " + " + op.d + " = " + op.r;
            case 'p':
                return op.g + " * " + op.d + " = " + op.r;
            case 'f':
                return op.g + " ! = " + op.r;
            case 'r':
                return "random number : " + op.r;
            default:
                return op.o + " " + op.g + " " + op.d + " = " + op.r;
        }
    }

    private static Path resolveTraceDirectory() {
        try {
            Path basePath = Paths.get(Trace.class.getProtectionDomain().getCodeSource().getLocation().toURI());

            if (Files.isRegularFile(basePath)) {
                basePath = basePath.getParent();
            }

            if (basePath != null && basePath.getFileName() != null && "out".equals(basePath.getFileName().toString())) {
                basePath = basePath.getParent();
            }

            if (basePath != null) {
                // Keep traces under the project tree instead of the caller's working directory.
                return basePath.resolve("data");
            }
        } catch (URISyntaxException e) {
            // Fall back to a stable directory under the current workspace.
        }

        return Paths.get(System.getProperty("user.dir")).toAbsolutePath().resolve("data");
    }
}

class AppendableObjectOutputStream extends ObjectOutputStream {
    AppendableObjectOutputStream(OutputStream out) throws IOException {
        super(out);
    }

    protected void writeStreamHeader() throws IOException {
        // Keep existing stream header untouched while appending more objects.
    }
}