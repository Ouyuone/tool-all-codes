package com.oo.tools.spring.boot.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * SexEnum</p>
 *
 * @author Yu.ou
 * @desc:
 * @since: 1.0.0
 */
@Getter
@RequiredArgsConstructor
public enum SexEnum implements DictionaryCode<String> {
    
    MALE("1", "男"),
    
    FEMALE("2", "女");
    
    private final String code;
    private final String name;
    
}
