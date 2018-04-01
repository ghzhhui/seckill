package opt.seckill.web;

import opt.seckill.dto.Exposer;
import opt.seckill.dto.SeckillExecution;
import opt.seckill.dto.SeckillResult;
import opt.seckill.entity.Seckill;
import opt.seckill.enums.SeckillStateEnum;
import opt.seckill.exception.RepeatKillException;
import opt.seckill.exception.SeckillCloseException;
import opt.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
@Controller
@RequestMapping("/seckill")
public class SeckillController {
    private final Logger logger= LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SeckillService seckillService;
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public String list(Model model){
        //获取列表页
        List<Seckill> list=seckillService.getSeckillList();
        model.addAttribute("list",list);
        return "list";
    }
    @RequestMapping(value = "/{seckillId}/detail",method =RequestMethod.GET)
    public String detail(@PathVariable("seckillId")Long seckillId,Model model){
        if(seckillId==null){
            return  "redirect:/seckill/list";
        }
        Seckill seckill=seckillService.getById(seckillId);
        if(seckill==null){
            return "forward:/seckill/list";
        }
        model.addAttribute("seckill",seckill);
        return "detail";
    }
    //ajax  json技术,用来输出秒杀地址的json接口已经封装完成。
    @RequestMapping(value = "/{seckillId}/exposer",method = RequestMethod.POST
    ,produces = {"application/json;charset=UTF-8"}
    )
    @ResponseBody
    public SeckillResult<Exposer> exposer(Long seckillId) {
        SeckillResult<Exposer> result;
        try {
            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
            result = new SeckillResult<Exposer>(true, exposer);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result = new SeckillResult<Exposer>(false, e.getMessage());
        }
        return result;
    }
    //DTO层是web与service之间的数据传递过程。
    @RequestMapping(value = "/{seckillId}/{md5}/execution",method = RequestMethod.POST,
    produces = "application/json;charset=UTF-8")
    @ResponseBody
    public SeckillResult<SeckillExecution>  execute(@PathVariable("seckillId") Long seckillId,
                                                    @PathVariable("md5") String md5,
                                                    @CookieValue(value = "killPhone",required = false) Long phone){
        if(phone==null){
            return new SeckillResult<SeckillExecution>(false,"用户未注册");
        }
        SeckillResult<SeckillExecution> result;

        try {
            SeckillExecution execution=seckillService.executeSeckill(seckillId,phone,md5);
            return  new SeckillResult<SeckillExecution>(true,execution);
        }catch (RepeatKillException e){
            //枚举类里面封装的是一些状态关系。
          SeckillExecution execution=new SeckillExecution(seckillId,SeckillStateEnum.REPEAT_KILL);
            return new SeckillResult<SeckillExecution>(false,execution);
        }
        catch (SeckillCloseException e) {
             SeckillExecution execution=new SeckillExecution(seckillId,SeckillStateEnum.END);
            return new SeckillResult<SeckillExecution>(false,execution);
        }
        catch (Exception e) {
            SeckillExecution execution=new SeckillExecution(seckillId,SeckillStateEnum.INNER_ERROR);
            return new SeckillResult<SeckillExecution>(false,execution);
        }
        //根据result封装成对应的DTO类，进行输出,spring输出的DTO信息，封装成最后的结果。

    }
    @RequestMapping(value = "/time/now",method = RequestMethod.GET)
    public  SeckillResult<Long> time(){
        Date now=new Date();
        return new SeckillResult<Long>(true,now.getTime());
    }

}
