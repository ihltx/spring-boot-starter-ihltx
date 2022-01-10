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
@TableName("sys_user_modules")
public class SysUserModule implements Serializable {
    @TableId(value = "userModuleId" , type = IdType.AUTO)
    private Long userModuleId;
    private String language;
    private Long shopId;
    private Long userId;
    private Long moduleId;
}

