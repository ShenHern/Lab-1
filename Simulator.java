public class Simulator {
    private final int numOfServers;
    private final int qmax;
    private final ImList<Pair<Double, Double>> inpTimes;
    

    Simulator(int numOfServers, int qmax, ImList<Pair<Double, Double>> inpTimes) {
        this.numOfServers = numOfServers;
        this.qmax = qmax;
        this.inpTimes = inpTimes;
    } 

    /**
     * Method to simulate the whole sequence of Events.
     * @return a String which represents the whole sequence of Events
     */

    public String simulate() {
        String logString = "";

        //add Arrive Events to PQ
        PQ<Event> pq = new PQ<Event>(new TimestampComp());
        pq = addArrives(pq);

        //create list of Servers
        ImList<Server> serverList = ServerList.createServerList(this.numOfServers, this.qmax);

        //instantiating StatCalc
        StatCalc statCalc = new StatCalc();

        //execute events from PQ
        while (!pq.isEmpty()) {
            Pair<Event, PQ<Event>> pr = pq.poll();
            Event e = pr.first();
            pq = pr.second();
            //add e.toString to log
            logString += e.toString() + "\n";

            //process current event
            Pair<PQ<Event>, ImList<Server>> pr2 = EventProcessor.processEvent(e, serverList, pq);
            pq = pr2.first();
            serverList = pr2.second();

            //calculating statistics
            statCalc = statCalc.updateStats(e);
        }

        //add statistics to log
        logString += String.format("[%.3f %d %d]", 
                statCalc.avgWaitTime(), 
                statCalc.customersServed(), 
                statCalc.customersLeft());

        return logString;
    }

    private PQ<Event> addArrives(PQ<Event> pq) {
        PQ<Event> newpq = pq;
        for (int i = 0; i < this.inpTimes.size(); i++) {
            Customer customer = new Customer(i + 1, this.inpTimes.get(i).first(), 
                                            this.inpTimes.get(i).second());

            Arrive a = new Arrive(customer, this.inpTimes.get(i).first());
            newpq = newpq.add(a);
        }
        return newpq;
    }
}
