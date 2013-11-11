Feature: Handling Event Registration

In order to get the cargo tracked  
As an operator  
I want to register a new handling event  

Scenario: Operator registers a RECEIVE handling event 

Given a cargo arrives at the first leg's origin
When I register an 'RECEIVE' handling event 
Then the transport status of the cargo is marked as IN_PORT
And the last known location of the cargo is updated
And the next expected handling activity is calculated as loaded onto the leg's voyage

 
Scenario: Operator registers a LOAD handling event

Given a cargo is received
When I register an 'LOAD' handling event
Then the transport status of the cargo is marked as ONBOARD_CARRIER
And the last known location of the cargo is updated
And the current voyage of the cargo is updated 
And the next expected handling activity is calculated as unloaded at the leg's destination

Scenario: Operator registers the first expected 'UNLOAD' handling event

Given a cargo is assigned to an itinerary with multiple legs
And the cargo arrives at the destination of the first leg 
When I register an 'UNLOAD' handling event for the first leg
Then the transport status of the cargo is marked as IN_PORT
And the last known location of the cargo is updated
And the cargo is marked as not currently on any voyage
And the next expected handling activity is calculated as loaded onto the next leg's voyage
 
Scenario: Operator registers the last expected 'UNLOAD' handling event

Given a cargo arrives at the final destination
When I register an 'UNLOAD' handling event for the last leg
Then the transport status of the cargo is marked as IN_PORT
And the last known location of the cargo is updated
And the cargo is marked as not currently on any voyage
And the cargo is marked as unloaded at the destination
And the next expected handling activity is calculated as claimed at the destination

Scenario: Operator registers a 'CLAIM' handling event

Given a cargo is unloaded at the destination
When I register a CLAIM handling event
Then the transport status of the cargo is marked as CLAIMED
And the next expected handling activity is calculated as none