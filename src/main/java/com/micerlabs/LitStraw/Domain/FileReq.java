package com.micerlabs.LitStraw.Domain;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;


@Data
public class FileReq {
    // 参数
    private Integer config;
    // 文件
    private MultipartFile file;

    public FileReq(){}
}
