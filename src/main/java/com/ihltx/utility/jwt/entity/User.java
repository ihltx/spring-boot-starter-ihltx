package com.ihltx.utility.jwt.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("用户")
public class User implements Serializable {

	@ApiModelProperty(hidden = true)
	private long user_id;
	@ApiModelProperty(value = "用户名")
	private String user_name;
	@ApiModelProperty(value = "密码")
	private String user_password;
	@ApiModelProperty(hidden = true)
	private int user_created;
	@ApiModelProperty(hidden = true)
	private int user_updated;
	@ApiModelProperty(hidden = true)
	private Boolean user_deleted;
}

	