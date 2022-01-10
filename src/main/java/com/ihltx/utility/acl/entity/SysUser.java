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
@TableName("sys_users")
public class SysUser implements Serializable {
    @TableId(value = "userId" , type = IdType.AUTO)
    private Long userId;
    private String userName;
    private String mobile;
    private String nickName;
    private String headIcon;
    private String password;
    private Integer salt;
    private Boolean disabled;
    private Boolean deleted;
    private Date lastLoginTime;
    private Long shopId;
    private Date created;
}

