package org.main.jobdispatchplatform.common;

import lombok.Data;

@Data
public class PageRequest {
    private int pageNum = 1;
    private int pageSize = 10;

    public int getOffset() {
        return (pageNum - 1) * pageSize;
    }
}
