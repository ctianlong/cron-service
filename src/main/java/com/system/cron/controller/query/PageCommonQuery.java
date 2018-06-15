package com.system.cron.controller.query;

/**
 * 封装公共分页查询参数
 * Created by ctianlong on 2018/5/28.
 */
public class PageCommonQuery
{
    // 不添加默认值，类中定义两个常量在需要时指定，更灵活
    private Integer pageNum;

    private Integer pageSize;

    private String orderColumn;

    private String orderDir;

    public Integer getPageNum()
    {
        return pageNum;
    }

    public void setPageNum(Integer pageNum)
    {
        this.pageNum = pageNum;
    }

    public Integer getPageSize()
    {
        return pageSize;
    }

    public void setPageSize(Integer pageSize)
    {
        this.pageSize = pageSize;
    }

    public String getOrderColumn()
    {
        return orderColumn;
    }

    public void setOrderColumn(String orderColumn)
    {
        this.orderColumn = orderColumn;
    }

    public String getOrderDir()
    {
        return orderDir;
    }

    public void setOrderDir(String orderDir)
    {
        this.orderDir = orderDir;
    }

    @Override
    public String toString()
    {
        return "PageCommonQuery{" +
            "pageNum=" + pageNum +
            ", pageSize=" + pageSize +
            ", orderColumn='" + orderColumn + '\'' +
            ", orderDir='" + orderDir + '\'' +
            '}';
    }
}
