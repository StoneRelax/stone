package stone.dal.seq.autoconfigure;

public class StSequenceConfig {

  private String storePath;

  StSequenceConfig(String storePath) {
    this.storePath = storePath;
  }

  public String getStorePath() {
    return storePath;
  }

}
