package com.leadexchange.util;

import org.springframework.util.StringUtils;

/**
 * 编辑距离工具类
 * 用于计算两个字符串之间的编辑距离（Levenshtein距离）
 * 主要用于线索重复检测
 * 
 * @author AI Assistant
 * @since 2024
 */
public class EditDistanceUtil {
    
    /**
     * 计算两个字符串的编辑距离
     * 使用动态规划算法实现Levenshtein距离
     * 
     * @param str1 第一个字符串
     * @param str2 第二个字符串
     * @return 编辑距离值
     */
    public static int calculateEditDistance(String str1, String str2) {
        if (!StringUtils.hasText(str1) && !StringUtils.hasText(str2)) {
            return 0;
        }
        if (!StringUtils.hasText(str1)) {
            return str2.length();
        }
        if (!StringUtils.hasText(str2)) {
            return str1.length();
        }
        
        // 预处理：转换为小写并去除空格
        str1 = str1.toLowerCase().replaceAll("\\s+", "");
        str2 = str2.toLowerCase().replaceAll("\\s+", "");
        
        int len1 = str1.length();
        int len2 = str2.length();
        
        // 创建动态规划表
        int[][] dp = new int[len1 + 1][len2 + 1];
        
        // 初始化边界条件
        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= len2; j++) {
            dp[0][j] = j;
        }
        
        // 填充动态规划表
        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                    // 字符相同，不需要操作
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    // 字符不同，取三种操作的最小值
                    dp[i][j] = Math.min(
                        Math.min(
                            dp[i - 1][j] + 1,     // 删除
                            dp[i][j - 1] + 1      // 插入
                        ),
                        dp[i - 1][j - 1] + 1      // 替换
                    );
                }
            }
        }
        
        return dp[len1][len2];
    }
    
    /**
     * 计算两个字符串的相似度
     * 基于编辑距离计算相似度百分比
     * 
     * @param str1 第一个字符串
     * @param str2 第二个字符串
     * @return 相似度百分比 (0.0 - 1.0)
     */
    public static double calculateSimilarity(String str1, String str2) {
        if (!StringUtils.hasText(str1) && !StringUtils.hasText(str2)) {
            return 1.0;
        }
        if (!StringUtils.hasText(str1) || !StringUtils.hasText(str2)) {
            return 0.0;
        }
        
        int editDistance = calculateEditDistance(str1, str2);
        int maxLength = Math.max(str1.length(), str2.length());
        
        return 1.0 - (double) editDistance / maxLength;
    }
    
    /**
     * 判断两个字符串是否相似
     * 基于相似度阈值判断
     * 
     * @param str1 第一个字符串
     * @param str2 第二个字符串
     * @param threshold 相似度阈值 (0.0 - 1.0)
     * @return 是否相似
     */
    public static boolean isSimilar(String str1, String str2, double threshold) {
        return calculateSimilarity(str1, str2) >= threshold;
    }
    
    /**
     * 计算中文字符串的编辑距离
     * 针对中文字符进行优化处理
     * 
     * @param str1 第一个中文字符串
     * @param str2 第二个中文字符串
     * @return 编辑距离值
     */
    public static int calculateChineseEditDistance(String str1, String str2) {
        if (!StringUtils.hasText(str1) && !StringUtils.hasText(str2)) {
            return 0;
        }
        if (!StringUtils.hasText(str1)) {
            return str2.length();
        }
        if (!StringUtils.hasText(str2)) {
            return str1.length();
        }
        
        // 预处理：去除标点符号和空格
        str1 = str1.replaceAll("[\\p{Punct}\\s]+", "");
        str2 = str2.replaceAll("[\\p{Punct}\\s]+", "");
        
        return calculateEditDistance(str1, str2);
    }
    
    /**
     * 计算中文字符串的相似度
     * 针对中文字符进行优化处理
     * 
     * @param str1 第一个中文字符串
     * @param str2 第二个中文字符串
     * @return 相似度百分比 (0.0 - 1.0)
     */
    public static double calculateChineseSimilarity(String str1, String str2) {
        if (!StringUtils.hasText(str1) && !StringUtils.hasText(str2)) {
            return 1.0;
        }
        if (!StringUtils.hasText(str1) || !StringUtils.hasText(str2)) {
            return 0.0;
        }
        
        // 预处理：去除标点符号和空格
        String cleanStr1 = str1.replaceAll("[\\p{Punct}\\s]+", "");
        String cleanStr2 = str2.replaceAll("[\\p{Punct}\\s]+", "");
        
        int editDistance = calculateEditDistance(cleanStr1, cleanStr2);
        int maxLength = Math.max(cleanStr1.length(), cleanStr2.length());
        
        return 1.0 - (double) editDistance / maxLength;
    }
}