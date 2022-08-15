package com.mkn.controller;

import com.mkn.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.UUID;

/**
 * @author ：mkn
 * @date ：Created in 2022/8/8 9:36
 * @description：文件的上传和下载
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        log.info(file.toString());

        String originalFilename = file.getOriginalFilename();
        assert originalFilename != null;
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //判断目录是否存在
        File dir = new File(basePath);
        if (!dir.exists()){
            dir.mkdirs();
        }

        //使用UUID重新生成文件名称,防止文件名重复造成的文件覆盖
        String fileName = UUID.randomUUID().toString()+ suffix;
        try {
            //将临时文件转存到指定位置
            file.transferTo(new File(basePath+fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }

    /**
     * 文件下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        //输入流,通过输入流读取文件内容

        try(//输入流,通过输入流读取文件
            FileInputStream fileInputStream = new FileInputStream(basePath + name);
            //输出流,通过输出流将文件写回浏览器,在浏览器展示图片了
            ServletOutputStream outputStream = response.getOutputStream()) {

            response.setContentType("image/jpeg");

            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        //输出流,通过输出流将文件写回浏览器,在浏览器展示图片
    }
}
