package org.example.utils;
import org.example.enity.Pojo.Collect;
import org.example.mapper.CollectMapper;
import org.example.mapper.PostMapper;
import org.example.mapper.UserMapper;
import org.example.service.CollectService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Component
@SpringBootTest
@RunWith(SpringRunner.class)
public class IBCFRecommender {
    @Autowired
    private CollectMapper collectMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserMapper userMapper;

    private double[][] similarityMatrix;

    private Integer maxUserId;

    private Integer maxItemId;

    @PostConstruct
    public void init(){
        maxItemId=postMapper.selectMaxPostId();
        maxUserId=userMapper.selectMaxUserId();
        if(maxItemId==null) maxItemId=0;
        if(maxUserId==null) maxUserId=0;
        this.update();
    }

    public List<Integer> recommend(Integer postId,Integer count){
        double[] similarityArray=this.similarityMatrix[postId];
        List<Integer> recommendPostIds = IntStream.range(0, similarityArray.length).boxed()
                .collect(Collectors.toMap(i -> i, i -> similarityArray[i]))
                .entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .filter(entry->entry.getValue()!=0.0)
                .limit(count).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                .keySet().stream().collect(Collectors.toList());
        return recommendPostIds;
    }

    @Test
    public void update() {
        List<Collect> _collects=collectMapper.selectAllCollects();
        int[][] collects=new int[_collects.size()][3];
        for(int i=0;i<_collects.size();i++){
            collects[i]=(new int[]{_collects.get(i).getPostId(), _collects.get(i).getUserId(), 1});
        }

        int[][] userItemArray = transformToUserItemArray(collects);
        this.similarityMatrix=transformToSimilarityMatrix(userItemArray);


        int numItems=similarityMatrix.length;
        // 输出结果
        for (int i = 0; i < numItems; i++) {
            for (int j = 0; j < numItems; j++) {
                System.out.print(similarityMatrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    public double[][] transformToSimilarityMatrix(int[][] userItemArray){
        int numItems=userItemArray[0].length;
        // 计算相似度矩阵
        double[][] similarityMatrix = new double[numItems][numItems];

        for (int i = 0; i < numItems; i++) {
            for (int j = 0; j < numItems; j++) {
                if(i==j){
                    similarityMatrix[i][j]=0;
                    continue;
                }
                int[] itemA = getColumn(userItemArray, i);
                int[] itemB = getColumn(userItemArray, j);
                double similarity = calculateCosineSimilarity(itemA, itemB);
                similarityMatrix[i][j] = similarity;
            }
        }
        return similarityMatrix;
    }

    public int[][] transformToUserItemArray(int[][] likes){
        int[][] userItemArray=new int[maxUserId+1][maxItemId+1];
        for(int[] like:likes){
            userItemArray[like[1]][like[0]]=like[2];
        }
        return userItemArray;
    }

    // 获取矩阵的某一列
    private static int[] getColumn(int[][] matrix, int col) {
        int[] column = new int[matrix.length];

        for (int i = 0; i < matrix.length; i++) {
            column[i] = matrix[i][col];
        }
        return column;
    }

    // 计算余弦相似度
    private static double calculateCosineSimilarity(int[] vectorA, int[] vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += vectorA[i] * vectorA[i];
            normB += vectorB[i] * vectorB[i];
        }

        if (normA == 0.0 || normB == 0.0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

}
