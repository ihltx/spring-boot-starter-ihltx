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
@TableName("sys_login_failures")
public class SysLoginFailure implements Serializable {
    @TableId(value = "loginId" , type = IdType.AUTO)
    private Long loginId;
    private String userName;
    private Integer failureTimes;
    private String loginIp;
    private Date loginTime;
}

