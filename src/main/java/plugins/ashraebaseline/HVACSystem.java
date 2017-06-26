package main.java.plugins.ashraebaseline;

import java.util.ArrayList;
import java.util.HashMap;

import main.java.model.data.EplusObject;


/**
 * This interface represents an HVAC System in the EnergyPlus
 * @author Weili
 *
 */
public interface HVACSystem {
    public HashMap<String, ArrayList<EplusObject>> getSystemData();

}
