FROM python:3.8

WORKDIR /app

COPY requirements.txt .
COPY *.py .

RUN pip install -r requirements.txt

CMD [ "python", "main.py" ]