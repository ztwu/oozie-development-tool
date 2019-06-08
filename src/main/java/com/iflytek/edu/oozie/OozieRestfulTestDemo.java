package com.iflytek.edu.oozie;

import com.iflytek.edu.util.HttpUtil;
import sun.misc.BASE64Encoder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

/**
 * Created with Intellij IDEA.
 * User: ztwu2
 * Date: 2018/6/28
 * Time: 14:29
 * Description restful api调用提交任务
 */

public class OozieRestfulTestDemo {

    private static String user;
    private static String passwd;

    static {
        Properties properties = new Properties();
        InputStream in = null;
        try {
            in = OozieRestfulTestDemo.class.getResourceAsStream("/authentication.properties");
            properties.load(in);
            user = properties.getProperty("odeon.user");
            passwd = properties.getProperty("odeon.passwd");
            MessageDigest md5= MessageDigest.getInstance("MD5");
            BASE64Encoder base64en = new BASE64Encoder();
            passwd = base64en.encode(md5.digest(passwd.getBytes("utf-8")));
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
//        getWorkflowInfo("0000004-180627220033949-oozie-root-W");
//        getWorkflowXML("0000004-180627220033949-oozie-root-W");
        //注意需要开启hadoop的jobhistory进程10020
        submitWorkflow();
    }

    private static void getWorkflowInfo(String jobId){
        String para = "http://ubuntu2:11000/oozie/v2/job/" + jobId +
                "?show=info&len=1&order=desc" +
                "&user=" + user +
                "&passwd="+passwd;
        String result = HttpUtil.doGet(para);
        System.out.println(result);
    }

    private static void getWorkflowXML(String jobId){
        String para = "http://ubuntu2:11000/oozie/v2/job/" + jobId +
                "?show=definition&len=1&order=desc" +
                "&user=" + user +
                "&passwd="+passwd;
        String result = HttpUtil.doGet(para);
        System.out.println(result);
    }

    private static void submitWorkflow(){
        String data = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<configuration>\n" +
                "  <property>\n" +
                "    <name>user.name</name>\n" +
                "    <value>root</value>\n" +
                "  </property>\n" +
                "  <property>\n" +
                "    <name>queueName</name>\n" +
                "    <value>default</value>\n" +
                "  </property>\n" +
                "  <property>\n" +
                "    <name>nameNode</name>\n" +
                "    <value>hdfs://ubuntu1:9000</value>\n" +
                "  </property>\n" +
                "  <property>\n" +
                "    <name>jobTracker</name>\n" +
                "    <value>ubuntu1:8032</value>\n" +
                "  </property>\n" +
                "  <property>\n" +
                "    <name>examplesRoot</name>\n" +
                "    <value>examples</value>\n" +
                "  </property>\n" +
                "  <property>\n" +
                "    <name>outputDir</name>\n" +
                "    <value>map-reduce</value>\n" +
                "  </property>\n" +
                "  <property>\n" +
                "    <name>oozie.wf.application.path</name>\n" +
                "    <value>hdfs://ubuntu1:9000/user/root/examples/apps/map-reduce/workflow.xml</value>\n" +
                "  </property>\n" +
                "</configuration>";
        try {
            //创建的作业将处于PREP状态。 如果在POST URL中提供了查询字符串参数'action = start'，
            // 则该作业将立即开始并且其状态将为RUNNING
            String result = HttpUtil.doPost("http://ubuntu2:11000/oozie/v1/jobs?action=start",data);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


