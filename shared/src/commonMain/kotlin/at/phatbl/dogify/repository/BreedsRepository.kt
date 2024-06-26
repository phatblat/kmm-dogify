package at.phatbl.dogify.repository

import at.phatbl.dogify.Breed
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope

class BreedsRepository internal constructor(
    private val remoteSource: BreedsRemoteSource,
    private val localSource: BreedsLocalSource,
) {
    val breeds = localSource.breeds

    internal suspend fun get() = with(localSource.selectAll()) {
        if (isNullOrEmpty()) {
            return@with fetch()
        } else {
            this
        }
    }

    private suspend fun fetch() = supervisorScope {
        remoteSource.getBreeds().map {
            async {
                Breed(
                    name = it,
                    imageUrl = remoteSource.getBreedImage(it)
                )
            }
        }.awaitAll().also {
            localSource.clear()
            it.map { async { localSource.insert(it) } }.awaitAll()
        }
    }

    internal suspend fun update(breed: Breed) = localSource.update(breed)
}
