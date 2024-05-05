package org.example.controller;

import org.example.result.Result;
import org.example.utils.FilePathUtils;
import org.example.utils.RandomUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@RestController
public class UploadController {

    //临时文件上传接口，返回临时文件路径，若确定上传，在具体的接口会利用文件工具类直接将临时文件转移到文件持久化具体路径
    @PostMapping("/upload")
    public Result uploadTmpFile(@RequestParam("file") MultipartFile file) {
        try {
            Map<String,String> tmpPaths=FilePathUtils.generateTmpPath(file.getOriginalFilename());
            file.transferTo(Paths.get(tmpPaths.get("tmpPath")));
            String tmpUrl=tmpPaths.get("tmpUrl");
            return Result.success(tmpUrl).setMsg("上传文件成功");
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error().setMsg("文件上传失败");
        }
    }



}
