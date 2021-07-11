import os
from flask.helpers import url_for
import string
import random

import requests
from flask import Flask, render_template, request, redirect, session

app = Flask(__name__)
app.secret_key = ''.join(random.choices(string.ascii_uppercase + string.ascii_lowercase + string.digits, k=15))

@app.route('/', methods=["GET"])
def index():
    return render_template('index.html')

@app.route('/home', methods=["GET"])
def home():
    if session['loggedin']:
        return session["service_data"]
    else:
        return "User is not logged in"

@app.route('/oauth/linkedin')
def oauth():
    # code to route request to linkedin service
    linkedin_host = os.environ['LINKEDIN_HOST']
    linkedin_port = os.environ['LINKEDIN_PORT']
    linkedin_url = "http://"+linkedin_host+":"+linkedin_port+"/processlogin" + \
        request.full_path.replace('/oauth/linkedin', '')
    linkedin_response = requests.get(url=linkedin_url, headers=request.headers)
    if linkedin_response.status_code == 200:
        session["loggedin"] = True
        session["service"] = "linkedin"
        session["service_data"] = linkedin_response.text
        return redirect(url_for('home'))
    else:
        return "Something went wrong in login!"


if __name__ == "__main__":
    app.run(debug=True, host="0.0.0.0")
