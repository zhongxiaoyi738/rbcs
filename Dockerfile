# # ARG app
FROM openjdk:17-slim-buster

ENV TZ=Asia/Shanghai
ENV LANG UTF-8
# 修改时区
RUN ln -sf /usr/share/zoneinfo/%TZ /etc/localtime

ARG app
ENV env=dev
RUN mkdir -p /app/{lib,tmp,logs}
WORKDIR /app
COPY ./target /app