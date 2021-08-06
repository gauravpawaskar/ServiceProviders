import json
import os
import random
import string

import requests
from flask import (Flask, redirect, render_template, request,
                   send_from_directory, session)
from flask.helpers import url_for

app = Flask(__name__, static_url_path='')
app.secret_key = ''.join(random.choices(
    string.ascii_uppercase + string.ascii_lowercase + string.digits, k=15))


def logInfo(message):
    app.logger.info(message)


def get_service_host(service):
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
        return service_host, service_port
    else:
        return False, False


@app.route('/', methods=["GET"])
def index():
    return render_template('index.html')


@app.route('/js/<path:path>')
def send_js(path):
    return send_from_directory('js', path)


@app.route('/home', methods=["GET"])
def home():
    if session['loggedin']:
        return session["service_data"]
    else:
        return "User is not logged in"


@app.route('/setup', methods=["POST"])
def oauthsetup():
    setup_json = request.get_json()
    service_name = setup_json["service"]
    client_id = setup_json["client_id"]
    client_secret = setup_json["client_secret"]
    service_host, service_port = get_service_host(service_name)
    if service_host and service_port:
        service_url = "http://"+service_host+":"+service_port+"/setup"
        service_headers = {'content-type': 'application/json'}
        service_data = {"client_id": client_id, "client_secret": client_secret}
        service_respose = requests.post(
            url=service_url, headers=service_headers, data=json.dumps(service_data))
        service_respose_json = service_respose.json()
        logInfo(service_url)
        logInfo(service_data)
        logInfo(service_respose_json)
        if service_respose_json["status"] == "success":
            return json.dumps({"status": "success"})
        else:
            return json.dumps({"status": "fail"})
    else:
        return json.dumps({"status": "fail"})


@app.route('/oauth/<service>')
def oauth(service):
    # code to call service discovery from db and call
    service_host, service_port = get_service_host(service)
    if service_host and service_port:
        service_url = "http://"+service_host+":"+service_port+"/processlogin" + \
            request.full_path.replace('/oauth/'+service, '')
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


@app.route('/saml/<service>', methods=["POST"])
def saml(service):
    # code to call service discovery from db and call
    service_host, service_port = get_service_host(service)
    if service_host and service_port:
        service_url = "http://"+service_host+":"+service_port+"/processlogin" + \
            request.full_path.replace('/saml/'+service, '')
        service_response = requests.post(
            url=service_url, headers=request.headers, data=request.get_data())
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
