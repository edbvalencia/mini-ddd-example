FROM eclipse-temurin:21-jre

LABEL creator="Eduardo Valencia" \
      email="evalencia@golden.com" \
      date="18/02/2025" \
      version="1.0.0"

USER root

COPY ./build/libs/*.jar /home/app.jar

EXPOSE 8082

CMD ["java", "-server", "-jar", "/home/app.jar"]
