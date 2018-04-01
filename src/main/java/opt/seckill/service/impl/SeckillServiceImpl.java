package opt.seckill.service.impl;

import opt.seckill.dao.SeckillDao;
import opt.seckill.dao.SuccessKilledDao;
import opt.seckill.dto.Exposer;
import opt.seckill.dto.SeckillExecution;
import opt.seckill.entity.Seckill;
import opt.seckill.entity.SuccessKilled;
import opt.seckill.enums.SeckillStateEnum;
import opt.seckill.exception.RepeatKillException;
import opt.seckill.exception.SeckillCloseException;
import opt.seckill.exception.SeckillException;
import opt.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;

@Service
public class SeckillServiceImpl implements SeckillService {
    private Logger logger= LoggerFactory.getLogger(this.getClass());
    //注入依赖
    @Autowired
    private SeckillDao seckillDao;
    @Autowired
    private SuccessKilledDao successKilledDao;

    private final String slat="56whdj%%^$^&&#hGHHHJ";

    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0,4);
    }

    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    public Exposer exportSeckillUrl(long seckillId) {
        //优化点：缓存优化，怎么去优化缓存呢？
        /**
         * get from cache
         * if null
         * get db
         * else
         *     put  cache
         * login
         *
         */

        Seckill seckill=seckillDao.queryById(seckillId);
        if(seckill==null){
            return  new Exposer(false,seckillId);
        }
        Date startTime=seckill.getStartTime();
        Date endTime=seckill.getEndTime();

        //当前系统时间
        Date nowTime=new Date();
        if(nowTime.getTime()<startTime.getTime()|| nowTime.getTime()>endTime.getTime()){
            return
                    new Exposer(false,seckillId,nowTime.getTime(),startTime.getTime()
            ,endTime.getTime());
        }
        String MD5=getMD5(seckillId);

        return new Exposer(true,MD5,seckillId);
    }
    private  String getMD5(long seckillId){
        String base=seckillId+"/"+slat;
        String md5= DigestUtils.md5DigestAsHex(base.getBytes());
        return  md5;
    }

    /**
     * 使用注解控制事务方法的优点
     * 保证事务方法执行的时间尽可能的短，不要穿插其他的网络操作，PRC/HTTP请求
     * 或者剥离到方法外面去，不是所有的方法都需要事务的。
     */
    @Transactional
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws RepeatKillException, SeckillCloseException, SeckillException {
        if(md5==null||!md5.equals(getMD5(seckillId))    ){
            throw  new SeckillException("seckill data rewrite");
        }
        //执行秒杀逻辑：减库存，记录购买明细
        Date nowTime=new Date();
        try {
            int updateCount=seckillDao.reduceNumber(seckillId,nowTime);
            if(updateCount<=0){
                throw  new  SeckillCloseException("秒杀结束");
            }else{
                //记录购买行为
                int insertCount=successKilledDao.insertSuccessKilled(seckillId,userPhone);
                if(insertCount<=0){
                    //重复秒杀
                    throw new RepeatKillException("重复秒杀！");
                } else {
                    //秒杀成功
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
                }
            }
        }catch (SeckillCloseException e1){
            throw e1;
        }catch(RepeatKillException e2){
            throw  e2;
        } catch (Exception e){
            logger.error(e.getMessage(),e);
            throw  new SeckillException("seckill inner error"+e.getMessage());
        }
    }
}
