# Storm Projects

TODO : Project 1: Database Backup Management. 

Technologies : 
1. Storm (Streaming Updates)
2. Rabbit Mq (Messaging the Events) 
3. Spring (Integration and DI) 

Production database is continously monitored for any insert or updates operations. 
When ever there is an event, The Thread sends a message to Rabbit MQ to report any change (insert/update/delete)
Storm Spout Acts as a listener to these messages and Sends the Event across the Bolts to replicate this on the Backup database. 

Brainstorming: 
Spouts: OracleSpout(Like wise for Other DB's) --> ODDLBolt --> OAlterBolt
                                              --> ODMLBolt --> OInsertBolt, OUpdateBolt, ODeleteBolt.
                                              
Topic Name: MONITOR
Message Sendery key : db.dml or db.ddl
Message Receiver Key: db.*

Based on the target(Oracle or mysql properties) (1/more) 
