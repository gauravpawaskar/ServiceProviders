FROM python:3.8

RUN apt-get update
RUN apt-get install xmlsec1 -y


WORKDIR /app

COPY requirements.txt .
RUN pip install -r requirements.txt

COPY *.py .

CMD [ "python", "main.py" ]