# Project

 EECS 3311 - Software Design Pattern

# Implementation


Database: Neo4J Database (NoSQL/Graph)
Server: Java (sun.net)
Testing: Robot Framework


# Use Case

Stores the Actors, movies and the released year of movie in the database. Computes Kevin Bacon number and returns shortest path to Kevin Bacon as well.


# Requirements ?

#1 Java SDK 1.8
#2 min. Python 3.0
#3 robot framework and request installed through pip


# How to Run?

Create a Neo4J database with the username : neo4j and password: 12345678

Run the database on the local server at port 7687.

Run App.java  --> (wait for the output: Server started on port 8080)

#Option 1: Run the localhost:8080/api/v1/{methodsname} on post man 

#Option 2: Open the terminal -> go to the project directory, type : robot testproject.robot


# Version
1.0.0



