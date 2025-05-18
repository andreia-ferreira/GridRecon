package net.penguin.domain

import kotlinx.coroutines.Deferred
import net.penguin.domain.entity.Grid

interface GridReaderInterface {
    fun get(): Deferred<Grid?>
}