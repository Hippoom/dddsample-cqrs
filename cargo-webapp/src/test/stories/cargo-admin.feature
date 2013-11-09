Feature: Cargo Registration

In order to get my cargo shipped
As a customer
I want to register a new cargo

Background: Customer registers a new cargo

When I fill the form with origin, destination and arrival deadline
Then a new cargo is registered
But the cargo is not routed
And the transport status of the cargo is NOT_RECEIVED


Scenario: Customer assigns the cargo to route

Given I request possible routes for the cargo
And some routes are shown
When I pick up a candidate
Then the cargo is assigned to the route
And the cargo is routed
And the estimated time of arrival equals to the last unloaded time of the route 
And the next expected handling activity is being received at the origin of the route specification