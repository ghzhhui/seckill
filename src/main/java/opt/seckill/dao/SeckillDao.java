package opt.seckill.dao;

import opt.seckill.entity.Seckill;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;


public interface SeckillDao {
    int reduceNumber(@Param("seckill_Id") long seckillId,@Param("killTime") Date killTime);
    Seckill queryById(long seckillId);
    List<Seckill> queryAll(@Param("offset") int offset,@Param("limit")int limit);
}
