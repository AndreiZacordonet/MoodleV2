FROM python:3.11.5-alpine
ENV APP_PORT=8000

WORKDIR /app
COPY requirements.txt requirements.txt
RUN pip install -r requirements.txt
COPY . .

#COPY supervisord.conf /etc/supervisor/conf.d/supervisord.conf

# Expose ports for FastAPI and gRPC
EXPOSE 8000
EXPOSE 50051

# Start supervisord to manage both processes
#CMD ["/usr/bin/supervisord", "-c", "/etc/supervisor/conf.d/supervisord.conf"]
CMD sh -c 'python3 server.py & uvicorn fastapi_client:app --host 0.0.0.0 --port $APP_PORT'