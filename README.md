<div align="center">

# Hadoop 实验指南 | Hadoop-Lab-Guide

### Hadoop experiments — HDFS management & MapReduce analytics.

Directory scanning with small-file merging, plus multi-dimensional MapReduce statistics — a hands-on Hadoop lab.

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Hadoop](https://img.shields.io/badge/Hadoop-3-66CCFF?logo=apachehadoop&logoColor=white)](https://hadoop.apache.org/)
[![MapReduce](https://img.shields.io/badge/MapReduce-3-FF6F00)](https://hadoop.apache.org/docs/current/hadoop-mapreduce-client/)

</div>

---

**Hadoop-Lab-Guide** is a hands-on Hadoop lab covering two core experiments: **HDFS directory scanning with small-file merging**, and **multi-dimensional MapReduce statistics** — with performance-focused implementations and full documentation.

> [!NOTE]
> 中文项目：Hadoop 实验指南——HDFS 目录扫描与小文件合并 + MapReduce 多维度统计分析。

---

## Features

- **HDFS management** — directory scanning and small-file merging.
- **MapReduce analytics** — multi-dimensional statistical analysis.
- **Performance** — 1M student-score records, per-course average in ~1.2 min.
- **Modular & reusable** — easy extension, complete scenario coverage.

---

## Quickstart

```bash
git clone https://github.com/Windyhhh/Hadoop-Lab-Guide.git
cd Hadoop-Lab-Guide

# put data on HDFS and run the MapReduce job
hdfs dfs -put data /input
hadoop jar target/lab.jar com.example.ScoreAvg /input /output
hdfs dfs -cat /output/part-r-00000
```

---

## Project Structure

```
Hadoop-Lab-Guide/
├── src/                    # MapReduce mappers / reducers
├── data/                   # sample datasets
├── scripts/                # HDFS + job scripts
└── docs/                   # lab guide, blog
```

---

## License

MIT — free to use, modify and distribute.
