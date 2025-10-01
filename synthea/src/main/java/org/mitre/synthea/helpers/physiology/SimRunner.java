package org.mitre.synthea.helpers.physiology;

import org.mitre.synthea.world.agents.Person;
import org.mitre.synthea.world.concepts.VitalSign;

/**
 * Class for handling execution of a PhysiologySimulator.
 * DISABLED: Physiology feature removed for Australian configuration
 */
public class SimRunner {
  // Physiology feature disabled for Australian configuration
  // This class is kept as a stub to maintain compatibility
  
  public SimRunner(PhysiologyGeneratorConfig config, Person person) {
    // Stub constructor - physiology feature disabled
  }
  
  public boolean setInputs(long time) {
    // Stub method - physiology feature disabled
    return false;
  }
  
  public void execute(long time) {
    // Stub method - physiology feature disabled
  }
  
  public boolean hasExecuted() {
    // Stub method - physiology feature disabled
    return false;
  }
  
  public double getVitalSignValue(VitalSign vitalSign) {
    // Stub method - physiology feature disabled
    return 0.0;
  }
}