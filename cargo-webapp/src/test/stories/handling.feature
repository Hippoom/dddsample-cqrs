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

Scenario: Operator registers a handling event of which type is UNLOAD

Given a cargo has been routed
And the cargo is received
When I register a new handling event of which type is UNLOAD
Then the transport status of the cargo is IN_PORT 
And the next expected handling activity is being loaded to the next leg's voyage
 
Scenario: Operator registers the last handling event of which type is UNLOAD

Given a cargo has been routed
And the cargo is received
When I register the last handling event of which type is UNLOAD
Then the transport status of the cargo is IN_PORT
And the cargo is unloaded at the destination
And the next expected handling activity is claimed at the destination

Scenario: Operator registers a handling event of which type is CLAIM

Given a cargo has been routed
And the cargo has been unloaded at the destination
When I register a handling event of which type is CLAIM
Then the transport status of the cargo is CLAIMED