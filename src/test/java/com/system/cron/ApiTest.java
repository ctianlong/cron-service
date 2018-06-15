package com.system.cron;

import com.github.pagehelper.PageInfo;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.system.cron.entity.Task;
import com.system.cron.service.job.HttpInvokeJob;
import com.system.cron.util.HttpUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ctianlong on 2018/5/23.
 */
public class ApiTest {

    @Test
    public void foolTest() {
//        List<Task> tasks = new ArrayList<>();
//        System.out.println(new PageInfo<>(tasks));
//        System.out.println(new HttpUtil.Response().toString());
//        System.out.println(System.getProperty("java.io.tmpdir"));
//        System.out.println(TaskConsts.URL_PATTERN.matcher("    ").matches());
//        System.out.println(System.currentTimeMillis());
//        System.out.println(new Date(1527481827595L).getTime());
//        System.out.println(HttpInvokeJob.class.getName());
//        System.out.println(HttpInvokeJob.class.getCanonicalName());
//        System.out.println(HttpInvokeJob.class.getSimpleName());
//        System.out.println(HttpInvokeJob.class.getTypeName());
//        System.out.println(Consts.ISO_8859_1.toString());

    }

    @Test
    public void testHttpUtil() throws Exception {
        String url = "https://easy-mock.com/mock/5a782ff478b29e38446a96c8/studentApi/student";
//        System.out.println(HttpUtil.httpGet("https://yesno.wtf/api", true)); // 无法访问
        System.out.println(HttpUtil.get(url,null, null)); // 正常访问
    }

}
