package net.penguin.domain.usecase

sealed interface UseCase {
    interface NoParamsUseCase<out R>: UseCase {
        suspend fun execute(): R
    }
    interface ParamsUseCase<in T, out R>: UseCase {
        suspend fun execute(requestParams: T): R
    }
}