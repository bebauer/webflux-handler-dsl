package de.bebauer.webflux.handler.dsl

import arrow.core.None
import arrow.core.Option

/**
 * Base complete operation.
 */
abstract class BaseCompleteOperation : CompleteOperation {

    /**
     * Optional parent complete operation.
     */
    internal var parent: Option<ChainableCompleteOperation> = None
}