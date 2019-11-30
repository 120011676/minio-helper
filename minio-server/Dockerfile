FROM openjdk:13-alpine
MAINTAINER Say.li <120011676@qq.com>
LABEL maintainer="Say.li <120011676@qq.com>"
ENV TZ Asia/Shanghai
RUN apk --update add tzdata && ln -sf /usr/share/zoneinfo/${TZ} /etc/localtime && echo ${TZ} > /etc/timezone
ENV DEPLOY_USER deploy
RUN addgroup -S -g 9999 $DEPLOY_USER && adduser -S -G $DEPLOY_USER -u 9999 $DEPLOY_USER
USER $DEPLOY_USER
WORKDIR /opt
ENV PROJECT_JAR_NAME minio-server-0.0.1.jar
COPY build/libs/$PROJECT_JAR_NAME .
EXPOSE 8080
CMD java -jar $PROJECT_JAR_NAME