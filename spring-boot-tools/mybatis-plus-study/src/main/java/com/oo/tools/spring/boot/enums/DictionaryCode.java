package com.oo.tools.spring.boot.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:yu.ou@alpha-ess.com">欧宇</a>
 * 2024/1/15 15:55
 */
public interface DictionaryCode<C extends Serializable> {
    
    @JsonValue
    C getCode();
    
    default Map<C, DictionaryCode<C>> getDictionaryMap() {
        DictionaryCode<C>[] enumConstants = this.getClass().getEnumConstants();
        return Arrays.stream(enumConstants).collect(Collectors.toMap(DictionaryCode::getCode, e -> e));
    }
}
