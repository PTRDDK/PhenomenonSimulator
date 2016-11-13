import java.util.concurrent.TimeUnit;

/**
 * Created by Piotrek on 12.11.2016.
 */
public class Controller {
    public static void main(String[] args){
        try {
            String phenomenon = args[0];
            phenomenon = phenomenon.toUpperCase();
            System.out.println("TEST: " + phenomenon);
            if (phenomenon.equals("DEADLOCK")) {
                System.out.println("deadlock"); //Deadlock jest kiedy jeden proces czeka na drugi
                deadlockTest();
            } else if (phenomenon.equals("LIVELOCK")) {
                System.out.println("livelock"); //LiveLock jest kiedy dwa procesy blokuja sie wzajemnie
                liveLockTest();
            } else if (phenomenon.equals("STARVATION")) {
                System.out.println("starvation"); //Starvation jest kiedy watek o nizszym priorytecie czeka na ten o wyzszym, a ten sie nie konczy przez inne
                starvationTest();
            }
        }catch(ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
            System.out.println("NO ARGUMENTS!");
        }
    }

    public static void deadlockTest(){
        long startTime = System.currentTimeMillis();
        long endTime = startTime + 15*1000;
        do {
            final BankAccount fooAccount = new BankAccount(1, 100d);
            final BankAccount barAccount = new BankAccount(2, 100d);

            new Thread() {
                public void run() {
                    BankAccount.transfer(fooAccount, barAccount, 10d);
                }
            }.start();

            new Thread() {
                public void run() {
                    BankAccount.transfer(barAccount, fooAccount, 10d);
                }
            }.start();
        }while(System.currentTimeMillis() < endTime);
        System.exit(1);
    }

    public static void liveLockTest(){
        long startTime = System.currentTimeMillis();
        long endTime = startTime + 15*1000;
        do {
            final BankAccount fooAccount = new BankAccount(1, 500d);
            final BankAccount barAccount = new BankAccount(2, 500d);

            new Thread(new Transaction(fooAccount, barAccount, 10d), "transaction-1").start();
            new Thread(new Transaction(barAccount, fooAccount, 10d), "transaction-2").start();
        }while(System.currentTimeMillis() < endTime);
        System.exit(1);
    }

    public static void starvationTest(){
        long startTime = System.currentTimeMillis();
        long endTime = startTime + 15*1000;
        do {
            final BankAccount fooAccount = new BankAccount(1, 500d);
            final BankAccount barAccount = new BankAccount(2, 500d);

            Thread balanceMonitorThread1 = new Thread(new BalanceMonitor(fooAccount), "BalanceMonitor");
            Thread transactionThread1 = new Thread(new Transaction(fooAccount, barAccount, 250d), "Transaction-1");
            Thread transactionThread2 = new Thread(new Transaction(fooAccount, barAccount, 250d), "Transaction-2");

            balanceMonitorThread1.setPriority(Thread.MAX_PRIORITY);
            transactionThread1.setPriority(Thread.MIN_PRIORITY);
            transactionThread2.setPriority(Thread.MIN_PRIORITY);

            // Start the monitor
            balanceMonitorThread1.start();

            // And later, transaction threads tries to execute.
            try {Thread.sleep(100l);} catch (InterruptedException e) {}
            transactionThread1.start();
            transactionThread2.start();
        }while(System.currentTimeMillis() < endTime);
        System.exit(1);
    }
}
