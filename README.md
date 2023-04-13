# COP4520 - Programming Assignment 3

### _This program was written by: Pedro Henrique Sotto-Mayor Pereira da Silva_

<br />

### Compiling and running the Birthday Presents code:

---

1. Open a windows terminal.

2. Go to the directory of **BirthdayPresents.java** file by using `cd` command.

3. Type **javac BirthdayPresents.java** and hit enter to compile the code.

4. Type **java BirthdayPresents** and hit enter to run the code.

5. The code will run and ask for the number of guests.

<br />

### Compiling and running the Temperature code:

---

1. Open a windows terminal.

2. Go to the directory of **Temperature.java** file by using `cd` command.

3. Type **javac Temperature.java** and hit enter to compile the code.

4. Type **java Temperature** and hit enter to run the code.

5. The code will run and ask for the number of guests.

<br />

### Program design:

---

**Birthday Presents**:

This program was designed to simulate a concurrent "Thank you" card writing scenario by dedicating 1 thread per servant and assuming that the Minotaur received 500000 presents from his guests. This is accomplished by implementing a concurrent linked list that represents a 'chain' of presents order by their tag numbers in increasing order.

<br />

**Temperature**:

This program was designed to simulate the operation of the temperature reading sensor by generating a random number from -100F to 70F at every reading. This is done by having 8 threads where each represent a different sensor, and each sensor can read 60 readings in an hour (1 reading per minute). Also, a report is generated at the end of each hour interval, giving the top 5 highest temperatures, top 5 lowest temperatures, and the largest temperature difference observed in a 10-min interval for the current hour.

<br />

### Correctness, Efficiency, and Evaluation:

---

**Birthday Presents**:

My strategy for this problem was to have 4 threads representing 4 servants of the Minotaur and a concurrent linked list of gifts. Each thread could perform 3 possible tasks in no particular order: add present to the linked list, write "Thank you" card to guest, and search for a present in the linked list. I also implemented a concurrent hash map to store the cards that were written to the guests. A navigable set of integers (meaning unordered) was also implemented to represent all gifts attached with their tags (number). After filling the set with random numbers up to 500000, everything is setup for the servants to start working.

Each servant would do a random task out of the 3 possible tasks cited earlier. If the task is add present to the linked list, the servant takes the next number in the gift bag in a sychronized block, and then inserts the number (gift) into the linked list.
If the task is write "Thank you" card, the servant would check if the list is empty. If not empty, remove the head of the linked list and add the card to the cards concurrent hash map. If the task is search for present, the servant will simply traverse through the linked list to find the present.

### _Execution Time:_

As far as execution time, my implementation runs anywhere between 1s to 9s on my computer. This large interval possibly occurs due to the random nature of the program, meaning the gift numbers and the servant tasks are randomized. My implementation also requires locks in several steps, which also impacts the execution time. For example, the linked list needs a lock to remove, insert, and search as well as the gift bag and card sets for insert and remove.
<br />

**Temperature**:

My implementation of the atmospheric temperature of Mars Rover is to have 8 threads each representing a sensor. Also, having a list of integers of size number_threads x minutes (8 x 60 or 480). Note that this means we have 480 readings every hours. This implementation is efficient because thread 0 would be responsible for indexes 0-59, thread 1 would be responsible for indexes 60-119, etc. For this reason, we do not need locks because each thread has its own portion of the array, so there is no concurrency here.

I also implemented a list of booleans of size num_threads that represent if each thread is ready for another temperature reading. Before the sensor reads a temperature, the current thread sets its state to false in the list, reads the temperature, and then sets its state to true, meaning it is ready for the next temperature reading. After reading the temperature, the threads will keep checking if all the sensors have read a temperature before going for another reading.

After every 60 minutes or 1 hour, thread 0 is responsible for generating the report for that hour, containing: the top 5 highest temperatures, the top 5 lowest temperatures, and the largest temperature difference observed in a 10-min interval along with what interval that occured.

### _Execution Time:_

As far as execution time, by running for 48 "hours", my implementation takes approximately 100ms to 150ms on my computer. This time is possibly slightly increased since each thread needs to spin waiting for the other ones to be ready. However, the good thing about this implementation is that we do not use locks, so there is no deadlock not starvation.
