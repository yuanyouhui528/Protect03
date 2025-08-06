package com.leadexchange.repository.user;

import com.leadexchange.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问层接口
 * 继承Spring Data JPA的JpaRepository，提供基础CRUD操作
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    Optional<User> findByUsernameAndDeleted(String username, Integer deleted);

    /**
     * 根据手机号查询用户
     * @param phone 手机号
     * @return 用户信息
     */
    Optional<User> findByPhoneAndDeleted(String phone, Integer deleted);

    /**
     * 根据邮箱查询用户
     * @param email 邮箱
     * @return 用户信息
     */
    Optional<User> findByEmailAndDeleted(String email, Integer deleted);

    /**
     * 根据用户名或手机号查询用户
     * @param username 用户名
     * @param phone 手机号
     * @return 用户信息
     */
    @Query("SELECT u FROM User u WHERE (u.username = :username OR u.phone = :phone) AND u.deleted = 0")
    Optional<User> findByUsernameOrPhoneAndDeleted(@Param("username") String username, @Param("phone") String phone);

    /**
     * 更新用户登录信息
     * @param userId 用户ID
     * @param loginTime 登录时间
     * @param loginIp 登录IP
     * @return 更新行数
     */
    @Query("UPDATE User u SET u.lastLoginTime = :loginTime, u.lastLoginIp = :loginIp, u.loginCount = u.loginCount + 1 WHERE u.id = :userId")
    int updateLoginInfo(@Param("userId") Long userId, @Param("loginTime") LocalDateTime loginTime, @Param("loginIp") String loginIp);

    /**
     * 根据状态查询用户列表
     * @param status 用户状态
     * @return 用户列表
     */
    List<User> findByStatusAndDeletedOrderByCreateTimeDesc(Integer status, Integer deleted);

    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsernameAndDeleted(String username, Integer deleted);

    /**
     * 检查手机号是否存在
     * @param phone 手机号
     * @return 是否存在
     */
    boolean existsByPhoneAndDeleted(String phone, Integer deleted);

    /**
     * 检查邮箱是否存在
     * @param email 邮箱
     * @return 是否存在
     */
    boolean existsByEmailAndDeleted(String email, Integer deleted);

}