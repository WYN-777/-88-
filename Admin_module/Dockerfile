# 使用官方Tomcat镜像
FROM tomcat:9.0-jdk11-openjdk

# 设置工作目录
WORKDIR /usr/local/tomcat

# 删除默认的ROOT应用
RUN rm -rf webapps/ROOT

# 复制整个web应用目录
COPY web/ webapps/ROOT/

# 确保classes目录存在
RUN mkdir -p webapps/ROOT/WEB-INF/classes

# 复制编译好的class文件（包括filter）
COPY web/WEB-INF/classes/ webapps/ROOT/WEB-INF/classes/

# 复制依赖的JAR包
COPY web/WEB-INF/lib/ webapps/ROOT/WEB-INF/lib/

# 创建数据目录
RUN mkdir -p webapps/ROOT/data

# 复制示例数据文件
COPY web/data/ webapps/ROOT/data/

# 暴露Tomcat端口
EXPOSE 8080

# 启动Tomcat
CMD ["catalina.sh", "run"]