## Secure Message "Sender"
This program acts as a proof of concept for encoding and encrypting information messages that could be sent over digital
channels. It uses the RSA encryption scheme with toy values for N, E, and D that I have provided. As for encoding, this
program uses a cyclic Hamming code, meaning it is capable of correcting a single error. The supported character set for
this program is all uppercase and lowercase letters, all numbers, the space character, and characters in the set
{., !, ?, ,}. Also, there is a class that supports Galois Fields (aka Finite Fields) of size 2^n, which are used to 
 enhance encoding functionality. 
 
If you are curious about any of the relevant mathematics, I have provided some links below that should help.

https://en.wikipedia.org/wiki/RSA_(cryptosystem)  
https://en.wikipedia.org/wiki/Hamming_code  
https://en.wikipedia.org/wiki/Cyclic_code  
https://en.wikipedia.org/wiki/Primitive_polynomial_(field_theory)  
https://en.wikipedia.org/wiki/Finite_field