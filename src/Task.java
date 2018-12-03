import java.util.TimerTask;

public class Task extends TimerTask {
    private Expand expand =new Expand();



    @Override
    public void run() {
        expand.monitorFtp();
    }

    public Expand getTask() {
        return expand;
    }

    public void setTask(Expand task) {
        this.expand = expand;
    }
}
