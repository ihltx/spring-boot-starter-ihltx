package com.ihltx.utility.sensitive.mapper;

import com.ihltx.utility.sensitive.entity.SensitiveWord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;

public interface SensitiveWordMapper extends BaseMapper<SensitiveWord> {

    @Insert("DROP TABLE `sensitive_words`")
    public void dropThemesTable();

    @Insert("CREATE TABLE `sensitive_words` (`id` bigint NOT NULL AUTO_INCREMENT , `shopId`  bigint(20) NOT NULL COMMENT '所属ShopId' ,`words` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '敏感词列表以,间隔' ,PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci AUTO_INCREMENT=1 ROW_FORMAT=DYNAMIC")
    public void createThemesTable();

    @Insert("TRUNCATE TABLE `sensitive_words`")
    public void truncateThemesTable();
}
