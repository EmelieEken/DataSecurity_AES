README Assignment 2

To run the code: 

    java AESInterface <filename>


Files included in this assignment:

-AESInterface.java
Reads the input file and extracts the settings for the encryption/decryption, text and key and convert 
them into the right format.
Relies on the Encryption class to do the encryption and Decyption class to do the decryption.

-Encryption.java
Encryption contains an array of blocks (the message), a mode of operation (represented by an int), a transmission size and an initialisation vector.
Its main method is to return the cipher message (String) corresponding to the block array encryption. There is one different function for each operation mode. They use functions of the Block class to compute every rounds.

-Decryption.java
Works the same than Encryption but whith the Block decryption functions.

-Block.java
This is the base ouf our project. It correspond to a state. It is designed as a 4x4 int matrix. The ints are evaluated as byte values.
This class contains all the operations required for the states. That includes adding two blocks (XOR), process all the operation in one round (byte substitution, shift rows, mix columns, add round key) for both encryption and decryption and also a toString() function.


-Polynomial.java
We use Polynomial in GF(8^2) for the mix column and the key expansion. It allows us to create, add, multiply and divide polynomials to compute bytes values.
Polynomial are represented by an array of boolean for the coefficients and an int for its degree.

-KeyManipulation.java
Expands a given key following the key expansion algorithm for AES.
