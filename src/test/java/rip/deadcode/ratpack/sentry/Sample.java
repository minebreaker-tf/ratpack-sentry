package rip.deadcode.ratpack.sentry;

import io.sentry.Sentry;
import io.sentry.event.User;
import io.sentry.event.UserBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.exec.Blocking;
import ratpack.handling.Handler;
import ratpack.server.RatpackServer;

public final class Sample {

    private static Logger logger = LoggerFactory.getLogger( Sample.class );

    static Handler handler = ctx -> {
        Sentry.getContext().setUser( new UserBuilder().setId( "[test]" ).build() );
        checkContext( "ratpack computing thread (1)" );
        Blocking.op( () -> {
            checkContext( "ratpack blocking thread (2)" );
        } ).then( () -> {
            checkContext( "ratpack computing thread (3)" );
            ctx.render( "OK" );
        } );
    };

    public static void main( String[] args ) throws Exception {

        Sentry.init( new RatpackSentryClientFactory() );

        RatpackServer.start( spec -> spec
                .serverConfig( config -> config.development( true ) )
                .registry( registry -> registry )
                .handlers( chain -> chain.all( handler ) )
        );
    }

    private static void checkContext( String name ) {
        User user = Sentry.getContext().getUser();
        logger.info( name );
        if ( user != null ) {
            logger.info( "Current context: {}", user.getId() );
        } else {
            logger.info( "Current context is null" );
        }
    }
}
