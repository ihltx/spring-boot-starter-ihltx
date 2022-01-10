package com.ihltx.utility.i18n.mapper;

import com.ihltx.utility.i18n.entity.Language;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;

public interface LanguageMapper extends BaseMapper<Language> {

    @Insert("DROP TABLE `languages`")
    public void dropLanguagesTable();

    @Insert("CREATE TABLE `languages` (`langId`  bigint(20) NOT NULL AUTO_INCREMENT ,`langCode`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '语言代码，唯一，如：zh-CN或zh' ,`shopId`  bigint(20) NOT NULL DEFAULT 0 COMMENT ' 所属shopId' ,`langName`  varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '语言名称，唯一' ,`langRemark`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '语言备注' ,`langCssClass`  varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '语言前端css样式类，用于呈现语言' ,`langOrder`  int(11) NOT NULL DEFAULT 100 COMMENT '语言排序，越大越靠前' ,`langEnabled`  tinyint(4) NOT NULL DEFAULT 1 COMMENT '语言状态，0--禁用  1--启用' ,`langIsDefault`  tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否默认语言  0--否   1--是，仅有一种语言能够成为shopId的默认语言' ,`langDeleted`  tinyint(4) NOT NULL DEFAULT 0 COMMENT '语言是否软删除  0--否   1--是' ,`langCreated`  datetime NOT NULL COMMENT '语言创建时间' ,`langUpdated`  datetime NOT NULL COMMENT '语言修改时间' ,PRIMARY KEY (`langId`),UNIQUE INDEX `languageCode_UNIQUE` (`shopId`, `langCode`) USING BTREE ,UNIQUE INDEX `languageName_UNIQUE` (`shopId`, `langName`) USING BTREE )ENGINE=InnoDBDEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='语言表，系统支持的所有语言列表，如果需要新增一种语言需要增加相应的/i18n/message_xxx_XXX.properties文件，同时在系统后台功能中新增语言及相应的语言资源并在前端通过相应的自定义标签<@language code=\\\"\\\" params=\\\"\\\"/>引用相应的资源，后台通过LanguageUtil.get(String key,String[] params)获取相应的语言资源，语言资源虽然均保存到数据库中，但将通过Redis缓存相应的语言资源并生成相应的.js资源文件包来加速前台及后台性能与速度，相应的语言资源包发生变化则Redis以及.js语言资源缓存及文件自动失效。' AUTO_INCREMENT=1000 ROW_FORMAT=DYNAMIC")
    public void createLanguagesTable();

    @Insert("TRUNCATE TABLE `languages`")
    public void truncateLanguagesTable();
}
