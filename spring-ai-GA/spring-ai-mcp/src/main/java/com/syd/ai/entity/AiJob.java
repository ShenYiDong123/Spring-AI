package com.syd.ai.entity;

import lombok.Data;
import java.util.Map;

@Data
public class AiJob {

    public record Job(JobType jobType, Map<String,String> keyInfos) {
    }

    public enum JobType{
        CANCEL,
        QUERY,
        OTHER,
    }
}
