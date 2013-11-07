Feature: Handling Event Registration

In order to get the cargo tracked
As an operator
I want to register a new handling event

Scenario: Operator registers a handling event of which type is load

Given a cargo has been routed
When I register a new handling event of which type is load 
Then the transport status of the cargo is IN_PORT
And the last known location of the cargo is updated as the location of the handling event
And the current voyage of the cargo is updated as the voyage of the handling event
And the next expected handling activity is being shipped by the leg's voyage and unloading
 