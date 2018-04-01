package opt.seckill.exception;
/**
 * 运行期异常信息
 */
public class RepeatKillException extends  SeckillException {
    public RepeatKillException(String message) {
        super(message);
    }

    public RepeatKillException(String message, Throwable cause) {
        super(message, cause);
    }
}
