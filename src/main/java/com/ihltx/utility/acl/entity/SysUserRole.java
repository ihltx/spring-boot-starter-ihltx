package com.ihltx.utility.acl.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_user_roles")
public class SysUserRole implements Serializable {
    @TableId(value = "userRoleId" , type = IdType.AUTO)
    private Long userRoleId;
    private String language;
    private Long shopId;
    private Long userId;
    private Long roleId;
}

