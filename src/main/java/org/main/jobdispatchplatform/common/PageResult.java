package org.main.jobdispatchplatform.common;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {
    private List<T> list;
    private long total;
    private int pageNum;
    private int pageSize;
    private int totalPage;

    public static <T> PageResult<T> of(List<T> list,long total ,int pageNum, int pageSize) {
        PageResult<T> result = new PageResult<T>();
        result.setList(list);
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setTotalPage((int) Math.ceil((double) total / pageSize));
        return result;
    }
}
