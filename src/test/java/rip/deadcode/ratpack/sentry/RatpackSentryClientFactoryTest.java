package rip.deadcode.ratpack.sentry;

import io.sentry.Sentry;
import io.sentry.SentryClient;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static com.google.common.truth.Truth.assertThat;

class RatpackSentryClientFactoryTest {

    @Test
    void test() throws Exception {
        Sentry.init( "https://dummy@localhost/1", new RatpackSentryClientFactory() );
        Field f = SentryClient.class.getDeclaredField( "contextManager" );
        f.setAccessible( true );
        Object manager = f.get( Sentry.getStoredClient() );

        assertThat( manager ).isInstanceOf( RatpackSentryContextManager.class );
    }
}
