# syntax=docker/dockerfile:1

# ---------------------------------------------------------------------------
# 1) Build stage — Maven ile fat-jar üretir.
#    pom.xml önce tek başına kopyalanıp bağımlılıklar indirilir; src/ değiştiğinde
#    bu katman cache'ten gelir, yalnızca kod derlemesi tekrar çalışır.
# ---------------------------------------------------------------------------
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /build

COPY pom.xml .
RUN mvn -B -ntp dependency:go-offline

COPY src src
RUN mvn -B -ntp clean package -DskipTests \
    && cp target/vocab-learn-*.jar app.jar

# ---------------------------------------------------------------------------
# 2) Runtime stage — yalnızca JRE + üretilen jar.
# ---------------------------------------------------------------------------
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

RUN groupadd --system vocablearn && useradd --system --gid vocablearn --no-create-home vocablearn \
    && mkdir -p /app-logs && chown -R vocablearn:vocablearn /app-logs /app

COPY --from=build --chown=vocablearn:vocablearn /build/app.jar app.jar

ENV JAVA_OPTS=""

EXPOSE 8080

USER vocablearn

HEALTHCHECK --interval=15s --timeout=5s --start-period=45s --retries=5 \
    CMD ["bash", "-c", "exec 3<>/dev/tcp/127.0.0.1/8080 || exit 1"]

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
