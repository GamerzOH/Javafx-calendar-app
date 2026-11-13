package com.shreyo.calendar;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileStorage {
    private static final String filename = "storage.txt";

    public static void saveTasks(List<Task> tasks) {
        List<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            String etitle = encode(task.getTitle());
            String econtent = encode(task.getContent());
            String epriority = task.getPriority().toString();
            String edate = task.getDate().toString();
            lines.add(etitle + "|" + econtent + "|" + epriority + "|" + edate);
        }
        Path path = Paths.get(filename);

        try {
            Files.write(path, lines);
        } catch (IOException e) {
            System.out.println("Error saving task: " + e.getMessage());
        }
    }

    public static List<Task> loadTasks() {
        List<Task> tasks = new ArrayList<>();
        Path path = Paths.get(filename);

        if (!Files.exists(path)) return tasks;

        try {
            List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                String[] parts = line.split("\\|", -1);
                if (parts.length >= 4) {
                    String title = decode(parts[0]);
                    String content = decode(parts[1]);
                    LayoutHandler.TaskPriority priority = LayoutHandler.TaskPriority.valueOf(parts[2]);
                    LocalDate date = LocalDate.parse(parts[3]);
                    tasks.add(new Task(title, content, priority, date));
                } else throw new IOException("Invalid file format!");
            }
        } catch (IOException e) {
            System.out.println("Error loading task: " + e.getMessage());
        } catch(Exception e) {
            System.out.println("Error loading task: " + e.getMessage());
        }
        return tasks;
    }

    private static String encode(String s) {
        if (s == null) return "";
        return s.replace("|", "<PIPE>")
                .replace("\n\r", "<BR>")
                .replace("\n", "<BR>");
    }

    private static String decode(String s) {
        if (s == null) return "";
        return s.replace("<BR>", "\n")
                .replace("<PIPE>", "|");
    }
}
