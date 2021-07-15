import os
import random
import string

import requests
from flask import Flask, redirect, render_template, request, session
from flask.helpers import url_for

app = Flask(__name__)
app.secret_key = ''.join(random.choices(
    string.ascii_uppercase + string.ascii_lowercase + string.digits, k=15))


@app.route('/', methods=["GET"])
def index():
    return render_template('index.html')


@app.route('/home', methods=["GET"])
def home():
    if session['loggedin']:
        return session["service_data"]
    else:
        return "User is not logged in"


@app.route('/oauth/<service>')
def oauth(service):
    # code to call service discovery from db and call
    database_host = os.environ['DATABASE_HOST']
    database_port = os.environ['DATABASE_PORT']
    database_url = "http://"+database_host+":" + \
        database_port+"/service?service_name="+service
    database_response = requests.get(url=database_url)
    app.logger.info(database_response.text)
    database_response_json = database_response.json()
    if database_response_json["status"] == "success":
        service_host = database_response_json["service_host"]
        service_port = database_response_json["service_port"]
        service_url = "http://"+service_host+":"+service_port+"/processlogin" + \
            request.full_path.replace('/oauth/linkedin', '')
        service_response = requests.get(
            url=service_url, headers=request.headers)
        if service_response.status_code == 200:
            session["loggedin"] = True
            session["service"] = service
            session["service_data"] = service_response.text
            return redirect(url_for('home'))
        else:
            return "Something went wrong in login!"
    else:
        return "Not able to detect {} service".format(service)


if __name__ == "__main__":
    app.run(debug=True, host="0.0.0.0")
