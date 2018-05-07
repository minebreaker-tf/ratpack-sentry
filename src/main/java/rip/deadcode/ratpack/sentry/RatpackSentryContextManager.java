package rip.deadcode.ratpack.sentry;

import io.sentry.context.Context;
import io.sentry.context.ContextManager;
import ratpack.exec.Execution;

import java.util.Optional;

/**
 * A {@link ContextManager} implementation for Ratpack applications.
 * If called in the Ratpack {@link Execution},
 */
public final class RatpackSentryContextManager implements ContextManager {

    private final Context singletonContext = new Context();

    @Override
    public Context getContext() {
        Optional<Execution> currentExecOpt = Execution.currentOpt();

        if ( currentExecOpt.isPresent() ) {
            Execution currentExec = currentExecOpt.get();
            Optional<Context> context = currentExec.maybeGet( Context.class );

            if ( context.isPresent() ) {
                return context.get();
            } else {
                Context newContext = new Context();
                currentExec.add( newContext );
                return newContext;
            }
        } else {
            return singletonContext;
        }
    }

    @Override
    public void clear() {
        Optional<Execution> currentExecOpt = Execution.currentOpt();

        if ( currentExecOpt.isPresent() ) {
            currentExecOpt.get()
                          .maybeGet( Context.class )
                          .ifPresent( Context::clear );
        } else {
            singletonContext.clear();
        }
    }
}
