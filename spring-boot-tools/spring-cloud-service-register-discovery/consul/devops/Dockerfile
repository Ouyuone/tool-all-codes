FROM consul:1.9.1
#FROM consul:1.14.7
ADD config.json /consul/config

ENTRYPOINT ["/bin/sh","-c","consul agent -server -ui -bootstrap-expect 1 -config-dir=/consul/config"]
#ENTRYPOINT ["/bin/sh","-c","consul agent -server -bootstrap-expect=1 -bind=0.0.0.0 -data-dir=consul/data"]

#  使用构建命令 docker build -t consul:1.9.1 .

# 启动命令 docker run -d -p 8500:8500  -v /Users/oo-mbp/docker/server/consul-docker/data:/consul/data --name=consul-service consul:1.9.1