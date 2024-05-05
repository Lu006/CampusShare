package org.example.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FilePathUtils {
    private static final String localUploadPath=System.getProperty("user.dir").replaceAll("\\\\","\\\\\\\\")+"\\\\";
    private static final String domain="http://192.168.1.3:8080/";
    private static final String tmp_dir="tmp/";
    private static final String posts_dir="upload/posts/";
    private static final String avatar_dir="upload/avatars/";
    public static Map generateTmpPath(String orignFileName){
        String filename=String.valueOf(System.currentTimeMillis())+ RandomUtils.generateRandomString(12);
        String fileExtension = orignFileName.substring(orignFileName.lastIndexOf("."));
        String tmpPath=localUploadPath+tmp_dir+filename+fileExtension;
        String tmpUrl=domain+tmp_dir+filename+fileExtension;
        Map map=new HashMap<String,String>();
        map.put("tmpPath",tmpPath);
        map.put("tmpUrl",tmpUrl);
        return map;
    }
    public static String transferTmpUrlToPostDir(String tmpUrl) throws IOException {
        // http://192.168.1.3:8080/tmp/1703602635957DcsYYCNewFbB.jpg
        String mediaUrl = tmpUrl.replaceFirst(tmp_dir, posts_dir);
        String tmpPath = tmpUrl.replaceFirst(domain,localUploadPath)
                .replaceAll("/","\\\\");
        String postsPath = tmpUrl.replaceFirst(domain, localUploadPath)
                .replaceFirst(tmp_dir, posts_dir).replaceAll("/","\\\\");
        Files.copy(Paths.get(tmpPath),Paths.get(postsPath), StandardCopyOption.REPLACE_EXISTING);
        System.out.println("transfer one finish: "+ mediaUrl);
        return mediaUrl;
    }

    public static String transferTmpUrlToAvatarDir(String tmpUrl) throws IOException {
        // http://192.168.1.3:8080/tmp/1703602635957DcsYYCNewFbB.jpg
        String mediaUrl = tmpUrl.replaceFirst(tmp_dir, avatar_dir);
        String tmpPath = tmpUrl.replaceFirst(domain,localUploadPath).replaceAll("/","\\\\");
        String avatarPath = tmpUrl.replaceFirst(domain, localUploadPath).replaceFirst(tmp_dir, avatar_dir).replaceAll("/","\\\\");
        Files.copy(Paths.get(tmpPath),Paths.get(avatarPath), StandardCopyOption.REPLACE_EXISTING);
        System.out.println("transfer one finish: "+ mediaUrl);
        return mediaUrl;
    }


}
