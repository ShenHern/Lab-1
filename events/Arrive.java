package events;
import servers.Server;
import customers.Customer;
import interfaces.Event;
import misc.Pair;

public class Arrive implements Event {
    private final Customer customer;
    private final Server server;
    private final double timestamp;
    private static final int PRIO = -1;

    public Arrive(Customer customer, double timestamp) {
        this.customer = customer;
        this.timestamp = timestamp;
        this.server = new Server("1", 0);
    }

    Arrive(Customer customer, Server server, double timestamp) {
        this.customer = customer;
        this.timestamp = timestamp;
        this.server = server;
    }

    @Override
    public double getTimestamp() {
        return this.timestamp;
    }

    @Override
    public Customer getCustomer() {
        return this.customer;
    }

    @Override
    public int getPriority() {
        return Arrive.PRIO;
    }

    @Override
    public Pair<Event, Server> execute() {
        if (!this.server.checkCanServe(this.customer)) {
            if (this.server.checkCanWait()) {
                return new Pair<Event, Server>(new Wait(this.customer, this.server, this.timestamp), this.server);
            }
            return new Pair<Event, Server>(new Leave(this.customer, this.server, this.timestamp), this.server);
        }
        return new Pair<Event, Server>(new Serve(this.customer, this.server, this.timestamp), this.server);
    }

    @Override
    public boolean hasNextEvent() {
        return true;
    }

    @Override
    public boolean isArrive() {
        return true;
    }

    @Override
    public boolean isWait() {
        return false;
    }

    @Override
    public boolean isServe() {
        return false;
    }

    @Override
    public boolean isLeave() {
        return false;
    }

    @Override
    public Event updateServer(Server server) {
        return new Arrive(this.customer, server, this.timestamp);
    }

    @Override
    public Server getServer () {
        return this.server;
    }

    @Override
    public String toString() {
        return String.format("%.3f", this.timestamp) + " " + customer.toString() + " arrives";
    }
}
