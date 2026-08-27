<div align="center">

# 🐘 Hadoop-Lab-Guide

### Hands-on Hadoop labs — MapReduce, HDFS and Hive.

Practical Java implementations for Hadoop experiments, from HDFS file ops to MapReduce aggregation.

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-8+-007396?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Hadoop](https://img.shields.io/badge/Hadoop-3-66CCFF?logo=apachehadoop&logoColor=black)](https://hadoop.apache.org/)

</div>

---

**Hadoop-Lab-Guide** contains hands-on Java implementations for Hadoop experiments — **HDFS** file operations and **MapReduce** jobs for course-data aggregation and ranking.

> [!NOTE]
> 中文项目：Hadoop 实验项目实战——MapReduce、HDFS、Hive 实验。

---

## Quickstart

```bash
git clone https://github.com/Windyhhh/Hadoop-Lab-Guide.git
cd Hadoop-Lab-Guide

mvn clean package

# Run an HDFS job
hadoop jar target/*.jar com.example.hdfs.HDFSScanner /input

# Run a MapReduce aggregation
hadoop jar target/*.jar com.example.mr.CourseAverageCalculator /input /output
```

---

## Features

- **HDFS ops** — `HDFSFileMerger`, `HDFSScanner`.
- **MapReduce aggregation** — course averages, student averages, top-three students.
- **Report-ready** — includes experiment reports.

---

## Project Structure

```
Hadoop-Lab-Guide/
├── src/main/java/com/example/
│   ├── hdfs/            # HDFSFileMerger, HDFSScanner
│   └── mr/              # CourseAverageCalculator, StudentCourseAverage, TopThreeStudents
├── pom.xml
└── README.md
```

---

## License

MIT — free to use, modify and distribute.
