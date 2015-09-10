Market Data Exporter 

Application calls the MarkitOnDemand API with all the stock symbols and get the data. 

API:  http://dev.markitondemand.com/Api/v2/Quote/json?symbol=AAPL
API with JSONP : http://dev.markitondemand.com/Api/v2/Quote/jsonp?symbol=AAPL&callback=myFunction

MarketDataImporterSpout : Parses the Stock Symbols File (stocksymbols.txt) and calls the above API and emits each response to mongoStorageBolt. 

MarketDataExporterBolt: Inserts data into MongoDB. 

Scheduler

EODFeedConsolidator: Runs every night at 1PM monday to friday. Consolidates the vast amount of data stored in Mongo db into json string acording to company name and the timestamp. ex: 
	AAPL:{"9-10-2015 9:10AM":{"high":"","low":""},...}
	
Scheduler is managed by Spring-Scheduler
Uses Apache http client to call the service. 
Storm for continuous processing. 
Joda time to run the topology only on Mon-Fri 9AM to 5 PM. 
