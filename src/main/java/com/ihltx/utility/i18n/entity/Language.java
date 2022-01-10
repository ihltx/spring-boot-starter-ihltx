package com.ihltx.utility.i18n.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("languages")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Language implements Serializable {
    @TableId(value = "langId",type = IdType.AUTO)
    private Long langId;
    private String langCode;
    private Long shopId;
    private String langName;
    private String langRemark;
    private String langCssClass;
    private Integer langOrder;
    private Boolean langEnabled;
    private Boolean langIsDefault;
    private Boolean langDeleted;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",locale = "zh",timezone = "GMT+8")
    private Date langCreated;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",locale = "zh",timezone = "GMT+8")
    private Date langUpdated;
}
