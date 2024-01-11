FROM node:18.18.2 

RUN apt update

#日本語環境のインストール
RUN apt -y install locales && \
    localedef -f UTF-8 -i ja_JP ja_JP.UTF-8
ENV LANG ja_JP.UTF-8
ENV LANGUAGE ja_JP:ja
ENV LC_ALL ja_JP.UTF-8
#標準時間帯の設定
ENV TZ JST-9

#標準時間帯の設定
ENV TZ JST-9

#アプリケーションのインストール。
RUN apt install -y vim less nmap

# エイリアスを設定 ll,ls,la
COPY ./.alias /root/
RUN cat /root/.alias >> /root/.bashrc


RUN mkdir cds-hook-server
WORKDIR /cds-hook-server