package model;

public class Options {

    public boolean stop, afterStop;
    public boolean pause, afterPause;
    public boolean rewind, afterRewind;
    public boolean forward, afterForward;
    public boolean plus15;
    public boolean minus15;
    public boolean scroll;
    public double playSpeed;

    public Options() {
        this.stop = false;
        this.afterStop = false;
        this.pause = false;
        this.afterPause = false;
        this.rewind = false;
        this.afterRewind = false;
        this.forward = false;
        this.afterForward = false;
        this.plus15 = false;
        this.minus15 = false;
        this.scroll = false;
        this.playSpeed = 100;
    }

    public boolean isAfterForward() {
        return afterForward;
    }

    public void setAfterForward(boolean afterForward) {
        this.afterForward = afterForward;
    }

    public boolean isAfterRewind() {
        return afterRewind;
    }

    public void setRewind(boolean rewind) {
        this.rewind = rewind;
    }

    public boolean isAfterPause() {
        return afterPause;
    }

    public void setAfterPause(boolean afterPause) {
        this.afterPause = afterPause;
    }

    public void setAfterStop(boolean afterStop) {
        this.afterStop = afterStop;
    }

    public boolean isAfterStop() {
        return afterStop;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

}
