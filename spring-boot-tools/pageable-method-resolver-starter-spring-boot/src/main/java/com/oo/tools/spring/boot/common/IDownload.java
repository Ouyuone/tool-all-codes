package com.oo.tools.spring.boot.common;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * @author Asta Ou
 * @date 30 May 2023
 */
@Getter
@Setter
public abstract class IDownload {

    private Boolean download = false;

    /**
     * 返回excel的模版类
     *
     * @return
     */
    public Class getExcelHeadClass() {
        return null;
    }

    /**
     * 返回excel name
     * @return
     */
    public String getExcelName(){
        return "";
    }

    public Boolean getDownload() {
        return Objects.isNull(this.download) ? Boolean.FALSE : this.download;
    }

}