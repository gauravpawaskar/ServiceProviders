import json
import os

from flask import Flask, request
from flask_pymongo import PyMongo

app = Flask(__name__)

db_user = os.environ.get('MONGO_DB_USER')
db_psw = os.environ.get('MONGO_DB_PSW')
db_host = os.environ.get('MONGO_HOST')
db_port = os.environ.get('MONGO_PORT')

app.config["MONGO_URI"] = "mongodb://{}:{}@{}:{}/user_tokens".format(
    db_user, db_psw, db_host, db_port)
mongodb_client = PyMongo(app)
db = mongodb_client.db
tokens_collection = None


def set_db():
    global tokens_collection
    collist = db.list_collection_names()
    if "tokens" not in collist:
        tokens_collection = db["customers"]


@app.route('/', methods=["GET"])
def index():
    return "Success"


@app.route('/token', methods=["GET", "POST"])
def token():
    if request.method == "POST":
        token_info = request.get_json()
        user_id = token_info["user_id"]
        service = token_info["service"]
        token = token_info["token"]
        app.logger.info(token_info)
        record = tokens_collection.find({"user_id": user_id})
        if record.count() == 0:
            tokens_collection.insert_one(
                {"user_id": user_id, "tokens": [{"service": service, "token": token}]})
        else:
            tokens_from_db = record[0]["tokens"]
            token_updated = False
            for token_from_db in tokens_from_db:
                if token_from_db["service"] == service:
                    token_from_db["token"] = token
                    token_updated = True

            if not token_updated:
                tokens_from_db.append({"service": service, "token": token})
            update_this = {"user_id": user_id}
            update_values = {"$set": {"tokens": tokens_from_db}}
            tokens_collection.update_one(update_this, update_values)
        return json.dumps({"status": "success"})

    else:
        user_id = request.args.get('user_id')
        service = request.args.get('service')
        record = tokens_collection.find({"user_id": user_id})
        if len(record) != 0:
            tokens_from_db = record[0]["tokens"]
            for token in tokens_from_db:
                if token["service"] == service:
                    return json.dumps({"user_id": user_id, "service": service, "token": token["token"], "status": "success"})
        return json.dumps({"status": "fail"})


if __name__ == "__main__":
    set_db()
    app.run(debug=True, host="0.0.0.0")
