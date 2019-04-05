# ForkExec

**Fork**: delicious menus for **Exec**utives

Distributed Systems 2018-2019, 2nd semester project

## Authors

Group T02

| Nome              | Número   | Github ID          |
| ----------------- | -------- | ------------------ | 
| Miguel Coelho     | 87687    | CubeSkyy           |
| Catarina Pedreira | 87524    | CatarinaPedreira   |
| Ricardo Silva     | 87700    | genlike            |


For each module, the README file must identify the lead developer and the contributors.
The leads should be evenly divided among the group members.


### Code identification

In all the source files (including POMs), please replace __CXX__ with your Campus: A (Alameda) or T (Tagus); and your group number with two digits.

This is important for code dependency management 
i.e. making sure that your code runs using the correct components and not someone else's.


## Getting Started

The overall system is composed of multiple services and clients.
The main service is the _hub_ service that is aided by the _pts_ service. 
There are also multiple _rst_ services, one for each participating restaurant.

See the project statement for a full description of the domain and the system.



### Prerequisites

Java Developer Kit 8 is required running on Linux, Windows or Mac.
Maven 3 is also required.

To confirm that you have them installed, open a terminal and type:

```
javac -version

mvn -version
```


### Installing

To compile and install all modules:

```
mvn clean install -DskipTests
```

The tests are skipped because they require each server to be running.

### Tests

Before testing, make sure to open the servers by running the  command:

```
mvn exec:java
```

in all server modules (hub-ws, pts-ws and rst-ws).

To run the tests, run the following command in the base folder:

```
mvn verify
```

## Changing UDDI server

Base project runs on RNL's UDDI server.

To change the uddi server, set the <uddi.url> atribute in base folder pom and in uddi-naming/pom to the desired url

## Built With

* [Maven](https://maven.apache.org/) - Build Tool and Dependency Management
* [JAX-WS](https://javaee.github.io/metro-jax-ws/) - SOAP Web Services implementation for Java



## Versioning

We use [SemVer](http://semver.org/) for versioning. 



