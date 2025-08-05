package com.leadexchange.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 用户权限响应DTO
 * 用于返回用户权限信息
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户权限响应")
public class UserPermissionResponse {

    /**
     * 用户ID
     */
    @Schema(description = "用户ID", example = "1")
    private Long userId;

    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "admin")
    private String username;

    /**
     * 用户角色列表
     */
    @Schema(description = "用户角色列表")
    private List<RoleInfo> roles;

    /**
     * 获取用户角色列表
     * 
     * @return 用户角色列表
     */
    public List<RoleInfo> getRoles() {
        return roles;
    }

    /**
     * 设置用户角色列表
     * 
     * @param roles 用户角色列表
     */
    public void setRoles(List<RoleInfo> roles) {
        this.roles = roles;
    }

    /**
     * 用户权限列表
     */
    @Schema(description = "用户权限列表")
    private List<PermissionInfo> permissions;

    /**
     * 权限代码列表（简化版本）
     */
    @Schema(description = "权限代码列表")
    private List<String> permissionCodes;

    /**
     * 获取权限代码列表
     * 
     * @return 权限代码列表
     */
    public List<String> getPermissionCodes() {
        return permissionCodes;
    }

    /**
     * 设置权限代码列表
     * 
     * @param permissionCodes 权限代码列表
     */
    public void setPermissionCodes(List<String> permissionCodes) {
        this.permissionCodes = permissionCodes;
    }

    /**
     * 角色信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "角色信息")
    public static class RoleInfo {
        /**
         * 角色ID
         */
        @Schema(description = "角色ID", example = "1")
        private Long roleId;

        /**
         * 角色代码
         */
        @Schema(description = "角色代码", example = "ADMIN")
        private String roleCode;

        /**
         * 角色名称
         */
        @Schema(description = "角色名称", example = "管理员")
        private String roleName;

        /**
         * 角色描述
         */
        @Schema(description = "角色描述", example = "系统管理员角色")
        private String description;

        // 手动添加setter方法
        public void setRoleId(Long roleId) {
            this.roleId = roleId;
        }

        public void setRoleCode(String roleCode) {
            this.roleCode = roleCode;
        }

        public void setRoleName(String roleName) {
            this.roleName = roleName;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        /**
         * 获取角色代码
         * 
         * @return 角色代码
         */
        public String getRoleCode() {
            return roleCode;
        }
    }

    /**
     * 权限信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "权限信息")
    public static class PermissionInfo {
        /**
         * 权限ID
         */
        @Schema(description = "权限ID", example = "1")
        private Long permissionId;

        /**
         * 权限代码
         */
        @Schema(description = "权限代码", example = "user:create")
        private String permissionCode;

        /**
         * 权限名称
         */
        @Schema(description = "权限名称", example = "创建用户")
        private String permissionName;

        /**
         * 权限类型
         */
        @Schema(description = "权限类型", example = "MENU")
        private String type;

        /**
         * 权限描述
         */
        @Schema(description = "权限描述", example = "创建新用户的权限")
        private String description;

        // 手动添加setter方法
        public void setPermissionId(Long permissionId) {
            this.permissionId = permissionId;
        }

        public void setPermissionCode(String permissionCode) {
            this.permissionCode = permissionCode;
        }

        public void setPermissionName(String permissionName) {
            this.permissionName = permissionName;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    /**
     * 创建成功的权限响应
     * 
     * @param userId 用户ID
     * @param username 用户名
     * @param roles 角色列表
     * @param permissions 权限列表
     * @param permissionCodes 权限代码列表
     * @return 权限响应
     */
    public static UserPermissionResponse success(Long userId, String username, 
                                               List<RoleInfo> roles, 
                                               List<PermissionInfo> permissions,
                                               List<String> permissionCodes) {
        UserPermissionResponse response = new UserPermissionResponse();
        response.setUserId(userId);
        response.setUsername(username);
        response.setRoles(roles);
        response.setPermissions(permissions);
        response.setPermissionCodes(permissionCodes);
        return response;
    }

    /**
     * 获取用户ID
     * @return 用户ID
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 设置用户ID
     * @param userId 用户ID
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 获取用户名
     * @return 用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置用户名
     * @param username 用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取权限列表
     * @return 权限列表
     */
    public List<PermissionInfo> getPermissions() {
        return permissions;
    }

    /**
     * 设置权限列表
     * @param permissions 权限列表
     */
    public void setPermissions(List<PermissionInfo> permissions) {
        this.permissions = permissions;
    }

    /**
     * 创建Builder实例
     * @return Builder实例
     */
    public static UserPermissionResponseBuilder builder() {
        return new UserPermissionResponseBuilder();
    }

    /**
     * Builder类
     */
    public static class UserPermissionResponseBuilder {
        private Long userId;
        private String username;
        private List<RoleInfo> roles;
        private List<PermissionInfo> permissions;
        private List<String> permissionCodes;

        public UserPermissionResponseBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public UserPermissionResponseBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UserPermissionResponseBuilder roles(List<RoleInfo> roles) {
            this.roles = roles;
            return this;
        }

        public UserPermissionResponseBuilder permissions(List<PermissionInfo> permissions) {
            this.permissions = permissions;
            return this;
        }

        public UserPermissionResponseBuilder permissionCodes(List<String> permissionCodes) {
            this.permissionCodes = permissionCodes;
            return this;
        }

        public UserPermissionResponse build() {
            UserPermissionResponse response = new UserPermissionResponse();
            response.setUserId(this.userId);
            response.setUsername(this.username);
            response.setRoles(this.roles);
            response.setPermissions(this.permissions);
            response.setPermissionCodes(this.permissionCodes);
            return response;
        }
    }
}