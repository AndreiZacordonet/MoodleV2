import grpc
import idm_pb2
import idm_pb2_grpc

def run():
    # Step 1: Connect to the gRPC server
    with grpc.insecure_channel('localhost:50051') as channel:
        stub = idm_pb2_grpc.IdmStub(channel)
        print("Connected to gRPC server on localhost:50051.")

        while True:
            # Step 2: Interactive input loop
            print("\nEnter credentials for authentication (or type 'exit' to quit).")
            email = input("Email: ").strip()
            if email.lower() == "exit":
                print("Exiting client...")
                break

            password = input("Password: ").strip()
            if password.lower() == "exit":
                print("Exiting client...")
                break

            # Step 3: Send the gRPC request
            try:
                request = idm_pb2.AuthRequest(email=email, password=password)
                response = stub.Authenticate(request)

                # Step 4: Display the response
                print(f"Received token: {response.token}")
            except grpc.RpcError as e:
                # Handle any errors
                print(f"Error: {e.code()} - {e.details()}")

if __name__ == '__main__':
    print("Starting gRPC client... Press 'exit' to quit.")
    run()
