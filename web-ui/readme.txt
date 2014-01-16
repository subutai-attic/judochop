Install grails 2.2.4 (newer versions can give bugs):
- http://grails.org/learn
- http://dist.springframework.org.s3.amazonaws.com/release/GRAILS/grails-2.2.4.zip

Run:
1. Make the project dir as current
2. Type 'grails run-app'
3. Open http://localhost:8080/web-ui/

Deploy:
1. Make the project dir as current
2. Type 'grails war'
3. The war file is under <projectDir>/target/

Config:
- Set the data dir in <appDir>/WEB-INF/config.properties