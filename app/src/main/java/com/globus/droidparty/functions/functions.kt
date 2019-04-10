package com.globus.droidparty.functions

@Suppress("NOTHING_TO_INLINE")
inline fun <T1, T2> first(): (T1, T2) -> T1 = { t1, _ -> t1 }

@Suppress("NOTHING_TO_INLINE")
inline fun <X, F : (X) -> R, R> apply(): (X, F) -> R = { x, f -> f(x) }