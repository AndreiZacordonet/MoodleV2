import grpc
import idm_pb2
import idm_pb2_grpc

def run():
    with grpc.insecure_channel('localhost:50051') as channel:
        stub = idm_pb2_grpc.IdmStub(channel)
        print("Connected to gRPC server on localhost:50051.")

        while True:

            print("\nEnter commnad: login / validate / exit:")
            command = input("> ").strip()

            if command.lower() == "exit":
                print("Exiting client...")
                break

            elif command.lower() == "login":
                print("\nEnter credentials for authentication")
                email = input("Email: ").strip()

                password = input("Password: ").strip()
                if password.lower() == "exit":
                    print("Exiting client...")
                    break

                try:
                    request = idm_pb2.AuthRequest(email=email, password=password)
                    response = stub.Authenticate(request)

                    # Step 4: Display the response
                    print(f"Received token: {response.token}")
                except grpc.RpcError as e:
                    print(f"Error: {e.code()} - {e.details()}")

            elif command.lower() == "validate":
                print("\nEnter jwt")
                jwt = input("jwt: ").strip()

                try:
                    request = idm_pb2.ValidateOrDestroyRequest(token=jwt)
                    response = stub.Validate(request)

                    print(f"Response:\nSub: {response.sub}\tRole: {response.role}")
                except grpc.RpcError as e:
                    print(f"Error: {e.code()} - {e.details()}")


if __name__ == '__main__':
    print("Starting gRPC client... Press 'exit' to quit.")
    run()
