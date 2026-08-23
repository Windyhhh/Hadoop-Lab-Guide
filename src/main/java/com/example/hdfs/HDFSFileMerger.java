package com.example.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HDFSFileMerger {
    private static final long SMALL_FILE_THRESHOLD = 1024; // 1KB
    private static final long MAX_MERGED_FILE_SIZE = 20480; // 20KB

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: HDFSFileMerger <HDFS directory path>");
            System.exit(1);
        }

        String path = args[0];
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);

        mergeSmallFiles(fs, new Path(path));
        fs.close();
    }

    private static void mergeSmallFiles(FileSystem fs, Path directory) throws IOException {
        List<LocatedFileStatus> smallFiles = new ArrayList<>();
        RemoteIterator<LocatedFileStatus> iterator = fs.listFiles(directory, true);

        // Collect all small files
        while (iterator.hasNext()) {
            LocatedFileStatus file = iterator.next();
            if (!file.isDirectory() && file.getLen() < SMALL_FILE_THRESHOLD) {
                smallFiles.add(file);
            }
        }

        if (smallFiles.isEmpty()) {
            System.out.println("No small files found to merge.");
            return;
        }

        System.out.printf("Found %d small files to merge.%n", smallFiles.size());

        // Merge small files
        int mergedFileCount = 0;
        long currentSize = 0;
        List<LocatedFileStatus> currentBatch = new ArrayList<>();

        for (LocatedFileStatus file : smallFiles) {
            if (currentSize + file.getLen() > MAX_MERGED_FILE_SIZE && !currentBatch.isEmpty()) {
                createMergedFile(fs, directory, currentBatch, mergedFileCount++);
                currentBatch.clear();
                currentSize = 0;
            }
            currentBatch.add(file);
            currentSize += file.getLen();
        }

        // Create the last merged file if there are remaining files
        if (!currentBatch.isEmpty()) {
            createMergedFile(fs, directory, currentBatch, mergedFileCount++);
        }

        System.out.printf("Successfully merged into %d files.%n", mergedFileCount);
    }

    private static void createMergedFile(FileSystem fs, Path directory, List<LocatedFileStatus> files, int batchIndex) throws IOException {
        String mergedFileName = String.format("merged_file_%d_%d.txt", batchIndex, files.size());
        Path mergedFilePath = new Path(directory, mergedFileName);
        FSDataOutputStream out = fs.create(mergedFilePath);

        try {
            for (LocatedFileStatus file : files) {
                Path filePath = file.getPath();
                FSDataInputStream in = fs.open(filePath);
                try {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) > 0) {
                        out.write(buffer, 0, bytesRead);
                    }
                    out.writeBytes("\n"); // Add a newline between files
                } finally {
                    in.close();
                }
            }
        } finally {
            out.close();
        }

        // Delete the small files after merging
        for (LocatedFileStatus file : files) {
            fs.delete(file.getPath(), false);
        }

        System.out.printf("Created merged file: %s with %d small files.%n", mergedFileName, files.size());
    }
}