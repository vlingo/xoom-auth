package io.vlingo.xoom.auth.infrastructure.exchange;

import io.vlingo.xoom.turbo.actors.Settings;
import io.vlingo.xoom.lattice.exchange.Exchange;
import io.vlingo.xoom.turbo.exchange.ExchangeSettings;
import io.vlingo.xoom.turbo.exchange.ExchangeInitializer;
import io.vlingo.xoom.lattice.exchange.rabbitmq.ExchangeFactory;
import io.vlingo.xoom.lattice.exchange.ConnectionSettings;
import io.vlingo.xoom.lattice.exchange.rabbitmq.Message;
import io.vlingo.xoom.lattice.exchange.rabbitmq.MessageSender;
import io.vlingo.xoom.lattice.exchange.rabbitmq.InactiveBrokerExchangeException;
import io.vlingo.xoom.lattice.exchange.Covey;
import io.vlingo.xoom.lattice.grid.Grid;
import io.vlingo.xoom.symbio.store.dispatch.Dispatcher;

import io.vlingo.xoom.lattice.model.IdentifiedDomainEvent;

public class ExchangeBootstrap implements ExchangeInitializer {

  private Dispatcher<?> dispatcher;

  @Override
  public void init(final Grid stage) {
    ExchangeSettings.load(Settings.properties());

    final ConnectionSettings xoomAuthSettings =
                ExchangeSettings.of("xoom-auth").mapToConnection();

    final Exchange xoomAuth =
                ExchangeFactory.fanOutInstanceQuietly(xoomAuthSettings, "xoom-auth", true);

    try {
      xoomAuth.register(Covey.of(
          new MessageSender(xoomAuth.connection()),
          received -> {},
          new RoleProducerAdapter(),
          IdentifiedDomainEvent.class,
          IdentifiedDomainEvent.class,
          Message.class));

      xoomAuth.register(Covey.of(
          new MessageSender(xoomAuth.connection()),
          received -> {},
          new TenantProducerAdapter(),
          IdentifiedDomainEvent.class,
          IdentifiedDomainEvent.class,
          Message.class));

      xoomAuth.register(Covey.of(
          new MessageSender(xoomAuth.connection()),
          received -> {},
          new PermissionProducerAdapter(),
          IdentifiedDomainEvent.class,
          IdentifiedDomainEvent.class,
          Message.class));

      xoomAuth.register(Covey.of(
          new MessageSender(xoomAuth.connection()),
          received -> {},
          new UserProducerAdapter(),
          IdentifiedDomainEvent.class,
          IdentifiedDomainEvent.class,
          Message.class));

      xoomAuth.register(Covey.of(
          new MessageSender(xoomAuth.connection()),
          received -> {},
          new GroupProducerAdapter(),
          IdentifiedDomainEvent.class,
          IdentifiedDomainEvent.class,
          Message.class));

    } catch (final InactiveBrokerExchangeException exception) {
      stage.world().defaultLogger().error("Unable to register covey(s) for exchange xoom-auth");
      stage.world().defaultLogger().warn(exception.getMessage());
    }

    this.dispatcher = new ExchangeDispatcher(xoomAuth);

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        xoomAuth.close();

        System.out.println("\n");
        System.out.println("==================");
        System.out.println("Stopping exchange.");
        System.out.println("==================");
    }));
  }

  @Override
  public Dispatcher<?> dispatcher() {
    return dispatcher;
  }

}