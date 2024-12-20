package com.oo.tools.comomon.domain.jsonTypeInfo.more;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author xiaoxuxuy
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GitlabRepo extends BaseCodeRepo {

    private String name;

    private String path;

    private String description;

}
