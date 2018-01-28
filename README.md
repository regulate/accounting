# accounting

This is a simple scala/play demo application which provides web service to perform transactions on single user's balance
and accessing transactions history

## Running

Run this using [sbt](http://www.scala-sbt.org/):

```bash
sbt run
```

You'll find a prepackaged version of sbt under ```sbt-dist```. Also, there is ```sbt``` 
and ```sbt.bat``` under the root of the project to run [sbt](http://www.scala-sbt.org/) quickly.

Then you'll find application server running under ```http://localhost:9000```.

## Api explained
|Http verb|URL               |Description                                |Example request|
|---------|------------------|-------------------------------------------|---------------
|GET      |/balance          |Gets current balance<br>                   |```curl -X GET http://localhost:9000/balance```
|POST     |/balance/debit    |Performs debit operation                   |```curl -X POST http://localhost:9000/balance/debit -H 'content-type: application/json' -d '{"amount" : 1.1}'```
|POST     |/balance/credit   |Performs credit operation                  |```curl -X POST http://localhost:9000/balance/credit -H 'content-type: application/json' -d '{"amount" : 1.1}'```
|GET      |/transactions     |Returns list of all performed transactions |```curl -X GET http://localhost:9000/transactions```
|GET      |/transactions/:id |Returns particular transaction with id=:id |```curl -X GET http://localhost:9000/transactions/1```
