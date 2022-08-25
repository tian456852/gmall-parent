package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.util.DateUtil;
import com.atguigu.gmall.product.config.minio.MinioProperties;
import com.atguigu.gmall.product.service.FileUploadService;
import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

/**
 * @author tkwrite
 * @create 2022-08-25-18:34
 */
@Service
public class FileUploadServiceImpl implements FileUploadService {
    //app:
    //   minio:
    //     # minio配置的地址，端口9000
    //     endpointUrl: http://192.168.6.66:9000
    //     # 账号
    //     accessKey: admin
    //     # 密码
    //     secreKey: admin123456
    //     # MinIO桶名字
    //     bucketName: gmall
    // @Value("${app.minio.endpointUrl}")
    // String endpointUrl;
    // @Value("${app.minio.accessKey}")
    // String accessKey;
    // @Value("${app.minio.secreKey}")
    // String secreKey;
    // @Value("${app.minio.bucketName}")
    // String bucketName;
@Autowired
    MinioProperties minioProperties;
    @Autowired
    MinioClient minioClient;
    @Override
    public String upload(MultipartFile file) throws Exception {

        //1.创建出一个MinioClient
        // MinioClient minioClient = new MinioClient(
        //         minioProperties.getEndpointUrl(),
        //         minioProperties.getAccessKey(),
        //         minioProperties.getSecreKey()
        // );
        //2.判断桶是否存在
        // boolean gmall = minioClient.bucketExists(minioProperties.getBucketName());
        // if(!gmall){
        // //    桶不存在
        //     minioClient.makeBucket(minioProperties.getBucketName());
        // }
        //3.给桶里上传文件
        //objectName 对象名  上传的文件名
        String name = file.getName();  //input的name名
        //uuid得到一个唯一文件名
        String dateStr = DateUtil.formatDate(new Date());
        String filename = UUID.randomUUID().toString().replace("-","")+"_"+file.getOriginalFilename();//原始文件名
        InputStream inputStream = file.getInputStream();
        String contentType = file.getContentType();
        //文件上传参数
        PutObjectOptions options = new PutObjectOptions(file.getSize(),-1L);
        //默认都是二进制，必须修改成对应的图片等类型
        options.setContentType(contentType);
        //4.文件上传
        minioClient.putObject(
                minioProperties.getBucketName(),
                dateStr+"/"+filename,
                inputStream,
                options
        );
        //5.http://192.168.6.66:9000/gmall/filename
        //上传文件之后的可访问路径

        String url= minioProperties.getEndpointUrl()+"/"+minioProperties.getBucketName()+"/"+dateStr+"/"+filename;

        return url;
    }
//    优化1：文件重名
//    优化2：文件归档：以日期为路径
//
}









