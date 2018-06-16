FROM 100.125.17.64:20202/audit_organization/centos7-base:1.0.1

WORKDIR /home/apps/
ADD target/cron-service-0.0.1-SNAPSHOT.jar .
ADD start.sh .

ENTRYPOINT ["sh", "/home/apps/start.sh"]