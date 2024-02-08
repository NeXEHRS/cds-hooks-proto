#FROM almalinux:latest
#apple silicon
FROM --platform=linux/amd64 almalinux:latest 

# java(OpenJDK), wget
RUN dnf install -y java-17-openjdk wget

# nodejs
WORKDIR /opt/
RUN wget https://nodejs.org/dist/v20.10.0/node-v20.10.0-linux-x64.tar.xz
RUN tar Jxfv node-v20.10.0-linux-x64.tar.xz

# ant 
RUN wget https://dlcdn.apache.org//ant/binaries/apache-ant-1.10.14-bin.tar.gz
RUN tar zxvf apache-ant-1.10.14-bin.tar.gz

# path: 
ENV ANT_HOME /opt/apache-ant-1.10.14
ENV NODEJS_HOME /opt/node-v20.10.0-linux-x64 
ENV PATH $ANT_HOME/bin:$NODEJS_HOME/bin:$PATH
RUN echo $PATH

# tomcat
RUN wget https://dlcdn.apache.org/tomcat/tomcat-9/v9.0.83/bin/apache-tomcat-9.0.83.tar.gz
RUN tar zxvf apache-tomcat-9.0.83.tar.gz
WORKDIR /opt/apache-tomcat-9.0.83/bin
RUN chmod +x *.sh

# cql-execution install
WORKDIR /opt
RUN wget https://github.com/cqframework/cql-execution/archive/refs/tags/v3.0.1.tar.gz
RUN mv v3.0.1.tar.gz cql-execution-3.0.1.tar.gz
RUN tar zxvf cql-execution-3.0.1.tar.gz
WORKDIR /opt/cql-execution-3.0.1
RUN /opt/node-v20.10.0-linux-x64/bin/npm install
RUN /opt/node-v20.10.0-linux-x64/bin/npm audit fix

# cql-fhir-exec install
WORKDIR /opt
RUN wget https://github.com/cqframework/cql-exec-fhir/archive/refs/tags/v2.1.5.tar.gz
RUN mv v2.1.5.tar.gz cql-exec-fhir-2.1.5.tar.gz
RUN tar zxvf cql-exec-fhir-2.1.5.tar.gz
WORKDIR /opt/cql-exec-fhir-2.1.5
RUN /opt/node-v20.10.0-linux-x64/bin/npm install
RUN /opt/node-v20.10.0-linux-x64/bin/npm audit fix

# pddiCdsService install
COPY pddiCdsService /opt/pddiCdsService
WORKDIR /opt/pddiCdsService
RUN mkdir logs
RUN mkdir works
RUN ant
RUN ant deploy

# tomcat startup
WORKDIR /opt/apache-tomcat-9.0.83/bin
CMD ["./catalina.sh", "run"]



