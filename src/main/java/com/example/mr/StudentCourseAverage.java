package com.example.mr;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StudentCourseAverage {

    public static class StudentCourseKey implements WritableComparable<StudentCourseKey> {
        private String courseName;
        private String studentName;
        private double averageScore;

        public StudentCourseKey() {}

        public StudentCourseKey(String courseName, String studentName, double averageScore) {
            this.courseName = courseName;
            this.studentName = studentName;
            this.averageScore = averageScore;
        }

        public String getCourseName() {
            return courseName;
        }

        public String getStudentName() {
            return studentName;
        }

        public double getAverageScore() {
            return averageScore;
        }

        @Override
        public void write(DataOutput out) throws IOException {
            out.writeUTF(courseName);
            out.writeUTF(studentName);
            out.writeDouble(averageScore);
        }

        @Override
        public void readFields(DataInput in) throws IOException {
            courseName = in.readUTF();
            studentName = in.readUTF();
            averageScore = in.readDouble();
        }

        @Override
        public int compareTo(StudentCourseKey o) {
            // First compare by course name
            int courseCompare = this.courseName.compareTo(o.courseName);
            if (courseCompare != 0) {
                return courseCompare;
            }
            // Then compare by average score in descending order
            return Double.compare(o.averageScore, this.averageScore);
        }

        @Override
        public String toString() {
            return courseName + "\t" + studentName;
        }
    }

    public static class CoursePartitioner extends Partitioner<StudentCourseKey, DoubleWritable> {
        private static Map<String, Integer> courseToPartition = new HashMap<>();
        private static int nextPartition = 0;

        @Override
        public int getPartition(StudentCourseKey key, DoubleWritable value, int numPartitions) {
            String course = key.getCourseName();
            return courseToPartition.computeIfAbsent(course, k -> nextPartition++ % numPartitions);
        }
    }

    public static class StudentCourseAverageMapper extends Mapper<Object, Text, Text, Text> {
        private Text courseStudentKey = new Text();
        private Text scoreInfo = new Text();

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            String[] parts = line.split(",");
            if (parts.length < 3) {
                return; // Skip invalid lines
            }

            String courseName = parts[0];
            String studentName = parts[1];
            courseStudentKey.set(courseName + "," + studentName);

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
                context.write(courseStudentKey, scoreInfo);
            }
        }
    }

    public static class StudentCourseAverageReducer extends Reducer<Text, Text, StudentCourseKey, DoubleWritable> {
        private DoubleWritable averageScore = new DoubleWritable();

        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            String[] parts = key.toString().split(",");
            if (parts.length != 2) {
                return;
            }

            String courseName = parts[0];
            String studentName = parts[1];

            int totalScore = 0;
            int totalCount = 0;

            for (Text val : values) {
                String[] scoreParts = val.toString().split(",");
                if (scoreParts.length == 2) {
                    totalScore += Integer.parseInt(scoreParts[0]);
                    totalCount += Integer.parseInt(scoreParts[1]);
                }
            }

            if (totalCount > 0) {
                double avg = (double) totalScore / totalCount;
                averageScore.set(avg);
                context.write(new StudentCourseKey(courseName, studentName, avg), averageScore);
            }
        }
    }

    public static class StudentCourseAverageFinalReducer extends Reducer<StudentCourseKey, DoubleWritable, Text, Text> {
        private Text result = new Text();
        private Text studentName = new Text();

        public void reduce(StudentCourseKey key, Iterable<DoubleWritable> values, Context context) throws IOException, InterruptedException {
            // Since we're grouping by StudentCourseKey, there should be only one value
            for (DoubleWritable val : values) {
                studentName.set(key.getStudentName());
                result.set(String.format("%.2f", val.get()));
                context.write(studentName, result);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "student course average");
        job.setJarByClass(StudentCourseAverage.class);
        job.setMapperClass(StudentCourseAverageMapper.class);
        job.setReducerClass(StudentCourseAverageReducer.class);
        job.setOutputKeyClass(StudentCourseKey.class);
        job.setOutputValueClass(DoubleWritable.class);
        job.setPartitionerClass(CoursePartitioner.class);
        job.setNumReduceTasks(10); // Set enough reducers for expected number of courses
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}