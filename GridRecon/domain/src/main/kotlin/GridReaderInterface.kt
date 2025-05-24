package net.penguin.domain

import kotlinx.coroutines.Deferred
import net.penguin.domain.entity.Grid
import net.penguin.domain.entity.GridType

interface GridReaderInterface {
    fun get(gridType: GridType, regenerationRate : Double): Deferred<Grid?>
}