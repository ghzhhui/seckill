package opt.seckill.service;

import opt.seckill.dto.Exposer;

import opt.seckill.dto.SeckillExecution;
import opt.seckill.entity.Seckill;
import opt.seckill.exception.RepeatKillException;
import opt.seckill.exception.SeckillCloseException;
import opt.seckill.exception.SeckillException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.util.List;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
        "classpath:spring/spring-dao.xml",
        "classpath:spring/spring-service.xml"
})
public class SeckillServiceTest {

    private  final   Logger logger= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private  SeckillService seckillService;

    @Test
    public void getSeckillList() throws Exception {
        List<Seckill> list=seckillService.getSeckillList();
        logger.info("list={}",list);
        //Closing non transactional SqlSession
    }
/**
 * 19:43:54.265 [main] INFO  o.seckill.service.SeckillServiceTest -
 * seckill=seckill{seckillId=1000, name='1000元秒杀iphone', number=100,
 * startTime=Sun Nov 01 00:00:00 CST 2015,
 * endTime=Mon Nov 02 00:00:00 CST 2015,
 * createTime=Thu Nov 30 21:33:29 CST 2017}
 */
    @Test
    public void getById() throws Exception {
        long id=1000;
        Seckill seckill=seckillService.getById(id);
        logger.info("seckill={}",seckill);
    }
    //测试代码完整逻辑，注意重复执行
    @Test
    public void testSeckillLogic() throws Exception {
        long id=1002;
        Exposer exposer=seckillService.exportSeckillUrl(id);
        if(exposer.isExposed()){
            logger.info("exposer={}",exposer);
            long phone=21838939488L;
            String md5=exposer.getMd5();
           try {
                SeckillExecution result=seckillService.executeSeckill(id,phone,md5);
                logger.info("result={}",result);
            } catch (RepeatKillException e1) {
                logger.error(e1.getMessage());
            }catch (SeckillCloseException e2 ){
                logger.error(e2.getMessage());
            }
        }else{
            //秒杀未开启
                logger.warn("exposer={}",exposer);
        }

        //19:51:51.052 [main] INFO  o.seckill.service.SeckillServiceTest - exposer=Exposer
        // {exposed=false, md5='null', seckillId=1000, now=1512820311052, start=1446307200000, end=1446393600000}

    }
/**
 * opt.seckill.exception.SeckillException: seckill data rewrite
 * 这个bug的主要用处是。
 */
    @Test
    public void executeSeckill() throws Exception {
        long id=1000;
        long phone=21838939488L;
        String md5="238a13f41face408178215cd14a719b7";

        try {
            SeckillExecution result=seckillService.executeSeckill(id,phone,md5);
            logger.info("result={}",result);
        } catch (RepeatKillException e) {
             logger.error(e.getMessage());
        }catch (SeckillCloseException e){
            logger.error(e.getMessage());
        }
        // SeckillExexution{
        // seckillId=1000, state=1, stateInfo='秒杀成功', successKilled=opt.seckill.entity.SuccessKilled@6f012914}
       //opt.seckill.exception.RepeatKillException: 重复秒杀！
    }
}