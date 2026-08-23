package com.example.mr;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TopThreeStudents {

    public static class CourseStudent implements WritableComparable<CourseStudent> {
        private String courseName;
        private String studentName;
        private double averageScore;

        public CourseStudent() {}

        public CourseStudent(String courseName, String studentName, double averageScore) {
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
        public int compareTo(CourseStudent o) {
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
            return courseName + " " + studentName + " " + String.format("%.2f", averageScore);
        }
    }

    public static class StudentScoreMapper extends Mapper<Object, Text, Text, Text> {
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

    public static class StudentAverageReducer extends Reducer<Text, Text, CourseStudent, NullWritable> {
        private NullWritable nullValue = NullWritable.get();

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
                context.write(new CourseStudent(courseName, studentName, avg), nullValue);
            }
        }
    }

    public static class TopThreeReducer extends Reducer<CourseStudent, NullWritable, Text, NullWritable> {
        private Text result = new Text();
        private NullWritable nullValue = NullWritable.get();
        private String currentCourse = "";
        private int count = 0;

        public void reduce(CourseStudent key, Iterable<NullWritable> values, Context context) throws IOException, InterruptedException {
            // If we're starting a new course, reset the count
            if (!key.getCourseName().equals(currentCourse)) {
                currentCourse = key.getCourseName();
                count = 0;
            }

            // Only output the top 3 students per course
            if (count < 3) {
                result.set(key.toString());
                context.write(result, nullValue);
                count++;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "top three students");
        job.setJarByClass(TopThreeStudents.class);
        job.setMapperClass(StudentScoreMapper.class);
        job.setReducerClass(StudentAverageReducer.class);
        job.setOutputKeyClass(CourseStudent.class);
        job.setOutputValueClass(NullWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}