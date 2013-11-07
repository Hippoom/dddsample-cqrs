Feature: Handling Event Registration

In order to get the cargo tracked
As an operator
I want to register a new handling event

Scenario: Operator registers the first handling event of which type is RECEIVE

Given a cargo has been routed
When I register a new handling event of which type is RECEIVE 
Then the transport status of the cargo is IN_PORT
And the last known location of the cargo is updated as the location of the handling event
And the next expected handling activity is being loaded to the leg's voyage
 
Scenario: Operator registers a handling event of which type is LOAD

Given a cargo has been routed
And the cargo is received
When I register a new handling event of which type is LOAD
Then the transport status of the cargo is ONBOARD_CARRIER 
And the next expected handling activity is being unloaded to the leg's unload location
