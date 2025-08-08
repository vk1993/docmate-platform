#!/bin/bash
# Docker Container Startup Script with JVM Optimizations
# This script optimizes JVM settings for containerized Spring Boot applications

# JVM Memory settings optimized for containers
export JAVA_OPTS="${JAVA_OPTS} -XX:+UseContainerSupport"
export JAVA_OPTS="${JAVA_OPTS} -XX:MaxRAMPercentage=75.0"
export JAVA_OPTS="${JAVA_OPTS} -XX:InitialRAMPercentage=50.0"

# Garbage Collection optimizations
export JAVA_OPTS="${JAVA_OPTS} -XX:+UseG1GC"
export JAVA_OPTS="${JAVA_OPTS} -XX:+UseStringDeduplication"
export JAVA_OPTS="${JAVA_OPTS} -XX:+UseCompressedOops"
export JAVA_OPTS="${JAVA_OPTS} -XX:+UseCompressedClassPointers"

# Performance optimizations for microservices
export JAVA_OPTS="${JAVA_OPTS} -XX:+TieredCompilation"
export JAVA_OPTS="${JAVA_OPTS} -XX:TieredStopAtLevel=1"
export JAVA_OPTS="${JAVA_OPTS} -Xss256k"

# Security optimizations
export JAVA_OPTS="${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom"

# Spring Boot specific optimizations
export JAVA_OPTS="${JAVA_OPTS} -Dspring.backgroundpreinitializer.ignore=true"
export JAVA_OPTS="${JAVA_OPTS} -Dspring.jmx.enabled=false"

# Execute the application
exec java ${JAVA_OPTS} -jar /app.jar "$@"
