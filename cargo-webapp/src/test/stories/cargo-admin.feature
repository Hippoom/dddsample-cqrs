Feature: Cargo Registration

In order to get my cargo shipped
As a customer
I want to register a new cargo

Scenario: Customer registers a new cargo

When I fill the form with origin, destination and arrival deadline
Then a new cargo is registered
And the cargo is not routed
And the tracking id is shown for following steps

Scenario: Customer assigns the cargo to route

Given a cargo has been registered
And I request possible routes for the cargo
And some routes are shown
When I pick up a candidate
Then the cargo is assigned to the route
And the cargo is routed
And the estimated time of arrival equals to the last unloaded time of the route 
Then the transporting status of the cargo is NOT_RECEIVED
And the next expected handling activity is being received at the origin of the route specification