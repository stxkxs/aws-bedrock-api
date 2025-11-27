FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY checkstyle.xml .
COPY spotbugs-exclude.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre

ENV JAVA_TOOL_OPTIONS="-javaagent:/opt/opentelemetry/opentelemetry-javaagent.jar"
ENV OTEL_SERVICE_NAME="aws-bedrock-api"
ENV OTEL_TRACES_EXPORTER="otlp"
ENV OTEL_METRICS_EXPORTER="otlp"
ENV OTEL_LOGS_EXPORTER="otlp"
ENV OTEL_EXPORTER_OTLP_ENDPOINT="http://otel-collector:4317"
ENV OTEL_EXPORTER_OTLP_PROTOCOL="grpc"
ENV OTEL_RESOURCE_ATTRIBUTES="service.name=aws-bedrock-api,service.version=1.0.0,deployment.environment=dev"
ENV OTEL_PROPAGATORS="tracecontext,baggage,b3multi"

WORKDIR /app

RUN mkdir -p /opt/opentelemetry
RUN apt-get update && apt-get install -y --no-install-recommends wget

ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v1.33.0/opentelemetry-javaagent.jar /opt/opentelemetry/opentelemetry-javaagent.jar

COPY --from=build /app/target/aws-bedrock-api-0.0.1-SNAPSHOT.jar /app/api.jar

EXPOSE 8080

CMD ["sh", "-c", "java -jar /app/api.jar"]
