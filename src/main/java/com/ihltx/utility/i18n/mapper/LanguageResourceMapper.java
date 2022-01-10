package com.ihltx.utility.i18n.mapper;

import com.ihltx.utility.i18n.entity.LanguageResource;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;

public interface LanguageResourceMapper  extends BaseMapper<LanguageResource> {

    @Insert("DROP TABLE `languageresources`")
    public void dropLanguageResourcesTable();

    @Insert("CREATE TABLE `languageresources` (`langResId`  bigint(11) NOT NULL AUTO_INCREMENT COMMENT '语言资源ID，唯一' ,`shopId`  bigint(20) NOT NULL COMMENT '所属店铺ID,0店铺为模板店铺，默认情况下，新店铺将从0店铺复制所有语言资源' ,`langCode`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '所属语言代码' ,`langResName`  varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '语言资源名称' ,`langResValue`  text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '语言资源值' ,PRIMARY KEY (`langResId`),UNIQUE INDEX `shopId_langCode_langResName` (`shopId`, `langCode`, `langResName`) USING BTREE ) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='语言资源表，每一种语言均需要设置相同的语言资源，资源包括字符串，图片，视频，音频，当相应的语言资源发生变化，对应的Redis缓存及.js资源文件均失效' AUTO_INCREMENT=100000 ROW_FORMAT=DYNAMIC")
    public void createLanguageResourcesTable();

    @Insert("TRUNCATE TABLE `languageresources`")
    public void truncateLanguageResourcesTable();

}
