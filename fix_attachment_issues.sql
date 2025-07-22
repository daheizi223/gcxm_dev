-- 修复合同附件问题的SQL脚本
-- 执行时间：2025-06-27

-- 1. 检查现有附件数据
SELECT 
    id,
    contract_id,
    file_name,
    file_original_name,
    file_path,
    file_size,
    file_type,
    file_extension,
    file_category,
    upload_user,
    upload_time
FROM contract_attachment 
WHERE del_flag = '0'
ORDER BY upload_time DESC;

-- 2. 检查是否有文件路径问题
SELECT 
    id,
    file_path,
    CASE 
        WHEN file_path LIKE '/contract/%' THEN 'Path format correct'
        WHEN file_path LIKE '\\contract\\%' THEN 'Windows path format'
        ELSE 'Path format may be incorrect'
    END as path_status
FROM contract_attachment 
WHERE del_flag = '0';

-- 3. 更新可能的文件路径格式问题（如果发现）
-- UPDATE contract_attachment 
-- SET file_path = REPLACE(file_path, '\\', '/') 
-- WHERE file_path LIKE '%\\%' AND del_flag = '0';

-- 4. 检查文件类型是否正确设置
SELECT 
    file_extension,
    file_type,
    COUNT(*) as count
FROM contract_attachment 
WHERE del_flag = '0'
GROUP BY file_extension, file_type
ORDER BY file_extension;

-- 5. 验证修复后的数据
SELECT 
    'Total attachments' as description,
    COUNT(*) as count
FROM contract_attachment 
WHERE del_flag = '0'
UNION ALL
SELECT 
    'Image files' as description,
    COUNT(*) as count
FROM contract_attachment 
WHERE del_flag = '0' 
    AND file_extension IN ('jpg', 'jpeg', 'png', 'gif', 'bmp')
UNION ALL
SELECT 
    'PDF files' as description,
    COUNT(*) as count
FROM contract_attachment 
WHERE del_flag = '0' 
    AND file_extension = 'pdf';
