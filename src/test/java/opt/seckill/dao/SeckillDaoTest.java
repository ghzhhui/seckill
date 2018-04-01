package opt.seckill.dao;

import opt.seckill.entity.Seckill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * 配置spring和junit整合,junit启动时加载springIOC容器
 * spring-test,junit
 */
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillDaoTest {

    //注入Dao实现类依赖
    @Resource
    private SeckillDao seckillDao;

    /**
     *
     * ### Error querying database.  Cause: org.springframework.jdbc.CannotGetJdbcConnectionException: Could not get JDBC Connection; nested exception is java.sql.SQLException: Connections could not be acquired from the underlying database!
     ### The error may exist in file [D:\IDEA\Seckill\target\classes\mapper\SeckillDao.xml]
     ### The error may involve opt.seckill.dao.SeckillDao.queryById
     ### The error occurred while executing a query
     ### Cause: org.springframework.jdbc.CannotGetJdbcConnectionException:
     Could not get JDBC Connection; nested exception is java.sql.SQLException:
     Connections could not be acquired from the underlying database!

     Caused by: org.springframework.jdbc.CannotGetJdbcConnectionException:
     Could not get JDBC Connection; nested exception is java.sql.SQLException:
     Connections could not be acquired from the underlying database!
      日他大爷，错误的原因尽然是把username 改成user 卧槽！！！
     */
    @Test
    public void testQueryById() throws Exception {
        long id = 1000;
        Seckill seckill = seckillDao.queryById(id);
        System.out.println(seckill.getName());
        System.out.println(seckill);
    }
    @Test
    public void testQueryAll() throws Exception {
        //Parameter 'offset' not found. Available parameters are [0, 1, param1, param2]
        // java没有保存形参的记录:queryAll(int offet,int limit) ->queryAll(arg0,arg1)
        List<Seckill> seckills = seckillDao.queryAll(0, 100);
        for (Seckill seckill : seckills) {
            System.out.println(seckill);
        }
    }
    @Test
    public void testReduceNumber() throws Exception {
        Date killTime = new Date();
        int updateCount = seckillDao.reduceNumber(1000L, killTime);
        System.out.println("updateCount=" + updateCount);
    }

}