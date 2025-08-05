package com.leadexchange.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 文件操作工具类
 * 提供文件上传、下载、删除等功能
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
public class FileUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    // 允许的文件类型
    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "jpg", "jpeg", "png", "gif", "bmp", "webp"
    );
    
    private static final Set<String> ALLOWED_DOCUMENT_TYPES = Set.of(
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt"
    );
    
    private static final Set<String> ALLOWED_ARCHIVE_TYPES = Set.of(
            "zip", "rar", "7z", "tar", "gz"
    );

    // 文件大小限制（字节）
    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final long MAX_DOCUMENT_SIZE = 20 * 1024 * 1024; // 20MB
    private static final long MAX_ARCHIVE_SIZE = 50 * 1024 * 1024; // 50MB

    // 默认上传路径
    private static final String DEFAULT_UPLOAD_PATH = "uploads";
    
    // 日期格式化器
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    /**
     * 上传文件
     * 
     * @param file 上传的文件
     * @param uploadPath 上传路径
     * @return 文件相对路径
     * @throws IOException IO异常
     */
    public static String uploadFile(MultipartFile file, String uploadPath) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }

        // 验证文件
        validateFile(file);

        // 创建上传目录
        String basePath = StringUtils.hasText(uploadPath) ? uploadPath : DEFAULT_UPLOAD_PATH;
        String datePath = LocalDateTime.now().format(DATE_FORMATTER);
        String fullPath = basePath + File.separator + datePath;
        
        Path uploadDir = Paths.get(fullPath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
            logger.info("创建上传目录: {}", uploadDir.toAbsolutePath());
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String uniqueFilename = generateUniqueFilename(originalFilename, extension);
        
        // 保存文件
        Path targetPath = uploadDir.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        
        String relativePath = datePath + "/" + uniqueFilename;
        logger.info("文件上传成功: {} -> {}", originalFilename, relativePath);
        
        return relativePath;
    }

    /**
     * 删除文件
     * 
     * @param filePath 文件路径
     * @return 是否删除成功
     */
    public static boolean deleteFile(String filePath) {
        if (!StringUtils.hasText(filePath)) {
            return false;
        }

        try {
            Path path = Paths.get(filePath);
            boolean deleted = Files.deleteIfExists(path);
            if (deleted) {
                logger.info("文件删除成功: {}", filePath);
            } else {
                logger.warn("文件不存在或删除失败: {}", filePath);
            }
            return deleted;
        } catch (IOException e) {
            logger.error("删除文件失败: {}", filePath, e);
            return false;
        }
    }

    /**
     * 获取文件扩展名
     * 
     * @param filename 文件名
     * @return 扩展名（小写）
     */
    public static String getFileExtension(String filename) {
        if (!StringUtils.hasText(filename)) {
            return "";
        }
        
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }
        
        return filename.substring(lastDotIndex + 1).toLowerCase();
    }

    /**
     * 获取文件MIME类型
     * 
     * @param filename 文件名
     * @return MIME类型
     */
    public static String getContentType(String filename) {
        String extension = getFileExtension(filename);
        
        return switch (extension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "bmp" -> "image/bmp";
            case "webp" -> "image/webp";
            case "pdf" -> "application/pdf";
            case "doc" -> "application/msword";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls" -> "application/vnd.ms-excel";
            case "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "ppt" -> "application/vnd.ms-powerpoint";
            case "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            case "txt" -> "text/plain";
            case "zip" -> "application/zip";
            case "rar" -> "application/x-rar-compressed";
            case "7z" -> "application/x-7z-compressed";
            default -> "application/octet-stream";
        };
    }

    /**
     * 格式化文件大小
     * 
     * @param size 文件大小（字节）
     * @return 格式化后的大小
     */
    public static String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        }
        
        String[] units = {"KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double fileSize = size;
        
        while (fileSize >= 1024 && unitIndex < units.length - 1) {
            fileSize /= 1024;
            unitIndex++;
        }
        
        return String.format("%.2f %s", fileSize, units[unitIndex]);
    }

    /**
     * 计算文件MD5值
     * 
     * @param file 文件
     * @return MD5值
     * @throws IOException IO异常
     */
    public static String calculateMD5(MultipartFile file) throws IOException {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[8192];
            int bytesRead;
            
            try (InputStream inputStream = file.getInputStream()) {
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    md.update(buffer, 0, bytesRead);
                }
            }
            
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            
            return sb.toString();
        } catch (Exception e) {
            logger.error("计算文件MD5失败", e);
            throw new IOException("计算文件MD5失败", e);
        }
    }

    /**
     * 验证文件
     * 
     * @param file 上传的文件
     * @throws IllegalArgumentException 验证失败异常
     */
    private static void validateFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (!StringUtils.hasText(originalFilename)) {
            throw new IllegalArgumentException("文件名不能为空");
        }

        String extension = getFileExtension(originalFilename);
        if (!StringUtils.hasText(extension)) {
            throw new IllegalArgumentException("文件必须有扩展名");
        }

        long fileSize = file.getSize();
        
        // 验证文件类型和大小
        if (ALLOWED_IMAGE_TYPES.contains(extension)) {
            if (fileSize > MAX_IMAGE_SIZE) {
                throw new IllegalArgumentException("图片文件大小不能超过" + formatFileSize(MAX_IMAGE_SIZE));
            }
        } else if (ALLOWED_DOCUMENT_TYPES.contains(extension)) {
            if (fileSize > MAX_DOCUMENT_SIZE) {
                throw new IllegalArgumentException("文档文件大小不能超过" + formatFileSize(MAX_DOCUMENT_SIZE));
            }
        } else if (ALLOWED_ARCHIVE_TYPES.contains(extension)) {
            if (fileSize > MAX_ARCHIVE_SIZE) {
                throw new IllegalArgumentException("压缩文件大小不能超过" + formatFileSize(MAX_ARCHIVE_SIZE));
            }
        } else {
            throw new IllegalArgumentException("不支持的文件类型: " + extension);
        }

        logger.debug("文件验证通过: {} ({})", originalFilename, formatFileSize(fileSize));
    }

    /**
     * 生成唯一文件名
     * 
     * @param originalFilename 原始文件名
     * @param extension 文件扩展名
     * @return 唯一文件名
     */
    private static String generateUniqueFilename(String originalFilename, String extension) {
        // 移除原始文件名的扩展名
        String nameWithoutExt = originalFilename;
        int lastDotIndex = originalFilename.lastIndexOf('.');
        if (lastDotIndex != -1) {
            nameWithoutExt = originalFilename.substring(0, lastDotIndex);
        }
        
        // 清理文件名，只保留字母、数字、中文和部分特殊字符
        String cleanName = nameWithoutExt.replaceAll("[^\\w\\u4e00-\\u9fa5.-]", "_");
        
        // 限制文件名长度
        if (cleanName.length() > 50) {
            cleanName = cleanName.substring(0, 50);
        }
        
        // 生成时间戳和随机数
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = String.valueOf(new Random().nextInt(1000));
        
        return cleanName + "_" + timestamp + "_" + random + "." + extension;
    }
}