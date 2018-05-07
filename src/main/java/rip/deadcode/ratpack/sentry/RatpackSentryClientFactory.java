package rip.deadcode.ratpack.sentry;

import io.sentry.DefaultSentryClientFactory;
import io.sentry.context.ContextManager;
import io.sentry.dsn.Dsn;

/**
 * {@link io.sentry.SentryClientFactory} for Ratpack applications.
 */
public final class RatpackSentryClientFactory extends DefaultSentryClientFactory {

    /**
     * Explicit public no-arg constructor.
     */
    public RatpackSentryClientFactory() {}

    /**
     * @see <a href="https://docs.sentry.io/clients/java/context/">Context & Breadcrumbs</a>
     *
     * @param dsn Sentry DSN
     * @return A new {@link RatpackSentryContextManager} instance.
     */
    @Override
    protected ContextManager getContextManager( Dsn dsn ) {
        return new RatpackSentryContextManager();
    }
}
