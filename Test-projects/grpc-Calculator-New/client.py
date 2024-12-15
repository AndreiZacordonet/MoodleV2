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
    print("  sum <number> <number> - to calculate the sum of 2 numbers")
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
            if len(parts) not in (2, 3):
                raise ValueError("Invalid command format. Expected format: <command> <number>")

            operation = parts[0]
            number_params_str = parts[1:]
            number_params = [float(x) for x in number_params_str]

            if operation.lower() == 'abs':
                number = number_params[0]
                request = calculator_pb2.Number(value=number)
                response = stub.AbsoluteValue(request)
                print(f"Absolute value of {number} is {response.value}")

            elif operation.lower() == 'sqrt':
                number = number_params[0]
                request = calculator_pb2.Number(value=number)
                response = stub.SquareRoot(request)
                print(f"Square root of {number} is {response.value}")

            elif operation.lower() == 'sum':
                number1, number2 = number_params[0:2]
                request = calculator_pb2.SumNumbers(a=number1, b=number2)
                response = stub.Sum(request)
                print(f"Sum of {number1} and {number2} is {response.value}")

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
