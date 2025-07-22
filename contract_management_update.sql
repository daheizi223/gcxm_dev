-- 合同管理模块数据库脚本（更新版）
-- 适配MySQL数据库
-- 此脚本会检查表是否存在，避免重复创建

-- 1. 删除已存在的表（如果需要重新创建）
-- 注意：执行前请备份数据！
-- DROP TABLE IF EXISTS `contract_operation_log`;
-- DROP TABLE IF EXISTS `contract_attachment`;
-- DROP TABLE IF EXISTS `contract_info`;

-- 2. 创建合同信息表（如果不存在）
CREATE TABLE IF NOT EXISTS `contract_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `contract_no` varchar(50) NOT NULL COMMENT '合同编号',
  `contract_name` varchar(200) NOT NULL COMMENT '合同名称',
  `main_content` text COMMENT '主要内容',
  `contract_amount` decimal(15,2) DEFAULT NULL COMMENT '合同金额(元)',
  `signing_date` date DEFAULT NULL COMMENT '签订时间',
  `party_a` varchar(100) DEFAULT NULL COMMENT '甲方',
  `party_b` varchar(100) DEFAULT NULL COMMENT '乙方',
  `contract_status` varchar(20) DEFAULT 'DRAFT' COMMENT '合同状态：DRAFT-草稿,SIGNED-已签订,EXECUTING-执行中,COMPLETED-已完成,TERMINATED-已终止',
  `start_date` date DEFAULT NULL COMMENT '合同开始日期',
  `end_date` date DEFAULT NULL COMMENT '合同结束日期',
  `contact_person_a` varchar(50) DEFAULT NULL COMMENT '甲方联系人',
  `contact_phone_a` varchar(20) DEFAULT NULL COMMENT '甲方联系电话',
  `contact_person_b` varchar(50) DEFAULT NULL COMMENT '乙方联系人',
  `contact_phone_b` varchar(20) DEFAULT NULL COMMENT '乙方联系电话',
  `remark` text COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_contract_no` (`contract_no`),
  KEY `idx_contract_name` (`contract_name`),
  KEY `idx_signing_date` (`signing_date`),
  KEY `idx_contract_status` (`contract_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合同信息表';

-- 3. 创建合同附件表（如果不存在）
CREATE TABLE IF NOT EXISTS `contract_attachment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `contract_id` bigint(20) NOT NULL COMMENT '合同ID',
  `file_name` varchar(200) NOT NULL COMMENT '文件名称',
  `file_original_name` varchar(200) NOT NULL COMMENT '文件原始名称',
  `file_path` varchar(500) NOT NULL COMMENT '文件路径',
  `file_size` bigint(20) DEFAULT NULL COMMENT '文件大小(字节)',
  `file_type` varchar(50) DEFAULT NULL COMMENT '文件类型',
  `file_extension` varchar(10) DEFAULT NULL COMMENT '文件扩展名',
  `file_category` varchar(20) DEFAULT 'CONTRACT' COMMENT '文件分类：CONTRACT-合同文件,ATTACHMENT-附件,IMAGE-图片',
  `upload_user` varchar(64) DEFAULT NULL COMMENT '上传用户',
  `upload_time` datetime DEFAULT NULL COMMENT '上传时间',
  `is_main` char(1) DEFAULT '0' COMMENT '是否主要文件（0否 1是）',
  `sort_order` int(11) DEFAULT 0 COMMENT '排序',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  PRIMARY KEY (`id`),
  KEY `idx_contract_id` (`contract_id`),
  KEY `idx_file_category` (`file_category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合同附件表';

-- 4. 创建合同操作日志表（如果不存在）
CREATE TABLE IF NOT EXISTS `contract_operation_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `contract_id` bigint(20) NOT NULL COMMENT '合同ID',
  `operation_type` varchar(20) NOT NULL COMMENT '操作类型：CREATE-创建,UPDATE-更新,DELETE-删除,UPLOAD-上传附件,DOWNLOAD-下载,PREVIEW-预览',
  `operation_desc` varchar(500) DEFAULT NULL COMMENT '操作描述',
  `operation_user` varchar(64) NOT NULL COMMENT '操作用户',
  `operation_time` datetime NOT NULL COMMENT '操作时间',
  `ip_address` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `user_agent` varchar(500) DEFAULT NULL COMMENT '用户代理',
  `remark` text COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_contract_id` (`contract_id`),
  KEY `idx_operation_type` (`operation_type`),
  KEY `idx_operation_user` (`operation_user`),
  KEY `idx_operation_time` (`operation_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合同操作日志表';

-- 5. 添加外键约束（如果不存在）
-- 检查外键是否已存在，避免重复添加
SET @constraint_exists = (SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS 
                         WHERE CONSTRAINT_SCHEMA = DATABASE() 
                         AND TABLE_NAME = 'contract_attachment' 
                         AND CONSTRAINT_NAME = 'fk_contract_attachment_contract_id');

SET @sql = IF(@constraint_exists = 0, 
             'ALTER TABLE contract_attachment ADD CONSTRAINT fk_contract_attachment_contract_id FOREIGN KEY (contract_id) REFERENCES contract_info(id) ON DELETE CASCADE;',
             'SELECT "外键约束已存在，跳过添加" AS message;');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 6. 插入初始数据（如果不存在）
INSERT IGNORE INTO `contract_info` (`contract_no`, `contract_name`, `main_content`, `contract_amount`, `signing_date`, `party_a`, `party_b`, `contract_status`, `remark`, `create_by`, `create_time`) VALUES
('CT202501001', '软件开发服务合同', '为甲方提供定制化软件开发服务，包括需求分析、系统设计、编码实现、测试部署等全流程服务。', 500000.00, '2025-01-15', '华中科技有限公司', '东气工程技术公司', 'SIGNED', '重要项目合同', 'admin', NOW()),
('CT202501002', '设备采购合同', '采购工程项目所需的专业设备及配套软件，确保项目顺利实施。', 1200000.00, '2025-01-20', '东气工程技术公司', '北京科技设备有限公司', 'EXECUTING', '设备质保期2年', 'admin', NOW());

-- 7. 数据字典配置
-- 合同状态字典类型
INSERT IGNORE INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark) VALUES 
('合同状态', 'contract_status', '0', 'admin', NOW(), '合同状态字典');

-- 获取刚插入或已存在的字典类型ID
SET @contract_status_dict_id = (SELECT dict_id FROM sys_dict_type WHERE dict_type = 'contract_status' LIMIT 1);

-- 合同状态字典数据
INSERT IGNORE INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) VALUES
(1, '草稿', 'DRAFT', 'contract_status', '', 'info', 'N', '0', 'admin', NOW(), '草稿状态'),
(2, '已签订', 'SIGNED', 'contract_status', '', 'success', 'N', '0', 'admin', NOW(), '已签订状态'),
(3, '执行中', 'EXECUTING', 'contract_status', '', 'primary', 'N', '0', 'admin', NOW(), '执行中状态'),
(4, '已完成', 'COMPLETED', 'contract_status', '', 'success', 'N', '0', 'admin', NOW(), '已完成状态'),
(5, '已终止', 'TERMINATED', 'contract_status', '', 'danger', 'N', '0', 'admin', NOW(), '已终止状态');

