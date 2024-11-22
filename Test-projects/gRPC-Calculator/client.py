import grpc

import calculator_pb2
import calculator_pb2_grpc


def run():
    with grpc.insecure_channel('localhost:50051') as channel:
        stub = calculator_pb2_grpc.CalculatorServiceStub(channel)
        response = stub.Add(calculator_pb2.CalculatorRequest(a=1, b=2))
        print(f"Greeter client received: {response.r}")
        # response = stub.Add(calculator_pb2.CalculatorRequest(a=2, b=3))
        # print("Greeter client received: " + response.r)

run()