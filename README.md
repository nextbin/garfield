# garfield

##环境依赖

- java
- maven

##配置文件

- garfield.conf
	- email.server.user：发送邮件的邮箱账号
	- email.server.pass：发送邮件的邮箱密码
	- email.server.host：
	- email.server.port：
	- email.send.to：邮件接收人
- watching-pages.yml

##部署
```bash
mvn clean package -U
cp garfield.conf.sample target/garfield.conf
cp watching-pages.yml target/
cd target/
vim garfield.conf
vim watching-pages.yml
sh start.sh
```