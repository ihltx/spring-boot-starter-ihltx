package com.ihltx.utility.freemarker.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@TableName("themes")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Theme implements Serializable {
    @TableId(value = "themeId",type = IdType.AUTO)
    private Long themeId;
    private Long shopId;
    private String themeName;
}
