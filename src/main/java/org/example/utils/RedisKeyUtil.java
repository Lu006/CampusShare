package org.example.utils;

public class RedisKeyUtil {
    private static final String SPLIT=":";
    private static final String PREFIX_COLLECTOR = "collection";

//        postId:1:collector:
//    收藏者列表
    public static String getCollectorKey(int postId) {
        return "postId" + SPLIT + postId + SPLIT + "collector";
    }

    public static String getThumberKey(int commentId) {
        return "commentId" + SPLIT + commentId + SPLIT + "thumber";
    }

    // 关注列表
    public static String getFolloweeKey(int userId) {
        return "userId" + SPLIT + userId + SPLIT + "followee";
    }

    // 粉丝列表
    public static String getFollowerKey(int userId) {
        return "userId" + SPLIT + userId + SPLIT + "follower";
    }
    public static String getChatBoxKey(int userId) {
        return "ChatBoxOwner" + SPLIT + userId + SPLIT + "Msg";
    }



}
