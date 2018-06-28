package com.iflytek.edu.oozie;

import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.util.Properties;

import org.apache.hadoop.security.UserGroupInformation;
import org.apache.oozie.client.*;

/**
 * Created with Intellij IDEA.
 * User: ztwu2
 * Date: 2018/6/27
 * Time: 17:40
 * Description java api调用oozie提交任务
 */
public class OozieClientTestDemo {

    public static void main(String[] args) throws Exception {
        UserGroupInformation.getLoginUser().doAs(new PrivilegedExceptionAction(){
            public Void run() throws Exception {
                submitJob();
                return null;
            }
        });
    }

    private static void submitJob() throws OozieClientException, InterruptedException {
        OozieClient wc =new OozieClient("http://ubuntu2:11000/oozie/");
        try {
            System.out.println(UserGroupInformation.getLoginUser());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Properties conf = wc.createConfiguration();
        conf.setProperty(OozieClient.APP_PATH, "hdfs://ubuntu1:9000/user/root/examples/apps/map-reduce/workflow.xml");
        //8021端口是mr1默认的jobtracker，
        //8032端口是mr2默认的jobtracker，（resourcemanager）
        conf.setProperty("jobTracker", "ubuntu1:8032");
        conf.setProperty("nameNode", "hdfs://ubuntu1:9000");
        conf.setProperty("examplesRoot", "examples");
        conf.setProperty("queueName", "default");
        conf.setProperty("outputDir", "map-reduce");
        conf.setProperty("user.name", "root");

        String jobId = wc.run(conf);
        System.out.println("Workflow job submitted");

        while (wc.getJobInfo(jobId).getStatus() == WorkflowJob.Status.RUNNING) {
            System.out.println("Workflow job running ...");
            Thread.sleep(10 * 1000);
        }

        System.out.println("Workflow job completed ...");
        System.out.println(wc.getJobInfo(jobId));
    }
}

