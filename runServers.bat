set "hub=%cd%\hub-ws"
set "pts=%cd%\pts-ws"
set "rst=%cd%\rst-ws"

start cmd /k "cd %hub% & mvn exec:java"
start cmd /k "cd %rst% & mvn exec:java"
start cmd /k "cd %pts% & mvn exec:java -Dws.i=1"
start cmd /k "cd %pts% & mvn exec:java -Dws.i=2"
start cmd /k "cd %pts% & mvn exec:java -Dws.i=3"