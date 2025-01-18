import grpc
import idm_pb2
import idm_pb2_grpc

def validate_token(token: str):
    """
    Validates a token by communicating with the gRPC server.
    """
    with grpc.insecure_channel('localhost:50051') as channel:
        stub = idm_pb2_grpc.IdmStub(channel)
        try:
            response = stub.Validate(idm_pb2.ValidateOrDestroyRequest(token=token))
            return {"sub": response.sub, "role": response.role}
        except grpc.RpcError as e:
            if e.code() == grpc.StatusCode.UNAUTHENTICATED:
                raise ValueError("Invalid or expired token")
            else:
                raise RuntimeError(f"gRPC error: {e.details()}")
