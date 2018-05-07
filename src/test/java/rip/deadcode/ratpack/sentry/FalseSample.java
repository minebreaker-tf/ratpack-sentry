package rip.deadcode.ratpack.sentry;

import ratpack.server.RatpackServer;

public final class FalseSample {

    public static void main( String[] args ) throws Exception {

        RatpackServer.start( spec -> spec
                .serverConfig( config -> config.development( true ) )
                .registry( registry -> registry )
                .handlers( chain -> chain.all( Sample.handler ) )
        );
    }
}
