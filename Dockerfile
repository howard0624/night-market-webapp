cat > Dockerfile << 'EOF'
# 1) Build 階段：用 Maven 打包 WAR
FROM maven:3.9-eclipse-temurin-11 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests package

# 2) Run 階段：用 Tomcat 9 部署成 ROOT.war
FROM tomcat:9.0-jdk11-temurin
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080
CMD ["catalina.sh", "run"]
EOF
