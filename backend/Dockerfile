# 使用多阶段构建
# 第一阶段：构建
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /build

# 先复制 pom.xml，利用 Docker 缓存加速
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源码并打包
COPY src ./src
RUN mvn clean package -DskipTests -B

# 第二阶段：运行
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# 安装时区数据
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone

# 创建上传目录
RUN mkdir -p /app/uploads

# 从构建阶段复制 jar
COPY --from=builder /build/target/*.jar app.jar

# 创建非 root 用户运行
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
RUN chown -R appuser:appgroup /app
USER appuser

EXPOSE 8080

ENTRYPOINT ["java", \
  "-server", \
  "-Xms256m", \
  "-Xmx512m", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-Dfile.encoding=UTF-8", \
  "-Duser.timezone=Asia/Shanghai", \
  "-jar", "app.jar"]
