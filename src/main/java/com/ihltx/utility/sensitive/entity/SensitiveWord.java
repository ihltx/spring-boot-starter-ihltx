package com.ihltx.utility.sensitive.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sensitive_words")
public class SensitiveWord {

    @TableId(value = "id" , type = IdType.AUTO)
    private Long id;
    private Long shopId;
    private String words;
}
