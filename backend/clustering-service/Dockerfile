FROM python:3.11-slim

WORKDIR /app

# Copy requirements and install
# We do this before copying code to leverage Docker layer caching
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy the rest of the application
COPY . .

EXPOSE 8000

CMD ["python", "src/main.py"]