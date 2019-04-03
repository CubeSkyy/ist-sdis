package com.forkexec.pts.domain;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

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

    private static final String VALID_EMAIL_REGEX = "[a-zA-Z0-9]+(.[a-zA-Z0-9]+)+@[a-zA-Z0-9]+(.[a-zA-Z0-9]+)+";

    private Map<String, Integer> users = new ConcurrentHashMap<String, Integer>();

    // Singleton -------------------------------------------------------------

    /**
     * Private constructor prevents instantiation from other classes.
     */
    private Points() {
    }

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
    * Returns the map with all the users
    */

    public Map<String, Integer> getUsers(){
        return users;
    }

    /**
     * Sets the initial value of points for created user accounts.
     */
    public void setInitialBalance(final int points) {
        initialBalance.set(points);
    }

    /**
     * @param userEmail email of authenticated user.
     * @return the amounts of points that user has.
     */
    public Integer getPoints(String userEmail) throws InvalidEmailException {

        if (!checkRegexPattern(userEmail, VALID_EMAIL_REGEX))
            throw new InvalidEmailException("Email é definido por user@dominio!");

        Integer points = users.get(userEmail);
        if (points == null) throw new InvalidEmailException("User não presente!");

        return points;
    }

    /**
     * @param userEmail     email of authenticated user.
     * @param pointsToSpend amount of points to spend
     */
    public int subtractPoints(String userEmail, Integer pointsToSpend)
            throws InvalidEmailException, NotEnoughBalanceException {

        Integer points = getPoints(userEmail);

        if (pointsToSpend > points) throw new NotEnoughBalanceException("Não tem saldo suficiente!");
        points -= pointsToSpend;
        users.put(userEmail, points);

        return points;

    }

    /**
    @param userEmail     email of new user.
    */

    public void addUser(String userEmail) throws EmailAlreadyExistsException, InvalidEmailException {

        if (!checkRegexPattern(userEmail, VALID_EMAIL_REGEX)) throw new InvalidEmailException("Email invalido!");

        for(String key: getUsers().keySet()){
            if (userEmail.equals(key)) {
                throw new EmailAlreadyExistsException("O email ja existe!");
            }
        }

        users.put(userEmail, DEFAULT_INITIAL_BALANCE);
    }

    /**
     * @param userEmail     email of authenticated user.
     * @param pointsToAdd amount of points to add
     */
    public int addPoints(String userEmail, Integer pointsToAdd)
            throws InvalidEmailException {

        Integer points = getPoints(userEmail);

        points += pointsToAdd;
        users.put(userEmail, points);
        return points;

    }

    public void resetState() {
        users.clear();
        setInitialBalance(DEFAULT_INITIAL_BALANCE);
    }

    private boolean checkRegexPattern(String text, String regex) {
        return Pattern.compile(regex).matcher(text).matches();
    }


}
