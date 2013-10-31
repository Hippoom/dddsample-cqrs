Feature: Cargo Registration

In order to get my cargo shipped
As a customer
I want to register a new cargo

Scenario: Customer registers a new cargo

When I fill the form with origin, destination and arrival deadline
Then a new cargo is registered
And the tracking id is shown for following steps

Scenario: Customer assigns the cargo to route

Given a cargo has been registered
And I request possible routes for the cargo
And some routes are shown
When I pick up a candidate
Then the cargo is assigned to the route