package com.example.xiaoji.controller;

import com.example.xiaoji.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ResourceUtils;
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
import java.util.Objects;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${file.upload.filePath}")
    private String filePath;

    /**
     * 文件上传
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        String suffix = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + suffix;
        File srcFile = new File(filePath);
        if (!srcFile.exists()) {
            srcFile.mkdir();
        }
        try {
            file.transferTo(new File(srcFile.getCanonicalPath() + "/" + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }

    /**
     * 文件下载
     */
    @GetMapping("/download")
    public R<String> download(String name, HttpServletResponse response) {
        try {
            FileInputStream inputStream = new FileInputStream(ResourceUtils.getFile("classpath:static").getPath() + filePath + "/" + name);
            ServletOutputStream outputStream = response.getOutputStream();
            int len;
            byte[] bytes = new byte[1024];
            while ((len = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success("下载成功");
    }
}
