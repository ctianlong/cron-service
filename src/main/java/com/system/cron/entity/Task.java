package com.system.cron.entity;

import com.system.cron.util.mybatis.typehandler.BigintDateTypeHandler;
import org.apache.ibatis.type.JdbcType;
import tk.mybatis.mapper.annotation.ColumnType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 任务实体，对应数据库
 */
@Table(name = "tbl_task")
public class Task implements Serializable
{
    /**
     * 任务id，唯一标识。主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 任务名称，简单表达该任务特征
     */
    private String name;

    /**
     * 任务描述，可详细描述任务
     */
    private String description;

    /**
     * cron定时表达式
     */
    @Column(name = "cron_expression")
    private String cronExpression;

    /**
     * 调用接口url地址
     */
    private String url;

    /**
     * 请求类型，0表示get，1表示post
     */
    @Column(name = "request_type")
    private Integer requestType;

    /**
     * 请求参数，json格式字符串，仅当请求类型为post时将通过http body以json格式传递该参数
     */
    @Column(name = "request_data")
    private String requestData;

    /**
     * 是否启用
     */
    @Column(name = "is_enabled")
    @ColumnType(jdbcType = JdbcType.INTEGER)
    private Boolean enabled;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    @ColumnType(jdbcType = JdbcType.BIGINT, typeHandler = BigintDateTypeHandler.class)
    private Date createTime;

    /**
     * 上次修改时间
     */
    @Column(name = "modified_time")
    @ColumnType(jdbcType = JdbcType.BIGINT, typeHandler = BigintDateTypeHandler.class)
    private Date modifiedTime;

    /**
     * 获取任务id，唯一标识。主键
     *
     * @return id - 任务id，唯一标识。主键
     */
    public Long getId()
    {
        return id;
    }

    /**
     * 设置任务id，唯一标识。主键
     *
     * @param id 任务id，唯一标识。主键
     */
    public void setId(Long id)
    {
        this.id = id;
    }

    /**
     * 获取任务名称，简单表达该任务特征
     *
     * @return name - 任务名称，简单表达该任务特征
     */
    public String getName()
    {
        return name;
    }

    /**
     * 设置任务名称，简单表达该任务特征
     *
     * @param name 任务名称，简单表达该任务特征
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * 获取任务描述，可详细描述任务
     *
     * @return description - 任务描述，可详细描述任务
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * 设置任务描述，可详细描述任务
     *
     * @param description 任务描述，可详细描述任务
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * 获取cron定时表达式
     *
     * @return cron_expression - cron定时表达式
     */
    public String getCronExpression()
    {
        return cronExpression;
    }

    /**
     * 设置cron定时表达式
     *
     * @param cronExpression cron定时表达式
     */
    public void setCronExpression(String cronExpression)
    {
        this.cronExpression = cronExpression;
    }

    /**
     * 获取调用接口url地址
     *
     * @return url - 调用接口url地址
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * 设置调用接口url地址
     *
     * @param url 调用接口url地址
     */
    public void setUrl(String url)
    {
        this.url = url;
    }

    /**
     * 获取请求类型，0表示get，1表示post
     *
     * @return request_type - 请求类型，0表示get，1表示post
     */
    public Integer getRequestType()
    {
        return requestType;
    }

    /**
     * 设置请求类型，0表示get，1表示post
     *
     * @param requestType 请求类型，0表示get，1表示post
     */
    public void setRequestType(Integer requestType)
    {
        this.requestType = requestType;
    }

    /**
     * 获取请求参数，json格式字符串，仅当请求类型为post时将通过http body以json格式传递该参数
     *
     * @return request_data - 请求参数，json格式字符串，仅当请求类型为post时将通过http body以json格式传递该参数
     */
    public String getRequestData()
    {
        return requestData;
    }

    /**
     * 设置请求参数，json格式字符串，仅当请求类型为post时将通过http body以json格式传递该参数
     *
     * @param requestData 请求参数，json格式字符串，仅当请求类型为post时将通过http body以json格式传递该参数
     */
    public void setRequestData(String requestData)
    {
        this.requestData = requestData;
    }

    /**
     * 获取是否启用
     *
     * @return is_enabled - 是否启用
     */
    public Boolean getEnabled()
    {
        return enabled;
    }

    /**
     * 设置是否启用
     *
     * @param enabled 是否启用
     */
    public void setEnabled(Boolean enabled)
    {
        this.enabled = enabled;
    }

    /**
     * 获取创建时间
     *
     * @return create_time - 创建时间
     */
    public Date getCreateTime()
    {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    /**
     * 获取上次修改时间
     *
     * @return modified_time - 上次修改时间
     */
    public Date getModifiedTime()
    {
        return modifiedTime;
    }

    /**
     * 设置上次修改时间
     *
     * @param modifiedTime 上次修改时间
     */
    public void setModifiedTime(Date modifiedTime)
    {
        this.modifiedTime = modifiedTime;
    }

    @Override
    public String toString()
    {
        return "Task{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", cronExpression='" + cronExpression + '\'' +
            ", url='" + url + '\'' +
            ", requestType=" + requestType +
            ", requestData='" + requestData + '\'' +
            ", enabled=" + enabled +
            ", createTime=" + createTime +
            ", modifiedTime=" + modifiedTime +
            '}';
    }
}