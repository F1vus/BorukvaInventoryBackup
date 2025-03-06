package net.fiv.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.japi.pf.DeciderBuilder;
import java.time.Duration;
import net.fiv.BorukvaInventoryBackup;

public class DatabaseManagerSupervisor extends AbstractActor {

    public static Props props() {
        return Props.create(DatabaseManagerSupervisor.class);
    }

    private final ActorRef databaseManagerActor;

    public DatabaseManagerSupervisor() {
        databaseManagerActor = getContext().actorOf(DatabaseManagerActor.props(), "databaseManagerActor");
    }
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .matchAny(message -> databaseManagerActor.forward(message, getContext()))
                .build();
    }

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return new OneForOneStrategy(
                5, // maxNrOfRetries
                Duration.ofMinutes(1), // withinTimeRange
                DeciderBuilder.match(SQLExceptionWrapper.class, e -> {
                            BorukvaInventoryBackup.LOGGER.error("SQLExceptionWrapper occurred, restarting actor: {}", e.getMessage());
                            return SupervisorStrategy.restart();
                        })
                        .matchAny(o -> SupervisorStrategy.escalate())
                        .build()
        );
    }
}
