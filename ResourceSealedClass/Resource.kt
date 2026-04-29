sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val message: String, val errorCode: Int? = null, val throwable: Throwable? = null): Resource<Nothing>()
    object Loading : Resource<Nothing>()
}

inline fun <T> Resource<T>.onSuccess(action: (T) -> Unit): Resource<T>{
    if (this is Resource.Success) action(data)
    return this
}

inline fun <T> Resource<T>.onError(action: (String, Int?, Throwable?) -> Unit): Resource<T>{
    if (this is Resource.Error) action(message, errorCode, throwable)
    return this
}

inline fun <T> Resource<T>.onLoading(action: () -> Unit): Resource<T>{
    if (this is Resource.Loading) action()
    return this
}
