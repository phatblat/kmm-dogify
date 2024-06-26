package at.phatbl.dogify.di

import at.phatbl.dogify.FetchBreedsUseCase
import at.phatbl.dogify.GetBreedsUseCase
import at.phatbl.dogify.ToggleFavouriteStateUseCase
import at.phatbl.dogify.api.BreedsApi
import at.phatbl.dogify.database.createDriver
import at.phatbl.dogify.db.DogifyDatabase
import at.phatbl.dogify.repository.BreedsLocalSource
import at.phatbl.dogify.repository.BreedsRemoteSource
import at.phatbl.dogify.repository.BreedsRepository
import at.phatbl.dogify.util.getDispatcherProvider
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

private val apiModule = module {
    factory { BreedsApi() }
}

private val repositoryModule = module {
    single { BreedsRepository(get(), get()) }

    factory<BreedsRemoteSource> { BreedsRemoteSource(get(), get()) }
    factory<BreedsLocalSource> { BreedsLocalSource(get(), get()) }
}

private val usecaseModule = module {
    factory { GetBreedsUseCase() }
    factory { FetchBreedsUseCase() }
    factory { ToggleFavouriteStateUseCase() }
}

private val utilityModule = module {
    factory { getDispatcherProvider() }
    single { DogifyDatabase(createDriver("dogify.db")) }
}

private val sharedModules = listOf(
    apiModule, repositoryModule, usecaseModule, utilityModule
)

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(sharedModules)
}
