/**
 * Created by Piotrek on 13.11.2016.
 */
public class BankAccount {
    private double balance;
    int id;

    BankAccount(int id, double balance) {
        this.id = id;
        this.balance = balance;
    }

    synchronized double getBalance() {
        try {
            Thread.sleep(100l);
        } catch (InterruptedException e) {}
        return balance;
    }

    synchronized void withdraw(double amount) {

        balance -= amount;
    }

    synchronized void deposit(double amount) {

        balance += amount;
    }

    synchronized void transfer(BankAccount to, double amount) {
        withdraw(amount);
        to.deposit(amount);
    }

    static void transfer(BankAccount from, BankAccount to, double amount) {
        synchronized(from) {
            from.withdraw(amount);
            synchronized(to) {
                to.deposit(amount);
            }
        }
    }
}

class BalanceMonitor implements Runnable {
    private BankAccount account;
    BalanceMonitor(BankAccount account) { this.account = account;}
    boolean alreadyNotified = false;

    @Override
    public void run() {
        System.out.format("%s started execution%n", Thread.currentThread().getName());
        while (true) {
            if(account.getBalance() <= 0) {
                break;
            }
        }
        System.out.format("%s : account has gone too low, email sent, exiting.", Thread.currentThread().getName());
    }

}
class Transaction implements Runnable {
    private BankAccount sourceAccount, destinationAccount;
    private double amount;

    Transaction(BankAccount sourceAccount, BankAccount destinationAccount, double amount) {
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
        this.amount = amount;
    }

    public void run() {
        System.out.format("%s started execution%n", Thread.currentThread().getName());
        sourceAccount.transfer(destinationAccount, amount);
        System.out.printf("%s completed execution%n", Thread.currentThread().getName());
    }

}
