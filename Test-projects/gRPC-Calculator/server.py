from concurrent import futures
import grpc
import calculator_pb2
import calculator_pb2_grpc


class CalculatorServicer(calculator_pb2_grpc.CalculatorService):
    def Add(self, request, context):
        result = request.a + request.b
        return calculator_pb2.CalculatorResponse(r=result)

    # def Subtract(self, request, context):
    #     result = request.a - request.b
    #     return calculator_pb2.CalculatorResponse(value=result)
    #
    # def Multiply(self, request, context):
    #     result = request.a * request.b
    #     return calculator_pb2.CalculatorResponse(value=result)
    #
    # def Divide(self, request, context):
    #     if request.b == 0:
    #         context.set_details('err div by 0')
    #         context.set_code(grpc.StatusCode.INVALID_ARGUMENT)
    #         return calculator_pb2.CalculatorResponse()
    #     result = request.a / request.b
    #     return calculator_pb2.CalculatorResponse(value=result)


def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    calculator_pb2_grpc.add_CalculatorServiceServicer_to_server(
        CalculatorServicer(),
        server
    )
    server.add_insecure_port('[::]:50051')

    server.start()
    print("running on 50051...")
    server.wait_for_termination()


# if __name__ == '__main__':
serve()