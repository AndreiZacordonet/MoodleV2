import grpc
import calculator_pb2
import calculator_pb2_grpc


def main():
    # Connect to the server
    channel = grpc.insecure_channel('localhost:50051')
    stub = calculator_pb2_grpc.CalculatorStub(channel)

    print("Connected to gRPC server. Enter commands in the format:")
    print("  abs <number> - to calculate the absolute value")
    print("  sqrt <number> - to calculate the square root")
    print("Type 'exit' to quit.")

    while True:
        # Read command from the user
        command = input("Enter command: ").strip()

        if command.lower() == 'exit':
            print("Exiting client...")
            break

        try:
            # Parse the command
            parts = command.split()
            if len(parts) != 2:
                raise ValueError("Invalid command format. Expected format: <command> <number>")

            operation, number_str = parts
            number = float(number_str)

            if operation.lower() == 'abs':
                request = calculator_pb2.Number(value=number)
                response = stub.AbsoluteValue(request)
                print(f"Absolute value of {number} is {response.value}")

            elif operation.lower() == 'sqrt':
                request = calculator_pb2.Number(value=number)
                response = stub.SquareRoot(request)
                print(f"Square root of {number} is {response.value}")

            else:
                print("Unknown command. Supported commands are 'abs' and 'sqrt'.")

        except ValueError as e:
            print(f"Error: {e}")
        except grpc.RpcError as e:
            print(f"gRPC Error: {e.details()} (Code: {e.code()})")


if __name__ == '__main__':
    main()

# import grpc
# import sys
#
# # import the generated classes
# import calculator_pb2
# import calculator_pb2_grpc
#
# # open a grpc channel
# channel = grpc.insecure_channel('localhost:50051')
#
# # create a stub (client)
# stub = calculator_pb2_grpc.CalculatorStub(channel)
#
# # create a valid request message
# number = calculator_pb2.Number(value=int(sys.argv[1]))
#
# # make a call
# response = stub.SquareRoot(number)
#
# print(response)
