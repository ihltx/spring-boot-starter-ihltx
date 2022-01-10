package com.ihltx.utility.freemarker.mapper;

import com.ihltx.utility.freemarker.entity.Theme;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;

public interface ThemeMapper extends BaseMapper<Theme> {

    @Insert("DROP TABLE `themes`")
    public void dropThemesTable();

    @Insert("CREATE TABLE `themes` (`themeId`  bigint(20) NOT NULL AUTO_INCREMENT ,`shopId`  bigint(20) NOT NULL DEFAULT 0 COMMENT '所属shopId' ,`themeName`  varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主题名称' ,PRIMARY KEY (`themeId`)) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci AUTO_INCREMENT=1 ROW_FORMAT=DYNAMIC")
    public void createThemesTable();

    @Insert("TRUNCATE TABLE `themes`")
    public void truncateThemesTable();
}
