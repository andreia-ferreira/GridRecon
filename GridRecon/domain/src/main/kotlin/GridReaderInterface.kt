package net.penguin.domain

import kotlinx.coroutines.Deferred

interface GridReaderInterface {
    fun get(initialParameters: InitialParameters): Deferred<Grid?>
}