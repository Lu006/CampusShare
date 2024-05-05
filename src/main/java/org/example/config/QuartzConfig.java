package org.example.config;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    @Autowired
    private DataPersistenceJob dataPersistenceJob; // 注入你的定时任务类

    @Bean
    public JobDetail dataPersistenceJobDetail() {
        return JobBuilder.newJob(DataPersistenceJob.class)
                .withIdentity("dataPersistenceJob")
                .storeDurably()
                .build();
    }

    @Bean
    public SimpleTrigger dataPersistenceJobTrigger() {
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInSeconds(60) // 每隔 60 秒执行一次
                .repeatForever();

        return TriggerBuilder.newTrigger()
                .forJob(dataPersistenceJobDetail())
                .withIdentity("dataPersistenceJobTrigger")
                .withSchedule(scheduleBuilder)
                .build();
    }
}
