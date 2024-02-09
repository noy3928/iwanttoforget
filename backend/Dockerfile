# Java 17을 사용하는 OpenJDK 이미지를 기반으로 설정
FROM openjdk:17-oracle

# 컨테이너 내에서 애플리케이션 파일을 저장할 디렉토리를 생성합니다.
WORKDIR /app

# Maven 빌드 결과물인 jar 파일을 컨테이너 내부로 복사합니다.
# 이 예시에서는 빌드된 jar 파일을 'app.jar'로 가정합니다. 실제 파일명에 맞게 조정해야 합니다.
COPY target/*.jar app.jar

# 애플리케이션 실행 명령어
ENTRYPOINT ["java","-jar","/app/app.jar"]
