package com.example.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HDFSScanner {
    private static final String INDENT = "    ";

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: HDFSScanner <HDFS directory path>");
            System.exit(1);
        }

        String path = args[0];
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);

        System.out.println(path + "/");
        scanDirectory(fs, new Path(path), 0);

        fs.close();
    }

    private static void scanDirectory(FileSystem fs, Path path, int level) throws IOException {
        RemoteIterator<LocatedFileStatus> iterator = fs.listFiles(path, false);
        List<LocatedFileStatus> files = new ArrayList<>();
        List<Path> directories = new ArrayList<>();

        // Separate files and directories
        while (iterator.hasNext()) {
            LocatedFileStatus status = iterator.next();
            if (status.isDirectory()) {
                directories.add(status.getPath());
            } else {
                files.add(status);
            }
        }

        // Process files
        for (LocatedFileStatus file : files) {
            printFileInfo(file, level);
        }

        // Process directories
        for (Path dir : directories) {
            printDirectoryInfo(dir, level);
            scanDirectory(fs, dir, level + 1);
        }
    }

    private static void printFileInfo(LocatedFileStatus file, int level) throws IOException {
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < level; i++) {
            indent.append(INDENT);
        }

        String fileName = file.getPath().getName();
        long size = file.getLen();
        int blockCount = file.getBlockLocations().length;
        short replication = file.getReplication();

        System.out.printf("%s|-- %s %d %d %d%n", indent, fileName, size, blockCount, replication);
    }

    private static void printDirectoryInfo(Path dir, int level) {
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < level; i++) {
            indent.append(INDENT);
        }

        String dirName = dir.getName();
        System.out.printf("%s|-- %s/%n", indent, dirName);
    }
}