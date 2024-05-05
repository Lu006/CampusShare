package org.example.service;

import org.example.enity.Pojo.Collect;
import org.example.mapper.CollectMapper;
import org.example.mapper.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollectService {

    @Autowired
    private CollectMapper collectMapper;

    @Autowired
    private PostMapper postMapper;

//    public boolean saveCollect(Collect collect){
//        if(collectMapper.existCollectByPostIdAndUserId(collect.getPostId(), collect.getUserId())){
//            collectMapper.updateCollectStatus(collect);
//        }else{
//            collectMapper.insertCollect(collect);
//        }
//        postMapper.addCollects(collect.getPostId());
//        return true;
//    }
//
//    public boolean cancelCollect(Collect collect){
//        collectMapper.updateCollectStatus(collect);
//        postMapper.subCollects(collect.getPostId());
//        return true;
//    }

    public Boolean cancelPostCollect(Integer postId){
        return collectMapper.deleteCollectByPostId(postId);
    }

    public List selectUserIdsByPostId(Integer postId){
        List<Integer> userIds = collectMapper.selectUserIdsByPostId(postId);
        return userIds;
    }
}
