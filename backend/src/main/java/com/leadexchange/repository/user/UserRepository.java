package com.leadexchange.repository.user;

import com.leadexchange.domain.user.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 用户数据访问层接口
 * 继承MyBatis Plus的BaseMapper，提供基础CRUD操作
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Mapper
public interface UserRepository extends BaseMapper<User> {

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    @Select("SELECT * FROM users WHERE username = #{username} AND deleted = 0")
    User findByUsername(@Param("username") String username);

    /**
     * 根据手机号查询用户
     * @param phone 手机号
     * @return 用户信息
     */
    @Select("SELECT * FROM users WHERE phone = #{phone} AND deleted = 0")
    User findByPhone(@Param("phone") String phone);

    /**
     * 根据邮箱查询用户
     * @param email 邮箱
     * @return 用户信息
     */
    @Select("SELECT * FROM users WHERE email = #{email} AND deleted = 0")
    User findByEmail(@Param("email") String email);

    /**
     * 根据用户名或手机号查询用户
     * @param loginName 登录名（用户名或手机号）
     * @return 用户信息
     */
    @Select("SELECT * FROM users WHERE (username = #{loginName} OR phone = #{loginName}) AND deleted = 0")
    User findByUsernameOrPhone(@Param("loginName") String loginName);

    /**
     * 更新用户登录信息
     * @param userId 用户ID
     * @param loginTime 登录时间
     * @param loginIp 登录IP
     * @return 更新行数
     */
    @Update("UPDATE users SET last_login_time = #{loginTime}, last_login_ip = #{loginIp}, login_count = login_count + 1 WHERE id = #{userId}")
    int updateLoginInfo(@Param("userId") Long userId, @Param("loginTime") LocalDateTime loginTime, @Param("loginIp") String loginIp);

    /**
     * 根据状态查询用户列表
     * @param status 用户状态
     * @return 用户列表
     */
    @Select("SELECT * FROM users WHERE status = #{status} AND deleted = 0 ORDER BY create_time DESC")
    List<User> findByStatus(@Param("status") Integer status);

    /**
     * 查询用户统计信息
     * @return 统计信息
     */
    @Select("SELECT status, COUNT(*) as count FROM users WHERE deleted = 0 GROUP BY status")
    List<Map<String, Object>> getUserStatistics();

    /**
     * 分页查询用户列表（带条件）
     * @param page 分页参数
     * @param username 用户名（模糊查询）
     * @param phone 手机号（模糊查询）
     * @param companyName 企业名称（模糊查询）
     * @param status 用户状态
     * @param verified 认证状态
     * @return 分页结果
     */
    @Select("<script>" +
            "SELECT * FROM users WHERE deleted = 0" +
            "<if test='username != null and username != \"\"'>" +
            " AND username LIKE CONCAT('%', #{username}, '%')" +
            "</if>" +
            "<if test='phone != null and phone != \"\"'>" +
            " AND phone LIKE CONCAT('%', #{phone}, '%')" +
            "</if>" +
            "<if test='companyName != null and companyName != \"\"'>" +
            " AND company_name LIKE CONCAT('%', #{companyName}, '%')" +
            "</if>" +
            "<if test='status != null'>" +
            " AND status = #{status}" +
            "</if>" +
            "<if test='verified != null'>" +
            " AND verified = #{verified}" +
            "</if>" +
            " ORDER BY create_time DESC" +
            "</script>")
    IPage<User> findUsersWithConditions(Page<User> page, 
                                       @Param("username") String username,
                                       @Param("phone") String phone,
                                       @Param("companyName") String companyName,
                                       @Param("status") Integer status,
                                       @Param("verified") Integer verified);

    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @param excludeId 排除的用户ID（用于更新时检查）
     * @return 是否存在
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM users WHERE username = #{username} AND deleted = 0" +
            "<if test='excludeId != null'>" +
            " AND id != #{excludeId}" +
            "</if>" +
            "</script>")
    int existsByUsername(@Param("username") String username, @Param("excludeId") Long excludeId);

    /**
     * 检查手机号是否存在
     * @param phone 手机号
     * @param excludeId 排除的用户ID（用于更新时检查）
     * @return 是否存在
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM users WHERE phone = #{phone} AND deleted = 0" +
            "<if test='excludeId != null'>" +
            " AND id != #{excludeId}" +
            "</if>" +
            "</script>")
    int existsByPhone(@Param("phone") String phone, @Param("excludeId") Long excludeId);

    /**
     * 检查邮箱是否存在
     * @param email 邮箱
     * @param excludeId 排除的用户ID（用于更新时检查）
     * @return 是否存在
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM users WHERE email = #{email} AND deleted = 0" +
            "<if test='excludeId != null'>" +
            " AND id != #{excludeId}" +
            "</if>" +
            "</script>")
    int existsByEmail(@Param("email") String email, @Param("excludeId") Long excludeId);
}