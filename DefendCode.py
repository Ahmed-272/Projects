import os
import re
import hashlib
import logging

logging.basicConfig(filename='error.log', level=logging.ERROR, format='%(asctime)s - %(message)s')

def get_name():
    print("Enter your first name (at most 10 characters, no special characters, using only letters (A-Z, a-z)): ")
    first_name = input()
    while len(first_name) > 10 or not re.match("^[a-zA-Z ]+$", first_name):
        print("Invalid input. Enter your first name again: ")
        first_name = input()

    print("Enter your last name (at most 10 characters, no special characters, using only letters (A-Z, a-z)): ")
    last_name = input()
    while len(last_name) > 10 or not re.match("^[a-zA-Z ]+$", last_name):
        print("Invalid input. Enter your last name again: ")
        last_name = input()

    print("First Name:", first_name)
    print("Last Name:", last_name)
    return first_name + " " + last_name

def get_int():
    print("Enter the first integer value (4-byte int range, between -2,147,483,648 and 2,147,483,647): ")
    while True:
        try:
            input1 = int(input())
            break
        except ValueError:
            print("Error: Please enter a valid integer.")
    print("Enter the second integer value (4-byte int range, between -2,147,483,648 and 2,147,483,647): ")
    while True:
        try:
            input2 = int(input())
            break
        except ValueError:
            print("Error: Please enter a valid integer.")
    print("First Integer:", input1)
    print("Second Integer:", input2)
    return input1, input2

def is_valid_file_name(file_name):
    return bool(re.match("^.+\.txt$", file_name))

def get_input_file():
    while True:
        print("Enter the name of your input file (.txt extension required): "
              "(A-Z, a-z) white spaces allowed. File must be in the same folder as this code")
        input_file_name = input()
        if is_valid_file_name(input_file_name) and os.path.exists(input_file_name):
            return input_file_name
        else:
            print("Invalid file name or file does not exist. Please make sure it ends with '.txt'. Try again.")

def get_output_file():
    while True:
        print("Enter the name of your output file (.txt extension required): "
              "(A-Z, a-z) white spaces allowed.")
        output_file_name = input()
        if is_valid_file_name(output_file_name):
            return output_file_name
        else:
            print("Invalid file name. Please make sure it ends with '.txt'. Try again.")

def get_password():
    while True:
        print("Enter password (at most 10 characters), letters (A-Z, a-z) special characters are allowed: ")
        password = input()
        if 0 < len(password) <= 10:
            break
        else:
            print("Password must be at most 10 characters long. Please try again.")

    salt = os.urandom(16)
    hashed_password = hashlib.sha256(password.encode() + salt).hexdigest()
    store_file(output_file_name, hashed_password)
    check_password(hashed_password, salt)

def store_file(file_path, hashed_password):
    try:
        with open(file_path, 'w') as f:
            f.write(hashed_password)
            print("Successfully wrote to the file.")
    except Exception as e:
        print("An error occurred.")
        logging.error("An error occurred: " + str(e))

def check_password(stored_hash, salt):
    while True:
        print("Enter password again for verification: ")
        password = input()
        hashed_password = hashlib.sha256(password.encode() + salt).hexdigest()

        if hashed_password == stored_hash:
            print("Password verified successfully!")
            break
        else:
            print("Password verification failed. Please try again.")

def write_output_to_file(name, numbers, input_file_name, output_file_name):
    try:
        with open(output_file_name, 'w') as output_file:
            output_file.write("User's Name: " + name + "\n")
            output_file.write("First Integer: " + str(numbers[0]) + "\n")
            output_file.write("Second Integer: " + str(numbers[1]) + "\n")
            try:
                sum_of_integers = numbers[0] + numbers[1]
                output_file.write("Sum of Integers: " + str(sum_of_integers) + "\n")
            except ArithmeticError:
                output_file.write("Sum of Integers: Overflow occurred\n")
            try:
                product_of_integers = numbers[0] * numbers[1]
                output_file.write("Product of Integers: " + str(product_of_integers) + "\n")
            except ArithmeticError:
                output_file.write("Product of Integers: Overflow occurred\n")

            output_file.write("Input File Name: " + input_file_name + "\n")
            output_file.write("Input File Contents:\n")
            with open(input_file_name, 'r') as input_file:
                for line in input_file:
                    output_file.write(line)
            print("Successfully wrote to the output file.")
    except Exception as e:
        raise RuntimeError(e)

def main():
    name = get_name()
    numbers = get_int()
    input_file_name = get_input_file()
    output_file_name = get_output_file()
    write_output_to_file(name, numbers, input_file_name, output_file_name)
    get_password()

if __name__ == "__main__":
    main()
