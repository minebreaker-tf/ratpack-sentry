package rip.deadcode.ratpack.sentry;

import io.sentry.context.Context;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.exec.Operation;
import ratpack.exec.Promise;
import ratpack.test.exec.ExecHarness;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

class RatpackSentryContextManagerTest {

    private static final Logger logger = LoggerFactory.getLogger( RatpackSentryContextManagerTest.class );

    private RatpackSentryContextManager contextManager;

    @BeforeEach
    void setUp() {
        contextManager = new RatpackSentryContextManager();
    }

    @Test
    void testGetContext1() throws ExecutionException, InterruptedException {
        Context c1 = contextManager.getContext();
        Context c2 = Executors.newSingleThreadExecutor().submit( () -> contextManager.getContext() ).get();

        assertWithMessage( "Should return singleton Context outside the Execution." )
                .that( c1 ).isSameAs( c2 );
    }

    @Test
    void testGetContext2() throws Exception {

        Map<String, String> result = ExecHarness.yieldSingle( e -> {
            return Operation.of( () -> {
                logger.info( "compute" );
                contextManager.getContext().addTag( "foo", "1" );
            } ).blockingNext( () -> {
                logger.info( "blocking" );
                contextManager.getContext().addTag( "bar", "2" );
            } ).promise().map( unit -> {
                logger.info( "compute" );
                return contextManager.getContext().getTags();
            } );
        } ).getValue();

        assertThat( result ).containsExactly( "foo", "1", "bar", "2" );
        assertWithMessage( "Singleton context should not be affected." )
                .that( contextManager.getContext().getTags() ).isEmpty();
    }

    @Test
    void testGetContext3() throws Exception {


        Context result1 = ExecHarness.yieldSingle( e -> {
            logger.info( "" );
            contextManager.getContext().addTag( "foo", "bar" );
            return Promise.value( contextManager.getContext() );
        } ).getValue();
        Context result2 = ExecHarness.yieldSingle( e -> {
            logger.info( "" );
            contextManager.getContext().addTag( "foo", "buz" );
            return Promise.value( contextManager.getContext() );
        } ).getValue();

        assertWithMessage( "Different Executions should use different contexts." )
                .that( result1 ).isNotSameAs( result2 );
        assertThat( result1.getTags() ).containsExactly( "foo", "bar" );
        assertThat( result2.getTags() ).containsExactly( "foo", "buz" );
    }


    @Test
    void testClear1() throws ExecutionException, InterruptedException {
        contextManager.getContext().addTag( "foo", "bar" );
        Executors.newSingleThreadExecutor().submit( () -> contextManager.clear() ).get();
        Map<String, String> tags = contextManager.getContext().getTags();

        assertThat( tags ).isEmpty();
    }

    @Test
    void testClear2() throws Exception {

        contextManager.getContext().addTag( "foo", "bar" );

        Map<String, String> result = ExecHarness.yieldSingle( e -> {
            return Operation.of( () -> {
                logger.info( "compute" );
                contextManager.getContext().addTag( "foo", "buz" );
            } ).blockingNext( () -> {
                logger.info( "blocking" );
                contextManager.clear();
            } ).promise().map( unit -> {
                logger.info( "compute" );
                return contextManager.getContext().getTags();
            } );
        } ).getValue();

        assertWithMessage( "Context in the Execution is cleared." )
                .that( result ).isEmpty();
        assertWithMessage( "Singleton Context should remain the same." )
                .that( contextManager.getContext().getTags() ).containsExactly( "foo", "bar" );
    }
}
