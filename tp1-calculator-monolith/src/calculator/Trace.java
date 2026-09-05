package calculator;

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

import javax.swing.JTextArea;

/**
 * Persists the history of executed operations independently from calculation logic.
 */
public class Trace {

    private static final Path TRACE_DIRECTORY = resolveTraceDirectory();
    private static final Path TRACE_FILE = TRACE_DIRECTORY.resolve("track.trc");

    public static void savop(char op, int opg, int opd, long res) {
        try {
            Files.createDirectories(TRACE_DIRECTORY);
            boolean isNewFile = Files.notExists(TRACE_FILE) || Files.size(TRACE_FILE) == 0;

            try (FileOutputStream fos = new FileOutputStream(TRACE_FILE.toFile(), true);
                 ObjectOutputStream oos = isNewFile
                         ? new ObjectOutputStream(fos)
                         : new AppendableObjectOutputStream(fos)) {
                Operation lop = new Operation(op, opg, opd, res);
                oos.writeObject(lop);
            }
        } catch (IOException e) {
            System.out.println("Error saving operation.");
        }
    }

    public static void lireopToTextArea(JTextArea area) {
        try (FileInputStream fis = new FileInputStream(TRACE_FILE.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            area.append("operation | opG | opD | resultat\n");
            area.append("=================================\n");

            while (true) {
                Operation op = (Operation) ois.readObject();
                area.append(op.o + " | " + op.g + " | " + op.d + " | " + op.r + "\n");
            }
        } catch (FileNotFoundException e) {
            area.append("No trace available.\n");
        } catch (EOFException e) {
            // End file
        } catch (Exception e) {
            area.append("No trace available.\n");
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
    }
}
