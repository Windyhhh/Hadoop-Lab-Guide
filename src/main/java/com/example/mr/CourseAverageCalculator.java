package com.example.mr;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.StringTokenizer;

public class CourseAverageCalculator {

    public static class CourseAverageMapper extends Mapper<Object, Text, Text, Text> {
        private Text courseName = new Text();
        private Text scoreInfo = new Text();

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            String[] parts = line.split(",");
            if (parts.length < 3) {
                return; // Skip invalid lines
            }

            courseName.set(parts[0]);
            int totalScore = 0;
            int count = 0;

            for (int i = 2; i < parts.length; i++) {
                try {
                    int score = Integer.parseInt(parts[i].trim());
                    totalScore += score;
                    count++;
                } catch (NumberFormatException e) {
                    // Skip invalid scores
                }
            }

            if (count > 0) {
                scoreInfo.set(totalScore + "," + count);
                context.write(courseName, scoreInfo);
            }
        }
    }

    public static class CourseAverageReducer extends Reducer<Text, Text, Text, Text> {
        private Text result = new Text();

        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            int totalScore = 0;
            int totalCount = 0;

            for (Text val : values) {
                String[] parts = val.toString().split(",");
                if (parts.length == 2) {
                    totalScore += Integer.parseInt(parts[0]);
                    totalCount += Integer.parseInt(parts[1]);
                }
            }

            if (totalCount > 0) {
                double average = (double) totalScore / totalCount;
                String formattedResult = String.format("%d %.2f", totalCount, average);
                result.set(formattedResult);
                context.write(key, result);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "course average calculator");
        job.setJarByClass(CourseAverageCalculator.class);
        job.setMapperClass(CourseAverageMapper.class);
        job.setReducerClass(CourseAverageReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}