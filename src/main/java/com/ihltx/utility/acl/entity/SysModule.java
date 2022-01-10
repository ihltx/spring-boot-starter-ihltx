package com.ihltx.utility.acl.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_modules")
public class SysModule implements Serializable {
    @TableId(value = "moduleId" , type = IdType.AUTO)
    private Long moduleId;
    private String language;
    private Long shopId;
    private String moduleCode;
    private Integer moduleTermType;
    private String moduleName;
    private String moduleShortName;
    private String moduleVersion;
    private Double moduleShopVersion;
    private String moduleIcon;
    private String moduleUrl;
    private String moduleQueryString;
    private Long moduleParentId;
    private String moduleTreePath;
    private String moduleRemark;
    private Boolean moduleIsMenu;
    private Integer moduleSort;
    private Integer moduleProductCategory;
    private Boolean moduleIsCustomized;
    private Boolean moduleIsPlatform;
    private Boolean moduleIsDeleted;
    private Date moduleCreated;
}

