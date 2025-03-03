package comon.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.io.Serializable;

/**
 * @author <a href="mailto:yu.ou@alpha-ess.com">欧宇</a>
 * 2024/1/15 15:55
 */
public interface DictionaryCode<C extends Serializable> {

    @JsonValue
    C getCode();

}
