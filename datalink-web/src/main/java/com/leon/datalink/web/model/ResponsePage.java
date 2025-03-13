
package com.leon.datalink.web.model;

import java.util.List;

/**
 * Health Controller.
 *
 * @author Leon
 */
public class ResponsePage<T> {
    private int pageSize;
    private int pageNo;
    private int totalCount;
    private int totalPage;
    private List<T> data;

    public ResponsePage(int pageSize, int pageNo, int totalCount, int totalPage, List<T> data) {
        this.pageSize = pageSize;
        this.pageNo = pageNo;
        this.totalCount = totalCount;
        this.totalPage = totalPage;
        this.data = data;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
