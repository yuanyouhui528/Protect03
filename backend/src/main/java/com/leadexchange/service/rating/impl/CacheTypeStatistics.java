package com.leadexchange.service.rating.impl;

/**
 * 缓存类型统计信息
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
public class CacheTypeStatistics {
    
    private String cacheType;
    private long hits;
    private long misses;
    private long total;
    private double hitRate;
    
    public CacheTypeStatistics() {}
    
    public CacheTypeStatistics(String cacheType, long hits, long misses, long total, double hitRate) {
        this.cacheType = cacheType;
        this.hits = hits;
        this.misses = misses;
        this.total = total;
        this.hitRate = hitRate;
    }
    
    public String getCacheType() {
        return cacheType;
    }
    
    public void setCacheType(String cacheType) {
        this.cacheType = cacheType;
    }
    
    public long getHits() {
        return hits;
    }
    
    public void setHits(long hits) {
        this.hits = hits;
    }
    
    public long getMisses() {
        return misses;
    }
    
    public void setMisses(long misses) {
        this.misses = misses;
    }
    
    public long getTotal() {
        return total;
    }
    
    public void setTotal(long total) {
        this.total = total;
    }
    
    public double getHitRate() {
        return hitRate;
    }
    
    public void setHitRate(double hitRate) {
        this.hitRate = hitRate;
    }
}