package com.oo.tools.comomon.domain.jsonTypeInfo;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
//上面使用MINIMAL_CLASS 或者 CLASS可以不使用@JsonSubTypes注解
//如果使用NAME 或者 SIMPLE_NAME 就需要使用@JsonSubTypes注解
@JsonSubTypes({
        @JsonSubTypes.Type(value = ClsRectangle.class, name = "ClsRectangle"),
        @JsonSubTypes.Type(value = ClsCircle.class, name = "ClsCircle")
})
public class ClsShape {
}