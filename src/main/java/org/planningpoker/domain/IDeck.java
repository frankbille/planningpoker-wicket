package org.planningpoker.domain;

import java.io.Serializable;
import java.util.List;

public interface IDeck extends Serializable {

	List<ICard> createDeck();
	
}
