FROM python:3.11.5-alpine
ENV APP_PORT=8001

WORKDIR /app
COPY requirements.txt requirements.txt
RUN pip install -r requirements.txt
COPY . .

EXPOSE 8001
#EXPOSE 50051

CMD sh -c 'uvicorn main:app --host 0.0.0.0 --port $APP_PORT'