FROM ghcr.io/graalvm/graalvm-ce:ol8-java11-22.3.3 AS build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -f /app/pom.xml


FROM ghcr.io/graalvm/graalvm-ce:ol8-java11-22.3.3

WORKDIR /app
# Install GraalJS (JavaScript engine)
RUN gu install js
# Set environment variables for GraalVM
ENV GRAALVM_HOME=/opt/graalvm
ENV PATH=$GRAALVM_HOME/bin:$PATH
ENV CLASSPATH=$GRAALVM_HOME/lib/*:$CLASSPATH
# Copy the packaged JAR file from the build stage
COPY --from=build /app/target/*.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
