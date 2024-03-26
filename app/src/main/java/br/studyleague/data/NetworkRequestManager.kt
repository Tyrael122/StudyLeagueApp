package br.studyleague.data

import br.studyleague.util.debug
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.isActive
import java.util.concurrent.CancellationException

class NetworkRequestManager(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    var coroutineScope: CoroutineScope = CoroutineScope(ioDispatcher)
) {
    private val logTag = "NetworkRequestManager"
    private var currentJob: Job? = null

    suspend fun <V> doNetworkRequestWithCancellation(request: suspend (Long) -> V?): V? {
        cancelLastRequest()

        val randomId = (0..10000L).random()
        val deferredResponse = coroutineScope.async {
            request(randomId)
        }

        deferredResponse.invokeOnCompletion { throwable ->
            if (throwable is CancellationException) {
                debug(
                    logTag,
                    "Network request with ID $randomId was cancelled.",
                )
            }
        }

        currentJob = deferredResponse

        return deferredResponse.await()
    }

    private fun cancelLastRequest() {
        currentJob?.cancel(CancellationException("Cancelling previous network request"))

        if (!coroutineScope.isActive) {
            coroutineScope = CoroutineScope(ioDispatcher)
        }
    }
}