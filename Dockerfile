# # ARG app
FROM anolis-registry.cn-zhangjiakou.cr.aliyuncs.com/openanolis/openjdk:17-8.6

ENV TZ=Asia/Shanghai
ENV LANG UTF-8
# 修改时区
RUN ln -sf /usr/share/zoneinfo/%TZ /etc/localtime

ARG app
ENV env=dev
RUN mkdir -p /app/{lib,tmp,logs}
WORKDIR /app
COPY ./target /app