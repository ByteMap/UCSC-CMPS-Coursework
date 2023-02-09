%Conversion to radians
convert_radians(degmin(Degrees, Minutes), Radians) :-
	Radians is (Degrees + (Minutes / 60)) * pi / 180.

%Conversion to haversine
convert_haversine(Lat1, Long1, Lat2, Long2, Dist) :-
	Dlong is Long2 - Long1,
	Dlat is Lat2 - Lat1,
    Tmpa is sin(Dlat / 2) ** 2 
    		+ cos(Lat1) * cos(Lat2) * sin(Dlong / 2) ** 2,
	UnitDist is 2 * atan2(sqrt(Tmpa), sqrt(1 - Tmpa)),
	Dist is UnitDist * 3961.

%Calculates the time in flight and returns the Hour and Minute of the
%time taken in flight
time_in_flight(Origin, Destination, Time, Hour, Minute) :-
	airport(Origin, _, Lat1, Long1),
	airport(Destination, _, Lat2, Long2),
	convert_radians(Lat1, RadLat1),
	convert_radians(Long1, RadLong1),
	convert_radians(Lat2, RadLat2),
	convert_radians(Long2, RadLong2),
	convert_haversine(RadLat1, RadLong1, RadLat2, RadLong2, Dist),
	Time is (Dist / 500),
	TimeMinutes = floor(Time * 60),
	Hour is div(TimeMinutes, 60),
	Minute is mod(TimeMinutes, 60).

%Arrival Time that returns the Hour of Arrival and the Minute of Arrival
arrival_time(flight(Origin, Destination, 
			time(Hour, Minute)), ArrivalH, ArrivalM) :-
	time_in_flight(Origin, Destination, Time, FlightHour, FlightMinute),
	TotalTimeMinutes = ((Hour + FlightHour) * 60) + 
						(Minute + FlightMinute),
	ArrivalH is div(TotalTimeMinutes, 60),
	ArrivalM is mod(TotalTimeMinutes, 60).

%Departure Time that returns the Hour of Departure and the Minute of
%Departure
departure_time(flight(Origin, Destination, time(Hour, Min)), 
			   ResHour, ResMin) :-
	ResHour is Hour,
	ResMin is Min.

%Not method in Prolog
not(X) :- X, !, fail.
not(_).

%create_path called from fly() and calls create_path/4.
create_path(Origin, Destination, 
		   [flight(Origin, NextOrigin, Time)| RestOfFlights]) :-
	flight(Origin, NextOrigin, Time),
	create_path(NextOrigin, Destination, 
				[flight(Origin, NextOrigin, Time)], RestOfFlights).

%If the Rest of Flights has been traversed through and the Origin
%is now at the Destination, exit create_path successfully.
create_path(Destination, Destination, FlightList, []).

%Finds the path from the origin to the destination by traversing through
%all list of flights, adding the airport and time to the list,
%and recursively calling itself.
create_path(Origin, Destination, 
			[flight(PrevOrigin, CurrOrigin, PrevTime)| PrevList],
			[flight(Origin, NextOrigin, Time)| List]) :-
	%write('Hi'),
	flight(Origin, NextOrigin, Time),
	arrival_time(flight(PrevOrigin, CurrOrigin, PrevTime), 
				 ArrHour, ArrMin),
	departure_time(flight(Origin, NextOrigin, Time), DepHour, DepMin),
	ArrivalMins is (ArrHour * 60) + ArrMin,
	DepartMins is (DepHour * 60) + DepMin,
	DepartMins >= ArrivalMins + 30,
	not(CurrOrigin = NextOrigin),
	not(PrevOrigin = Origin),
	NewPrevList = append([flight(PrevOrigin, CurrOrigin, PrevTime)], 
						PrevList),
	not(member(NextOrigin, NewPrevList)),
	create_path(NextOrigin, Destination, 
			   [flight(Origin, NextOrigin, Time)| NewPrevList], List).

%If the list is empty, return successfully.
print_path([]).

%Prints the path of each airport, and calls itself recursively
print_path([flight(OriginAbbr, DestinationAbbr, 
			time(Hour, Minute))| RestOfFlights]) :-
	airport(OriginAbbr, OriginName, Lat1, Long1),
	airport(DestinationAbbr, DestinationName, Lat2, Long2),
	arrival_time(flight(OriginAbbr, DestinationAbbr, 
				time(Hour, Minute)), ArrH, ArrM),
	nl,
	write('depart '),
	write(OriginAbbr),
	write(' '),
	write(OriginName),
	write(' '),
	write(Hour),
	write(':'),
	(Minute < 10 -> write('0') ; write('')),
	write(Minute),
	nl,
	write('arrive '),
	write(DestinationAbbr),
	write(' '),
	write(DestinationName),
	write(' '),
	write(ArrH),
	write(':'),
	(ArrM < 10 -> write('0') ; write('')),
	write(ArrM),
	print_path(RestOfFlights).

%If Destination is Origin, write out the error
fly(Destination, Destination) :-
	nl,
	write('Already at your destination').

%Main method to find the path of Origin to Destination.
fly(Origin, Destination) :-
	%time_in_flight(Origin, Destination, Time, Hour, Minute),
	/*airport(Origin, _, Lat1, Long1),
	airport(Destination, _, Lat2, Long2),
	convert_radians(Lat1, RadLat1),
	convert_radians(Long1, RadLong1),
	convert_radians(Lat2, RadLat2),
	convert_radians(Long2, RadLong2),
	convert_haversine(RadLat1, RadLong1, RadLat2, RadLong2, Dist),
	write(Dist).*/
	(create_path(Origin, Destination, FlightList) -> 
												print_path(FlightList) ;
												write('No path from '), 
												write(Origin), 
										 		write(' to '), 
										 		write(Destination),
						 						write(' exists.')),
	%write(FlightList),
	/*(list(FlightList) -> print_path(FlightList) ; 
						 write('No path from '), write(Origin), 
						 write(' to '), write(Destination)
						 write(' exists.')),*/
	%print_path(FlightList),
	!.