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
@TableName("cdnurls")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CdnUrl implements Serializable {
    @TableId(value = "cdnUrlId",type = IdType.AUTO)
    private Long cdnUrlId;
    private Long shopId;
    private String cdnUrl;
}
