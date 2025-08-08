package com.leadexchange.repository.analytics;

import com.leadexchange.domain.analytics.SystemStats;
import com.leadexchange.domain.analytics.TrendData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 数据分析Repository实现类
 * 提供复杂的统计查询功能
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Repository
public class AnalyticsRepositoryImpl implements CustomAnalyticsRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Object[] getPersonalLeadStats(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        String sql = "SELECT " +
                "COUNT(*) as total_leads, " +
                "COUNT(CASE WHEN status = 'PUBLISHED' THEN 1 END) as published_leads, " +
                "COUNT(CASE WHEN status = 'EXCHANGED' THEN 1 END) as exchanged_leads, " +
                "COUNT(CASE WHEN status = 'COMPLETED' THEN 1 END) as completed_leads, " +
                "AVG(CASE WHEN rating IS NOT NULL THEN " +
                    "CASE rating " +
                        "WHEN 'A' THEN 8 " +
                        "WHEN 'B' THEN 4 " +
                        "WHEN 'C' THEN 2 " +
                        "WHEN 'D' THEN 1 " +
                        "ELSE 0 " +
                    "END " +
                "END) as avg_rating_score " +
            "FROM leads " +
            "WHERE user_id = ? AND created_at BETWEEN ? AND ?";
        
        return jdbcTemplate.queryForObject(sql, Object[].class, userId, startTime, endTime);
    }

    @Override
    public Object[] getPersonalExchangeStats(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        String sql = "SELECT " +
                "COUNT(DISTINCT e.id) as total_exchanges, " +
                "COUNT(DISTINCT CASE WHEN e.status = 'COMPLETED' THEN e.id END) as completed_exchanges, " +
                "COUNT(DISTINCT CASE WHEN e.requester_id = ? THEN e.id END) as initiated_exchanges, " +
                "COUNT(DISTINCT CASE WHEN e.provider_id = ? THEN e.id END) as received_exchanges, " +
                "COALESCE(SUM(CASE WHEN e.requester_id = ? THEN e.points_cost ELSE 0 END), 0) as points_spent, " +
                "COALESCE(SUM(CASE WHEN e.provider_id = ? THEN e.points_earned ELSE 0 END), 0) as points_earned " +
            "FROM exchanges e " +
            "WHERE (e.requester_id = ? OR e.provider_id = ?) " +
                "AND e.created_at BETWEEN ? AND ?";
        
        return jdbcTemplate.queryForObject(sql, Object[].class, userId, userId, userId, userId, userId, userId, startTime, endTime);
    }

    @Override
    public Map<String, Long> getRatingDistribution(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        String sql = "SELECT " +
                "rating, " +
                "COUNT(*) as count " +
            "FROM leads " +
            "WHERE user_id = ? AND created_at BETWEEN ? AND ? AND rating IS NOT NULL " +
            "GROUP BY rating";
        
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, userId, startTime, endTime);
        return results.stream().collect(
            java.util.stream.Collectors.toMap(
                row -> (String) row.get("rating"),
                row -> ((Number) row.get("count")).longValue()
            )
        );
    }

    @Override
    public Object[] getPersonalViewFavoriteStats(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        String sql = "SELECT " +
                "COUNT(DISTINCT lv.id) as total_views, " +
                "COUNT(DISTINCT lf.id) as total_favorites, " +
                "COUNT(DISTINCT lv.lead_id) as unique_leads_viewed, " +
                "COUNT(DISTINCT lf.lead_id) as unique_leads_favorited " +
            "FROM users u " +
            "LEFT JOIN lead_views lv ON u.id = lv.user_id AND lv.created_at BETWEEN ? AND ? " +
            "LEFT JOIN lead_favorites lf ON u.id = lf.user_id AND lf.created_at BETWEEN ? AND ? " +
            "WHERE u.id = ?";
        
        return jdbcTemplate.queryForObject(sql, Object[].class, startTime, endTime, startTime, endTime, userId);
    }

    @Override
    public Object[] getSystemOverallStats(LocalDateTime startTime, LocalDateTime endTime) {
        String sql = "SELECT " +
                "COUNT(DISTINCT u.id) as total_users, " +
                "COUNT(DISTINCT CASE WHEN u.last_login_at >= ? THEN u.id END) as active_users, " +
                "COUNT(DISTINCT CASE WHEN u.created_at BETWEEN ? AND ? THEN u.id END) as new_users, " +
                "COUNT(DISTINCT l.id) as total_leads, " +
                "COUNT(DISTINCT CASE WHEN l.rating IN ('A', 'B') THEN l.id END) as valid_leads, " +
                "COUNT(DISTINCT e.id) as total_exchanges, " +
                "COUNT(DISTINCT CASE WHEN e.status = 'COMPLETED' THEN e.id END) as successful_exchanges " +
            "FROM users u " +
            "LEFT JOIN leads l ON l.created_at BETWEEN ? AND ? " +
            "LEFT JOIN exchanges e ON e.created_at BETWEEN ? AND ?";
        
        return jdbcTemplate.queryForObject(sql, Object[].class,
            startTime.minusDays(30), // 活跃用户定义为30天内登录
            startTime, endTime, // 新用户时间范围
            startTime, endTime, // 线索时间范围
            startTime, endTime); // 交换时间范围
    }

    @Override
    public Map<String, Long> getIndustryDistribution(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        String sql;
        Object[] params;
        
        if (userId != null) {
            sql = "SELECT " +
                    "industry, " +
                    "COUNT(*) as count " +
                    "FROM leads " +
                    "WHERE user_id = ? AND created_at BETWEEN ? AND ? AND industry IS NOT NULL " +
                    "GROUP BY industry " +
                    "ORDER BY count DESC " +
                    "LIMIT 10";
            params = new Object[]{userId, startTime, endTime};
        } else {
            sql = "SELECT " +
                    "industry, " +
                    "COUNT(*) as count " +
                    "FROM leads " +
                    "WHERE created_at BETWEEN ? AND ? AND industry IS NOT NULL " +
                    "GROUP BY industry " +
                    "ORDER BY count DESC " +
                    "LIMIT 10";
            params = new Object[]{startTime, endTime};
        }
        
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, params);
        return results.stream().collect(
            java.util.stream.Collectors.toMap(
                row -> (String) row.get("industry"),
                row -> ((Number) row.get("count")).longValue(),
                (existing, replacement) -> existing,
                java.util.LinkedHashMap::new
            )
        );
    }

    @Override
    public List<TrendData> getLeadTrendData(Long userId, LocalDateTime startTime, LocalDateTime endTime, String granularity) {
        String dateFormat = getDateFormat(granularity);
        String sql;
        Object[] params;
        
        if (userId != null) {
            sql = String.format("SELECT " +
                    "%s as time_point, " +
                    "COUNT(*) as value, " +
                    "'leads' as data_type " +
                    "FROM leads " +
                    "WHERE user_id = ? AND created_at BETWEEN ? AND ? " +
                    "GROUP BY %s " +
                    "ORDER BY time_point", dateFormat, dateFormat);
            params = new Object[]{userId, startTime, endTime};
        } else {
            sql = String.format("SELECT " +
                    "%s as time_point, " +
                    "COUNT(*) as value, " +
                    "'leads' as data_type " +
                    "FROM leads " +
                    "WHERE created_at BETWEEN ? AND ? " +
                    "GROUP BY %s " +
                    "ORDER BY time_point", dateFormat, dateFormat);
            params = new Object[]{startTime, endTime};
        }
        
        return jdbcTemplate.query(sql, params, new TrendDataRowMapper());
    }

    @Override
    public List<TrendData> getExchangeTrendData(Long userId, LocalDateTime startTime, LocalDateTime endTime, String granularity) {
        String dateFormat = getDateFormat(granularity);
        String sql;
        Object[] params;
        
        if (userId != null) {
            sql = String.format("SELECT " +
                    "%s as time_point, " +
                    "COUNT(*) as value, " +
                    "'exchanges' as data_type " +
                    "FROM exchanges " +
                    "WHERE (requester_id = ? OR provider_id = ?) AND created_at BETWEEN ? AND ? " +
                    "GROUP BY %s " +
                    "ORDER BY time_point", dateFormat, dateFormat);
            params = new Object[]{userId, userId, startTime, endTime};
        } else {
            sql = String.format("SELECT " +
                    "%s as time_point, " +
                    "COUNT(*) as value, " +
                    "'exchanges' as data_type " +
                    "FROM exchanges " +
                    "WHERE created_at BETWEEN ? AND ? " +
                    "GROUP BY %s " +
                    "ORDER BY time_point", dateFormat, dateFormat);
            params = new Object[]{startTime, endTime};
        }
        
        return jdbcTemplate.query(sql, params, new TrendDataRowMapper());
    }

    @Override
    public List<TrendData> getUserActivityTrendData(LocalDateTime startTime, LocalDateTime endTime, String granularity) {
        String dateFormat = getDateFormat(granularity);
        String sql = String.format("SELECT " +
                "%s as time_point, " +
                "COUNT(DISTINCT user_id) as value, " +
                "'user_activity' as data_type " +
                "FROM ( " +
                "SELECT user_id, created_at FROM leads WHERE created_at BETWEEN ? AND ? " +
                "UNION ALL " +
                "SELECT requester_id as user_id, created_at FROM exchanges WHERE created_at BETWEEN ? AND ? " +
                "UNION ALL " +
                "SELECT provider_id as user_id, created_at FROM exchanges WHERE created_at BETWEEN ? AND ? " +
                "UNION ALL " +
                "SELECT user_id, created_at FROM lead_views WHERE created_at BETWEEN ? AND ? " +
                ") activity " +
                "GROUP BY %s " +
                "ORDER BY time_point", dateFormat, dateFormat);
        
        return jdbcTemplate.query(sql, new Object[]{startTime, endTime, startTime, endTime, startTime, endTime, startTime, endTime}, new TrendDataRowMapper());
    }

    @Override
    public List<Map<String, Object>> getTopIndustries(LocalDateTime startTime, LocalDateTime endTime, int limit) {
        String sql = "SELECT " +
                "industry, " +
                "COUNT(*) as lead_count, " +
                "COUNT(DISTINCT user_id) as user_count " +
                "FROM leads " +
                "WHERE created_at BETWEEN ? AND ? AND industry IS NOT NULL " +
                "GROUP BY industry " +
                "ORDER BY lead_count DESC " +
                "LIMIT ?";
        
        return jdbcTemplate.queryForList(sql, startTime, endTime, limit);
    }

    @Override
    public List<Map<String, Object>> getTopActiveUsers(LocalDateTime startTime, LocalDateTime endTime, int limit) {
        String sql = "SELECT " +
                "u.id as user_id, " +
                "u.username, " +
                "u.company_name, " +
                "COUNT(DISTINCT l.id) as lead_count, " +
                "COUNT(DISTINCT e.id) as exchange_count, " +
                "(COUNT(DISTINCT l.id) + COUNT(DISTINCT e.id)) as activity_score " +
                "FROM users u " +
                "LEFT JOIN leads l ON u.id = l.user_id AND l.created_at BETWEEN ? AND ? " +
                "LEFT JOIN exchanges e ON (u.id = e.requester_id OR u.id = e.provider_id) AND e.created_at BETWEEN ? AND ? " +
                "GROUP BY u.id, u.username, u.company_name " +
                "HAVING activity_score > 0 " +
                "ORDER BY activity_score DESC " +
                "LIMIT ?";
        
        return jdbcTemplate.queryForList(sql, startTime, endTime, startTime, endTime, limit);
    }

    /**
     * 根据时间粒度获取日期格式化字符串
     */
    private String getDateFormat(String granularity) {
        switch (granularity.toLowerCase()) {
            case "hour":
                return "DATE_FORMAT(created_at, '%Y-%m-%d %H:00:00')";
            case "day":
                return "DATE_FORMAT(created_at, '%Y-%m-%d')";
            case "week":
                return "DATE_FORMAT(created_at, '%Y-%u')";
            case "month":
                return "DATE_FORMAT(created_at, '%Y-%m')";
            case "year":
                return "DATE_FORMAT(created_at, '%Y')";
            default:
                return "DATE_FORMAT(created_at, '%Y-%m-%d')";
        }
    }

    @Override
    public List<Object[]> getLeadTrendsByDay(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        String sql;
        Object[] params;
        
        if (userId != null) {
            sql = "SELECT " +
                    "DATE(created_at) as date, " +
                    "COUNT(*) as count " +
                    "FROM leads " +
                    "WHERE user_id = ? AND created_at BETWEEN ? AND ? " +
                    "GROUP BY DATE(created_at) " +
                    "ORDER BY date";
            params = new Object[]{userId, startTime, endTime};
        } else {
            sql = "SELECT " +
                    "DATE(created_at) as date, " +
                    "COUNT(*) as count " +
                    "FROM leads " +
                    "WHERE created_at BETWEEN ? AND ? " +
                    "GROUP BY DATE(created_at) " +
                    "ORDER BY date";
            params = new Object[]{startTime, endTime};
        }
        
        return jdbcTemplate.query(sql, params, (rs, rowNum) -> new Object[]{
            rs.getDate("date"),
            rs.getLong("count")
        });
    }

    @Override
    public List<Object[]> getExchangeTrendsByDay(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        String sql;
        Object[] params;
        
        if (userId != null) {
            sql = "SELECT " +
                    "DATE(created_at) as date, " +
                    "COUNT(*) as count " +
                    "FROM exchanges " +
                    "WHERE (requester_id = ? OR provider_id = ?) AND created_at BETWEEN ? AND ? " +
                    "GROUP BY DATE(created_at) " +
                    "ORDER BY date";
            params = new Object[]{userId, userId, startTime, endTime};
        } else {
            sql = "SELECT " +
                    "DATE(created_at) as date, " +
                    "COUNT(*) as count " +
                    "FROM exchanges " +
                    "WHERE created_at BETWEEN ? AND ? " +
                    "GROUP BY DATE(created_at) " +
                    "ORDER BY date";
            params = new Object[]{startTime, endTime};
        }
        
        return jdbcTemplate.query(sql, params, (rs, rowNum) -> new Object[]{
            rs.getDate("date"),
            rs.getLong("count")
        });
    }

    @Override
    public List<Object[]> getPersonalRatingDistribution(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        String sql = "SELECT " +
                "rating, " +
                "COUNT(*) as count " +
                "FROM leads " +
                "WHERE user_id = ? AND created_at BETWEEN ? AND ? AND rating IS NOT NULL " +
                "GROUP BY rating " +
                "ORDER BY rating";
        
        return jdbcTemplate.query(sql, new Object[]{userId, startTime, endTime}, (rs, rowNum) -> new Object[]{
            rs.getString("rating"),
            rs.getLong("count")
        });
    }

    @Override
    public List<Object[]> getSystemRatingDistribution(LocalDateTime startTime, LocalDateTime endTime) {
        String sql = "SELECT " +
                "rating, " +
                "COUNT(*) as count " +
                "FROM leads " +
                "WHERE created_at BETWEEN ? AND ? AND rating IS NOT NULL " +
                "GROUP BY rating " +
                "ORDER BY rating";
        
        return jdbcTemplate.query(sql, new Object[]{startTime, endTime}, (rs, rowNum) -> new Object[]{
            rs.getString("rating"),
            rs.getLong("count")
        });
    }

    @Override
    public List<Object[]> getUserActivityTrendsByDay(LocalDateTime startTime, LocalDateTime endTime) {
        String sql = "SELECT " +
                "DATE(activity_date) as date, " +
                "COUNT(DISTINCT user_id) as count " +
                "FROM ( " +
                "SELECT user_id, created_at as activity_date FROM leads WHERE created_at BETWEEN ? AND ? " +
                "UNION ALL " +
                "SELECT requester_id as user_id, created_at as activity_date FROM exchanges WHERE created_at BETWEEN ? AND ? " +
                "UNION ALL " +
                "SELECT provider_id as user_id, created_at as activity_date FROM exchanges WHERE created_at BETWEEN ? AND ? " +
                "UNION ALL " +
                "SELECT user_id, created_at as activity_date FROM lead_views WHERE created_at BETWEEN ? AND ? " +
                ") activity " +
                "GROUP BY DATE(activity_date) " +
                "ORDER BY date";
        
        return jdbcTemplate.query(sql, new Object[]{startTime, endTime, startTime, endTime, startTime, endTime, startTime, endTime}, (rs, rowNum) -> new Object[]{
            rs.getDate("date"),
            rs.getLong("count")
        });
    }

    @Override
    public Map<String, Object> getRealTimeStats() {
        String sql = "SELECT " +
                "COUNT(DISTINCT u.id) as online_users, " +
                "COUNT(DISTINCT l.id) as total_leads, " +
                "COUNT(DISTINCT e.id) as total_exchanges, " +
                "COUNT(DISTINCT CASE WHEN l.status = 'PUBLISHED' THEN l.id END) as active_leads, " +
                "COUNT(DISTINCT CASE WHEN e.status = 'PENDING' THEN e.id END) as pending_exchanges " +
                "FROM users u " +
                "LEFT JOIN leads l ON l.created_at >= DATE_SUB(NOW(), INTERVAL 24 HOUR) " +
                "LEFT JOIN exchanges e ON e.created_at >= DATE_SUB(NOW(), INTERVAL 24 HOUR) " +
                "WHERE u.last_login_at >= DATE_SUB(NOW(), INTERVAL 1 HOUR)";
        
        Map<String, Object> result = jdbcTemplate.queryForMap(sql);
        return result;
    }
    
    @Override
    public List<TrendData> getUserActivityTrends(LocalDateTime startTime, LocalDateTime endTime) {
        String sql = "SELECT " +
                "DATE_FORMAT(activity_date, '%Y-%m-%d') as time_point, " +
                "COUNT(DISTINCT user_id) as value, " +
                "'user_activity' as data_type " +
                "FROM ( " +
                "SELECT user_id, created_at as activity_date FROM leads WHERE created_at BETWEEN ? AND ? " +
                "UNION ALL " +
                "SELECT requester_id as user_id, created_at as activity_date FROM exchanges WHERE created_at BETWEEN ? AND ? " +
                "UNION ALL " +
                "SELECT provider_id as user_id, created_at as activity_date FROM exchanges WHERE created_at BETWEEN ? AND ? " +
                "UNION ALL " +
                "SELECT user_id, created_at as activity_date FROM lead_views WHERE created_at BETWEEN ? AND ? " +
                ") activity " +
                "GROUP BY DATE(activity_date) " +
                "ORDER BY time_point";
        
        return jdbcTemplate.query(sql, new Object[]{startTime, endTime, startTime, endTime, startTime, endTime, startTime, endTime}, new TrendDataRowMapper());
    }
    
    @Override
    public SystemStats getSystemStats(LocalDateTime startTime, LocalDateTime endTime) {
        String sql = "SELECT " +
                "COUNT(DISTINCT u.id) as total_users, " +
                "COUNT(DISTINCT CASE WHEN u.last_login_at >= ? THEN u.id END) as active_users, " +
                "COUNT(DISTINCT CASE WHEN u.created_at BETWEEN ? AND ? THEN u.id END) as new_users, " +
                "COUNT(DISTINCT l.id) as total_leads, " +
                "COUNT(DISTINCT CASE WHEN l.status = 'PUBLISHED' THEN l.id END) as active_leads, " +
                "COUNT(DISTINCT e.id) as total_exchanges, " +
                "COUNT(DISTINCT CASE WHEN e.status = 'COMPLETED' THEN e.id END) as successful_exchanges, " +
                "COALESCE(SUM(e.points_cost), 0) as total_points_circulation, " +
                "AVG(CASE WHEN l.rating IS NOT NULL THEN " +
                    "CASE l.rating " +
                        "WHEN 'A' THEN 8 " +
                        "WHEN 'B' THEN 4 " +
                        "WHEN 'C' THEN 2 " +
                        "WHEN 'D' THEN 1 " +
                        "ELSE 0 " +
                    "END " +
                "END) as avg_rating_score " +
                "FROM users u " +
                "LEFT JOIN leads l ON l.created_at BETWEEN ? AND ? " +
                "LEFT JOIN exchanges e ON e.created_at BETWEEN ? AND ?";
        
        return jdbcTemplate.queryForObject(sql, new SystemStatsRowMapper(),
            startTime.minusDays(30), // 活跃用户定义为30天内登录
            startTime, endTime, // 新用户时间范围
            startTime, endTime, // 线索时间范围
            startTime, endTime); // 交换时间范围
    }

    /**
     * SystemStats行映射器
     */
    private static class SystemStatsRowMapper implements RowMapper<SystemStats> {
        @Override
        public SystemStats mapRow(ResultSet rs, int rowNum) throws SQLException {
            SystemStats stats = new SystemStats();
            stats.setTotalUsers(rs.getLong("total_users"));
            stats.setActiveUsers(rs.getLong("active_users"));
            stats.setNewUsers(rs.getLong("new_users"));
            stats.setTotalLeads(rs.getLong("total_leads"));
            stats.setValidLeads(rs.getLong("active_leads")); // 映射到validLeads字段
            stats.setTotalExchanges(rs.getLong("total_exchanges"));
            stats.setSuccessfulExchanges(rs.getLong("successful_exchanges"));
            stats.setTotalPointsCirculation(rs.getLong("total_points_circulation"));
            stats.setAverageRating(rs.getDouble("avg_rating_score")); // 使用正确的方法名
            return stats;
        }
    }
     
    @Override
    public byte[] generateReport(Long userId, LocalDateTime startTime, LocalDateTime endTime, String reportType) {
        // 简单的报告生成实现
        StringBuilder report = new StringBuilder();
        report.append("Analytics Report\n");
        report.append("================\n");
        report.append("User ID: ").append(userId).append("\n");
        report.append("Start Time: ").append(startTime).append("\n");
        report.append("End Time: ").append(endTime).append("\n");
        report.append("Report Type: ").append(reportType).append("\n");
        report.append("Generated at: ").append(LocalDateTime.now()).append("\n");
        
        return report.toString().getBytes();
    }
    
    /**
     * TrendData行映射器
     */
    public static class TrendDataRowMapper implements RowMapper<TrendData> {
        @Override
        public TrendData mapRow(ResultSet rs, int rowNum) throws SQLException {
            TrendData trendData = new TrendData();
            trendData.setTimeLabel(rs.getString("time_point"));
            trendData.setValue(rs.getLong("value"));
            trendData.setDataType(rs.getString("data_type"));
            return trendData;
        }
    }
}