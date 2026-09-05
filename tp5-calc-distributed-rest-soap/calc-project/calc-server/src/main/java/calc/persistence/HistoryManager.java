package calc.persistence;

import calc.model.HistoryRecord;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class HistoryManager {

    private static final String HISTORY_FILE = "history.txt";
    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static HistoryManager instance;

    private int nextId;

    private HistoryManager() {
        File file = new File(HISTORY_FILE);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Unable to create history file", e);
            }
            this.nextId = 1;
        } else {
            int count = 0;
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                while (reader.readLine() != null) {
                    count++;
                }
            } catch (IOException e) {
                throw new RuntimeException("Unable to read history file", e);
            }
            this.nextId = count + 1;
        }
    }

    public static synchronized HistoryManager getInstance() {
        if (instance == null) {
            instance = new HistoryManager();
        }
        return instance;
    }

    public synchronized void save(double a,
                                  String operator,
                                  double b,
                                  double result,
                                  String via) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        HistoryRecord record = new HistoryRecord(
                nextId++,
                timestamp,
                a,
                operator,
                b,
                result,
                via
        );
        try (FileWriter writer = new FileWriter(HISTORY_FILE, true)) {
            writer.write(record.toFileLine());
            writer.write(System.lineSeparator());
        } catch (IOException e) {
            throw new RuntimeException("Unable to write history file", e);
        }
    }

    public synchronized List<HistoryRecord> getAll() {
        File file = new File(HISTORY_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        List<HistoryRecord> records = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }
                HistoryRecord record = HistoryRecord.fromFileLine(line);
                if (record != null) {
                    records.add(record);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to read history file", e);
        }
        return Collections.unmodifiableList(records);
    }
}
