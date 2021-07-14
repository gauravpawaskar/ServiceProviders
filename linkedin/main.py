import json
import os

import requests
from flask import Flask, request

app = Flask(__name__)


@app.route('/', methods=["GET"])
def index():
    return "Hello"


@app.route('/processlogin')
def processlogin():
    code = request.args.get('code')
    state = request.args.get('state')
    redirect_uri = "https://flagprotectors.com/oauth/linkedin"
    headers = {'content-type': 'application/x-www-form-urlencoded'}
    client_id = os.environ['CLIENT_ID']
    client_secret = os.environ['CLIENT_SECRET']
    auth_url = "https://www.linkedin.com/oauth/v2/accessToken"
    data = "grant_type=authorization_code&code="+code+"&redirect_uri=" + \
        redirect_uri+"&client_id="+client_id+"&client_secret="+client_secret
    access_code_response = requests.post(
        url=auth_url, data=data, headers=headers)

    access_code_response_json = access_code_response.json()
    access_token = access_code_response_json["access_token"]

    me_url = "https://api.linkedin.com/v2/me"
    auth_header = {"Authorization": "Bearer "+access_token}
    me_response = requests.get(url=me_url, headers=auth_header)
    me_response_json = me_response.json()

    # Store access token
    database_host = os.environ['DATABASE_HOST']
    database_port = os.environ['DATABASE_PORT']
    database_url = "http://"+database_host+":"+database_port+"/token"
    database_header = {'content-type': 'application/json'}
    database_data = {
        "user_id": me_response_json["id"], "service": "linkedin", "token": access_token}
    database_response = requests.post(
        url=database_url, data=json.dumps(database_data), headers=database_header)
    app.logger.info(database_response.text)
    #database_response_json = database_response.json()

    return me_response.text


if __name__ == "__main__":
    app.run(debug=True, host="0.0.0.0")
