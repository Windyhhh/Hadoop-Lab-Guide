# 🐘 Hadoop 实验指南 | Hadoop Lab Guide

> **Hadoop 实验项目实战指南——MapReduce、HDFS、Hive 实验全覆盖，从环境搭建到完整实验，大数据入门必备。**
>
> *Practical Hadoop lab project guide — full coverage of MapReduce, HDFS, Hive experiments, from environment setup to complete labs, essential for big data beginners.*

---

## ⭐ 核心卖点 | Why Star This

| 卖点 | Feature | 一句话 |
|------|---------|--------|
| 🐘 **Hadoop 全覆盖** | Full Coverage | MapReduce、HDFS、Hive 实验齐全 |
| 🛠️ **环境搭建** | Setup Guide | 手把手环境搭建教程 |
| 📋 **实验手册** | Lab Manual | 完整实验步骤与代码 |
| 🔍 **原理详解** | Principles | 核心原理通俗讲解 |
| 🎓 **入门必备** | Beginner Friendly | 大数据入门学习指南 |

---

## 🏆 技术栈 | Tech Stack

![Hadoop](https://img.shields.io/badge/Hadoop-3.0+-yellow?logo=apachehadoop)
![MapReduce](https://img.shields.io/badge/MapReduce-3.0+-blue?logo=apachehadoop)
![HDFS](https://img.shields.io/badge/HDFS-3.0+-blue?logo=apachehadoop)
![Hive](https://img.shields.io/badge/Hive-3.0+-blue?logo=apachehive)
![Java](https://img.shields.io/badge/Java-8+-orange?logo=openjdk)
![Docker](https://img.shields.io/badge/Docker-24.0+-blue?logo=docker)

---

## 📚 实验目录 | Lab List

| 实验 | 主题 | 核心内容 |
|------|------|---------|
| Lab 1 | 环境搭建 | Hadoop 集群安装配置 |
| Lab 2 | HDFS 操作 | 文件系统命令、API |
| Lab 3 | MapReduce 入门 | WordCount 实验 |
| Lab 4 | MapReduce 进阶 | 排序、聚合、连接 |
| Lab 5 | Hive 建表 | DDL、数据导入 |
| Lab 6 | Hive 查询 | HQL 分析查询 |
| Lab 7 | 综合项目 | 日志分析实战 |

---

## 🚀 快速开始 | Quick Start

```bash
git clone https://github.com/Windyhhh/Hadoop-Lab-Guide.git
cd Hadoop-Lab-Guide

# 1. Docker 搭建 Hadoop 环境
docker-compose up -d

# 2. 进入 Hadoop 容器
docker exec -it hadoop-namenode bash

# 3. 运行 WordCount 实验
cd /opt/labs/lab3-wordcount
hadoop jar wordcount.jar WordCount /input /output/wordcount

# 4. 查看结果
hdfs dfs -cat /output/wordcount/part-r-00000
```

---

## 📂 项目结构 | Project Structure

```
Hadoop-Lab-Guide/
├── labs/                      # 实验目录
│   ├── lab1-setup/           # 环境搭建
│   ├── lab2-hdfs/            # HDFS 操作
│   ├── lab3-wordcount/       # MapReduce 入门
│   ├── lab4-mapreduce/       # MapReduce 进阶
│   ├── lab5-hive-ddl/        # Hive 建表
│   ├── lab6-hive-query/      # Hive 查询
│   └── lab7-project/         # 综合项目
├── docs/                      # 文档
│   ├── setup-guide.md        # 环境搭建指南
│   ├── hadoop-principles.md  # 原理讲解
│   └── troubleshooting.md    # 常见问题
├── docker-compose.yml        # 集群编排
└── README.md
```

---

## 🔬 核心实验示例 | Sample Lab

### MapReduce WordCount | Word Count

```java
// WordCount Mapper
public class WordCountMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
    private final static IntWritable one = new IntWritable(1);
    private Text word = new Text();
    
    @Override
    protected void map(LongWritable key, Text value, Context context) 
            throws IOException, InterruptedException {
        String[] words = value.toString().split("\\s+");
        for (String w : words) {
            word.set(w);
            context.write(word, one);
        }
    }
}

// WordCount Reducer
public class WordCountReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context)
            throws IOException, InterruptedException {
        int sum = 0;
        for (IntWritable val : values) sum += val.get();
        context.write(key, new IntWritable(sum));
    }
}
```

---

## 🎯 应用场景 | Use Cases

- 🎓 **学习入门**：Hadoop 大数据入门
- 🏫 **课程实验**：大数据课程实验
- 🧪 **技术储备**：大数据技能提升
- 📚 **自学资料**：Hadoop 自学手册

---

## 📄 License

MIT License — 自由使用、修改和分发。

---

> 💡 **Hadoop 实验实战指南，Star ⭐ 大数据入门必备！**
