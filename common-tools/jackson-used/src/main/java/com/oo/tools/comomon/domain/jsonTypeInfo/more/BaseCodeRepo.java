package com.oo.tools.comomon.domain.jsonTypeInfo.more;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;

/**
 * @author xiaoxuxuy
 */
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = GithubRepo.class, name = "GITHUB"),
        @JsonSubTypes.Type(value = GitlabRepo.class, name = "GITLAB")
})
public class BaseCodeRepo {

    private Long id;

    private String type;

}
