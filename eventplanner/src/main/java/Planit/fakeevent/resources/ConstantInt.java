package Planit.fakeevent.resources;

import com.google.common.base.Optional;

import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Expenditure;
import edu.toronto.cs.se.ci.utils.BasicSource;

/**
 * This source always returns the same answer.
 * It is intended for use where you want to generate
 * training data, so this source will be the classifying attribute.
 * @author wginsberg
 *
 */
public class ConstantInt extends BasicSource<Object, Integer, Void> {

	private int value;
	
	public ConstantInt(int value){
		this.value = value;
	}
	
	@Override
	public Integer getResponse(Object input) throws UnknownException {
		return value;
	}

	@Override
	public Expenditure[] getCost(Object args) throws Exception {
		
		return null;
	}

	@Override
	public Void getTrust(Object args, Optional<Integer> value) {
		
		return null;
	}

}
