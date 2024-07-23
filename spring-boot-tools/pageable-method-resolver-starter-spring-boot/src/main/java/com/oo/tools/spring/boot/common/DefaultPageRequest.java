package com.oo.tools.spring.boot.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:yu.ou@alpha-ess.com">ouyu</a>
 * @date: 2024/02/22 13:39:44
 */
@Setter
public class DefaultPageRequest extends IDownload implements Pageable {

    @JsonIgnore
    @Schema(hidden = true)
    private Pageable pageable;

    public DefaultPageRequest() {
        this(Constants.DEFAULT_PAGE_INDEX, Constants.DEFAULT_PAGE_SIZE);
    }

    public DefaultPageRequest(int page, int size) {
        this(page, size, Sort.unsorted());
    }

    public DefaultPageRequest(int page, int size, Sort sort) {
        setPageable(PageRequest.of(page, size, sort));
    }

    @Schema(hidden = true, defaultValue = "1")
    public void setPageNumber(int pageNumber) {
        setPageable(PageRequest.of(pageNumber > 0 ? pageNumber - 1 : 0, getPageSize(), getSort()));
    }

    public void setPage(int pageNumber) {
        setPageNumber(pageNumber);
    }

    @Schema(hidden = true, defaultValue = "20")
    public void setPageSize(int pageSize) {
        setPageable(PageRequest.of(getPageNumber(), pageSize, getSort()));
    }

    public void setSize(int pageSize) {
        setPageSize(pageSize);
    }

    @Override
    @Schema(hidden = true)
    public int getPageNumber() {
        return this.pageable.getPageNumber();
    }

    @Override
    @Schema(hidden = true)
    public int getPageSize() {
        return this.pageable.getPageSize();
    }

    @Override
    @Schema(hidden = true)
    public long getOffset() {
        return this.pageable.getOffset();
    }

    @Override
    @Schema(name = "sort", title = "排序", examples = {"id desc", "name"}, hidden = true)
    public Sort getSort() {
        return this.pageable.getSort();
    }

    @Override
    @Schema(hidden = true)
    public Pageable next() {
        return this.pageable.next();
    }

    @Override
    @Schema(hidden = true)
    public Pageable previousOrFirst() {
        return this.pageable.previousOrFirst();
    }

    @Override
    @Schema(hidden = true)
    public Pageable first() {
        return this.pageable.first();
    }

    @Override
    @Schema(hidden = true)
    public Pageable withPage(int pageNumber) {
        return this.pageable.withPage(pageNumber);
    }

    @Override
    @Schema(hidden = true)
    public boolean hasPrevious() {
        return this.pageable.hasPrevious();
    }

    @Override
    @Schema(hidden = true)
    public boolean isUnpaged() {
        return this.pageable.isUnpaged();
    }

    @Override
    @Schema(hidden = true)
    public boolean isPaged() {
        return this.pageable.isPaged();
    }

    public void withSort(Sort sort) {
        setPageable(PageRequest.of(getPageNumber(), getPageSize(), sort));
    }

    /**
     * 此方法是集成pageHelper的排序使用
     *
     * @return order by ....
     */
    public String getOrderBy() {
        List<String> orders = pageable.getSort().stream().unordered().map(order -> order.getProperty() + " " + order.getDirection()).collect(Collectors.toList());
        StringJoiner stringJoiner = new StringJoiner(",");

        orders.forEach(order -> stringJoiner.add(order));
        return stringJoiner.toString();
    }

    /**
     * 兼容 Mybatis pageHelper
     * 小于等于 0 都从第一页开始查询
     *
     * @param pageable
     */
    public void setPageable(Pageable pageable) {
        int pageNumber;
        this.pageable = PageRequest.of((pageNumber = pageable.getPageNumber()) > 0 ? pageNumber : 1, pageable.getPageSize(), pageable.getSort());
    }

    /**
     * 用来下载数据
     */
    public void checkDownload() {
        if (Objects.nonNull(super.getDownload()) && super.getDownload()) {
            this.setPageNumber(0);
            this.setPageSize(Integer.MAX_VALUE);
        }
    }
}