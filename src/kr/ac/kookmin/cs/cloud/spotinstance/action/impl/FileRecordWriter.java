package kr.ac.kookmin.cs.cloud.spotinstance.action.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import kr.ac.kookmin.cs.cloud.spotinstance.action.RecordWriter;

public class FileRecordWriter implements RecordWriter {

    private final File outputFile;

    public FileRecordWriter(String outputPath) {
        outputFile = new File(outputPath);
        try {
            outputFile.createNewFile();
        } catch (IOException e) {
            System.out.println(outputPath + " already exist. Append to the file");
        }
    }

    @Override
    public void write(String message) {
        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write(message);
            writer.flush();
        } catch (IOException e) {
            System.out.println("Fail to write message: " + message);
        }
    }
}
