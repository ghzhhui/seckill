-- 秒杀执行存储过程
-- ；
-- rowcount():返回上一条修改类型sql的行数
--
DELIMITER $$ -- console；
CREATE  PROCEDURE  `seckill`.`execute_seckill`
  (in v_seckill_id BIGINT,in v_phone BIGINT,
  in v_kill_time TIMESTAMP,OUT  r_result INT)
BEGIN
  DECLARE  insert_count int DEFAULT  0;
  START TRANSACTION ;
  INSERT IGNORE  into success_killed(seckill_id, user_phone)
    VALUES (v_seckill_id,v_phone,v_kill_time);
  SELECT  row_count() INTO  insert_count;
  if(insert_count=0) THEN
    ROLLBACK;
    set  r_result=-1;
  ELSEIF (insert_count<0)

END $$
