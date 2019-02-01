package stone.dal.tools;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.yaml.snakeyaml.Yaml;
import stone.dal.kernel.utils.StringUtils;
import stone.dal.tools.meta.EntityListener;
import stone.dal.tools.meta.RawFieldMeta;

public class ExtensionRuleReader {

  enum TurnOnSwitches {
    header,
    details,
    both,
  }

  public static RuleSet read(String path) throws FileNotFoundException {
    if (!StringUtils.isEmpty(path)) {
      Yaml yaml = new Yaml();
      return yaml.loadAs(new FileInputStream(path), RuleSet.class);
    }
    return new RuleSet();
  }

  public static class RuleSet {

    private List<Rule> rules = new ArrayList<>();

    public Map<String, Rule> getTurnOnMap() {
      return rules.stream().collect(Collectors.toMap(Rule::getTurnOn, rule -> rule));
    }

    public void setRules(List<Rule> rules) {
      this.rules = rules;
    }

    public static class Rule {
      private String turnOn;

      private List<RawFieldMeta> addOnFields = new ArrayList<>();

      private List<EntityListener> entityListeners;

      public String getTurnOn() {
        return turnOn;
      }

      public void setTurnOn(String turnOn) {
        this.turnOn = turnOn;
      }

      public List<RawFieldMeta> getAddOnFields() {
        return addOnFields;
      }

      public void setAddOnFields(List<RawFieldMeta> addOnFields) {
        this.addOnFields = addOnFields;
      }

      public List<EntityListener> getEntityListeners() {
        return entityListeners;
      }

      public void setEntityListeners(
          List<EntityListener> entityListeners) {
        this.entityListeners = entityListeners;
      }
    }
  }
}
