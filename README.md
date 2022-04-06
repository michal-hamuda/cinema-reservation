# cinema-reservation

A microservice handling cinema ticket reservations.

Technologies: Scala, Slick, Akka Http, Macwire, database H2 (in memory) for demonstration purposes

Setup: just run ``sbt run``, or `run-app.sh`, after that you can run `.test-script.sh` to show the example requests 

Assumptions:
- for the simplicity of presentation, no authorisation/authentication
- for simplicity no timezones handling or multiple currencies handling (will be totally sufficient for a system used only in one cinema location)
- excluded payment handling, since it was not specified in the requirements
- every room is a rectangle (by that I mean, seats are aligned in rows and every row has the same amount of seats)
- user specifies reservations in the following format: I want the following seats for screening X: seat in row 1 column 3 for adult, seat in row 1 column 4 for a child, etc.
- additional task implemented - Reservation confirmation
