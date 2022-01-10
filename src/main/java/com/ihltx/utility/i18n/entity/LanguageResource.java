package com.ihltx.utility.i18n.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@TableName("languageresources")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LanguageResource implements Serializable {
    @TableId(value = "langResId",type = IdType.AUTO)
    private Long langResId;
    private Long shopId;
    private String langCode;
    private String langResName;
    private String langResValue;
}
