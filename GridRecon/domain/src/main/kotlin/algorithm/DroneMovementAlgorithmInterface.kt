package algorithm

import entity.Drone
import entity.Grid
import entity.InputParams

interface DroneMovementAlgorithmInterface {
    fun getCandidates(latestMove: Drone.Move, grid: Grid, inputParams: InputParams): List<CandidateNextMove>
    fun getNextBestMove(latestMove: Drone.Move, candidates: List<CandidateNextMove>): Drone.Move?
}