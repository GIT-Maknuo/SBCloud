@echo off
set STORAGE_TYPE=mysql
set MYSQL_USER=zipkin
set MYSQL_PASS=zipkin
java -jar zipkin-server-3.4.4-exec.jar
pause
