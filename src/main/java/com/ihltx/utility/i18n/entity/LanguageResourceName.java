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
@TableName("languageresourcenames")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LanguageResourceName implements Serializable {
    @TableId(value = "langResNameId",type = IdType.AUTO)
    private Long langResNameId;
    private Long shopId;
    private String langResName;
    private String langResNameRemark;
    private String langResNameType;
    private Boolean langResNameIsCstomize;
    private Integer langResNameOrder;
    private Boolean langResNameDeleted;
    private Date langResNameCreated;
    private Date langResNameUpdated;
}
