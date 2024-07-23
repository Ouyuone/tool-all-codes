package com.oo.tools.spring.boot.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author <a href="mailto:yu.ou@alpha-ess.com">ouyu</a>
 * @date: 2024/02/22 13:39:44
 */
@Target(ElementType.TYPE)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface CheckRequest {
    /**
     *
     * 使用方式：
     *   public void checkRequest() {
     *         boolean must = interval != null && StringUtils.isNotBlank(date) && state != null;
     *
     *         if (!must) {
     *             throw new CheckRequestException(String.format("params interval:{%s} or date:{%s} or state :{%s} not null", interval, date, state));
     *         }
     *
     *         List<StateEnum> ai_states = List.of(StateEnum.NSW, StateEnum.QLD, StateEnum.SA, StateEnum.VIC);
     *         if (!ai_states.contains(state)) {
     *             throw new CheckRequestException(String.format("state not exist in %s state error:{%s}", ai_states, state));
     *         }
     *         List<Integer> intervalValidate = List.of(5, 30);
     *         if (!intervalValidate.contains(interval)) {
     *             throw new CheckRequestException(String.format("interval :{%s} just only in %s", interval, intervalValidate));
     *         }
     *         //把LocalDate 变为LocalDateTime
     *         date = date + " 00:00:00";
     *
     *     }
     *
     */
    @AliasFor("checkMethod")
    String value() default "checkRequest";

    @AliasFor("value")
    String checkMethod() default "checkRequest";
}