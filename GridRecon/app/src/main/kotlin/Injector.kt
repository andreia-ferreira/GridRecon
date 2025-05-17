package net.penguin.app

import net.penguin.data.GridFileReader
import net.penguin.domain.GridReaderInterface

object Injector {
    fun provideGridReader(): GridReaderInterface {
        return GridFileReader
    }
}