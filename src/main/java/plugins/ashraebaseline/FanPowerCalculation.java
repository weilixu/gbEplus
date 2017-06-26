package main.java.plugins.ashraebaseline;

/**
 * Calculation method to estimate the baseline fan power
 * 
 * @author weilixu
 *
 */
public final class FanPowerCalculation {
	private static final double CFMConversion = 2118.88;

	/**
	 * Acquire the fan power for system type 1 and 2, the air flow must be m3/s.
	 * The return result's unit is watts
	 * 
	 * @param airFlow
	 * @return
	 */
	public static double getFanPowerForSystem1and2(Double airFlow) {
		double cfms = airFlow * CFMConversion;
		return cfms * 0.3;
	}

	/**
	 * calculate the fan power for system type 3 and system type 4. The fan
	 * power limitation pressure drop credit includes: 1. Fully ducted return
	 * and/or exhaust air system 2. MERV 13 through 15
	 * 
	 * @param airFlow
	 * @return
	 */
	public static double getFanPowerForSystem3To4(Double airFlow) {
		double bhp = getBhPforConstantVolumeSystem(airFlow);
		double motoreff = selectMotorEff(bhp);
		return bhp * 746 / motoreff;
	}

	/**
	 * calculate the fan motor efficiency for system type 3 to system type 4.
	 * The fan power limitation pressure drop credit includes: 1. Fully ducted
	 * return and/or exhaust air system 2. MERV 13 through 15
	 * 
	 * @param airFlow
	 * @return
	 */
	public static double getFanMotorEfficiencyForSystem3To4(Double airFlow) {
		double bhp = getBhPforConstantVolumeSystem(airFlow);
		double motoreff = selectMotorEff(bhp);
		return motoreff;
	}

	/**
	 * calculate the fan power for system type 5 to system type 8. The fan power
	 * limitation pressure drop credit includes: 1. Fully ducted return and/or
	 * exhaust air system 2. MERV 13 through 15
	 * 
	 * @param airFlow
	 * @return
	 */
	public static double getFanPowerForSystem5To8(Double airFlow) {
		double bhp = getBhPforVariableVolumeSystem(airFlow);
		double motoreff = selectMotorEff(bhp);
		return bhp * 746 / motoreff;
	}

	/**
	 * calculate the fan motor efficiency for system type 5 to system type 8.
	 * The fan power limitation pressure drop credit includes: 1. Fully ducted
	 * return and/or exhaust air system 2. MERV 13 through 15
	 * 
	 * @param airFlow
	 * @return
	 */
	public static double getFanMotorEffciencyForSystem5To8(Double airFlow) {
		double bhp = getBhPforVariableVolumeSystem(airFlow);
		double motoreff = selectMotorEff(bhp);
		return motoreff;
	}

	/**
	 * Calculate the fan power for system type 9 and 10
	 * 
	 * @param airFlow
	 * @return
	 */
	public static double getFanPowerForSystem9To10(Double airFlow) {
		double cfms = airFlow * CFMConversion;
		return cfms * 0.054;
	}

	private static double getBhPforVariableVolumeSystem(Double airFlow) {
		double cfm = airFlow * CFMConversion;
		double A = (0.5 + 0.9) * cfm / 4131;
		return cfm * 0.0013 + A;
	}

	private static double getBhPforConstantVolumeSystem(Double airFlow) {
		double cfm = airFlow * CFMConversion;
		// assume credit, fully ducted return and/or exhaust air system
		// with particulate filtration credit:MERV 13 through 15
		double A = (0.5 + 0.9) * cfm / 4131;
		return cfm * 0.00094 + A;
	}

	/**
	 * Selet the motor efficiency for the fan, As stated in the G3.1.2.10 Fan
	 * Motor Efficiency is the efficiency from Table 10.8 for the next motor
	 * size greater than the bhp using a totally enclosed fancooled motor at
	 * 1800 rpm.
	 * 
	 * @param bhp
	 * @return
	 */
	private static double selectMotorEff(double bhp) {
		if (bhp <= 1) {
			return 0.825;
		} else if (bhp <= 2) {
			return 0.84;
		} else if (bhp <= 5) {
			return 0.875;
		} else if (bhp <= 10) {
			return 0.895;
		} else if (bhp <= 20) {
			return 0.910;
		} else if (bhp <= 30) {
			return 0.924;
		} else if (bhp <= 50) {
			return 0.930;
		} else if (bhp <= 60) {
			return 0.936;
		} else if (bhp <= 75) {
			return 0.941;
		} else if (bhp <= 125) {
			return 0.945;
		} else {
			return 0.950;
		}
	}
}
