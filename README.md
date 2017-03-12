# garfield

##环境依赖

- java
- maven

##配置文件

- garfield.conf
	- email.server.user: 发送邮件的邮箱账号
	- email.server.pass: 发送邮件的邮箱密码
	- email.server.host: 发送邮件的服务域名,如smtp.163.com
	- email.server.port: 发送邮件的服务端口,如465
	- email.send.to：邮件接收人
- watching-pages.yml
    - name: 页面(剧集)名称
    - selector: 页面变更判定的选择器
    - url: 页面URL

##部署
```bash
mvn clean package -U
cp garfield.conf.sample target/garfield.conf
cp watching-pages.yml.sample target/watching-pages.yml
cd target/
##编辑配置文件 garfield.conf, watching-pages.yml
sh start.sh
```