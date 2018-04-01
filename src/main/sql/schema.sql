
CREATE  DATABASE  seckill;
use seckill;

CREATE  TABLE seckill(
seckill_id  bigint  not null AUTO_INCREMENT,
name  varchar(300)  not null,
number int NOT NULL COMMENT '库存数量',
create_time TIMESTAMP NOT NULL  DEFAULT CURRENT_TIMESTAMP(),
start_time TIMESTAMP not null,
end_time TIMESTAMP not null,
PRIMARY KEY (seckill_id),
KEY idx_start_time(start_time),
KEY  idx_end_time(end_time),
KEY  idx_create_time(create_time)
)engine=InnoDB AUTO_INCREMENT=1000 default charset=utf8  comment='秒杀库存表';


INSERT INTO seckill(name,number,start_time,end_time)
VALUES
('1000元秒杀iphone',100 ,'2015-11-01 00:00:00','2015-11-02 00:00:00'),
('500元秒杀ipad2',200,'2015-11-01 00:00:00','2015-11-02 00:00:00'),
('300元秒杀小米4',300,'2015-11-01 00:00:00','2015-11-02 00:00:00'),
('200元秒杀红米note',400,'2015-11-01 00:00:00','2015-11-02 00:00:00');


create table success_killed(
seckill_id bigint NOT NULL,
user_phone bigint NOT NULL ,
state tinyint NOT  NULL DEFAULT -1,
create_time  TIMESTAMP NOT NULL ,
key idx_create_time(create_time),
PRIMARY KEY (seckill_id,user_phone)
)engine=InnoDB DEFAULT charset=utf8  comment='秒杀成功明细表';

