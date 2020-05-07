package com.shenyy.yuan.util;

import io.minio.MinioClient;
import io.minio.errors.MinioException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class MinioUtil {
    private static final Logger log = LoggerFactory.getLogger(MinioUtil.class);

    public static void main(String[] args) {
        try {
            MinioClient minioClient = new MinioClient("https://docs.min.io/docs/java-client-quickstart-guide", "VDRYFL9EQR9ST9KAA772", "IGY+vmDfLes4CWBI6fCyubkLH3AbYX5PiQMTjZmH",true);

            // 检查存储桶是否已经存在
            if(minioClient.bucketExists("test1")) {
                log.info("Bucket already exists.");
            } else {
                // 创建一个名为ota的存储桶
                minioClient.makeBucket("test1");
                log.info("create a new bucket.");
            }

            //获取下载文件的url，直接点击该url即可在浏览器中下载文件
           /* String url = minioClient.presignedGetObject("test1","hello.txt");
            log.info(url);

            //获取上传文件的url，这个url可以用Postman工具测试，在body里放入需要上传的文件即可
            String url2 = minioClient.presignedPutObject("test1","ubuntu.tar");
            log.info(url2);

            // 下载文件到本地
            minioClient.getObject("test1","hello.txt", "/Users/hbl/hello2.txt");
            log.info("get");

            // 使用putObject上传一个本地文件到存储桶中。
            minioClient.putObject("test1","tenant2/hello.txt", "/Users/hbl/hello.txt");
            log.info("/Users/hbl/hello.txt is successfully uploaded as hello.txt to `task1` bucket.");*/
            File file = new File("E://test.gif");
            InputStream inputStream = new FileInputStream(file);
            minioClient.putObject("test1", "test.gif", inputStream, inputStream.available(), "application/octet-stream");
        } catch(MinioException e) {
            log.error("Error occurred: " + e);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
