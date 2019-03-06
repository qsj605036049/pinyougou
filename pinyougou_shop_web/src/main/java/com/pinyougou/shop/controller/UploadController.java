package com.pinyougou.shop.controller;

import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import utils.FastDFSClient;

/**
 * @author qinshiji
 * @data 2019/1/18 15:54
 */
@RestController
@RequestMapping("/upload")
public class UploadController {
    @Value("${FILE_SERVER_URL}")
    private  String FILE_SERVER_URL;
    @RequestMapping("/uploadFile")
    public Result uploadFile(MultipartFile file){
        String filename = file.getOriginalFilename();
        String extName = filename.substring(filename.indexOf(".")+1);

        try {
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fdfs_client.properties");
            String s = fastDFSClient.uploadFile(file.getBytes(), extName);
            String url = FILE_SERVER_URL + s;
            return new Result(true, url);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败" );
        }


    }
}
