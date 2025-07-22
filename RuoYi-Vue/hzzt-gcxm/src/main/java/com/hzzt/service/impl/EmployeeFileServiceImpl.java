package com.hzzt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hzzt.filedomain.EmployeeFile;
import com.hzzt.mapper.EmployeeFileMapper;
import com.hzzt.service.EmployeeFileService;
import com.ruoyi.common.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.core.io.Resource; // 用Spring的Resource
import org.springframework.core.io.FileSystemResource;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EmployeeFileServiceImpl  extends ServiceImpl<EmployeeFileMapper, EmployeeFile> implements EmployeeFileService {

    @Autowired
    private EmployeeFileMapper employeeFileMapper;

    @Value("${file.upload.path}")
    private String uploadPath;

    @Override
    public boolean uploadFile(Long employeeId, String fileType, MultipartFile file) throws IOException {
        try {
            // 文件名唯一化处理
            String fileName = file.getOriginalFilename();
            String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;

            // 创建上传目录
            String employeeDir = uploadPath + "/" + employeeId;
            File uploadDir = new File(employeeDir);
            if (!uploadDir.exists()) {
                if (!uploadDir.mkdirs()) {
                    throw new IOException("无法创建目录: " + employeeDir);
                }
            }

            // 保存文件
            File destFile = new File(uploadDir, uniqueFileName);
            file.transferTo(destFile);

            // 保存文件信息到数据库
            EmployeeFile employeeFile = new EmployeeFile();
            employeeFile.setEmployeeId(employeeId);
            employeeFile.setFileType(fileType);
            employeeFile.setFileName(fileName);
            employeeFile.setFilePath(employeeId + "/" + uniqueFileName);
            employeeFile.setUploadTime(new Date());
            employeeFile.setFileSize(file.getSize());
            employeeFile.setMimeType(file.getContentType());

            // 设置审计字段
            String username = SecurityUtils.getUsername();
            employeeFile.setCreateBy(username);
            employeeFile.setCreateTime(new Date());

            return employeeFileMapper.insert(employeeFile) > 0;
        } catch (IOException e) {
            log.error("文件上传失败: ", e);
            throw e;
        }
    }

    @Override
    public List<EmployeeFile> getFilesByEmployeeId(Long employeeId) {
        try {
            // 构建查询条件
            LambdaQueryWrapper<EmployeeFile> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(EmployeeFile::getEmployeeId, employeeId)
                    .orderByDesc(EmployeeFile::getUploadTime);

            // 获取文件列表
            List<EmployeeFile> files = employeeFileMapper.selectList(queryWrapper);

            // 为每个文件添加访问URL
            files.forEach(file -> {
                file.setUrl("/api/hrman/files/" + file.getId());
            });

            return files;
        } catch (Exception e) {
            log.error("获取员工文件列表失败: ", e);
            throw new RuntimeException("获取文件列表失败", e);
        }
    }

    @Override
    public boolean deleteFile(Long fileId) {
        try {
            // 获取文件信息
            EmployeeFile file = employeeFileMapper.selectById(fileId);
            if (file == null) {
                return false;
            }

            // 删除物理文件
            File physicalFile = new File(uploadPath + "/" + file.getFilePath());
            if (physicalFile.exists()) {
                if (!physicalFile.delete()) {
                    log.warn("物理文件删除失败: {}", physicalFile.getAbsolutePath());
                }
            }

            // 删除数据库记录
            return employeeFileMapper.deleteById(fileId) > 0;
        } catch (Exception e) {
            log.error("删除文件失败: ", e);
            throw new RuntimeException("删除文件失败", e);
        }
    }

    @Override
    public Resource getFileContent(Long fileId) {
        try {
            // 获取文件信息
            EmployeeFile file = employeeFileMapper.selectById(fileId);
            if (file == null) {
                return null;
            }

            // 获取物理文件
            File physicalFile = new File(uploadPath + "/" + file.getFilePath());
            if (!physicalFile.exists()) {
                log.warn("文件不存在: {}", physicalFile.getAbsolutePath());
                return null;
            }

            return (Resource) new FileSystemResource(physicalFile);
        } catch (Exception e) {
            log.error("获取文件内容失败: ", e);
            throw new RuntimeException("获取文件内容失败", e);
        }
    }

    @Override
    public EmployeeFile getFileInfo(Long fileId) {
        return employeeFileMapper.selectById(fileId);
    }

    /**
     * 检查文件类型是否合法
     */
    private boolean isValidFileType(String mimeType) {
        // 支持的文件类型列表
        Set<String> allowedTypes = new HashSet<>(Arrays.asList(
                "image/jpeg",
                "image/png",
                "application/pdf"
        ));
        return allowedTypes.contains(mimeType);
    }

    /**
     * 检查文件大小是否合法
     */
    private boolean isValidFileSize(long size) {
        // 最大允许10MB
        return size <= 10 * 1024 * 1024;
    }

    /**
     * 清理临时文件
     */
    @Scheduled(cron = "0 0 1 * * ?") // 每天凌晨1点执行
    public void cleanupTempFiles() {
        try {
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                return;
            }

            // 获取所有上传的文件记录
            List<EmployeeFile> allFiles = employeeFileMapper.selectList(null);
            Set<String> validPaths = allFiles.stream()
                    .map(EmployeeFile::getFilePath)
                    .collect(Collectors.toSet());

            // 遍历目录
            cleanupDirectory(uploadDir, validPaths);
        } catch (Exception e) {
            log.error("清理临时文件失败: ", e);
        }
    }

    private void cleanupDirectory(File directory, Set<String> validPaths) {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                cleanupDirectory(file, validPaths);
                // 如果目录为空，删除目录
                if (file.list() != null && file.list().length == 0) {
                    file.delete();
                }
            } else {
                // 检查文件是否在数据库中有记录
                String relativePath = file.getAbsolutePath().substring(uploadPath.length() + 1)
                        .replace("\\", "/");
                if (!validPaths.contains(relativePath)) {
                    file.delete();
                    log.info("删除孤立文件: {}", relativePath);
                }
            }
        }
    }

    /**
     * 生成文件缩略图（如果是图片的话）
     */
    private void generateThumbnail(File sourceFile, String mimeType, String thumbnailPath) {
        if (!mimeType.startsWith("image/")) {
            return;
        }

        try {
            BufferedImage originalImage = ImageIO.read(sourceFile);
            if (originalImage == null) {
                return;
            }

            // 计算缩略图尺寸（最大200px）
            int maxDimension = 200;
            int width = originalImage.getWidth();
            int height = originalImage.getHeight();
            double scale = Math.min(
                    (double) maxDimension / width,
                    (double) maxDimension / height
            );

            int scaledWidth = (int) (width * scale);
            int scaledHeight = (int) (height * scale);

            // 创建缩略图
            BufferedImage thumbnailImage = new BufferedImage(
                    scaledWidth, scaledHeight, BufferedImage.TYPE_INT_RGB
            );
            Graphics2D g2d = thumbnailImage.createGraphics();
            g2d.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
            g2d.dispose();

            // 保存缩略图
            File thumbnailFile = new File(thumbnailPath);
            ImageIO.write(thumbnailImage, "JPEG", thumbnailFile);
        } catch (IOException e) {
            log.error("生成缩略图失败: ", e);
        }
    }
}