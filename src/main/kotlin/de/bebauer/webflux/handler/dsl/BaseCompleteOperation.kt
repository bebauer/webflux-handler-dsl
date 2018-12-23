package de.bebauer.webflux.handler.dsl

import arrow.core.None
import arrow.core.Option

abstract class BaseCompleteOperation : CompleteOperation {
    internal var parent: Option<ChainableCompleteOperation> = None
}