package org.example.config;

import org.example.enity.Pojo.Collect;
import org.example.mapper.CollectMapper;
import org.example.mapper.PostMapper;
import org.example.service.CollectService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataPersistenceJob implements Job {

    @Autowired
    private CollectMapper collectMapper; // 替换为你的数据持久化操作的具体实现

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final String COLLECT="collect-";

    public void persistCollects() {
        Set<String> keys = redisTemplate.keys(COLLECT);
        for (String key : keys) {
            System.out.println(key);
            if(key.split(COLLECT).length!=1) {
                System.out.println(key.split(COLLECT).toString());
                Integer postId = Integer.parseInt(key.split(COLLECT)[1]);
                collectMapper.deleteCollectByPostId(postId);
                redisTemplate.opsForSet().members(key).forEach((userId) -> {
                    Collect collect = new Collect().
                            setPostId(postId).setUserId(Integer.parseInt(userId));
                    collectMapper.insertCollect(collect);
                });
                postMapper.updateCollects(postId,redisTemplate.opsForSet()
                        .members(COLLECT+postId.toString()).size());
            }
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        persistCollects();
    }
}
