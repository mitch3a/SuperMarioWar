package smw;

public interface Updatable {
  public void update(float timeDif_ms);
  public abstract boolean shouldBeRemoved();
}
