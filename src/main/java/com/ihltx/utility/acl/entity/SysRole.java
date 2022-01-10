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
@TableName("sys_roles")
public class SysRole implements Serializable {
    @TableId(value = "roleId" , type = IdType.AUTO)
    private Long roleId;
    private String language;
    private Long shopId;
    private String roleName;
    private Integer roleTermType;
    private String roleRemark;
    private Boolean roleIsDeleted;
    private Date roleCreated;
}

