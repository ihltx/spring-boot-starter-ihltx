package com.ihltx.utility.freemarker.mapper;

import com.ihltx.utility.freemarker.entity.CdnUrl;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;

public interface CdnUrlMapper extends BaseMapper<CdnUrl> {

    @Insert("DROP TABLE `cdnurls`")
    public void dropThemesTable();

    @Insert("CREATE TABLE `cdnurls` (`cdnUrlId`  bigint(20) NOT NULL AUTO_INCREMENT ,`shopId`  bigint(20) NOT NULL DEFAULT 0 COMMENT '所属shopId' ,`cdnUrl`  varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'cdnUrl' ,PRIMARY KEY (`cdnUrlId`)) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci AUTO_INCREMENT=1 ROW_FORMAT=DYNAMIC")
    public void createThemesTable();

    @Insert("TRUNCATE TABLE `cdnurls`")
    public void truncateThemesTable();
}
