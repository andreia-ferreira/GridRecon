# GridRecon

This project simulates one or more drones navigating a grid of cells, each with a score and regeneration behavior. 
The drones goal is to move across the cells with the highest score and, when the algorithm chooses the best path, the drone collects that score and the grid gets updated (sets that cell value to 0).

### General Flow
The program runs in the command line and aims to collect the initial simulation data (which grid to use, how many time steps, how long should it run and where to place one or more drones).
At each main step the current grid is printed along with the drone(s) position(s) and the past moves, unless the grid is too big, in that case only the final state will be printed.
The items printed on the console have different colors depending on their purpose:
- Red: Drone
- Yellow: Path
- Green: positions that are being evaluated as the next step
- Black background: the positions that were already explored
<img src="https://github.com/user-attachments/assets/fc01c11e-ca72-41b2-851c-ef0dc27384f7" width="600"/>

#### Drone movement algorithm
This program implements an adaptation of a Beam Search algorithm in order to find the best path across the grid.
The neighboring cells are considered for the next step and sorted based on their immediate value, the cummulative score and an estimation of how much they'll be worth in the next turns, taking the regeneration rate into consideration.
Only the top candidates are picked as potential moves, based on the beam width, which differs per grid size and time steps. From these, a top candidate is then picked for the next move.
Note: The regeneration rate is hardcoded at 0.25 at the moment. Meaning that each cell regenerates 0.25 each turn until it reaches the full initial value.

### Project Structure
This is a multi module app that follows Clean Architecture principles.
<img src="https://github.com/user-attachments/assets/30d4d639-ec49-4022-aebe-ffd7e8f34432" width="600"/>

### Testing
In the app modules, the `SimulationIntegrationTest` should test the overall functioning of the process given a mocked grid.
In the other modules (data and domain) you can find unit tests
