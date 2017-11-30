# consul

start F:/Program Files/MongoDB/Server/3.4/bin/mongod --maxConns 100 --logpath E:/LOGS/mongo/mogo.log --jsonp --rest --serviceName hua --servicePassword 123456 --dbpath E:/db/mongo
pause

#springboot config
debug=true
server.port=8087
server.address=0.0.0.0
server.session.timeout=10
server.tomcat.uri-encoding=UTF-8
endpoints.health.sensitive = false
spring.application.name=jiangxinDAO

#mongodb config
#spring.data.mongodb.uri=mongodb://admin:123456@192.168.4.21:27017/pro_zbt
spring.data.mongodb.uri=mongodb://admin:123456@43.254.53.60:27017/pro_zbt

#log config
logging.path=D:/data/logs/jiangxinDAO
logging.level.root=DEBUG
logging.level.org.springframework.web=DEBUG
#logging.level.org.hibernate=ERROR
spring.output.ansi.enabled=DETECT

#logging.pattern.console=%-d{yyyy-MM-dd HH:mm:ss} %p ${PID} --- [ %t ] %c     %m%n
#logging.pattern.file=%-d{yyyy-MM-dd HH:mm:ss} %p ${PID} --- [ %t ] %c     %m%n

#PID=20170502


redis.pool.maxTotal=600
redis.pool.maxIdle=300
redis.pool.maxWaitMillis=50
redis.pool.minIdle=0
redis.host=192.168.99.100
redis.port=6379
redis.password=
redis.pool.testOnBorrow=1
redis.pool.testOnReturn=true
redis.timeout=2000

#test
session.pool.maxTotal=600
session.pool.maxIdle=300
session.pool.maxWaitMillis=50
session.pool.minIdle=0
session.host=192.168.99.100
session.port=6378
session.password=
session.pool.testOnBorrow=1
session.pool.testOnReturn=true
session.timeout=2000


driver=com.mysql.jdbc.Driver
#url=jdbc\:mysql\://127.0.0.1:3306/pro_zbt?useUnlcode\=1&characterEncoding\=utf-8&zeroDateTimeBehavior\=convertToNull&allowMultiQueries\=true&remarksReporting\=true
url=jdbc\:mysql\://103.21.117.60:3306/pro_zbt?useUnlcode\=1&characterEncoding\=utf-8&zeroDateTimeBehavior\=convertToNull&allowMultiQueries\=true&remarksReporting\=true&tinyInt1isBit\=false
username1=root
password1=ZBT123456test007

zookeeper.address=zookeeper://192.168.99.100:2181
zookeeper.properties=/usr/local/logs/jianxinDAO.properties


#project2
#activemq
#dev
activemq.url=tcp://192.168.99.100:61616
activemq.username=admin
activemq.password=admin

#redis 6378
#dev
session.pool.maxTotal=600
session.pool.maxIdle=300
session.pool.maxWaitMillis=50
session.pool.minIdle=0
session.host=192.168.99.100
session.port=6378
session.password=
session.pool.testOnBorrow=1
session.pool.testOnReturn=true
session.timeout=2000

#redis 6379
#dev
redis.pool.maxTotal=600
redis.pool.maxIdle=300
redis.pool.maxWaitMillis=50
redis.pool.minIdle=0
redis.host=192.168.99.100
redis.port=6379
redis.password=
redis.pool.testOnBorrow=1
redis.pool.testOnReturn=true
redis.timeout=2000

#数据源
driver=com.mysql.jdbc.Driver
#url=jdbc\:mysql\://127.0.0.1:3306/pro_zbt?useUnlcode\=1&characterEncoding\=utf-8&zeroDateTimeBehavior\=convertToNull&allowMultiQueries\=true&remarksReporting\=true
url=jdbc\:mysql\://103.21.117.60:3306/pro_zbt?useUnlcode\=1&characterEncoding\=utf-8&zeroDateTimeBehavior\=convertToNull&allowMultiQueries\=true&remarksReporting\=true&tinyInt1isBit\=false
username1=root
password1=ZBT123456test007

<dependency>
		    <groupId>com.ecwid.consul</groupId>
		    <artifactId>consul-api</artifactId>
		    <version>1.2.1</version>
		</dependency>
