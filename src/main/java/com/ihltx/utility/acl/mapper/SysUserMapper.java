package com.ihltx.utility.acl.mapper;

import com.ihltx.utility.acl.entity.SysModule;
import com.ihltx.utility.acl.entity.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysUserMapper extends BaseMapper<SysUser> {

    List<SysModule> getModulesByUserId(@Param("userId") Long userId, @Param("language") String language, @Param("shopId") Long shopId);
}
