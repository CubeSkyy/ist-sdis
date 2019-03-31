package com.forkexec.pts.domain;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Points
 * <p>
 * A points server.
 */
public class Points {

    /**
     * Constant representing the default initial balance for every new client
     */
    private static final int DEFAULT_INITIAL_BALANCE = 100;
    /**
     * Global with the current value for the initial balance of every new client
     */
    private final AtomicInteger initialBalance = new AtomicInteger(DEFAULT_INITIAL_BALANCE);

    private Map<String, Integer> users = new ConcurrentHashMap<String, Integer>();

    // Singleton -------------------------------------------------------------

    /**
     * Private constructor prevents instantiation from other classes.
     */
    private Points() { }

    /**
     * SingletonHolder is loaded on the first execution of Singleton.getInstance()
     * or the first access to SingletonHolder.INSTANCE, not before.
     */
    private static class SingletonHolder {
        private static final Points INSTANCE = new Points();
    }

    public static synchronized Points getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * Sets the initial value of points for created user accounts.
     * */
    public void setInitialBalance (final int points) {
        initialBalance.set(points);
    }

    /**
     *
     * @param userEmail email of authenticated user.
     * @return the amounts of points that user has.
     */
    public Integer getPoints(String userEmail){
        return users.get(userEmail);
    }

    /**
     *
     * @param userEmail email of authenticated user.
     * @param pointsToSpend amount of points to spend
     */
    public void subtractPoints(String userEmail, Integer pointsToSpend)
        throws InvalidEmailException, NotEnoughBalanceException {

        Integer points = getPoints(userEmail);
        if (points == null) throw new InvalidEmailException("User não presente!");

        synchronized (points) {
            if (pointsToSpend > points) throw new NotEnoughBalanceException("Não tem saldo suficiente!");
            points -= pointsToSpend;
            users.put(userEmail, points);
        }
    }

    public void addPoints(String userEmail, Integer pointsToSpend)
            throws InvalidEmailException {

        Integer points = getPoints(userEmail);
        if (points == null) throw new InvalidEmailException("User não presente!");

        synchronized (points) {
            users.put(userEmail, points);
        }
    }

    public void resetState(){
        users.clear();
        setInitialBalance(DEFAULT_INITIAL_BALANCE);
    }
}
