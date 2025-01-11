from fastapi import FastAPI, HTTPException
from pydantic import BaseModel

import grpc
import idm_pb2
import idm_pb2_grpc

app = FastAPI()


class LoginRequest(BaseModel):
    email: str
    password: str


class LogoutRequest(BaseModel):
    token: str


grpc_server_address = "localhost:50051"


@app.post("/login")
async def login(login_request: LoginRequest):
    try:
        with grpc.insecure_channel(grpc_server_address) as channel:
            stub = idm_pb2_grpc.IdmStub(channel)

            request = idm_pb2.AuthRequest(email=login_request.email, password=login_request.password)
            response = stub.Authenticate(request)

            return {"token": response.token}

    except grpc.RpcError as e:
        # FIXME: function to translate status codes from RpcError to http
        raise HTTPException(status_code=e.code().value[0], detail=e.details())


@app.post("/logout")
async def logout(logout_request: LogoutRequest):
    try:
        with grpc.insecure_channel(grpc_server_address) as channel:
            stub = idm_pb2_grpc.IdmStub(channel)

            request = idm_pb2.ValidateOrDestroyRequest(token=logout_request.token)

            response = stub.Destroy(request)

            return {"message": response.success}

    except grpc.RpcError as e:
        # FIXME: function to translate status codes from RpcError to http
        raise HTTPException(status_code=e.code().value[0], detail=e.details())
