# Use a lightweight official Python image
FROM python:3.11-slim

# Set working directory inside the container
WORKDIR /client

# Copy your Python script into the container
COPY client.py .

ENTRYPOINT ["python", "client.py"]
