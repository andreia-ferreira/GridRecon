package algorithm

import entity.*

interface DroneMovementAlgorithmInterface {
    fun getCandidates(
        latestMove: Drone.Move,
        grid: Grid,
        simulationParameters: SimulationParameters,
        forbidPositions: List<Position> = emptyList()
    ): List<CandidateNextMove>
    fun getNextBestMove(
        latestMove: Drone.Move,
        candidates: List<CandidateNextMove>,
        simulationParameters: SimulationParameters
    ): Drone.Move?
}