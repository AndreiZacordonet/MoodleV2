import grpc
from concurrent import futures
import time

import idm_pb2, idm_pb2_grpc

import idm
from exceptions.exceptions import *


class IdmServicer(idm_pb2_grpc.IdmServicer):

    def Authenticate(self, request, context):
        response = idm_pb2.AuthResponse()

        try:
            response.token = idm.authenticate(request.email, request.password)

            return response
        except UserNotFoundException as e:
            context.abort(grpc.StatusCode.NOT_FOUND, str(e))
        except InvalidCredentialsException as e:
            context.abort(grpc.StatusCode.UNAUTHENTICATED, str(e))


server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))


idm_pb2_grpc.add_IdmServicer_to_server(
    IdmServicer(), server
)


print("Starting server. Listening on port 50051.")
server.add_insecure_port('[::]:50051')

server.start()


try:
    while True:
        time.sleep(86400)
except KeyboardInterrupt:
    server.stop(0)