-- 文件分类字典类型
INSERT IGNORE INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark) VALUES 
('文件分类', 'file_category', '0', 'admin', NOW(), '文件分类字典');

-- 文件分类字典数据
INSERT IGNORE INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) VALUES
(1, '合同文件', 'CONTRACT', 'file_category', '', 'primary', 'N', '0', 'admin', NOW(), '合同文件'),
(2, '附件', 'ATTACHMENT', 'file_category', '', 'info', 'N', '0', 'admin', NOW(), '附件'),
(3, '图片', 'IMAGE', 'file_category', '', 'success', 'N', '0', 'admin', NOW(), '图片');

-- 8. 菜单权限配置
-- 主菜单：合同管理
INSERT IGNORE INTO sys_menu (menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) VALUES
('合同管理', 0, 6, 'contract', NULL, NULL, 1, 0, 'M', '0', '0', NULL, 'documentation', 'admin', NOW(), '合同管理菜单');

-- 获取合同管理菜单ID
SET @contract_menu_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '合同管理' AND parent_id = 0 LIMIT 1);

-- 子菜单：合同信息
INSERT IGNORE INTO sys_menu (menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) VALUES
('合同信息', @contract_menu_id, 1, 'info', 'gcxm/contract/index', NULL, 1, 0, 'C', '0', '0', 'gcxm:contract:list', 'form', 'admin', NOW(), '合同信息菜单');

-- 获取合同信息菜单ID
SET @contract_info_menu_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '合同信息' AND parent_id = @contract_menu_id LIMIT 1);

-- 功能按钮
INSERT IGNORE INTO sys_menu (menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) VALUES
('合同查询', @contract_info_menu_id, 1, '', '', NULL, 1, 0, 'F', '0', '0', 'gcxm:contract:query', '#', 'admin', NOW(), '合同查询'),
('合同新增', @contract_info_menu_id, 2, '', '', NULL, 1, 0, 'F', '0', '0', 'gcxm:contract:add', '#', 'admin', NOW(), '合同新增'),
('合同修改', @contract_info_menu_id, 3, '', '', NULL, 1, 0, 'F', '0', '0', 'gcxm:contract:edit', '#', 'admin', NOW(), '合同修改'),
('合同删除', @contract_info_menu_id, 4, '', '', NULL, 1, 0, 'F', '0', '0', 'gcxm:contract:remove', '#', 'admin', NOW(), '合同删除'),
('合同导出', @contract_info_menu_id, 5, '', '', NULL, 1, 0, 'F', '0', '0', 'gcxm:contract:export', '#', 'admin', NOW(), '合同导出'),
('附件上传', @contract_info_menu_id, 6, '', '', NULL, 1, 0, 'F', '0', '0', 'gcxm:contract:upload', '#', 'admin', NOW(), '附件上传'),
('附件下载', @contract_info_menu_id, 7, '', '', NULL, 1, 0, 'F', '0', '0', 'gcxm:contract:download', '#', 'admin', NOW(), '附件下载'),
('附件预览', @contract_info_menu_id, 8, '', '', NULL, 1, 0, 'F', '0', '0', 'gcxm:contract:preview', '#', 'admin', NOW(), '附件预览');

-- 9. 为管理员角色分配权限（可选）
-- 获取管理员角色ID
SET @admin_role_id = (SELECT role_id FROM sys_role WHERE role_key = 'admin' LIMIT 1);

-- 获取所有合同相关菜单ID
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT @admin_role_id, menu_id FROM sys_menu 
WHERE menu_name IN ('合同管理', '合同信息', '合同查询', '合同新增', '合同修改', '合同删除', '合同导出', '附件上传', '附件下载', '附件预览');

-- 10. 完成提示
SELECT '合同管理模块数据库初始化完成！' AS message;
