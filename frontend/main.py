import os

import requests
from flask import Flask, render_template, request

app = Flask(__name__)


@app.route('/', methods=["GET"])
def index():
    return render_template('index.html')


@app.route('/oauth/linkedin')
def oauth():
    # code to route request to linkedin service
    linkedin_host = os.environ['LINKEDIN_HOST']
    linkedin_port = os.environ['LINKEDIN_PORT']
    linkedin_url = "http://"+linkedin_host+":"+linkedin_port+"/processlogin" + \
        request.full_path.replace('/oauth/linkedin', '')
    linkedin_response = requests.get(url=linkedin_url, headers=request.headers)
    return linkedin_response.text


if __name__ == "__main__":
    app.run(debug=True, host="0.0.0.0")
