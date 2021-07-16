FROM python:3.8

WORKDIR /app

COPY requirements.txt .
RUN pip install -r requirements.txt

COPY *.py .
COPY templates/ templates/
COPY js/ js/

CMD [ "python", "main.py" ]