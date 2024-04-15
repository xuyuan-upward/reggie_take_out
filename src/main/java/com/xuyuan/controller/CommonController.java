package com.xuyuan.controller;

import com.xuyuan.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {
    @Value("${raggie.path}")
    private String basePath;

    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        //file是临时文件，得保存到某个服务器上面
        log.info("上传文件：{}", file);
        String originalFilename = file.getOriginalFilename();
        String Last = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newfileName = UUID.randomUUID() + Last;
        //判断当前存放目录是否存在，不存在就创建
        File fileName = new File(basePath);
        if (!fileName.exists()){
            fileName.mkdirs();
        }

        try {
            //将文件到指定位置
            file.transferTo(new File(basePath+newfileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(newfileName);
    }

    /**
     * 文件下载
     */
    @GetMapping("/download")
    public void download(String name , HttpServletResponse response) throws IOException {
        //读取文件内容
        FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));
        //下载文件内容到浏览器展示
        ServletOutputStream outputStream = response.getOutputStream();

        byte [] bytes = new byte[1024];
        int len = 0;
        while( (len = fileInputStream.read(bytes)) != -1){
            outputStream.write(bytes,0,len);
            outputStream.flush();
        }
        outputStream.close();
        fileInputStream.close();
    }
}
