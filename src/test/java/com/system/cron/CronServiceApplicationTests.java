package com.system.cron;

import com.system.cron.dao.TaskDao;
import com.system.cron.entity.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CronServiceApplicationTests {

    private final Logger logger = LoggerFactory.getLogger(CronServiceApplicationTests.class);

	@Autowired
	private TaskDao taskDao;

	@Test
	public void contextLoads() {
		System.out.println("--------------------------------");
//		taskDao.updateIsEnabledById(false, 4L, new Date());
//		taskDao.count();
		Task t = new Task();
		t.setId(12L);
//		t.setName("");
		t.setModifiedTime(new Date());
		t.setCreateTime(new Date());
		t.setRequestType(0);
		t.setCronExpression("*/10 * * * * ?");
		t.setUrl("http://localhost:8080/task/10");
		t.setEnabled(false);
        try
        {
            System.out.println(taskDao.updateById(t));
        } catch (Exception e)
        {
            logger.error("+++++++++++++++++++++++++++++ {}", "222", e);
            e.printStackTrace();

        }

		System.out.println("--------------------------------");

	}

}
