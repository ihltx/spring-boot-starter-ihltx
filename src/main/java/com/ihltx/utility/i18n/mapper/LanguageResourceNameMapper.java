package com.ihltx.utility.i18n.mapper;

import com.ihltx.utility.i18n.entity.LanguageResourceName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;

public interface LanguageResourceNameMapper extends BaseMapper<LanguageResourceName> {

    @Insert("DROP TABLE `languageresourcenames`")
    public void dropLanguageResourceNamesTable();

    @Insert("CREATE TABLE `languageresourcenames` (`langResNameId`  bigint(20) NOT NULL AUTO_INCREMENT ,`shopId`  bigint(20) NOT NULL COMMENT '所属店铺ID，0店铺为模板店铺，默认情况下，新店铺将从0店铺复制所有语言名称' ,`langResName`  varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '语言资源名称，在同一店铺中唯一' ,`langResNameRemark`  text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '资源名称描述' ,`langResNameType`  tinyint(4) NOT NULL DEFAULT 0 COMMENT '语言资源名称类型：0--文字   1--图片   2--音频    3--视频' ,`langResNameIsCstomize`  tinyint(4) NOT NULL COMMENT '是否店铺自定义资源名称  0--系统   1--店铺自定义' ,`langResNameOrder`  int(11) NOT NULL DEFAULT 100 COMMENT '语言资源名称排序' ,`langResNameDeleted`  tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否软删除 0--否  1--是' ,`langResNameCreated`  datetime NULL DEFAULT NULL COMMENT '创建时间' ,`langResNameUpdated`  datetime NULL DEFAULT NULL COMMENT '修改时间' ,PRIMARY KEY (`langResNameId`),UNIQUE INDEX `shopId_langResName` (`shopId`, `langResName`) USING BTREE ) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='语言资源名称表，用于描述每一种语言所需要设置的所有语言资源名称' AUTO_INCREMENT=100000 ROW_FORMAT=DYNAMIC")
    public void createLanguageResourceNamesTable();

    @Insert("TRUNCATE TABLE `languageresourcenames`")
    public void truncateLanguageResourceNamesTable();

}
