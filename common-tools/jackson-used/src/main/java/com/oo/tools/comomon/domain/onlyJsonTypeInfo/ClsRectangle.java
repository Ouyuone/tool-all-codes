package com.oo.tools.comomon.domain.onlyJsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class ClsRectangle extends ClsShape {
  private Integer width;
  private Integer height;
}