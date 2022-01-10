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
@TableName("sys_role_modules")
public class SysRoleModule implements Serializable {
    @TableId(value = "roleModuleId" , type = IdType.AUTO)
    private Long roleModuleId;
    private String language;
    private Long shopId;
    private Long roleId;
    private Long moduleId;
}

