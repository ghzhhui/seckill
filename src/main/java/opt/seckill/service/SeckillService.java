package opt.seckill.service;

import opt.seckill.dto.Exposer;

import opt.seckill.dto.SeckillExecution;
import opt.seckill.entity.Seckill;
import opt.seckill.exception.RepeatKillException;
import opt.seckill.exception.SeckillCloseException;
import opt.seckill.exception.SeckillException;
import org.springframework.stereotype.Service;

import java.util.List;
/*
定义方法的粒度，参数
*/
public interface SeckillService {
    /**
     * 查询多个秒杀记录
     */
    List<Seckill> getSeckillList();

    Seckill getById(long seckillId);
    /**
     * 秒杀开始前输出秒杀地址，否则输出系统时间
     */
    Exposer exportSeckillUrl(long seckillId);

    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
            throws RepeatKillException,SeckillCloseException,SeckillException;

}
