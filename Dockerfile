FROM 100.125.17.64:20202/hwcse/dockerhub-java:8-jre-alpine

WORKDIR /home/apps/
ADD app/ .
ADD oiosaml-config/ ./oiosaml-config/
ADD start.sh .

ENTRYPOINT ["sh", "/home/apps/start.sh"]