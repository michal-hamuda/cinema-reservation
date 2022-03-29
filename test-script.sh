echo ""
echo "Cinema reservations demo"
echo ""
echo "--------------------------------"
echo "Search for available screenings"
echo ""
echo "curl localhost:8080/screenings/search?from=`date +"%Y-%m-%dT%H:%M:%S"`.000\&to=`date +"%Y-%m-%dT%H:%M:%S" --date="4 hours"`.000"
echo ""
curl localhost:8080/screenings/search?from=`date +"%Y-%m-%dT%H:%M:%S"`.000\&to=`date +"%Y-%m-%dT%H:%M:%S" --date="4 hours"`.000
echo ""
echo ""
echo "--------------------------------"
echo "Get screening details (info about available/taken seats)"
echo ""
echo "curl localhost:8080/screenings/103bb746-88a1-4239-ae5b-414c04ae4dab"
echo ""

screening=`curl localhost:8080/screenings/103bb746-88a1-4239-ae5b-414c04ae4dab`
echo ""
echo $screening
totalRows=`echo $screening | grep -oP 'totalRows":\K[0-9.]+'`
echo $totalRows

echo ""
echo ""
echo "--------------------------------"
echo "Create reservation with one seat"
echo ""
echo "curl -d '{\"userFirstName\":\"Thomas\", \"userLastName\":\"Anderson\", \"screeningId\":\"103bb746-88a1-4239-ae5b-414c04ae4dab\", \"seats\":[{\"row\":1,\"column\":1,\"priceCategory\":\"Adult\"}] }'  -H \"Content-Type: application/json\" -X POST http://localhost:8080/reservations"
echo ""

createReservationResponse=`curl -d '{"userFirstName":"Thomas", "userLastName":"Anderson", "screeningId":"103bb746-88a1-4239-ae5b-414c04ae4dab", "seats":[{"row":1,"column":1,"priceCategory":"Adult"}] }'  -H "Content-Type: application/json" -X POST http://localhost:8080/reservations`
echo ""
echo $createReservationResponse
confirmationLink=`echo $createReservationResponse | grep -oP 'confirmationLink":"\K[a-z:/\-0-9]+'`


echo ""
echo "--------------------------------"
echo "Confirm reservation"
echo ""
echo "curl $confirmationLink"
echo ""
curl $confirmationLink
echo ""
echo ""

