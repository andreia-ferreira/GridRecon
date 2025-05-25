package algorithm

import entity.CandidateNextMove
import entity.Drone
import entity.Grid
import entity.SimulationParameters

interface DroneMovementAlgorithmInterface {
    fun getCandidates(latestMove: Drone.Move, grid: Grid, simulationParameters: SimulationParameters): List<CandidateNextMove>
    fun getNextBestMove(latestMove: Drone.Move, candidates: List<CandidateNextMove>, simulationParameters: SimulationParameters): Drone.Move?
}