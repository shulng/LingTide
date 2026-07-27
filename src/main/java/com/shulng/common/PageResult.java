package com.shulng.common;

import java.util.List;

public class PageResult<T> {
    private List<T> records;
    private Long total;
    private Integer current;
    private Integer size;

    public PageResult() {}

    public PageResult(List<T> records, Long total, Integer current, Integer size) {
        this.records = records;
        this.total = total;
        this.current = current;
        this.size = size;
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        this.current = current;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public static <T> PageResult<T> of(List<T> records, Long total, Integer current, Integer size) {
        return new PageResult<>(records, total, current, size);
    }

    public static <T> PageResult<T> empty(Integer current, Integer size) {
        return new PageResult<>(List.of(), 0L, current, size);
    }
}