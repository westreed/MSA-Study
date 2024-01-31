from flask import Flask
from py_eureka_client import eureka_client

instance_port = 40010
eureka_client.init(
    eureka_server="http://discovery-service:8761/eureka",# "http://localhost:8761/eureka",
    app_name="llm-service",
    instance_host="localhost",
    instance_port=instance_port,
)

app = Flask(__name__)


@app.route('/info', methods=["GET"])
def test3():
    return "hello eureka"


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=instance_port, debug=True)