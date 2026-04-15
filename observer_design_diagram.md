# Design-Level Diagram - Observer Pattern (Assign work to animator)

Questo diagramma rappresenta l'implementazione del **GoF Observer Pattern** calato rigorosamente nello Use Case specificato, eliminando gli strati non necessari.

```mermaid
classDiagram

    %% --- GANG OF FOUR ABSTRACT ROLES ---
    
    namespace Abstract_Subject {
        class Subject {
            <<abstract>>
            - List~Observer~ observers
            + attach(observer: Observer) void
            + detach(observer: Observer) void
            + notifyObservers(msg: String) void
        }
    }

    namespace Abstract_Observer {
        class Observer {
            <<interface>>
            + update(message: String) void
        }
    }

    %% Relazione Core del Pattern
    Subject "1" --> "*" Observer : notifica

    %% --- CONCRETE IMPLEMENTATIONS ---

    namespace Concrete_Subject {
        class PartyController {
            + findEligibleAnimators(party: PartyBean) List~UserBean~
            + assignAnimator(party: PartyBean, animator: UserBean) void
            + getAssignmentStatuses(partyId: int) Map~String, AssignmentStatus~
            + getProposalCount(partyId: int) int
        }
    }

    namespace Concrete_Observer {
        class JavaFXPartyListController {
            - TableView partyTable
            - TableColumn assignColumn
            - TableColumn statusColumn
            - PartyController partyController
            + initialize() void
            + addButtonToTable() void
            + handleCancelParty(party: PartyBean) void
            + openAssignmentDialog(party: PartyBean) void
            + loadParties() void
            + update(message: String) void
        }
    }

    %% Implementazioni Pattern
    Subject <|-- PartyController 
    Observer <|.. JavaFXPartyListController 

    %% Interazione Runtime
    JavaFXPartyListController --> PartyController : usa/assegna
```
