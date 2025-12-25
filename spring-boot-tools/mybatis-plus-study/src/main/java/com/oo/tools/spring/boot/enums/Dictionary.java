package com.oo.tools.spring.boot.enums;

import java.io.Serializable;

/**
 * @author <a href="mailto:yu.ou@alpha-ess.com">欧宇</a>
 * 2024/1/15 15:57
 */
public interface Dictionary<C extends Serializable> extends DictionaryCode<C>, DictionaryName {
    
    default int getSortIndex() {
        return 0;
    }

    default String getDescription() {
        return this.getName();
    }

    default String getExpands() {
        return null;
    }

    @Override
    default String getName() {
        return String.valueOf(this.getCode());
    }

}
