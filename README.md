# POP

Programming Internship Partial-Order Planner

## Description

This Partial-Order Planner solves problems written in STRIPS-language and delivers an output that represents the solution. 

## Installation

Install Java [JDK 20: openjdk version "20"](https://jdk.java.net/20/).

Once you have the JDK installed, you can clone this repository to your local machine using the following command:
```bash
git clone https://git.tu-berlin.de/programmierpraktikum/POP.git
```

Install the java build system maven: [Maven Download](https://maven.apache.org/download.cgi)  
Compile the program:
```bash
cd POP/my-app && mvn compile
```

Or execute to create a standalone JAR file:
```bash
cd POP/my-app && mvn package
```

The JAR file can be found under `POP/my-app/target/my-app-1.0-SNAPSHOT.jar`


## Usage

To run this project follow one of the following instructions:

Run in the JDK bin folder with java.exe:
```
./java.exe -cp "<PATH TO POP>\POP\out\production\POP\;<PATH TO POP>\POP\lib\json-20220924.jar" Main
```

Run in the POP folder (make sure the System recognises java from JDK-20):
```
java -cp ".\out\production\POP\;.\lib\json-20220924.jar" Main
```

## Authors

- Hok Kai John Cheung
- Luca Garriet Janßen
- Niklas Markus Keitsch
- Leon Neubert
- Hanna Leoni Riegel
- Daniel Taubkin
- Johannes Uhlig

## License

todo

## Project status

This project is currently in active development. We are working on adding new features and improving existing functionality. The current version is stable and ready for use, but please keep in mind that there may be some bugs or issues as we continue to develop the project.

## Wiki

- [Wiki](https://git.tu-berlin.de/programmierpraktikum/POP/-/wikis/home)

## Contributing

1. Generate SSH keys and copy the public key to your gitlab SSH Keys in the User Settings
```
cd ~/.ssh
ssh-keygen -t rsa -C "your_email@example.com"
cat .\id_rsa.pub
```
2. Create an own [fork](https://git.tu-berlin.de/programmierpraktikum/POP/-/forks) of this project
3. Clone and from this project and push to fork:
```
git clone git@git.tu-berlin.de:programmierpraktikum/POP.git
cd POP
git remote add fork git@git.tu-berlin.de:< FORKNAME >.git
git add .
git commit -m "message"
git push fork (note: git push origin would push directly to the project repository, pushing to fork and then merge is recommended)
git pull (to get latests changes from others)
```
