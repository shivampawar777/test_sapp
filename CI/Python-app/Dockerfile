#----------Stage 1: Builder------------
FROM python:3.12-slim-bullseye AS builder

#set working directory for build stage
WORKDIR /build

COPY requirements.txt .

#install build tools, system dependencies
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        build-essential \
        software-properties-common \
        git && \
    pip install --upgrade pip && \
    pip install --no-cache-dir -r requirements.txt && \
    apt-get purge -y --auto-remove build-essential software-properties-common git && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*


#----------Stage 2: Runtime------------
FROM python:3.12-slim-bullseye

#1.copy the Python dependencies from the builder stage
#2.also copy binaries from builder stage
COPY --from=builder /usr/local/lib/python3.12/site-packages/ /usr/local/lib/python3.12/site-packages/
COPY --from=builder /usr/local/bin/ /usr/local/bin/

#create non-root user for security
#create directory
#assign directory ownership to non-root user 
RUN useradd -m -r appuser && \
    mkdir /app && \
    chown -R appuser /app

#set working directory for production stage
WORKDIR /app

#copy source code to container alongwith set ownership & group to non-root user
COPY --chown=appuser:appuser src /app/

#switch to non-root user
USER appuser

#expose port & set env variable for real-time logs
EXPOSE 8501
ENV PYTHONUNBUFFERED=1

ENTRYPOINT ["streamlit", "run", "app.py", "--server.port=8501", "--server.address=0.0.0.0"]

